package net.vpc.app.vainruling.core.web.ws;

import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.core.service.security.UserTokenProvider;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class HttpUserTokenProvider implements UserTokenProvider {
    @Override
    public UserToken getToken() {
        HttpServletRequest r = VrWebHelper.getHttpServletRequest();
        if (r != null) {
            UserToken t = (UserToken) r.getAttribute(UserToken.class.getName());
            if (t != null) {
                return t;
            }
        }
        UserSession userSession = UserSession.get();
        if (userSession != null) {
            return userSession.getToken();
        }
        return null;
    }
}
