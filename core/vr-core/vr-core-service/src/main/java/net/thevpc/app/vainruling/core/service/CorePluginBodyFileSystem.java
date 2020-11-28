package net.thevpc.app.vainruling.core.service;

import net.thevpc.app.vainruling.core.service.cache.CacheService;
import net.thevpc.app.vainruling.core.service.fs.FileInfo;
import net.thevpc.app.vainruling.core.service.fs.MirroredPath;
import net.thevpc.app.vainruling.core.service.fs.VrFS;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.app.vainruling.core.service.util.AppVersion;
import net.thevpc.app.vainruling.core.service.model.AppProfile;
import net.thevpc.app.vainruling.core.service.model.AppProperty;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.model.AppUserType;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.common.io.FileUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.vfs.*;
import net.thevpc.upa.Action;
import net.thevpc.upa.UPA;
import net.thevpc.upa.VoidAction;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.thevpc.app.vainruling.core.service.model.AppFsSharing;
import net.thevpc.upa.PersistenceUnit;

class CorePluginBodyFileSystem extends CorePluginBody {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePluginBodyFileSystem.class.getName());

    @Override
    public void onStart() {
        ProfileRightBuilder b = new ProfileRightBuilder();
        b.addName(CorePluginSecurity.RIGHT_CUSTOM_FILESYSTEM_ROOT_FILE_SYSTEM, "Root FileSystem Access");
        b.addName(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM, "My FileSystem Access");
        b.addName(CorePluginSecurity.RIGHT_FILESYSTEM_ASSIGN_RIGHTS, "Assign Access Rights for File System");
        b.addName(CorePluginSecurity.RIGHT_FILESYSTEM_SHARE_FOLDERS, "Share Folders in File System");
        b.addName(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE, "Enable Write Access for File System");
        b.execute();
    }

    public String getNativeFileSystemPath() {
        CorePluginSecurity.requireAdmin();
        CacheService cacheService = getContext().getCacheService();
        return cacheService.get(AppProperty.class).getProperty("System.FileSystem.Path", new Action<String>() {
            @Override
            public String run() {
                CorePlugin core = getContext().getCorePlugin();
                AppVersion appVersion = core.getAppVersion();
                String appName = appVersion.getId().toLowerCase();
                String home = System.getProperty("user.home");
                home = home.replace("\\", "/");
                String domain = core.getCurrentDomain();
                if (StringUtils.isBlank(domain)) {
                    domain = "";
                }
                String path = (String) core.getOrCreateAppPropertyValue("System.FileSystem", null,
                        home + "/workspace/" + appName + "/filesystem/" + domain
                );
                return path;
            }
        });
    }

    VirtualFileSystem getRootFileSystem0() {
        CacheService cacheService = getContext().getCacheService();
        return cacheService.get(AppProperty.class).getProperty("System.FileSystem", new Action<VirtualFileSystem>() {
            @Override
            public VirtualFileSystem run() {
                String path = getNativeFileSystemPath();
                VirtualFileSystem fileSystem = new VrFS().subfs(path, "vrfs");
                fileSystem.get("/").mkdirs();
                return fileSystem;
            }
        });
    }

    public VirtualFileSystem getRootFileSystem() {
        CorePluginSecurity.requireUser(null);
        return getRootFileSystem0();
    }

    public VFile getUserDocumentsFolder(final String login) {
        return getUserFileSystem(login).get("/" + CorePlugin.FOLDER_MY_DOCUMENTS);
    }

    public VFile getUserFolder(final String login) {
        CorePluginSecurity.requireUser(login);
        AppUser u = getContext().getCorePlugin().findUser(login);
        if (u != null) {
            AppUserType t = u.getType();
            String typeName = t == null ? "NoType" : AppUserType.getCodeOrName(t);
            final String path = "/Documents/ByUser/" + VrUtils.normalizeFilePath(typeName) + "/" + VrUtils.normalizeFilePath(login);
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getRootFileSystem0().mkdirs(path);
                    VirtualFileACL v = getRootFileSystem0().getACL(path);
                    if (!v.isReadOnly()) {
                        v.setOwner(login);
                    }
                    return null;
                }

            }, null);
            return getRootFileSystem0().get(path);
        }
        return null;
    }

    public VFile getUserSharedFolder() {
        final String path = "/Documents/Shared/";
        UPA.getContext().invokePrivileged(new VoidAction() {

            @Override
            public void run() {
                getRootFileSystem0().mkdirs(path);
                VirtualFileACL v = getRootFileSystem0().getACL(path);
            }

        });
        return getRootFileSystem0().get(path);
    }

    public VFile getProfileFolder(final String profileCode) {
        AppProfile u = getContext().getCorePlugin().findProfileByCode(profileCode);
        if (u != null) {
            final String path = "/Documents/ByProfile/" + VrUtils.normalizeFilePath(profileCode);

            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getRootFileSystem0().mkdirs(path);
                    VirtualFileACL v = getRootFileSystem0().getACL(path);
                    if (!v.isReadOnly()) {
                        v.setPermissionListDirectory(profileCode);
                    }
                    return null;
                }

            }, null);

            return getRootFileSystem0().get(path);
        }
        return null;
    }

    public VFile getUserTypeFolder(int userTypeId) {
        AppUserType u = getContext().getCorePlugin().findUserType(userTypeId);
        if (u != null) {
            final String path = "/Documents/ByUserType/" + VrUtils.normalizeFilePath(AppUserType.getCodeOrName(u));
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getRootFileSystem0().mkdirs(path);
//                    VirtualFileACL v = getRootFileSystem().getACL(path);
//                    v.setOwner(login);
                    return null;
                }

            }, null);
            return getRootFileSystem0().get(path);
        }
        return null;
    }

    public FileInfo getMyHomeFile(String path) {
        return createFileInfo(getMyHomeFileSystem().get(path));
    }

    public FileInfo getMyFile(String path) {
        return createFileInfo(getMyFileSystem().get(path));
    }

    public FileInfo getUserFile(String login, String path) {
        return createFileInfo(getUserFileSystem(login).get(path));
    }

    public FileInfo getUserHomeFile(String login, String path) {
        return createFileInfo(getUserHomeFileSystem(login).get(path));
    }

    public FileInfo getProfileFile(String profile, String path) {
        return createFileInfo(getProfileFileSystem(profile).get(path));
    }

    public FileInfo getRootFile(String path) {
        return createFileInfo(getRootFileSystem().get(path));
    }

    public FileInfo createFileInfo(VFile f) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(f.getName());
        int i = 0;
        VFile[] listeFile = f.listFiles();
        FileInfo[] childrenArr = new FileInfo[listeFile.length];
        childrenArr = new FileInfo[listeFile.length];
        for (VFile vf : listeFile) {
            childrenArr[i] = new FileInfo(vf.getName(), vf.getFileType(), vf.lastModified(), vf.isFile() ? getDownloadsCount(vf) : 0);
            i++;
        }
        fileInfo.setLastModif(f.lastModified());
        fileInfo.setType(f.getFileType());
        fileInfo.setChildren(childrenArr);
        return fileInfo;
    }

    public VirtualFileSystem getMyHomeFileSystem() {
        return getUserHomeFileSystem(getContext().getCorePlugin().getCurrentUserLogin());
    }

    public VirtualFileSystem getUserHomeFileSystem(final String login) {
        CorePluginSecurity.requireUser(login);
        VFile home = getUserFolder(login);
        return getRootFileSystem0().subfs(home.getPath());
    }

    public VirtualFileSystem getUserFileSystem(final String login) {
        CorePluginSecurity.requireUser(login);
        AppUser u = getContext().getCorePlugin().findUser(login);
        final CorePlugin core = getContext().getCorePlugin();
        if (u != null) {
            return UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {
                @Override
                public VirtualFileSystem run() {
                    VFile home = getUserFolder(login);
                    MountableFS mfs = VFS.createMountableFS("user:" + login);
                    try {
                        mfs.mount("/" + CorePlugin.FOLDER_MY_DOCUMENTS, home);
                        List<AppProfile> profiles = getContext().getCorePlugin().findProfilesByUser(u.getId());
                        for (AppProfile p : profiles) {
                            if (CorePlugin.PROFILE_ADMIN.equals(p.getCode())) {
                                //this is admin
                                mfs.mount("/" + CorePlugin.FOLDER_ALL_DOCUMENTS, getRootFileSystem0().get("/"));
                                break;
                            }
                        }

                        for (AppProfile p : profiles) {
                            if (p.isShareFolder()) {
                                String profileMountPoint = "/" + I18n.get().get("System.documents.profile-folder-name", new Arg("name", VrUtils.normalizeFileName(p.getName())));
                                AppProfile appProfile = getContext().getCorePlugin().findProfileByCode(p.getCode());
                                if (appProfile != null) {
                                    VirtualFileSystem profileFileSystem = getProfileFileSystem(p.getCode());
                                    final VFile rootFolder = profileFileSystem.get("/");
//                                boolean visible = rootFolder.listFiles().length != 0;
//                                if (!visible) {
//                                    if (u.getLogin().equals(rootFolder.getACL().getOwner())
//                                            || core.isUserMatchesProfileFilter(u.getId(), appProfile.getAdmin())
//                                            || core.isUserAdmin(u.getId())) {
//                                        visible = true;
//                                    }
//                                }
//                                if (visible) {
                                    mfs.mount(profileMountPoint, rootFolder);
//                                }
                                }
                            }
                        }

                        for (AppFsSharing e : getEnabledFsSharings()) {
                            if (core.isUserMatchesProfileFilter(u.getId(), e.getAllowedUsers())) {
                                try {
                                    mountSubFS(mfs, e);
                                } catch (Exception ex) {
                                    log.log(Level.SEVERE, null, ex);
                                }
                            }
                            //}
                        }

                    } catch (IOException ex) {
                        log.log(Level.SEVERE, null, ex);
                    }
                    return mfs;
                }
            }
            );
        } else {
            return VFS.createEmptyFS();
        }
    }

    private void mountSubFS(MountableFS mfs, AppFsSharing e) throws IOException {
        if (e == null) {
            throw new IllegalArgumentException("Missing Sharing");
        }
        AppUser user = e.getUser();
        if (user == null) {
            throw new IllegalArgumentException("Invalid User");
        }
        String mountPath = e.getMountPath();
        String sharedPath = e.getSharedPath();
        if (!sharedPath.startsWith("/")) {
            VFile h = getUserFolder(user.getLogin());
            if (h == null) {
                throw new IllegalArgumentException("Invalid User");
            }
            sharedPath = h.getPath() + "/" + sharedPath;
        }

        String[] mountPathArr = StringUtils.split(mountPath, "/");
        if (mountPathArr.length == 0) {
            throw new IllegalArgumentException("Invalid Mount Point :" + mountPath);
        }
        if (StringUtils.isBlank(sharedPath)) {
            throw new IllegalArgumentException("Blank target path");
        }
//        if (getContext().getCorePlugin().isUserMatchesProfileFilter(e.getUser().getId(), e.getAllowedUsers())) {
        if (sharedPath.contains("*")) {
            for (VFile file : getRootFileSystem().get("/").find(sharedPath, VFile::isDirectory)) {
                mfs.mount("/" + mountPath + "/" + file.getName(), file);
            }
        } else {
            VFile p = getRootFileSystem().get(sharedPath);
            if (!p.exists() || !p.isDirectory()) {
                throw new IOException("Invalid path to mount : " + sharedPath);
            }
            mfs.mount("/" + mountPath, getRootFileSystem().get(sharedPath));
        }
//        }
    }

    protected VirtualFileSystem getProfileFileSystem(String profileCode) {
        CorePluginSecurity.requireProfile(profileCode);
        AppProfile u = getContext().getCorePlugin().findProfileByCode(profileCode);
        if (u != null) {
            return UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {

                @Override
                public VirtualFileSystem run() {
                    final String path = "/Documents/ByProfile/" + VrUtils.normalizeFilePath(profileCode);
                    getRootFileSystem0().mkdirs(path);
                    return getRootFileSystem0().subfs(path);
                }
            });
        } else {
            return VFS.createEmptyFS();
        }
    }

    public List<AppFsSharing> getFsSharings() {
        return getContext().getCacheService().getList(AppFsSharing.class)
                .stream().filter(x -> !x.isDisabled()).collect(Collectors.toList());
    }

    public List<AppFsSharing> getEnabledFsSharings() {
        return getContext().getCacheService().getList(AppFsSharing.class);
    }

    public List<AppFsSharing> findFsSharings(Integer userId, String mountPath, String sharedPath) {
        if (userId == null) {
            userId = getContext().getCorePlugin().getCurrentUserId();
            if (userId == null) {
                return Collections.emptyList();
            }
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        CorePlugin core = getContext().getCorePlugin();
        int fuserId = userId;
        AppUser o = pu.invokePrivileged(() -> core.findUser(fuserId));
        if (o == null) {
            return Collections.emptyList();
        }
        CorePluginSecurity.requireUser(userId);
        return pu.createQueryBuilder(AppFsSharing.class)
                .byField("userId", userId)
                .byField("mountPath", mountPath, mountPath != null)
                .byField("sharedPath", sharedPath, sharedPath != null)
                .getResultList();
    }

    public void removeFsSharing(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppFsSharing a = pu.findById(AppFsSharing.class, id);
        if (a != null) {
            if (a.getUser() != null) {
                CorePluginSecurity.requireUser(a.getUser().getId());
            }
            pu.invokePrivileged(() -> pu.remove(a));
        }
    }

    public void saveFsSharing(final AppFsSharing entry) {
        int uid = entry.getUser().getId();
        CorePluginSecurity.requireUser(uid);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (entry.getId() > 0) {
            pu.invokePrivileged(() -> pu.merge(entry));
        } else {
            pu.invokePrivileged(() -> pu.persist(entry));
        }
    }

    public long getDownloadsCount(final VFile file) {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<Long>() {

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
        UPA.getPersistenceUnit().invokePrivileged(new Action<Object>() {

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
//                    VrACL v = (VrACL) getRootFileSystem().getACL(path);
//                    v.setOwner(login);
                return null;
            }

        });
    }

    public VirtualFileSystem getMyFileSystem() {
        return getUserFileSystem(getContext().getCorePlugin().getCurrentUserLogin());
    }

    public MirroredPath createTempUploadFolder() {
        String login = getContext().getCorePlugin().getCurrentUserLogin();
        String tempPath = CorePlugin.PATH_TEMP + "/Files/" + VrUtils.date(new Date(), "yyyy-MM-dd-HH-mm")
                + "-" + login;
        String p = getNativeFileSystemPath() + tempPath;
        File file = new File(VrPlatformUtils.validatePath(p));
        file.mkdirs();
        return new MirroredPath(getRootFileSystem().get(tempPath), file);
    }

    public VFile uploadFile(VFile baseFile, UploadedFileHandler event) throws IOException {
        CorePluginSecurity.requireAdmin();
        String fileName = VrUtils.normalizeFilePath(event.getFileName());
        VFile newFile = baseFile;//.get(fileName);
        VFile baseFolder = null;
        if (newFile.exists() && newFile.isDirectory()) {
            baseFolder = newFile;
            int pos = 0;
            while (true) {
                String n = fileName;
                if (pos > 0) {
                    n = FileUtils.changeFileSuffix(new File(fileName), "-" + pos).getPath();
                }
                VFile vFile = newFile.get(n);
                if (!vFile.exists()) {
                    newFile = vFile;
                    break;
                }
                pos++;
            }
        } else if (newFile.exists()) {
            boolean doOverride = false;
            //check if alreay selected
            if (event.acceptOverride(newFile)) {
                doOverride = true;
            }
            if (!doOverride) {
                throw new IOException(fileName + " already exists please select to force override.");
            }
        }
        MirroredPath temp = createTempUploadFolder();
        File f = new File(temp.getNativePath(), fileName);
        try {
            event.write(f.getPath());
            //do work here
            int count = 1;//
            if (count > 0) {
                return newFile; //addInfoMessage(event.getFileName() + " successfully uploaded.");
            } else {
                return newFile;
                //addWarnMessage(null, event.getFileName() + " is uploaded but nothing is updated.");
            }
        } finally {
            //should not delete the file!
            temp.getPath().get(fileName).copyTo(newFile);
        }
    }

}
