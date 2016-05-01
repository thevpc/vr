/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.i18n.I18n;
import net.vpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
public class EnumConverter implements Converter {
    private Class enumClass;
    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if(StringUtils.isEmpty(value)){
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
