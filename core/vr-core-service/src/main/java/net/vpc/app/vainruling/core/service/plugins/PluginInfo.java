package net.vpc.app.vainruling.core.service.plugins;

import java.util.*;

/**
 * Created by vpc on 8/9/16.
 */
public abstract class PluginInfo {
    private String id;
    private String name;
    private String description;
    private String version;
    private Set<String> dependencies = new HashSet<>();
    private String url;
    private List<PluginDeveloper> developers = new ArrayList<>();
    private PluginOrganization organization = new PluginOrganization();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSimpleName() {
        String n=getName();
        if(n!=null && n.indexOf(":")>0){
            String n2 = n.substring(n.indexOf(":" + 1));
            return n2;
        }
        return name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<PluginDeveloper> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<PluginDeveloper> developers) {
        this.developers = developers;
    }

    public PluginOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(PluginOrganization organization) {
        this.organization = organization;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
