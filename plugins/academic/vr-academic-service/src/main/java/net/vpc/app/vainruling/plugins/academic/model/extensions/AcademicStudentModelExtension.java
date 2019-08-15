/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.extensions;

import net.vpc.upa.*;
import net.vpc.upa.Entity;
import net.vpc.upa.events.EntityEvent;
import net.vpc.upa.config.*;
import net.vpc.upa.config.Callback;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.types.DataTypeFactory;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicStudentModelExtension {

    @OnPrepare(name = "AcademicStudent")
    public void onPrepareEntity(EntityEvent event) throws UPAException {
        Entity entity = event.getEntity();
        entity.addField(
                new DefaultFieldBuilder()
                        .setName("contactEmail")
                        .addModifier(UserFieldModifier.SUMMARY)
                        .setReadProtectionLevel(ProtectionLevel.PROTECTED)
                        .setDataType(DataTypeFactory.STRING)
                        .setPosition(3)
                        .setLiveSelectFormula("this.user.email")
                        .setIgnoreExisting(true)
        );
        entity.addField(
                new DefaultFieldBuilder()
                        .setName("contactPhone1")
                        .addModifier(UserFieldModifier.SUMMARY)
                        .setReadProtectionLevel(ProtectionLevel.PROTECTED)
                        .setDataType(DataTypeFactory.STRING)
                        .setPosition(4)
                        .setLiveSelectFormula("this.user.phone1")
                        .setIgnoreExisting(true)
        );
    }

}
