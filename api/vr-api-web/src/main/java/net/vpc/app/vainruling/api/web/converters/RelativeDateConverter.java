/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.converters;

import java.util.Date;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import net.vpc.app.vainruling.api.util.VrHelper;

/**
 *
 * @author vpc
 */
@FacesConverter(value = "RelativeDateConverter")
public class RelativeDateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        Date dte = (java.util.Date) o;
        return VrHelper.getRelativeDateMessage(dte);
    }

}
