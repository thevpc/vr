/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.fs;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.common.vfs.VFS;
import net.thevpc.common.vfs.impl.ACLPermission;
import net.thevpc.common.vfs.impl.FileACLVirtualFileSystem;

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
        return VrApp.getBean(CorePlugin.class).isUserMatchesProfileFilter(null, login, profile, null) ? ACLPermission.GRANT : ACLPermission.DENY;
    }

}
