package com.dictao.dtp.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.modules.junit4.PowerMockRunner;

import com.dictao.dtp.core.ContentType;
import com.dictao.dtp.core.api.pdf.PdfExplorer;
import com.dictao.dtp.core.data.SignatureField;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ecm.ECMDbService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.services.rendering.DocRenderingFileSystemRepository;
import com.dictao.dtp.core.services.rendering.DocRenderingService;
import com.dictao.dtp.core.services.rendering.IDocRenderingRepository;
import com.dictao.dtp.core.services.rendering.RenderingData;
import com.dictao.dtp.core.transactions.ApplicationConf;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.core.transactions.IDocumentProofResolver;
import com.dictao.dtp.core.transactions.TenantConf;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.data.EntitySignature;
import com.dictao.dtp.persistence.data.PersonalInfo;
import com.dictao.dtp.persistence.data.PersonalSignature;
import com.dictao.dtp.persistence.data.Signatures;
import com.dictao.dtp.persistence.data.VisibleSignatureField;
import com.dictao.dtp.persistence.entity.Transaction;
import com.dictao.dtp.persistence.entity.UserAccess;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author cwattebled
 *         Date: 06/02/12
 */
@RunWith(PowerMockRunner.class)
public class DocumentServletBaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentServletBaseTest.class);

    @Rule
    private static TemporaryFolder folder = new TemporaryFolder();

    private static String basepath;
    private static String ecmName = "StubedECM";
    private static String fileName = "MockedFileName";
    private static String proofName = "proofMockedFileName";
    private static String txId = "txid";
    private static String tenant = "tenant";
    
    @Mock
    private ECMDbService ecm;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ECMDocument document;
    @Mock
    ApplicationListConf conf;
    @Mock
    ApplicationConf appconf;
    @Mock
    TenantConf tenantConf;
    @Mock
    private Transaction transaction;
    @Mock
    private UserAccess userAccess;
    @Mock
    private PersonalInfo personalInfo;
    @Mock
    private TransactionHandler txHandler;   

    
    private static DocumentServlet servlet = new DocumentServlet();
    
    
    private DocRenderingService docRenderingService;

    @BeforeClass
    public static void beforeClass() throws IOException {
        folder.create();
        basepath = folder.newFolder("cache").getAbsolutePath();
    }

    @Before
    public void setUp() throws CertificateException {
        MockitoAnnotations.initMocks(this);

        Mockito.when(transaction.getTenant()).thenReturn(tenant);
        Mockito.when(transaction.getTransactionID()).thenReturn(txId);
        Mockito.when(conf.getTenant(tenant)).thenReturn(tenantConf);
        Mockito.when(tenantConf.getCacheLocation()).thenReturn(basepath);
        Mockito.when(txHandler.getDatabaseTransaction()).thenReturn(transaction);
        Mockito.when(txHandler.getDatabaseUserAccess()).thenReturn(userAccess);
        Mockito.when(userAccess.getPersonalInfo()).thenReturn(personalInfo);
        Mockito.when(txHandler.getDatabaseUserAccess().getPersonalInfo().getUser()).thenReturn("USER2");
        Mockito.when(conf.getConfiguration(Mockito.anyString(), Mockito.anyString(), Mockito.any(X509Certificate.class))).thenReturn(appconf);
        IDocRenderingRepository repo = new DocRenderingFileSystemRepository(basepath);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0,
                10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10));

        docRenderingService = new DocRenderingService(repo, null, null, threadPoolExecutor);
        PdfExplorer.temporaryPath = basepath;
        Mockito.when(ecm.getRenderer()).thenReturn(docRenderingService);
        Mockito.when(txHandler.getDocumentProofResolver()).thenReturn(new IDocumentProofResolver() {
            @Override
            public String getDocumentProofFilename(TransactionHandler txHandler, IECMService ecm, ECMDocument doc) {
                return proofName;
            }
        });
        
        Answer<Void> answer = new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                LOG.debug("Render doc");
                docRenderingService.put(ecm, txId, null, null, document);
                return null;
            }
        };
        Mockito.doAnswer(answer).when(ecm).put(Mockito.anyString(), Mockito.any(ECMDocument.class), Mockito.any(TransactionHandler.class));
    }

    private RenderingData getRenderingfromDefault() throws IOException {
        return getFSRendering(ecm, txId, fileName);
    }

    private RenderingData getFSRendering(IECMService ecm, String txId, String fileName) throws IOException {
        InputStream proof = (null != ecm.get(txId, proofName))? ecm.get(txId, proofName).readContent():null;
        return docRenderingService.getRenderingData(ecm, txId, txHandler.getDatabaseUserAccess().getPersonalInfo().getUser(), document,proof , Locale.FRANCE);
    }
    
    private static void setupRequest(String pageNumber, Long renderingID, String requestURI, String mimeType, HttpServletRequest request) {
        //request
        if (pageNumber != null)
            Mockito.when(request.getParameter(DocumentServletBase.URL_PARAM_PAGE_NUMBER)).thenReturn(pageNumber);
        if (requestURI != null)
            Mockito.when(request.getRequestURI()).thenReturn(requestURI);
        if (mimeType != null)
            Mockito.when(request.getParameter(DocumentServlet.URL_PARAM_FORMAT)).thenReturn(mimeType);
        if (renderingID != null)
            Mockito.when(request.getParameter(DocumentServlet.URL_PARAM_RENDERING_ID)).thenReturn(renderingID.toString());
        Mockito.when(request.getLocale()).thenReturn(Locale.FRANCE);
    }

    public static void setupDocument(
            ECMDocument document, Date updateDate, String fileName, String contentPath, String contentMimeType, SignatureField entityFiled, List<SignatureField> personalFields) throws PDFException, IOException, PDFSecurityException {
        //document
        Mockito.when(document.getUpdateDate()).thenReturn(updateDate);
        Mockito.when(document.getFilename()).thenReturn(fileName);
        Mockito.when(document.getContentMimeType()).thenReturn(contentMimeType);
        Mockito.when(document.readContent()).thenReturn(new FileInputStream(contentPath));
        if (personalFields != null && null != entityFiled )
        {
            List<PersonalSignature> personalSignatures = new ArrayList<PersonalSignature>();
            int i = 0;
            for (SignatureField field : personalFields) {
                VisibleSignatureField visibleSignatureField = new VisibleSignatureField(0, 0, 0, 0, field.getPage(), "");
                PersonalSignature ps = new PersonalSignature(visibleSignatureField, field.getLabelName(), field.getLabelName());
                personalSignatures.add(ps);
                i++;
            }
            Signatures signatures = new Signatures(new EntitySignature(new VisibleSignatureField(0, 0, 0, 0, entityFiled.getPage(), "")), personalSignatures);
            
            Mockito.when(document.getSignatures()).thenReturn(signatures);
        }
    }

    private static void setupECM(ECMDbService ecm, ECMDocument document) {
        Mockito.when(ecm.get(Mockito.any(String.class), org.mockito.Matchers.eq(fileName))).thenReturn(document);
        Mockito.when(ecm.getName()).thenReturn(ecmName);
    }
    
    private static void setupResponse(final ByteArrayOutputStream baos, HttpServletResponse response) throws IOException
    {
        Mockito.when(response.getOutputStream()).thenReturn(new ServletOutputStream()
        {
            @Override
            public void write(int b) throws IOException
            {
                baos.write(b);
            }
        });
    }

    private ByteArrayOutputStream reader(Date documentDate, String filename, SignatureField entity, List<SignatureField> personalFields) throws PDFException, IOException, PDFSecurityException {
        //Setup the request to get the Document as JSON
        setupRequest(null, null, "/uri", "application/json", request);
        
        setupDocument(document, documentDate, fileName,
                "./src/test/resources/pdf/" + filename,
                ContentType.MIMETYPE_PDF, entity, personalFields);
        reloadDocument(filename);
        setupECM(ecm, document); 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        setupResponse(baos, response);
        
        LOG.debug("put document");
        reloadDocument(filename);
        ecm.put(txId, document, null);
        
        //Simulate request sending
        reloadDocument(filename);
        boolean requestHandled = servlet.tryServeDocumentViewing(request, response, ecm, txHandler, fileName);        
        if (!requestHandled) {
            return null;
        }
        return baos; 
    }
    
    private void reloadDocument(String filename) throws PDFException, IOException, PDFSecurityException{
        Mockito.when(document.readContent()).thenReturn(new FileInputStream("./src/test/resources/pdf/"+ filename));
        setupDocument(document, new Date(0), fileName,
                "./src/test/resources/pdf/" + filename,
                ContentType.MIMETYPE_PDF, null, null);
    }
    /*
    ** Main test:
    */
    @Test 
    public void mainTest() throws IOException, PDFException, PDFSecurityException {
        // prepare proof
        Mockito.when(ecm.get(Mockito.any(String.class), org.mockito.Matchers.eq(proofName))).thenReturn(new ECMDocument(proofName, ContentType.MIMETYPE_XML, new FileInputStream("./src/test/resources/pdf/pdf_basic_ok_proof.xml"),null));
        //Simulate rendering request
        ByteArrayOutputStream baos = reader(new Date(0),"pdf_basic_ok.pdf",null,null);
        reloadDocument("pdf_basic_ok.pdf");
        Assert.assertNotNull(baos); 
        //Verify Response content type
        Mockito.verify(response).setContentType("application/json");
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        RenderingData data = gson.fromJson(baos.toString(), RenderingData.class);
        System.out.println(data.toJson());
        //Verify Page Number
        //Verify Pages array
        Assert.assertNotNull(data.getPages());
        Assert.assertEquals("Page number", 3, data.getPages().size());
        //Verify Rendering ID
        Assert.assertNotNull(data.getRenderingId());
        Mockito.when(ecm.get(txId,fileName)).thenReturn(new ECMDocument(fileName, "application/pdf", new FileInputStream("./src/test/resources/pdf/pdf_basic_ok.pdf"),null));
        Mockito.when(document.readContent()).thenReturn( new FileInputStream("./src/test/resources/pdf/pdf_basic_ok.pdf"));
        
        // Assert.assertEquals(data.getRenderingId(), getRenderingfromDefault().getRenderingId());

        //Simulate page query for all pages
        for (int i = 0; i < data.getPages().size(); i++) {
            Mockito.reset(request);
            Mockito.reset(response);

            setupRequest(Integer.toString(i), data.getRenderingId(), "/uri", null, request);
            baos.reset();
            setupResponse(baos, response);
            Assert.assertTrue(servlet.tryServeDocumentViewing(request, response, ecm, txHandler, fileName));
            Mockito.verify(response).setContentType("image/png");
            Assert.assertTrue(baos.toByteArray().length > 0);
        }
    }
    
    /*
    ** Previous test created an entry in the cache
    ** Let's simulate that the document has changed and that a signature field has been added
    */

    @Test
    public void testOutdated() throws IOException, PDFException, PDFSecurityException 
    {
        SignatureField entity = (new SignatureField("ENTITY", 1, 23, 23, 23, 23));
        List<SignatureField> signatureFields = new ArrayList<SignatureField>();
        signatureFields.add(new SignatureField("USER1", 1, 23, 23, 23, 23));
        Date date = new Date(0);
        // prepare proof
        Mockito.when(ecm.get(Mockito.any(String.class), org.mockito.Matchers.eq(proofName))).thenReturn(new ECMDocument(proofName, ContentType.MIMETYPE_XML, new FileInputStream("./src/test/resources/pdf/pdf_basic_ok_proof.xml"),null));
        ByteArrayOutputStream baos = reader(date,"pdf_basic_ok.pdf", entity, signatureFields);
        Assert.assertNotNull(baos); 

        // prepare proof
        Mockito.when(ecm.get(Mockito.any(String.class), org.mockito.Matchers.eq(proofName))).thenReturn(new ECMDocument(proofName, ContentType.MIMETYPE_XML, new FileInputStream("./src/test/resources/pdf/pdf_basic_ok_proof.xml"),null));
      //Read from the filesystem the existing entry
        reloadDocument("pdf_basic_ok.pdf");
        RenderingData data = getRenderingfromDefault();
        
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getRenderingId());

        //Reput the doc. This time, the document updateDate is more recent that the Rendering on disk.
         // ecm.put(txId, document, null); 

        //Rerun the request. This time, the document updateDate is more recent that the Rendering on disk.
         
        //reloadDocument("pdf_basic_ok_updated.pdf");
         
         SignatureField entity_udpated = (new SignatureField("ENTITY", 1, 23, 23, 23, 23));
         List<SignatureField> signatureFields_updated = new ArrayList<SignatureField>();
         signatureFields_updated.add(new SignatureField("USER1", 1, 23, 23, 23, 23));
         signatureFields_updated.add(new SignatureField("USER2", 1, 23, 23, 23, 23));
         
         // See http://stackoverflow.com/questions/11121772/when-i-run-mockito-test-occurs-wrongtypeofreturnvalue-exception
         // Mockito.when(ecm.get(Mockito.any(String.class), org.mockito.Matchers.eq(proofName))).thenReturn(new ECMDocument(proofName, ContentType.MIMETYPE_XML, new FileInputStream("./src/test/resources/pdf/pdf_basic_ok_updated_proof.xml"),null));
         Mockito.doReturn(new ECMDocument(proofName, ContentType.MIMETYPE_XML, new FileInputStream("./src/test/resources/pdf/pdf_basic_ok_updated_proof.xml"),null)).when(ecm).get(Mockito.any(String.class), org.mockito.Matchers.eq(proofName));
         
         ByteArrayOutputStream baos_updated = reader(new Date(0),"pdf_basic_ok_updated.pdf",entity_udpated,signatureFields_updated);
         Assert.assertNotNull(baos_updated); 
             
        //LOG.debug("put document");
        //reloadDocument("pdf_basic_ok_updated.pdf");
        //ecm.put(txId, document, null);
        
        //Simulate request sending 
        
        //Parse the response
        //Mockito.verify(response, Mockito.times(3)).setContentType("application/json");
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        RenderingData dataFromJSON = gson.fromJson(baos_updated.toString(), RenderingData.class);
        System.out.println(gson.toJson(dataFromJSON));
        System.out.flush();
        //Verify that a new RenderingID is returned.
        Assert.assertNotNull(dataFromJSON);
        Assert.assertNotNull(dataFromJSON.getRenderingId());
        // TODO Assert.assertFalse(data.getRenderingId() == dataFromJSON.getRenderingId());
        
        List<RenderingData.Page> pages = dataFromJSON.getPages();
        Assert.assertNotNull(pages);
        RenderingData.Page page = pages.get(0);
        Assert.assertNotNull(page);
        Assert.assertNotNull(page.signatureFields);
      //Verify that a Signature Field number is 3 
        Assert.assertEquals(3, page.signatureFields.size());
        
        SignatureField field = page.signatureFields.get(0);
        Assert.assertEquals("ENTITY", field.getLabelName());
        Assert.assertFalse(field.isSignable());
        
        SignatureField field2 = page.signatureFields.get(1);
        Assert.assertEquals("USER1", field2.getLabelName());
        Assert.assertFalse(field2.isSignable());
        
        SignatureField field3 = page.signatureFields.get(2);
        Assert.assertEquals("USER2", field3.getLabelName());
        Assert.assertTrue(field3.isSignable());
    }

    /*
    ** Test Load from disk
    */
    @Test
    public void testLoadFromDisk() throws IOException, PDFException, PDFSecurityException 
    {
        setupRequest(null, null, "/uri", "application/json", request);
        setupDocument(document, new Date(0), "MockedFileName", 
                "./src/test/resources/pdf/pdf_basic_ok.pdf", 
                ContentType.MIMETYPE_PDF, null,null);
        setupECM(ecm, document);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        setupResponse(baos, response);
        
        // prepare proof
        Mockito.when(ecm.get(Mockito.any(String.class), org.mockito.Matchers.eq(proofName))).thenReturn(new ECMDocument(proofName, ContentType.MIMETYPE_XML, new FileInputStream("./src/test/resources/pdf/pdf_basic_ok_proof.xml"),null));
      //Read from the filesystem the existing entry
        reloadDocument("pdf_basic_ok.pdf");
        //Read from the filesystem the existing entry
        RenderingData data = getRenderingfromDefault();
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getRenderingId());

        //Execute Request
        Mockito.when(ecm.get(txId,fileName)).thenReturn(new ECMDocument(fileName, "application/pdf", new FileInputStream("./src/test/resources/pdf/pdf_basic_ok.pdf"),null));
        Mockito.when(document.readContent()).thenReturn( new FileInputStream("./src/test/resources/pdf/pdf_basic_ok.pdf"));
        servlet.tryServeDocumentViewing(request, response, ecm, txHandler, fileName);

        //Parse the response
        Mockito.verify(response).setContentType("application/json");
        Gson gson = new Gson();
        RenderingData dataFromJSON = gson.fromJson(baos.toString(), RenderingData.class);
        //Verify that a new RenderingID is returned.
        Assert.assertNotNull(dataFromJSON);
        Assert.assertNotNull(dataFromJSON.getRenderingId());
        Assert.assertTrue(data.getRenderingId() == dataFromJSON.getRenderingId());
    }

    @Test(expected = UserException.class)
    public void testErrorNegPageNumber() throws IOException, PDFException, PDFSecurityException 
    {
        testErrorPageOutOfBound("-1");
    }

    public void testErrorZeroPageNumber() throws IOException, PDFException, PDFSecurityException  {
        testErrorPageOutOfBound("0");
    }

    @Test(expected = UserException.class)
    public void testErrorOOBPageNumber() throws IOException, PDFException, PDFSecurityException  {
        testErrorPageOutOfBound("999");
    }
    
    @Test(expected = UserException.class)
    public void testErrorInvalidPageNumber() throws IOException, PDFException, PDFSecurityException  {
        testErrorPageOutOfBound("invalid");
    }

    public void testErrorPageOutOfBound(String pageNumber) throws IOException, PDFException, PDFSecurityException  {
        setupDocument(document, new Date(0), fileName, 
                "./src/test/resources/pdf/pdf_basic_ok.pdf", 
                ContentType.MIMETYPE_PDF, null,null);
        setupRequest(pageNumber, getRenderingfromDefault().getRenderingId(), "/uri", "image/png", request);
        setupECM(ecm, document);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        setupResponse(baos, response);

        servlet.tryServeDocumentViewing(request, response, ecm, txHandler, fileName);
    }

    @Test(expected = UserException.class)
    public void testInvalidOutputMimeType() throws IOException, PDFException, PDFSecurityException  {
        testOutputMimeType("asdf");
    }

    public boolean testOutputMimeType(String mimeType) throws IOException, PDFException, PDFSecurityException {
        setupRequest("2", getRenderingfromDefault().getRenderingId(), "/uri", mimeType, request);
        setupDocument(document, new Date(0), "MockedFileName", 
                "./src/test/resources/pdf/pdf_basic_ok.pdf", 
                ContentType.MIMETYPE_PDF, null,null);
        setupECM(ecm, document);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        setupResponse(baos, response);

        return servlet.tryServeDocumentViewing(request, response, ecm, txHandler, fileName);
    }

    @Test(expected = EnvironmentException.class)
    public void testFileExistsButIsNotPDF() throws IOException, PDFException, PDFSecurityException {
        setupRequest(null, null, "/uri", "application/json", request);
        setupDocument(document, new Date(0), "MockedFileName", 
                "./src/test/resources/pdf/not_a_pdf.pdf",
                 ContentType.MIMETYPE_PDF, null,null);
        setupECM(ecm, document);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        setupResponse(baos, response);
        ecm.put(txId, document, null);
    }
}
