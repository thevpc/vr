/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.web.files;

import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.web.util.JsfCtrl;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.vfs.VFile;

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
        FileSystemPlugin fs = VrApp.getBean(FileSystemPlugin.class);
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
}
