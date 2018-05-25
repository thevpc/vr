/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppRightName;
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
import org.primefaces.PrimeFaces;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController
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
        if (!StringUtils.isEmpty(config.profile)) {
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
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        PrimeFaces.current().dialog().openDynamic("/modules/admin/update-profile-rights-dialog", options, null);

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
