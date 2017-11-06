package net.vpc.app.vainruling.core.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by vpc on 9/16/16.
 */
//@Component
//@javax.ws.rs.Path("/core")
@RestController
public class CoreWS {
//    @GET
//    @Transactional
//    @javax.ws.rs.Path("/login")
    @RequestMapping("/core/login")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.TEXT_PLAIN)
    public boolean login(@RequestParam("login") String login, @RequestParam("password") String password) {
        try {
            VrWebHelper.prepareUserSession();
            UserSession userSession = UserSession.get();
            AppUser u=VrApp.getBean(CorePlugin.class).login(login, password);
            if(u!=null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //return false;
        }
        return false;
    }

//    @GET
//    @Transactional
    @RequestMapping("/core/logout")
//    @Produces(MediaType.APPLICATION_JSON)
    public boolean logout() {
        try {
            VrApp.getBean(CorePlugin.class).logout();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @RequestMapping("/core/invoke")
    public String invoke(@RequestParam("commands") WSCommand[] commands) {
        System.out.println(commands == null ? null : Arrays.asList(commands));
        return "thanks";
    }
//    @GET
//    @Transactional
    @RequestMapping("/core/wscript")
    public Map<String,String> rsi(@RequestParam("script") String script) {
        WebScriptServiceInvoker e=new WebScriptServiceInvoker();
        return e.invoke(script);
    }
}
