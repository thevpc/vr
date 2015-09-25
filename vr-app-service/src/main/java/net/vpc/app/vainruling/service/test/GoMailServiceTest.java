/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.service.test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.lib.gomail.GoMail;
import net.vpc.lib.gomail.GoMailDataSourceFactory;
import net.vpc.lib.gomail.GoMailFormat;
import net.vpc.lib.gomail.modules.GoMailModuleProcessor;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class GoMailServiceTest {

//    public static void main(String[] args) {
//        Expression t = new UQLParser(new StringReader("${if a=4 then 3 else 4 end}")).Any();
//        System.out.println(t);
//    }
    public static void main(String[] args) {
        VrApp.runStandalone(args);
        VrApp.getBean(GoMailServiceTest.class).run();
    }

    public void run() {
        try {
            MailboxPlugin s = VrApp.getBean(MailboxPlugin.class);
//            XMail m = s.read(XMailFormat.TEXT, new File("/home/vpc/Data/eniso/contacts/reprise-examens.xmail"));
//            String file="/home/vpc/Data/eniso/students/students-aid-fitr-karim.xmail";
            String file="/home/vpc/Data/eniso/students/students-pfe-soutenance-technique.xmail";
//            String file="/home/vpc/Data/eniso/teaching-load/2015-2016/emails/teachers-vr-problem.xmail";
//            String file="/home/vpc/Data/eniso/emails/companies-aid-fitr.xmail";
            GoMail m = s.read(GoMailFormat.TEXT, new File(file));
            m.setSimulate(false);
//            System.out.println(m);
            s.sendExternalMail(m);
        }
        catch (IOException ex) {
            Logger.getLogger(GoMailServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main0(String[] args) {
        try {
            GoMail m = new GoMail();

            m.from("taha.bensalah@gmail.com");
            m.setCredentials("taha.bensalah", "petitcanard77");
//            m.from("taha.bensalah@eniso.rnu.tn");
//            m.setCredentials("bashtaha", "tasan231");
//            m.from("taha.bensalah@laposte.net");
//            m.setCredentials("taha.bensalah@laposte.net", "canard77");

            m.to("${from}");
            m.to("${from}");
            m.cc("${from}");
            m.bcc("${from}");

            m.subject("test");
            m.body("<html><body>Hello world</body></html>", "text/html");
            m.footer(
                    "<p><strong>Taha Ben Salah</strong></p>\n"
                    + "<p>Directeur departmrnt Informatique Industrielle</p>\n"
                    + "<p>Ecole Nationale D'ingenieurs de Sousse (ENISo)</p>\n"
                    + "<img src=\"cid:part1\" alt=\"ATTACHMENT\"/>\n", "text/html");
            m.attachment(new File("/home/vpc/Data/eniso/visual-identity/eniso-ii-half.jpg").toURI().toURL(), null);
            m.repeatDatasource(GoMailDataSourceFactory.forPattern(new File("/home/vpc/var/xmail-ds.xlsx").toURI().toURL().toString()));
            m.setSimulate(true);
            MailboxPlugin service = new MailboxPlugin();
//            service.write(m, Format.TEXT, System.out);
            String t1 = service.gomailToString(m);
            System.out.println(t1);
            System.out.println("--------------------------------------");

//            XMail m2 = service.xmailFromString(t1);
//            System.out.println(m.equals(m2));
//            String t2 = service.xmailToString(m2);
//            System.out.println(t2);
//            System.out.println("--------------------------------------");
//            if (t1.equals(t2)) {
//                System.out.println("Super");
//            } else {
//                System.out.println("Why?");
//            }
            //XMailProcessor p = new XMailProcessor();
            service.sendExternalMail(m);
        }
        catch (Exception ex) {
            Logger.getLogger(GoMailModuleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
