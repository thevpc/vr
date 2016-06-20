/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.service.test;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.common.gomail.GoMail;
import net.vpc.common.gomail.GoMailBodyPosition;
import net.vpc.upa.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

//import ;

/**
 * @author vpc
 */
public class I18nChecker {

    public static void main(String[] args) {

        VrAppTest.runStandalone();
        int count = checkI18n();
        System.out.println(count + " Missing resources!");
        UPA.getContext().invokePrivileged(new Action<Object>() {

            @Override
            public Object run() {
                AcademicFeedback f = new AcademicFeedback();
                UPA.getPersistenceUnit().updateFormulas();
                Record rr = UPA.getPersistenceUnit().findRecordById(AcademicCoursePlan.class, 121);
                System.out.println(rr.getObject("fullName"));
//                UPA.getPersistenceUnit().persist(f);
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
