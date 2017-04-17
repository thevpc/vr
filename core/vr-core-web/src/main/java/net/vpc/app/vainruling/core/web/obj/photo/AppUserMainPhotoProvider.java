package net.vpc.app.vainruling.core.web.obj.photo;

import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.core.web.obj.MainPhotoProvider;
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
        return Vr.get().getUserPhoto(Convert.toInt(id, IntegerParserConfig.LENIENT_F));
    }
}
