/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.VrControllerInfo;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;

import java.util.logging.Logger;
import net.vpc.app.vainruling.core.web.VrControllerInfoResolver;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController
public class MyProfileCtrl implements VrControllerInfoResolver {

    private static final Logger log = Logger.getLogger(MyProfileCtrl.class.getName());

    @Override
    public VrControllerInfo resolveVrControllerInfo(String cmd) {
        VrMenuManager mm = VrApp.getBean(VrMenuManager.class);
        VrControllerInfo o = mm.resolveBestVrControllerInfo(MyProfileAlternative.class, cmd);
        if (o == null) {
            return new VrControllerInfo(
                    "My Profile",
                    "",
                    "modules/config/my-profile",
                    "",
                    "Custom.MyProfile"
            );
        }
        return o;
    }

//    private Model model = new Model();
//
//    public Model getModel() {
//        return model;
//    }
//
//    @OnPageLoad
//    public void onLoad() {
//        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
//
//        getModel().getPublicThemes().clear();
//        getModel().getPublicThemes().add(FacesUtils.createSelectItem("", "<Default>", null));
//        for (VrTheme vrTheme : tfactory.getThemes(VrThemeFace.PUBLIC)) {
//            getModel().getPublicThemes().add(FacesUtils.createSelectItem(vrTheme.getId(), vrTheme.getName(), null));
//        }
//        getModel().setPublicTheme(CorePlugin.get().getCurrentUserPublicTheme());
//
//        getModel().getPrivateThemes().clear();
//        getModel().getPrivateThemes().add(FacesUtils.createSelectItem("", "<Default>", null));
//        for (VrTheme vrTheme : tfactory.getThemes(VrThemeFace.PRIVATE)) {
//            getModel().getPrivateThemes().add(FacesUtils.createSelectItem(vrTheme.getId(), vrTheme.getName(), null));
//        }
//        getModel().setPrivateTheme(CorePlugin.get().getCurrentUserPrivateTheme());
//    }
//
//    public void onSelectTheme() {
//        final CorePlugin t = VrApp.getBean(CorePlugin.class);
//        try {
//            t.setCurrentUserPublicTheme(getModel().getPublicTheme());
//            t.setCurrentUserPrivateTheme(getModel().getPrivateTheme());
//            FacesUtils.addInfoMessage("Theme mis a jour");
//        } catch (Exception ex) {
//            log.log(Level.SEVERE, "Error", ex);
//            FacesUtils.addErrorMessage(ex);
//        }
//    }
//
//    public static class Model {
//
//        private String publicTheme;
//        private List<SelectItem> publicThemes=new ArrayList<>();
//        private String privateTheme;
//        private List<SelectItem> privateThemes=new ArrayList<>();
//
//        public String getPublicTheme() {
//            return publicTheme;
//        }
//
//        public void setPublicTheme(String publicTheme) {
//            this.publicTheme = publicTheme;
//        }
//
//        public List<SelectItem> getPublicThemes() {
//            return publicThemes;
//        }
//
//        public void setPublicThemes(List<SelectItem> publicThemes) {
//            this.publicThemes = publicThemes;
//        }
//
//        public String getPrivateTheme() {
//            return privateTheme;
//        }
//
//        public void setPrivateTheme(String privateTheme) {
//            this.privateTheme = privateTheme;
//        }
//
//        public List<SelectItem> getPrivateThemes() {
//            return privateThemes;
//        }
//
//        public void setPrivateThemes(List<SelectItem> privateThemes) {
//            this.privateThemes = privateThemes;
//        }
//    }
}
