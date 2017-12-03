package net.vpc.app.vainruling.core.web.ws;

import net.vpc.app.vainruling.core.service.security.SessionStore;
import net.vpc.app.vainruling.core.service.security.SessionStoreProvider;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

@Service
public class HttpSessionStoreProvider implements SessionStoreProvider {
    @Override
    public SessionStore resolveSessionStore() {
        HttpServletRequest httpServletRequest = VrWebHelper.getHttpServletRequest();
        if(httpServletRequest==null){
            return null;
        }
        return (SessionStore) httpServletRequest.getServletContext().getAttribute(SessionStore.class.getName());
    }
}
