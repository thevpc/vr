/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.core.web.obj.ActionDialogResult;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.common.util.Convert;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicCoursePlan.class,
        actionLabel = "chrg", actionStyle = "fa-envelope-o",
        dialog = false,
        confirm = true
)
public class GenerateAssignmentsAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        VrApp.getBean(UpdateStudentClassActionCtrl.class).openDialog(itemIds);
    }

    @Override
    public boolean isEnabled(String actionId, Class entityType, EditCtrlMode mode, Object value) {
        return true;
    }

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        AcademicCourseType td = aca.findCourseType("TD");
        AcademicCourseType tp = aca.findCourseType("TP");
        AcademicCourseType pm = aca.findCourseType("PM");
        AcademicCourseType c = aca.findCourseType("C");
        AcademicCourseType cm = aca.findCourseType("CM");
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            AcademicCoursePlan coursePlan = aca.findCoursePlan(Convert.toInt(selectedIdStrings.get(i)));
            if(coursePlan!=null){
                List<AcademicCourseAssignment> courseAssignmentsByCoursePlan = aca.findCourseAssignmentsByCoursePlan(coursePlan.getId());
                if(courseAssignmentsByCoursePlan.isEmpty()){
                    if(coursePlan.getValueC()!=0 && coursePlan.getValueTD()!=0 && c!=null) {
                        AcademicCourseAssignment a = new AcademicCourseAssignment();
                        a.setCoursePlan(coursePlan);
                        a.setCourseType(c);
                        pu.persist(a);
                    }else if(coursePlan.getValueC()!=0 && cm!=null){
                        AcademicCourseAssignment a=new AcademicCourseAssignment();
                        a.setCoursePlan(coursePlan);
                        a.setCourseType(cm);
                        pu.persist(a);
                    }

                    if(coursePlan.getValueTP()!=0 && tp!=null){
                        AcademicCourseAssignment a=new AcademicCourseAssignment();
                        a.setCoursePlan(coursePlan);
                        a.setCourseType(tp);
                        pu.persist(a);
                    }

                    if(coursePlan.getValuePM()!=0 && pm!=null){
                        AcademicCourseAssignment a=new AcademicCourseAssignment();
                        a.setCoursePlan(coursePlan);
                        a.setCourseType(pm);
                        pu.persist(a);
                    }
                }
            }
        }
        return ActionDialogResult.RELOAD_CURRENT;
    }
}
