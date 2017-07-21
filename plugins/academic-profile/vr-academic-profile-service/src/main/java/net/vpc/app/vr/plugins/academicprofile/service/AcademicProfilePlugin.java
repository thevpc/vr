package net.vpc.app.vr.plugins.academicprofile.service;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicStudentCV;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCV;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

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
                //check teacher
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
}
