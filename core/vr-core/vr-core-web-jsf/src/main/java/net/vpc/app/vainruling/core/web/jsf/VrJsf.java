package net.vpc.app.vainruling.core.web.jsf;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;
import net.vpc.app.vainruling.core.service.fs.VFileInfo;
import net.vpc.app.vainruling.core.web.util.DocumentsUtils;
import net.vpc.common.io.FileUtils;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.upa.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.model.SelectItem;

public class VrJsf {
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
        event.getFile().write(f.getPath());
        return rootfs.get(path).getParentFile().get(f.getName());
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
