/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

import java.io.File;
import java.util.Date;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.util.JsfCtrl;
import net.vpc.app.vainruling.core.service.fs.FileSystemService;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author vpc
 */
public class FSWebUtils {

    public static String getUserAbsoluteWebPath(int id, String path) {
        VFile f = getUserAbsoluteFile(id, path);
        if (f == null) {
            return null;
        }
        return getAbsoluteWebPath(f.getPath());
    }

    public static String getUserAppWebPath(int userId, String path) {
        VFile f = getUserAbsoluteFile(userId, path);
        if (f == null) {
            return null;
        }
        return getAppWebPath(f.getPath());
    }

    public static String getAbsoluteWebPath(String virtualAbsolutePath) {
        if (virtualAbsolutePath == null) {
            return null;
        }
        return VrApp.getBean(JsfCtrl.class).getContext() + "/fs/" + virtualAbsolutePath;
    }

    public static String getAppWebPath(String virtualAbsolutePath) {
        if (virtualAbsolutePath == null) {
            return null;
        }
        return "/fs/" + virtualAbsolutePath;
    }

    public static VFile getUserAbsoluteFile(int userId, String path) {
        CorePlugin ap = VrApp.getBean(CorePlugin.class);
        AppUser t = ap.findUser(userId);
        FileSystemService fs = VrApp.getBean(FileSystemService.class);
        if (t != null) {
            VFile thisTeacherPhoto = fs.getUserFolder(t.getLogin()).get(path);
            if (thisTeacherPhoto.exists()) {
                return thisTeacherPhoto;
            } else {
                if (t.getType() != null) {
                    VFile anyTeacherPhoto = fs.getUserTypeFolder(t.getType().getName()).get(path);
                    if (anyTeacherPhoto.exists()) {
                        return anyTeacherPhoto;
                    }
                }
            }
        }
        return null;
    }

    public VFile handleFileUploadEvent(FileUploadEvent event) throws Exception {
        String tempPath = "/Temp/Files/" + VrHelper.date(new Date(), "yyyy-MM-dd-HH-mm")
                + "-" + VrApp.getBean(UserSession.class).getUser().getLogin();
        FileSystemService fsp = VrApp.getBean(FileSystemService.class);
        String p = fsp.getNativeFileSystemPath() + tempPath;
        new File(p).mkdirs();
        File f = new File(p, event.getFile().getFileName());
        try {
            event.getFile().write(f.getPath());
        } finally {
            //should not delete the file!
        }
        VirtualFileSystem nfs = VFS.createNativeFS();
        return nfs.get(f.getPath());
    }

    public VFile handleFileUploadEvent(FileUploadEvent event, VFile out) throws Exception {
        VFile temp = handleFileUploadEvent(event);
        temp.renameTo(out);
        return out;
    }
}
