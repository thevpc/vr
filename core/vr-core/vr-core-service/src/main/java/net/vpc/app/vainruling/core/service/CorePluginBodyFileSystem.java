package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.fs.FileInfo;
import net.vpc.app.vainruling.core.service.fs.VrFS;
import net.vpc.app.vainruling.core.service.fs.VrFSEntry;
import net.vpc.app.vainruling.core.service.fs.VrFSTable;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppProperty;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.common.io.FileUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.*;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Level;

class CorePluginBodyFileSystem extends CorePluginBody {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePluginBodyFileSystem.class.getName());

    @Override
    public void onStart() {
        CorePlugin core = getContext().getCorePlugin();
        core.createRight(CorePluginSecurity.RIGHT_CUSTOM_FILESYSTEM_ROOT_FILE_SYSTEM, "Root FileSystem Access");
        core.createRight(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM, "My FileSystem Access");
        core.createRight(CorePluginSecurity.RIGHT_FILESYSTEM_ASSIGN_RIGHTS, "Assign Access Rights for File System");
        core.createRight(CorePluginSecurity.RIGHT_FILESYSTEM_SHARE_FOLDERS, "Share Folders in File System");
        core.createRight(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE, "Enable Write Access for File System");
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
                if (StringUtils.isEmpty(domain)) {
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
        if (u != null) {
            return UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {
                @Override
                public VirtualFileSystem run() {
                    VFile home = getUserFolder(login);
                    final VirtualFileSystem me = getRootFileSystem0().subfs(home.getPath());
                    MountableFS mfs = VFS.createMountableFS("user:" + login);
                    try {
                        mfs.mount("/" + CorePlugin.FOLDER_MY_DOCUMENTS, me);
                        List<AppProfile> profiles = getContext().getCorePlugin().findProfilesByUser(u.getId());
                        for (AppProfile p : profiles) {
                            if (CorePlugin.PROFILE_ADMIN.equals(p.getCode())) {
                                //this is admin
                                mfs.mount("/" + CorePlugin.FOLDER_ALL_DOCUMENTS, getRootFileSystem0());
                                break;
                            }
                        }
                        VrFSTable t0 = getVrFSTable();
                        Map<Integer, VrFSTable> usersVrFSTable = getUsersVrFSTable();
                        List<VrFSTable> all = new ArrayList<>();
                        all.add(t0);
                        all.addAll(usersVrFSTable.values());

                        for (AppProfile p : profiles) {
                            String profileMountPoint = "/" + I18n.get().get("System.documents.profile-folder-name", new Arg("name", VrUtils.normalizeFileName(p.getName())));
                            VirtualFileSystem profileFileSystem = getProfileFileSystem(p.getCode(), t0);
                            if (profileFileSystem.get("/").listFiles().length > 0) {
                                mfs.mount(profileMountPoint, profileFileSystem);
                            }
                        }

                        for (VrFSTable t : all) {
                            for (VrFSEntry e : t.getEntries(login, "User")) {
                                mfs.mount("/" + e.getMountPoint(), getRootFileSystem0().subfs(e.getLinkPath()));
                            }
                            for (VrFSEntry e : t.getEntriesByType("Profile")) {
                                //if (isComplexProfileExpr(e.getFilterName())) {
                                if (getContext().getCorePlugin().userMatchesProfileFilter(u.getId(), e.getFilterName())) {
                                    try {
                                        mountSubFS(mfs, e);
                                    } catch (IOException ex) {
                                        log.log(Level.SEVERE, null, ex);
                                    }
                                }
                                //}
                            }
                        }

                    } catch (IOException ex) {
                        log.log(Level.SEVERE, null, ex);
                    }
                    return mfs;
                }
            });
        } else {
            return VFS.createEmptyFS();
        }
    }

    private void mountSubFS(MountableFS mfs, VrFSEntry e) throws IOException {
        String linkPath = e.getLinkPath();
        if (linkPath.contains("*")) {
            VFile[] files = getRootFileSystem().get("/").find(linkPath, new VFileFilter() {
                @Override
                public boolean accept(VFile pathname) {
                    return pathname.isDirectory();
                }
            });
            ListFS lfs = VFS.createListFS(e.getMountPoint());
            for (VFile file : files) {
                lfs.addOrRename(file.getName(), file, null);
            }
            mfs.mount("/" + e.getMountPoint(), lfs);
        } else {
            mfs.mount("/" + e.getMountPoint(), getRootFileSystem().subfs(e.getLinkPath()));
        }
    }

    public VirtualFileSystem getProfileFileSystem(String profileName) {
        return getProfileFileSystem(profileName, null);
    }

    protected VirtualFileSystem getProfileFileSystem(String profileCode, final VrFSTable t) {
        CorePluginSecurity.requireProfile(profileCode);
        AppProfile u = getContext().getCorePlugin().findProfileByCode(profileCode);
        if (u != null) {
            return UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {

                @Override
                public VirtualFileSystem run() {
                    final String path = "/Documents/ByProfile/" + VrUtils.normalizeFilePath(profileCode);

                    getRootFileSystem0().mkdirs(path);
//
                    VirtualFileSystem pfs = getRootFileSystem0().subfs(path);
                    MountableFS mfs = null;
                    try {
                        VrFSTable t2 = t;
                        if (t2 == null) {
                            t2 = getVrFSTable();
                        }
                        mfs = VFS.createMountableFS("profile:" + profileCode);
                        mfs.mount("/", pfs);
                        for (VrFSEntry e : t2.getEntries(profileCode, "Profile")) {
                            MountableFS finalMfs = mfs;
                            UPA.getContext().invokePrivileged(new VoidAction() {

                                @Override
                                public void run() {
                                    try {
                                        mountSubFS(finalMfs, e);
                                    } catch (IOException ex) {
                                        log.log(Level.SEVERE, null, ex);
                                    }
                                }
                            });
                        }
                    } catch (IOException ex) {
                        log.log(Level.SEVERE, null, ex);
                    }
                    if (mfs == null) {
                        return pfs;
                    }
                    //VFile[] all = mfs.listFiles("/");
                    return mfs;
                }
            });
        } else {
            return VFS.createEmptyFS();
        }
    }

    private void commitVrFSTable(VrFSTable tab) {
        getRootFileSystem().mkdirs("/Config");
        OutputStream out = null;
        try {
            try {
                out = getRootFileSystem().getOutputStream("/Config/fstab");
                tab.store(out);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (Exception exx) {
            //ignore it
        }
    }

    public void removeUserLinkPathEntry(int userId, String linkPath) throws IOException {
        VrFSTable table = getUserVrFSTable(userId);
        VrFSEntry[] entries = table.getEntries();
        for (int i = 0; i < entries.length; i++) {
            VrFSEntry e = entries[i];
            if (e.getLinkPath().equals(linkPath)) {
                table.removeEntry(i);
                saveUserVrFSTable(userId, table);
                return;
            }
        }
    }

    public void setUserLinkPathEntry(int userId, VrFSEntry entry) throws IOException {
        if (StringUtils.isEmpty(entry.getMountPoint())) {
            removeUserLinkPathEntry(userId, entry.getLinkPath());
        } else {
            VrFSTable table = getUserVrFSTable(userId);
            String lp = entry.getLinkPath();
            for (VrFSEntry e : table.getEntries()) {
                if (e.getLinkPath().equals(lp)) {
                    e.setFilterName(entry.getFilterName());
                    e.setFilterType(entry.getFilterType());
                    e.setLinkPath(entry.getLinkPath());
                    e.setMountPoint(entry.getMountPoint());
                    saveUserVrFSTable(userId, table);
                    return;
                }
            }
            VrFSEntry e = new VrFSEntry();
            e.setFilterName(entry.getFilterName());
            e.setFilterType(entry.getFilterType());
            e.setLinkPath(entry.getLinkPath());
            e.setMountPoint(entry.getMountPoint());
            table.addEntry(e);
            saveUserVrFSTable(userId, table);
        }
    }

    public void saveUserVrFSTable(int userId, VrFSTable table) throws IOException {
        CorePluginSecurity.requireUser(userId);
        AppUser u = getContext().getCorePlugin().findUser(userId);
        if (u == null) {
            throw new IllegalArgumentException("Invalid user");
        }
        UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                VirtualFileSystem fs = getRootFileSystem0();
                fs.get("/Config").mkdirs();
                VFile file = fs.get("/Config/" + u.getLogin() + ".fstab");
                try {
                    table.store(file);
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    public VrFSTable getUserVrFSTable(int userId) {
        CorePluginSecurity.requireUser(userId);
        VrFSTable t = new VrFSTable();
        AppUser u = getContext().getCorePlugin().findUser(userId);
        if (u == null) {
            return null;
        }
        VirtualFileSystem fs = getRootFileSystem0();
        VFile file = fs.get("/Config/" + u.getLogin() + ".fstab");
        if (file.isFile()) {
            t.loadSilently(file);
        }
        return t;
    }

    private Map<Integer, VrFSTable> getUsersVrFSTable() {
        HashMap<Integer, VrFSTable> map = new HashMap<>();
        VirtualFileSystem fs = getRootFileSystem();
        if (fs.exists("/Config")) {
            for (VFile userfstab : fs.get("/Config").listFiles(new VFileFilter() {
                @Override
                public boolean accept(VFile pathname) {
                    return pathname.getName().endsWith(".fstab");
                }
            })) {
                String login = userfstab.getName().substring(0, userfstab.getName().length() - ".fstab".length());
                AppUser u = getContext().getCorePlugin().findUser(login);
                if (u != null) {
                    VrFSTable t = new VrFSTable();
                    t.loadSilently(userfstab);

                    for (VrFSEntry vrFSEntry : t.getEntries()) {
                        String m = vrFSEntry.getMountPoint();
                        if (m == null) {
                            m = "unknown";
                        }
                        m = m.trim();
                        if (m.endsWith("/")) {
                            m = m.substring(0, m.length() - 1);
                        }
                        if (m.isEmpty()) {
                            m = "unknown";
                        }
                        vrFSEntry.setMountPoint("/" + m + "#" + u.getId());
                    }
                    map.put(u.getId(), t);
                }
            }
        }
        return map;
    }

    private VrFSTable getVrFSTable() {
        VrFSTable t = new VrFSTable();
        InputStream in = null;
        try {
            try {
                if (getRootFileSystem().exists("/Config/fstab")) {
                    in = getRootFileSystem().getInputStream("/Config/fstab");
                    t.load(in);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (Exception exx) {
            //ignore it
        }
        return t;
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
