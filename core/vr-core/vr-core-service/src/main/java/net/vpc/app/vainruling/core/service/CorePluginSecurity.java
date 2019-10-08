package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.ProtectionLevel;

import java.util.ArrayList;
import java.util.List;

public class CorePluginSecurity {

    public static final String RIGHT_CUSTOM_FILESYSTEM_ROOT_FILE_SYSTEM = "Custom.FileSystem.RootFileSystem";
    public static final String RIGHT_FILESYSTEM_WRITE = "Custom.FileSystem.Write";
    public static final String RIGHT_FILESYSTEM_ASSIGN_RIGHTS = "Custom.FileSystem.AssignRights";
    public static final String RIGHT_FILESYSTEM_SHARE_FOLDERS = "Custom.FileSystem.ShareFolders";
    public static final String RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM = "Custom.FileSystem.MyFileSystem";
    public static final String RIGHT_CUSTOM_ADMIN_PASSWD = "Custom.Admin.Passwd";
    public static final String RIGHT_CUSTOM_DEV_TOOLS = "Custom.DevTools";
    public static final String RIGHT_CUSTOM_COMPLETION_MONITOR = "Custom.CompletionMonitor";
    public static final String RIGHT_CUSTOM_ADMIN_ACTIVE_SESSIONS = "Custom.Admin.ActiveSessions";
    public static final String RIGHT_CUSTOM_ADMIN_IMPERSONATE = "Custom.Admin.Impersonate";
    public static final String RIGHT_CUSTOM_ADMIN_INVALIDATE_CACHE = "Custom.Admin.InvalidateCache";
    public static final String RIGHT_CUSTOM_ADMIN_UPDATE_ALL_FORMULAS = "Custom.Admin.UpdateAllFormulas";
    public static final String RIGHT_CUSTOM_ADMIN_IMPORT_FILE = "Custom.Admin.ImportFile";
    public static final String RIGHT_CUSTOM_MY_PROFILE = "Custom.MyProfile";
    public static final String RIGHT_CUSTOM_UTIL_NAVIGATION_HISTORY = "Custom.Util.NavigationHistory";
    public static final String RIGHT_CUSTOM_ADMIN = "Custom.Admin";
    public static final String RIGHT_CUSTOM_UPDATE_MY_PROFILES = "Custom.Admin.UpdateMyProfiles";
    public static final String[] RIGHTS_CORE = VrPlatformUtils.getStringArrayConstantsValues(CorePluginSecurity.class, "RIGHT_*");

    public static void requireRight(String right) {
        if (right == null) {
            return;
        }
        if (!CorePlugin.get().isCurrentAllowed(right)) {
            throw new SecurityException("Not Allowed");
        }
    }

    public static void requireAdmin() {
        if (!CorePlugin.get().isCurrentSessionAdmin()) {
            throw new SecurityException("Not Allowed");
        }
    }

    public static void requireUser(String login) {
        if (!CorePlugin.get().isCurrentSessionAdminOrUser(login)) {
            throw new SecurityException("Not Allowed");
        }
    }

    public static void requireProfile(String profileName) {
        if (!CorePlugin.get().isCurrentSessionAdminOrProfile(profileName)) {
            throw new SecurityException("Not Allowed");
        }
    }

    public static void requireAllOfProfiles(String... profileNames) {
        if (!CorePlugin.get().isCurrentSessionAdminOrAllOfProfiles(profileNames)) {
            throw new SecurityException("Not Allowed");
        }
    }

    public static void requireAnyOfProfiles(String... profileNames) {
        if (!CorePlugin.get().isCurrentSessionAdminOrAnyOfProfiles(profileNames)) {
            throw new SecurityException("Not Allowed");
        }
    }

    public static void requireUser(int userId) {
        if (!CorePlugin.get().isCurrentSessionAdminOrUser(userId)) {
            throw new SecurityException("Not Allowed");
        }
    }

//    public static void requireContact(int contactId) {
//        if (!CorePlugin.get().isCurrentSessionAdminOrContact(contactId)) {
//            throw new SecurityException("Not Allowed");
//        }
//    }
    public static String[] getEntityRights(Entity entity) {
        return new String[]{
            getEntityRightEditor(entity),
            getEntityRightLoad(entity),
            getEntityRightNavigate(entity),
            getEntityRightPersist(entity),
            getEntityRightUpdate(entity),
            getEntityRightRemove(entity),};
    }

    public static String[] getEntityRights(String entity) {
        return new String[]{
            getEntityRightEditor(entity),
            getEntityRightLoad(entity),
            getEntityRightNavigate(entity),
            getEntityRightPersist(entity),
            getEntityRightUpdate(entity),
            getEntityRightRemove(entity),};
    }

    public static String[] getEntityRights(Entity entity, boolean read, boolean write, boolean remove, boolean actions, boolean fields) {
        List<String> all = new ArrayList<>();
        if (read) {
            all.add(getEntityRightEditor(entity));
            all.add(getEntityRightNavigate(entity));
            all.add(getEntityRightLoad(entity));
        }
        if (write) {
            if (entity.getShield().isPersistSupported()) {
                all.add(getEntityRightPersist(entity));
            }
            if (entity.getShield().isUpdateSupported()) {
                all.add(getEntityRightUpdate(entity));
            }
            if (entity.getShield().isRenameSupported()) {
                all.add(getEntityRightRemove(entity));
            }
        }
        if (actions) {
            String extraActions = entity.getProperties().getString("actions");
            if (extraActions != null) {
                for (String a : extraActions.split(" ,|;")) {
                    if (a.length() > 0) {
                        all.add(a);
                    }
                }
            }
        }
        if (fields) {
            for (Field field : entity.getFields()) {
                if (field.getUpdateProtectionLevel() == ProtectionLevel.PROTECTED || field.getPersistProtectionLevel() == ProtectionLevel.PROTECTED) {
                    if (write) {
                        all.add(getFieldRightWrite(field));
                    }
                }
                if (field.getReadProtectionLevel() == ProtectionLevel.PROTECTED || field.getUpdateProtectionLevel() == ProtectionLevel.PROTECTED || field.getPersistProtectionLevel() == ProtectionLevel.PROTECTED) {
                    if (read) {
                        all.add(getFieldRightRead(field));
                    }
                }
            }
        }
        return all.toArray(new String[all.size()]);
    }

    public static String[] getRights(Field field) {
        return new String[]{
            getFieldRightWrite(field),
            getFieldRightRead(field)
        };
    }

    public static String getEntityRightEditor(Entity entity) {
        return getEntityRightEditor(entity.getName());
    }

    public static String getEntityRightLoad(Entity entity) {
        return getEntityRightLoad(entity.getName());
    }

    public static String getEntityRightNavigate(Entity entity) {
        return getEntityRightNavigate(entity.getName());
    }

    public static String getEntityRightPersist(Entity entity) {
        return getEntityRightPersist(entity.getName());
    }

    public static String getEntityRightUpdate(Entity entity) {
        return getEntityRightUpdate(entity.getName());
    }

    public static String getEntityRightRemove(Entity entity) {
        return getEntityRightRemove(entity.getName());
    }

    public static String getEntityRightEditor(String entity) {
        return entity + ".DefaultEditor";
    }

    public static String getEntityRightLoad(String entity) {
        return entity + ".Load";
    }

    public static String getEntityRightNavigate(String entity) {
        return entity + ".Navigate";
    }

    public static String getEntityRightPersist(String entity) {
        return entity + ".Persist";
    }

    public static String getEntityRightUpdate(String entity) {
        return entity + ".Update";
    }

    public static String getEntityRightRemove(String entity) {
        return entity + ".Remove";
    }

    public static String getFieldRightWrite(Field field) {
        return field.getEntity().getAbsoluteName() + "." + field.getName() + ".Write";
    }

    public static String getFieldRightRead(Field field) {
        return field.getEntity().getAbsoluteName() + "." + field.getName() + ".Read";
    }
}
