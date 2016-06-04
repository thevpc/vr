/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.admin.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppRightName;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.common.strings.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
@ManagedBean
@Scope("session")
public class UpdateProfileRightsActionCtrl {

    private static final Logger log = Logger.getLogger(UpdateProfileRightsActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public static class Config {

        private String profile;

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

    }

    public void openDialog(String config) {
        openDialog(VrHelper.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        if (config == null) {
            config = new Config();
        }
        if (!StringUtils.isEmpty(config.profile)) {
            int profileId = Integer.parseInt(config.profile);
            List<AppRightName>[] list = core.findProfileRightNamesDualList(profileId);
            Comparator<AppRightName> comp = new Comparator<AppRightName>() {
                @Override
                public int compare(AppRightName o1, AppRightName o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
            Collections.sort(list[0], comp);
            Collections.sort(list[1],comp);
            DualListModel<AppRightName> v = new DualListModel<AppRightName>();
            v.setSource(list[1]);
            v.setTarget(list[0]);
            getModel().setValues(v);
            getModel().setProfileId(profileId);
        }else{
            DualListModel<AppRightName> v = new DualListModel<AppRightName>();
            getModel().setValues(v);
            getModel().setProfileId(-1);
        }
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/admin/updateProfileRightsDialog", options, null);

    }

  

    public void save() {
        List t = getModel().getValues().getTarget();
        List<String> rights=new ArrayList<>();
        for (Object tt : t) {
            //primefaces withh "stringify" AppRightName instances
            String name=null;
            if(tt instanceof AppRightName){
                name=((AppRightName)tt).getName();
            }else{
                name=((String)tt);
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

    public Model getModel() {
        return model;
    }

}
