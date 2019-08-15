/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUPAUtils;
import net.vpc.app.vainruling.core.web.jsf.ctrl.EditorCtrl;
import net.vpc.app.vainruling.core.service.editor.ViewContext;
import net.vpc.common.jsf.FacesUtils;
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
            if(id instanceof String){
                id=VrUPAUtils.jsonToObj((String) id,entity.getDataType());
            }
            return id;//entity.getBuilder().idToObject(id);
        }
        if (value instanceof String) {
            return VrUPAUtils.jsonToObj((String) this.value,getDataType());
        }
        return value;
    }

    public boolean isDisabledNavigation() {
        return !UPA.getPersistenceUnit().getSecurityManager().isAllowedNavigate(entity);
    }

    public String buildActionCommand() {
        EditorCtrl ctrl = VrApp.getBean(EditorCtrl.class);
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
//        EditorCtrl editorCtrl = VrApp.getBean(EditorCtrl.class);
//        Map<String, Object> editorCtrl = editorCtrl.currentViewToMap();
//        List<PropertyView> updatablePropertyViews = getUpdatablePropertyViews();
//        List<PropertyView> dependentPropertyViews = getDependentPropertyViews();
        this.setValues(getPropertyViewManager().getPropertyViewValuesProvider(getField(), dt).resolveValues(this, getField(), dt, viewContext));
        List<SelectItem> items = new ArrayList<>();
        if (dt.isNullable()) {
            items.add(FacesUtils.createSelectItem(null, "N/A"));
        }
        for (NamedId val : this.getValues()) {
            items.add(FacesUtils.createSelectItem(val.getStringId(), val.getStringName()));
        }
        this.setItems(items);
    }

    public Entity getEntity() {
        return entity;
    }
}
