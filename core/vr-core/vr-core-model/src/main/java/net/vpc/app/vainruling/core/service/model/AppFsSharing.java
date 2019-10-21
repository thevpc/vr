/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.config.*;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.user.fullName, this.mountPath")
@Path("/Admin/Security")
@Properties(
        {
            @Property(name = "ui.auto-filter.department", value = "{expr='this.user.department',order=1}"),
            @Property(name = "ui.auto-filter.user", value = "{expr='this.user',order=2}")
        })
public class AppFsSharing {

    @Id @Sequence
    private int id;

    @Summary
    private AppUser user;

    @Summary
    private String mountPath;

    @Main
    private String sharedPath;

    @Summary
    private String allowedUsers;

    private boolean disabled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getMountPath() {
        return mountPath;
    }

    public void setMountPath(String mountPath) {
        this.mountPath = mountPath;
    }

    public String getSharedPath() {
        return sharedPath;
    }

    public void setSharedPath(String sharedPath) {
        this.sharedPath = sharedPath;
    }

    public String getAllowedUsers() {
        return allowedUsers;
    }

    public void setAllowedUsers(String allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
