/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj.defaultimpl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.ObjManagerService;
import net.vpc.app.vainruling.core.web.obj.ObjCtrl;
import net.vpc.app.vainruling.core.web.obj.PropertyView;
import net.vpc.app.vainruling.core.web.obj.PropertyViewValuesProvider;
import net.vpc.app.vainruling.core.web.obj.ViewContext;
import net.vpc.upa.Action;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.NamedId;
import net.vpc.upa.types.DataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vpc
 */
public class EntityTypePropertyViewValuesProvider implements PropertyViewValuesProvider {

    @Override
    public List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType datatype, ViewContext viewContext) {
        EntityTypePropertyView ev = (EntityTypePropertyView) propertyView;
        List<PropertyView> updatablePropertyViews = propertyView.getUpdatablePropertyViews();
        List<PropertyView> dependentPropertyViews = propertyView.getDependentPropertyViews();

        final ObjManagerService objService = VrApp.getBean(ObjManagerService.class);
        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
        Map<String, Object> currentValues = objCtrl.currentViewToMap();
        final Map<String, Object> constraints = new HashMap<>();
        for (PropertyView dependentPropertyView : dependentPropertyViews) {
            Object v = currentValues.get(dependentPropertyView.getComponentId());
            if (v != null) {
                EntityTypePropertyView etpv = (EntityTypePropertyView) dependentPropertyView;
                Entity me = etpv.getMasterEntity();
                Object mid = (v instanceof NamedId) ? ((NamedId) v).getId() : me.getBuilder().objectToId(v);
                String expr = etpv.getComponentId().substring(propertyView.getComponentId().length() + 1);
                constraints.put(expr + "." + me.getPrimaryFields().get(0).getName(), mid);
            }
        }
        final Entity me = ev.getMasterEntity();
        return viewContext.getCacheItem("EntityTypePropertyViewValuesProvider." + me.getName() + ":" + constraints, new Action<List<NamedId>>() {
            @Override
            public List<NamedId> run() {
                return objService.findAllNamedIds(me.getName(), constraints);
            }
        });
    }
}
