/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.obj.*;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.common.util.Convert;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

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
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ListValueMap<String, AcademicCoursePlan> all = new ListValueMap<>();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            aca.academicCoursePlan_validationErrors_Formula_fix(Convert.toInt(selectedIdStrings.get(i)));
        }
        return new ActionDialogResult(ActionDialogResultPostProcess.RELOAD_CURRENT);
    }
}
