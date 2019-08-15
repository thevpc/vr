/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.converters;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.common.strings.StringUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * @author taha.bensalah@gmail.com
 */
public class EnumConverter implements Converter {
    private Class enumClass;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Enum.valueOf(enumClass, value.trim());
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        return VrApp.getBean(I18n.class).getEnum(value);
        //...
    }
}
