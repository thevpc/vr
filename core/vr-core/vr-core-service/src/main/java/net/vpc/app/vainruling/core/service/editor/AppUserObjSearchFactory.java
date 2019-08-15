package net.vpc.app.vainruling.core.service.editor;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 6/25/17.
 */
@ForEntity("AppUser")
@Component
public class AppUserObjSearchFactory extends AbstractEntityObjSearchFactory {

    @Override
    protected List filterContactsByProfileFilter0(List objects, String profileSearchText) {
        return VrApp.getBean(CorePlugin.class).filterUsersByProfileFilter(objects, profileSearchText, null, null);
    }
}
