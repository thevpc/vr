/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.obj.EntityAction;

import net.vpc.app.vainruling.core.web.obj.ActionDialogResult;
import net.vpc.app.vainruling.core.web.obj.ActionDialogResultPostProcess;
import net.vpc.app.vainruling.core.web.obj.EntityViewActionInvoke;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.upa.AccessMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicCourseAssignment.class,
        actionLabel = "g/2", actionStyle = "fa-envelope-o",
        confirm = true
)
public class SplitGroupsAcademicCourseAssignmentAction implements EntityViewActionInvoke {

    @Autowired
    AcademicPlugin academic;

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return mode!=AccessMode.PERSIST;
    }

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        for (String id : selectedIdStrings) {
            academic.splitGroupCourseAssignment(Integer.parseInt(id));
        }
        //do nothing!
        return new ActionDialogResult(ActionDialogResultPostProcess.RELOAD_ALL);
    }
}
