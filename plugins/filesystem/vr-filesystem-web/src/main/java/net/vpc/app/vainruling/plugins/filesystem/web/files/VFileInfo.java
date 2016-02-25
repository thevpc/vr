/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.web.files;

import java.util.Date;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.common.vfs.VFile;

/**
 *
 * @author vpc
 */
public class VFileInfo implements Comparable<VFileInfo> {

    private String name;
    private String css;
    private String desc;
    VFile file;
    private boolean selected;
    private long downloads;

    public VFileInfo(String name, VFile file, String css, long downloads, String desc) {
        this.name = name;
        this.file = file;
        this.css = css;
        this.downloads = downloads;
        this.desc = desc;
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
            return VrHelper.formatFileSize(file.length());
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

    public Date getLastModifiedDate() {
        return new Date(file.lastModified());
    }

    public void setFile(VFile file) {
        this.file = file;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
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
