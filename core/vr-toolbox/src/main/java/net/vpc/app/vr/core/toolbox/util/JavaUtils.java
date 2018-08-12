/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.core.toolbox.util;

import net.vpc.app.vr.core.toolbox.ProjectConfig;
import net.vpc.app.vr.core.toolbox.VrModule;
import net.vpc.app.vr.core.toolbox.VrModules;
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

    public static String pathTpPackage(String s) {
        StringBuilder sb = new StringBuilder();
        for (String item : s.split("/|\\.")) {
            if (item.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(".");
                }
                sb.append(item);
            }
        }
        return sb.toString();
    }

    public static String vrMavenModelDependency(String module, ProjectConfig config) {
        boolean ok = config.getBoolean("vrEnableModule_" + module, true);
        if (ok) {
            VrModule mm = VrModules.get(module);
            if (mm != null && mm.isModel()) {
                return "<dependency>\n"
                        + "            <groupId>" + mm.getGroupId() + "</groupId>\n"
                        + "            <artifactId>" + mm.getBaseArtifactId() + "-model</artifactId>\n"
                        + "            <version>${version.vr}</version>\n"
                        + "        </dependency>";
            }
        }
        return "";
    }

    public static String vrMavenServiceDependency(String module, ProjectConfig config) {
        boolean ok = config.getBoolean("vrEnableModule_" + module, true);
        if (ok) {
            VrModule mm = VrModules.get(module);
            if (mm != null && mm.isService()) {
                return "<dependency>\n"
                        + "            <groupId>" + mm.getGroupId() + "</groupId>\n"
                        + "            <artifactId>" + mm.getBaseArtifactId() + "-service</artifactId>\n"
                        + "            <version>${version.vr}</version>\n"
                        + "        </dependency>";
            }
        }
        return "";
    }

    public static String vrMavenWebDependency(String module, ProjectConfig config) {
        boolean ok = config.getBoolean("vrEnableModule_" + module, true);
        if (ok) {
            VrModule mm = VrModules.get(module);
            if (mm != null && mm.isWeb()) {
                return "<dependency>\n"
                        + "            <groupId>" + mm.getGroupId() + "</groupId>\n"
                        + "            <artifactId>" + mm.getBaseArtifactId() + (mm.isTheme() ? "" : "-web") + "</artifactId>\n"
                        + "            <version>${version.vr}</version>\n"
                        + "        </dependency>";
            }
        }
        return "";
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
