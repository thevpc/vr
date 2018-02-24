/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.config.*;
import net.vpc.upa.types.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.serviceName")
@Path("Admin")
public class AppVersion {

    @Path("Main")
    @Id
    @Main
    private String serviceName;
    @Summary
    private String serviceVersion;
    @Summary
    private boolean active;
    @Summary
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppVersion that = (AppVersion) o;

        if (serviceName != null ? !serviceName.equals(that.serviceName) : that.serviceName != null) return false;
        return serviceVersion != null ? serviceVersion.equals(that.serviceVersion) : that.serviceVersion == null;
    }

    @Override
    public int hashCode() {
        int result = serviceName != null ? serviceName.hashCode() : 0;
        result = 31 * result + (serviceVersion != null ? serviceVersion.hashCode() : 0);
        return result;
    }
}
