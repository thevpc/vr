/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.extensions;

import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.*;
import net.thevpc.upa.Entity;
import net.thevpc.upa.events.EntityEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.*;
import net.thevpc.upa.exceptions.UPAException;
import net.thevpc.upa.types.DataTypeFactory;


/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicTeacherModelExtension {

    @OnPrepare
    public void OnPrepareEntity(EntityEvent event) throws UPAException {
        Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicTeacher.class)) {
            if (!entity.containsField("contactEmail")) {
                entity.addField(
                        new DefaultFieldBuilder().setName("contactEmail")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setDataType(DataTypeFactory.STRING)
                                .setProtectionLevel(ProtectionLevel.PROTECTED)
                                .setPosition(3)
                                .setLiveSelectFormula("this.user.email")
                );
            }
            if (!entity.containsField("contactPhone1")) {
                entity.addField(
                        new DefaultFieldBuilder()
                                .setName("contactPhone1")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setProtectionLevel(ProtectionLevel.PROTECTED)
                                .setDataType(DataTypeFactory.STRING)
                                .setPosition(4)
                                .setLiveSelectFormula("this.user.phone1")
                );
            }
        }
    }
}
