/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.service;

import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.common.vfs.VFile;

/**
 *
 * @author vpc
 */
public class FSUtils {

    public static String getUserAppWebPath(int userId, String path) {
        VFile f = getUserAbsoluteFile(userId, path);
        if (f == null) {
            return null;
        }
        return getAppWebPath(f.getPath());
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
