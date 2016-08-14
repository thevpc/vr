/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.vfs.VFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
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
        return VrApp.getBean(Vr.class).getContext() + "/fs/" + virtualAbsolutePath;
    }

    public static String getAppWebPath(String virtualAbsolutePath) {
        if (virtualAbsolutePath == null) {
            return null;
        }
        return "/fs/" + virtualAbsolutePath;
    }

    public static VFile getTeacherAbsoluteFile(int id, String... path) {
        VFile[] p = getTeacherAbsoluteFiles(id, path);
        if (p.length == 0) {
            return null;
        }
        return p[0];
    }

    public static VFile[] getTeacherAbsoluteFiles(int id, String[] path) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(id);
        CorePlugin fs = VrApp.getBean(CorePlugin.class);
        List<VFile> files = new ArrayList<VFile>();
        if (t != null && t.getUser() != null) {
            VFile userFolder = fs.getUserFolder(t.getUser().getLogin());
            VFile profileFolder = fs.getProfileFolder("Teacher");
            for (String p : path) {
                VFile ff = userFolder.get(p);
                if (ff.exists()) {
                    files.add(ff);
                }
            }
            for (String p : path) {
                VFile ff = profileFolder.get(p);
                if (ff.exists()) {
                    files.add(ff);
                }
            }
        }
        return files.toArray(new VFile[files.size()]);
    }

    public static VFile getTeacherAbsoluteFile(int id, String path) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(id);
        CorePlugin fs = VrApp.getBean(CorePlugin.class);
        if (t != null && t.getUser() != null) {
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
