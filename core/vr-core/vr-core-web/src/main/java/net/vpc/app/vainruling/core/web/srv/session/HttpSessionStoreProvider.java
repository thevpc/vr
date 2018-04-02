package net.vpc.app.vainruling.core.web.srv.session;

import javax.servlet.ServletContext;
import net.vpc.app.vainruling.core.service.security.SessionStore;
import net.vpc.app.vainruling.core.service.security.SessionStoreProvider;
import org.springframework.stereotype.Service;

import net.vpc.app.vainruling.core.service.VrApp;

@Service
public class HttpSessionStoreProvider implements SessionStoreProvider {
    @Override
    public SessionStore resolveSessionStore() {
        ServletContext scontext = VrApp.getBean(javax.servlet.ServletContext.class);
        return (SessionStore) scontext.getAttribute(SessionStore.class.getName());
    }
}
