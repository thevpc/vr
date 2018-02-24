package enisoinfotest;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.common.io.IOUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import net.vpc.common.vfs.VFS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this is not a unit test
 */
public class FindEmails {
    public static void main(String[] args) {
        Chronometer ch = new Chronometer();
        VrApp.runStandalone("taha.bensalah", "canard77");
        try {
            StringBuilder emails=new StringBuilder();
            List<String> notfound=new ArrayList<>();
            for (String name : IOUtils.toStringIterable(IOUtils.toInputStreamSource(new File("/home/vpc/emails.txt")),false)) {
                name=name.trim();
                if(!name.isEmpty()){
                    for (AppUser appUser : CorePlugin.get().findUsersByFullTitle(name)) {
                        if(!StringUtils.isEmpty(appUser.getContact().getEmail())){
                            if(emails.length()>0){
                                emails.append(",");
                            }
                            emails.append(appUser.getContact().getEmail());
                        }else{
                            notfound.add(name);
                        }
                    }
                }
            }
            System.out.println(emails);
            System.out.println(notfound);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(ch.stop());
    }
}
