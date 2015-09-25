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
import net.vpc.upa.Field;
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

    public Field getField(){
        return (Field)getReferrer();
    }
    
    public void storeToMap(Map<String, Object> map) {
        if (getReferrer() != null) {
            map.put(getComponentId(), getValue());
        }
    }

    public void storeTo(Object o) {
        Field field = getField();
        final Object v2 = field.getDataType().convert(value);
        if (o != null && field != null && field.getEntity().getEntityType().isAssignableFrom(o.getClass())) {
            field.getEntity().getBuilder().entityToRecord(o).setObject(field.getName(), v2);
        }
    }

    public void loadFrom(Object o) {
        Field field = getField();
        boolean someUpdates = false;
        if (o != null && field != null && field.getEntity().getEntityType().isAssignableFrom(o.getClass())) {
            DataType dataType = field.getDataType();
            Object object = field.getEntity().getBuilder().entityToRecord(o).getObject(field.getName());
            if (dataType instanceof EntityType) {
                EntityType et = (EntityType) dataType;
                someUpdates |= !Objects.equal(this.value, object);
                this.value = object;
                Object newSelectedItem = et.getRelationship().getTargetRole().getEntity().getBuilder().entityToId(object);
                someUpdates |= !Objects.equal(this.selectedItem, newSelectedItem);
                this.selectedItem = newSelectedItem;
            } else {
                someUpdates |= !Objects.equal(this.value, object);
                this.value = object;
                someUpdates |= !Objects.equal(this.selectedItem, object);
                this.selectedItem = object;
            }
        }
        if (someUpdates) {
            onChange(null);
        }
    }
}
