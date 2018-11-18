/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.*;
import net.vpc.common.strings.MessageNameFormat;

/**
 * @author dev01
 */
public class ResourceBundleSuite {

    private int defaultDepth = 10;
    private List<ResourceBundle> all = new ArrayList<ResourceBundle>();
    private Properties map = new Properties();
    private PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("%{", "}");

    public void add(Properties r,String label) {
        all.add(new I18n.PropertiesResourceBundle(r));
        map.putAll(r);
    }

    public Properties getMap() {
        return map;
    }

    public String get(String name, String defaultValue, Map<String,Object> params) {
        return get0(name, defaultDepth, defaultValue, params);
    }
    public String get(String name, String defaultValue, Arg... params) {
        return get0(name, defaultDepth, defaultValue, params);
    }
    
    private String get0(String name, final int maxDepth, String defaultValue, Map<String,Object> params) {
        String value = null;
        for (ResourceBundle b : all) {
            try {
                String s = b.getString(name);
                if (s != null) {
                    value = s;
                    break;
                }
            } catch (MissingResourceException e) {
                //ignore
            }
        }
        if (value == null) {
            value = defaultValue;
        }
        if (value == null) {
            return null;
        }
        return format0(value, maxDepth, params);
    }

    private String get0(String name, final int maxDepth, String defaultValue, Arg... params) {
        String value = null;
        for (ResourceBundle b : all) {
            try {
                String s = b.getString(name);
                if (s != null) {
                    value = s;
                    break;
                }
            } catch (MissingResourceException e) {
                //ignore
            }
        }
        if (value == null) {
            value = defaultValue;
        }
        if (value == null) {
            return null;
        }
        return format0(value, maxDepth, params);
    }

    public String format(String message, Arg... params) {
        return format0(message, defaultDepth, params);
    }

    private String format0(String message, final int maxDepth, Map<String,Object> params) {
        if (message == null) {
            return message;
        }
        if (message.contains("%{")) {
            message = placeholderHelper.replacePlaceholders(message, new PropertyPlaceholderHelper.PlaceholderResolver() {

                @Override
                public String resolvePlaceholder(String placeholderName) {
                    String v = null;
                    if (maxDepth > 0) {
                        v = get0(placeholderName, maxDepth, null);
                    }
                    if (v == null) {
                        v = placeholderName;
                    }
                    return v;
                }
            });
        }
        if (params!=null && params.size() > 0) {
            message = new MessageNameFormat(message).format(params);
        }
        return message;
    }
    
    private String format0(String message, final int maxDepth, Arg... params) {
        if (message == null) {
            return message;
        }
        if (message.contains("%{")) {
            message = placeholderHelper.replacePlaceholders(message, new PropertyPlaceholderHelper.PlaceholderResolver() {

                @Override
                public String resolvePlaceholder(String placeholderName) {
                    String v = null;
                    if (maxDepth > 0) {
                        v = get0(placeholderName, maxDepth, null);
                    }
                    if (v == null) {
                        v = placeholderName;
                    }
                    return v;
                }
            });
        }
        if (params.length > 0) {
            Map<String,Object> paramsMap=new HashMap<>();
            for (Arg param : params) {
                if(param!=null){
                    paramsMap.put(param.getName(), param.getValue());
                }
            }
            message = new MessageNameFormat(message).format(paramsMap);
        }
        return message;
    }
}
