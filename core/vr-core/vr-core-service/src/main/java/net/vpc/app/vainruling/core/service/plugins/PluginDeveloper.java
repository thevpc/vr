package net.vpc.app.vainruling.core.service.plugins;

import net.vpc.common.strings.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vpc on 8/9/16.
 */
public class PluginDeveloper {
    private String id;
    private String name;
    private String email;
    private String url;
    private PluginOrganization organization = new PluginOrganization();
    private Set<String> roles = new HashSet<String>();

    public PluginDeveloper() {
    }

    public PluginDeveloper(String id, String name, String email, String url, PluginOrganization organization, List<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.url = url;
        this.organization = new PluginOrganization(organization);
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    public PluginDeveloper(PluginDeveloper other) {
        updateNonNull(other);
    }

    public void updateNonNull(PluginDeveloper other) {
        if (other != null) {
            if (!StringUtils.isEmpty(other.id)) {
                this.id = other.id;
            }
            if (!StringUtils.isEmpty(other.name)) {
                this.name = other.name;
            }
            if (!StringUtils.isEmpty(other.email)) {
                this.email = other.email;
            }
            if (!StringUtils.isEmpty(other.url)) {
                this.url = other.url;
            }
            this.organization.updateNonNull(other.organization);
            if (roles != null) {
                this.roles.addAll(other.roles);
            }
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PluginOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(PluginOrganization organization) {
        this.organization = organization;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
