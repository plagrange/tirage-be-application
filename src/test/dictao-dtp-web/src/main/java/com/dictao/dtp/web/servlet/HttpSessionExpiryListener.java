package com.dictao.dtp.web.servlet;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

/**
 * <p>Listens to HttpSession expiry.<br />
 * <p>The class is configured in web.xml</p>
 */
public class HttpSessionExpiryListener implements HttpSessionListener {

    private static Logger LOG = LoggerFactory.getLogger(HttpSessionExpiryListener.class);
    private static int currentSession = 0;

    public HttpSessionExpiryListener() {
        super();
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        LOG.debug("new session %1$s was created.", se.getSession().getId());
        synchronized(this) {
            currentSession++;
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

        HttpSession httpSession = se.getSession();
        LOG.debug("session %1$s expired.", httpSession.getId());
        synchronized(this) {
            currentSession--;
        }
        Date sessionCreationDate = new Date(httpSession.getCreationTime());
        Date sessionLastAccessDate = new Date(httpSession.getLastAccessedTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        LOG.info("session %1$s timed out. "
                + "Http session creation date was %2$s, last access date was %3$s.",
                httpSession.getId(), sdf.format(sessionCreationDate), sdf.format(sessionLastAccessDate));
    }

    public static int getCurrentSession() {
        return currentSession;
    }
}
