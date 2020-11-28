/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.extensions.data;

import net.thevpc.upa.*;
import net.thevpc.upa.events.FieldEvent;
import net.thevpc.upa.events.PersistenceUnitEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.OnPreCreate;
import net.thevpc.upa.config.OnUpdateFormula;
import net.thevpc.upa.exceptions.UPAException;
import net.thevpc.upa.HierarchyExtension;
import net.thevpc.upa.exceptions.UpdateDocumentNotAllowedException;

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
            f.setPersistProtectionLevel(ProtectionLevel.PROTECTED);
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
                    }catch (UpdateDocumentNotAllowedException e){
                        throw e;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
