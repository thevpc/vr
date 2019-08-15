package net.vpc.app.vainruling.core.service.extensions.data;

import java.util.Map;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.upa.UPAI18n;
import net.vpc.upa.UPAObject;
import net.vpc.upa.types.I18NString;

public class I18nHolder implements UPAI18n {
    public static final I18nHolder INSTANCE=new I18nHolder();
    @Override
    public String get(UPAObject s, Map<String,Object> params) {
        return VrApp.getBean(I18n.class).get(s,params);
    }

    @Override
    public String get(I18NString s, Map<String,Object> params) {
        return VrApp.getBean(I18n.class).get(s,params);
    }

    @Override
    public String getEnum(Object obj) {
        return VrApp.getBean(I18n.class).getEnum(obj);
    }
}
