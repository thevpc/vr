package net.vpc.app.vainruling.core.service.extensions.editor;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.editor.ForEntity;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.app.vainruling.core.service.editor.EntityEditorMainPhotoProvider;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 4/15/17.
 */
@ForEntity("AppUser")
@Component
public class AppUserMainPhotoProvider implements EntityEditorMainPhotoProvider {
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
