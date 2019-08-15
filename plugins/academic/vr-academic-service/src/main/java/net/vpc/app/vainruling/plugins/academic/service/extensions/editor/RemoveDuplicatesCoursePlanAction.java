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
import net.vpc.upa.AccessMode;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.List;
import net.vpc.common.util.ListValueMap;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityUsage;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicCoursePlan.class,
        actionStyle = "fa-envelope-o",
        confirm = true
)
public class RemoveDuplicatesCoursePlanAction implements EntityViewActionInvoke {

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return true;
    }

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ListValueMap<String, AcademicCoursePlan> all = new ListValueMap<>();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            AcademicCoursePlan coursePlan = aca.findCoursePlan(Convert.toInt(selectedIdStrings.get(i)));
            if (coursePlan != null) {
                all.put(coursePlan.getPeriod().getName() + "-" + coursePlan.getFullName(), coursePlan);
            }
        }
        for (String k : all.keySet()) {
            List<AcademicCoursePlan> values = all.get(k);
            if (values.size() > 1) {
                Entity acp = UPA.getPersistenceUnit().getEntity(AcademicCoursePlan.class);
                for (AcademicCoursePlan academicCoursePlan : values.toArray(new AcademicCoursePlan[values.size()])) {
                    List<EntityUsage> u = acp.findUsage(academicCoursePlan.getId());
                    if (u.isEmpty()) {
                        if (values.size() > 1) {
                            pu.remove(academicCoursePlan);
                            all.remove(k,academicCoursePlan);
                        }
                    }
                }
            }
        }
        return new ActionDialogResult(ActionDialogResultPostProcess.RELOAD_CURRENT);
    }
}
