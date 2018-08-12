/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.core.toolbox.util;

import net.vpc.app.vr.core.toolbox.util.ClassInfo;
import net.vpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
public class JavaUtils {

    public static String path(String s) {
        return s.replace('.', '/');
    }

    public static String packageName(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '-') {
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    public static ClassInfo detectedClassInfo(String javaCode) {
        String pack = null;
        String cls = null;
        for (String line : javaCode.split("\n")) {
            line = line.trim();
//            System.out.println(line);
            if (!StringUtils.isEmpty(line)) {
                if (pack == null && _StringUtils.isStartsWithWord(line, "package")) {
                    pack = line.substring("package".length(), line.indexOf(';', "package".length())).trim();
                } else {
                    for (String prefix : new String[]{
                        "public class",
                        "public interface",}) {
                        String rest = null;
                        if ((rest = _StringUtils.consumeWords(line, prefix)) != null && cls == null) {
                            String name = _StringUtils.consumeWord(rest);
                            if (name != null) {
                                cls = name;
                            }
                        }
                    }
                }
            }
            if (pack != null && cls != null) {
                break;
            }
        }
        if (cls != null) {
            return new ClassInfo(cls, pack);
        }
        throw new IllegalArgumentException("Unable to resolve class name");
    }

    public static String className(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '-') {
                i++;
                sb.append(Character.toUpperCase(s.charAt(i)));
            } else {
                sb.append(c);
            }
        }
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
    
    public static String varName(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '-') {
                i++;
                sb.append(Character.toUpperCase(s.charAt(i)));
            } else {
                sb.append(c);
            }
        }
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

}
