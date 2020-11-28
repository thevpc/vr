/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.util.I18n;
import net.thevpc.app.vainruling.core.service.util.VrUPAUtils;
import net.thevpc.upa.Entity;
import net.thevpc.upa.Field;
import net.thevpc.upa.Relationship;
import net.thevpc.upa.types.DataType;

import java.util.Map;
import java.util.Objects;

/**
 * @author taha.bensalah@gmail.com
 */
public class FieldPropertyView extends PropertyView {

    public FieldPropertyView(String componentId, Field field, DataType dataType, String ctrlType, PropertyViewManager manager) {
        super(componentId,
                field == null ? VrApp.getBean(I18n.class).get(componentId)
                        : field.getTitle(), field, ctrlType, manager);
        setHeader(field == null ? VrApp.getBean(I18n.class).get(componentId)
                : field.getTitle());
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
            if (o != null && field.getEntity().isInstance(o)) {
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
                Object oo = sv.value == null ? null : field.getValue(sv.value);
                Relationship manyToOneRelationship = field.getManyToOneRelationship();
                if (manyToOneRelationship != null) {
                    sv = new SelectValue(oo, VrUPAUtils.objToJson(oo, field.getDataType()).toString(), manyToOneRelationship.getTargetEntity());
                } else {
                    sv = new SelectValue(oo, VrUPAUtils.objToJson(oo, field.getDataType()).toString(), null);
                }
            }
        }
        if (sv == null) {
            sv = new SelectValue(null, null, null);
        }
        boolean someUpdates = false;
        someUpdates |= !Objects.equals(this.value, sv.value);
        someUpdates |= !Objects.equals(this.selectedItem, sv.selectedItem);
        this.value = sv.value;
        this.selectedItem = sv.selectedItem;
        if (someUpdates) {
            onChange(null);
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

}
