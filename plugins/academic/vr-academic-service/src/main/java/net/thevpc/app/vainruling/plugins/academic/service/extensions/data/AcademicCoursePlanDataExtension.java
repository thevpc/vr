/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions.data;

import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.upa.events.PersistEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.OnPersist;
import net.thevpc.upa.exceptions.UPAException;

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
