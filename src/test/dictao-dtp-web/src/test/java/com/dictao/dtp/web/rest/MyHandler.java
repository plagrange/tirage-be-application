package com.dictao.dtp.web.rest;

import com.dictao.dtp.core.ResourceBundleHandler;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ISignService;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.StepNameEnum;

public class MyHandler extends TransactionHandler {

    public IECMService ecm;
    public ISignService sign;

    public void seal(ResourceBundleHandler rbh) {
        super.seal(ecm,  rbh, "SEAL");
    }

    @Override
    public void finishUserAccess() {
        super.addStep(StepNameEnum.USERACCESS_ENDED_SUCESSFULY);
    }
}
