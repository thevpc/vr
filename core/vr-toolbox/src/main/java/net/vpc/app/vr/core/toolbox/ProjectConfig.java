package net.vpc.app.vr.core.toolbox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProjectConfig {

    private final Properties config = new Properties();
    private final Map<String, Getter> getters = new HashMap<String, Getter>();

    private TemplateConsole io = new DefaultConsole();

    public ProjectConfig() {
    }

    public interface Getter {

        String get(String name, ProjectConfig c);
    }

    public final void setProperty(String name, String defaultvalue, StringValidator g) {
        config.setProperty(name + "DefaultValue", defaultvalue);
        getters.put(name, (n, c) -> c.get(n, defaultvalue, g));
    }

    public void setIo(TemplateConsole io) {
        this.io = io;
    }

    public void unset(String propertyName) {
        set(propertyName, null);
    }

    public void set(String propertyName, String value) {
        if (value == null || value.trim().isEmpty()) {
            config.remove(propertyName);
        } else {
            config.setProperty(propertyName, value);
        }
    }

    public String getModuleName() {
        return get("vrModuleName");
    }

    public String getModuleVersion() {
        return get("vrModuleVersion");
    }

    public String getProjectName() {
        return get("vrProjectName");
    }

    public String getProjectVersion() {
        return get("vrProjectVersion");
    }

    public String getProjectGroup() {
        return get("vrProjectGroup");
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        String s = get(name, String.valueOf(defaultValue), ValidatorFactory.BOOLEAN);
        return "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s);
    }

    public String get(String name, String defaultValue, StringValidator v) {
        String f = config.getProperty(name);
        if (f != null) {
            return f;
        }
        if (defaultValue == null) {
            defaultValue = config.getProperty(name + "DefaultValue");
        }
        String o = io.askForString(name, v, defaultValue);
        if (o != null) {

        }
        if (o != null) {
            set(name, o);
        }
        return o;
    }

    public String get(String name) {
        String f = config.getProperty(name);
        if (f != null) {
            return f;
        }
        Getter p = getters.get(name);
        if (p == null) {
            p = (n, c) -> c.get(n, null, null);
        }
        String o = p.get(name, this);
        if (o != null) {
            set(name, o);
        }
        return o;
    }

    public File getProjectRootFolder() {
        return new File(get("vrProjectRootFolder"));
    }

}
