/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.common.io.PathInfo;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileACL;
import net.vpc.common.vfs.VirtualFileSystem;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentsUtils {

    private static final Logger log = Logger.getLogger(DocumentsUtils.class.getName());



    public static String evalVFileDesc(VFile file) {
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


//    public static VirtualFileSystem createFS() {
//        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
//        VirtualFileSystem rootfs = fsp.getRootFileSystem();
//        VirtualFileSystem userfs = rootfs.filter(null);
//        return userfs;
//    }


    public static StreamedContent getContent(VFileInfo i) {
        InputStream stream = null;
        try {
            if (i.getFile().isDirectory()) {
                //should zip it?
            } else {
                final VFile f = i.getFile();
                CorePlugin fsp = VrApp.getBean(CorePlugin.class);
                fsp.markDownloaded(f);
                stream = f.getInputStream();
                return new DefaultStreamedContent(stream, f.probeContentType(), f.getName());
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentsUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static List<VFileInfo> loadFiles(VFile curr) {
        VFile[] all = curr.getFileSystem().listFiles(curr.getPath());
        ArrayList<VFileInfo> ret = new ArrayList<>();
        for (VFile a : all) {
            ret.add(DocumentsUtils.createFileInfo(a));
        }
        Collections.sort(ret);
        if (!"/".equals(curr.getPath())) {
            ret.add(0, DocumentsUtils.createFileInfo(CorePlugin.FOLDER_BACK, VFileKind.BACK, curr.getParentFile()));
        }
        return ret;
    }


    public static VFileInfo createFileInfo(VFile file) {
        return createFileInfo(file.getName(),VFileKind.ORDINARY, file);
    }

    public static VFileInfo createFileInfo(String name, VFileKind kind, VFile file) {
        String iconCss = "file";
        String labelCss = "";
        long downloads = 0;
        boolean homeFolder = false;
        boolean backFolder = false;
        if (file.isDirectory()) {
            iconCss = "folder";
            homeFolder = file.getPath().equals("/" + CorePlugin.FOLDER_MY_DOCUMENTS);
            if (homeFolder) {
                iconCss = "home";
            }
            backFolder = CorePlugin.FOLDER_BACK.equals(name);
            if (backFolder) {
                iconCss = "parent";
            }
        } else {
            String n = file.getName().toLowerCase();
            String e = PathInfo.create(n).getExtensionPart();
            iconCss = Vr.extensionsToCss.get(e);
            if (iconCss == null) {
                iconCss = "file";
            }
            CorePlugin fsp = VrApp.getBean(CorePlugin.class);
            downloads = fsp.getDownloadsCount(file);
        }
        String desc = backFolder ? "" : evalVFileDesc(file);
        labelCss = homeFolder ? "color:#349dc9;font-weight: bold;" : backFolder ? "color:#9e9e9e;" : "";
        if(!backFolder) {
            VirtualFileACL acl = file.getACL();
            if (acl != null) {
                //should never be null
                String view = acl.getProperty("ViewFormat");
                if (view != null) {
                    ViewFormat format = null;
                    try {
                        format = VrUtils.parseJSONObject(view, ViewFormat.class);
                    } catch (Exception ex) {
                        //ignore any error
                    }
                    if (format != null) {
                        if (StringUtils.isEmpty(format.getIconCss())) {
                            iconCss=format.getIconCss();
                        }
                        if (StringUtils.isEmpty(format.getIconCss())) {
                            iconCss=format.getIconCss();
                        }
                    }
                }
            }
        }
        return new VFileInfo(name,kind, file, labelCss, iconCss, downloads, desc);
    }
    public static class ViewFormat{
        private String labelCss;
        private String iconCss;

        public String getLabelCss() {
            return labelCss;
        }

        public void setLabelCss(String labelCss) {
            this.labelCss = labelCss;
        }

        public String getIconCss() {
            return iconCss;
        }

        public void setIconCss(String iconCss) {
            this.iconCss = iconCss;
        }
    }


}
