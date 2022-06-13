package com.dictao.dtp.web.proofexplorer;

import com.dictao.dtp.web.proofexplorer.ProofVisualization;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("dpve")
@RequestScoped
public class DepositProofVisualizationController extends ProofVisualization {

    /*
     * This Constructor is for passivation: no args.
     * Do not remove it
     */
    public DepositProofVisualizationController() {
    }
}