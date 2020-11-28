/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions.editor;


import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionInvoke;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicCourseAssignment",
        actionIcon = "divide",
        confirm = true
)
public class SplitGroupsAcademicCourseAssignmentAction implements VrEditorActionInvoke {

    @Autowired
    AcademicPlugin academic;

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return mode!=VrAccessMode.PERSIST;
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        for (String id : selectedIdStrings) {
            academic.splitGroupCourseAssignment(Integer.parseInt(id));
        }
        //do nothing!
        return new ActionDialogResult(ActionDialogResultPostProcess.RELOAD_ALL);
    }
}
