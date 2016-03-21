/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.Install;
import net.vpc.app.vainruling.api.Start;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppProfile;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.model.AppUserType;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.common.vfs.MountableFS;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.common.vfs.VirtualFileACL;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author vpc
 */
@AppPlugin(version = "1.1")
//@Service
public class FileSystemPlugin {

    @Autowired
    CorePlugin core;

    private VirtualFileSystem fileSystem;
    private String nativeFileSystemPath;
    public static final String RIGHT_FILESYSTEM_WRITE = "Custom.FileSystem.Write";

    @Install
    public void installService() {
        VrApp.getBean(CorePlugin.class).createRight("Custom.FileSystem.RootFileSystem", "Root FileSystem Access");
        VrApp.getBean(CorePlugin.class).createRight("Custom.FileSystem.MyFileSystem", "My FileSystem Access");
        VrApp.getBean(CorePlugin.class).createRight(RIGHT_FILESYSTEM_WRITE, "Enable Write Access for File System");
    }

    @Start
    public void start() {
        String home = System.getProperty("user.home");
        home = home.replace("\\", "/");
        if (!home.startsWith("/")) {
            home = "/" + home;
        }
        //setFileSystem(new UPAFileSystem());
        String path = (String) core.getOrCreateAppPropertyValue("System.FileSystem", null,
                home + "/filesystem/"
        //System.getProperty("user.home") + "/vr/filesystem"

        );
        nativeFileSystemPath = path;
        fileSystem = new VrFS().subfs(path, "vrfs");
        fileSystem.get("/").mkdirs();
    }

    public String getNativeFileSystemPath() {
        return nativeFileSystemPath;
    }

    public VirtualFileSystem getFileSystem() {
        return fileSystem;
    }

    public VFile getUserFolder(final String login) {
        AppUser u = core.findUser(login);
        if (u != null) {
            AppUserType t = u.getType();
            String typeName = t == null ? "NoType" : t.getName();
            final String path = "/Documents/ByUser/" + typeName + "/" + login;
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
                    VirtualFileACL v = getFileSystem().getACL(path);
                    if (!v.isReadOnly()) {
                        v.chown(login);
                    }
                    return null;
                }

            }, null);
            return fileSystem.get(path);
        }
        return null;
    }

    public VFile getProfileFolder(final String profile) {
        AppProfile u = core.findProfileByName(profile);
        if (u != null) {
            final String path = "/Documents/ByProfile/" + profile;

            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
                    VirtualFileACL v = getFileSystem().getACL(path);
                    if (!v.isReadOnly()) {
                        v.grantListDirectory(profile);
                    }
                    return null;
                }

            }, null);

            return fileSystem.get(path);
        }
        return null;
    }

    public VFile getUserTypeFolder(String userType) {
        AppProfile u = core.findProfileByName(userType);
        if (u != null) {
            final String path = "/Documents/ByUserType/" + userType;
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
//                    VirtualFileACL v = getFileSystem().getACL(path);
//                    v.chown(login);
                    return null;
                }

            }, null);
            return fileSystem.get(path);
        }
        return null;
    }

    public VirtualFileSystem getUserFileSystem(final String login) {
        AppUser u = core.findUser(login);
        if (u != null) {
            VFile home = getUserFolder(login);
            final VirtualFileSystem me = fileSystem.subfs(home.getPath());
            MountableFS mfs = VFS.createMountableFS("user:" + login);
            try {
                mfs.mount("/Mes Documents", me);
                List<AppProfile> profiles = core.findProfilesByUser(u.getId());
                for (AppProfile p : profiles) {
                    if (CorePlugin.PROFILE_ADMIN.equals(p.getName())) {
                        //this is admin
                        mfs.mount("/Tous", fileSystem);
                    }
                }
                VrFSTable t = new VrFSTable();
                try {
                    if (fileSystem.exists("/Config/fstab")) {
                        t.load(fileSystem.getInputStream("/Config/fstab"));
                    }
                } catch (Exception exx) {
                    //ignore it
                }
                for (AppProfile p : profiles) {
                    String profileMountPoint = "/" + p.getName() + " Documents";
                    mfs.mount(profileMountPoint, getProfileFileSystem(p.getName(), t));
                }
                for (VrFSEntry e : t.getEntries(login, "User")) {
                    mfs.mount("/" + e.getMountPoint(), fileSystem.subfs(e.getLinkPath()));
                }
                for (VrFSEntry e : t.getEntriesByType("Profile")) {
                    if (core.isComplexProfileExpr(e.getFilterName())) {
                        if (core.userMatchesProfileFilter(u.getId(), e.getFilterName())) {
                            mfs.mount("/" + e.getMountPoint(), fileSystem.subfs(e.getLinkPath()));
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(CorePlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
            return mfs;
        } else {
            return VFS.createEmptyFS();
        }
    }

    public VirtualFileSystem getProfileFileSystem(String profileName) {
        return getProfileFileSystem(profileName, null);
    }

    public VirtualFileSystem getProfileFileSystem(String profileName, VrFSTable t) {
        AppProfile u = core.findProfileByName(profileName);
        if (u != null) {
            final String path = "/Documents/ByProfile/" + profileName;
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
//                    VrACL v = (VrACL) getFileSystem().getACL(path);
//                    v.chown(login);
                    return null;
                }

            }, null);
            VirtualFileSystem pfs = fileSystem.subfs(path);
            MountableFS mfs = null;
            try {
                if (t == null) {
                    t = new VrFSTable();
                    try {
                        if (fileSystem.exists("/Config/fstab")) {
                            t.load(fileSystem.getInputStream("/Config/fstab"));
                        }
                    } catch (Exception exx) {
                        //ignore it
                    }
                }
                mfs = VFS.createMountableFS("profile:" + profileName);
                mfs.mount("/", pfs);
                for (VrFSEntry e : t.getEntries(profileName, "Profile")) {
                    mfs.mount("/" + e.getMountPoint(), fileSystem.subfs(e.getLinkPath()));
                }
            } catch (IOException ex) {
                Logger.getLogger(CorePlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (mfs == null) {
                return pfs;
            }
            VFile[] all = mfs.listFiles("/");
            return mfs;
        } else {
            return VFS.createEmptyFS();
        }
    }

    public void setFileSystem(VirtualFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public long getDownloadsCount(final VFile file) {
        return UPA.getContext().invokePrivileged(new Action<Long>() {

            @Override
            public Long run() {
                VirtualFileACL a = file.getACL();
                String d = a.getProperty("downloads");
                if (d == null) {
                    d = "0";
                }
                long dd = 0;
                try {
                    dd = Long.parseLong(d);
                } catch (Exception ee) {
                    //
                }
                return dd;
            }

        });
    }

    public void markDownloaded(final VFile file) {
        UPA.getContext().invokePrivileged(new Action<Object>() {

            @Override
            public Object run() {
                VirtualFileACL a = file.getACL();
                if (!a.isReadOnly()) {
                    String d = a.getProperty("downloads");
                    if (d == null) {
                        d = "0";
                    }
                    long dd = 0;
                    try {
                        dd = Long.parseLong(d);
                    } catch (Exception ee) {
                        //
                    }
                    a.setProperty("downloads", String.valueOf(dd + 1));
                }
//                    VrACL v = (VrACL) getFileSystem().getACL(path);
//                    v.chown(login);
                return null;
            }

        });
    }

}
