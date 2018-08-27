/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.callbacks;

import net.vpc.upa.*;
import net.vpc.upa.events.FieldEvent;
import net.vpc.upa.events.PersistenceUnitEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPreCreate;
import net.vpc.upa.config.OnUpdateFormula;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.HierarchyExtension;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class VRDefaultFieldsCallback {

    @OnPreCreate
    public void onPreCreateField(FieldEvent event) throws UPAException {
        Field f = event.getField();
        String name = f.getName();
        if (name.equals("deleted")
                || name.equals("deletedOn")
                || name.equals("deletedBy")
                || name.equals("archived")
                || name.equals("archivedOn")
                || name.equals("archivedBy")) {
            f.setPersistProtectionLevel(ProtectionLevel.PRIVATE);
            f.setUpdateProtectionLevel(ProtectionLevel.PROTECTED);
            f.setReadProtectionLevel(ProtectionLevel.PROTECTED);
        }
    }

    @OnUpdateFormula
    public void onUpdateFormulas(PersistenceUnitEvent event) {
        for (Entity entity : event.getPersistenceUnit().getEntities()) {
            boolean h=false;
            for (Relationship relationship : entity.getRelationships()) {
                if(relationship instanceof ManyToOneRelationship) {
                    HierarchyExtension hierarchyExtension = ((ManyToOneRelationship)relationship).getHierarchyExtension();
                    if (hierarchyExtension != null) {
                        h = true;
                        break;
                    }
                }
            }
            if(h){
                for (Object o : entity.createQueryBuilder().setLazyListLoadingEnabled(true).getResultList()) {
                    try {
                        entity.merge(o);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
