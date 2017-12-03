package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.security.UserSessionConfigurator;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.core.web.Vr;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class WebUserSessionConfigurator implements UserSessionConfigurator {

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
            token.setTheme(Vr.get().getUserTheme(token.getUserLogin()).getId());
        } else {
            token.setTheme(Vr.get().getAppTheme().getId());
        }
    }
}
