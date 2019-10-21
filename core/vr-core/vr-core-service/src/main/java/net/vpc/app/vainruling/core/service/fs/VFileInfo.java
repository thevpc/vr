/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.fs;

import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileACL;

import java.util.Date;

/**
 * @author taha.bensalah@gmail.com
 */
public class VFileInfo implements Comparable<VFileInfo> {

    private VFile file;
    private VFileKind kind;
    private String name;
    private String longName;
    private String iconCss;
    private String labelCss;
    private String desc;
    private boolean selectable;
    private boolean selected;
    private long downloads;
    private String aclDirCreateDirectory;
    private String aclDirCreateFile;
    private String aclDirRemoveDirectory;
    private String aclDirRemoveFile;
    private String aclDirList;
    private String aclWriteFile;
    private String aclReadFile;
    private String aclRemove;
    private String aclOwner;
    private String aclSimpleRead;
    private String aclSimpleWrite;
    private String shareProfiles;
    private String shareName;
    private boolean aclDirPropagateACL;
    private boolean aclDirPropagateOwner;
    private boolean sharable;
    private boolean advanced;

    public VFileInfo(String name, VFileKind kind, VFile file, String labelCss, String iconCss, long downloads, String desc) {
        this.name = name;
        this.kind = kind;
        this.file = file;
        this.iconCss = iconCss;
        this.labelCss = labelCss;
        this.downloads = downloads;
        this.desc = desc;
        this.longName = name;
        switch (kind) {
            case BACK: {
                selectable = false;
                break;
            }
            default: {
                selectable = true;
            }
        }
        readACL();
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public final void reset() {
        aclOwner = null;
        aclReadFile = null;
        aclWriteFile = null;
        aclRemove = null;
        aclDirCreateDirectory = null;
        aclDirCreateFile = null;
        aclDirRemoveDirectory = null;
        aclDirRemoveFile = null;
        aclDirList = null;
        aclDirPropagateACL = false;
        aclDirPropagateOwner = false;
    }

    public final void readACL() {
        reset();
        VirtualFileACL acl = file.getACL();
        if (acl != null) {
            aclOwner = acl.getOwner();
            if (file.isDirectory()) {
                aclDirPropagateACL = acl.isPropagateACL();
                aclDirPropagateOwner = acl.isPropagateOwner();
            }
            if (advanced) {
                aclReadFile = acl.getPermissionReadFile();
                aclWriteFile = acl.getPermissionWriteFile();
                aclRemove = acl.getPermissionRemove();
                if (file.isDirectory()) {
                    aclDirCreateDirectory = acl.getPermissionCreateDirectory();
                    aclDirCreateFile = acl.getPermissionCreateFile();
                    aclDirRemoveDirectory = acl.getPermissionRemoveDirectory();
                    aclDirRemoveFile = acl.getPermissionRemoveFile();
                    aclDirList = acl.getPermissionListDirectory();
                }
            } else {
                aclSimpleRead = acl.getPermissionListDirectory();
                aclSimpleWrite = acl.getPermissionCreateFile();
            }
        }
    }

    public void writeACL() {
        VirtualFileACL acl = file.getACL();
        if (acl != null && !acl.isReadOnly()) {
            acl.setAutoSave(false);
            acl.setOwner(aclOwner);
            if (advanced) {
                acl.setPermissionReadFile(aclReadFile);
                acl.setPermissionListDirectory(aclDirList);

                acl.setPermissionCreateDirectory(aclDirCreateDirectory);
                acl.setPermissionCreateFile(aclDirCreateFile);
                acl.setPermissionWriteFile(aclWriteFile);
                acl.setPermissionRemoveDirectory(aclDirRemoveDirectory);
                acl.setPermissionRemoveFile(aclDirRemoveFile);
                acl.setPermissionRemove(aclRemove);

                acl.setPropagateACL(aclDirPropagateACL);
                acl.setPropagateOwner(aclDirPropagateOwner);
            } else {
                acl.setPermissionReadFile(aclSimpleRead);
                acl.setPermissionListDirectory(aclSimpleRead);

                acl.setPermissionCreateDirectory(aclSimpleWrite);
                acl.setPermissionCreateFile(aclSimpleWrite);
                acl.setPermissionWriteFile(aclSimpleWrite);
                acl.setPermissionRemoveDirectory(aclSimpleWrite);
                acl.setPermissionRemoveFile(aclSimpleWrite);
                acl.setPermissionRemove(aclSimpleWrite);

                acl.setPropagateACL(aclDirPropagateACL);
                acl.setPropagateOwner(aclDirPropagateOwner);
            }
            acl.save();
        }
    }

    public boolean isAcceptAdvanced() {
        if (file.isDirectory()) {
            String a = aclDirCreateDirectory;
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclDirCreateDirectory))) {
                return true;
            }
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclDirCreateFile))) {
                return true;
            }
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclReadFile))) {
                return true;
            }
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclWriteFile))) {
                return true;
            }
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclDirRemoveDirectory))) {
                return true;
            }
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclDirRemoveFile))) {
                return true;
            }
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclRemove))) {
                return true;
            }
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclDirList))) {
                return true;
            }
        } else if (file.isFile()) {
            String a = aclReadFile;
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclReadFile))) {
                return true;
            }
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclWriteFile))) {
                return true;
            }
            if (!StringUtils.trim(a).equals(StringUtils.trim(aclRemove))) {
                return true;
            }
        }
        return false;
    }

    public String getAclRemove() {
        return aclRemove;
    }

    public void setAclRemove(String aclRemove) {
        this.aclRemove = aclRemove;
    }

    public VFileKind getKind() {
        return kind;
    }

    public VFileInfo setKind(VFileKind kind) {
        this.kind = kind;
        return this;
    }

    public String getLabelCss() {
        return labelCss;
    }

    public String getDesc() {
        return desc;
    }

    public long length() {
        if (file.isFile()) {
            return file.length();
        }
        if (file.isDirectory()) {
            VFile[] files = file.listFiles();
            return files == null ? 0 : files.length;
        }
        return 0;
    }

    public String lengthDesc() {
        if (file.isFile()) {
            return VrUtils.formatFileSize(file.length());
        }
        if (file.isDirectory()) {
            VFile[] files = file.listFiles();
            int f = 0;
            int d = 0;
            if (files != null) {
                for (VFile ff : files) {
                    if (ff.isDirectory()) {
                        d++;
                    } else {
                        f++;
                    }
                }
            }
            if (f == 0 && d == 0) {
                return "vide";
            }
            StringBuilder sb = new StringBuilder();
            if (d > 0) {
                sb.append(d).append(" rep.");
            }
            if (f > 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(f).append(" fich.");
            }
            return sb.toString();
        }
        return "";
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return file.getPath();
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public VFile getFile() {
        return file;
    }

    public void setFile(VFile file) {
        this.file = file;
    }

    public Date getLastModifiedDate() {
        return new Date(file.lastModified());
    }

    public String getIconCss() {
        return iconCss;
    }

    public void setIconCss(String iconCss) {
        this.iconCss = iconCss;
    }

    @Override
    public int compareTo(VFileInfo o) {
        if (file.isDirectory() != o.file.isDirectory()) {
            return file.isDirectory() ? -1 : 1;
        }
        return file.getName().compareToIgnoreCase(o.file.getName());
    }

    public long getDownloads() {
        return downloads;
    }

    public void setDownloads(long downloads) {
        this.downloads = downloads;
    }

    public String getAclDirCreateDirectory() {
        return aclDirCreateDirectory;
    }

    public void setAclDirCreateDirectory(String aclDirCreateDirectory) {
        this.aclDirCreateDirectory = aclDirCreateDirectory;
    }

    public String getAclDirCreateFile() {
        return aclDirCreateFile;
    }

    public void setAclDirCreateFile(String aclDirCreateFile) {
        this.aclDirCreateFile = aclDirCreateFile;
    }

    public String getAclDirRemoveDirectory() {
        return aclDirRemoveDirectory;
    }

    public void setAclDirRemoveDirectory(String aclDirRemoveDirectory) {
        this.aclDirRemoveDirectory = aclDirRemoveDirectory;
    }

    public String getAclDirRemoveFile() {
        return aclDirRemoveFile;
    }

    public void setAclDirRemoveFile(String aclDirRemoveFile) {
        this.aclDirRemoveFile = aclDirRemoveFile;
    }

    public String getAclWriteFile() {
        return aclWriteFile;
    }

    public void setAclWriteFile(String aclWriteFile) {
        this.aclWriteFile = aclWriteFile;
    }

    public String getAclReadFile() {
        return aclReadFile;
    }

    public void setAclReadFile(String aclReadFile) {
        this.aclReadFile = aclReadFile;
    }

    public String getAclDirList() {
        return aclDirList;
    }

    public void setAclDirList(String aclDirList) {
        this.aclDirList = aclDirList;
    }

    public String getAclOwner() {
        return aclOwner;
    }

    public void setAclOwner(String aclOwner) {
        this.aclOwner = aclOwner;
    }

    public boolean isAclDirPropagateACL() {
        return aclDirPropagateACL;
    }

    public void setAclDirPropagateACL(boolean aclDirPropagateACL) {
        this.aclDirPropagateACL = aclDirPropagateACL;
    }

    public boolean isAclDirPropagateOwner() {
        return aclDirPropagateOwner;
    }

    public void setAclDirPropagateOwner(boolean aclDirPropagateOwner) {
        this.aclDirPropagateOwner = aclDirPropagateOwner;
    }

    public String getShareProfiles() {
        return shareProfiles;
    }

    public void setShareProfiles(String shareProfiles) {
        this.shareProfiles = shareProfiles;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public boolean isSharable() {
        return sharable;
    }

    public void setSharable(boolean sharable) {
        this.sharable = sharable;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }

    public String getAclSimpleRead() {
        return aclSimpleRead;
    }

    public void setAclSimpleRead(String aclSimpleRead) {
        this.aclSimpleRead = aclSimpleRead;
    }

    public String getAclSimpleWrite() {
        return aclSimpleWrite;
    }

    public void setAclSimpleWrite(String aclSimpleWrite) {
        this.aclSimpleWrite = aclSimpleWrite;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

}
