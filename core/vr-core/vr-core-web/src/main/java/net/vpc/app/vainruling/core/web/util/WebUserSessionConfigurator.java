package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.security.UserToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.vpc.app.vainruling.VrUserTokenConfigurator;

@Component
public class WebUserSessionConfigurator implements VrUserTokenConfigurator {

    @Override
    public void preConfigure(UserToken token) {
        HttpServletRequest req = VrWebHelper.getHttpServletRequest();
//        UserToken token=s.getToken();
        if (token.getSessionId() == null) {
            HttpSession session = req.getSession(true); // true == allow create
            token.setSessionId(session.getId());
        }
        if (token.getLocale() == null) {
            token.setLocale(req.getLocale().toString());
        }
        if (token.getIpAddress() == null) {
            String ipAddress = req.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = req.getRemoteAddr();
            }
            token.setIpAddress(ipAddress);
        }
    }

    @Override
    public void postConfigure(UserToken token) {
//        UserToken token=s==null?null:s.getToken();
        if (token != null && token.getUserLogin() != null) {
            token.setPublicTheme(VrWebHelper.getUserPublicTheme(token.getUserLogin()).getId());
            token.setPrivateTheme(VrWebHelper.getUserPrivateTheme(token.getUserLogin()).getId());
        } else {
            token.setPublicTheme(VrWebHelper.getAppPublicTheme().getId());
            token.setPrivateTheme(VrWebHelper.getAppPrivateTheme().getId());
        }
    }
}
