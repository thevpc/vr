package net.vpc.app.vainruling.core.service.obj;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.upa.Document;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 6/25/17.
 */
public class AppUserObjSearchFactory extends AbstractEntityObjSearchFactory {
    @Override
    protected List filterContactsByProfileFilter0(List objects, String profileSearchText) {
        return VrApp.getBean(CorePlugin.class).filterUsersByProfileFilter(objects,profileSearchText,null);
    }
}
