package net.vpc.app.vr.plugins.academicprofile.service;

import net.vpc.app.vr.plugins.academicprofile.model.AcademicStudentCVItem;
import net.vpc.app.vr.plugins.academicprofile.model.AcademicTeacherCV;
import net.vpc.app.vr.plugins.academicprofile.model.AcademicTeacherCVItem;
import net.vpc.app.vr.plugins.academicprofile.model.AcademicCVSection;
import net.vpc.app.vr.plugins.academicprofile.model.AcademicStudentCV;
import java.util.List;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import net.vpc.app.vainruling.VrPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.upa.Document;
import net.vpc.upa.Entity;
import net.vpc.app.vainruling.VrInstall;
import net.vpc.app.vainruling.VrStart;

/**
 * Created by vpc on 7/19/17.
 */
@VrPlugin
public class AcademicProfilePlugin {

    @VrInstall
    public void installService() {
        //called if the plugin is installed : first install of new version
    }

    @VrStart
    public void startService() {
        findOrCreateAcademicCVSection("Course");
        findOrCreateAcademicCVSection("Experience");
        findOrCreateAcademicCVSection("Project");
        findOrCreateAcademicCVSection("Research");
        findOrCreateAcademicCVSection("Education");
    }

    public void updateViewsCounterForTeacherCV(int t) {
        AcademicTeacherCV cv = findOrCreateAcademicTeacherCV(t);
        if (cv != null) {
            cv.setViewsCounter(cv.getViewsCounter() + 1);
            UPA.getPersistenceUnit().merge(cv);
        }
    }

    public AcademicTeacherCV findOrCreateAcademicTeacherCV(final int teacherId) {
        return UPA.getContext().invokePrivileged(new Action<AcademicTeacherCV>() {

            @Override
            public AcademicTeacherCV run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                AcademicTeacherCV a = pu.createQuery("Select u from AcademicTeacherCV u where u.teacherId=:id")
                        .setParameter("id", teacherId).getFirstResultOrNull();
                if (a != null) {
                    return a;
                }
                //check teacher
                AcademicTeacher teacher = VrApp.getBean(AcademicPlugin.class).findTeacher(teacherId);
                if (teacher != null) {
                    final AcademicTeacherCV cv = new AcademicTeacherCV();
                    cv.setTeacher(teacher);
                    UPA.getPersistenceUnit().persist(cv);
                    return cv;
                }
                return null;
            }
        }, null);
    }

    public void updateViewsCounterFoStudentCV(int studentId) {
        CorePluginSecurity.requireUser(AcademicPlugin.get().findStudent(studentId).getUser().getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                AcademicStudentCV cv = findOrCreateAcademicStudentCV(studentId);
                if (cv != null) {
                    cv.setViewsCounter(cv.getViewsCounter() + 1);
                    UPA.getPersistenceUnit().merge(cv);
                }
            }
        });
    }

    public AcademicStudentCV getCurrentStudentCV() {
        AcademicStudent s = AcademicPlugin.get().getCurrentStudent();
        if(s!=null){
            return findOrCreateAcademicStudentCV(s.getId());
        }
        return null;
    }

    public AcademicTeacherCV getCurrentTeacherCV() {
        AcademicTeacher s = AcademicPlugin.get().getCurrentTeacher();
        if(s!=null){
            return findOrCreateAcademicTeacherCV(s.getId());
        }
        return null;
    }

    public AcademicStudentCV findOrCreateAcademicStudentCV(final int studentId) {
        AcademicPluginSecurity.requireStudentOrManager(studentId);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return UPA.getContext().invokePrivileged(new Action<AcademicStudentCV>() {

            @Override
            public AcademicStudentCV run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                AcademicStudentCV a = pu.createQuery("Select u from AcademicStudentCV u where u.studentId=:id")
                        .setParameter("id", studentId).getFirstResultOrNull();
                if (a != null) {
                    return a;
                }
                //check student
                AcademicStudent student = VrApp.getBean(AcademicPlugin.class).findStudent(studentId);
                if (student != null) {
                    final AcademicStudentCV cv = new AcademicStudentCV();
                    cv.setStudent(student);
                    UPA.getPersistenceUnit().persist(cv);
                    return cv;
                }
                return null;
            }
        }, null);
    }

//    public AcademicTeacherCVItem findTeacherCVItem(int teacherId) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        AcademicTeacherCVItem a = pu.createQuery("Select u from AcademicTeacherCVItem u where u.academicTeacherCVId=:id")
//                .setParameter("id", teacherId).getFirstResultOrNull();
//        if (a != null) {
//            return a;
//        }
//        return null;
//    }

    public List<AcademicTeacherCVItem> findTeacherCvItemsBySection(int teacherCVId, int sectionId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicTeacherCVItem> itemList = pu.createQuery("Select u from AcademicTeacherCVItem u where u.teacherCVId=:teacherCVId and u.sectionId=:sectionId")
                .setParameter("teacherCVId", teacherCVId)
                .setParameter("sectionId", sectionId)
                .getResultList();
//        List<AcademicTeacherCVItem> itemList = pu.findAll(AcademicTeacherCVItem.class);
        return itemList;
    }

    public List<AcademicTeacherCVItem> findTeacherCvItemsByTeacherAndSectionCode(int teacherId, int sectionCode) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicTeacherCVItem> itemList = pu
                .createQuery("Select u from AcademicTeacherCVItem u where u.teacherCV.teacherId=:teacherId and u.section.code=:sectionCode")
                .setParameter("teacherId", teacherId)
                .setParameter("sectionCode", sectionCode)
                .getResultList();
        return itemList;
    }

    public List<AcademicStudentCVItem> findStudentCvItemsByTeacherAndSectionCode(int studentId, int sectionCode) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicStudentCVItem> itemList = pu
                .createQuery("Select u from AcademicStudentCVItem u where u.studentCV.studentId=:studentId and u.section.code=:sectionCode")
                .setParameter("studentId", studentId)
                .setParameter("sectionCode", sectionCode)
                .getResultList();
        return itemList;
    }

    public void removeTeacherCvItem(int itemId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicTeacherCVItem item = pu.findById(AcademicTeacherCVItem.class, itemId);
        if (item != null) {
            AcademicPluginSecurity.requireTeacherOrManager(item.getTeacherCV().getTeacher().getId());
            pu.invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    pu.remove(item);
                }
            });
        }
    }

//    public void updateTeacherCVItem(AcademicTeacherCVItem item) {
//        AcademicPluginSecurity.requireTeacherOrManager(item.getTeacherCV().getTeacher().getId());
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        pu.invokePrivileged(new VoidAction() {
//            @Override
//            public void run() {
//                pu.merge(item);
//            }
//        });
//    }

    public void saveUserContact(AppUser user) {
        CorePluginSecurity.requireUser(user.getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                //only contact information is update!!
                Entity appContactEntity = pu.getEntity(AppContact.class);
                Entity u = pu.getEntity(AppUser.class);
                Document d = u.createDocument();
                d.setAll(u.getBuilder().objectToDocument(user));
                for (String fieldName : d.keySet().toArray(new String[d.size()])) {
                    if (appContactEntity.containsField(fieldName)) {
                        //okkay
                    } else {
                        d.remove(fieldName);
                    }
                }
                pu.merge(user);
            }
        });
    }

    public void saveTeacherCVItem(AcademicTeacherCVItem item) {
        AcademicPluginSecurity.requireTeacherOrManager(item.getTeacherCV().getTeacher().getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                pu.merge(item);
            }
        });
    }

    public void saveStudent(AcademicStudent student) {
        AcademicPluginSecurity.requireStudentOrManager(student.getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.invokePrivileged(new VoidAction() {
            @Override
            public void run() {

                PersistenceUnit pu = UPA.getPersistenceUnit();
                pu.merge(student);
            }
        });
    }

    public void saveTeacherCV(AcademicTeacherCV teacherCV) {
        AcademicPluginSecurity.requireTeacherOrManager(teacherCV.getTeacher().getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                pu.merge(teacherCV);
            }
        });
    }

    public void saveStudentCV(AcademicStudentCV studentCV) {
        AcademicPluginSecurity.requireStudentOrManager(studentCV.getStudent().getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                pu.merge(studentCV);
            }
        });
    }

    public void createAcademicTeacherCVItem(AcademicTeacherCVItem academicTeacherCVItem) {
        AcademicPluginSecurity.requireTeacherOrManager(academicTeacherCVItem.getTeacherCV().getTeacher().getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                pu.persist(academicTeacherCVItem);
            }
        });
    }

    public AcademicCVSection findAcademicCVSectionByCode(String code) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCVSection s = pu.createQuery("Select u from AcademicCVSection u where u.code=:code")
                .setParameter("code", code).getFirstResultOrNull();
        if(s==null){
            s = pu.createQuery("Select u from AcademicCVSection u where u.title=:code")
                    .setParameter("code", code).getFirstResultOrNull();
            if(s!=null){
                s.setCode(code);
                AcademicCVSection finalS = s;
                pu.invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        pu.persist(finalS);
                    }
                });
            }
        }
        return s;
    }

    public AcademicCVSection findOrCreateAcademicCVSection(String code) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCVSection s = pu.createQuery("Select u from AcademicCVSection u where u.code=:code")
                .setParameter("code", code).getFirstResultOrNull();
        if (s == null) {
            s = new AcademicCVSection();
            s.setCode(code);
            s.setTitle(code);
            pu.persist(s);
        }
        return s;
    }

}
