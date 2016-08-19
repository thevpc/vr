/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.converters;

import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.Date;

/**
 * @author taha.bensalah@gmail.com
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
        UserSession s = null;
        try {
            s = UserSession.get();
        } catch (Exception e) {
            //ignore error
        }
        return VrHelper.getRelativeDateMessage(dte, s == null ? null : s.getLocale());
    }

}
