/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewFactory;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.api.web.util.UPAObjectHelper;
import net.vpc.common.utils.PlatformTypes;
import net.vpc.upa.Field;
import net.vpc.upa.KeyType;
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
    public PropertyView[] createPropertyView(String componentId, Field field, DataType datatype, Map<String, Object> configuration, PropertyViewManager manager) {
        PropertyView propView = null;
        FieldPropertyViewInfo nfo = FieldPropertyViewInfo.build(field, datatype, configuration);
        if (!nfo.visible) {
            return null;
        }
        if (nfo.dataType instanceof EntityType || nfo.dataType instanceof KeyType) {
            return entityTypePropertyViewFactory.createPropertyView(componentId, field, nfo.dataType, configuration, manager);
        } else if (nfo.dataType instanceof EnumType) {
            return enumTypePropertyViewFactory.createPropertyView(componentId, field, nfo.dataType, configuration, manager);
        } else {
            Class t = nfo.dataType.getPlatformType();
            if (t.equals(String.class)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.TEXT);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                if (nfo.main || controlType.equals(UIConstants.ControlType.TEXTAREA) || controlType.equals(UIConstants.ControlType.RICHTEXTAREA)) {
                    propView.setPrependNewLine(true);
                    propView.setColspan(Integer.MAX_VALUE);
                    propView.setAppendNewLine(true);
                }
            } else if (PlatformTypes.isIntegerType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.INTEGER);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isLongType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.LONG);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isFloatType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.FLOAT);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isDoubleType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DOUBLE);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isAnyIntegerType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.TEXT);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isAnyFloatType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.TEXT);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isDateType(t)) {
                if (nfo.dataType instanceof DateType) {
                    DateType ddt = (DateType) nfo.dataType;
                    if (ddt.isDateOnly()) {
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DATE);
                        propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                    }
                    if (ddt.isTimeOnly()) {
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.TIME);
                        propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                    }
                    if (ddt.isTimestamp()) {
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DATETIME);
                        propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                    }
                    if (ddt.isDateTime()) {
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DATETIME);
                        propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                    }
                }
                if (propView == null) {
                    String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.DATE);
                    propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                }
            } else if (PlatformTypes.isBooleanType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.CHECKBOX);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                propView.setRequired(false);//no threestate
            } else {
                log.log(Level.SEVERE, "Unsupported type : no view found for " + t);
            }
        }
        if (propView != null) {
            UPAObjectHelper.applyLayout(field, propView);
            if (nfo.disabled) {
                propView.setDisabled(nfo.disabled);
            }
            return new PropertyView[]{propView};
        }
        return new PropertyView[0];
    }

}
