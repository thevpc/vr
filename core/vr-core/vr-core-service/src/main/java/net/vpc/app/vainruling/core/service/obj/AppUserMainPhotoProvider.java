package net.vpc.app.vainruling.core.service.obj;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;

/**
 * Created by vpc on 4/15/17.
 */
public class AppUserMainPhotoProvider implements MainPhotoProvider {
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
