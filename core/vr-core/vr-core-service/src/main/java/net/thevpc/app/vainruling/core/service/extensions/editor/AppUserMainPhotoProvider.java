package net.thevpc.app.vainruling.core.service.extensions.editor;

import net.thevpc.app.vainruling.VrEditorMainPhotoProvider;
import net.thevpc.app.vainruling.VrEntityName;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.common.util.Convert;
import net.thevpc.common.util.IntegerParserConfig;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 4/15/17.
 */
@VrEntityName("AppUser")
@Component
public class AppUserMainPhotoProvider implements VrEditorMainPhotoProvider {
    @Override
    public String getMainPhotoPath(Object id, Object valueOrNull) {
        if(id==null){
            return null;
        }
        return CorePlugin.get().getUserPhoto(Convert.toInt(id, IntegerParserConfig.LENIENT_F));
    }

    @Override
    public String getMainIconPath(Object id, Object valueOrNull) {
        if(id==null){
            return null;
        }
        return CorePlugin.get().getUserIcon(Convert.toInt(id, IntegerParserConfig.LENIENT_F));
    }
}
