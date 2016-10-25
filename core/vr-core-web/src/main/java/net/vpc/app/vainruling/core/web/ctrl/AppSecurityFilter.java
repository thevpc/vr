/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.security.UserSession;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.faces.application.ViewExpiredException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@WebFilter(filterName = "AppSecurityFilter",
        urlPatterns = {"/*"})
public class AppSecurityFilter implements Filter {

    private static final Logger log = Logger.getLogger(AppSecurityFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(filterConfig.getServletContext());
//        this.webSSOTracer = ctx.getBean(WebSSOTracer.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String contextPath = ((HttpServletRequest) request).getContextPath();
        StringBuffer r = req.getRequestURL();
        boolean acceptRequest = true;
        if (r.toString().contains("/modules/")) {
            UserSession sso = null;
            try {
                sso = (UserSession) req.getSession().getAttribute("userSession");
            } catch (Exception e) {

            }
            if (sso == null || sso.getUser() == null) {
                acceptRequest = false;
//                //make sso authentification!
//                String ssotoken = request.getParameter("ssotoken");
//                if (ssotoken != null) {
//                    //process some specific sso here
//                    CoreService cs = (CoreService) req.getSession().getServletContext().getAttribute("coreService");
//                    if (cs != null) {
//                        try {
//                            cs.authentificateSSO(ssotoken);
//                        } catch (Exception e) {
//                            log.log(Level.SEVERE, "Unable to authentificateSSO as {0}", ssotoken);
//                        }
//                        //reload SSOManager
//                        try {
//                            sso = (SSOManager) req.getSession().getAttribute("SSOManager");
//                        } catch (Exception e) {
//                            //ignore
//                        }
//                        if (sso != null && sso.getPrincipal() != null) {
//                            acceptRequest = true;
//                        }
//                    }
//                }
            }
        }
        if (acceptRequest && r.toString().contains("/modules/")) {
//            WebSSOTracer s = webSSOTracer;

//            if (s != null && !s.isAllowed(r.toString())) {
//                acceptRequest = false;
//            }
        }
        if (acceptRequest) {
            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                HttpServletResponse webresponse = (HttpServletResponse) response;
                if (e instanceof ServletException) {
                    Throwable rootCause = ((ServletException) e).getRootCause();
                    if (rootCause instanceof ViewExpiredException) {
                        webresponse.sendRedirect(contextPath+"/r/index.xhtml?faces-redirect=true");
                        return;
                    }
                }
                if (e instanceof ViewExpiredException) {
                    webresponse.sendRedirect(contextPath+"/r/index.xhtml?faces-redirect=true");
                    return;
                }
                if(e.getClass().getName().endsWith(".ClientAbortException")){
                    //this is a tomcat 'Broken pipe' handling i suppose
                    log.log(Level.SEVERE, "ClientAbortException");
                    webresponse.sendRedirect(contextPath+"/r/index.xhtml?faces-redirect=true");
                }
                log.log(Level.SEVERE, "Unhandled Error", e);
                webresponse.sendRedirect(contextPath+"/r/index.xhtml?faces-redirect=true");
//                HttpServletResponse res = (HttpServletResponse) response;
//                res.sendError(500);
            }
        } else {
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendRedirect(req.getContextPath());
        }
    }

    @Override
    public void destroy() {

    }

}
