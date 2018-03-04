/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.admin;

import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.core.web.VrController;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.admin.actions.UpdateProfileUsersActionCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import net.vpc.app.vainruling.core.web.VrActionEnabler;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
            @UPathItem(title = "Admin", css = "fa-dashboard", ctrl = "")},
        menu = "/Admin/Security",
        url = "/modules/admin/my-profiles"//,
//        securityKey = CorePluginSecurity.RIGHT_CUSTOM_UPDATE_MY_PROFILES
)
@Controller
public class UpdateMyProfilesCtrl implements VrActionEnabler {

    private static final Logger log = Logger.getLogger(UpdateMyProfilesCtrl.class.getName());
    private Model model = new Model();
    @Autowired
    private CorePlugin core;
    private List<AppProfile> cachedAdministrableProfiles;

    @OnPageLoad
    public void onInit() {
        getModel().setProfile(null);
        refresh();
    }

    @Override
    public boolean isEnabled(net.vpc.app.vainruling.core.web.VrActionInfo data) {
        if (cachedAdministrableProfiles == null) {
            cachedAdministrableProfiles = core.findAdministrableProfiles();
        }
        return !cachedAdministrableProfiles.isEmpty();
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
