package net.thevpc.app.vainruling.core.service.editor;

import net.thevpc.app.vainruling.VrEntityName;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Created by vpc on 6/25/17.
 */
@VrEntityName("AppUser")
@Component
public class AppUserByProfileEditorSearch extends ProfileBasedEntityEditorSearch {

    @Override
    protected List filterDocumentByProfileFilter(List objects, String profileSearchText) {
        return VrApp.getBean(CorePlugin.class).filterUsersByProfileFilter(objects, profileSearchText, null, null);
    }
}
