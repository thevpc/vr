/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.fs;

/**
 * @author taha.bensalah@gmail.com
 */
public class FSUtils {

//    public static String getUserAppWebPath(int userId, String path) {
//        VFile f = getUserAbsoluteFile(userId, path);
//        if (f == null) {
//            return null;
//        }
//        return getAppWebPath(f.getPath());
//    }

    public static String getAppWebPath(String virtualAbsolutePath) {
        if (virtualAbsolutePath == null) {
            return null;
        }
        return "/fs/" + virtualAbsolutePath;
    }

//    public static VFile getUserAbsoluteFile(int userId, String path) {
//        CorePlugin ap = VrApp.getBean(CorePlugin.class);
//        AppUser t = ap.findUser(userId);
//        CorePlugin fs = VrApp.getBean(CorePlugin.class);
//        if (t != null) {
//            VFile thisTeacherPhoto = fs.getUserFolder(t.getUserLogin()).get(path);
//            if (thisTeacherPhoto.exists()) {
//                return thisTeacherPhoto;
//            } else {
//                if (t.getType() != null) {
//                    VFile anyTeacherPhoto = fs.getUserTypeFolder(t.getType().getName()).get(path);
//                    if (anyTeacherPhoto.exists()) {
//                        return anyTeacherPhoto;
//                    }
//                }
//            }
//        }
//        return null;
//    }
}
