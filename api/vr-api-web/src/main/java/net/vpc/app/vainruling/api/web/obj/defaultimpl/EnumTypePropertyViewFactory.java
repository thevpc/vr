/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewFactory;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.app.vainruling.api.web.util.UPAObjectHelper;
import net.vpc.upa.Field;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EnumType;

/**
 *
 * @author vpc
 */
public class EnumTypePropertyViewFactory implements PropertyViewFactory {

    private static final Logger log = Logger.getLogger(EnumTypePropertyViewFactory.class.getName());

    @Override
    public PropertyView[] createPropertyView(String componentId, Field field, DataType datatype, PropertyViewManager manager) {
        DataType dt = field.getDataType();
        EnumType t = (EnumType) dt;
        boolean main = field.getModifiers().contains(FieldModifier.MAIN);
        boolean id = field.getModifiers().contains(FieldModifier.ID);
        boolean insert = field.getModifiers().contains(FieldModifier.PERSIST_DEFAULT);
        boolean update = !id && field.getModifiers().contains(FieldModifier.UPDATE_DEFAULT);
        boolean nullable = dt.isNullable();
        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
        boolean listMode = objCtrl.getModel().getMode() == EditCtrlMode.LIST;
        boolean insertMode = objCtrl.getModel().getMode() == EditCtrlMode.NEW;
        boolean updateMode = objCtrl.getModel().getMode() == EditCtrlMode.UPDATE;
        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.SELECT);
        PropertyView propView = new FieldPropertyView(componentId, field, controlType, manager);
        propView.setValues(manager.getPropertyViewValuesProvider(field, dt).resolveValues(propView, field, dt));
        List<SelectItem> items = new ArrayList<>();
        if (dt.isNullable()) {
            items.add(new SelectItem(null, "N/A"));
        }
        for (Object value : propView.getValues()) {
            items.add(new SelectItem(value, value.toString()));
        }
        propView.setItems(items);
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
        return new PropertyView[]{propView};
    }

}
