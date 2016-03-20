/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityBuilder;
import net.vpc.upa.Field;
import net.vpc.upa.KeyType;
import net.vpc.upa.Relationship;
import net.vpc.upa.UPA;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EntityType;

/**
 *
 * @author vpc
 */
public class EntityTypePropertyView extends FieldPropertyView {

    private String actionCommand;
    private Entity entity;

    public EntityTypePropertyView(String componentId, Field field, DataType dataType, String ctrlType, PropertyViewManager manager) {
        super(componentId, field, dataType, ctrlType, manager);
        DataType dt = dataType;
        if (dt == null) {
            dt = field.getDataType();
        }
        if (dt instanceof EntityType) {
            EntityType entityType = (EntityType) dt;
            Relationship relation = entityType.getRelationship();
            entity = relation.getTargetEntity();
        } else if (dt instanceof KeyType) {
            entity = ((KeyType) dt).getEntity();
        }
        setActionCommand("{entity:\"" + entity.getName() + "\",id:\"${ID}\"}");
    }

    public boolean isDisabledNavigation() {
        return !UPA.getPersistenceUnit().getSecurityManager().isAllowedNavigate(entity);
    }

    public String buildActionCommand() {
        ObjCtrl ctrl = (ObjCtrl) VrApp.getBean(ObjCtrl.class);
        Object idVal = getSelectedItem();
        return getActionCommand().replace("${ID}", idVal == null ? "" : idVal.toString());
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public final void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    public Entity getMasterEntity() {
        Entity e = null;
        DataType dt = getDataType();
        if (dt == null) {
            dt = getField().getDataType();
        }
        if (dt instanceof EntityType) {
            EntityType entityType = (EntityType) dt;
            Relationship relation = entityType.getRelationship();
            e = relation.getTargetEntity();
        } else if (dt instanceof KeyType) {
            e = ((KeyType) dt).getEntity();
        }
        return e;

//        return getEntityType().getRelationship().getTargetRole().getEntity();
    }

    public EntityType getEntityType() {
        Field field = this.getField();
        DataType dt = field.getDataType();
        return (EntityType) dt;
    }

    public void update() {
        DataType dt = getDataType();
        if (dt == null) {
            dt = getField().getDataType();
        }
        Entity me = getMasterEntity();
//        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
//        Map<String, Object> otherValues = objCtrl.currentViewToMap();
//        List<PropertyView> updatablePropertyViews = getUpdatablePropertyViews();
//        List<PropertyView> dependentPropertyViews = getDependentPropertyViews();
        this.setValues(getPropertyViewManager().getPropertyViewValuesProvider(getField(), dt).resolveValues(this, getField(), dt));
        List<SelectItem> items = new ArrayList<>();
        if (dt.isNullable()) {
            items.add(new SelectItem(null, "N/A"));
        }
        EntityBuilder mec = me.getBuilder();
        for (Object value : this.getValues()) {
            items.add(new SelectItem(mec.entityToId(value), String.valueOf(mec.getMainValue(value))));
        }
        this.setItems(items);
    }
}
