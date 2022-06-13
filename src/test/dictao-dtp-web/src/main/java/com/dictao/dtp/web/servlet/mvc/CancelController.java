package com.dictao.dtp.web.servlet.mvc;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.StepNameEnum;
import com.dictao.dtp.web.servlet.mvc.annotation.Path;

/**
 * A controller than handle click on cancel.
 * 
 * @author vreyduboissieu
 *
 */
@RequestScoped
@Path("/cancel")
public class CancelController extends AbstractController<TransactionHandler> {

    @Override
    public ModelAndView handle(String aid, String path, HttpServletRequest req, HttpServletResponse resp) {
        // get and build cancel url
        String backUrl = txHandler.getDatabaseUserAccess().getUIInfo().getBackUrl();
        String backUrlClickOnCancel = addParameterToUrl(backUrl, "status", "CLICK_ON_CANCEL");
        // addStep
        txHandler.addStep(StepNameEnum.USERACCESS_CANCELLED_BY_USER,"Redirection on url =" + backUrlClickOnCancel);
        // return a redirection
        ModelAndView mv = new ModelAndView("redirect:" + backUrlClickOnCancel);
        return mv;
    }


}
