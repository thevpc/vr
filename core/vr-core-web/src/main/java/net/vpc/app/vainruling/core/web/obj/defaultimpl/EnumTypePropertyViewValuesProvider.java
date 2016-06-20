/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj.defaultimpl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.web.obj.PropertyView;
import net.vpc.app.vainruling.core.web.obj.PropertyViewValuesProvider;
import net.vpc.app.vainruling.core.web.obj.ViewContext;
import net.vpc.upa.Field;
import net.vpc.upa.NamedId;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EnumType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class EnumTypePropertyViewValuesProvider implements PropertyViewValuesProvider {

    @Override
    public List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType dt, ViewContext viewContext) {
        EnumType t = (EnumType) dt;
        I18n i18n = VrApp.getBean(I18n.class);
        List<NamedId> list = new ArrayList<>();
        for (Object value : t.getValues()) {
            list.add(new NamedId(value, i18n.getEnum(value)));
        }
        return list;
    }

}
