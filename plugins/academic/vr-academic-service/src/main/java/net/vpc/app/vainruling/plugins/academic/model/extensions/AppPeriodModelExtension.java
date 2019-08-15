/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.extensions;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionTable;
import net.vpc.upa.DefaultFieldBuilder;
import net.vpc.upa.MissingStrategy;
import net.vpc.upa.Section;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.events.EntityEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPrePrepare;
import net.vpc.upa.types.ManyToOneType;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AppPeriodModelExtension {
    @OnPrePrepare
    public void onPreInitEntity(EntityEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        String entityName = entity.getName();
        if (entityName.equals(AppPeriod.class.getSimpleName())) {
            if (entity.findField("loadConversionTable") == null) {
                Section tracking = entity.getSection("Load", MissingStrategy.CREATE, 0);
                tracking.addField(new DefaultFieldBuilder()
                        .setName("loadConversionTable")
                        .addModifier(UserFieldModifier.SUMMARY)
                        .setDataType(new ManyToOneType(AcademicLoadConversionTable.class, true))
                );
            }
        }
    }
}
