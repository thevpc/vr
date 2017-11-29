package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.security.UserSessionConfigurator;
import net.vpc.app.vainruling.core.web.Vr;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class WebUserSessionConfigurator implements UserSessionConfigurator {

    @Override
    public void preConfigure(UserSession s) {
        HttpServletRequest req = VrWebHelper.getHttpServletRequest();
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

    @Override
    public void postConfigure(UserSession s) {
        if (s != null && s.getUserLogin() != null) {
            s.setTheme(Vr.get().getUserTheme(s.getUserLogin()).getId());
        } else {
            s.setTheme(Vr.get().getAppTheme().getId());
        }
    }
}
