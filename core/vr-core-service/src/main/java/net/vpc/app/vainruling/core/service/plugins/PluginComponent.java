package net.vpc.app.vainruling.core.service.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by vpc on 8/11/16.
 */
public class PluginComponent extends PluginInfo{
    private URL runtimeURL;
    private String bundleId;
    private PluginBundle bundle;
    private String componentType;
    private String bundleName;
    private String bundleDescription;

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleDescription() {
        return bundleDescription;
    }

    public void setBundleDescription(String bundleDescription) {
        this.bundleDescription = bundleDescription;
    }

    public PluginBundle getBundle() {
        return bundle;
    }

    public void setBundle(PluginBundle bundle) {
        this.bundle = bundle;
    }

    public URL getRuntimeURL() {
        return runtimeURL;
    }

    public void setRuntimeURL(URL runtimeURL) {
        this.runtimeURL = runtimeURL;
    }

    public URL getRuntimeURL(String path) throws MalformedURLException {
        String s = runtimeURL.toString();
        String suffix = "/META-INF/vr-plugin.properties";
        if(s.endsWith(suffix)){
            return new URL(s.substring(0,s.length()-suffix.length())+path);
        }
        return null;
    }

    public static PluginComponent parsePluginComponent(URL url) throws IOException {
        InputStream is = url.openStream();
        try {
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                if (!"true".equals(p.getProperty("vr-plugin"))) {
                    return null;
                }
                String component = p.getProperty("vr-plugin.component-type");
                PluginComponent i = new PluginComponent();
                i.setRuntimeURL(url);
                i.setId(p.getProperty("project.id"));
                i.setBundleId(p.getProperty("vr-plugin.id"));
                i.setBundleName(p.getProperty("vr-plugin.name"));
                i.setBundleDescription(p.getProperty("vr-plugin.description"));
                i.setVersion(p.getProperty("project.version"));
                i.setComponentType(component);
                i.setOrganization(new PluginOrganization(
                        p.getProperty("project.organization.name"),
                        p.getProperty("project.organization.url")
                ));
                int index = 0;
                while (p.getProperty("project.developers[" + index + "].id") != null) {
                    String prefix = "project.developers[" + index + "]";
                    PluginDeveloper d = new PluginDeveloper();
                    d.setId(p.getProperty(prefix + ".id"));
                    d.setName(p.getProperty(prefix + ".name"));
                    d.setUrl(p.getProperty(prefix + ".url"));
                    d.setOrganization(
                            new PluginOrganization(
                                    p.getProperty(prefix + ".organization.name"),
                                    p.getProperty(prefix + ".organization.url")
                            )
                    );
                    String roles = p.getProperty(prefix + ".roles");
                    if (roles != null) {
                        d.getRoles().addAll(Arrays.asList(roles.split(" ")));
                    }
                    i.getDevelopers().add(d);
                    index++;
                }
                index=0;
                String dependenciesCompile = p.getProperty("project.dependencies.compile");
                if(dependenciesCompile!=null){
                    for (String gav : dependenciesCompile.split(";")) {
                        i.getDependencies().add(gav);
                    }
                }
                return i;
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return null;
    }


    public static String parsePluginInfoId(URL url) throws IOException {
        InputStream is = url.openStream();
        if (is != null) {
            try {
                Properties p = new Properties();
                p.load(is);
                if (!"true".equals(p.getProperty("vr-plugin"))) {
                    return null;
                }

                return p.getProperty("project.id");
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return null;
    }
}
