package com.dictao.dtp.web.proofexplorer;

import java.security.cert.X509Certificate;

import com.dictao.dtp.web.proofexplorer.ProofExplorer;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.ResourceBundleHandler;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("dpe")
@RequestScoped
public class DepositProofController extends ProofExplorer {

    /*
     * This Constructor is for passivation: no args.
     * Do not remove it
     */
    public DepositProofController() {
    }

    /*
     * This Constructor is for test
     */
    public DepositProofController(TransactionFactory txService, ResourceBundleHandler rbh, X509Certificate certificate) {
        this.txService = txService;
        this.rbh = rbh;
        this.setCertificate(certificate);
    }

}
