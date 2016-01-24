/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.plugins.academic.internship.service.AcademicInternshipPlugin;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import org.primefaces.context.RequestContext;
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
public class CreateIntershipsActionCtrl {

    private static final Logger log = Logger.getLogger(CreateIntershipsActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public void openDialog(AcademicInternship internship) {
        getModel().setDisabled(true);
        getModel().setMessage("");
        getModel().setProfile("");
        getModel().setInternship(internship);

        if (internship.getBoard()== null) {
            getModel().setMessage("Merci de preciser la commission");
        } else if (internship.getInternshipStatus() == null) {
            getModel().setMessage("Merci de preciser la phase");
        }else{
            getModel().setDisabled(false);
        }

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/internship/createintershipsDialog", options, null);

    }

    public void save() {
        VrApp.getBean(AcademicInternshipPlugin.class).generateInternships(getModel().getInternship(), getModel().getProfile());
    }

    public void onChange() {

    }

    public void fireEventExtraDialogClosed() {
        RequestContext.getCurrentInstance().closeDialog(null);
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

    public Model getModel() {
        return model;
    }

}
