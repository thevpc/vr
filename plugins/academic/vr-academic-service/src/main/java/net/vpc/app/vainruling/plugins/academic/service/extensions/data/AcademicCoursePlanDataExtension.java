/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.extensions.data;

import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.upa.events.PersistEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPersist;
import net.vpc.upa.exceptions.UPAException;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicCoursePlanDataExtension {

    @OnPersist(name = "AcademicCoursePlan")
    public void onPersist(PersistEvent event) throws UPAException {
        Integer coursePlanId = (Integer) event.getPersistedId();
        AcademicPlugin.get().generateAssignments(coursePlanId);
    }

}
