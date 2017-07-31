import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCVItem;
import net.vpc.upa.Entity;
import net.vpc.upa.NamedId;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.List;

/**
 * Created by vpc on 12/31/16.
 */
public class IssueTester {
    public static void main(String[] args) {
        VrApp.runStandalone();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Object af1 = pu.findById(AppPeriod.class, 11);
        Object af2 = pu.findById(AppDepartment.class, 1);
        List<AcademicCoursePlan> all = pu
                .createQuery("Select TOP 7000 o From AcademicCoursePlan o Where (period=:af1) and (IsHierarchyDescendant(:af2 , courseLevel.academicClass.program.department,AppDepartment)) Order By period.name Desc , fullName Asc , name Asc ")
                .setParameter("af1",af1)
                .setParameter("af2",af2)
                .getResultList();
        for (AcademicCoursePlan academicCoursePlan : all) {
            System.out.println(academicCoursePlan);
        }
        if(true)return;
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
