/**
 * ====================================================================
 * Nuts : Network Updatable Things Service
 * (universal package manager)
 * <p>
 * is a new Open Source Package Manager to help install packages
 * and libraries for runtime execution. Nuts is the ultimate companion for
 * maven (and other build managers) as it helps installing all package
 * dependencies at runtime. Nuts is not tied to java and is a good choice
 * to share shell scripts and other 'things' . Its based on an extensible
 * architecture to help supporting a large range of sub managers / repositories.
 * <p>
 * Copyright (C) 2016-2017 Taha BEN SALAH
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * ====================================================================
 */
package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.common.strings.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by vpc on 1/7/17.
 */
public class JavascriptEvaluator {
    public Set<String> blacklistClassNames = new HashSet<>(
            Arrays.asList(
                    "java.io.File",
                    "java.lang.Process",
                    "java.lang.System",
                    "java.lang.Thread",
                    "java.lang.Runtime",
                    "java.nio.file.Files",
                    "java.nio.file.Paths",
                    "java.nio.file.FileSystem",
                    "java.io.FileSystem",
                    "java.lang.ProcessBuilder",
                    "java.lang.Class",
                    "java.lang.ClassLoader",
                    "net.thevpc.upa.UPA"
            )
    );
    public List<Pattern> blacklistClassNamePatterns = new ArrayList<>();
    private String code;
    private ScriptEngine engine;

    public JavascriptEvaluator(String code) {
        this(code, null);
    }

    public JavascriptEvaluator(String code, Set<String> blacklist) {
        if (blacklist == null) {
            blacklistClassNames.addAll(Arrays.asList(
                    "java.io.File",
                    "java.lang.Process",
                    "java.lang.System",
                    "java.lang.Thread"
            ));
        } else {
            for (String s : blacklist) {
                if (s.contains("*")) {
                    blacklistClassNamePatterns.add(Pattern.compile(VrUtils.simpexpToRegexp(s, false)));
                } else {
                    blacklistClassNames.add(s);
                }
            }
        }
        if (code == null) {
            throw new IllegalArgumentException("Illegal js filter : empty content");
        }
        if (!code.contains("return")) {
            if (code.contains(";") || code.contains("{") || code.contains("}")) {
                throw new IllegalArgumentException("js filter must contain a return clause");
            } else {
                code = "return (" + code + ");";
            }
        }
        String varName="x";
        this.code = replaceWord(code,"this",varName);
        try {
            engine = createScriptEngine();
        } catch (Exception ex) {
            engine = createManagerJdk();
        }
        try {
            engine.eval("function jseval("+varName+") { " + code + " }");
            engine.put("vr", new ScriptUtil());
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }

    }

    private static String replaceWord(String x,String a,String b){
        return x.replaceAll("\\b"+a+"\\b", b);
    }

//    public static void main(String[] args) {
////        System.out.println(new ScriptUtil().same(new boolean[]{true,false},"[true, false]"));
////        if(true){
////            return;
////        }
////        
////        JavascriptEvaluator e = new JavascriptEvaluator("vr.same(x.get('type'),'ACQUISITION')");
////        Map<String, Object> m = new LinkedHashMap<>();
////        m.put("type", "ACQUISITION");
////        System.out.println(e.eval(m));
////        System.out.println(java.lang.Runtime.getRuntime().availableProcessors());
//        JavascriptEvaluator e = new JavascriptEvaluator("java.lang.Runtime.getRuntime().availableProcessors()");
//        Map<String, Object> m = new LinkedHashMap<>();
//        m.put("type", "ACQUISITION");
//        System.out.println(e.eval(m));
//        
//    }

    private ScriptEngine createScriptEngine() {
        jdk.nashorn.api.scripting.NashornScriptEngineFactory f = new jdk.nashorn.api.scripting.NashornScriptEngineFactory();
        return f.getScriptEngine(new jdk.nashorn.api.scripting.ClassFilter() {
            @Override
            public boolean exposeToScripts(String s) {
                if (blacklistClassNames.contains(s)) {
                    return false;
                }
                for (Pattern pattern : blacklistClassNamePatterns) {
                    if (pattern.matcher(s).matches()) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    private ScriptEngine createManagerJdk() {
        ScriptEngineManager engineManager
                = new ScriptEngineManager();
        return engineManager.getEngineByName("nashorn");
    }

    public String getCode() {
        return code;
    }

    public void declare(String name, Object d) {
        if (d != null) {
            engine.put(name, d);
        }
    }

    public Object eval(Object d) {
        engine.put("THE_ITEM_1278", d);
        try {
            return (engine.eval("jseval(THE_ITEM_1278);"));
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }


    public static class ScriptUtil {

        public boolean matches(String pattern, String value) {
            if (pattern == null) {
                pattern = "";
            }
            if (value == null) {
                value = "";
            }
            return value.matches(VrUtils.simpexpToRegexp(pattern, true));
        }

        public String trim(String s) {
            return StringUtils.trim(s);
        }

        public boolean same(Object v1, Object v2) {
            if (v1 == null) {
                v1 = "";
            }
            if (v2 == null) {
                v2 = "";
            }
            if (Objects.equals(v1, v2)) {
                return true;
            }
            if (v1 instanceof String) {
                if (v2 instanceof Enum) {
                    return v1.equals(v2.toString());
                }
                if (v2 instanceof Number) {
                    return v1.equals(v2);
                }
                if (v2 instanceof Object[]) {
                    return v1.equals(Arrays.deepToString((Object[]) v2));
                }
                if (v2 instanceof int[]) {
                    return v1.equals(Arrays.toString((int[]) v2));
                }
                if (v2 instanceof double[]) {
                    return v1.equals(Arrays.toString((double[]) v2));
                }
                if (v2 instanceof boolean[]) {
                    return v1.equals(Arrays.toString((boolean[]) v2));
                }
                if (v1.equals(v2.toString())) {
                    return true;
                }
            } else if (v2 instanceof String) {
                return same(v2, v1);
            }
            return false;
        }

    }

}
