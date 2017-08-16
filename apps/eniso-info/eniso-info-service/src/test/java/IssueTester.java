
import java.lang.reflect.Field;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCVItem;
import net.vpc.upa.Entity;
import net.vpc.upa.NamedId;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseLevel;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgram;

/**
 * Created by vpc on 12/31/16.
 */
public class IssueTester {

    private static int xIsNotSoBig = 5;

    public static void main(String[] args) {
        xIsNotSoBig++;
        if (xIsNotSoBig > 4) {

        }
        List<AcademicCourseAssignment> list = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a "
                + " where a.coursePlan.periodId=11 "
                + " and a.id >=1009 and a.id <=1012 "
                + " order by a.id")
                //                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                .getResultList();
        System.out.println(list.size());
        for (int i = 0; i < list.size(); i++) {
            AcademicCourseAssignment courseAssignment = null;
            AcademicCoursePlan coursePlan = null;
            AcademicCourseLevel courseLevel = null;
            AcademicClass academicClass = null;
            AcademicProgram program = null;
            boolean pbm = false;
            try {
                courseAssignment = list.get(i);
                coursePlan = courseAssignment.getCoursePlan();
                courseLevel = coursePlan.getCourseLevel();
                academicClass = courseLevel.getAcademicClass();
                program = academicClass.getProgram();

                program.getDepartment();
            } catch (NullPointerException ex) {
                pbm = true;
            }
            System.out.println(i + " " + (pbm ? "ERR" : "   ") + ": [courseAssignment]=" + dbg(courseAssignment) + " ; [coursePlan]=" + dbg(coursePlan) 
                    + " ; [courseLevel]=" + dbg(courseLevel) + " ; [academicClass]=" + dbg(academicClass) + " ; [program]=" + dbg(program));

        }
        System.out.println("Bye");
        System.exit(0);
    }

    private static String dbg(Object o) {
        if(o==null){
            return "null";
        }
        try {
            Field declaredField = o.getClass().getDeclaredField("id");
            declaredField.setAccessible(true);
            return String.valueOf(declaredField.get(o)) + "@" + o;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void mainOld(String[] args) {
        VrApp.runStandalone();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Object af1 = pu.findById(AppPeriod.class, 11);
        Object af2 = pu.findById(AppDepartment.class, 1);
        List<AcademicCoursePlan> all = pu
                .createQuery("Select TOP 7000 o From AcademicCoursePlan o Where (period=:af1) and (IsHierarchyDescendant(:af2 , courseLevel.academicClass.program.department,AppDepartment)) Order By period.name Desc , fullName Asc , name Asc ")
                .setParameter("af1", af1)
                .setParameter("af2", af2)
                .getResultList();
        for (AcademicCoursePlan academicCoursePlan : all) {
            System.out.println(academicCoursePlan);
        }
        if (true) {
            return;
        }
        Entity e = pu.getEntity(AppContact.class);
        List<NamedId> typeList = e.createQuery("Select a.id id, a.fullTitle name from AppContact a").getResultList(NamedId.class);
        for (NamedId allNamedId : typeList) {
            System.out.println(allNamedId);
        }

        List<NamedId> allNamedIds = CorePlugin.get().findAllNamedIds(e, null, null);
        for (NamedId allNamedId : allNamedIds) {
            System.out.println(allNamedId);
        }
//        AcademicPerfEvalPlugin a=VrApp.getBean(AcademicPerfEvalPlugin.class);
//        List<AcademicFeedback> feedbacks = a.findFeedbacks(null, null, null, null, null, null, null, null, null, null, null);
//        for (AcademicFeedback feedback : feedbacks) {
//            System.out.println(feedback);
//        }
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        pu.findAll(AcademicTeacherCVItem.class).size();
//
//        List<AcademicClass> classesWithFeedbacks = a.findClassesWithFeedbacks(11, null, false, true);
//        for (AcademicClass classesWithFeedback : classesWithFeedbacks) {
//            System.out.println(classesWithFeedback);
//        }
    }
}
