/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.i18n.I18n;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewFactory;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.app.vainruling.api.web.util.UPAObjectHelper;
import net.vpc.upa.Field;
import net.vpc.upa.types.DataType;

/**
 *
 * @author vpc
 */
public class EnumTypePropertyViewFactory implements PropertyViewFactory {

    private static final Logger log = Logger.getLogger(EnumTypePropertyViewFactory.class.getName());

    @Override
    public PropertyView[] createPropertyView(String componentId, Field field, DataType datatype, Map<String, Object> configuration, PropertyViewManager manager) {
        FieldPropertyViewInfo nfo = FieldPropertyViewInfo.build(field, configuration);
        DataType dataType = field.getDataType();
        String controlType = UPAObjectHelper.findStringProperty(field, UIConstants.FIELD_FORM_CONTROL, null, UIConstants.ControlType.SELECT);
        PropertyView propView = new FieldPropertyView(componentId, field, controlType, manager);
        propView.setValues(manager.getPropertyViewValuesProvider(field, dataType).resolveValues(propView, field, dataType));
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
//                = insertMode ? UPAObjectHelper.findBooleanProperty(field, UIConstants.FIELD_FORM_VISIBLE_ON_CREATE, null, true)
//                        : updateMode ? UPAObjectHelper.findBooleanProperty(field, UIConstants.FIELD_FORM_VISIBLE_ON_UPDATE, null, true)
//                                : true;
        if (!nfo.visible) {
            return null;
        }
        List<SelectItem> items = new ArrayList<>();
        if (dataType.isNullable()) {
            items.add(new SelectItem(null, "N/A"));
        }
        I18n i18n = VrApp.getBean(I18n.class);

        for (Object value : propView.getValues()) {
            items.add(new SelectItem(value, i18n.getEnum(value)));
        }
        propView.setItems(items);
        propView.setDisabled(nfo.disabled);
        UPAObjectHelper.applyLayout(field, propView);
        return new PropertyView[]{propView};
    }

}
