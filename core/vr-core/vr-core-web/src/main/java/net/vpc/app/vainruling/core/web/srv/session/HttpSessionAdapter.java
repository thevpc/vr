package net.vpc.app.vainruling.core.web.srv.session;

import net.vpc.app.vainruling.core.service.security.SessionStore;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

public class HttpSessionAdapter implements HttpSession {
    private final SessionStore store;
    private final HttpSession base;
    private final String id;
    private final String id0;

    public HttpSessionAdapter(String id,HttpSession base, SessionStore store) {
        this.base = base;
        this.store = store;
        this.id = id;
        this.id0 = base.getId();
        if(!id.equals(id0)){
            System.out.println("Why");
        }
    }

    public HttpSession getBase() {
        return base;
    }

    @Override
    public long getCreationTime() {
        try {
            return base.getCreationTime();
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
          throw ex;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        try {
            return base.getLastAccessedTime();
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
            throw ex;
        }
    }

    @Override
    public ServletContext getServletContext() {
        return base.getServletContext();
    }

    @Override
    public int getMaxInactiveInterval() {
        return base.getMaxInactiveInterval();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        base.setMaxInactiveInterval(interval);
    }

    @Override
    public Object getAttribute(String name) {
        try {
            return base.getAttribute(name);
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
            throw ex;
        }
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        try {
            return base.getAttributeNames();
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
            throw ex;
        }
    }

    @Override
    public void removeAttribute(String name) {
        try {
            base.removeAttribute(name);
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
            throw ex;
        }
    }

    @Override
    public void invalidate() {
        IllegalStateException ex0 = null;
        Exception ex1 = null;
        String id = getId();
        try {
            base.invalidate();
        } catch (IllegalStateException ex) {
            ex0 = ex;
        }
        try {
            propagateInvalidation();
        } catch (Exception ex) {
            ex1 = ex;
        }
        if (ex0 != null) {
            throw ex0;
        }
        if (ex1 != null) {
            throw new IllegalStateException(ex1);
        }
    }

    private boolean silentPropagateInvalidation() {
        try {
            propagateInvalidation();
            return true;
        } catch (Exception ex) {
            //
        }
        return false;
    }

    private void propagateInvalidation() {
        String id = getId();
        store.remove(id);
    }

    public boolean isNew() {
        try {
            return base.isNew();
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
            throw ex;
        }
    }

    public void setAttribute(String name, Object value) {
        try {
            base.setAttribute(name, value);
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
            throw ex;
        }
    }


    //deprecated!!
    @Override
    public HttpSessionContext getSessionContext() {
        return base.getSessionContext();
    }

    @Override
    public Object getValue(String name) {
        return base.getValue(name);
    }

    @Override
    public String[] getValueNames() {
        try {
            return base.getValueNames();
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
            throw ex;
        }
    }

    @Override
    public void putValue(String name, Object value) {
        try {
            base.putValue(name, value);
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
            throw ex;
        }
    }

    @Override
    public void removeValue(String name) {
        try {
            base.removeValue(name);
        }catch (IllegalStateException ex){
            silentPropagateInvalidation();
            throw ex;
        }
    }

}
