/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.plugins.Plugin;
import net.vpc.app.vainruling.core.service.plugins.PluginComponent;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.upa.UPAObject;
import net.vpc.upa.types.I18NString;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import sun.util.ResourceBundleEnumeration;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dev01
 */
@Service
public class I18n implements Serializable {

    private static final Logger log = Logger.getLogger(I18n.class.getName());
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final LinkedHashSet<String> bundlesUrls = new LinkedHashSet<String>();
    private final Map<String, ResourceBundleSuite> bundles = new HashMap<>();

    public static I18n get(){
        return VrApp.getBean(I18n.class);
    }

    public I18n() {
    }

    public void register(String bundle) {
        bundlesUrls.add(bundle);
    }

    public ResourceBundle loadResourceBundle(String baseName,
                                             Locale locale) {
        List<Plugin> plugins = VrApp.getBean(CorePlugin.class).getPlugins();
        Properties properties=new Properties();
        for (Plugin plugin : plugins) {
            for (String s : toBundleNames(baseName, locale)) {
                for (PluginComponent component : plugin.getInfo().getComponents()) {
                    URL url = null;
                    try {
                        url = component.getRuntimeURL("/" + s.replace('.','/')+".properties");
                    } catch (MalformedURLException e) {
                        //ignore
                    }
                    if (url != null){
                        InputStream is = null;
                        try {
                            try {
                                is = url.openStream();
                                properties.load(is);
                            } finally {
                                if (is != null) {
                                    is.close();
                                }
                            }
                        }catch(FileNotFoundException e){
                            //ignore
                        }catch(Exception e){
                            log.log(Level.SEVERE, e.toString());
                        }
                    }
                }
            }
        }
        return new PropertiesResourceBundle(properties);
    }


    private List<String> toBundleNames(String baseName, Locale locale) {
        List<String> list = new ArrayList<>();
        if (locale != null) {
            list.add(toBundleName0(baseName, locale));
            if (!StringUtils.isEmpty(locale.getVariant())) {
                list.add(0,toBundleName0(baseName, new Locale(locale.getLanguage(), locale.getCountry())));
                if (!StringUtils.isEmpty(locale.getCountry())) {
                    list.add(0,toBundleName0(baseName, new Locale(locale.getLanguage())));
                    if (!StringUtils.isEmpty(locale.getLanguage())) {
                        list.add(0,toBundleName0(baseName, Locale.ROOT));
                    }
                }
            }else{
                if (!StringUtils.isEmpty(locale.getCountry())) {
                    list.add(0,toBundleName0(baseName, new Locale(locale.getLanguage())));
                    if (!StringUtils.isEmpty(locale.getLanguage())) {
                        list.add(0,toBundleName0(baseName, Locale.ROOT));
                    }
                }else{
                    if (!StringUtils.isEmpty(locale.getLanguage())) {
                        list.add(0,toBundleName0(baseName, Locale.ROOT));
                    }
                }
            }
        }else{
            list.add(baseName);
        }
        return list;
    }

    private String toBundleName0(String baseName, Locale locale) {
        if (locale == Locale.ROOT) {
            return baseName;
        }

        String language = locale.getLanguage();
        String script = locale.getScript();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        if (Objects.equals(language, "") && Objects.equals(country, "") && Objects.equals(variant, "")) {
            return baseName;
        }

        StringBuilder sb = new StringBuilder(baseName);
        sb.append('_');
        if (!Objects.equals(script, "")) {
            if (!Objects.equals(variant, "")) {
                sb.append(language).append('_').append(script).append('_').append(country).append('_').append(variant);
            } else if (!Objects.equals(country, "")) {
                sb.append(language).append('_').append(script).append('_').append(country);
            } else {
                sb.append(language).append('_').append(script);
            }
        } else {
            if (!Objects.equals(variant, "")) {
                sb.append(language).append('_').append(country).append('_').append(variant);
            } else if (!Objects.equals(country, "")) {
                sb.append(language).append('_').append(country);
            } else {
                sb.append(language);
            }
        }
        return sb.toString();

    }

    public ResourceBundleSuite getResourceBundleSuite() {
        UserSession s = null;
        try {
            s = CorePlugin.get().getUserSession();
        } catch (Exception e) {
            // not in session context!
        }
        Locale lang = null;
        if (s != null && s.getUser() != null && s.getLang() != null) {
            lang = new Locale(s.getLang());
        }
        if (lang == null) {
            lang = Locale.getDefault();
        }
        ResourceBundleSuite b = bundles.get(lang.toString());
        if (b == null) {
            b = new ResourceBundleSuite();
            for (String u : bundlesUrls) {
                try {
                    b.add(loadResourceBundle(u, lang));
//                    b.add(ResourceBundle.getBundle(u, lang));
                } catch (java.util.MissingResourceException e) {
                    log.log(Level.SEVERE, "Unable to load ResourceBundle {0} for lang {1} :: {2}", new Object[]{u, lang, e});
                }
            }
            bundles.put(lang.toString(), b);
        }
        return b;
    }

    public String getString(String s) {
        String v = getResourceBundleSuite().get(s, null);
        if (v != null) {
            return v;
        }
        return s + "!!";
    }

    public String get(UPAObject s, Object... params) {
        return get(s.getI18NString(), params);
    }

    public String get(I18NString s, Object... params) {
        for (String key : s.getKeys()) {
            String v = getResourceBundleSuite().get(key, null, params);
            if (v != null) {
                return v;
            }
        }
        String d = s.getDefaultValue();
        if (d == null) {
            return s.toString() + "!!";
        }
        return d;
    }

    public String getOrNull(I18NString s, Object... params) {
        for (String key : s.getKeys()) {
            String v = getResourceBundleSuite().get(key, null, params);
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    public String getOrDefault(String s, String defaultValue,Object[] params) {
        String v = getOrNull(s, params);
        if(v!=null){
            return v;
        }
        if(params==null || params.length==0){
            return defaultValue;
        }
        return format(defaultValue,params);
    }

    public String getOrNull(UPAObject s, Object... params) {
        return getOrNull(s.getI18NString(), params);
    }

    public String getOrNull(String key, Object... params) {
        return getResourceBundleSuite().get(key, null, params);
    }

    public String getEnum(Object obj) {
        if (obj == null) {
            return "";
        }
        String t = obj.getClass().getName();
        String r = getOrNull("Enum." + t + "[" + obj + "]");
        if (r != null) {
            return r;
        }
        t = obj.getClass().getSimpleName();
        r = getOrNull("Enum." + t + "[" + obj + "]");
        if (r != null) {
            return r;
        }
        return obj.toString();
    }

    public String get(String key) {
        return get(key, new Object[0]);
    }

    public String get(String key, Object... params) {
        return getResourceBundleSuite().get(key, key + "!!", params);
    }

    public String format(String message) {
        return format(message, new Object[0]);
    }

    public String format(String message, Object... params) {
        return getResourceBundleSuite().format(message, message + "!!", params);
    }

    public String getConcat(String key, String keyConcat) {
        String param = key + keyConcat;
        return get(param, new Object[0]);
    }

    //    public String getInfoSys(String key) {
//        String k = key.replace(":", ".");
//        return get(k, new Object[0]);
//    }
    private static class PropertiesResourceBundle extends ResourceBundle {
        private Map<String,String> lookup;

        @SuppressWarnings({"unchecked", "rawtypes"})
        public PropertiesResourceBundle(Map<String,String> lookup) {
            this.lookup = lookup;
        }

        public PropertiesResourceBundle(Properties properties) {
            this.lookup = new HashMap(properties);
        }


        // Implements java.util.ResourceBundle.handleGetObject; inherits javadoc specification.
        public Object handleGetObject(String key) {
            if (key == null) {
                throw new NullPointerException();
            }
            return lookup.get(key);
        }

        /**
         * Returns an <code>Enumeration</code> of the keys contained in
         * this <code>ResourceBundle</code> and its parent bundles.
         *
         * @return an <code>Enumeration</code> of the keys contained in
         * this <code>ResourceBundle</code> and its parent bundles.
         * @see #keySet()
         */
        public Enumeration<String> getKeys() {
            ResourceBundle parent = this.parent;
            return new ResourceBundleEnumeration(lookup.keySet(),
                    (parent != null) ? parent.getKeys() : null);
        }

        /**
         * Returns a <code>Set</code> of the keys contained
         * <em>only</em> in this <code>ResourceBundle</code>.
         *
         * @return a <code>Set</code> of the keys contained only in this
         * <code>ResourceBundle</code>
         * @see #keySet()
         * @since 1.6
         */
        protected Set<String> handleKeySet() {
            return lookup.keySet();
        }

    }

}
