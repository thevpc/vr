/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.actions;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.core.service.model.AppRightName;
import net.thevpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.util.Convert;
import net.thevpc.common.util.IntegerParserConfig;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage
public class UpdateProfileRightsActionCtrl {

    private static final Logger log = Logger.getLogger(UpdateProfileRightsActionCtrl.class.getName());
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
        if (!StringUtils.isBlank(config.profile)) {
            int profileId = Convert.toInt(config.profile,IntegerParserConfig.LENIENT_F);
            List<AppRightName>[] list = core.findProfileRightNamesDualList(profileId);
            Comparator<AppRightName> comp = new Comparator<AppRightName>() {
                @Override
                public int compare(AppRightName o1, AppRightName o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
            Collections.sort(list[0], comp);
            Collections.sort(list[1], comp);
            DualListModel<AppRightName> v = new DualListModel<AppRightName>();
            v.setSource(list[1]);
            v.setTarget(list[0]);
            getModel().setValues(v);
            getModel().setProfileId(profileId);
        } else {
            DualListModel<AppRightName> v = new DualListModel<AppRightName>();
            getModel().setValues(v);
            getModel().setProfileId(-1);
        }
        new DialogBuilder("/modules/admin/update-profile-rights-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();

    }

    public void save() {
        List t = getModel().getValues().getTarget();
        List<String> rights = new ArrayList<>();
        for (Object tt : t) {
            //primefaces withh "stringify" AppRightName instances
            String name = null;
            if (tt instanceof AppRightName) {
                name = ((AppRightName) tt).getName();
            } else {
                name = ((String) tt);
            }
            rights.add(name);
        }
        core.setProfileRights(getModel().getProfileId(), rights);
        fireEventExtraDialogClosed();
    }

    public void onChange() {

    }

    public void fireEventExtraDialogClosed() {
        DialogBuilder.closeCurrent();
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
        private DualListModel<AppRightName> values;

        public DualListModel<AppRightName> getValues() {
            return values;
        }

        public void setValues(DualListModel<AppRightName> values) {
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
