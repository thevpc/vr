/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.srv;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.security.UserToken;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.faces.application.ViewExpiredException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.VrController;

/**
 * @author taha.bensalah@gmail.com
 */
//@WebFilter(filterName = "VrWebSecurityFilter",
//        urlPatterns = {"/*"})
public class VrWebSecurityFilter implements Filter {

    private static final Logger log = Logger.getLogger(VrWebSecurityFilter.class.getName());

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
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        boolean acceptRequest = true;
        CorePlugin sso = null;
        UserToken currentToken = null;
        boolean requireAuth = false;
        if (r.toString().contains("/modules/")) {
            requireAuth = true;
        } else if (servletPath != null) {
            if ((servletPath.equals("/ue") || servletPath.startsWith("/ue/"))) {
                //no need for auth cause it will be done after expanding the url!
                requireAuth = false;
            } else if ((servletPath.equals("/p") || servletPath.startsWith("/p/"))) {
                requireAuth = pathInfo == null;
//                if (pathInfo == null) {
//                    requireAuth = true;
//                } else if (pathInfo.startsWith("/news")) {
//                    requireAuth = false;
//                } else if (pathInfo.equals("/login") || pathInfo.equals("/loginCtrl")) {
//                    requireAuth = false;
//                } else {
//                    String ctrlName = pathInfo.substring(1);
//                    int intr = ctrlName.indexOf('?');
//                    if (intr > 0) {
//                        ctrlName = ctrlName.substring(0, intr);
//                    }
//                    Object b = null;
//                    try {
//                        b = VrApp.getBean(ctrlName);
//                    } catch (Exception ex) {
//                        //ignore;
//                    }
//                    if (b == null && !ctrlName.endsWith("Ctrl")) {
//                        try {
//                            b = VrApp.getBean(ctrlName + "Ctrl");
//                        } catch (Exception ex) {
//                            //ignore;
//                        }
//                    }
//                    if (b != null) {
//                        Class ctrlType = b.getClass();
//                        VrController vc = (VrController) ctrlType.getAnnotation(VrController.class);
//                        if (vc == null) {
//                            requireAuth = true;
//                        }else{
//                            String sc = vc.securityKey();
//                        }
//                    } else {
//                        requireAuth = true;
//                    }
//                }
            } else if (servletPath.startsWith("/r/modules/")) {
                requireAuth = true;
            } else if (servletPath.startsWith("/r/")) {
                requireAuth = false;
            } else if (servletPath.equals("/r")) {
                requireAuth = false;
            }
        }
        if (requireAuth) {
            try {
                sso = (CorePlugin) CorePlugin.get();
                currentToken = sso.getCurrentToken();
            } catch (Exception e) {

            }
            if (currentToken == null || currentToken.getUserId() == null) {
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
                if (e instanceof javax.el.ELException || e instanceof IllegalArgumentException) {
                    e.printStackTrace();
                }
                HttpServletResponse webresponse = (HttpServletResponse) response;
                if (e instanceof ServletException) {
                    Throwable rootCause = ((ServletException) e).getRootCause();
                    if (rootCause instanceof ViewExpiredException) {
                        if (!webresponse.isCommitted()) {
                            webresponse.sendRedirect(contextPath + "/r/index.xhtml?faces-redirect=true");
                        }
                        return;
                    }
                }
                if (e instanceof ViewExpiredException) {
                    if (!webresponse.isCommitted()) {
                        webresponse.sendRedirect(contextPath + "/r/index.xhtml?faces-redirect=true");
                    }
                    return;
                }
                if (e.getClass().getName().endsWith(".ClientAbortException")) {
                    //this is a tomcat 'Broken pipe' handling i suppose
                    log.log(Level.SEVERE, "ClientAbortException");
                    if (!webresponse.isCommitted()) {
                        webresponse.sendRedirect(contextPath + "/r/index.xhtml?faces-redirect=true");
                    }
                    return;
                }
                log.log(Level.SEVERE, "Unhandled Error", e);
                if (!webresponse.isCommitted()) {
                    webresponse.sendRedirect(contextPath + "/r/index.xhtml?faces-redirect=true");
                }
//                HttpServletResponse res = (HttpServletResponse) response;
//                res.sendError(500);
            }
        } else {
            if (sso != null) {
                String ri = req.getRequestURI();
                String qs = req.getQueryString();
                if (qs != null) {
                    ri = ri + "?" + qs;
                }
                UserSession currentSession = sso.getCurrentSession();
                currentSession.setPreConnexionURL(ri);
            }
            HttpServletResponse res = (HttpServletResponse) response;
            if (!res.isCommitted()) {
                res.sendRedirect(req.getContextPath() + "/p/loginCtrl");
            }
        }
    }

    @Override
    public void destroy() {

    }

}
