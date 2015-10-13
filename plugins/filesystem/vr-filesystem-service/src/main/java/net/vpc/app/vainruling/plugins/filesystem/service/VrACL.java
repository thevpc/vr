/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.upa.impl.util.Strings;
import net.vpc.vfs.VFile;
import net.vpc.vfs.VFileType;
import net.vpc.vfs.impl.SerializableVirtualFileACL;
import net.vpc.vfs.impl.VirtualFileACL;

/**
 *
 * @author vpc
 */
public class VrACL implements SerializableVirtualFileACL {

    private static final Logger log = Logger.getLogger(VrACL.class.getName());
    private Properties p;
    private String path;
    private VrFS outer;

    public VrACL(String path, Properties p, VrFS outer) {
        this.outer = outer;
        this.p = p;
        this.path = path;
    }

    protected void set(String prop, String val) {
        if (val == null) {
            val = "";
        }
        p.put(prop, val);
    }

    public String getUser(String login) {
        if (Strings.isNullOrEmpty(login)) {
            String login2 = VrApp.getBean(CorePlugin.class).getActualLogin();
            if (!Strings.isNullOrEmpty(login2)) {
                return login2;
            }
        }
        return login;
    }

    public boolean isAllowed(String action, String login, boolean defaultValue) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (core.isActualAdmin()) {
            return true;
        }
        //            String login0=login;
        login = getUser(login);
        if (p == null) {
            return defaultValue;
        }
        if (Strings.isNullOrEmpty(login) || getOwner().equals(login)) {
            return true;
        }
        String allowedProfiles = p.getProperty(action);
        if (Strings.isNullOrEmpty(allowedProfiles)) {
            return defaultValue;
        }
        if (allowedProfiles.trim().equals("*")) {
            return true;
        }
        return core.userMatchesProfileFilter(null, login, allowedProfiles, null);
    }

    public String getOwner() {
        String owner = p == null ? null : p.getProperty("Owner");
        return owner == null ? "" : owner;
    }

    @Override
    public boolean isAllowedCreateChild(VFileType type, String user) {
        String typeSuffix = type == VFileType.FILE ? "File" : type == VFileType.DIRECTORY ? "Directory" : "";
        return isAllowed("CreateChild" + typeSuffix, user, false);
    }

    @Override
    public boolean isAllowedRemoveChild(VFileType type, String user) {
        String typeSuffix = type == VFileType.FILE ? "File" : type == VFileType.DIRECTORY ? "Directory" : "";
        return isAllowed("RemoveChild" + typeSuffix, user, false);
    }

    @Override
    public boolean isAllowedUpdateChild(VFileType type, String user) {
        String typeSuffix = type == VFileType.FILE ? "File" : type == VFileType.DIRECTORY ? "Directory" : "";
        return isAllowed("UpdateChild" + typeSuffix, user, false);
    }

    @Override
    public boolean isAllowedRemove(String user) {
        VFile cf = outer.get(path);
        VFile pp = cf.getParentFile();
        if (pp != null) {
            VirtualFileACL pacl = pp.getACL();
            if (pacl != null && !pacl.isAllowedRemoveChild(cf.getFileType(), user)) {
                return false;
            }
        }
        return isAllowed("Remove", user, false);
    }

    @Override
    public boolean isAllowedRead(String user) {
        return isAllowed("Read", user, true);
    }

    @Override
    public boolean isAllowedWrite(String user) {
        VFile cf = outer.get(path);
        VFile pp = cf.getParentFile();
        if (pp != null) {
            VirtualFileACL pacl = pp.getACL();
            if (pacl != null && !pacl.isAllowedUpdateChild(cf.getFileType(), user)) {
                return false;
            }
        }
        return isAllowed("Write", user, false);
    }

    @Override
    public boolean isAllowedList(String user) {

        return isAllowed("List", user, true);
    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            p.store(s, "Virtual File System ACL");
            return s.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(VrFS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new byte[0];
    }

    @Override
    public VirtualFileACL getDefaultFileACL() {
        Properties p2 = new Properties();
        p2.setProperty("Owner", getUser(getOwner()));
        p2.setProperty("ReadFile", "*");
        return new VrACL(null, p2, outer);
    }

    @Override
    public VirtualFileACL getDefaultFolderACL() {
        Properties p2 = new Properties();
        p2.setProperty("Owner", getUser(getOwner()));
        p2.setProperty("ListDirectory", "*");
        return new VrACL(null, p2, outer);
    }

    public void chown(String newOwner) {
        setACLProperty("Owner", newOwner);
    }

    public void grantCreateFile(String profiles) {
        setACLProperty("CreateFile", profiles);
    }

    public void grantCreateDirectory(String profiles) {
        setACLProperty("CreateDirectory", profiles);
    }

    public void grantRemovePath(String profiles) {
        setACLProperty("RemovePath", profiles);
    }

    public void grantReadFile(String profiles) {
        setACLProperty("ReadFile", profiles);
    }

    public void grantWriteFile(String profiles) {
        setACLProperty("WriteFile", profiles);
    }

    public void grantListDirectory(String profiles) {
        setACLProperty("ListDirectory", profiles);
    }

    protected void setACLProperty(String property, String value) {
        try {
            if (VrApp.getBean(CorePlugin.class).isActualAdmin()
                    || getOwner().equals(VrApp.getBean(CorePlugin.class).getActualLogin())) {
                set(property, value);
                outer.storeACL(path, this);
            }
        } catch (Exception e) {
            log.log(Level.FINER, "Error", e);
            //ignore
        }
    }

}
