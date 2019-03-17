package net.vpc.app.vainruling.core.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.plugins.Plugin;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.UPA;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import net.vpc.common.util.MapUtils;

public class WebScriptServiceInvoker {

    public static ThreadLocal<ScriptEngine> currentEngine = new ThreadLocal<>();
    public Set<String> blacklistClassNames = new HashSet<>(
            Arrays.asList(
                    "java.io.File",
                    "java.lang.Process",
                    "java.lang.System",
                    "java.lang.Thread"
            )
    );
    public List<Pattern> blacklistClassNamePatterns = new ArrayList<>();

    public static Map buildError(Throwable error, Map out) {
        return buildError(error.getClass().getSimpleName(), error.getMessage(), out);
    }

    public static Map buildError(String type, String message, Map out) {
        if (out == null) {
            out = new LinkedHashMap();
        }
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("type", type);
        err.put("message", message);
        out.put("$error", err);
        return out;
    }

    public static Map buildSimpleResult(Object result, Map out) {
        if (out == null) {
            out = new LinkedHashMap();
        }
        out.put("$1", result);
        return out;
    }

    public Map invoke(String script) {
        UserToken s = CorePlugin.get().getCurrentToken();
        if (s == null || s.getUserLogin() == null) {
            return buildError("SecurityException", "not connected", null);
        }
        if (StringUtils.isEmpty(script)) {
            return new HashMap();
        }
        ScriptEngine scriptEngine = createScriptEngine();
        currentEngine.set(scriptEngine);
        try {
            try {
                String firstLineOfScript = new BufferedReader(new StringReader(script)).readLine();
                TraceService.get().trace("System.actions.web-script", null,MapUtils.map("script", firstLineOfScript), "web-script", Level.WARNING);
                scriptEngine.eval(script);
                Map m = (Map) scriptEngine.get("out");
                for (Object k : m.keySet().toArray(new Object[m.size()])) {
                    Object v = m.get(k);
                    if(v instanceof List){
                        v=new ArrayList<Object>((List)v);
                        m.put(k, v);
                    }
                }
                return m;
            } catch (Exception e) {
                Map out = (Map) scriptEngine.get("out");
                return buildError(e, out);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ScriptEngine createScriptEngine() {
        ScriptEngine engine = null;
        try {
            engine = createNashornScriptEngine();
        } catch (Exception ex) {
            engine = createManagerJdk();
        }
        engine.put("out", new LinkedHashMap<>());
        engine.put("app", new VrJs());
        engine.put("pu", UPA.getPersistenceUnit());
        engine.put("privateVrReturnCount", 0);
        for (Plugin plugin : CorePlugin.get().getPlugins()) {
            for (String beanName : plugin.getBeanNames()) {
                Object bean = VrApp.getBean(beanName);
                engine.put(beanName, bean);
                if(beanName.endsWith("Plugin")){
                    engine.put(beanName.substring(0,beanName.length()-"Plugin".length()), bean);
                }
            }
        }
        try {
            engine.eval("function Return(x) { privateVrReturnCount=privateVrReturnCount+1; out['$'+privateVrReturnCount]=x; }");
            engine.eval("function ReturnVar(v,x) { privateVrReturnCount=privateVrReturnCount+1; out[v]=x; }");
            engine.eval("function logout(u,p) { Return(app.logout(u,p)); }");
            engine.eval("function bean(n) { return (app.bean(n)); }");
            engine.eval("function typeFor(n) { return (app.typeFor(n)); }");
            engine.eval("function fromJson(t,n) { return (app.fromJson(t,n)); }");
            engine.eval("function convert(t,n) { return (app.convert(t,n)); }");
            engine.eval("function anydate(t,f) { return (app.anydate(t,f)); }");
            engine.eval("function date(t) { return (app.date(t)); }");
            engine.eval("function datetime(t) { return (app.datetime(t)); }");
            engine.eval("function time(t) { return (app.time(t)); }");
        } catch (Exception any) {
            throw new RuntimeException(any);
        }
        return engine;
    }

    private ScriptEngine createNashornScriptEngine() {
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

    public static class VrJs {

        public Object bean(String name) {
            //force the bean to be a plugin!!
            if (!name.endsWith("Plugin")) {
                name = name + "Plugin";
            }
            return VrApp.getBean(name);
        }

        public Object datetime(Object dte) {
            return anydate(dte, "yyyy-MM-dd HH:mm:SS");
        }

        public Object date(Object dte) {
            return anydate(dte, "yyyy-MM-dd");
        }

        public Object time(Object dte) {
            return anydate(dte, "HH:mm:SS");
        }

        public Object anydate(Object dte, String... formats) {
            if (dte instanceof Date) {
                return (Object) dte;
            }
            if (formats == null || formats.length == 0) {
                formats = new String[]{"yyyy-MM-dd HH:mm:SS", "yyyy-MM-dd", "HH:mm:SS"};
            }
            for (String format : formats) {
                if (StringUtils.isEmpty(format)) {
                    format = "yyyy-MM-dd HH:mm:SS";
                }
                try {
                    return new SimpleDateFormat(format).parse(String.valueOf(dte));
                } catch (ParseException e) {
                    //ignore
                }
            }
            throw new IllegalArgumentException("invalid date " + dte + " for formats " + Arrays.asList(formats));
        }

        public Object convert(String toType, Object value) {
            if (value == null) {
                return null;
            }
            return convert2(typeFor(String.valueOf(toType)), value);
        }

        public Object convert2(Class expectedType, Object value) {
            if (value == null) {
                return null;
            }
            if (String.class.equals(expectedType)) {
                return String.valueOf(value);
            }
            if (JsonElement.class.isAssignableFrom(expectedType)) {
                if (value instanceof ScriptObjectMirror) {
                    ScriptObjectMirror m = (ScriptObjectMirror) value;
                    JsonObject oo = new JsonObject();
                    for (Map.Entry<String, Object> e : m.entrySet()) {
                        VrPlatformUtils.PropertyInfo property = VrPlatformUtils.forType(expectedType).getProperty(e.getKey());
                        Object value1 = e.getValue();
                        Object t = convert2(property.getType().getClazz(), value1);
                        oo.add(e.getKey(), (JsonElement) convert2(JsonElement.class, t));
                    }
                } else {
                    return VrUtils.GSON.toJsonTree(value);
                }
                return String.valueOf(value);
            }
            if (value instanceof String) {
                return VrUtils.parseJSONObject(String.valueOf(value), expectedType);
            }
            if (value instanceof ScriptObjectMirror) {
                ScriptObjectMirror m = (ScriptObjectMirror) value;
                Object oo = null;
                try {
                    oo = expectedType.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                for (Map.Entry<String, Object> e : m.entrySet()) {
                    VrPlatformUtils.PropertyInfo property = VrPlatformUtils.forType(expectedType).getProperty(e.getKey());
                    if (property.isUpdatable()) {
                        property.set(oo, convert2(property.getType().getClazz(), e.getValue()));
                    }
                }
                return oo;
            }
            return VrUtils.parseJSONObject(String.valueOf(value), expectedType);
        }

        public Class typeFor(String name) {
            if (name == null) {
                return Object.class;
            }
            if ("class".equals(name) || "Class".equals(name)) {
                return Class.class;
            }
            if ("string".equals(name) || "string".equals(name)) {
                return String.class;
            }
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return Object.class;
        }

        public Object fromJson(String type, String json) {
            return VrUtils.parseJSONObject(json, typeFor(type));
        }

//        public boolean login(String login, String password) {
//            boolean ret = false;
//            try {
//                VrWebHelper.prepareUserSession();
//                UserSession userSession = UserSession.get();
//                AppUser u = VrApp.getBean(CorePlugin.class).login(login, password);
//                ret = u != null;
//            } catch (Exception e) {
//                ret = false;
//                e.printStackTrace();
//                //return false;
//            }
//            try {
//                currentEngine.get().eval("Return(" + ret + ")");
//            } catch (ScriptException e) {
//                e.printStackTrace();
//            }
//            return ret;
//        }
        public boolean logout() {
            boolean ret = false;
            try {
                VrApp.getBean(CorePlugin.class).logout();
                ret = true;
            } catch (Exception e) {
                ret = false;
                e.printStackTrace();
            }
//            try {
//                currentEngine.get().eval("Return(" + ret + ")");
//            } catch (ScriptException e) {
//                e.printStackTrace();
//            }
            return ret;
        }
    }
}
