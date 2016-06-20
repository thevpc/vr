/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UCtrlData;
import net.vpc.app.vainruling.core.web.UCtrlProvider;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.menu.VRMenuDef;
import net.vpc.app.vainruling.core.web.menu.VRMenuDefFactory;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.streams.PathInfo;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.*;
import net.vpc.upa.UPA;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.context.annotation.Scope;

import javax.faces.bean.ManagedBean;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentsUtils {

    public static final Map<String, String> extensionsToCss = new HashMap<String, String>();
    private static final Logger log = Logger.getLogger(DocumentsUtils.class.getName());

    static {
        extensionsToCss.put("csv", "file-csv");

        extensionsToCss.put("txt", "file-txt");
        extensionsToCss.put("properties", "file-txt");

        extensionsToCss.put("log", "file-log");

        extensionsToCss.put("xls", "file-xls");
        extensionsToCss.put("xlsx", "file-xls");
        extensionsToCss.put("ods", "file-xls");

        extensionsToCss.put("doc", "file-doc");
        extensionsToCss.put("docx", "file-doc");
        extensionsToCss.put("odt", "file-doc");

        extensionsToCss.put("zip", "file-zip");
        extensionsToCss.put("tar", "file-zip");
        extensionsToCss.put("rar", "file-zip");

        extensionsToCss.put("pdf", "file-pdf");
        extensionsToCss.put("xml", "file-xml");
        extensionsToCss.put("css", "file-css");
        extensionsToCss.put("html", "file-html");

        extensionsToCss.put("png", "file-img");
        extensionsToCss.put("gif", "file-img");
        extensionsToCss.put("jpg", "file-img");
        extensionsToCss.put("jpeg", "file-img");
    }


    public static String evalVFileDesc(VFile file) {
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




    public static VirtualFileSystem createFS() {
        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
        VirtualFileSystem rootfs = fsp.getFileSystem();
        VirtualFileSystem userfs = rootfs.filter(null);
        return userfs;
    }



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
            ret.add(DocumentsUtils.createFileInfo(a.getName(), a));
        }
        Collections.sort(ret);
        if (!"/".equals(curr.getPath())) {
            ret.add(0, DocumentsUtils.createFileInfo(CorePlugin.FOLDER_BACK, curr.getParentFile()));
        }
        return ret;
    }


    public static VFileInfo createFileInfo(VFile file) {
        return createFileInfo(file.getName(), file);
    }

    public static VFileInfo createFileInfo(String name, VFile file) {
        String iconCss = "file";
        String labelCss = "";
        long downloads = 0;
        boolean homeFolder=false;
        boolean backFolder = false;
        if (file.isDirectory()) {
            iconCss = "folder";
            homeFolder = file.getPath().equals("/" + CorePlugin.FOLDER_MY_DOCUMENTS);
            if(homeFolder){
                iconCss="home";
            }
            backFolder=CorePlugin.FOLDER_BACK.equals(name);
            if(backFolder){
                iconCss="parent";
            }
        } else {
            String n = file.getName().toLowerCase();
            String e = PathInfo.create(n).getExtensionPart();
            iconCss = extensionsToCss.get(e);
            if (iconCss == null) {
                iconCss = "file";
            }
            CorePlugin fsp = VrApp.getBean(CorePlugin.class);
            downloads = fsp.getDownloadsCount(file);
        }
        String desc = backFolder ? "" : evalVFileDesc(file);
        labelCss=homeFolder ? "color:#349dc9;font-weight: bold;":backFolder ?"color:#9e9e9e;":"";
        return new VFileInfo(name, file, labelCss,iconCss, downloads, desc);
    }


}
