package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgramType;
import net.vpc.app.vainruling.plugins.academic.service.stat.DeviationConfig;
import net.vpc.app.vainruling.plugins.academic.service.stat.DeviationGroup;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CourseLoadFilterComponent {
    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public int getPeriodId() {
        String p = getModel().getSelectedPeriod();
        if (StringUtils.isEmpty(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig appConfig = core.getCurrentConfig();
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
        AppPeriod mainPeriod = core.getCurrentPeriod();
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

//        UserSession currentSession = UserSession.get();
        for (AppDepartment item : core.findDepartments()) {
//            if(currentSession!=null &&)
            //TODO: should add filter to let see ust one's department?
            getModel().getDepartmentItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

//        getModel().getSituationItems().clear();
//        for (AcademicTeacherSituation item : academicPlugin.findTeacherSituations()) {
//            getModel().getSituationItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
//        }
//
//        getModel().getDegreeItems().clear();
//        for (AcademicTeacherDegree item : academicPlugin.findTeacherDegrees()) {
//            getModel().getDegreeItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
//        }
        getModel().getProgramTypeItems().clear();
        for (AcademicProgramType pt : a.findProgramTypes()) {
            getModel().getProgramTypeItems().add(FacesUtils.createSelectItem(String.valueOf(pt.getId()), pt.getName(), "vr-checkbox"));
        }

        getModel().getDeviationGroupItems().clear();
        for (DeviationGroup deviationGroup : DeviationGroup.values()) {
            getModel().getDeviationGroupItems().add(FacesUtils.createSelectItem(String.valueOf(deviationGroup.name()), deviationGroup.name(), "vr-checkbox"));
        }

        getModel().getSemesterItems().clear();
        for (AcademicSemester item : a.findSemesters()) {
            getModel().getSemesterItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

        getModel().getCourseTypeItems().clear();
        for (AcademicCourseType item : a.findCourseTypes()) {
            getModel().getCourseTypeItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

        getModel().getClassItems().clear();
        for (AcademicClass item : a.findAcademicClasses()) {
            getModel().getClassItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

        getModel().setRefreshFilter(new String[]{"intents", "deviation-extra", "deviation-week"});
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
        if (periodId >= -1) {
            for (String label : a.findCoursePlanLabels(periodId)) {
                getModel().getLabelItems().add(FacesUtils.createSelectItem(label, label, "vr-checkbox"));
                getModel().getLabelItems().add(FacesUtils.createSelectItem("!" + label, "Sans " + label, "vr-checkbox"));
            }
        }
    }

    public DeviationConfig getDeviationConfig() {
        DeviationConfig deviationConfig = new DeviationConfig();
        for (String item : getModel().getSelectedDeviationGroups()) {
            deviationConfig.getGroups().add(DeviationGroup.valueOf(item.toUpperCase()));
        }
        deviationConfig.setExtraBased(containsRefreshFilter("deviation-extra"));
        deviationConfig.setWeekBased(containsRefreshFilter("deviation-week"));
        return deviationConfig;
    }

    public DefaultCourseAssignmentFilter getCourseAssignmentFilter() {
        DefaultCourseAssignmentFilter c = new DefaultCourseAssignmentFilter();
        for (String s : getModel().getSelectedProgramTypes()) {
            c.addAcceptedProgramType(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedLabels()) {
            c.addLabelExpression(s);
        }
        for (String s : getModel().getSelectedSemesters()) {
            c.addAcceptedSemester(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedCourseTypes()) {
            c.addAcceptedCourseType(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedClasses()) {
            c.addAcceptedClass(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }

//        for (String s : getModel().getSelectedDegrees()) {
//            c.addAcceptedDegree(StringUtils.isEmpty(s)?null:Integer.parseInt(s));
//        }
        for (String s : getModel().getSelectedDepartments()) {
            c.addAcceptedDepartment(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedOwnerDepartments()) {
            c.addAcceptedOwnerDepartment(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
//        for (String s : getModel().getSelectedOfficialDisciplines()) {
//            c.addAcceptedOfficialDisciplines(StringUtils.isEmpty(s)?null:Integer.parseInt(s));
//        }
//        for (String s : getModel().getSelectedSituations()) {
//            c.addAcceptedSituation(StringUtils.isEmpty(s)?null:Integer.parseInt(s));
//        }
        c.setAcceptAssignments(true);
        c.setAcceptIntents(isIncludeIntents());
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

    public static class Model {
        private List<SelectItem> refreshFilterItems;
        private String[] refreshFilter = {};

        private List<SelectItem> officialDisciplinesItems = new ArrayList<>();
        private List<String> selectedOfficialDisciplines = new ArrayList<>();

//        private List<SelectItem> degreeItems=new ArrayList<>();
//        private List<String> selectedDegrees=new ArrayList<>();
//
//        private List<SelectItem> situationItems=new ArrayList<>();
//        private List<String> selectedSituations=new ArrayList<>();

        private List<SelectItem> departmentItems = new ArrayList<>();
        private List<String> selectedDepartments = new ArrayList<>();
        private List<String> selectedOwnerDepartments = new ArrayList<>();

        private List<SelectItem> programTypeItems = new ArrayList<>();
        private List<String> selectedProgramTypes = new ArrayList<>();

        private List<SelectItem> labelItems = new ArrayList<>();
        private List<String> selectedLabels = new ArrayList<>();

        private List<SelectItem> periodItems = new ArrayList<>();
        private String selectedPeriod = null;

        private List<SelectItem> deviationGroupItems = new ArrayList<>();
        private List<String> selectedDeviationGroups = new ArrayList<>();

        private List<SelectItem> semesterItems = new ArrayList<>();
        private List<String> selectedSemesters = new ArrayList<>();

        private List<SelectItem> courseTypeItems = new ArrayList<>();
        private List<String> selectedCourseTypes = new ArrayList<>();

        private List<SelectItem> classItems = new ArrayList<>();
        private List<String> selectedClasses = new ArrayList<>();

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

//        public List<SelectItem> getDegreeItems() {
//            return degreeItems;
//        }
//
//        public void setDegreeItems(List<SelectItem> degreeItems) {
//            this.degreeItems = degreeItems;
//        }
//
//        public List<SelectItem> getSituationItems() {
//            return situationItems;
//        }
//
//        public void setSituationItems(List<SelectItem> situationItems) {
//            this.situationItems = situationItems;
//        }

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

//        public List<String> getSelectedDegrees() {
//            return selectedDegrees;
//        }
//
//        public void setSelectedDegrees(List<String> selectedDegrees) {
//            this.selectedDegrees = selectedDegrees;
//        }
//
//        public List<String> getSelectedSituations() {
//            return selectedSituations;
//        }
//
//        public void setSelectedSituations(List<String> selectedSituations) {
//            this.selectedSituations = selectedSituations;
//        }

        public List<String> getSelectedDepartments() {
            return selectedDepartments;
        }

        public void setSelectedDepartments(List<String> selectedDepartments) {
            this.selectedDepartments = selectedDepartments;
        }

        public List<String> getSelectedOwnerDepartments() {
            return selectedOwnerDepartments;
        }

        public void setSelectedOwnerDepartments(List<String> selectedOwnerDepartments) {
            this.selectedOwnerDepartments = selectedOwnerDepartments;
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

        public List<SelectItem> getSemesterItems() {
            return semesterItems;
        }

        public void setSemesterItems(List<SelectItem> semesterItems) {
            this.semesterItems = semesterItems;
        }

        public List<String> getSelectedSemesters() {
            return selectedSemesters;
        }

        public void setSelectedSemesters(List<String> selectedSemesters) {
            this.selectedSemesters = selectedSemesters;
        }

        public List<SelectItem> getCourseTypeItems() {
            return courseTypeItems;
        }

        public void setCourseTypeItems(List<SelectItem> courseTypeItems) {
            this.courseTypeItems = courseTypeItems;
        }

        public List<String> getSelectedCourseTypes() {
            return selectedCourseTypes;
        }

        public void setSelectedCourseTypes(List<String> selectedCourseTypes) {
            this.selectedCourseTypes = selectedCourseTypes;
        }

        public List<SelectItem> getClassItems() {
            return classItems;
        }

        public void setClassItems(List<SelectItem> classItems) {
            this.classItems = classItems;
        }

        public List<String> getSelectedClasses() {
            return selectedClasses;
        }

        public void setSelectedClasses(List<String> selectedClasses) {
            this.selectedClasses = selectedClasses;
        }
    }
}
