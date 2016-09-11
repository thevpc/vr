package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultTeacherFilter;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeacherLoadFilterComponent {
    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public AppPeriod getPeriod() {
        String p = getModel().getSelectedPeriod();
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (StringUtils.isEmpty(p)) {
            AppConfig appConfig = core.findAppConfig();
            if (appConfig != null && appConfig.getMainPeriod() != null) {
                return appConfig.getMainPeriod();
            }
            return null;
        }
        return core.findPeriod(p);
    }

    public int getPeriodId() {
        String p = getModel().getSelectedPeriod();
        if (StringUtils.isEmpty(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig appConfig = core.findAppConfig();
            if (appConfig != null && appConfig.getMainPeriod() != null) {
                return appConfig.getMainPeriod().getId();
            }
            return -1;
        }
        return Integer.parseInt(p);
    }

    public void onInit() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        List<AppPeriod> navigatablePeriods = core.findNavigatablePeriods();
        AppPeriod mainPeriod = core.findAppConfig().getMainPeriod();
        getModel().setSelectedPeriod(null);
        getModel().getPeriodItems().clear();
        for (AppPeriod p : navigatablePeriods) {
            getModel().getPeriodItems().add(new SelectItem(String.valueOf(p.getId()), p.getName()));
            if (mainPeriod != null && p.getId() == mainPeriod.getId()) {
                getModel().setSelectedPeriod(String.valueOf(p.getId()));
            }
        }


        getModel().getOfficialDisciplinesItems().clear();
        for (AcademicOfficialDiscipline item : academicPlugin.findOfficialDisciplines()) {
            getModel().getOfficialDisciplinesItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

        getModel().getDepartmentItems().clear();

        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);

        for (AppDepartment item : core.findDepartments()) {
//            if(currentSession!=null &&)
            //TODO: should add filter to let see ust one's department?
            getModel().getDepartmentItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

        getModel().getSituationItems().clear();
        for (AcademicTeacherSituation item : academicPlugin.findTeacherSituations()) {
            getModel().getSituationItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

        getModel().getDegreeItems().clear();
        for (AcademicTeacherDegree item : academicPlugin.findTeacherDegrees()) {
            getModel().getDegreeItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }
    }

    public void onChangePeriod() {

    }

    public DefaultTeacherFilter getTeacherFilter() {
        DefaultTeacherFilter defaultTeacherFilter = new DefaultTeacherFilter();
        for (String s : getModel().getSelectedDegrees()) {
            defaultTeacherFilter.addAcceptedDegree(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedDepartments()) {
            defaultTeacherFilter.addAcceptedDepartment(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedOfficialDisciplines()) {
            defaultTeacherFilter.addAcceptedOfficialDisciplines(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedSituations()) {
            defaultTeacherFilter.addAcceptedSituation(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        return defaultTeacherFilter;
    }

//    public boolean containsRefreshFilter(String s) {
//        String[] f = getModel().getRefreshFilter();
//        return Arrays.asList(f).indexOf(s) >= 0;
//    }

    public static class Model {
//        String[] refreshFilter = {};
        List<SelectItem> refreshFilterItems;

        List<SelectItem> officialDisciplinesItems = new ArrayList<>();
        List<SelectItem> degreeItems = new ArrayList<>();
        List<SelectItem> situationItems = new ArrayList<>();
        List<SelectItem> departmentItems = new ArrayList<>();

        List<String> selectedOfficialDisciplines = new ArrayList<>();
        List<String> selectedDegrees = new ArrayList<>();
        List<String> selectedSituations = new ArrayList<>();
        List<String> selectedDepartments = new ArrayList<>();
        List<SelectItem> periodItems = new ArrayList<>();
        String selectedPeriod = null;

        public List<SelectItem> getPeriodItems() {
            return periodItems;
        }

        public void setPeriodItems(List<SelectItem> periodItems) {
            this.periodItems = periodItems;
        }

        public String getSelectedPeriod() {
            return selectedPeriod;
        }

        public void setSelectedPeriod(String selectedPeriod) {
            this.selectedPeriod = selectedPeriod;
        }

        public List<SelectItem> getOfficialDisciplinesItems() {
            return officialDisciplinesItems;
        }

        public void setOfficialDisciplinesItems(List<SelectItem> officialDisciplinesItems) {
            this.officialDisciplinesItems = officialDisciplinesItems;
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

        public List<String> getSelectedOfficialDisciplines() {
            return selectedOfficialDisciplines;
        }

        public void setSelectedOfficialDisciplines(List<String> selectedOfficialDisciplines) {
            this.selectedOfficialDisciplines = selectedOfficialDisciplines;
        }

        public List<String> getSelectedDegrees() {
            return selectedDegrees;
        }

        public void setSelectedDegrees(List<String> selectedDegrees) {
            this.selectedDegrees = selectedDegrees;
        }

        public List<String> getSelectedSituations() {
            return selectedSituations;
        }

        public void setSelectedSituations(List<String> selectedSituations) {
            this.selectedSituations = selectedSituations;
        }

        public List<String> getSelectedDepartments() {
            return selectedDepartments;
        }

        public void setSelectedDepartments(List<String> selectedDepartments) {
            this.selectedDepartments = selectedDepartments;
        }

//        public String[] getRefreshFilter() {
//            return refreshFilter;
//        }

//        public void setRefreshFilter(String[] refreshFilter) {
//            this.refreshFilter = refreshFilter;
//        }

        public List<SelectItem> getRefreshFilterItems() {
            return refreshFilterItems;
        }

        public void setRefreshFilterItems(List<SelectItem> refreshFilterItems) {
            this.refreshFilterItems = refreshFilterItems;
        }

    }
}
