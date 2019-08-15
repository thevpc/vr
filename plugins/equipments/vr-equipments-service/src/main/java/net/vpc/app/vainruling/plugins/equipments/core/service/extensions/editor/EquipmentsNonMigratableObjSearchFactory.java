/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.service.extensions.editor;

import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.core.service.editor.ForEntity;
import net.vpc.app.vainruling.core.service.editor.HashtagObjSearchFactory;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.vpc.upa.Document;
import net.vpc.upa.Entity;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@ForEntity("Equipment")
@Component
public class EquipmentsNonMigratableObjSearchFactory extends HashtagObjSearchFactory {

    public EquipmentsNonMigratableObjSearchFactory() {
        super("#nonmigratable", "non migratable");
    }

    @Override
    public List filterDocumentListByTag(List list, String name, Entity entity, String expression) {
        ArrayList<Document> filtered = new ArrayList<Document>();
        EquipmentPlugin eqp = EquipmentPlugin.get();
        for (Document doc : (List<Document>) list) {
            int eid = doc.getInt("id");
            if (!eqp.migrateEquipmentStatuses(eid, false)) {
                filtered.add(doc);
            }
        }
        return filtered;
    }

}
