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

    public VrFSEntry(String filterName, String filterType, String mountPoint, String linkPath) {
        this.filterName = filterName;
        this.filterType = filterType;
        this.mountPoint = mountPoint;
        this.linkPath = linkPath;
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
        if (mountPoint == null) {
            throw new IllegalArgumentException("Invalid path");
        }
        mountPoint = mountPoint.trim();
        if (mountPoint.isEmpty()) {
            throw new IllegalArgumentException("Invalid path");
        }
//        if (mountPoint.endsWith("/")) {
//            mountPoint = mountPoint.substring(0, mountPoint.length() - 1);
//        }
//        if (!mountPoint.startsWith("/")) {
//            mountPoint = "/" + mountPoint;
//        }
        this.mountPoint = mountPoint;
    }

    public String getLinkPath() {
        return linkPath;
    }

    public void setLinkPath(String linkPath) {
        if (linkPath == null) {
            throw new IllegalArgumentException("Invalid path");
        }
        linkPath = linkPath.trim();
        if (linkPath.isEmpty()) {
            throw new IllegalArgumentException("Invalid path");
        }
        if (linkPath.endsWith("/")) {
            linkPath = linkPath.substring(0, linkPath.length() - 1);
        }
        if (!linkPath.startsWith("/")) {
            linkPath = "/" + linkPath;
        }
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
