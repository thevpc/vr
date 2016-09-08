/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.internship.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
@Scope("session")
public class CreateInternshipsActionCtrl {

    private static final Logger log = Logger.getLogger(CreateInternshipsActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public void openDialog(AcademicInternship internship) {
        getModel().setDisabled(true);
        getModel().setMessage("");
        getModel().setProfile("");
        getModel().setInternship(internship);

        if (internship.getBoard() == null) {
            getModel().setMessage("Merci de preciser le Comité");
        } else if (internship.getInternshipStatus() == null) {
            getModel().setMessage("Merci de preciser l'étape");
        } else {
            getModel().setDisabled(false);
        }

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/internship/create-interships-dialog", options, null);

    }

    public void save() {
        VrApp.getBean(AcademicPlugin.class).generateInternships(getModel().getInternship().getId(), getModel().getProfile());
    }

    public void onChange() {

    }

    public void fireEventExtraDialogClosed() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private String profile;
        private boolean disabled;
        private String message;
        private AcademicInternship internship;

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public AcademicInternship getInternship() {
            return internship;
        }

        public void setInternship(AcademicInternship internship) {
            this.internship = internship;
        }

        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
