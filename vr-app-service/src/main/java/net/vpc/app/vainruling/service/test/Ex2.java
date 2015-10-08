package net.vpc.app.vainruling.service.test;
import java.util.logging.Level;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.core.service.LoginService;
import net.vpc.app.vainruling.plugins.filesystem.service.VrFS;
import net.vpc.vfs.VirtualFileSystem;
public class Ex2 {
    public static void main(String[] args) {
        net.vpc.common.utils.LogUtils.configure(Level.FINE, "net.vpc");
        VrApp.runStandalone(args);
        VrApp.getBean(UserSession.class).setSessionId("custom");
        VrApp.getBean(LoginService.class).login("aref.meddeb", "aref1243");
        VrFS f=new VrFS();
        VirtualFileSystem vfs2 = f.subfs("/home/vpc/vfs");
        vfs2.mkdir("/test");
    }
}
