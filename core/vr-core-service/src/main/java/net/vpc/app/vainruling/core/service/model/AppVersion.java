/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.types.Timestamp;

/**
 * @author vpc
 */
@Entity(listOrder = "serviceName")
@Path("Admin")
public class AppVersion {

    @Id
    @Field(modifiers = {UserFieldModifier.MAIN})
    private String serviceName;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private String serviceVersion;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private boolean active;
    @Field(defaultValue = "true", modifiers = {UserFieldModifier.SUMMARY})
    private boolean coherent;
    private Timestamp installDate;
    private Timestamp updateDate;

    public AppVersion() {
    }

    public AppVersion(String name) {
        this.serviceName = name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Timestamp installDate) {
        this.installDate = installDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isCoherent() {
        return coherent;
    }

    public void setCoherent(boolean coherent) {
        this.coherent = coherent;
    }

}
