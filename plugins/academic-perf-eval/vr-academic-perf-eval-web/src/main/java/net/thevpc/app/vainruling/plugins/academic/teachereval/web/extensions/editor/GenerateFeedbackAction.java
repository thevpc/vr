/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.teachereval.web.extensions.editor;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionDialog;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.common.util.Convert;
import net.thevpc.common.util.IntegerParserConfig;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.app.vainruling.plugins.academic.teachereval.web.GenerateFeedbackActionCtrl;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicFeedbackSession",
        actionIcon = "folder-plus"
)
public class GenerateFeedbackAction implements VrEditorActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        if(itemIds.size()==0){
            return;
        }
        GenerateFeedbackActionCtrl.Config c = new GenerateFeedbackActionCtrl.Config();
        c.setSessionId(Convert.toInt(itemIds.get(0),IntegerParserConfig.LENIENT_F));
        VrApp.getBean(GenerateFeedbackActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
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
