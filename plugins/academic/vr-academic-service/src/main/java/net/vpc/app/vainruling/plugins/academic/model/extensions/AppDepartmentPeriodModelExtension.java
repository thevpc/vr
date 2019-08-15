/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.extensions;

import net.vpc.app.vainruling.core.service.model.AppDepartmentPeriod;
import net.vpc.upa.DefaultFieldBuilder;
import net.vpc.upa.MissingStrategy;
import net.vpc.upa.Section;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.events.EntityEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPrePrepare;
import net.vpc.upa.types.BooleanType;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AppDepartmentPeriodModelExtension {

    @OnPrePrepare
    public void onPreInitEntity(EntityEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        String entityName = entity.getName();
        if (entityName.equals(AppDepartmentPeriod.class.getSimpleName())) {
            if (entity.findField("enableLoadEditing") == null) {
                Section tracking = entity.getSection("Load", MissingStrategy.CREATE, 0);
                tracking.addField(new DefaultFieldBuilder().setName("enableLoadEditing").addModifier(UserFieldModifier.SUMMARY).setDefaultObject(true).setDataType(BooleanType.BOOLEAN));
            }
            if (entity.findField("enableLoadConfirmation") == null) {
                Section tracking = entity.getSection("Load", MissingStrategy.CREATE, 0);
                tracking.addField(new DefaultFieldBuilder().setName("enableLoadConfirmation").addModifier(UserFieldModifier.SUMMARY).setDefaultObject(true).setDataType(BooleanType.BOOLEAN));
            }
        }
    }

}
