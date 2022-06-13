package com.dictao.dtp.web.ws.port.fo;

import com.dictao.dtp.web.ws.ExceptionConverter;
import com.dictao.dtp.web.ws.Task;
import com.dictao.dtp.web.ws.WebServiceImpl;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import com.dictao.wsdl.dtp.frontoffice.v3.EchoPort;
import com.dictao.wsdl.dtp.frontoffice.v3.SystemFaultException;
import com.dictao.wsdl.dtp.frontoffice.v3.UserFaultException;
import com.dictao.xsd.dtp.error.v3.SystemErrorInfo;
import com.dictao.xsd.dtp.error.v3.UserErrorInfo;
import com.sun.xml.ws.developer.SchemaValidation;
import javax.jws.WebService;

@SchemaValidation
@WebService(serviceName = "FrontOfficeService", portName = "echoPort", endpointInterface = "com.dictao.wsdl.dtp.frontoffice.v3.EchoPort", targetNamespace = "http://dictao.com/wsdl/dtp/frontoffice/v3")
public class EchoImpl extends WebServiceImpl implements EchoPort{
    private static final Logger LOGU = LoggerFactory.getLogger(EchoImpl.class, true);
    
    private static final ExceptionConverter<UserFaultException, SystemFaultException> ec = new ExceptionConverter<UserFaultException, SystemFaultException>() {

        @Override
        protected UserFaultException newUserException(String msg,
                UserErrorInfo faultInfo) {
            return new UserFaultException(msg, faultInfo);
        }

        @Override
        protected SystemFaultException newEnvironmentException(String msg,
                SystemErrorInfo faultInfo) {
            return new SystemFaultException(msg, faultInfo);
        }
    };
    
    @Override
    public String echo(final String request) throws SystemFaultException, UserFaultException {
        Task<String, UserFaultException, SystemFaultException> t = 
                new Task<String, UserFaultException, SystemFaultException>(ec, getSSLCertificate()) {
            @Override
            public String run() {
                LOGU.info("Return value='%s'", request);                
                return request;
            }
        };
        return t.execute();        
    }
    
}
