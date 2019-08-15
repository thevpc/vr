package net.vpc.app.vainruling.core.service.plugins;

import net.vpc.common.strings.StringUtils;

/**
 * Created by vpc on 8/9/16.
 */
public class PluginOrganization {
    private String name;
    private String url;

    public PluginOrganization(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public PluginOrganization(PluginOrganization o) {
        updateNonNull(o);
    }

    public void updateNonNull(PluginOrganization o) {
        if (o != null) {
            if (!StringUtils.isBlank(o.name)) {
                this.name = o.name;
            }
            if (!StringUtils.isBlank(o.url)) {
                this.url = o.url;
            }
        }
    }

    public PluginOrganization() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
