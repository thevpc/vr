/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.core.service.editor.ViewContext;
import net.thevpc.upa.Entity;
import net.thevpc.upa.Field;
import net.thevpc.upa.KeyType;
import net.thevpc.upa.types.DataType;
import net.thevpc.upa.types.ManyToOneType;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
public class ManyToOneTypePropertyViewFactory implements PropertyViewFactory {

    private static final Logger log = Logger.getLogger(ManyToOneTypePropertyViewFactory.class.getName());

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
        FieldPropertyViewInfo nfo = FieldPropertyViewInfo.build(field, datatype, configuration);

//        EditorCtrl editorCtrl = VrApp.getBean(EditorCtrl.class);
//        DataType dataType = field.getDataType();
//        boolean main = field.getModifiers().contains(FieldModifier.MAIN);
//        boolean id = field.getModifiers().contains(FieldModifier.ID);
//        boolean insert = field.getModifiers().contains(FieldModifier.PERSIST_DEFAULT);
//        boolean update = !id && field.getModifiers().contains(FieldModifier.UPDATE_DEFAULT);
//        boolean nullable = dataType.isNullable();
//        boolean listMode = editorCtrl.getModel().getMode() == AccessMode.READ;
//        boolean insertMode = editorCtrl.getModel().getMode() == AccessMode.PERSIST;
//        boolean updateMode = editorCtrl.getModel().getMode() == AccessMode.UPDATE;
//        boolean forceDisabled = configuration != null && configuration.get("disabled") != null && (Boolean.TRUE.equals(configuration.get("disabled")) || "true".equalsIgnoreCase(String.valueOf(configuration.get("disabled"))));
//        boolean forceInvisible = configuration != null && configuration.get("invisible") != null && (Boolean.TRUE.equals(configuration.get("invisible")) || "true".equalsIgnoreCase(String.valueOf(configuration.get("invisible"))));
//        boolean visible
//                = insertMode
//                        ? UPAObjectHelper.findBooleanProperty(field, UIConstants.Form.VISIBLE_ON_CREATE, null, true)
//                        : updateMode ? UPAObjectHelper.findBooleanProperty(field, UIConstants.Form.VISIBLE_ON_UPDATE, null, true)
//                                : true;
        if (!nfo.visible) {
            return null;
        }
        Entity me = null;
        if (nfo.dataType instanceof ManyToOneType) {
            ManyToOneType t = (ManyToOneType) nfo.dataType;
            me = t.getRelationship().getTargetRole().getEntity();
        } else if (nfo.dataType instanceof KeyType) {
            KeyType t = (KeyType) nfo.dataType;
            me = t.getEntity();
        }
        String controlType = field == null ? UIConstants.Control.ENTITY : UPAObjectHelper.findStringProperty(field, UIConstants.Form.CONTROL, null, UIConstants.Control.ENTITY);
        final ManyToOneTypePropertyView propView = new ManyToOneTypePropertyView(componentId, field, nfo.dataType, controlType, manager);
        propView.update(viewContext);

        propView.setDisabled(nfo.disabled);
        UPAObjectHelper.applyLayout(field, propView,viewContext);
        List<PropertyView> all = new ArrayList<>();
        String ih = UPAObjectHelper.findStringProperty(me, UIConstants.ENTITY_ID_HIERARCHY, null, null);
        if (ih != null) {
            PropertyView[] r = manager.createPropertyViews(me.getField(ih).getName(), me.getField(ih), configuration, viewContext);
            if (r != null) {
                for (int i = 0; i < r.length; i++) {
                    PropertyView r0 = r[i];
                    if (r0 != null) {
                        r0.setName(field.getName() + " / " + r0.getName());
                        r0.setComponentId(propView.getComponentId() + "." + r0.getComponentId());
                        Object rootReferrer = propView.getRootReferrer();
                        if (rootReferrer == null && propView.getReferrer() instanceof Field) {
                            rootReferrer = ((Field) propView.getReferrer()).getEntity();
                        }
                        r0.setRootReferrer(rootReferrer);
                        if (nfo.disabled) {
                            r0.setDisabled(true);
                        }
                        if (i == r.length - 1) {
                            r0.getUpdatablePropertyViews().add(propView);
                        }
                        all.add(r0);
                        if (i == (r.length - 1)) {
                            if (r0.getCtrlType().equals(UIConstants.Control.ENTITY)) {
                                r0.setCtrlType(UIConstants.Control.ENTITY + "_onchange");
                            }
                            r0.setSubmitOnChange(true);
                            r0.setChangeListener(new ValueChangeListener() {

                                @Override
                                public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
                                    propView.update(new ViewContext());
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
