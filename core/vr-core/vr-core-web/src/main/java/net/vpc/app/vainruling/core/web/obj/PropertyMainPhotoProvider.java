package net.vpc.app.vainruling.core.web.obj;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.Action;
import net.vpc.upa.Document;
import net.vpc.upa.UPA;

import java.io.IOException;

/**
 * Created by vpc on 4/16/17.
 */
public class PropertyMainPhotoProvider implements MainPhotoProvider {
    private final String propertyName;
    private final String defaultPath;

    public PropertyMainPhotoProvider(String propertyName, String defaultPath) {
        this.propertyName = propertyName;
        this.defaultPath = defaultPath;
    }

    @Override
    public String getMainPhotoPath(Object id, Object valueOrNull) {
        String val = ((Document) valueOrNull).getString(propertyName);
        if (StringUtils.isEmpty(val) && !StringUtils.isEmpty(defaultPath)) {
            val = defaultPath;
        }
        if (StringUtils.isEmpty(val)) {
            val = "private-theme-context://images/image.png";
        }
        return val;
    }

    @Override
    public String getMainIconPath(Object id, Object valueOrNull) {
        String val = ((Document) valueOrNull).getString(propertyName);
        if (StringUtils.isEmpty(val) && !StringUtils.isEmpty(defaultPath)) {
            val = defaultPath;
        }
        if (StringUtils.isEmpty(val)) {
            val = "private-theme-context://images/image.png";
        }else{
            if(val.startsWith("/")) {
                String finalVal = val;
                return UPA.getContext().invokePrivileged(new Action<String>() {
                    @Override
                    public String run() {
                        VFile file = CorePlugin.get().getRootFileSystem().get(finalVal);
                        if(file.isFile()) {
                            try {
                                return VrUtils.getOrResizeIcon(file).getPath();
                            } catch (IOException e) {
                                //
                            }
                        }
                        return finalVal;
                    }
                });
            }
        }
        return val;
    }
}
