/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.util;

import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.common.utils.Convert;
import net.vpc.common.utils.IntegerParserConfig;
import net.vpc.upa.Properties;
import net.vpc.upa.UPAObject;

/**
 *
 * @author vpc
 */
public class UPAObjectHelper {

    public static Object findObjectProperty(UPAObject f, String property, String context, Object defaultValue) {
        if(f==null){
            return defaultValue;
        }
        Properties p = f.getProperties();
        if (context != null && context.length() > 0 && p.containsKey(property + "@" + context)) {
            return p.getObject(property + "@" + context);
        }
        if (p.containsKey(property)) {
            return p.getObject(property);
        }
        return defaultValue;
    }

    public static boolean findBooleanProperty(UPAObject f, String property, String context, boolean defaultValue) {
        return Convert.toBoolean(findObjectProperty(f, property, context, defaultValue));
    }

    public static int findIntProperty(UPAObject f, String property, String context, int defaultValue) {
        return Convert.toInteger(findObjectProperty(f, property, context, defaultValue));
    }

    public static String findStringProperty(UPAObject f, String property, String context, String defaultValue) {
        return Convert.toString(findObjectProperty(f, property, context, defaultValue));
    }

    public static void applyLayout(UPAObject f, PropertyView pv) {
        String newline = findStringProperty(f, UIConstants.FIELD_FORM_NEWLINE, null, "");
        for (String s : newline.split(", ")) {
            if (s.equalsIgnoreCase("before")) {
                pv.setPrependNewLine(true);
            } else if (s.equalsIgnoreCase("after")) {
                pv.setAppendNewLine(true);
            } else if (s.equalsIgnoreCase("label")) {
                pv.setLabelNewLine(true);
            }
        }
        pv.setNoLabel(findBooleanProperty(f, UIConstants.FIELD_FORM_NOLABEL, null, pv.isNoLabel()));
        String span = findStringProperty(f, UIConstants.FIELD_FORM_SPAN, null, "");
        if (span.length() > 0) {
            String[] spanned = span.split(",x");
            if (spanned.length > 0) {
                if (spanned[0].equalsIgnoreCase("MAX_VALUE")) {
                    pv.setColspan(Integer.MAX_VALUE);
                } else {
                    pv.setColspan(Convert.toInteger(spanned[0], IntegerParserConfig.LENIENT.setInvalidValue(pv.getColspan()).setNullValue(1)));
                }
                if (spanned.length > 1) {
                    if (spanned[1].equalsIgnoreCase("MAX_VALUE")) {
                        pv.setRowpan(Integer.MAX_VALUE);
                    } else {
                        pv.setRowpan(Convert.toInteger(spanned[1], IntegerParserConfig.LENIENT.setInvalidValue(pv.getRowpan()).setNullValue(1)));
                    }
                }
            }
        }
        pv.setPrependEmptyCells(findIntProperty(f, UIConstants.FIELD_FORM_EMPTY_PREFIX, null, pv.getPrependEmptyCells()));
        pv.setAppendEmptyCells(findIntProperty(f, UIConstants.FIELD_FORM_EMPTY_SUFFIX, null, pv.getAppendEmptyCells()));
        pv.setSeparatorText(findStringProperty(f, UIConstants.FIELD_FORM_SEPARATOR, null, pv.getSeparatorText()));
    }
}
