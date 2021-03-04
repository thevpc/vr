/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.converters;

import net.thevpc.common.time.DatePart;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import net.thevpc.common.time.TimeDuration;

/**
 * @author taha.bensalah@gmail.com
 */
@FacesConverter("periodToMinutesConverter")
public class PeriodToMinutesConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        if (value == null) {
            return "";
        }
        long b = ((Number) value).longValue();
        return TimeDuration.formatPeriodMilli(b, DatePart.MINUTE);
    }
}
