/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.core.web.obj.ActionDialogResult;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackModel;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackSession;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicFeedbackSession.class,
        actionName = "GenerateFeedback",
        actionLabel = "Gen.", actionStyle = "fa-envelope-o",
        dialog = true
)
public class GenerateFeedbackAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        GenerateFeedbackActionCtrl.Config c = new GenerateFeedbackActionCtrl.Config();
        c.setSessionId(Integer.parseInt(itemIds.get(0)));
        VrApp.getBean(GenerateFeedbackActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(String actionId, Class entityType, EditCtrlMode mode, Object value) {
        return value != null;
    }

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {

        VrApp.getBean(AcademicPerfEvalPlugin.class).generateStudentsFeedbackForm(
                ((AcademicFeedbackModel) obj).getId(),
                Convert.toInt(args[1], IntegerParserConfig.LENIENT_F),
                (String) args[0]);
        return ActionDialogResult.VOID;
    }

}
