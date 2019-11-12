/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.vpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.vpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.vpc.common.util.Convert;

import java.util.List;
import net.vpc.common.util.ListValueMap;
import net.vpc.app.vainruling.VrEditorActionInvoke;
import net.vpc.app.vainruling.VrEditorAction;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicCoursePlan",
        actionStyle = "fa-envelope-o",
        confirm = true
)
public class FixValidationCoursePlanAction implements VrEditorActionInvoke {

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        ListValueMap<String, AcademicCoursePlan> all = new ListValueMap<>();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            aca.academicCoursePlan_validationErrors_Formula_fix(Convert.toInt(selectedIdStrings.get(i)));
        }
        return new ActionDialogResult(ActionDialogResultPostProcess.RELOAD_CURRENT);
    }
}
