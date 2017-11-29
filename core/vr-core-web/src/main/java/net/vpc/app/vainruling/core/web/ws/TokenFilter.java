package net.vpc.app.vainruling.core.web.ws;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.web.HttpPlatformSession;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles Http Header X-JSESSIONID as a replacement of the SessionId when
 * specified. When not, fallback to legacy Cookie based sessions.
 */
public class TokenFilter implements Filter{
    private Map<String,HttpSession> sessions=new HashMap<>();
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequestWrapper rr = new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public HttpSession getSession() {
                return getSession(true);
            }

            @Override
            public HttpSession getSession(boolean create) {
                if (!"GET".equals(getMethod()) && !"POST".equals(getMethod())) {
                    return super.getSession(create);
                }
                String requestedSID = getRequestedSessionId();
                String preferredSID = getHeader("X-JSESSIONID");
                String theID = requestedSID;
                if (preferredSID != null && !preferredSID.equals(requestedSID)) {
                    theID = preferredSID;
                    HttpSession s = resolveSession(preferredSID);
                    if (s != null) {
                        return s;
                    }
                }
                HttpSession s = super.getSession(create);
                if(s==null){
                    return null;
                }
                HttpSession old = resolveSession(s.getId());

                if (old!=null) {
                    return old;
                }
                ///wrap Session into adapter so that it can be invalidated correctly
                HttpSessionAdapter adaptedSession = new HttpSessionAdapter(s, TokenFilter.this);
                if (theID == null) {
                    theID = s.getId();
                }
                registerSession(theID, adaptedSession);
                return adaptedSession;
            }
        };

        // reset Spring Web Thread Local vars.
        String REQUEST_ATTRIBUTES_ATTRIBUTE = RequestContextListener.class.getName() + ".REQUEST_ATTRIBUTES";
        ServletRequestAttributes attributes = new ServletRequestAttributes(rr);
        request.setAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE, attributes);
        LocaleContextHolder.setLocale(request.getLocale());
        RequestContextHolder.setRequestAttributes(attributes);

        chain.doFilter(rr, response);
    }

    private HttpSession resolveSession(String id){
        return sessions.get(id);
    }
    private void registerSession(String id,HttpSession session){
        sessions.put(id,session);
    }

    @Override
    public void destroy() {

    }


    private static class HttpSessionAdapter implements HttpSession {
        private final TokenFilter filter;
        private final HttpSession base;

        public HttpSessionAdapter(HttpSession base,TokenFilter filter) {
            this.base = base;
            this.filter = filter;
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

        public void setMaxInactiveInterval(int interval) {
            base.setMaxInactiveInterval(interval);
        }

        public int getMaxInactiveInterval() {
            return base.getMaxInactiveInterval();
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
            String id = getId();
            base.invalidate();
            filter.sessions.remove(id);
        }

        public boolean isNew() {
            return base.isNew();
        }
    }
}
