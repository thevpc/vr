/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj.defaultimpl;

import jersey.repackaged.com.google.common.base.Objects;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.web.obj.PropertyView;
import net.vpc.app.vainruling.core.web.obj.PropertyViewManager;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.Record;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.ManyToOneType;

import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
public class FieldPropertyView extends PropertyView {

    public FieldPropertyView(String componentId, Field field, DataType dataType, String ctrlType, PropertyViewManager manager) {
        super(componentId,
                field == null ? VrApp.getBean(I18n.class).get(componentId)
                        : VrApp.getBean(I18n.class).get(field), field, ctrlType, manager);
        setHeader(field == null ? VrApp.getBean(I18n.class).get(componentId)
                : VrApp.getBean(I18n.class).get(field));
        setDataType(dataType != null ? dataType : field.getDataType());
        setRequired(getDataType() == null ? false : !getDataType().isNullable());
    }

    public Field getField() {
        return (Field) getReferrer();
    }

    public void storeToMap(Map<String, Object> map) {
        if (getReferrer() != null) {
            map.put(getComponentId(), getValue());
        }
    }

    public Object getObjectValue() {
        return value;
    }

    public void storeTo(Object o) {
        if (!isDisabled() && !getComponentId().contains(".")) {
            Field field = getField();
            final Object v2 = field.getDataType().convert(getObjectValue());
            if (o != null
                    && (o instanceof Record
                    || field.getEntity().getEntityType().isAssignableFrom(o.getClass()))) {
                field.getEntity().getBuilder().setProperty(o, field.getName(), v2);
            }
        }
    }

    public Entity getEntity() {
        return getField().getEntity();
    }

    public void loadFrom(Object obj) {
        String expr = getComponentId();
        SelectValue sv = null;
        if (obj != null) {
            String[] exprArr = expr.split("\\.");
            Object rootReferrer = getRootReferrer();
            Entity rr = null;
            if (rootReferrer instanceof Entity) {
                rr = (Entity) rootReferrer;
            } else {
                rr = getField().getEntity();
            }
            sv = new SelectValue(obj, obj, rr);
            for (String n : exprArr) {
                Field field = sv.entity.getField(n);
                DataType dataType = field.getDataType();
                Object oo = sv.value == null ? null : field.getValue(sv.value);
                if (dataType instanceof ManyToOneType) {
                    ManyToOneType et = (ManyToOneType) dataType;
                    Entity e2 = et.getRelationship().getTargetRole().getEntity();
                    Object newSelectedItem = e2.getBuilder().objectToId(oo);
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

}
