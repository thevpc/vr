package net.vpc.app.vainruling.core.service.fs;

import net.vpc.common.vfs.VFileType;

public class FileInfo {
    private String name;
    private VFileType type;
    private long lastModif;
    private FileInfo[] children;

    public FileInfo(String name, VFileType type, long lastModif) {
        this.name = name;
        this.type = type;
        this.lastModif = lastModif;
    }

    public FileInfo() {
    }

    public FileInfo[] getChildren() {
        return children;
    }

    public void setChildren(FileInfo[] children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VFileType getType() {
        return type;
    }

    public void setType(VFileType type) {
        this.type = type;
    }

    public long getLastModif() {
        return lastModif;
    }

    public void setLastModif(long lastModif) {
        this.lastModif = lastModif;
    }
}
