/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewFactory;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.api.web.util.UPAObjectHelper;
import net.vpc.common.utils.PlatformTypes;
import net.vpc.upa.Field;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.DateType;
import net.vpc.upa.types.EntityType;
import net.vpc.upa.types.EnumType;

/**
 *
 * @author vpc
 */
public class DefaultPropertyViewFactory implements PropertyViewFactory {

    private static final Logger log = Logger.getLogger(DefaultPropertyViewFactory.class.getName());
    private EntityTypePropertyViewFactory entityTypePropertyViewFactory = new EntityTypePropertyViewFactory();
    private EnumTypePropertyViewFactory enumTypePropertyViewFactory = new EnumTypePropertyViewFactory();

    @Override
    public PropertyView[] createPropertyView(String componentId, Field field, DataType datatype, PropertyViewManager manager) {
        DataType dt = field.getDataType();
        PropertyView propView = null;
        boolean main = field.getModifiers().contains(FieldModifier.MAIN);
        boolean id = field.getModifiers().contains(FieldModifier.ID);
        boolean insert = field.getModifiers().contains(FieldModifier.PERSIST_DEFAULT);
        boolean insert_seq = field.getModifiers().contains(FieldModifier.PERSIST_SEQUENCE);
        boolean update = !id && field.getModifiers().contains(FieldModifier.UPDATE_DEFAULT);
        boolean nullable = dt.isNullable();
        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
        boolean listMode = objCtrl.getModel().getMode() == EditCtrlMode.LIST;
        boolean insertMode = objCtrl.getModel().getMode() == EditCtrlMode.NEW;
        boolean updateMode = objCtrl.getModel().getMode() == EditCtrlMode.UPDATE;
        boolean visible = 
                insertMode? UPAObjectHelper.findBooleanProperty(field, UIConstants.FIELD_FORM_VISIBLE_ON_CREATE, null, !insert_seq):
                updateMode? UPAObjectHelper.findBooleanProperty(field, UIConstants.FIELD_FORM_VISIBLE_ON_UPDATE, null, !insert_seq):
                true;
        if(!visible){
            return null;
        }
        if (dt instanceof EntityType) {
            return entityTypePropertyViewFactory.createPropertyView(componentId, field, datatype, manager);
        } else if (dt instanceof EnumType) {
            return enumTypePropertyViewFactory.createPropertyView(componentId, field, datatype, manager);
        } else {
            Class t = dt.getPlatformType();
            if (t.equals(String.class)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.TEXT);
                propView = new FieldPropertyView(componentId, field, controlType, manager);
                if (main || controlType.equals(UIConstants.ControlType.TEXTAREA) || controlType.equals(UIConstants.ControlType.RICHTEXTAREA)) {
                    propView.setPrependNewLine(true);
                    propView.setColspan(Integer.MAX_VALUE);
                    propView.setAppendNewLine(true);
                }
            } else if (PlatformTypes.isIntegerType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.INTEGER);
                propView = new FieldPropertyView(componentId, field, controlType, manager);
            } else if (PlatformTypes.isLongType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.LONG);
                propView = new FieldPropertyView(componentId, field, controlType, manager);
            } else if (PlatformTypes.isFloatType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.FLOAT);
                propView = new FieldPropertyView(componentId, field, controlType, manager);
            } else if (PlatformTypes.isDoubleType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DOUBLE);
                propView = new FieldPropertyView(componentId, field, controlType, manager);
            } else if (PlatformTypes.isAnyIntegerType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.TEXT);
                propView = new FieldPropertyView(componentId, field, controlType, manager);
            } else if (PlatformTypes.isAnyFloatType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.TEXT);
                propView = new FieldPropertyView(componentId, field, controlType, manager);
            } else if (PlatformTypes.isDateType(t)) {
                if(dt instanceof DateType){
                    DateType ddt=(DateType)dt;
                    if(ddt.isDateOnly()){
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DATE);
                        propView = new FieldPropertyView(componentId, field, controlType, manager);
                    }
                    if(ddt.isTimeOnly()){
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.TIME);
                        propView = new FieldPropertyView(componentId, field, controlType, manager);
                    }
                    if(ddt.isTimestamp()){
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DATETIME);
                        propView = new FieldPropertyView(componentId, field, controlType, manager);
                    }
                    if(ddt.isDateTime()){
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DATETIME);
                        propView = new FieldPropertyView(componentId, field, controlType, manager);
                    }
                }
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DATE);
                propView = new FieldPropertyView(componentId, field, controlType, manager);
            } else if (PlatformTypes.isBooleanType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.CHECKBOX);
                propView = new FieldPropertyView(componentId, field, controlType, manager);
                propView.setRequired(false);//no threestate
            } else {
                log.log(Level.SEVERE, "Unsupported type : no view found for " + t);
            }
        }
        if (propView != null) {
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
        return new PropertyView[0];
    }

}
