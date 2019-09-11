/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.vpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
public class RowHelper {

    private String name;
    private Object[] values;

    public RowHelper(Object[] values, String name) {
        this.values = values;
        this.name = name;
    }

    public Object getObject(int col) {
        if (col >= 0 && col < values.length) {
            return values[col];
        } else {
            throw new IllegalArgumentException(name + " : Invalid column " + col);
        }
    }

    
    public String getString(int col) {
        return StringUtils.trimObject(getObject(col));
    }
    
    public int getInt(int col, int defaultValue) {
        try {
            Object o = getObject(col);
            if (o == null || (o instanceof String && o.toString().trim().isEmpty())) {
                return -1;
            }
            return Integer.parseInt(StringUtils.trimObject(o));
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage() + " . " + name + ", Column " + col, ex);
        }
    }

    public Date getDate(int col) {
        Object o = getObject(col);
        if (o == null) {
            return null;
        }
        if (o instanceof Date) {
            return (Date) o;
        }
        if (o instanceof String) {
            String s = o.toString().trim();
            if (s.length() > 0) {
                if (s.contains("/")) {
                    try {
                        return new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(s);
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e.getMessage() + " . " + name + " Colum " + col, e);
                    }
                } else if (s.contains("-")) {
                    try {
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(s);
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e.getMessage() + " . " + name + " Colum " + col, e);
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported format for date " + o + " . " + name + " Colum " + col);
                }
            } else {
                return null;
            }
        }
        throw new IllegalArgumentException("Unsupported format for date " + o + " . " + name + " Colum " + col);
    }
}
