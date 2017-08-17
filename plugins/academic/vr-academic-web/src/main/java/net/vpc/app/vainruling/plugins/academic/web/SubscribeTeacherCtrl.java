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
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicTeacherImport;
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
//        title = "Inscription Enseignant",
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
            UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    try {
                        p.importTeacher(-1, getModel().getTeacher());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            FacesUtils.addInfoMessage("Inscription reussie");
            onRefresh();
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Inscription echouee : "+e.getMessage());
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
            list.add(new SelectItem(x.getId(), x.getName()));
        }
        getModel().setPeriodItems(list);

        list = new ArrayList<>();
        for (AppCivility x : core.findCivilities()) {
            list.add(new SelectItem(x.getId(), x.getName()));
        }
        getModel().setCivilityItems(list);

        list = new ArrayList<>();
        for (AppGender x : core.findGenders()) {
            list.add(new SelectItem(x.getId(), x.getName()));
        }
        getModel().setGenderItems(list);

        list = new ArrayList<>();
        for (AcademicTeacherSituation x : p.findTeacherSituations()) {
            list.add(new SelectItem(x.getId(), x.getName()));
        }
        getModel().setSituationItems(list);

        list = new ArrayList<>();
        for (AcademicOfficialDiscipline x : p.findOfficialDisciplines()) {
            list.add(new SelectItem(x.getId(), x.getName()));
        }
        getModel().setOfficialDisciplineItems(list);

        list = new ArrayList<>();
        for (AcademicTeacherDegree x : p.findTeacherDegrees()) {
            list.add(new SelectItem(x.getId(), x.getName()));
        }
        getModel().setDegreeItems(list);

        list = new ArrayList<>();
        for (AppDepartment x : core.findDepartments()) {
            list.add(new SelectItem(x.getId(), x.getName()));
        }
        getModel().setDepartmentItems(list);

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
        List<SelectItem> genderItems = new ArrayList<>();
        List<SelectItem> civilityItems = new ArrayList<>();
        List<SelectItem> periodItems = new ArrayList<>();
        List<SelectItem> degreeItems = new ArrayList<>();
        List<SelectItem> situationItems = new ArrayList<>();
        List<SelectItem> officialDisciplineItems = new ArrayList<>();
        List<SelectItem> departmentItems = new ArrayList<>();

        public AcademicTeacherImport getTeacher() {
            return teacher;
        }

        public void setTeacher(AcademicTeacherImport teacher) {
            this.teacher = teacher;
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

        public List<SelectItem> getDegreeItems() {
            return degreeItems;
        }

        public void setDegreeItems(List<SelectItem> degreeItems) {
            this.degreeItems = degreeItems;
        }

        public List<SelectItem> getSituationItems() {
            return situationItems;
        }

        public void setSituationItems(List<SelectItem> situationItems) {
            this.situationItems = situationItems;
        }

        public List<SelectItem> getDepartmentItems() {
            return departmentItems;
        }

        public void setDepartmentItems(List<SelectItem> departmentItems) {
            this.departmentItems = departmentItems;
        }

        public List<SelectItem> getOfficialDisciplineItems() {
            return officialDisciplineItems;
        }

        public void setOfficialDisciplineItems(List<SelectItem> officialDisciplineItems) {
            this.officialDisciplineItems = officialDisciplineItems;
        }
    }

}
