/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author taha.bensalah@gmail.com
 */
@FacesConverter(value = "TimestampConverter")
public class TimestampConverter implements Converter {

    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//02/20/2015 10:51:23

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        Object a = uic.getAttributes().get("pattern");
        try {
            if (string == null || string.length() == 0) {
                return null;
            }
            return new Timestamp(f.parse(string).getTime());
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return o == null ? "" : f.format((java.util.Date) o);
    }

}
