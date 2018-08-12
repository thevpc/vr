package net.vpc.app.vr.core.toolbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectConfig {

    private final Properties config = new Properties();
    private final Map<String, Getter> getters = new HashMap<String, Getter>();

    private TemplateConsole console = new DefaultConsole();

    public ProjectConfig() {
    }

    public interface Getter {

        String get(String name, ProjectConfig c);
    }

    public final void setProperty(String name, String defaultvalue, StringValidator g) {
        config.setProperty(name + "DefaultValue", defaultvalue);
        getters.put(name, (n, c) -> c.get(n, defaultvalue, g));
    }

    public void setConsole(TemplateConsole console) {
        this.console = console;
    }

    public TemplateConsole getConsole() {
        return console;
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

    public void loadProperties(File file) {
        Properties p = new Properties();
        try {
            p.load(new FileReader(file));
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        config.putAll(p);
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
        String o = console.ask(name, v, defaultValue);
        if (o != null) {
            set(name, o);
        }
        return o;
    }

    public Properties getProperties(boolean includeDefaults) {
        Properties p = new Properties();
        for (Map.Entry<Object, Object> entry : config.entrySet()) {
            if (includeDefaults || !entry.getKey().toString().endsWith("DefaultValue")) {
                p.put(entry.getKey(), entry.getValue());
            }
        }
        return p;
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
