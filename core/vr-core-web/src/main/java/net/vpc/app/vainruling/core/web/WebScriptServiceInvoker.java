package net.vpc.app.vainruling.core.web;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.util.VrPlatformUtils;
import net.vpc.upa.UPA;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

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


    public Map invoke(String script) {
        UserSession s = UserSession.get();
        if (s == null || !s.isConnected()) {
            Map out = new LinkedHashMap();
            Map<String,Object> err=new LinkedHashMap<>();
            err.put("type","SecurityException");
            err.put("message","not connected");
            out.put("$error", err);
            return out;
        }
        ScriptEngine scriptEngine = createScriptEngine();
        currentEngine.set(scriptEngine);
        try {
            try {
                scriptEngine.eval(script);
                return (Map) scriptEngine.get("out");
            } catch (Exception e) {
                Map out = (Map) scriptEngine.get("out");
                Map<String,Object> err=new LinkedHashMap<>();
                err.put("type",e.getClass().getSimpleName());
                err.put("message",e.getMessage());
                out.put("$error", err);
                return out;
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
        try {
            engine.eval("function Return(x) { privateVrReturnCount=privateVrReturnCount+1; out['$'+privateVrReturnCount]=x; }");
            engine.eval("function ReturnVar(v,x) { privateVrReturnCount=privateVrReturnCount+1; out[v]=x; }");
            engine.eval("function logout(u,p) { Return(app.logout(u,p)); }");
            engine.eval("function bean(n) { return (app.bean(n)); }");
            engine.eval("function typeFor(n) { return (app.typeFor(n)); }");
            engine.eval("function fromJson(t,n) { return (app.fromJson(t,n)); }");
            engine.eval("function convert(t,n) { return (app.convert(t,n)); }");
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
            return VrApp.getBean(name);
        }

        public Object convert(String toType,Object value) {
            if(value==null){
                return null;
            }
            return convert2(typeFor(String.valueOf(toType)),value);
        }

        public Object convert2(Class expectedType,Object value) {
            if(value==null){
                return null;
            }
            if(String.class.equals(expectedType)){
                return String.valueOf(value);
            }
            if(JsonElement.class.isAssignableFrom(expectedType)){
                if(value instanceof ScriptObjectMirror){
                    ScriptObjectMirror m=(ScriptObjectMirror)value;
                    JsonObject oo=new JsonObject();
                    for (Map.Entry<String,Object> e : m.entrySet()) {
                        VrPlatformUtils.PropertyInfo property = VrPlatformUtils.forType(expectedType).getProperty(e.getKey());
                        Object value1 = e.getValue();
                        Object t = convert2(property.getType().getClazz(), value1);
                        oo.add(e.getKey(), (JsonElement) convert2(JsonElement.class,t));
                    }
                }else{
                    Gson g=new Gson();
                    return g.toJsonTree(value);
                }
                return String.valueOf(value);
            }
            if(value instanceof String) {
                return VrUtils.parseJSONObject(String.valueOf(value), expectedType);
            }
            if(value instanceof ScriptObjectMirror){
                ScriptObjectMirror m=(ScriptObjectMirror)value;
                Object oo= null;
                try {
                    oo = expectedType.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                for (Map.Entry<String,Object> e : m.entrySet()) {
                    VrPlatformUtils.PropertyInfo property = VrPlatformUtils.forType(expectedType).getProperty(e.getKey());
                    if(property.isUpdatable()) {
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
