/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web;

import javax.servlet.http.HttpServletRequest;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.common.jsf.FacesUtils;

/**
 *
 * @author vpc
 */
public class VRWebHelper {

    public static void prepareUserSession() {
        UserSession s = VrApp.getContext().getBean(UserSession.class);
        if (s.getSessionId() == null) {
            s.setSessionId(FacesUtils.getHttpSession(true).getId());
        }
        if (s.getLocale() == null) {
            s.setLocale(FacesUtils.getExternalContext().getRequestLocale());
        }
        if (s.getClientIpAddress() == null) {
            HttpServletRequest request = FacesUtils.getHttpRequest();
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            s.setClientIpAddress(ipAddress);
        }
    }
}
