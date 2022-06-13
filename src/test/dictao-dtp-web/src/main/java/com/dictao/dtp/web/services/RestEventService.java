package com.dictao.dtp.web.services;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IEventService;
import com.dictao.dtp.core.services.Service;
import com.dictao.dtp.core.services.internal.SSLConnection;
import com.dictao.dtp.core.transactions.TransactionHandlerBase;
import com.dictao.dtp.core.transactions.events.Event;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import com.google.gson.Gson;

/**
 * 
 * @author kchakali, msauvee
 */
public class RestEventService extends Service implements IEventService {

    private static final Logger logu = LoggerFactory.getLogger("user." + RestEventService.class.getName());

    private List<SSLConnection> endpoints = new ArrayList<SSLConnection>();
    private final String serverId;
    private final boolean insecure;


    /**
     * @param name
     * @param description
     * @param endpoints Rest endpoints
     * @param insecure enabling host name verifying
     * @param serverId
     */
    public RestEventService(String name, String description, List<SSLConnection> endpoints, final boolean insecure,
            String serverId) {
        super(name, description);
        this.serverId = serverId;
        
        if (null == endpoints)
            throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "Invalid Rest Connections list was provided");
        
        for (SSLConnection ep : endpoints) {
            if (null != ep)
                this.endpoints.add(ep);
        }
        if (0 == this.endpoints.size())
            throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                    "No valid rest connection object was provided.");
        
        this.insecure = insecure;
    }

    public static class SendEventTask implements Runnable {

        private final SSLConnection connection;
        private final Event event;
        private final boolean allowAllHosts;

        private SendEventTask(SSLConnection connection, Event event, boolean insecure) {
            if (null == connection)
                throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                        "Invalid Rest Connection object was provided");
            this.connection = connection;
            this.event = event;
            this.allowAllHosts = insecure;
        }

        @Override
        public void run() {
            String message = new Gson().toJson(event);
            String url = this.connection.getEndpointUrl();    
            try {
                HttpClient httpClient = new DefaultHttpClient();
                int port = 443; // FIXME no port, no call..
                URL aURL = new URL(url);
                
                if (aURL.getPort() != -1) {
                    port = aURL.getPort();
                }
                
                SSLSocketFactory sslFactory =  buildSSLFactory(this.connection, this.allowAllHosts);
                Scheme sch = new Scheme("https", port, sslFactory);
                httpClient.getConnectionManager().getSchemeRegistry().register(sch);

                HttpPost postRequest = new HttpPost(url);
                StringEntity input = new StringEntity(message);
                input.setContentType("application/json");
                postRequest.setEntity(input);
                HttpResponse response = httpClient.execute(postRequest);

                if (response.getStatusLine().getStatusCode() != 201) {
                    logu.info("Sent event message '%s' to url '%s' : returned status = %d", message, url,
                            response.getStatusLine().getStatusCode());
                }
            } catch (Throwable ex) {
                logu.warn(ex, "unable to send event message '%s' to url '%s'", message, url);
            }
        }
    }

    @Override
    public void send(String event, TransactionHandlerBase txhandler) {
        
        // do nothing if no event
        if (event == null) 
            return;
        
        if (null == txhandler) 
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid parameter: provided transactionHandler should not be null");
 
        Event e = new Event(txhandler.getDatabaseTransaction().getTransactionID(), txhandler.getDatabaseUserAccess()
                .getAccessID(), event);
        for (SSLConnection cnx : endpoints) {
            new Thread(new SendEventTask(cnx, e, insecure)).start();
        }
    }    
    
    private static SSLSocketFactory buildSSLFactory (SSLConnection cnx, boolean insecure) throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException{
        
        SSLContext ctx = SSLContext.getInstance("TLS");

        ctx.init((null == cnx || null == cnx.getClientSSLParameter()) ? null : cnx.getClientSSLParameter().createKeyManagers(),
                 (null == cnx || null == cnx.getServerSSLParameter()) ? null : cnx.getServerSSLParameter().createTrustManagers(), 
                  null);
        X509HostnameVerifier hostnameVerifier = insecure ? SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
                : SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
        
        return new  SSLSocketFactory(ctx, hostnameVerifier);
    }

}
