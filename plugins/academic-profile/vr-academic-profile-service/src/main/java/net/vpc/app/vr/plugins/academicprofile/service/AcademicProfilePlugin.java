package net.vpc.app.vr.plugins.academicprofile.service;

import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicCVSection;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicStudentCV;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCV;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCVItem;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

/**
 * Created by vpc on 7/19/17.
 */
@AppPlugin
public class AcademicProfilePlugin {

    @Install
    public void installService() {
        //called if the plugin is installed : first install of new version
    }

    @Start
    public void startService() {
        //called at the startup of the server
    }

    public void customMethod(){

    }

    public void updateViewsCounterForTeacherCV(int t) {
        AcademicTeacherCV cv = findOrCreateAcademicTeacherCV(t);
        if (cv != null) {
            cv.setViewsCounter(cv.getViewsCounter() + 1);
            UPA.getPersistenceUnit().merge(cv);
        }
    }

    public AcademicTeacherCV findOrCreateAcademicTeacherCV(final int t) {
        return UPA.getContext().invokePrivileged(new Action<AcademicTeacherCV>() {

            @Override
            public AcademicTeacherCV run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                AcademicTeacherCV a = pu.createQuery("Select u from AcademicTeacherCV u where u.teacherId=:id")
                        .setParameter("id", t).getFirstResultOrNull();
                if (a != null) {
                    return a;
                }
                //check teacher
                AcademicTeacher teacher = VrApp.getBean(AcademicPlugin.class).findTeacher(t);
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

    public void updateViewsCounterFoStudentCV(int t) {
        AcademicStudentCV cv = findOrCreateAcademicStudentCV(t);
        if (cv != null) {
            cv.setViewsCounter(cv.getViewsCounter() + 1);
            UPA.getPersistenceUnit().merge(cv);
        }
    }

    public AcademicStudentCV findOrCreateAcademicStudentCV(final int t) {
        return UPA.getContext().invokePrivileged(new Action<AcademicStudentCV>() {

            @Override
            public AcademicStudentCV run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                AcademicStudentCV a = pu.createQuery("Select u from AcademicStudentCV u where u.studentId=:id")
                        .setParameter("id", t).getFirstResultOrNull();
                if (a != null) {
                    return a;
                }
                //check student
                AcademicStudent student = VrApp.getBean(AcademicPlugin.class).findStudent(t);
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

    public AcademicTeacherCVItem findTeacherCVItem(int t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicTeacherCVItem a = pu.createQuery("Select u from AcademicTeacherCVItem u where u.academicTeacherCVId=:id")
                .setParameter("id", t).getFirstResultOrNull();
        if (a != null) {
            return a;
        }
        return null;
    }
    
    public List<AcademicTeacherCVItem> findTeacherCvItemsBySection(int teacherCV, int sectionId){
        PersistenceUnit pu = UPA.getPersistenceUnit();
        /*List<AcademicTeacherCVItem> itemList = pu.createQuery("Select u from AcademicTeacherCVItem u where u.academicTeacherCVId=:id")
                .setParameter("id", teacherCV)
                .getResultList();*/
        List<AcademicTeacherCVItem> itemList = pu.findAll(AcademicTeacherCVItem.class);
        return itemList;
    } 
    
    public void removeTeacherCvItem(int itemId){
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicTeacherCVItem item = pu.findById(AcademicTeacherCVItem.class, itemId);
        //AcademicTeacherCVItem item = findTeacherCVItem(itemId);
        if(item != null){
            pu.remove(item);
        }
    }
    
    public void updateTeacherCVItem(AcademicTeacherCVItem item) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.merge(item);
    }
  

    public void updateContactInformations(AppContact contact) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.merge(contact);
    }
    
    public void updateStudentInformations(AcademicStudent student) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.merge(student);
    }

    public void updateTeacherCVInformations(AcademicTeacherCV teacherCV) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.merge(teacherCV);
    }

    public void updateStudentCVInformations(AcademicStudentCV studentCV) {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                pu.merge(studentCV);
            }
        });
    }

    public void createAcademicTeacherCVItem(AcademicTeacherCVItem academicTeacherCVItem) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.persist(academicTeacherCVItem);
    }
    
    public AcademicCVSection findAcademicCVSectionByName(String title){
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCVSection s = pu.createQuery("Select u from AcademicCVSection u where u.title=:title")
                        .setParameter("title", title).getFirstResultOrNull();
        return s;
    } 

}
