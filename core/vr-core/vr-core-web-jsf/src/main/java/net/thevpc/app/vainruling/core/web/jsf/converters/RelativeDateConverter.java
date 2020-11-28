/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.converters;

import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.core.web.jsf.Vr;

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
        return VrUtils.getRelativeDateMessage(dte, Vr.get().getLocale(null));
    }

}
