package com.dictao.dtp.web.gwt.server;

import java.util.Date;

import org.apache.log4j.MDC;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.web.INavHandler;
import com.dictao.dtp.web.data.RedirectStatus;
import com.dictao.dtp.web.gwt.common.shared.entity.exception.RedirectException;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

/**
 * @author VRB adapted by KCH
 */
public abstract class DtpServiceCall<V,T extends TransactionHandler>  {

    /* **************************** CONSTANTS *************************** */
    private static final Logger LOG = LoggerFactory
            .getLogger(DtpServiceCall.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + DtpServiceCall.class.getName());
    public static final String HTTP_PARAM_TRANSACTION_ID = "tid";
    /* **************************** ATTRIBUTES ************************** */
    protected T txHandler;
    private INavHandler navHandler;
    /* ************************** PUBLIC METHODS ************************ */

    public DtpServiceCall(final String accessID, TransactionFactory txFct,
            final INavHandler navHdlr) throws RedirectException {
        try {
            this.navHandler = navHdlr;
            this.txHandler = txFct.<T> find(accessID);
            if ((txHandler == null) || !txHandler.isValidUserAccess())
                throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                        "Access unauthorized for accessId='%s'",accessID);

            txHandler.setRequestInfo(navHandler.getRequestInfo());
            navHandler.setTransactionHandler(txHandler);
            
        } catch (EnvironmentException ex) {
            LOG.error(ex); 
            navHandler.redirectOnError(RedirectStatus.ENVIRONMENT_ERROR);
        } catch (UserException ex) {
            LOGU.error(ex);
            navHandler.redirectOnError(RedirectStatus.USER_ERROR);
        } catch (Exception ex) {
            LOG.error(ex);
            navHandler.redirectOnError(RedirectStatus.INTERNAL_ERROR);
        }            
    }
    
    public V execute() throws RedirectException {
        LOGU.info("%s", this);
        V result = null;
        Date start = new Date();
        long duration = 0;
        try {
            result = run();
            MDC.clear();
        } catch (RedirectException re) {
            LOG.debug(re);
            throw re;
        } catch (EnvironmentException ex) {
            LOG.error(ex);
            navHandler.redirectOnError(RedirectStatus.ENVIRONMENT_ERROR);
        } catch (UserException ex) {
            LOGU.error(ex);
            navHandler.redirectOnError(RedirectStatus.USER_ERROR);
        } catch (Exception ex) {
            LOG.error(ex);
            navHandler.redirectOnError(RedirectStatus.INTERNAL_ERROR);
        }
        duration = new Date().getTime() - start.getTime();
        LOGU.info("%s executed in %d ms", this, duration);
        return result;
    }

    public abstract V run() throws Exception;
    
    /* ********************* PROTECTED/PRIVATE METHODS ****************** */
}
