/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.vfs.VFS;
import net.vpc.vfs.impl.FileACLVirtualFileSystem;
import net.vpc.vfs.impl.SerializableVirtualFileACL;

/**
 *
 * @author vpc
 */
public class VrFS extends FileACLVirtualFileSystem {

    public VrFS() {
        super(VFS.NATIVE_FS);
    }

    @Override
    protected SerializableVirtualFileACL loadACL(String path, byte[] bytes) {
        try {
            Properties p = new Properties();
            if (bytes != null) {
                p.load(new ByteArrayInputStream(bytes));
                return new VrACL(path, p, this);
            }
        } catch (IOException ex) {
            Logger.getLogger(VrFS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new VrACL(path, null, this);
    }


    @Override
    public String toString() {
        return "VrFS";
    }

}
