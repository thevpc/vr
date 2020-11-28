/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.web;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.web.jsf.VrJsf;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.model.imp.AcademicTeacherImport;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.upa.UPA;
import net.thevpc.upa.VoidAction;

import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Inscription Enseignant",
        url = "modules/academic/subscribe-teacher",
        menu = "/Contact",
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

    @VrOnPageLoad
    public void onRefresh(String cmd) {
        getModel().setTeacher(new AcademicTeacherImport());
        onRefresh();
    }

    public void updateLists() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);

        getModel().setPeriodItems(VrJsf.toSelectItemList(core.findValidPeriods()));
        getModel().setCivilityItems(VrJsf.toSelectItemList(core.findCivilities()));
        getModel().setGenderItems(VrJsf.toSelectItemList(core.findGenders()));
        getModel().setSituationItems(VrJsf.toSelectItemList(p.findTeacherSituations()));
        getModel().setOfficialDisciplineItems(VrJsf.toSelectItemList(p.findOfficialDisciplines()));
        getModel().setDegreeItems(VrJsf.toSelectItemList(p.findTeacherDegrees()));
        getModel().setDepartmentItems(VrJsf.toSelectItemList(core.findDepartments()));

    }

    public void onRefresh() {
        getModel().getTeacher().setDiscipline(null);
        getModel().getTeacher().setEmail(null);
        getModel().getTeacher().setFirstName(null);
        getModel().getTeacher().setFirstName2(null);
        getModel().getTeacher().setLastName(null);
        getModel().getTeacher().setLastName2(null);
        getModel().getTeacher().setNin(null);
        getModel().getTeacher().setPhone(null);
        getModel().getTeacher().setPhone(null);
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
