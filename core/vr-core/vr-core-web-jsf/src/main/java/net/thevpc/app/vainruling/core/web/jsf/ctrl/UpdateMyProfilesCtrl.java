/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.VrActionEnabler;
import net.thevpc.app.vainruling.VrActionInfo;
import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.actions.UpdateProfileUsersActionCtrl;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.AppProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Admin", css = "fa-dashboard", ctrl = "")},
        menu = "/Contact",
        url = "/modules/admin/my-profiles"//,
//        securityKey = CorePluginSecurity.RIGHT_CUSTOM_UPDATE_MY_PROFILES
)
@Controller
public class UpdateMyProfilesCtrl implements VrActionEnabler {

    private static final Logger log = Logger.getLogger(UpdateMyProfilesCtrl.class.getName());
    private final Model model = new Model();
    @Autowired
    private CorePlugin core;
    private List<AppProfile> cachedAdministrableProfiles;

    @VrOnPageLoad
    public void onInit() {
        getModel().setProfile(null);
        refresh();
    }

    @Override
    public void checkEnabled(VrActionInfo data) {
        if (cachedAdministrableProfiles == null) {
            cachedAdministrableProfiles = core.findAdministrableProfiles();
        }
        if (cachedAdministrableProfiles.isEmpty()) {
            throw new SecurityException("There is no Administrable profiles");
        }
    }

    public void refresh() {
        getModel().setProfiles(core.findAdministrableProfiles());
        if (getModel().getProfiles().isEmpty()) {
            getModel().setProfile(null);
        } else {
            getModel().setProfile(getModel().getProfiles().get(0));
        }
        onChange();
    }

    public void onChange() {
        AppProfile profile = getModel().getProfile();
        VrApp.getBean(UpdateProfileUsersActionCtrl.class).setProfile(profile == null ? -1 : profile.getId());
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private AppProfile profile;
        private List<AppProfile> profiles = new ArrayList<>();

        public AppProfile getProfile() {
            return profile;
        }

        public void setProfile(AppProfile profile) {
            this.profile = profile;
        }

        public List<AppProfile> getProfiles() {
            return profiles;
        }

        public void setProfiles(List<AppProfile> profiles) {
            this.profiles = profiles;
        }

    }

}
