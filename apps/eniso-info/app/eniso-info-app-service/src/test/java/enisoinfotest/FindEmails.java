package enisoinfotest;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.common.io.IOUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.time.Chronometer;
import net.thevpc.common.vfs.VFS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this is not a unit test
 */
public class FindEmails {
    public static void main(String[] args) {
        Chronometer ch = Chronometer.start();
        VrApp.runStandalone("test", "test");
        try {
            StringBuilder emails=new StringBuilder();
            List<String> notfound=new ArrayList<>();
            for (String name : IOUtils.toStringIterable(IOUtils.toInputStreamSource(new File("/home/vpc/emails.txt")),false)) {
                name=name.trim();
                if(!name.isEmpty()){
                    for (AppUser appUser : CorePlugin.get().findUsersByFullTitle(name)) {
                        if(!StringUtils.isBlank(appUser.getEmail())){
                            if(emails.length()>0){
                                emails.append(",");
                            }
                            emails.append(appUser.getEmail());
                        }else{
                            notfound.add(name);
                        }
                    }
                }
            }
            System.out.println(emails);
            System.out.println(notfound);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(ch.stop());
    }
}
