/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions.data;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.upa.events.PersistenceUnitEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.OnPreUpdateFormula;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicDataExtension {

    @OnPreUpdateFormula
    public void onPreUpdateFormulas(PersistenceUnitEvent event) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        p.validateAcademicData(c.getCurrentPeriod().getId());
    }

}
