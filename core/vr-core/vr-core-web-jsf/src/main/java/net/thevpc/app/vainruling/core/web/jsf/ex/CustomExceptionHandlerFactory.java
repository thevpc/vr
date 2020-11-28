package net.thevpc.app.vainruling.core.web.jsf.ex;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Created by vpc on 6/23/16.
 */
public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {
    private ExceptionHandlerFactory parent;

    // this injection handles jsf
    public CustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {

        ExceptionHandler handler = new CustomExceptionHandler(parent.getExceptionHandler());

        return handler;
    }

}