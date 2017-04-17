package net.vpc.app.vainruling.core.web.obj;

import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Document;

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
            val = "theme-context://images/image.png";
        }
        return val;
    }
}
