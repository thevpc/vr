/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj.defaultimpl;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.core.web.obj.*;
import net.vpc.upa.Field;
import net.vpc.upa.NamedId;
import net.vpc.upa.types.DataType;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
public class EnumTypePropertyViewFactory implements PropertyViewFactory {

    private static final Logger log = Logger.getLogger(EnumTypePropertyViewFactory.class.getName());

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
        FieldPropertyViewInfo nfo = FieldPropertyViewInfo.build(field, datatype, configuration);
        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.SELECT);
        PropertyView propView = new FieldPropertyView(componentId, field, datatype, controlType, manager);
        propView.setValues(manager.getPropertyViewValuesProvider(field, nfo.dataType).resolveValues(propView, field, nfo.dataType, viewContext));
//        EnumType t = (EnumType) dataType;
//        boolean main = field.getModifiers().contains(FieldModifier.MAIN);
//        boolean id = field.getModifiers().contains(FieldModifier.ID);
//        boolean insert = field.getModifiers().contains(FieldModifier.PERSIST_DEFAULT);
//        boolean update = !id && field.getModifiers().contains(FieldModifier.UPDATE_DEFAULT);
//        boolean nullable = dataType.isNullable();
//        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
//        boolean listMode = objCtrl.getModel().getMode() == EditCtrlMode.LIST;
//        boolean insertMode = objCtrl.getModel().getMode() == EditCtrlMode.NEW;
//        boolean updateMode = objCtrl.getModel().getMode() == EditCtrlMode.UPDATE;
//        boolean forceDisabled = configuration != null && (configuration.get("disabled") != null && Boolean.TRUE.equals(configuration.get("disabled")) || "true".equalsIgnoreCase(String.valueOf(configuration.get("disabled"))));
//        boolean forceInvisible = configuration != null && (configuration.get("invisible") != null && Boolean.TRUE.equals(configuration.get("invisible")) || "true".equalsIgnoreCase(String.valueOf(configuration.get("invisible"))));
//        boolean visible
//                = insertMode ? UPAObjectHelper.findBooleanProperty(field, UIConstants.Form.VISIBLE_ON_CREATE, null, true)
//                        : updateMode ? UPAObjectHelper.findBooleanProperty(field, UIConstants.Form.VISIBLE_ON_UPDATE, null, true)
//                                : true;
        if (!nfo.visible) {
            return null;
        }
        List<SelectItem> items = new ArrayList<>();
        if (nfo.dataType.isNullable()) {
            items.add(new SelectItem(null, "N/A"));
        }
        for (NamedId value : propView.getValues()) {
            items.add(new SelectItem(value.getId(), value.getName()));
        }
        propView.setItems(items);
        propView.setDisabled(nfo.disabled);
        UPAObjectHelper.applyLayout(field, propView);
        return new PropertyView[]{propView};
    }

}
