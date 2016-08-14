package net.vpc.app.vainruling.core.service;

import net.vpc.common.strings.StringUtils;

import java.util.*;

/**
 * Created by vpc on 8/11/16.
 */
public class PluginBundle extends PluginInfo{
    private static final Map<String,Integer> componentTypeOrders=new HashMap<>();
    static{
        componentTypeOrders.put("service",0);
        componentTypeOrders.put("web",1);
    }
    private static class PluginComponentComparator implements Comparator<PluginComponent>{
        @Override
        public int compare(PluginComponent o1, PluginComponent o2) {
            Integer a = componentTypeOrders.get(o1.getComponentType());
            Integer b = componentTypeOrders.get(o2.getComponentType());
            if(a==null){
                a=100;
            }
            if(b==null){
                b=100;
            }
            if(!a.equals(b)){
                return Integer.compare(a,b);
            }
            return o1.getId().compareTo(o2.getId());
        }
    }
    private static final PluginComponentComparator COMP=new PluginComponentComparator();
    private List<PluginComponent> components = new ArrayList<PluginComponent>();
    private Set<String> bundleDependencies = new HashSet<>();
    private Set<String> extraDependencies = new HashSet<>();

    public List<PluginComponent> getComponents() {
        return components;
    }

//    public void setChildren(List<PluginComponent> components) {
//        this.components = components;
//    }

    public void addComponent(PluginComponent component) {
        if (component != null) {
//            if (!StringUtils.isEmpty(component.getId())) {
//                setId(component.getId());
//            }
            if (!StringUtils.isEmpty(component.getBundleName())) {
                this.setName(component.getBundleName());
            }else if (StringUtils.isEmpty(getName()) && !StringUtils.isEmpty(component.getName())) {
                if(
                        !component.getId().endsWith(":"+component.getName())
                        && !!component.getId().equals(component.getName())
                        ) {
                    this.setName(component.getName());
                }else if(StringUtils.isEmpty(getName())){
                    if(getId().contains(":")){
                        setName(getId().substring(getId().indexOf(':')+1));
                    }else{
                        setName(getId());
                    }
                }
            }
            if (!StringUtils.isEmpty(component.getBundleDescription())) {
                this.setDescription(component.getBundleDescription());
            }else if (StringUtils.isEmpty(getDescription()) && !StringUtils.isEmpty(component.getDescription())) {
                this.setDescription(component.getDescription());
            }
            if (!StringUtils.isEmpty(component.getUrl())) {
                this.setUrl(component.getUrl());
            }
            this.getOrganization().updateNonNull(component.getOrganization());
            //reeval devs
            Map<String, PluginDeveloper> dev = new HashMap<>();
            for (PluginDeveloper d : getDevelopers()) {
                dev.put(d.getId(), d);
            }
            for (PluginDeveloper d : component.getDevelopers()) {
                PluginDeveloper d2 = dev.get(d.getId());
                if (d2 == null) {
                    dev.put(d.getId(), d);
                } else {
                    d2.updateNonNull(d);
                }
            }
            this.getDevelopers().clear();
            this.getDevelopers().addAll(dev.values());

            this.getDependencies().addAll(component.getDependencies());
            this.getDependencies().add(component.getId()+":"+component.getVersion());
            getComponents().add(component);
            component.setBundle(this);

            //reeval version
            Set<String> vers = new HashSet<>();
            for (PluginComponent d : getComponents()) {
                if(!StringUtils.isEmpty(d.getVersion())) {
                    vers.add(d.getVersion());
                }
            }
            setVersion(StringUtils.listToString(vers,", "));
            Collections.sort(getComponents(),COMP);
        }
    }

    public Set<String> getBundleDependencies() {
        return bundleDependencies;
    }

    public void setBundleDependencies(Set<String> bundleDependencies) {
        this.bundleDependencies = bundleDependencies;
    }

    public Set<String> getExtraDependencies() {
        return extraDependencies;
    }

    public void setExtraDependencies(Set<String> extraDependencies) {
        this.extraDependencies = extraDependencies;
    }
}
