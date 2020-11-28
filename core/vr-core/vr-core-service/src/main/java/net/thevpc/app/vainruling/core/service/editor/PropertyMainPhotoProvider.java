package net.thevpc.app.vainruling.core.service.editor;

import net.thevpc.app.vainruling.VrEditorMainPhotoProvider;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.Document;

/**
 * Created by vpc on 4/16/17.
 */
public class PropertyMainPhotoProvider implements VrEditorMainPhotoProvider {

    private final String propertyName;
    private final String defaultPath;

    public PropertyMainPhotoProvider(String propertyName, String defaultPath) {
        this.propertyName = propertyName;
        this.defaultPath = defaultPath;
    }

    @Override
    public String getMainPhotoPath(Object id, Object valueOrNull) {
        String val = ((Document) valueOrNull).getString(propertyName);
        if (StringUtils.isBlank(val) && !StringUtils.isBlank(defaultPath)) {
            val = defaultPath;
        }
        if (StringUtils.isBlank(val)) {
            val = "private-theme-context://images/image.png";
        }
        return val;
    }

    @Override
    public String getMainIconPath(Object id, Object valueOrNull) {
        String val = ((Document) valueOrNull).getString(propertyName);
        return VrUtils.getIconPath(val, defaultPath);
    }
}
