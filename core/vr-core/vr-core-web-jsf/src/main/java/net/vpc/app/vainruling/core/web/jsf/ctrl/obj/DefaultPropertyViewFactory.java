/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.core.web.obj.*;
import net.vpc.common.util.PlatformTypes;
import net.vpc.upa.Field;
import net.vpc.upa.KeyType;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.DateType;
import net.vpc.upa.types.EnumType;
import net.vpc.upa.types.ManyToOneType;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
public class DefaultPropertyViewFactory implements PropertyViewFactory {

    private static final Logger log = Logger.getLogger(DefaultPropertyViewFactory.class.getName());
    private ManyToOneTypePropertyViewFactory manyToOneTypePropertyViewFactory = new ManyToOneTypePropertyViewFactory();
    private EnumTypePropertyViewFactory enumTypePropertyViewFactory = new EnumTypePropertyViewFactory();

    @Override
    public PropertyView[] createPropertyView(String componentId, Field field, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext) {
        return createPropertyView(componentId, field, null, configuration, manager, viewContext);
    }

    public PropertyView[] createPropertyView(String componentId, DataType datatype, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext) {
        return createPropertyView(componentId, null, datatype, configuration, manager, viewContext);
    }

    public PropertyView[] createPropertyView(String componentId, Field field, DataType datatype, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext) {
        if (field != null) {
            datatype = field.getDataType();
        }
        PropertyView propView = null;
        FieldPropertyViewInfo nfo = FieldPropertyViewInfo.build(field, datatype, configuration);
        if (!nfo.visible) {
            return null;
        }
        if (nfo.dataType instanceof ManyToOneType || nfo.dataType instanceof KeyType) {
            return manyToOneTypePropertyViewFactory.createPropertyView(componentId, field, nfo.dataType, configuration, manager, viewContext);
        } else if (nfo.dataType instanceof EnumType) {
            return enumTypePropertyViewFactory.createPropertyView(componentId, field, nfo.dataType, configuration, manager, viewContext);
        } else {
            Class t = nfo.dataType.getPlatformType();
            if (t.equals(String.class)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.TEXT);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                if (nfo.main
                        || controlType.equals(UIConstants.Control.TEXTAREA)
                        || controlType.equals(UIConstants.Control.RICHTEXTAREA)
                        || controlType.equals(UIConstants.Control.WIKITEXTAREA)
                        ) {
                    propView.setPrependNewLine(true);
                    propView.setColspan(Integer.MAX_VALUE);
                    propView.setAppendNewLine(true);
                }
            } else if (PlatformTypes.isIntegerType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.INTEGER);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isLongType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.LONG);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isFloatType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.FLOAT);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isDoubleType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.DOUBLE);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isAnyIntegerType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.TEXT);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isAnyFloatType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.TEXT);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
            } else if (PlatformTypes.isDateType(t)) {
                if (nfo.dataType instanceof DateType) {
                    DateType ddt = (DateType) nfo.dataType;
                    if (ddt.isDateOnly()) {
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.DATE);
                        propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                    }
                    if (ddt.isTimeOnly()) {
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.TIME);
                        propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                    }
                    if (ddt.isTimestamp()) {
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.DATETIME);
                        propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                    }
                    if (ddt.isDateTime()) {
                        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.DATETIME);
                        propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                    }
                }
                if (propView == null) {
                    String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.DATETIME);
                    propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                }
            } else if (PlatformTypes.isBooleanType(t)) {
                String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.CHECKBOX);
                propView = new FieldPropertyView(componentId, field, nfo.dataType, controlType, manager);
                propView.setRequired(false);//no threestate
            } else {
                log.log(Level.SEVERE, "Unsupported type : no view found for " + t);
            }
        }
        if (propView != null) {
            if(nfo.id) {
                propView.setPrependNewLine(true);
                propView.setColspan(Integer.MAX_VALUE);
                propView.setAppendNewLine(true);
            }
            UPAObjectHelper.applyLayout(field, propView,viewContext);
            if (nfo.disabled) {
                propView.setDisabled(nfo.disabled);
            }
            return new PropertyView[]{propView};
        }
        return new PropertyView[0];
    }

}
