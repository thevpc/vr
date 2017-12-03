package net.vpc.app.vainruling.core.web.ws;

import net.vpc.app.vainruling.core.service.security.SessionStore;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

class HttpSessionAdapter implements HttpSession {
    private final SessionStore store;
    private final HttpSession base;

    public HttpSessionAdapter(HttpSession base, SessionStore store) {
        this.base = base;
        this.store = store;
    }

    public HttpSession getBase() {
        return base;
    }

    public long getCreationTime() {
        return base.getCreationTime();
    }

    public String getId() {
        return base.getId();
    }

    public long getLastAccessedTime() {
        return base.getLastAccessedTime();
    }

    public ServletContext getServletContext() {
        return base.getServletContext();
    }

    public int getMaxInactiveInterval() {
        return base.getMaxInactiveInterval();
    }

    public void setMaxInactiveInterval(int interval) {
        base.setMaxInactiveInterval(interval);
    }

    public HttpSessionContext getSessionContext() {
        return base.getSessionContext();
    }

    public Object getAttribute(String name) {
        return base.getAttribute(name);
    }

    public Object getValue(String name) {
        return base.getValue(name);
    }

    public Enumeration<String> getAttributeNames() {
        return base.getAttributeNames();
    }

    public String[] getValueNames() {
        return base.getValueNames();
    }

    public void setAttribute(String name, Object value) {
        base.setAttribute(name, value);
    }

    public void putValue(String name, Object value) {
        base.putValue(name, value);
    }

    public void removeAttribute(String name) {
        base.removeAttribute(name);
    }

    public void removeValue(String name) {
        base.removeValue(name);
    }

    public void invalidate() {
        IllegalStateException ex0=null;
        Exception ex1=null;
        String id = getId();
        try {
            base.invalidate();
        }catch (IllegalStateException ex){
            ex0=ex;
        }
        try {
            store.remove(id);
        }catch (Exception ex){
            ex1=ex;
        }
        if(ex0!=null){
            throw ex0;
        }
        if(ex1!=null){
            throw new IllegalStateException(ex1);
        }
    }

    public boolean isNew() {
        return base.isNew();
    }
}
