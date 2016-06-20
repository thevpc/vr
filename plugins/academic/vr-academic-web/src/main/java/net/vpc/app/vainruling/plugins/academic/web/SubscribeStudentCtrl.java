/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppCivility;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppGender;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicBac;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicPreClass;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicStudentImport;
import net.vpc.common.jsf.FacesUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Inscription Etudiant",
        url = "modules/academic/subscribestudent",
        menu = "/Education/Config",
        securityKey = "Custom.Education.SubscribeStudent"
)
@ManagedBean
public class SubscribeStudentCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void onImport() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        try {
            p.importStudent(getModel().getStudent());
            FacesUtils.addInfoMessage(null, "Inscription reussie");
            getModel().setStudent(new AcademicStudentImport());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(null, "Inscription echouee");
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

        List<SelectItem> list = null;

        list = new ArrayList<>();
        for (AppPeriod x : core.findValidPeriods()) {
            list.add(new SelectItem(x.getName(), x.getName()));
        }
        getModel().setPeriods(list);

        list = new ArrayList<>();
        for (AppCivility x : core.findCivilities()) {
            list.add(new SelectItem(x.getName(), x.getName()));
        }
        getModel().setCivilities(list);

        list = new ArrayList<>();
        for (AppGender x : core.findGenders()) {
            list.add(new SelectItem(x.getCode(), x.getName()));
        }
        getModel().setGenders(list);

        list = new ArrayList<>();
        for (AcademicBac x : p.findAcademicBacs()) {
            list.add(new SelectItem(x.getName(), x.getName()));
        }
        getModel().setBacs(list);

        list = new ArrayList<>();
        for (AcademicPreClass x : p.findAcademicPreClasses()) {
            list.add(new SelectItem(x.getName(), x.getName()));
        }
        getModel().setPreps(list);

        list = new ArrayList<>();
        for (AppDepartment x : core.findDepartments()) {
            list.add(new SelectItem(x.getCode(), x.getName()));
        }
        getModel().setDepartments(list);

        list = new ArrayList<>();
        for (AcademicClass x : p.findAcademicClasses()) {
            list.add(new SelectItem(x.getName(), x.getName()));
        }
        getModel().setClasses(list);

    }

    public class Model {

        AcademicStudentImport student = new AcademicStudentImport();
        List<SelectItem> genders = new ArrayList<>();
        List<SelectItem> civilities = new ArrayList<>();
        List<SelectItem> periods = new ArrayList<>();
        List<SelectItem> departments = new ArrayList<>();
        List<SelectItem> bacs = new ArrayList<>();
        List<SelectItem> preps = new ArrayList<>();
        List<SelectItem> classes = new ArrayList<>();

        public AcademicStudentImport getStudent() {
            return student;
        }

        public void setStudent(AcademicStudentImport student) {
            this.student = student;
        }

        public List<SelectItem> getGenders() {
            return genders;
        }

        public void setGenders(List<SelectItem> genders) {
            this.genders = genders;
        }

        public List<SelectItem> getCivilities() {
            return civilities;
        }

        public void setCivilities(List<SelectItem> civilities) {
            this.civilities = civilities;
        }

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
        }

        public List<SelectItem> getDepartments() {
            return departments;
        }

        public void setDepartments(List<SelectItem> departments) {
            this.departments = departments;
        }

        public List<SelectItem> getBacs() {
            return bacs;
        }

        public void setBacs(List<SelectItem> bacs) {
            this.bacs = bacs;
        }

        public List<SelectItem> getPreps() {
            return preps;
        }

        public void setPreps(List<SelectItem> preps) {
            this.preps = preps;
        }

        public List<SelectItem> getClasses() {
            return classes;
        }

        public void setClasses(List<SelectItem> classes) {
            this.classes = classes;
        }

    }
}
