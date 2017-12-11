package net.vpc.app.vainruling.core.web.ws;

import net.vpc.app.vainruling.core.service.PlatformSession;
import net.vpc.app.vainruling.core.service.security.SessionStore;
import net.vpc.app.vainruling.core.web.HttpPlatformSession;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * This class handles Http Header X-JSESSIONID as a replacement of the SessionId when
 * specified. When not, fallback to legacy Cookie based sessions.
 */
public class TokenManagerFilter implements Filter {
    private ThreadLocal<HttpServletResponse> httpServletResponseThreadLocal=new ThreadLocal<>();
    private FilterConfig filterConfig;
    private LinkedHashMap<String, HttpSessionSerializer> serializers = new LinkedHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        Map<String, String> configMap = new HashMap<>();
        for (String s : Collections.list(filterConfig.getInitParameterNames())) {
            String v = filterConfig.getInitParameter(s);
            configMap.put(s, v);
        }

        SessionStore store = new MemSessionStore();
        store.init(configMap);
        filterConfig.getServletContext().setAttribute(SessionStore.class.getName(), store);
        filterConfig.getServletContext().setAttribute(TokenManagerFilter.class.getName(), this);
        List<HttpSessionSerializer> all = new ArrayList<>();
        all.add(new HttpSessionSerializerOAuth2());
        all.add(new HttpSessionSerializerXSession());
        all.add(new HttpSessionSerializerSession());
        for (HttpSessionSerializer httpSessionSerializer : all) {
            httpSessionSerializer.init(configMap);
            serializers.put(httpSessionSerializer.getType(), httpSessionSerializer);
        }
    }

    public SessionStore getSessionStore(ServletRequest request) {
        return (SessionStore) request.getServletContext().getAttribute(SessionStore.class.getName());
    }


    public HttpSessionId resolveSessionId() {
        HttpServletRequest request= VrWebHelper.getHttpServletRequest();
        if(request!=null){
            return resolveSessionId(request);
        }
        return null;
    }

    public void updateHeaders() throws IOException, ServletException {
        HttpServletRequest request= VrWebHelper.getHttpServletRequest();
        HttpServletResponse response = httpServletResponseThreadLocal.get();
        if(request!=null && response!=null){
            updateHeaders(request,response);
        }
    }

    public void updateHeaders(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSessionId httpSessionId = resolveSessionId(request);
        updateHeaders(resolveSessionId(request),request,response);
    }

    public void updateHeaders(HttpSessionId httpSessionId,HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (httpSessionId != null) {
            request.setAttribute(HttpSessionId.class.getName(), httpSessionId);
            HttpSessionSerializer ser = serializers.get(httpSessionId.getType());
            if(ser!=null) {
                ser.write((HttpServletResponse) response, httpSessionId);
            }
        }
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        httpServletResponseThreadLocal.set((HttpServletResponse) response);
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSessionId httpSessionId = resolveSessionId(httpRequest);
        if (httpSessionId != null) {
            request.setAttribute(HttpSessionId.class.getName(), httpSessionId);
            serializers.get(httpSessionId.getType()).write((HttpServletResponse) response,httpSessionId);
        }
        HttpServletResponse hresponse = (HttpServletResponse) response;
        String referer = ((HttpServletRequest) request).getHeader("referer");
        String origin=null;
        if(referer!=null){
            if(referer.toLowerCase().startsWith("http://")){
                int ii = referer.indexOf("/", "http://".length());
                origin=(ii<0?referer:referer.substring(0,ii));
            }else if(referer.toLowerCase().startsWith("https://")){
                int ii = referer.indexOf("/", "https://".length());
                origin=(ii<0?referer:referer.substring(0,ii));
            }
        }
        hresponse.addHeader("Access-Control-Allow-Origin", origin!=null?origin:"http://localhost:4200");
//        hresponse.addHeader("Access-Control-Allow-Origin", "*");
        hresponse.addHeader("Access-Control-Allow-Credentials", "true");
        hresponse.addHeader("Access-Control-Allow-Methods", "GET,POST");
        hresponse.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Cookie, X-JSESSIONID, X-RECEIVED-COOKIE");
        hresponse.addHeader("Access-Control-Expose-Headers", "Content-Length, Set-Cookie, Server, Date, X-JSESSIONID, X-RECEIVED-COOKIE");


        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }

        String finalIpAddress = ipAddress;
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public HttpSession getSession() {
                return getSession(true);
            }

            @Override
            public HttpSession getSession(boolean create) {
                if (!"GET".equals(getMethod()) && !"POST".equals(getMethod())) {
                    return super.getSession(create);
                }
                SessionStore store = getSessionStore(request);
                String requestedSID = getRequestedSessionId();

                String theID = requestedSID;
                if (httpSessionId != null && !httpSessionId.getSessionId().equals(requestedSID)) {
                    theID = httpSessionId.getSessionId();
                    PlatformSession s = store.get(httpSessionId.getSessionId());
                    if (s != null) {
                        return ((HttpPlatformSession) s).unwrap();
                    }
                }
                HttpSession s = super.getSession(create);
                if (s == null) {
                    return null;
                }
                PlatformSession old = store.get(s.getId());

                if (old != null) {
                    return ((HttpPlatformSession) old).unwrap();
                }
                ///wrap Session into adapter so that it can be invalidated correctly
                HttpSessionAdapter adaptedSession = new HttpSessionAdapter(s, store);
                theID = s.getId();
                store.put(theID, new HttpPlatformSession(adaptedSession, finalIpAddress));
                return adaptedSession;
            }
        };

        // reset Spring Web Thread Local vars.
        String REQUEST_ATTRIBUTES_ATTRIBUTE = RequestContextListener.class.getName() + ".REQUEST_ATTRIBUTES";
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestWrapper);
        request.setAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE, attributes);
        LocaleContextHolder.setLocale(request.getLocale());
        RequestContextHolder.setRequestAttributes(attributes);
        chain.doFilter(requestWrapper, response);
    }


    @Override
    public void destroy() {
        SessionStore store = (SessionStore) filterConfig.getServletContext().getAttribute(SessionStore.class.getName());
        if (store != null) {
            store.destroy();
        }
        for (HttpSessionSerializer s : serializers.values()) {
            s.destroy();
        }
    }


    private HttpSessionId resolveSessionId(HttpServletRequest request) {
        for (HttpSessionSerializer s : serializers.values()) {
            HttpSessionId sid = s.read(request);
            if (sid != null) {
                return sid;
            }
        }
        return null;
    }
}
