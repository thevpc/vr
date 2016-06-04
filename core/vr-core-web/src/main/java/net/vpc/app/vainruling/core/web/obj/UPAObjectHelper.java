/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityPart;
import net.vpc.upa.Field;
import net.vpc.upa.Properties;
import net.vpc.upa.Section;
import net.vpc.upa.UPAObject;

/**
 *
 * @author vpc
 */
public class UPAObjectHelper {

    public static Object findObjectProperty(UPAObject f, String property, String context, Object defaultValue) {
        if (f == null) {
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
        if (f instanceof Field) {
            Field ff = (Field) f;
            Entity ffe = ((Field) f).getEntity();
            EntityPart p1 = ff.getParent();
            if (p1 != null && p1 instanceof Section) {
                int i = ffe.indexOfField(ff.getName());
                if (i > 0) {
                    Field ffp = ffe.getField(i - 1);
                    EntityPart p0 = ffp.getParent();
                    if (p0 != p1) {
                        pv.setSeparatorText(VrApp.getBean(I18n.class).get(p1));
                    }
                }
            }
        }
    }
}
