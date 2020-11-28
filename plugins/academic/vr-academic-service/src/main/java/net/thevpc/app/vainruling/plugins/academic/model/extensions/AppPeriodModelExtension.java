/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.extensions;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionTable;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.upa.DefaultFieldBuilder;
import net.thevpc.upa.MissingStrategy;
import net.thevpc.upa.Section;
import net.thevpc.upa.UserFieldModifier;
import net.thevpc.upa.events.EntityEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.OnPrePrepare;
import net.thevpc.upa.types.ManyToOneType;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AppPeriodModelExtension {
    @OnPrePrepare
    public void onPreInitEntity(EntityEvent event) {
        net.thevpc.upa.Entity entity = event.getEntity();
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
