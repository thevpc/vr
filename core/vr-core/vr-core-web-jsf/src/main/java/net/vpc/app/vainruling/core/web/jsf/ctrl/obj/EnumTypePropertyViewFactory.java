/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.core.web.jsf.VrJsf;
import net.vpc.app.vainruling.core.web.jsf.ctrl.EditorCtrl;
import net.vpc.app.vainruling.core.service.editor.ViewContext;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.Field;
import net.vpc.upa.types.DataType;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
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

        if (!nfo.visible) {
            return null;
        }
        if (nfo.submitOnChange) {
            propView.setSubmitOnChange(true);
            propView.setCtrlType(controlType+"_onchange");
            propView.setChangeListener(new ValueChangeListener() {
                @Override
                public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
                    if(event!=null){
                        propView.setSelectedItem(event.getNewValue());
                        EditorCtrl obj = VrApp.getBean(EditorCtrl.class);
                        obj.currentViewToModel();
                        obj.updatePropertyViews();
                    }

                }
            });
        }
        List<SelectItem> items = new ArrayList<>();
        if (nfo.dataType.isNullable()) {
            items.add(FacesUtils.createSelectItem(null, "N/A"));
        }
        items.addAll(VrJsf.toSelectItemList(propView.getValues()));
        propView.setItems(items);
        propView.setDisabled(nfo.disabled);
        UPAObjectHelper.applyLayout(field, propView,viewContext);
        return new PropertyView[]{propView};
    }

}
