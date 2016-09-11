/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.common.vfs.VFile;

import java.util.Date;

/**
 * @author taha.bensalah@gmail.com
 */
public class VFileInfo implements Comparable<VFileInfo> {

    VFile file;
    private String name;
    private String iconCss;
    private String labelCss;
    private String desc;
    private boolean selected;
    private long downloads;

    public VFileInfo(String name, VFile file, String labelCss, String iconCss, long downloads, String desc) {
        this.name = name;
        this.file = file;
        this.iconCss = iconCss;
        this.labelCss = labelCss;
        this.downloads = downloads;
        this.desc = desc;
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

}
