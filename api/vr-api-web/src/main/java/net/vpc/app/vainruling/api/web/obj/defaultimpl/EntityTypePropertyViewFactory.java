/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewFactory;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.app.vainruling.api.web.util.UPAObjectHelper;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EntityType;

/**
 *
 * @author vpc
 */
public class EntityTypePropertyViewFactory implements PropertyViewFactory {

    private static final Logger log = Logger.getLogger(EntityTypePropertyViewFactory.class.getName());

    @Override
    public PropertyView[] createPropertyView(String componentId, Field field, DataType datatype, PropertyViewManager manager) {
        DataType dt = field.getDataType();
        boolean main = field.getModifiers().contains(FieldModifier.MAIN);
        boolean id = field.getModifiers().contains(FieldModifier.ID);
        boolean insert = field.getModifiers().contains(FieldModifier.PERSIST_DEFAULT);
        boolean update = !id && field.getModifiers().contains(FieldModifier.UPDATE_DEFAULT);
        boolean nullable = dt.isNullable();
        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
        boolean listMode = objCtrl.getModel().getMode() == EditCtrlMode.LIST;
        boolean insertMode = objCtrl.getModel().getMode() == EditCtrlMode.NEW;
        boolean updateMode = objCtrl.getModel().getMode() == EditCtrlMode.UPDATE;
        EntityType t = (EntityType) dt;
        Entity me = t.getRelationship().getTargetRole().getEntity();
        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.ENTITY);
        final EntityTypePropertyView propView = new EntityTypePropertyView(componentId, field, controlType, manager);
        propView.update();

        if (insertMode) {
            if (!insert) {
                propView.setDisabled(true);
            }
        }
        if (updateMode) {
            if (!update) {
                propView.setDisabled(true);
            }
        }
        UPAObjectHelper.applyLayout(field, propView);
        List<PropertyView> all = new ArrayList<>();
        String ih = UPAObjectHelper.findStringProperty(me, UIConstants.ENTITY_ID_HIERARCHY, null, null);
        if (ih != null) {
            PropertyView[] r = manager.createPropertyView(me.getField(ih).getName(), me.getField(ih));
            if (r != null) {
                for (int i = 0; i < r.length; i++) {
                    PropertyView r0 = r[i];
                    if (r0 != null) {
                        r0.setName(field.getName() + " / " + r0.getName());
                        r0.setComponentId(propView.getComponentId() + "." + r0.getComponentId());
                        if (i == r.length - 1) {
                            r0.getUpdatablePropertyViews().add(propView);
                        }
                        all.add(r0);
                        if (i == (r.length - 1)) {
                            r0.setChangeListener(new ValueChangeListener() {

                                @Override
                                public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
                                    propView.update();
                                }
                            });
                        }
                    }
                }
            }
        }
        for (PropertyView a : all) {
            propView.getDependentPropertyViews().add(a);
        }
        all.add(propView);
        if (all.size() > 1) {
            if (propView.isPrependNewLine()) {
                propView.setPrependNewLine(false);
                all.get(0).setPrependNewLine(true);
            }
            if (propView.getPrependEmptyCells() > 0) {
                all.get(0).setPrependEmptyCells(propView.getPrependEmptyCells());
                propView.setPrependEmptyCells(0);
            }
        }
        return all.toArray(new PropertyView[all.size()]);

    }

}
