/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import java.util.Map;
import jersey.repackaged.com.google.common.base.Objects;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.i18n.I18n;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.Record;
import net.vpc.upa.UPA;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EntityType;

/**
 *
 * @author vpc
 */
public class FieldPropertyView extends PropertyView {

    public FieldPropertyView(String componentId, Field field, String ctrlType, PropertyViewManager manager) {
        super(componentId, VrApp.getBean(I18n.class).get(field), field, ctrlType, manager);
        setHeader(VrApp.getBean(I18n.class).get(field));
        setRequired(!field.getDataType().isNullable());
        setDataType(field.getDataType());
    }

    public Field getField() {
        return (Field) getReferrer();
    }

    public void storeToMap(Map<String, Object> map) {
        if (getReferrer() != null) {
            map.put(getComponentId(), getValue());
        }
    }

    public void storeTo(Object o) {
        if (!getComponentId().contains(".")) {
            Field field = getField();
            final Object v2 = field.getDataType().convert(value);
            if (o != null && field.getEntity().getEntityType().isAssignableFrom(o.getClass())) {
                field.getEntity().getBuilder().entityToRecord(o).setObject(field.getName(), v2);
            }
        }
    }

    protected static class SelectValue {

        Object value;
        Object selectedItem;
        Entity entity;

        public SelectValue(Object value, Object selectedItem, Entity entity) {
            this.value = value;
            this.selectedItem = selectedItem;
            this.entity = entity;
        }

    }

    public void loadFrom(Object obj) {
        String expr = getComponentId();
        SelectValue sv = null;
        if (obj != null) {
            String[] exprArr = expr.split("\\.");
            sv = new SelectValue(obj, obj, UPA.getPersistenceUnit().getEntity(obj.getClass()));
            for (String n : exprArr) {
                Field field = sv.entity.getField(n);
                DataType dataType = field.getDataType();
                Record er = field.getEntity().getBuilder().entityToRecord(sv.value);
                Object oo = er==null?null:er.getObject(field.getName());
                if (dataType instanceof EntityType) {
                    EntityType et = (EntityType) dataType;
                    Entity e2 = et.getRelationship().getTargetRole().getEntity();
                    Object newSelectedItem = e2.getBuilder().entityToId(oo);
                    sv = new SelectValue(oo, newSelectedItem, e2);
                } else {
                    sv = new SelectValue(oo, oo, null);
                }
            }
        }
        if (sv == null) {
            sv = new SelectValue(null, null, null);
        }
        boolean someUpdates = false;
        someUpdates |= !Objects.equal(this.value, sv.value);
        someUpdates |= !Objects.equal(this.selectedItem, sv.selectedItem);
        this.value = sv.value;
        this.selectedItem = sv.selectedItem;
        if (someUpdates) {
            onChange(null);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getComponentId() + ')';
    }

}
