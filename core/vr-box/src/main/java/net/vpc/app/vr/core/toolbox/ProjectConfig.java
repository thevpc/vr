package net.vpc.app.vr.core.toolbox;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ProjectConfig {

    private final Map<String, ProjectProperty> config = new HashMap<>();

    private TemplateConsole console;
    List<ProjectConfigListener> listeners = new ArrayList<>();

    public ProjectConfig(TemplateConsole console) {
        this.console = console;
    }

    public List<ProjectConfigListener> getListeners() {
        return listeners;
    }

    public final void setProperty(String name, String defaultvalue, StringValidator g, String title) {
        ProjectProperty v = get(name);
        v.setDefaultValue(defaultvalue);
        v.setValidator(g);
        v.setTitle(title);
    }

//    public void setConsole(TemplateConsole console) {
//        this.console = console;
//    }

    public TemplateConsole getConsole() {
        return console;
    }

    public void set(String propertyName, String value) {
        if (value == null || value.trim().isEmpty()) {
            value = null;
        }
        get(propertyName).setValue(value);
    }

    public void loadProperties(File file) {
        Properties p = new Properties();
        try {
            p.load(new FileReader(file));
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        for (Map.Entry<Object, Object> e : p.entrySet()) {
            set((String) e.getKey(), (String) e.getValue());
        }
    }

    public String getModuleName() {
        return get("vrModuleName").get();
    }

    public String getModuleVersion() {
        return get("vrModuleVersion").get();
    }

    public String getProjectName() {
        return get("vrProjectName").get();
    }

    public String getProjectVersion() {
        return get("vrProjectVersion").get();
    }

    public String getProjectGroup() {
        return get("vrProjectGroup").get();
    }





    public Properties getProperties() {
        Properties p = new Properties();
        for (Map.Entry<String, ProjectProperty> entry : config.entrySet()) {
            ProjectProperty value = entry.getValue();
            if(entry.getKey()!=null && value!=null) {
                if(value.getValue()!=null) {
                    p.put(entry.getKey(), value.getValue());
                }
            }else{
                System.out.print("");
            }
        }
        return p;
    }

    public ProjectProperty get(String name) {
        ProjectProperty projectProperty = config.get(name);
        if (projectProperty == null) {
            projectProperty = new ProjectProperty(name, name, null, null, ValidatorFactory.STRING, this);
            config.put(name, projectProperty);
        }
        return projectProperty;
    }

    public File getProjectRootFolder() {
        return new File(get("vrProjectRootFolder").get());
    }

}
