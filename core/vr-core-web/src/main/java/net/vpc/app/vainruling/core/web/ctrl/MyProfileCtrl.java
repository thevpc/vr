/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.themes.VrTheme;
import net.vpc.app.vainruling.core.web.themes.VrThemeFactory;
import net.vpc.common.jsf.FacesUtils;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
//        title = "Mon Profil", css = "fa-dashboard",
        url = "modules/config/my-profile",
        menu = "/Config",
        securityKey = "Custom.MyProfile"
)
public class MyProfileCtrl {

    private static final Logger log = Logger.getLogger(MyProfileCtrl.class.getName());

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    public void onLoad() {
        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
        getModel().getThemes().clear();
        getModel().getThemes().add(FacesUtils.createSelectItem("", "<Default>", null));
        for (VrTheme vrTheme : tfactory.getThemes()) {
            getModel().getThemes().add(FacesUtils.createSelectItem(vrTheme.getId(), vrTheme.getName(), null));
        }
        getModel().setTheme(CorePlugin.get().getCurrentUserTheme());
    }

    public void onSelectTheme() {
        final CorePlugin t = VrApp.getBean(CorePlugin.class);
        try {
            t.setCurrentUserTheme(getModel().getTheme());
            FacesUtils.addInfoMessage("Theme mis a jour");
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public static class Model {

        private String theme;
        private List<SelectItem> themes=new ArrayList<>();

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public List<SelectItem> getThemes() {
            return themes;
        }

        public void setThemes(List<SelectItem> themes) {
            this.themes = themes;
        }
    }
}
