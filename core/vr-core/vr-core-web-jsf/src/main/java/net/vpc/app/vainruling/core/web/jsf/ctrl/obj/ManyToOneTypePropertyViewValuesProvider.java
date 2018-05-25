/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUPAUtils;
import net.vpc.app.vainruling.core.web.jsf.ctrl.ObjCtrl;
import net.vpc.app.vainruling.core.web.obj.ViewContext;
import net.vpc.upa.*;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.ManyToOneType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
public class ManyToOneTypePropertyViewValuesProvider implements PropertyViewValuesProvider {

    @Override
    public List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType datatype, ViewContext viewContext) {
        ManyToOneTypePropertyView ev = (ManyToOneTypePropertyView) propertyView;
        List<PropertyView> updatablePropertyViews = propertyView.getUpdatablePropertyViews();
        List<PropertyView> dependentPropertyViews = propertyView.getDependentPropertyViews();
//        PersistenceUnit pu = field.getEntity().getPersistenceUnit();

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
                String expr = etpv.getComponentId().substring(propertyView.getComponentId().length() + 1);
                constraints.put(expr + "." + me.getIdFields().get(0).getName(), mid);
            }
        }
        final Entity me = ev.getTargetEntity();
        final ManyToOneType mtype = (ManyToOneType) datatype;
        if (!(mtype.getRelationship() instanceof ManyToOneRelationship && ((ManyToOneRelationship)mtype.getRelationship()).getFilter() != null)) {
            List<NamedId> cacheItem = viewContext.getCacheItem("EntityPropertyViewValuesProvider." + me.getName() + ":" + constraints, new Action<List<NamedId>>() {
                @Override
                public List<NamedId> run() {
                    return core.findAllNamedIdsByRelationship(mtype.getRelationship().getName(), constraints, objCtrl.getModel().getCurrentDocument());
                }
            });
            List<NamedId> cacheItem2=new ArrayList<>(cacheItem.size());
            for (NamedId namedId : cacheItem) {
                Object id = namedId.getId();
                Object id2 = VrUPAUtils.objToJson(id, me.getDataType()).toString();
                cacheItem2.add(new NamedId(id2,namedId.getName()));
            }
            return cacheItem2;
        }
        return core.findAllNamedIdsByRelationship(mtype.getRelationship().getName(), constraints, objCtrl.getModel().getCurrentDocument());
    }
}
