/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web.actions;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.obj.EntityViewAction;
import net.vpc.app.vainruling.core.web.obj.ActionDialogResult;
import net.vpc.app.vainruling.core.web.obj.EntityViewActionDialog;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackModel;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackSession;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.upa.AccessMode;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicFeedbackSession.class,
        actionName = "GenerateFeedback",
        actionLabel = "Gen.", actionStyle = "fa-envelope-o"
)
public class GenerateFeedbackAction implements EntityViewActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        GenerateFeedbackActionCtrl.Config c = new GenerateFeedbackActionCtrl.Config();
        c.setSessionId(Convert.toInt(itemIds.get(0),IntegerParserConfig.LENIENT_F));
        VrApp.getBean(GenerateFeedbackActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return value != null;
    }

//    @Override
//    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
//
//        VrApp.getBean(AcademicPerfEvalPlugin.class).generateStudentsFeedbackForm(
//                ((AcademicFeedbackModel) obj).getId(),
//                Convert.toInt(args[1], IntegerParserConfig.LENIENT_F),
//                (String) args[0]);
//        return ActionDialogResult.VOID;
//    }

}
