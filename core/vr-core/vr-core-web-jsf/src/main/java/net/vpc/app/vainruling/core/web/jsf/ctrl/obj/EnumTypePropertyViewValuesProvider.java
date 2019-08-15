/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.service.util.VrUPAUtils;
import net.vpc.app.vainruling.core.service.editor.ViewContext;
import net.vpc.upa.Field;
import net.vpc.upa.NamedId;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EnumType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class EnumTypePropertyViewValuesProvider implements PropertyViewValuesProvider {

    @Override
    public List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType dt, ViewContext viewContext) {
        EnumType t = (EnumType) dt;
        I18n i18n = VrApp.getBean(I18n.class);
        List<NamedId> list = new ArrayList<>();
        for (Object value : t.getValues()) {
            list.add(new NamedId(VrUPAUtils.objToJson(value,dt).toString(), i18n.getEnum(value)));
        }
        return list;
    }

}
