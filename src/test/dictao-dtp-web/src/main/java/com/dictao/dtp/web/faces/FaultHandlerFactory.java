package com.dictao.dtp.web.faces;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 *
 * @author msauvee
 */
public class FaultHandlerFactory extends ExceptionHandlerFactory {

    private ExceptionHandlerFactory parent;

    public FaultHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler result = parent.getExceptionHandler();
        return new FaultHandler(result);
    }
}
