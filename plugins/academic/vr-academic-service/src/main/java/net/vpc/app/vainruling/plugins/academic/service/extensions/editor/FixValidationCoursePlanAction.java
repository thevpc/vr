/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.vpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.vpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.vpc.app.vainruling.core.service.editor.EntityViewActionInvoke;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.editor.EntityAction;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.vpc.common.util.Convert;

import java.util.List;
import net.vpc.common.util.ListValueMap;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicCoursePlan.class,
        actionStyle = "fa-envelope-o",
        confirm = true
)
public class FixValidationCoursePlanAction implements EntityViewActionInvoke {

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        ListValueMap<String, AcademicCoursePlan> all = new ListValueMap<>();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            aca.academicCoursePlan_validationErrors_Formula_fix(Convert.toInt(selectedIdStrings.get(i)));
        }
        return new ActionDialogResult(ActionDialogResultPostProcess.RELOAD_CURRENT);
    }
}
