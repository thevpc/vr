/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import net.vpc.common.utils.Chronometer;

/**
 *
 * @author vpc
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
        return Chronometer.formatPeriod(b, Chronometer.DatePart.m);
    }
}
