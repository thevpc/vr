/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.util.I18n;
import net.thevpc.app.vainruling.core.service.util.VrUPAUtils;
import net.thevpc.app.vainruling.core.service.editor.ViewContext;
import net.thevpc.upa.Field;
import net.thevpc.upa.NamedId;
import net.thevpc.upa.types.DataType;
import net.thevpc.upa.types.EnumType;

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
