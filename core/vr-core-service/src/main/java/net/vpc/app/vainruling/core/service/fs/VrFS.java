/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.fs;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.impl.ACLPermission;
import net.vpc.common.vfs.impl.FileACLVirtualFileSystem;

/**
 * @author taha.bensalah@gmail.com
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
    public boolean isAdmin() {
        try {
            return VrApp.getBean(CorePlugin.class).isCurrentSessionAdmin();
        } catch (Exception e) {
            //session not yet created!
            return true;
        }
    }

    @Override
    public String getUserLogin() {
        return VrApp.getBean(CorePlugin.class).getActualLogin();
    }

    @Override
    public ACLPermission userMatchesProfileFilter(String login, String profile) {
        return VrApp.getBean(CorePlugin.class).userMatchesProfileFilter(null, login, profile, null) ? ACLPermission.GRANT : ACLPermission.DENY;
    }

}
