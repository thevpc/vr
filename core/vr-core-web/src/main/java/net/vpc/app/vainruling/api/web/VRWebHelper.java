/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.common.jsf.FacesUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author vpc
 */
public class VRWebHelper {

    public static void prepareUserSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = attr.getRequest();
        UserSession s = VrApp.getContext().getBean(UserSession.class);
        if (s.getSessionId() == null) {
            HttpSession session = req.getSession(true); // true == allow create
            s.setSessionId(session.getId());
        }
        if (s.getLocale() == null) {
            s.setLocale(req.getLocale());
        }
        if (s.getClientIpAddress() == null) {
            String ipAddress = req.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = req.getRemoteAddr();
            }
            s.setClientIpAddress(ipAddress);
        }
    }
}
