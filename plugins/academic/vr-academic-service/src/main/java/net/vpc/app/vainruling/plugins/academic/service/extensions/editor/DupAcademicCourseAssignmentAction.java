/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.extensions.editor;


import net.vpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.upa.AccessMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import net.vpc.app.vainruling.VrEditorActionInvoke;
import net.vpc.app.vainruling.VrEditorAction;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicCourseAssignment",
        actionStyle = "fa-envelope-o",
        confirm = true
)
public class DupAcademicCourseAssignmentAction implements VrEditorActionInvoke {

    @Autowired
    AcademicPlugin academic;

    @Override
    public boolean isEnabled(String actionId, String entityType, AccessMode mode, Object value) {
        return mode!=AccessMode.PERSIST;
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        for (String id : selectedIdStrings) {
            academic.dupCourseAssignment(Integer.parseInt(id));
        }
        //do nothing!
        return ActionDialogResult.VOID;
    }
}
