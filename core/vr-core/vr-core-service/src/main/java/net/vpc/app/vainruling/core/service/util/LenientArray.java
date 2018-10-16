/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.vpc.common.util.Convert;
import net.vpc.common.util.DoubleParserConfig;
import net.vpc.common.util.IntegerParserConfig;

/**
 *
 * @author vpc
 */
public class LenientArray {

    private static DoubleParserConfig LENIENT_1 = DoubleParserConfig.LENIENT.setNullValue(1.0).setInvalidValue(1.0);

    private Object[] value;

    public LenientArray(Object[] value) {
        this.value = value;
    }

    Object get(int x) {
        if (x >= 0 && x < value.length) {
            return value[x];
        }
        return null;
    }

    public static Date convertDate(Object d){
        if(d==null || d instanceof Date){
            return (Date)d;
        }
        String s = Convert.toString(d);
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        s = s.trim();
        for (String string : new String[]{"yyyy-MM-dd","yyyy/MM/dd","dd/MM/yyyy","dd-MM-yyyy"}) {
            SimpleDateFormat f = new SimpleDateFormat(string);
            try {
                Date dd=f.parse(s);
                return dd;
            } catch (Exception ex) {
                //
            }
        }
        return null;
    }
    public Date getDate(int x) {
        Object d = get(x);
        return convertDate(d);
    }

    public String getString(int x) {
        return Convert.toString(get(x));
    }

    public int getInt(int x) {
        Object o = get(x);
        if (o != null) {
            if (o instanceof String) {
                o = o.toString().replace(',', '.');
                if (o.toString().isEmpty()) {
                    o = "0";
                }
            }
        }
        return Convert.toInt(o, IntegerParserConfig.LENIENT);
    }

    public double getDouble(int x) {
        Object o = get(x);
        if (o != null) {
            if (o instanceof String) {
                o = o.toString().replace(',', '.');
                if (o.toString().isEmpty()) {
                    o = "0";
                }
            }
        }
        return Convert.toDouble(o, DoubleParserConfig.LENIENT);
    }

    public double getDoubleOr1(int x) {
        Object o = get(x);
        if (o != null) {
            if (o instanceof String) {
                o = o.toString().replace(',', '.');
                if (o.toString().isEmpty()) {
                    o = "0";
                }
            }
        }
        return Convert.toDouble(o, LENIENT_1);
    }

}
