package com.dictao.dtp.web.proofexplorer;

import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;

import com.dictao.util.convert.Base64;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import com.dictao.dtp.core.ContentType;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import junit.framework.Assert;
import com.dictao.dtp.core.services.ecm.IndexEntry;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.core.ResourceBundleHandler;
import com.dictao.dtp.core.services.IValidationService;
import com.dictao.dtp.persistence.entity.Transaction;
import com.dictao.dtp.persistence.entity.UserAccess;
import com.dictao.dtp.web.proofexplorer.ProofItem;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(PowerMockRunner.class)
public class TestDepositProofController {

    private DepositProofController dpc;
    private X509Certificate certificate;
    @Mock private ResourceBundleHandler rbh;
    @Mock private TransactionFactory txService;
    @Mock private TransactionHandler handler;
    @Mock private UserAccess userAccess;
    @Mock private IECMService ecm;
    @Mock private IValidationService validation;

    private final static String ECM_SVC_ID = "mySvc";
    private final static String VALIDATION_SVC_ID = "myValidation";
    private final static String TX_ID = "myTx";
    private final static String ACCESS_ID = "myAccess";

    @Before
    public void setUp() throws CertificateException, IOException {
        MockitoAnnotations.initMocks(this);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(Base64.decode("MIIEXzCCA0egAwIBAgICBm0wDQYJKoZIhvcNAQEFBQAwgYsxCzAJBgNVBAYTAkZSMRMwEQYDVQQIEwpTb21lLVN0YXRlMQ4wDAYDVQQHEwVQYXJpczESMBAGA1UEChMJRGljdGFvIFNBMRAwDgYDVQQLEwdBbnlTaWduMTEwLwYDVQQDEyhBdXRvcml0ZSBkZSBjZXJ0aWZpY2F0aW9uIERpY3RhbyBBbnlTaWduMB4XDTEwMDQwNzEyMjk0NFoXDTIwMDQwNDEyMjk0NFowXTELMAkGA1UEBhMCRlIxDDAKBgNVBAgTA0lERjEOMAwGA1UEBxMFUGFyaXMxFzAVBgNVBAoTDkRlbW8gRGljdGFvIFNBMRcwFQYDVQQDEw5kd2Utc3NsLWNsaWVudDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMkqRwf5+ES9TGBrKNxNhuK2AayZTuoM6ySw4fyFESoCcD8+Hmj1KIbffR09wPRaVivfkPGK2RxnyY/zPJ5zG0lyD8SzHFmzhiOkqgpUdC2cqWj/h66XAeI6Y6ktCU3XU6v+ilnebtcpnon1qcLmADrHHDGCQtwTLhFvPO1arGhTAgKw53rKG1HjWbaRsVC+46mzL6YPaedUfV8nVIuelUlZgtV3g/uBcqMSYUH4wmcHcnID7PxM/sEzn9xboi4xbJfNgMuDGGrPNa5hePpuCRLVteXz5IW/pD0LNhKWPAU6SRl4McSCuSE0Fkae0TJNp+JyBgzwfnuEsgEMwJlxYTECAwEAAaOB+TCB9jAJBgNVHRMEAjAAMAsGA1UdDwQEAwIHgDATBgNVHSUEDDAKBggrBgEFBQcDAjARBglghkgBhvhCAQEEBAMCB4AwOAYJYIZIAYb4QgENBCsWKUludGVybmFsIERpY3RhbyBQS0kgR2VuZXJhdGVkIENlcnRpZmljYXRlMDoGA1UdHwQzMDEwL6AtoCuGKWh0dHA6Ly93d3cuZGljdGFvLmNvbS9BbnlTaWduL0FueVNpZ24uY3JsMB0GA1UdDgQWBBRpL5M49bQ6uCCHFWHdpCKfSv4W6DAfBgNVHSMEGDAWgBRuQPshsiUT7JErRI47FmUyqycBSDANBgkqhkiG9w0BAQUFAAOCAQEAwAlbRy/I9bYJQLET7J+Chlg3tj+viOPSaQ/B18CCyJfHkxHbp4NSaPK7Lo7I48qOstVS7cMcJNR2G6iPsEtwa2io8vQzufNjQEPqLbVf8NcOAShdLGxtMy/r4wcDykYaxXMjoFG2oqhBQ9wIAInhIA2WI5LjCJRVjIjYoe2YEMMFrUJjAho+OLEUlpzffQJXTKLXaOB3hjQAxmwxiZyXk1LxHylYymLN2ivR1rYtTmEw2wNRBO4vlwV6cfhGogEOllKFsFFAa+bBI0U9Iv7WDQx9MyU76wNXTBDgBDJdm/xX80+K37KKj/GyieItymKpmrbNcnCI0n8+wbD1BLPbvQ==")));
        certificate = (X509Certificate) cf.generateCertificate(bis);
        dpc = new DepositProofController(txService, rbh,certificate);
        dpc.setEcmServiceName(ECM_SVC_ID);
        dpc.setValidationServiceName(VALIDATION_SVC_ID);
        dpc.setTxId(TX_ID);
        when(txService.getService(TX_ID, ECM_SVC_ID,certificate)).thenReturn(ecm);
        when(txService.getService(TX_ID, VALIDATION_SVC_ID,certificate)).thenReturn(validation);
        when (txService.find (TX_ID,certificate)).thenReturn (handler);
        when (handler.getDatabaseTransaction ()).thenReturn (new Transaction (null, null, null, null, null, null, null, null, null, null, null, null));
    }


    @Test
    public void testLookUpLabelsAndDescInIndex() throws UnsupportedEncodingException {
        IndexEntry[] entries = new IndexEntry[1];
        //size set to 0
        entries[0] = new IndexEntry("Type1", "myfile.txt", null, false, 0);
        when(ecm.getIndexedDocumentListFromType(eq(TX_ID),any(List.class))).thenReturn(entries);
        when(ecm.get(TX_ID, "myfile.txt")).thenReturn(new ECMDocument("myfile.txt", ContentType.MIMETYPE_PDF, null));
        when(ecm.getIndexEntryLabel(TX_ID, "Type1",rbh)).thenReturn("label1");
        when(ecm.getIndexEntryDescription(TX_ID, "Type1",rbh)).thenReturn("desc1");
        List<ProofItem> items = dpc.getItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals("label1", items.get(0).getLabel());
        Assert.assertEquals("desc1", items.get(0).getDescription());
    }

}
