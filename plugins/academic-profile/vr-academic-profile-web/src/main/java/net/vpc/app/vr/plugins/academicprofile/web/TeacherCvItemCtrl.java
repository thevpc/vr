/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.plugins.academicprofile.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.springframework.beans.factory.annotation.Autowired;
import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCVItem;

/**
 *
 * @author olfa
 */
//@VrController(
//        url = "modules/academic/profile/add-teacher-course"
//)
public class TeacherCvItemCtrl {

    @Autowired
    private AcademicProfilePlugin app;
    
    private static final Logger log = Logger.getLogger(TeacherCvItemCtrl.class.getName());
    
    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void addNewItem() {

        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().cvItem);
                    FacesUtils.addInfoMessage(null, "Opération d'ajout réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }

    public static class Model {
        
        private AcademicTeacherCVItem cvItem = new AcademicTeacherCVItem();
        List<AppCompany> companyList = new ArrayList<>();

        public AcademicTeacherCVItem getCvItem() {
            return cvItem;
        }

        public void setCvItem(AcademicTeacherCVItem cvItem) {
            this.cvItem = cvItem;
        }

        public List<AppCompany> getCompanyList() {
            return companyList;
        }

        public void setCompanyList(List<AppCompany> companyList) {
            this.companyList = companyList;
        }

    }

}
