/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web;

import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.util.JsfCtrl;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.vfs.VFile;

/**
 *
 * @author vpc
 */
public class AcademicCtrlUtils {

    public static String getTeacherAbsoluteWebPath(int id, String path) {
        VFile f = getTeacherAbsoluteFile(id, path);
        if (f == null) {
            return null;
        }
        return getAbsoluteWebPath(f.getPath());
    }

    public static String getTeacherAppWebPath(int id, String path) {
        VFile f = getTeacherAbsoluteFile(id, path);
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

    public static VFile getTeacherAbsoluteFile(int id, String path) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(id);
        FileSystemPlugin fs = VrApp.getBean(FileSystemPlugin.class);
        if (t != null && t.getUser()!=null) {
            VFile thisTeacherPhoto = fs.getUserFolder(t.getUser().getLogin()).get(path);
            if (thisTeacherPhoto.exists()) {
                return thisTeacherPhoto;
            } else {
                VFile anyTeacherPhoto = fs.getProfileFolder("Teacher").get(path);
                if (anyTeacherPhoto.exists()) {
                    return anyTeacherPhoto;
                }
            }
        }
        return null;
    }
}
