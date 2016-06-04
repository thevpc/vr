/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.converters;

import java.text.DecimalFormat;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author vpc
 */
@FacesConverter("fileSizeConverter")
public class FileSizeConverter implements Converter {

    DecimalFormat f = new DecimalFormat("0.0");
    final int KO = 1024;
    final int MO = KO * KO;
    final int GO = KO * MO;
    final int TO = KO * GO;

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
        if (b < 0) {
            return "??" + b;
        }
        if (b == 0) {
            return "0";
        }
        if (b < KO) {
            return b + " b";
        }
        if (b < (MO)) {
            return f.format((((double) b) / KO)) + " Kb";
        }
        if (b < (GO)) {
            return f.format((((double) b) / MO)) + " Mb";
        }
        if (b < (TO)) {
            return f.format((((double) b) / GO)) + " Gb";
        }
        return f.format((((double) b) / TO)) + " Tb";
    }
}
