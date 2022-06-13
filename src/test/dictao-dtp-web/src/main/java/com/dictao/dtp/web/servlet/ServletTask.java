package com.dictao.dtp.web.servlet;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.web.ws.Task;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.MDC;

public abstract class ServletTask {

    private static final Logger LOG = LoggerFactory.getLogger(ServletTask.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."+ServletTask.class.getName());
    private final HttpServletResponse response;
    
    public ServletTask(final HttpServletResponse response) {
        this.response = response;
    }

    public void execute() throws IOException {
        Logger logu = LOGU;
        Date start = new Date();
        long duration = 0;
        try {
            LOG.entering(this.toString());
            Monitoring.start();
            Logger tmp = getUserLogger();
            if (tmp != null)
                logu = tmp;
            logu.info("%s", this);
            run();
            Monitoring.stop();
            duration = new Date().getTime() - start.getTime();
            logu.info("%s executed in %d ms", this, duration);
            MDC.clear();
        } catch (UserException ex) {
            Monitoring.stop();
            logu.error(ex, "Failed to %s in %d ms. Cause: %s", this, duration, ex.getMessage());
            if (ex.getCode().equals(UserException.Code.DTP_USER_UNAUTHORIZED))
                sendError(response,HttpServletResponse.SC_UNAUTHORIZED);
            else {
                sendError(response,HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (EnvironmentException ex) {
            Monitoring.stop();
            LOG.error(ex, "Failed to %s in %d ms. Cause: %s", this,duration,  ex.getMessage());
            sendError(response,HttpServletResponse.SC_SERVICE_UNAVAILABLE);
       
        } catch (Exception ex) {
            Monitoring.stop();
            LOG.error(ex, "Failed to %s in %d ms. Cause: %s", this, duration, ex.getMessage());
            sendError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        MDC.clear();
        LOG.exiting(this.toString());
    }

    public abstract void run() throws Exception;    

    protected Logger getUserLogger() {
        return LOGU;
    }
    
    private void sendError(HttpServletResponse response, int errorStatusCode) throws IOException {
        if (!response.isCommitted()) {
            response.reset();
            response.sendError(errorStatusCode);
        } else {
            LOG.warn("the response has already been committed,"
                    + " After using sendError method, the response should be considered to be committed and should not be written to.");
        }
    }
}
