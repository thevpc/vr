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
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicTeacherImport;
import net.vpc.common.jsf.FacesUtils;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Inscription Enseignant",
        url = "modules/academic/subscribe-teacher",
        menu = "/Education/Config",
        securityKey = "Custom.Education.SubscribeTeacher"
)
public class SubscribeTeacherCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void onImport() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        try {
            p.importTeacher(-1, getModel().getTeacher());
            FacesUtils.addInfoMessage(null, "Inscription reussie");
            onRefresh();
        } catch (Exception e) {
            FacesUtils.addErrorMessage(null, "Inscription echouee");
            e.printStackTrace();
        }
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();
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
        for (AcademicTeacherSituation x : p.findTeacherSituations()) {
            list.add(new SelectItem(x.getName(), x.getName()));
        }
        getModel().setSituations(list);

        list = new ArrayList<>();
        for (AcademicTeacherDegree x : p.findTeacherDegrees()) {
            list.add(new SelectItem(x.getCode(), x.getName()));
        }
        getModel().setDegrees(list);

        list = new ArrayList<>();
        for (AppDepartment x : core.findDepartments()) {
            list.add(new SelectItem(x.getCode(), x.getName()));
        }
        getModel().setDepartments(list);

    }

    public void onRefresh() {
        getModel().setTeacher(new AcademicTeacherImport());
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        int semesterMaxWeeks = ap.getSemesterMaxWeeks();
        getModel().getTeacher().setWeekLoads(new int[]{semesterMaxWeeks, semesterMaxWeeks});
        updateLists();
    }

    public void onFiltersChanged() {
        //onRefresh();
    }

    public class Model {

        AcademicTeacherImport teacher = new AcademicTeacherImport();
        List<SelectItem> genders = new ArrayList<>();
        List<SelectItem> civilities = new ArrayList<>();
        List<SelectItem> periods = new ArrayList<>();
        List<SelectItem> degrees = new ArrayList<>();
        List<SelectItem> situations = new ArrayList<>();
        List<SelectItem> departments = new ArrayList<>();

        public AcademicTeacherImport getTeacher() {
            return teacher;
        }

        public void setTeacher(AcademicTeacherImport teacher) {
            this.teacher = teacher;
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

        public List<SelectItem> getDegrees() {
            return degrees;
        }

        public void setDegrees(List<SelectItem> degrees) {
            this.degrees = degrees;
        }

        public List<SelectItem> getSituations() {
            return situations;
        }

        public void setSituations(List<SelectItem> situations) {
            this.situations = situations;
        }

        public List<SelectItem> getDepartments() {
            return departments;
        }

        public void setDepartments(List<SelectItem> departments) {
            this.departments = departments;
        }

    }

}
