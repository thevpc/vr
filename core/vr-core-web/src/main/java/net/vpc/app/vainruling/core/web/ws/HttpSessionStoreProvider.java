package net.vpc.app.vainruling.core.web.ws;

import net.vpc.app.vainruling.core.service.security.SessionStore;
import net.vpc.app.vainruling.core.service.security.SessionStoreProvider;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

@Service
public class HttpSessionStoreProvider implements SessionStoreProvider {
    @Override
    public SessionStore resolveSessionStore() {
        return (SessionStore) VrWebHelper.getHttpServletRequest().getServletContext().getAttribute(SessionStore.class.getName());
    }
}
