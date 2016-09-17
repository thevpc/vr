package net.vpc.app.vainruling.core.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import net.vpc.upa.config.Path;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by vpc on 9/16/16.
 */
@Component
@javax.ws.rs.Path("/core")
public class CoreWS {
    @GET
    @Transactional
    @javax.ws.rs.Path("/login")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.TEXT_PLAIN)
    public boolean login(@QueryParam("login") String login, @QueryParam("password") String password) {
        try {
            VrWebHelper.prepareUserSession();
            UserSession userSession = UserSession.get();
            VrApp.getBean(CorePlugin.class).login(login, password);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GET
    @Transactional
    @javax.ws.rs.Path("/logout")
//    @Produces(MediaType.APPLICATION_JSON)
    public boolean logout() {
        try {
            VrApp.getBean(CorePlugin.class).logout();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
