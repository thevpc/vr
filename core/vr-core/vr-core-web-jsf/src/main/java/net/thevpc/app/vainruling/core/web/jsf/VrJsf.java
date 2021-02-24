package net.thevpc.app.vainruling.core.web.jsf;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.web.util.DocumentsUtils;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.fs.VFileInfo;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.util.VrPlatformUtils;
import net.thevpc.common.io.FileUtils;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.vfs.VFile;
import net.thevpc.common.vfs.VirtualFileSystem;
import net.thevpc.upa.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.model.SelectItem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.app.vainruling.core.service.fs.MirroredPath;
import net.thevpc.common.io.IOUtils;

public class VrJsf {

    public static VFile createTempFile(FileUploadEvent event) {
        MirroredPath temp = CorePlugin.get().createTempUploadFolder();
        VFile o = temp.getPath().get(event.getFile().getFileName());
        try (InputStream is = event.getFile().getInputStream()) {
            try (OutputStream os = o.getOutputStream()) {
                IOUtils.copy(is, os);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return o;
    }

    public static StreamedContent getContent(String path) {
        InputStream stream = null;
        try {
            CorePlugin core = CorePlugin.get();
            VFile file = UPA.getPersistenceUnit().invokePrivileged(() -> core.getRootFileSystem().get(path));
            if (file.isDirectory()) {
                //should zip it?
            } else {
                core.markDownloaded(file);
                stream = file.getInputStream();
                return new DefaultStreamedContent(stream, file.probeContentType(), file.getName());
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentsUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static StreamedContent getContent(VFileInfo i) {
        InputStream stream = null;
        try {
            if (i.getFile().isDirectory()) {
                //should zip it?
            } else {
                final VFile f = i.getFile();
                CorePlugin fsp = VrApp.getBean(CorePlugin.class);
                fsp.markDownloaded(f);
                stream = f.getInputStream();
                return new DefaultStreamedContent(stream, f.probeContentType(), f.getName());
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentsUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static VFile handleFileUpload(FileUploadEvent event, String destinationPath, boolean userhome, boolean override) throws Exception {
        String fileName = event.getFile().getFileName();
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (destinationPath == null) {
            destinationPath = "";
        }
        if (StringUtils.indexOfWord(destinationPath, "..", 0, "/") >= 0) {
            throw new RuntimeException("Invalid path : " + destinationPath);
        }
        if (destinationPath.endsWith("/*")) {
            destinationPath = destinationPath.substring(0, destinationPath.length() - 2) + "/" + fileName;
        }

        String path = "";
        if (userhome) {
            AppUser user = core.getCurrentUser();
            if (user == null) {
                return null;
            }
            path = core.getUserFolder(user.getLogin()).getPath() + "/" + destinationPath;
        } else {
            path = destinationPath;
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        VirtualFileSystem rootfs = UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {
            @Override
            public VirtualFileSystem run() {
                return core.getRootFileSystem();
            }
        });
        VFile nativeFile = rootfs.get(path).getBaseFile("NativeFS");
        VFile pf = nativeFile.getParentFile();
        if (!pf.exists()) {
            pf.mkdirs();
        }
        String p = nativeFile.getPath();

        File f = new File(VrPlatformUtils.validatePath(p));
        if (f.exists() && !override) {
            int index = 2;
            while (true) {
                File f2 = FileUtils.changeFileSuffix(f, "-" + index);
                if (!f2.exists()) {
                    f = f2;
                    break;
                }
                index++;
            }
        }
        f.getParentFile().mkdirs();
        try (InputStream is = event.getFile().getInputStream()) {
            IOUtils.copy(is, f);
        } catch (Exception e) {
            throw new IOException(e);
        }
        return rootfs.get(path).getParentFile().get(f.getName());
    }

    public static SelectItem toSelectItem(Object o) {
        Class<?> lastEntityClass = null;
        EntityBuilder lastBuilder = null;
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (o == null) {
            return (FacesUtils.createSelectItem("", ""));
        } else if (o instanceof SelectItem) {
            return ((SelectItem) o);
        } else if (o instanceof NamedId) {
            NamedId n = (NamedId) o;
            return (FacesUtils.createSelectItem(n.getStringId(), n.getStringName()));
        } else {
            Class<?> newClass = o.getClass();
            if (newClass.equals(lastEntityClass)) {
                NamedId n = lastBuilder.objectToNamedId(o);
                return (FacesUtils.createSelectItem(n.getStringId(), n.getStringName()));
            } else {
                Entity entity = pu.findEntity(newClass);
                if (entity != null) {
                    lastBuilder = entity.getBuilder();
                    NamedId n = lastBuilder.objectToNamedId(o);
                    return (FacesUtils.createSelectItem(n.getStringId(), n.getStringName()));
                } else {
                    return (FacesUtils.createSelectItem(String.valueOf(o), String.valueOf(o)));
                }
            }
        }
    }

    public static List<SelectItem> toSelectItemList(List any) {
        Class<?> lastEntityClass = null;
        EntityBuilder lastBuilder = null;
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<SelectItem> list = new ArrayList<>();
        for (Object o : any) {
            if (o == null) {
                list.add(FacesUtils.createSelectItem("", ""));
            } else if (o instanceof SelectItem) {
                list.add((SelectItem) o);
            } else if (o instanceof NamedId) {
                NamedId n = (NamedId) o;
                list.add(FacesUtils.createSelectItem(n.getStringId(), n.getStringName()));
            } else {
                Class<?> newClass = o.getClass();
                if (newClass.equals(lastEntityClass)) {
                    NamedId n = lastBuilder.objectToNamedId(o);
                    list.add(FacesUtils.createSelectItem(n.getStringId(), n.getStringName()));
                } else {
                    Entity entity = pu.findEntity(newClass);
                    if (entity != null) {
                        lastBuilder = entity.getBuilder();
                        NamedId n = lastBuilder.objectToNamedId(o);
                        list.add(FacesUtils.createSelectItem(n.getStringId(), n.getStringName()));
                        lastEntityClass = newClass;
                    } else {
                        list.add(FacesUtils.createSelectItem(String.valueOf(o), String.valueOf(o)));
                    }
                }
            }
        }
        return list;
    }

}
