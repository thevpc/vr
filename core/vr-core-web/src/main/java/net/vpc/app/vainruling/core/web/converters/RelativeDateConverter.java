/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.converters;

import java.util.Date;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;

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
        UserSession s = null;
        try {
            s = VrApp.getBean(UserSession.class);
        } catch (Exception e) {
            //ignore error
        }
        return VrHelper.getRelativeDateMessage(dte, s == null ? null : s.getLocale());
    }

}
