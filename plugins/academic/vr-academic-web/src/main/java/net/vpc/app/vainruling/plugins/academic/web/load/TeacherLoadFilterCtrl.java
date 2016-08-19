package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgramType;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.DeviationConfig;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.DeviationGroup;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultTeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TeacherLoadFilterCtrl {
    private Model model=new Model();

    public Model getModel() {
        return model;
    }

    public int getPeriodId() {
        String p = getModel().getSelectedPeriod();
        if (StringUtils.isEmpty(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig appConfig = core.findAppConfig();
            if(appConfig!=null && appConfig.getMainPeriod()!=null) {
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

        UserSession currentSession = UserSession.get();
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
        getModel().getProgramTypeItems().clear();
        for (AcademicProgramType pt : a.findProgramTypes()) {
            getModel().getProgramTypeItems().add(FacesUtils.createSelectItem(String.valueOf(pt.getId()), pt.getName(), "vr-checkbox"));
        }
        getModel().getDeviationGroupItems().clear();
        for (DeviationGroup deviationGroup : DeviationGroup.values()) {
            getModel().getDeviationGroupItems().add(FacesUtils.createSelectItem(String.valueOf(deviationGroup.name()), deviationGroup.name(), "vr-checkbox"));
        }
        getModel().setRefreshFilter(new String[]{"intents","deviation-extra","deviation-week"});
    }

    public void onChangePeriod() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        List<SelectItem> refreshableFilters = new ArrayList<>();
        refreshableFilters.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
        refreshableFilters.add(FacesUtils.createSelectItem("deviation-week", "Balance/Sem", "vr-checkbox"));
        refreshableFilters.add(FacesUtils.createSelectItem("deviation-extra", "Balance/Supp", "vr-checkbox"));
        getModel().setRefreshFilterItems(refreshableFilters);


        getModel().getLabelItems().clear();

        int periodId = getPeriodId();
        if(periodId>=-1) {
            for (String label : a.findCoursePlanLabels(periodId)) {
                getModel().getLabelItems().add(FacesUtils.createSelectItem(label, label, "vr-checkbox"));
                getModel().getLabelItems().add(FacesUtils.createSelectItem("!" + label, "!" + label, "vr-checkbox"));
            }
        }
    }

    public DeviationConfig getDeviationConfig(){
        DeviationConfig deviationConfig = new DeviationConfig();
        for (String item : getModel().getSelectedDeviationGroups()) {
            deviationConfig.getGroups().add(DeviationGroup.valueOf(item.toUpperCase()));
        }
        deviationConfig.setExtraBased(containsRefreshFilter("deviation-extra"));
        deviationConfig.setWeekBased(containsRefreshFilter("deviation-week"));
        return deviationConfig;
    }

    public TeacherFilter getTeacherFilter(){
        DefaultTeacherFilter defaultTeacherFilter =new DefaultTeacherFilter();
        for (String s : getModel().getSelectedDegrees()) {
            defaultTeacherFilter.addAcceptedDegree(StringUtils.isEmpty(s)?null:Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedDepartments()) {
            defaultTeacherFilter.addAcceptedDepartment(StringUtils.isEmpty(s)?null:Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedOfficialDisciplines()) {
            defaultTeacherFilter.addAcceptedOfficialDisciplines(StringUtils.isEmpty(s)?null:Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedSituations()) {
            defaultTeacherFilter.addAcceptedSituation(StringUtils.isEmpty(s)?null:Integer.parseInt(s));
        }
        return defaultTeacherFilter;
    }

    public CourseAssignmentFilter getCourseAssignmentFilter() {
        DefaultCourseAssignmentFilter c = new DefaultCourseAssignmentFilter();
        for (String s : getModel().getSelectedProgramTypes()) {
            c.addAcceptedProgramType(StringUtils.isEmpty(s)?null:Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedLabels()) {
            c.addLabelExpression(s);
        }
        return c;
    }

    public boolean isIncludeIntents() {
        HashSet<String> labels = new HashSet<>(Arrays.asList(getModel().getRefreshFilter()));
        return labels.contains("intents");
    }

    public boolean containsRefreshFilter(String s) {
        String[] f = getModel().getRefreshFilter();
        return Arrays.asList(f).indexOf(s) >= 0;
    }

    public static class Model{
        String[] refreshFilter = {};
        List<SelectItem> refreshFilterItems;

        List<SelectItem> officialDisciplinesItems=new ArrayList<>();
        List<SelectItem> degreeItems=new ArrayList<>();
        List<SelectItem> situationItems=new ArrayList<>();
        List<SelectItem> departmentItems=new ArrayList<>();
        List<SelectItem> programTypeItems=new ArrayList<>();
        List<SelectItem> labelItems=new ArrayList<>();

        List<String> selectedOfficialDisciplines=new ArrayList<>();
        List<String> selectedDegrees=new ArrayList<>();
        List<String> selectedSituations=new ArrayList<>();
        List<String> selectedDepartments=new ArrayList<>();
        List<String> selectedProgramTypes=new ArrayList<>();
        List<String> selectedLabels=new ArrayList<>();
        List<SelectItem> periodItems = new ArrayList<>();
        List<SelectItem> deviationGroupItems = new ArrayList<>();
        String selectedPeriod = null;
        List<String> selectedDeviationGroups = new ArrayList<>();
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
        public String[] getRefreshFilter() {
            return refreshFilter;
        }

        public void setRefreshFilter(String[] refreshFilter) {
            this.refreshFilter = refreshFilter;
        }

        public List<SelectItem> getRefreshFilterItems() {
            return refreshFilterItems;
        }

        public void setRefreshFilterItems(List<SelectItem> refreshFilterItems) {
            this.refreshFilterItems = refreshFilterItems;
        }

        public List<SelectItem> getProgramTypeItems() {
            return programTypeItems;
        }

        public void setProgramTypeItems(List<SelectItem> programTypeItems) {
            this.programTypeItems = programTypeItems;
        }

        public List<String> getSelectedProgramTypes() {
            return selectedProgramTypes;
        }

        public void setSelectedProgramTypes(List<String> selectedProgramTypes) {
            this.selectedProgramTypes = selectedProgramTypes;
        }

        public List<SelectItem> getLabelItems() {
            return labelItems;
        }

        public void setLabelItems(List<SelectItem> labelItems) {
            this.labelItems = labelItems;
        }

        public List<String> getSelectedLabels() {
            return selectedLabels;
        }

        public void setSelectedLabels(List<String> selectedLabels) {
            this.selectedLabels = selectedLabels;
        }

        public List<SelectItem> getDeviationGroupItems() {
            return deviationGroupItems;
        }

        public void setDeviationGroupItems(List<SelectItem> deviationGroupItems) {
            this.deviationGroupItems = deviationGroupItems;
        }

        public List<String> getSelectedDeviationGroups() {
            return selectedDeviationGroups;
        }

        public void setSelectedDeviationGroups(List<String> selectedDeviationGroups) {
            this.selectedDeviationGroups = selectedDeviationGroups;
        }
    }
}
