/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.service.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.i18n.I18n;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.core.service.LoginService;
import net.vpc.lib.gomail.GoMail;
import net.vpc.lib.gomail.GoMailBodyPosition;
import net.vpc.upa.Action;
//import ;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.Relationship;
import net.vpc.upa.UPA;
import net.vpc.upa.UPAObject;

/**
 *
 * @author vpc
 */
public class I18nChecker {

    public static void main(String[] args) {

        net.vpc.common.utils.LogUtils.configure(Level.FINE, "net.vpc");
        VrApp.runStandalone(args);
        VrApp.getBean(UserSession.class).setSessionId("custom");
        VrApp.getBean(LoginService.class).login(CorePlugin.USER_ADMIN, "admin");
        int count = checkI18n();
        System.out.println(count + " Missing resources!");
        UPA.getContext().invokePrivileged(new Action<Object>() {

            @Override
            public Object run() {
//                for (Object o : UPA.getPersistenceUnit().findAll(AcademicInternship.class)) {
//                    System.out.println(o);
//                }
//                VrApp.getBean(DevSrv.class).doUpgrade();
                return null;
            }
        }, null);
        //        final List<ArticlesItem> findArticlesByUserAndCategory = VrApp.getBean(ArticlesPlugin.class).findArticlesByUserAndCategory("admin", "Welcome");
        //        System.out.println(findArticlesByUserAndCategory);
        //        UPA.getContext().invokePrivileged(new Action<Object>() {
        //
        //            @Override
        //            public Object run() {
        //                AcademicTeacher academicTeacher = new AcademicTeacher();
        //                academicTeacher.setName(UUID.randomUUID().toString());
        //                VrApp.getBean(AcademicPlugin.class).add(academicTeacher);
        //                return null;
        //            }
        //        }, null);
        //        UPA.getContext().invokePrivileged(new Action<Object>() {
        //
        //            @Override
        //            public Object run() {
        //                UPA.getPersistenceUnit().updateFormulas();
        ////                for (AcademicCoursePlan c : VrApp.getBean(AcademicPlugin.class).findCoursePlans()) {
        ////                    if (c.getFullName() == null) {
        ////                        UPA.getPersistenceUnit().merge(c);
        ////                    }
        ////                }
        //                return null;
        //            }
        //        }, null);
        //        System.out.println("***************");
        //        List<AppUser> uu = VrApp.getBean(CorePlugin.class)
        //                .resolveUsersByProfileFilter("(taha.bensalah +admin) | 'ad+min'");
        //        System.out.println("***************");
        //        for (AppUser u : uu) {
        //            System.out.println("*************** "+u);
        //        }
    }

    public static void main0(String[] args) {
        try {
            GoMail m = new GoMail();
            m.setCredentials("eniso.info", "canard77");
            m.from("eniso.info@gmail.com");
            m.to("taha.bensalah@gmail.com");
            m.subject("Hi");
            m.body("This is my first example", "text/plain", GoMailBodyPosition.OBJECT);
            m.send();
        } catch (IOException ex) {
            Logger.getLogger(I18nChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int checkI18n() {
        int count = 0;
        for (net.vpc.upa.Package p : UPA.getPersistenceUnit().getPackages()) {
            count += checkUPAObject(p);
        }
        for (Entity entity : UPA.getPersistenceUnit().getEntities()) {
            count += checkUPAObject(entity);
            for (Field field : entity.getFields()) {
                if (field.isId() && field.getModifiers().contains(FieldModifier.PERSIST_SEQUENCE)) {
                    continue;
                }
                if (field.getModifiers().contains(FieldModifier.FOREIGN)) {
                    continue;
                }
                count += checkUPAObject(field);
            }
        }
        for (Relationship relationship : UPA.getPersistenceUnit().getRelationships()) {
            count += checkUPAObject(relationship);
            count += checkUPAObject(relationship.getSourceRole());
            count += checkUPAObject(relationship.getTargetRole());
        }
        return count;
    }

    private static int checkUPAObject(UPAObject o) {
        I18n i18n = VrApp.getBean(I18n.class);
        String s = i18n.get(o);
        if (s != null && (s.endsWith("!") || s.equals("Lien Manquant"))) {
            final String k = o.getI18NString().getKeys().get(0);
            if (!k.endsWith(".Target")) {
                System.err.println(k + "=");
            }
            return 1;
        }
        return 0;
    }
}
