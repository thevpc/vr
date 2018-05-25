/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartmentPeriod;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicLoadConversionTable;
import net.vpc.upa.DefaultFieldBuilder;
import net.vpc.upa.MissingStrategy;
import net.vpc.upa.Section;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.callbacks.EntityEvent;
import net.vpc.upa.callbacks.PersistenceUnitEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPrePrepare;
import net.vpc.upa.config.OnPreUpdateFormula;
import net.vpc.upa.types.BooleanType;
import net.vpc.upa.types.ManyToOneType;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicPersistenceUnitCallback {

    @OnPrePrepare
    public void onPreInitEntity(EntityEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        String entityName = entity.getName();
        if (entityName.equals(AppDepartmentPeriod.class.getSimpleName())) {
            if (entity.findField("enableLoadEditing") == null) {
                Section tracking = entity.getSection("Load", MissingStrategy.CREATE);
                tracking.addField(new DefaultFieldBuilder().setName("enableLoadEditing").addModifier(UserFieldModifier.SUMMARY).setDefaultObject(true).setDataType(BooleanType.BOOLEAN));
            }
        }
        if (entityName.equals(AppPeriod.class.getSimpleName())) {
            if (entity.findField("loadConversionTable") == null) {
                Section tracking = entity.getSection("Load", MissingStrategy.CREATE);
                tracking.addField(new DefaultFieldBuilder().setName("loadConversionTable").addModifier(UserFieldModifier.SUMMARY)
                        .setDataType(new ManyToOneType(AcademicLoadConversionTable.class, true))
                );
            }
        }
    }

    @OnPreUpdateFormula
    public void onPreUpdateFormulas(PersistenceUnitEvent event) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        p.validateAcademicData(c.getCurrentPeriod().getId());
    }


}
