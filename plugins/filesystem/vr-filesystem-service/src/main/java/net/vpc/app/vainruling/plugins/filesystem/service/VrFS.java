/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.service;

import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.impl.FileACLVirtualFileSystem;

/**
 *
 * @author vpc
 */
public class VrFS extends FileACLVirtualFileSystem {

    public VrFS() {
        super("vrnativefs", VFS.NATIVE_FS);
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public boolean isActualAdmin() {
        try {
            return VrApp.getBean(CorePlugin.class).isActualAdmin();
        } catch (Exception e) {
            //session not yet created!
            return true;
        }
    }

    @Override
    public String getActualLogin() {
        return VrApp.getBean(CorePlugin.class).getActualLogin();
    }

    @Override
    public boolean userMatchesProfileFilter(String login, String profile) {
        return VrApp.getBean(CorePlugin.class).userMatchesProfileFilter(null, login, profile, null);
    }

}
