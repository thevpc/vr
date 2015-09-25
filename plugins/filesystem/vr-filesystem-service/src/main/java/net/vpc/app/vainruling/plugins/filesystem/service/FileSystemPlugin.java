/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.Start;
import net.vpc.app.vainruling.api.model.AppProfile;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.model.AppUserType;
import net.vpc.vfs.MountableFS;
import net.vpc.vfs.VFS;
import net.vpc.vfs.VFile;
import net.vpc.vfs.VirtualFileSystem;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author vpc
 */
@AppPlugin(version = "1.0")
public class FileSystemPlugin {

    @Autowired
    CorePlugin core;

    private VirtualFileSystem fileSystem;
    private String nativeFileSystemPath;

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
        fileSystem = VFS.createNativeFS().subfs(path);
        fileSystem.get("/").mkdirs();
    }

    public String getNativeFileSystemPath() {
        return nativeFileSystemPath;
    }

    public VirtualFileSystem getFileSystem() {
        return fileSystem;
    }

    public VFile getUserFolder(String login) {
        AppUser u = core.findUser(login);
        if (u != null) {
            AppUserType t = u.getType();
            String typeName = t == null ? "NoType" : t.getName();
            String path = "/Documents/ByUser/" + typeName + "/" + login;
            getFileSystem().mkdirs(path);
            return fileSystem.get(path);
        }
        return null;
    }

    public VFile getProfileFolder(String profile) {
        AppProfile u = core.findProfileByName(profile);
        if (u != null) {
            String path = "/Documents/ByProfile/" + profile;
            getFileSystem().mkdirs(path);
            return fileSystem.get(path);
        }
        return null;
    }
    
    public VFile getUserTypeFolder(String userType) {
        AppProfile u = core.findProfileByName(userType);
        if (u != null) {
            String path = "/Documents/ByUserType/" + userType;
            getFileSystem().mkdirs(path);
            return fileSystem.get(path);
        }
        return null;
    }

    public VirtualFileSystem getUserFileSystem(String login) {
        AppUser u = core.findUser(login);
        if (u != null) {
            AppUserType t = u.getType();
            String typeName = t == null ? "NoType" : t.getName();
            String path = "/Documents/ByUser/" + typeName + "/" + login;
            getFileSystem().mkdirs(path);
            final VirtualFileSystem me = fileSystem.subfs(path);
            MountableFS mfs = VFS.createMountableFS();
            try {
                mfs.mount("/MyDocuments", me);
                for (AppProfile p : core.findProfilesByUser(u.getId())) {
                    mfs.mount("/" + p.getName() + "Documents", getProfileFileSystem(p.getName()));
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
        AppProfile u = core.findProfileByName(profileName);
        if (u != null) {
            String path = "/Documents/ByProfile/" + profileName;
            getFileSystem().mkdirs(path);
            return fileSystem.subfs(path);
        } else {
            return VFS.createEmptyFS();
        }
    }

    public void setFileSystem(VirtualFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

}
