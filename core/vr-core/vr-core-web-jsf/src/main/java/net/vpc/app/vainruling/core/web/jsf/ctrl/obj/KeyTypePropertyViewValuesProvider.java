/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.jsf.ctrl.ObjCtrl;
import net.vpc.app.vainruling.core.web.obj.ViewContext;
import net.vpc.upa.*;
import net.vpc.upa.types.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
public class KeyTypePropertyViewValuesProvider implements PropertyViewValuesProvider {

    @Override
    public List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType datatype, ViewContext viewContext) {
        List<PropertyView> updatablePropertyViews = new ArrayList<>();
        List<PropertyView> dependentPropertyViews = new ArrayList<>();
        String componentId = "unknown";

        if(propertyView!=null) {
            updatablePropertyViews = propertyView.getUpdatablePropertyViews();
            dependentPropertyViews = propertyView.getDependentPropertyViews();
            componentId = propertyView.getComponentId();
        }
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        final ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
        Map<String, Object> currentValues = objCtrl.currentViewToMap();

        final Map<String, Object> constraints = new HashMap<>();
        for (PropertyView dependentPropertyView : dependentPropertyViews) {
            Object v = currentValues.get(dependentPropertyView.getComponentId());
            if (v != null) {
                ManyToOneTypePropertyView etpv = (ManyToOneTypePropertyView) dependentPropertyView;
                Entity me = etpv.getTargetEntity();
                Object mid = (v instanceof NamedId) ? ((NamedId) v).getId() : me.getBuilder().objectToId(v);
                String expr = etpv.getComponentId().substring(componentId.length() + 1);
                constraints.put(expr + "." + me.getIdFields().get(0).getName(), mid);
            }
        }
        final KeyType mtype = (KeyType) datatype;
        final Entity me = mtype.getEntity();
        return viewContext.getCacheItem("EntityPropertyViewValuesProvider." + me.getName() + ":" + constraints, new Action<List<NamedId>>() {
            @Override
            public List<NamedId> run() {
                return core.findAllNamedIds(mtype.getEntity().getName(), constraints, objCtrl.getModel().getCurrentDocument());
            }
        });
    }
}
