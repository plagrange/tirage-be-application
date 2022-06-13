package com.dictao.dtp.web.servlet.mvc;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.web.servlet.mvc.annotation.Path;

@RequestScoped
@Path("/error")
public class ErrorController extends AbstractController<TransactionHandler> {

    @Inject
    private TransactionFactory tf;

    @Override
    public ModelAndView handle(String aid, String path, HttpServletRequest req, HttpServletResponse resp) {
        // TODO log!!!!
        TransactionHandler th = tf.find(aid);
        String backUrl = th.getDatabaseUserAccess().getUIInfo().getBackUrl();
        String backUrlOk = addParameterToUrl(backUrl, "status", "INTERNAL_ERROR");
        ModelAndView mv = new ModelAndView("redirect:" + backUrlOk);
        return mv;
    }


}
