package com.dictao.dtp.web.services.events;



import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.services.internal.SSLConnection;
import com.dictao.dtp.core.transactions.TransactionHandlerBase;
import com.dictao.dtp.core.transactions.events.Event;
import com.dictao.dtp.persistence.TransactionService;
import com.dictao.dtp.persistence.entity.Transaction;
import com.dictao.dtp.persistence.entity.UserAccess;
import com.dictao.dtp.web.services.RestEventService;
import com.dictao.dtp.web.services.RestEventService.SendEventTask;

/**
 *
 * @author msauvee
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SendEventTask.class, RestEventService.class})

public class TestEventService {
  
    List<SSLConnection> endpoints = new ArrayList<SSLConnection>(); 
    
    @Mock
    SSLConnection serverConnection;
    @Mock
    UserAccess ua;
    @Mock
    Transaction tx;
    @Mock
    SendEventTask eT;
    @Mock
    TransactionService transactionRepository;  
    @Mock
    TransactionHandlerBase th; 
    @Mock
    RestEventService srv;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);  
        PowerMockito.whenNew(SendEventTask.class).withArguments(eq(serverConnection), any(Event.class),eq(true)).thenReturn(eT);
    }
    
    @Test
    public void testNoEndpointsNOP() {
        RestEventService srv = null;
        
        try { // null
            srv = new RestEventService("event", "desc", null, true, "serverId");      

        } catch (EnvironmentException ex) {
            if (!EnvironmentException.Code.DTP_ENV_CONFIGURATION.equals(ex.getCode()))
                Assert.fail();
        }
        
        try { // empty
            List<SSLConnection> endpoints = new ArrayList<SSLConnection>();
            srv = new RestEventService("event", "desc", endpoints, true, "serverId");

        } catch (EnvironmentException ex) {
            if (!EnvironmentException.Code.DTP_ENV_CONFIGURATION.equals(ex.getCode()))
                Assert.fail();
        }
        try { // null entry
            List<SSLConnection> endpoints = new ArrayList<SSLConnection>();
            endpoints.add(null);
            srv = new RestEventService("event", "desc", endpoints, true, "serverId");

        } catch (EnvironmentException ex) {
            if (!EnvironmentException.Code.DTP_ENV_CONFIGURATION.equals(ex.getCode()))
                Assert.fail();
        }
        
        assertEquals(srv, null);
    }

    @Ignore //FIXME 
    @Test
    public void testNoEventNoFire() throws Exception {

       // RestEventService srv = new RestEventService("event", "desc", endpoints, true, "serverId");  
       srv.send(null, null);
       verify(eT, never()).run();
    }
    
    @Ignore //FIXME 
    @Test
    public void testEventFiredTwice() throws Exception {
        
       when(th.getDatabaseTransaction()).thenReturn(tx);
       when(th.getDatabaseTransaction().getTransactionID()).thenReturn("mockedTxid");
       when(th.getDatabaseUserAccess()).thenReturn(ua);
       when(th.getDatabaseUserAccess().getAccessID()).thenReturn("mockedAid");
       // add second endpoint.
       endpoints.add(serverConnection);
       //sending event..
       srv.send("foo", th);    
       verify(eT, times(2)).run();
    }

}
