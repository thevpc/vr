/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj.defaultimpl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUPAUtils;
import net.vpc.app.vainruling.core.web.obj.ObjCtrl;
import net.vpc.app.vainruling.core.web.obj.PropertyViewManager;
import net.vpc.app.vainruling.core.web.obj.ViewContext;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.upa.*;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.ManyToOneType;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class ManyToOneTypePropertyView extends FieldPropertyView {

    private String actionCommand;
    private Entity entity;

    public ManyToOneTypePropertyView(String componentId, Field field, DataType dataType, String ctrlType, PropertyViewManager manager) {
        super(componentId, field, dataType, ctrlType, manager);
        DataType dt = dataType;
        if (dt == null) {
            dt = field.getDataType();
        }
        if (dt instanceof ManyToOneType) {
            ManyToOneType manyToOneType = (ManyToOneType) dt;
            Relationship relation = manyToOneType.getRelationship();
            entity = relation.getTargetEntity();
        } else if (dt instanceof KeyType) {
            entity = ((KeyType) dt).getEntity();
        }
        setActionCommand("{entity:\"" + entity.getName() + "\",id:\"${ID}\"}");
    }

    public Object getObjectValue() {
        if (value == null) {
            return null;
        }
        if (value instanceof NamedId) {
            Object id = ((NamedId) value).getId();
            id=VrUPAUtils.stringToId(Convert.toString(id),entity);
            return entity.getBuilder().idToObject(id);
        }
        if (value instanceof String) {
            return VrUPAUtils.stringToId(Convert.toString(this.value),entity);
//
//            String svalue = (String) this.value;
//            if (svalue.isEmpty()) {
//                return null;
//            }
//            Class idType = entity.getIdType();
//            if (idType.equals(Integer.class) || idType.equals(Integer.TYPE)) {
//                entity.getBuilder().idToObject(Convert.toInt(value));
//            } else if (idType.equals(Long.class) || idType.equals(Long.TYPE)) {
//                entity.getBuilder().idToObject(Convert.toInt(value));
//            } else {
//                throw new UnsupportedOperationException();
//            }
        }
        return value;
//        throw new UnsupportedOperationException();
//        return value;
    }

    public boolean isDisabledNavigation() {
        return !UPA.getPersistenceUnit().getSecurityManager().isAllowedNavigate(entity);
    }

    public String buildActionCommand() {
        ObjCtrl ctrl = VrApp.getBean(ObjCtrl.class);
        Object idVal = getSelectedItem();
        return getActionCommand().replace("${ID}", idVal == null ? "" : idVal.toString());
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public final void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    public Entity getTargetEntity() {
        Entity e = null;
        DataType dt = getDataType();
        if (dt == null) {
            dt = getField().getDataType();
        }
        if (dt instanceof ManyToOneType) {
            ManyToOneType manyToOneType = (ManyToOneType) dt;
            Relationship relation = manyToOneType.getRelationship();
            e = relation.getTargetEntity();
        } else if (dt instanceof KeyType) {
            e = ((KeyType) dt).getEntity();
        }
        return e;

//        return getEntityType().getRelationship().getTargetRole().getEntity();
    }

    public ManyToOneType getEntityType() {
        Field field = this.getField();
        DataType dt = field.getDataType();
        return (ManyToOneType) dt;
    }

    public void update(ViewContext viewContext) {
        DataType dt = getDataType();
        if (dt == null) {
            dt = getField().getDataType();
        }
        Entity me = getTargetEntity();
//        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
//        Map<String, Object> otherValues = objCtrl.currentViewToMap();
//        List<PropertyView> updatablePropertyViews = getUpdatablePropertyViews();
//        List<PropertyView> dependentPropertyViews = getDependentPropertyViews();
        this.setValues(getPropertyViewManager().getPropertyViewValuesProvider(getField(), dt).resolveValues(this, getField(), dt, viewContext));
        List<SelectItem> items = new ArrayList<>();
        if (dt.isNullable()) {
            items.add(new SelectItem(null, "N/A"));
        }
        for (NamedId val : this.getValues()) {
            items.add(new SelectItem(val.getId(), StringUtils.nonNull(val.getName())));
        }
        this.setItems(items);
    }

    public Entity getEntity() {
        return entity;
    }
}
