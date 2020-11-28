/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.core.service.extensions.editor;

import java.util.ArrayList;
import java.util.List;

import net.thevpc.app.vainruling.VrEntityName;
import net.thevpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.thevpc.upa.Document;
import org.springframework.stereotype.Component;
import net.thevpc.app.vainruling.core.service.util.TextSearchFilter;
import net.thevpc.app.vainruling.core.service.editor.VrEditorSearchBase;

/**
 *
 * @author vpc
 */
@VrEntityName("Equipment")
@Component
public class EquipmentsNonMigratableEditorSearch extends VrEditorSearchBase {

    @Override
    public String getName() {
        return "#nonmigratable";
    }

    @Override
    public String getTitle() {
        return "Afficher les équipements non migrables";
    }

    @Override
    public List filterDocumentList(List<Document> list, String entityName, String expression) {
        ArrayList<Document> filtered = new ArrayList<Document>();
        EquipmentPlugin eqp = EquipmentPlugin.get();
        for (Document doc : (List<Document>) list) {
            int eid = doc.getInt("id");
            if (!eqp.migrateEquipmentStatuses(eid, false)) {
                filtered.add(doc);
            }
        }
        return TextSearchFilter.filterList(filtered, expression, entityName);
    }

}
