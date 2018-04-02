/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.common.strings.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.logging.Logger;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import org.springframework.stereotype.Controller;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController
@Controller
public class UpdateProfileUsersActionCtrl {

    private static final Logger log = Logger.getLogger(UpdateProfileUsersActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        if (config == null) {
            config = new Config();
        }
        int profileId = (!StringUtils.isEmpty(config.profile)) ? Convert.toInt(config.profile, IntegerParserConfig.LENIENT_F) : -1;
        setProfile(profileId);
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        options.put("width", 500);
        options.put("height", 350);
//        options.put("contentWidth", "100%");
//        options.put("contentHeight", "100%");
        RequestContext.getCurrentInstance().openDialog("/modules/admin/update-profile-users-dialog", options, null);

    }

    public void setProfile(int profileId) {
        if (profileId >= 0) {
            List<AppUser>[] list = core.findProfileUsersDualList(profileId);
            Comparator<AppUser> comp = new Comparator<AppUser>() {
                @Override
                public int compare(AppUser o1, AppUser o2) {
                    return o1.getLogin().compareTo(o2.getLogin());
                }
            };
            Collections.sort(list[0], comp);
            Collections.sort(list[1], comp);
            DualListModel<AppUser> v = new DualListModel<AppUser>();
            v.setSource(list[1]);
            v.setTarget(list[0]);
            getModel().setValues(v);
            getModel().setProfileId(profileId);
        } else {
            DualListModel<AppUser> v = new DualListModel<AppUser>();
            getModel().setValues(v);
            getModel().setProfileId(-1);
        }
    }

    public void save() {
        List t = getModel().getValues().getTarget();
        List<String> users = new ArrayList<>();
        for (Object tt : t) {
            String name = null;
            if (tt instanceof AppUser) {
                name = ((AppUser) tt).getLogin();
            } else {
                name = ((String) tt);
            }
            users.add(name);
        }
        core.setProfileUsers(getModel().getProfileId(), users);
        fireEventExtraDialogClosed();
    }

    public void onChange() {

    }

    public void fireEventExtraDialogClosed() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public Model getModel() {
        return model;
    }

    public static class Config {

        private String profile;

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

    }

    public static class Model {

        private int profileId;
        private DualListModel<AppUser> values;

        public DualListModel<AppUser> getValues() {
            return values;
        }

        public void setValues(DualListModel<AppUser> values) {
            this.values = values;
        }

        public int getProfileId() {
            return profileId;
        }

        public void setProfileId(int profileId) {
            this.profileId = profileId;
        }

    }

}
