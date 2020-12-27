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
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.common.util.Collections2;
import net.thevpc.common.util.ListValueMap;
import net.thevpc.upa.Entity;
import net.thevpc.upa.EntityUsage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicCoursePlan",
        actionIcon = "copy",
        confirm = true
)
public class RemoveDuplicatesCoursePlanAction implements VrEditorActionInvoke {

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return true;
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ListValueMap<String, AcademicCoursePlan> all = Collections2.arrayListValueHashMap();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            AcademicCoursePlan coursePlan = aca.findCoursePlan(Convert.toInt(selectedIdStrings.get(i)));
            if (coursePlan != null) {
                all.add(coursePlan.getPeriod().getName() + "-" + coursePlan.getFullName(), coursePlan);
            }
        }
        for (String k : all.keySet()) {
            List<AcademicCoursePlan> values = all.getValues(k);
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
