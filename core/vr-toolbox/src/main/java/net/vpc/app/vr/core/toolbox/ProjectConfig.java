package net.vpc.app.vr.core.toolbox;

import java.io.File;
import java.util.Properties;

public class ProjectConfig {
    private Properties config=new Properties();
    private IOTemplater io;

    public ProjectConfig() {
        config.setProperty("projectNameDefaultValue","MyProject");
        config.setProperty("projectGroupDefaultValue","com.mycompany");
        config.setProperty("projectVersionDefaultValue","1.0");
        config.setProperty("moduleNameDefaultValue","MyModule");
        config.setProperty("moduleVersionDefaultValue","1.0");
    }

    public void unset(String propertyName){
        set(propertyName,null);
    }

    public void set(String propertyName, String value){
        if(value==null || value.trim().isEmpty()){
            config.remove(propertyName);
        }else{
            config.setProperty(propertyName,value);
        }
    }

    public String getModuleName(){
        return getOrAskForId("moduleName");
    }

    public String getModuleVersion(){
        return getOrAskForId("moduleVersion");
    }

    public String getProjectName(){
        return getOrAskForId("projectName");
    }

    public String getProjectVersion(){
        return getOrAskForId("projectVersion");
    }

    public String getProjectGroup(){
        return getOrAskForId("projectGroup");
    }

    public String getOrAskForId(String id){
        String defaultValue=config.getProperty(id+"DefaultValue");
        return get(id,p->io.askForString(id,ValidatorFactory.ID,defaultValue));
    }

    public File getProjectRootFolder(){
        return new File(get("projectRootFolder",x->"."));
    }

    public String get(String name){
        return get(name,null);
    }

    public String get(String name,PropertyProvider p){
        String f = config.getProperty(name);
        if(f==null && p!=null){
            f=p.getProperty(name);
            set(name,f);
        }
        return f;
    }
}
