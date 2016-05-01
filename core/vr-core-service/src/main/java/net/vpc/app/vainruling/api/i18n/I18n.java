/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.i18n;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;

import net.vpc.upa.UPAObject;
import net.vpc.upa.types.I18NString;

import org.springframework.stereotype.Service;

/**
 *
 * @author dev01
 */
@Service
public class I18n implements Serializable {

    private static final Logger log = Logger.getLogger(I18n.class.getName());
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final List<String> bundlesUrls = new ArrayList<String>();
    private final Map<String, ResourceBundleSuite> bundles = new HashMap<>();

    public I18n() {
    }

    public void register(String bundle) {
        bundlesUrls.add(bundle);
    }

    public ResourceBundleSuite getResourceBundleSuite() {
        UserSession s = null;
        try {
            s = VrApp.getBean(UserSession.class);
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
                    b.add(ResourceBundle.getBundle(u, lang));
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
}
