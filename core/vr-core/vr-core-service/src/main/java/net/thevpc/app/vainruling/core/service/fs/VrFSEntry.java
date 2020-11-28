/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.fs;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrFSEntry {
    private String allowedUsers;
    private String mountPoint;
    private String linkPath;

    public VrFSEntry() {
    }

    public VrFSEntry(String mountPoint, String linkPath, String allowedUsers) {
        this.allowedUsers = allowedUsers;
        this.mountPoint = mountPoint;
        this.linkPath = linkPath;
    }

    public String getAllowedUsers() {
        return allowedUsers;
    }

    public void setAllowedUsers(String allowedUsers) {
        this.allowedUsers = allowedUsers;
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

        if (allowedUsers != null ? !allowedUsers.equals(vrFSEntry.allowedUsers) : vrFSEntry.allowedUsers != null) return false;
        if (mountPoint != null ? !mountPoint.equals(vrFSEntry.mountPoint) : vrFSEntry.mountPoint != null) return false;
        return linkPath != null ? linkPath.equals(vrFSEntry.linkPath) : vrFSEntry.linkPath == null;

    }

    @Override
    public int hashCode() {
        int result = allowedUsers != null ? allowedUsers.hashCode() : 0;
        result = 31 * result + (mountPoint != null ? mountPoint.hashCode() : 0);
        result = 31 * result + (linkPath != null ? linkPath.hashCode() : 0);
        return result;
    }
}
