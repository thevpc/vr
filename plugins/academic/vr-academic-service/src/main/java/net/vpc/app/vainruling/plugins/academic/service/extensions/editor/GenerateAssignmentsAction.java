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
import net.vpc.upa.AccessMode;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.List;
import net.vpc.app.vainruling.VrEditorActionInvoke;
import net.vpc.app.vainruling.VrEditorAction;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityType = AcademicCoursePlan.class,
        actionStyle = "fa-envelope-o",
        confirm = true
)
public class GenerateAssignmentsAction implements VrEditorActionInvoke {

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return true;
    }

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            int cid = Convert.toInt(selectedIdStrings.get(i));
            aca.generateAssignments(cid);
        }
        return new ActionDialogResult(ActionDialogResultPostProcess.RELOAD_CURRENT);
    }
}
