/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.jsf.VrJsf;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicStudentImport;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Inscription Etudiant",
        url = "modules/academic/subscribe-student",
        menu = "/Education/Config",
        securityKey = "Custom.Education.SubscribeStudent"
)
public class SubscribeStudentCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void onImport() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        try {
            UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    try {
                        p.importStudent(-1, getModel().getStudent());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            FacesUtils.addInfoMessage("Inscription reussie");
            getModel().setStudent(new AcademicStudentImport());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Inscription echouee");
            e.printStackTrace();
        }
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();

    }

    public void onRefresh() {
        getModel().setStudent(new AcademicStudentImport());
        updateLists();
    }

    public void onFiltersChanged() {
        //onRefresh();
    }

    public void updateLists() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setPeriodItems(VrJsf.toSelectItemList(core.findValidPeriods()));
        getModel().setCivilityItems(VrJsf.toSelectItemList(core.findCivilities()));
        getModel().setGenderItems(VrJsf.toSelectItemList(core.findGenders()));
        getModel().setBacItems(VrJsf.toSelectItemList(p.findAcademicBacs()));
        getModel().setPrepItems(VrJsf.toSelectItemList(p.findAcademicPreClasses()));
        getModel().setDepartmentItems(VrJsf.toSelectItemList(core.findDepartments()));
        getModel().setClassItems(VrJsf.toSelectItemList(p.findAcademicClasses()));

    }

    public class Model {

        AcademicStudentImport student = new AcademicStudentImport();
        List<SelectItem> genderItems = new ArrayList<>();
        List<SelectItem> civilityItems = new ArrayList<>();
        List<SelectItem> periodItems = new ArrayList<>();
        List<SelectItem> departmentItems = new ArrayList<>();
        List<SelectItem> bacItems = new ArrayList<>();
        List<SelectItem> prepItems = new ArrayList<>();
        List<SelectItem> classItems = new ArrayList<>();

        public AcademicStudentImport getStudent() {
            return student;
        }

        public void setStudent(AcademicStudentImport student) {
            this.student = student;
        }

        public List<SelectItem> getGenderItems() {
            return genderItems;
        }

        public void setGenderItems(List<SelectItem> genderItems) {
            this.genderItems = genderItems;
        }

        public List<SelectItem> getCivilityItems() {
            return civilityItems;
        }

        public void setCivilityItems(List<SelectItem> civilityItems) {
            this.civilityItems = civilityItems;
        }

        public List<SelectItem> getPeriodItems() {
            return periodItems;
        }

        public void setPeriodItems(List<SelectItem> periodItems) {
            this.periodItems = periodItems;
        }

        public List<SelectItem> getDepartmentItems() {
            return departmentItems;
        }

        public void setDepartmentItems(List<SelectItem> departmentItems) {
            this.departmentItems = departmentItems;
        }

        public List<SelectItem> getBacItems() {
            return bacItems;
        }

        public void setBacItems(List<SelectItem> bacItems) {
            this.bacItems = bacItems;
        }

        public List<SelectItem> getPrepItems() {
            return prepItems;
        }

        public void setPrepItems(List<SelectItem> prepItems) {
            this.prepItems = prepItems;
        }

        public List<SelectItem> getClassItems() {
            return classItems;
        }

        public void setClassItems(List<SelectItem> classItems) {
            this.classItems = classItems;
        }

    }
}
