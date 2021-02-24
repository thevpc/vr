/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionInvoke;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.common.util.Convert;

import java.util.List;
import net.thevpc.common.collections.Collections2;
import net.thevpc.common.collections.ListValueMap;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicCoursePlan",
        actionIcon = "tools",
        confirm = true
)
public class FixValidationCoursePlanAction implements VrEditorActionInvoke {

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        ListValueMap<String, AcademicCoursePlan> all = Collections2.arrayListValueHashMap();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            aca.academicCoursePlan_validationErrors_Formula_fix(Convert.toInt(selectedIdStrings.get(i)));
        }
        return new ActionDialogResult(ActionDialogResultPostProcess.RELOAD_CURRENT);
    }
}
