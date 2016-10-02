/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.fs;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrFSEntry {
    private String filterName;
    private String filterType;
    private String mountPoint;
    private String linkPath;

    public VrFSEntry() {
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public String getLinkPath() {
        return linkPath;
    }

    public void setLinkPath(String linkPath) {
        this.linkPath = linkPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VrFSEntry vrFSEntry = (VrFSEntry) o;

        if (filterName != null ? !filterName.equals(vrFSEntry.filterName) : vrFSEntry.filterName != null) return false;
        if (filterType != null ? !filterType.equals(vrFSEntry.filterType) : vrFSEntry.filterType != null) return false;
        if (mountPoint != null ? !mountPoint.equals(vrFSEntry.mountPoint) : vrFSEntry.mountPoint != null) return false;
        return linkPath != null ? linkPath.equals(vrFSEntry.linkPath) : vrFSEntry.linkPath == null;

    }

    @Override
    public int hashCode() {
        int result = filterName != null ? filterName.hashCode() : 0;
        result = 31 * result + (filterType != null ? filterType.hashCode() : 0);
        result = 31 * result + (mountPoint != null ? mountPoint.hashCode() : 0);
        result = 31 * result + (linkPath != null ? linkPath.hashCode() : 0);
        return result;
    }
}
