/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web.actions;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackModel;

import java.util.List;

/**
 * @author vpc
 */
@EntityAction(entityType = AcademicFeedbackModel.class,
        actionName = "GenerateFeedback",
        actionLabel = "Generate", actionStyle = "fa-envelope-o",
        dialog = true
)
public class GenerateFeedbackAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        GenerateFeedbackActionCtrl.Config c = new GenerateFeedbackActionCtrl.Config();
        c.setModelId(Integer.parseInt(itemIds.get(0)));
        VrApp.getBean(GenerateFeedbackActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return value != null;
    }

    @Override
    public void invoke(Class entityType, Object obj, Object[] args) {
        VrApp.getBean(AcademicPerfEvalPlugin.class).generateStudentsFeedbackForm(((AcademicFeedbackModel) obj).getId(),
                (String) args[0]);
    }

}
