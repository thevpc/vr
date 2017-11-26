/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.upa.*;

/**
 * @author taha.bensalah@gmail.com
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
        return Convert.toInt(findObjectProperty(f, property, context, defaultValue));
    }

    public static String findStringProperty(UPAObject f, String property, String context, String defaultValue) {
        return Convert.toString(findObjectProperty(f, property, context, defaultValue));
    }

    public static void applyLayout(UPAObject f, PropertyView pv,ViewContext viewContext) {
        String newline = findStringProperty(f, UIConstants.Form.NEWLINE, null, "");
        for (String s : newline.split(", ")) {
            if (s.equalsIgnoreCase("before")) {
                pv.setPrependNewLine(true);
            } else if (s.equalsIgnoreCase("after")) {
                pv.setAppendNewLine(true);
            } else if (s.equalsIgnoreCase("label")) {
                pv.setLabelNewLine(true);
            }
        }
        pv.setNoLabel(findBooleanProperty(f, UIConstants.Form.NO_LABEL, null, pv.isNoLabel()));
        String span = findStringProperty(f, UIConstants.Form.SPAN, null, "");
        if (span.length() > 0) {
            String[] spanned = span.split(",x");
            if (spanned.length > 0) {
                if (spanned[0].equalsIgnoreCase("MAX_VALUE")) {
                    pv.setColspan(Integer.MAX_VALUE);
                } else {
                    pv.setColspan(Convert.toInt(spanned[0], IntegerParserConfig.LENIENT.setInvalidValue(pv.getColspan()).setNullValue(1)));
                }
                if (spanned.length > 1) {
                    if (spanned[1].equalsIgnoreCase("MAX_VALUE")) {
                        pv.setRowpan(Integer.MAX_VALUE);
                    } else {
                        pv.setRowpan(Convert.toInt(spanned[1], IntegerParserConfig.LENIENT.setInvalidValue(pv.getRowpan()).setNullValue(1)));
                    }
                }
            }
        }
        pv.setPrependEmptyCells(findIntProperty(f, UIConstants.Form.EMPTY_PREFIX, null, pv.getPrependEmptyCells()));
        pv.setAppendEmptyCells(findIntProperty(f, UIConstants.Form.EMPTY_SUFFIX, null, pv.getAppendEmptyCells()));
        if(f instanceof Field){
            EntityPart parent = ((Field) f).getParent();
            if(parent!=null && parent instanceof Section){
                String path = parent.getPath();
                String name=VrApp.getBean(I18n.class).get(parent);
                if(((Section) parent).getParts().get(0).getName().equals(f.getName())){
                    //first field in the section
                    pv.setSeparatorText(path);
                }
            }
        }
        //pv.setSeparatorText(findStringProperty(f, UIConstants.Form.SEPARATOR, null, pv.getSeparatorText()));
        if (f instanceof Field) {
            Field ff = (Field) f;
            Entity ffe = ((Field) f).getEntity();
            EntityPart p1 = ff.getParent();
            String separatorText=null;
            String toStoreSeparatorText=null;
            if (p1 != null && p1 instanceof Section) {
                separatorText = VrApp.getBean(I18n.class).get(p1);
                String lastSeparatorText = (String) viewContext.getProperties().get("lastSeparatorText");
                toStoreSeparatorText=separatorText;
                if(separatorText.equals(lastSeparatorText)){
                    separatorText=null;
                }
            }else{
                toStoreSeparatorText="";
                String lastSeparatorText = (String) viewContext.getProperties().get("lastSeparatorText");
                if("".equals(lastSeparatorText)){
                    separatorText=null;
                }
            }
            pv.setSeparatorText(separatorText);
            if(pv.isVisible()) {
                viewContext.getProperties().put("lastSeparatorText", toStoreSeparatorText);
            }
        }
    }
}
