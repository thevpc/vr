/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.upa.*;
import net.vpc.upa.Entity;
import net.vpc.upa.callbacks.EntityEvent;
import net.vpc.upa.config.*;
import net.vpc.upa.config.Callback;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.types.TypesFactory;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicStudentCallback {


    @OnPrepare
    public void onPrepareEntity(EntityEvent event) throws UPAException {
        Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicStudent.class)) {
            if(!entity.containsField("contactEmail")) {
                entity.addField(
                        new DefaultFieldBuilder()
                                .setName("contactEmail")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setReadProtectionLevel(ProtectionLevel.PROTECTED)
                                .setDataType(TypesFactory.STRING)
                                .setIndex(3)
                                .setLiveSelectFormula("this.contact.email")
                );
            }
            if(!entity.containsField("phone1")) {
                entity.addField(
                        new DefaultFieldBuilder()
                                .setName("contactPhone1")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setReadProtectionLevel(ProtectionLevel.PROTECTED)
                                .setDataType(TypesFactory.STRING)
                                .setIndex(4)
                                .setLiveSelectFormula("this.contact.phone1")
                );
            }
        }
    }


}
