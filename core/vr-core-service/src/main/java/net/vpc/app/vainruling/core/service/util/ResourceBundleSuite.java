/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import org.springframework.util.PropertyPlaceholderHelper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author dev01
 */
public class ResourceBundleSuite {

    private int defaultDepth = 10;
    private List<ResourceBundle> all = new ArrayList<ResourceBundle>();
    private PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("%{", "}");

    public void add(ResourceBundle r) {
        all.add(r);
    }

    public String get(String name, String defaultValue, Object... params) {
        return get0(name, defaultDepth, defaultValue, params);
    }

    private String get0(String name, final int maxDepth, String defaultValue, Object... params) {
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

    public String format(String message, Object... params) {
        return format0(message, defaultDepth, params);
    }

    private String format0(String message, final int maxDepth, Object... params) {

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
            message = MessageFormat.format(message, params);
        }
        return message;
    }
}
