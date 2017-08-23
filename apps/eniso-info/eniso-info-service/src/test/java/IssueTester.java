import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.upa.Entity;
import net.vpc.upa.NamedId;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.impl.UPAImplDefaults;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by vpc on 12/31/16.
 */
public class IssueTester {

    public static void main(String[] args) {
        UPAImplDefaults.PRODUCTION_MODE=false;
        List<Object> all = UPA.getPersistenceUnit()
                .createQuery("Select u from AcademicTeacher u where u.id in ("
                        + " (Select t.id from AcademicTeacher t "
                        + " inner join AcademicCourseAssignment a on a.teacherId=t.id "
                        + " where a.coursePlan.period.id=:periodId) "
                        + " union (Select t.id from AcademicTeacher t "
                        + " inner join AcademicCourseAssignment a on a.teacherId=t.id "
                        + " where a.coursePlan.period.id=:periodId) "
                        + ") order by u.contact.fullName")
                .setParameter("periodId",12)
                .getResultList();
        System.out.println("found = "+all);
    }
    public static void main1(String[] args) {
        List<AcademicCoursePlan> list = UPA.getPersistenceUnit().createQuery("Select a from AcademicCoursePlan a "
//                + " where a.id=159"
                        + " order by a.id"
        )
                //                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                .getResultList();
        System.out.println(list.size());
        int errCount = 0;
        for (int i = 0; i < list.size(); i++) {
            AcademicCoursePlan coursePlan = list.get(i);
            AcademicProgram p = coursePlan.resolveProgram();
            if (p == null) {
                AcademicCourseLevel courseLevel = coursePlan.getCourseLevel();
                AcademicClass academicClass = coursePlan.resolveAcademicClass();
                AcademicProgram program = coursePlan.resolveProgram();
                System.out.println(i + " " + ("ERR") + " ; [coursePlan]=" + dbg(coursePlan)
                        + " ; [courseLevel]=" + dbg(courseLevel) + " ; [academicClass]=" + dbg(academicClass) + " ; [program]=" + dbg(program));
                errCount++;
            }
//            AcademicCourseAssignment courseAssignment = null;
        }
        System.out.println("Bye " + errCount + "/" + list.size());
        System.exit(0);
    }

    private static int countErrors(List<AcademicCourseAssignment> list){
        int errCount = 0;
        System.out.println("========================================================================");
        for (int i = 0; i < list.size(); i++) {
            AcademicCourseAssignment courseAssignment = list.get(i);
            AcademicProgram p = courseAssignment.resolveProgram();
            AcademicCoursePlan coursePlan = courseAssignment.getCoursePlan();
            AcademicCourseLevel courseLevel = courseAssignment.resolveCourseLevel();
            AcademicClass academicClass = courseAssignment.resolveAcademicClass();
            AcademicProgram program = courseAssignment.resolveProgram();
            if (p == null) {
                System.out.println(i + " " + ("ERR") + ": [courseAssignment]=" + dbg(courseAssignment) + " ; [coursePlan]=" + dbg(coursePlan)
                        + " ; [courseLevel]=" + dbg(courseLevel) + " ; [academicClass]=" + dbg(academicClass) + " ; [program]=" + dbg(program));
                errCount++;
            }else{
                System.out.println(i + " " + ("   ") + ": [courseAssignment]=" + dbg(courseAssignment) + " ; [coursePlan]=" + dbg(coursePlan)
                        + " ; [courseLevel]=" + dbg(courseLevel) + " ; [academicClass]=" + dbg(academicClass) + " ; [program]=" + dbg(program));
            }
//            AcademicCourseAssignment courseAssignment = null;
        }
        return errCount;
    }

    public static void main2(String[] args) {
        List<AcademicCourseAssignment> old=null;
        for (int i = 173; i < 1000; i++) {
            int max = 159 + i;
            String condition = " a.id >= 159 and a.id<= "+ max;
            PersistenceUnit pu = UPA.getPersistenceUnit();
            List<AcademicCourseAssignment> list = pu.createQuery("Select a from AcademicCourseAssignment a "
                    + " where" + condition + " "
                    + " order by a.id"
            ).getResultList();
            int errCount = countErrors(list);
            if(errCount >0){
                System.out.println("["+max+"] Bye " + errCount + "/" + list.size());
                System.exit(0);
            }
            old=list;

        }
        System.out.println("Bye ");
        System.exit(0);
    }

    private static String dbg(Object o) {
        if (o == null) {
            return "null";
        }
        try {
            Field declaredField = o.getClass().getDeclaredField("id");
            declaredField.setAccessible(true);
            return  "@("+System.identityHashCode(o)+":"+String.valueOf(declaredField.get(o))+")" + o;
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
