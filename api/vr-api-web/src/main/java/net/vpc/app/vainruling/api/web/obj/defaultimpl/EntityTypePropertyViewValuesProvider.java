/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.core.ObjManagerService;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewValuesProvider;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.types.DataType;

/**
 *
 * @author vpc
 */
public class EntityTypePropertyViewValuesProvider implements PropertyViewValuesProvider {

    @Override
    public List<Object> resolveValues(PropertyView propertyView, Field field, DataType datatype) {
        EntityTypePropertyView ev = (EntityTypePropertyView) propertyView;
        List<PropertyView> updatablePropertyViews = propertyView.getUpdatablePropertyViews();
        List<PropertyView> dependentPropertyViews = propertyView.getDependentPropertyViews();

        ObjManagerService objService = VrApp.getBean(ObjManagerService.class);
        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
        Map<String, Object> currentValues = objCtrl.currentViewToMap();
        Map<String, Object> constraints = new HashMap<>();
        for (PropertyView dependentPropertyView : dependentPropertyViews) {
            Object v = currentValues.get(dependentPropertyView.getComponentId());
            if (v != null) {
                EntityTypePropertyView etpv = (EntityTypePropertyView) dependentPropertyView;
                Entity me = etpv.getMasterEntity();
                Object mid = me.getBuilder().entityToId(v);
                String expr = etpv.getComponentId().substring(propertyView.getComponentId().length()+1);
                constraints.put(expr + "." + me.getPrimaryFields().get(0).getName(), mid);
            }
        }
        Entity me = ev.getMasterEntity();
        return objService.findAll(me.getName(), constraints);
    }
}
