package com.dictao.dtp.web.rest.ui;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.dictao.dtp.core.data.DocStepInfo;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ISignService;
import com.dictao.dtp.core.services.ecm.ECMDbService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.transactions.ApplicationConf;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.core.transactions.CertificateConf;
import com.dictao.dtp.core.transactions.HandlerTypeConf;
import com.dictao.dtp.core.transactions.ServiceMapConf;
import com.dictao.dtp.core.transactions.TenantConf;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.TransactionService;
import com.dictao.dtp.persistence.UserAccessService;
import com.dictao.dtp.persistence.data.AuthenticationInfo;
import com.dictao.dtp.persistence.data.PersonalInfo;
import com.dictao.dtp.persistence.data.UIInfo;
import com.dictao.dtp.persistence.data.UserDN;
import com.dictao.dtp.persistence.entity.Transaction;
import com.dictao.dtp.persistence.entity.UserAccess;
import com.dictao.dtp.web.rest.BaseDocumentCtrl;
import com.dictao.dtp.web.rest.MockHelper;
import com.dictao.dtp.web.rest.MyHandler;
import com.dictao.util.convert.Base64;
import com.dictao.dtp.core.services.ecm.IndexEntry;

public class DocumentCtrlTest {

    private static String APP_ID = "myapp";
    private static String APP_NAME = "myapp";
    private static String TENANT_ID = "customer";
    @Mock
    private TransactionService transactionRepository;
    @Mock
    private UserAccessService accessRepository;
    @Mock
    private TenantConf tenant;
    @Mock
    private IECMService ecm;
    @Mock
    private ISignService sign;
    @Mock
    private ISignService signEntity;
    @Mock
    private ISignService d2sSealing;
    @Mock
    private HttpServletRequest request = null;
    @Mock
    private ServletContext servletContext = null;
    private TransactionFactory factory;
    private X509Certificate certificate;
    private Map<String, String> ref = new HashMap<String, String>();
    private static final String TYPE_X509 = "javax.servlet.request.X509Certificate";
    private String tid;
    private DocumentCtrl documentCtrl;
    private List<String> documents = new ArrayList<String>();
    private TransactionHandler txHandler;
    private Transaction tx;

    @Before
    public void setUp() throws CertificateException {
        MockitoAnnotations.initMocks(this);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(Base64.decode("MIID2zCCAsOgAwIBAgICB6cwDQYJKoZIhvcNAQEFBQAwgYsxCzAJBgNVBAYTAkZSMRMwEQYDVQQIEwpTb21lLVN0YXRlMQ4wDAYDVQQHEwVQYXJpczESMBAGA1UEChMJRGljdGFvIFNBMRAwDgYDVQQLEwdBbnlTaWduMTEwLwYDVQQDEyhBdXRvcml0ZSBkZSBjZXJ0aWZpY2F0aW9uIERpY3RhbyBBbnlTaWduMB4XDTExMDMyNTEzNTc1NloXDTIxMDMyMjEzNTc1NlowXTELMAkGA1UEBhMCRlIxDDAKBgNVBAgTA0lERjEOMAwGA1UEBxMFUGFyaXMxFzAVBgNVBAoTDkRlbW8gRGljdGFvIFNBMRcwFQYDVQQDEw5EZW1vLURUUC1Vc2VyMTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAqA0HE586V85JM8j1vTquOHuJQl9xVgyupT8gAPe4caI1aAeJlWDDMw9s6VEdou77PsGenIIbM2uEC59e03Yl5SbVeoaVsP1OzkubW5T1W0lv/ESl3vOiCsUey7xd8RJB4Plb4RqYf8jjDYPXQW87h2Q3Qj2l2mciRUY+8i/uFb8CAwEAAaOB+TCB9jAJBgNVHRMEAjAAMAsGA1UdDwQEAwIHgDATBgNVHSUEDDAKBggrBgEFBQcDAjARBglghkgBhvhCAQEEBAMCB4AwOAYJYIZIAYb4QgENBCsWKUludGVybmFsIERpY3RhbyBQS0kgR2VuZXJhdGVkIENlcnRpZmljYXRlMDoGA1UdHwQzMDEwL6AtoCuGKWh0dHA6Ly93d3cuZGljdGFvLmNvbS9BbnlTaWduL0FueVNpZ24uY3JsMB0GA1UdDgQWBBS58bEAoBCacw7gHf2YtOs6VVg4jDAfBgNVHSMEGDAWgBRuQPshsiUT7JErRI47FmUyqycBSDANBgkqhkiG9w0BAQUFAAOCAQEApDBaacGOCb57lZqZh8dt85kr+Wgb7hNWM9qT6l3aYqm9KWjqOhVSc5nVoq2hqWHRYbMfwtFTUHtGruEbWNfASv2GvRD0w1wEicEpUBHGPfvWtyNH4cLiTELDf5hfI2wCSxm6r/DS+BP+FwskEcGyEDtEYNdjy95UXON0tTl34IOLn67NHwDQ3AfAm0KgoVhqI1lgg4rXr3AZlSoyjuXIikMXBKLCJl0IElIYG0/iIuleYhNRE8f3wssZrDclWvhMOiuoBdbvONfUmwfbgrbFBFFf5XRsHjTSnnlffwAXhqIPLFETHVRqwtXfjpS8PAO1lO1mt2tKAq35RZhxxpYVDA==")));
        certificate = (X509Certificate) cf.generateCertificate(bis);
        ApplicationListConf appsConf = new ApplicationListConf();
        factory = new TransactionFactory(appsConf, transactionRepository, accessRepository);
        Map<String, HandlerTypeConf> bt = new HashMap<String, HandlerTypeConf>();
        bt.put("myHandler", new HandlerTypeConf(MyHandler.class, null, null));
        List<CertificateConf> certificatesList = new ArrayList<CertificateConf>();
        certificatesList.add(
                new CertificateConf("CN=Demo-DTP-User1, O=Demo Dictao SA, L=Paris, ST=IDF, C=FR",
                "CN=Autorite de certification Dictao AnySign, OU=AnySign, O=Dictao SA, L=Paris, ST=Some-State, C=FR"));
        ServiceMapConf appSvc = new ServiceMapConf();
        appSvc.put("ecm", ecm, bt);
        appSvc.put("sign", sign, bt);
        appSvc.put("signEntity", signEntity, bt);
        appSvc.put("d2sSealing", d2sSealing, bt);

        when(tenant.getName()).thenReturn(TENANT_ID);

        ref.put(tenant.getName() + ":" + APP_NAME, APP_ID);

        Map<String, ApplicationConf> appTmp = new HashMap<String, ApplicationConf>();
        final ApplicationConf appConf = new ApplicationConf(APP_ID, APP_NAME, tenant, certificatesList, null, null, appSvc,null, bt, null, null, null, true, 0, null);
        appTmp.put(APP_ID, appConf);
        appsConf.setConfigurations(null, null, appTmp, null, ref);

        when(d2sSealing.signDetachedXadesManifest(any(TransactionHandler.class), any(Map.class), any(DocStepInfo.class))).thenReturn(new ByteArrayInputStream("my signature".getBytes()));

        when(ecm.getName()).thenReturn("ecm");
        when(accessRepository.update(any(UserAccess.class))).thenAnswer(new Answer<UserAccess>() {

            @Override
            public UserAccess answer(InvocationOnMock invocation) throws Throwable {
                return (UserAccess) invocation.getArguments()[0];
            }
        });
        when(transactionRepository.update(any(Transaction.class))).thenAnswer(new Answer<Transaction>() {

            @Override
            public Transaction answer(InvocationOnMock invocation) throws Throwable {
                return (Transaction) invocation.getArguments()[0];
            }
        });

        when(request.getAttribute(TYPE_X509)).thenReturn(new X509Certificate[]{certificate});


        txHandler = factory.create("myHandler", tenant.getName(), APP_ID, null, null, "mycompany", certificate, "ecm", null, null, null, "tags");
        tid = txHandler.getTransactionId();
        	
	List<IndexEntry> indexEntries = new ArrayList<IndexEntry>();
        indexEntries.add(new IndexEntry("type1", "contract.pdf", null, false, 0));
	indexEntries.add(new IndexEntry("type2", "id.png", null, false, 0));
	when(ecm.getIndexedDocumentList(tid)).thenReturn(indexEntries.toArray(new IndexEntry[0]));

	tx = MockHelper.getCreatedTransaction(transactionRepository);
        when(transactionRepository.find(tx.getTransactionID())).thenReturn(tx);
        when(transactionRepository.update(any(Transaction.class))).thenReturn(tx);

        documentCtrl = new DocumentCtrl(factory);
    }

    @After
    public void tearDown() {
    }

    /**
     * Tests for getDocumentWithAccessId method, of class DocumentCtrl.
     */
    @Test
    public void testGetDocumentWithAccessId() throws Exception {

        byte[] data1 = "data1".getBytes();
        byte[] data2 = "data2".getBytes();
        addDocument("contract.pdf", "application/pdf", data1);
        addDocument("id.png", "image/png", data2);

        String aid = addUserAccess();

        Response response = documentCtrl.getDocumentWithAccessId("ecm", aid, "contract.pdf", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
        checkReponse(response, data1, "application/pdf", "contract.pdf", "inline");

        Response response2 = documentCtrl.getDocumentWithAccessId("ecm", aid, "id.png", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_ATTACHMENT, null, null);
        checkReponse(response2, data2, "image/png", "id.png", "attachment");

    }

    @Test(expected = UserException.class)
    public void testGetDocumentWithAccessIdWithInvalidAccessId() throws Exception {
        byte[] data = "data".getBytes();
        addDocument("contract.pdf", "application/pdf", data);
        addUserAccess();
        documentCtrl.getDocumentWithAccessId("ecm", "INVALID", "contract.pdf", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
    }

    @Test(expected = UserException.class)
    public void testGetDocumentWithAccessIdWithInvalidEcm() throws Exception {
        byte[] data = "data".getBytes();
        addDocument("contract.pdf", "application/pdf", data);
        String aid = addUserAccess();
        documentCtrl.getDocumentWithAccessId("BAD_ECM", aid, "contract.pdf", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
    }

    @Test(expected = UserException.class)
    public void testGetDocumentWithAccessIdWithInvalidDocumentName() throws Exception {
        byte[] data = "data".getBytes();
        addDocument("contract.pdf", "application/pdf", data);
        String aid = addUserAccess();
        documentCtrl.getDocumentWithAccessId("ecm", aid, "INVALID_CONTRACT", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
    }

    /**
     * Tests for getDocumentWithAccessId method, of class DocumentCtrl.
     */
    @Test
    public void testGetDocumentWithExternalAccessId() throws Exception {

        byte[] data1 = "data1".getBytes();
        byte[] data2 = "data2".getBytes();
        addDocument("contract.pdf", "application/pdf", data1);
        addDocument("id.png", "image/png", data2);

        String eaid = addUserAccess("ext");

        Response response = documentCtrl.getDocumentWithExternalAccessId("ecm", TENANT_ID, APP_NAME, eaid, "contract.pdf", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
        checkReponse(response, data1, "application/pdf", "contract.pdf", "inline");

        Response response2 = documentCtrl.getDocumentWithExternalAccessId("ecm", TENANT_ID, APP_NAME, eaid, "id.png", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_ATTACHMENT, null, null);
        checkReponse(response2, data2, "image/png", "id.png", "attachment");

    }

    @Test(expected = UserException.class)
    public void testGetDocumentWithExternalAccessIdWithInvalidTenant() throws Exception {
        byte[] data = "data".getBytes();
        addDocument("contract.pdf", "application/pdf", data);
        String eaid = addUserAccess("ext");
        documentCtrl.getDocumentWithExternalAccessId("ecm", "INVALID_TENANT", APP_NAME, eaid, "contract.pdf", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
    }

    @Test(expected = UserException.class)
    public void testGetDocumentWithExternalAccessIdWithInvalidAppName() throws Exception {
        byte[] data = "data".getBytes();
        addDocument("contract.pdf", "application/pdf", data);
        String eaid = addUserAccess("ext");
        documentCtrl.getDocumentWithExternalAccessId("ecm", TENANT_ID, "INVALID_APP_NAME", eaid, "contract.pdf", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
    }

    @Test(expected = UserException.class)
    public void testGetDocumentWithExternalAccessIdWithInvalidExternalAccessId() throws Exception {
        byte[] data = "data".getBytes();
        addDocument("contract.pdf", "application/pdf", data);
        addUserAccess();
        documentCtrl.getDocumentWithExternalAccessId("ecm", TENANT_ID, APP_NAME, "INVALID", "contract.pdf", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
    }

    @Test(expected = UserException.class)
    public void testGetDocumentWithExternalAccessIdWithInvalidEcm() throws Exception {
        byte[] data = "data".getBytes();
        addDocument("contract.pdf", "application/pdf", data);
        String eaid = addUserAccess("ext");
        documentCtrl.getDocumentWithExternalAccessId("BAD_ECM", TENANT_ID, APP_NAME, eaid, "contract.pdf", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
    }

    @Test(expected = UserException.class)
    public void testGetDocumentWithExternalAccessIdWithInvalidDocumentName() throws Exception {
        byte[] data = "data".getBytes();
        addDocument("contract.pdf", "application/pdf", data);
        String eaid = addUserAccess("ext");
        documentCtrl.getDocumentWithExternalAccessId("ecm", TENANT_ID, APP_NAME, eaid, "INVALID_CONTRACT", request, servletContext, BaseDocumentCtrl.URL_PARAM_DISPOSITION_VALUE_INLINE, null, null);
    }

    private void addDocument(String filename, String contentType, byte[] data) {
        ECMDocument doc = new ECMDocument(filename, contentType, data);
        ecm.put(tid, doc, null);
        when(ecm.get(tid, doc.getFilename(), ECMDbService.ROLE_USER_ACCESS)).thenReturn(doc);
        documents.add(filename);
        when(ecm.getDocumentList(tid)).thenReturn(documents);
    }

    private String addUserAccess(String externalAccessId) throws ParseException {

        PersonalInfo personalInfo = new PersonalInfo("USER0", "M.", "fn", "ls", new SimpleDateFormat("yyyy-MM-dd").parse("2000-11-22"),
                new UserDN("cn", "o", "ou", "email@dictao.com", "cn", "alt"), true);
        UIInfo uiInfo = new UIInfo("myworkflow", "uaLabel", "uaType", "uaConsent", "uaTerms", "http://www.acme.com/portal/back");
        AuthenticationInfo authenticationInfo = new AuthenticationInfo("uid", "0601020304", 15);

        ArgumentCaptor<UserAccess> ua = ArgumentCaptor.forClass(UserAccess.class);
        String auId = txHandler.addUserAccess(externalAccessId, true, uiInfo, personalInfo, authenticationInfo, 100000L, null);
        verify(accessRepository).update(ua.capture());
        when(accessRepository.update(any(UserAccess.class))).thenReturn(ua.getValue());

        if (externalAccessId == null) {
            when(accessRepository.find(auId)).thenReturn(ua.getValue());
        } else {
            when(accessRepository.findFromExternalId(APP_ID, externalAccessId)).thenReturn(ua.getValue());
        }

        return auId;
    }

    private String addUserAccess() throws ParseException {
        return addUserAccess(null);
    }

    private void checkReponse(Response response, byte[] expectedData, String contentType, String filename, String disposition) throws IOException {
        StreamingOutput outputStream = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        outputStream.write(os);
        byte[] responseData = os.toByteArray();
        assertArrayEquals(expectedData, responseData);
        checkReponseHeaders(response, contentType, filename, disposition);
    }

    private void checkReponseHeaders(Response response, String contentType, String filename, String disposition) throws IOException {
        assertTrue(((String) response.getMetadata().get("Content-Disposition").get(0)).contains(disposition));
        assertTrue(((String) response.getMetadata().get("Content-Disposition").get(0)).contains(filename));
        assertEquals(((MediaType) response.getMetadata().get("Content-Type").get(0)).toString(), contentType);
    }
}
