/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.extensions;

import net.thevpc.app.vainruling.core.service.model.AppDepartmentPeriod;
import net.thevpc.upa.DefaultFieldBuilder;
import net.thevpc.upa.MissingStrategy;
import net.thevpc.upa.Section;
import net.thevpc.upa.UserFieldModifier;
import net.thevpc.upa.events.EntityEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.OnPrePrepare;
import net.thevpc.upa.types.BooleanType;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AppDepartmentPeriodModelExtension {

    @OnPrePrepare
    public void onPreInitEntity(EntityEvent event) {
        net.thevpc.upa.Entity entity = event.getEntity();
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
