/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.extensions;

import net.thevpc.upa.*;
import net.thevpc.upa.Entity;
import net.thevpc.upa.events.EntityEvent;
import net.thevpc.upa.config.*;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.exceptions.UPAException;
import net.thevpc.upa.types.DataTypeFactory;

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
