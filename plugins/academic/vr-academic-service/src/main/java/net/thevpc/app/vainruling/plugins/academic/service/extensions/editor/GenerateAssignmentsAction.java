/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionInvoke;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.common.util.Convert;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicCoursePlan",
        actionIcon = "folder-plus",
        confirm = true
)
public class GenerateAssignmentsAction implements VrEditorActionInvoke {

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return true;
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            int cid = Convert.toInt(selectedIdStrings.get(i));
            aca.generateAssignments(cid);
        }
        return new ActionDialogResult(ActionDialogResultPostProcess.RELOAD_CURRENT);
    }
}
