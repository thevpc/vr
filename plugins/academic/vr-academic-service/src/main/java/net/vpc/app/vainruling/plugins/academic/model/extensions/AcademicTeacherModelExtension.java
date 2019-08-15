/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.extensions;

import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.upa.*;
import net.vpc.upa.Entity;
import net.vpc.upa.events.EntityEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.*;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.types.DataTypeFactory;


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
