package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgramType;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
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
            getModel().getPeriodItems().add(FacesUtils.createSelectItem(String.valueOf(p.getId()), p.getName()));
            if (mainPeriod != null && p.getId() == mainPeriod.getId()) {
                getModel().setSelectedPeriod(String.valueOf(p.getId()));
            }
        }


        getModel().getTeacherDisciplineItems().clear();
        for (AcademicOfficialDiscipline item : academicPlugin.findOfficialDisciplines()) {
            getModel().getTeacherDisciplineItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
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

        getModel().getTeacherSituationItems().clear();
        for (AcademicTeacherSituation item : a.findTeacherSituations()) {
            getModel().getTeacherSituationItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

        getModel().getTeacherDegreeItems().clear();
        for (AcademicTeacherDegree item : a.findTeacherDegrees()) {
            getModel().getTeacherDegreeItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

        getModel().getTeacherDisciplineItems().clear();
        for (AcademicOfficialDiscipline item : a.findOfficialDisciplines()) {
            getModel().getTeacherDisciplineItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(), "vr-checkbox"));
        }

        getModel().getTeacherItems().clear();
        for (AcademicTeacher item : a.findTeachersWithAssignmentsOrIntents(getPeriodId(),-1,true,true,-1,-1)) {
            getModel().getTeacherItems().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.resolveFullName(), "vr-checkbox"));
        }

        getModel().setRefreshFilterSelected(new String[]{"intents", "deviation-extra", "deviation-week"});
    }

    public void onChangePeriod() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);


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
        for (String s : getModel().getSelectedTeachers()) {
            c.addAcceptedTeacher(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedOwnerDepartments()) {
            c.addAcceptedOwnerDepartment(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedTeacherDepartments()) {
            c.addAcceptedTeacherDepartment(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedTeacherDegrees()) {
            c.addAcceptedTeacherDegree(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedTeacherSituations()) {
            c.addAcceptedTeacherSituation(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedTeacherDisciplines()) {
            c.addAcceptedTeacherDiscipline(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
        for (String s : getModel().getSelectedCourseDisciplines()) {
            c.addAcceptedCourseDiscipline(StringUtils.isEmpty(s) ? null : Integer.parseInt(s));
        }
//        for (String s : getModel().getSelectedTeacherDisciplines()) {
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
        HashSet<String> labels = new HashSet<>(Arrays.asList(getModel().getRefreshFilterSelected()));
        return labels.contains("intents");
    }

    public boolean containsRefreshFilter(String s) {
//        if("extra-abs".equals(s)){
//            return true;
//        }
        String[] f = getModel().getRefreshFilterSelected();
        return Arrays.asList(f).indexOf(s) >= 0;
    }

    public static class Model {
        private List<SelectItem> refreshFilterItems;
        private String[] refreshFilterSelected = {};

        private List<SelectItem> teacherDisciplineItems = new ArrayList<>();
        private List<String> selectedTeacherDisciplines = new ArrayList<>();
        private List<String> selectedCourseDisciplines = new ArrayList<>();

//        private List<SelectItem> degreeItems=new ArrayList<>();
//        private List<String> selectedDegrees=new ArrayList<>();
//
//        private List<SelectItem> situationItems=new ArrayList<>();
//        private List<String> selectedSituations=new ArrayList<>();

        private List<SelectItem> departmentItems = new ArrayList<>();
        private List<String> selectedDepartments = new ArrayList<>();
        private List<String> selectedOwnerDepartments = new ArrayList<>();
        private List<String> selectedTeacherDepartments = new ArrayList<>();

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

        private List<SelectItem> teacherDegreeItems = new ArrayList<>();
        private List<String> selectedTeacherDegrees = new ArrayList<>();

        private List<SelectItem> teacherItems = new ArrayList<>();
        private List<String> selectedTeachers = new ArrayList<>();

        private List<SelectItem> teacherSituationItems = new ArrayList<>();
        private List<String> selectedTeacherSituations = new ArrayList<>();

        public List<String> getSelectedCourseDisciplines() {
            return selectedCourseDisciplines;
        }

        public void setSelectedCourseDisciplines(List<String> selectedCourseDisciplines) {
            this.selectedCourseDisciplines = selectedCourseDisciplines;
        }

        public List<String> getSelectedTeacherDepartments() {
            return selectedTeacherDepartments;
        }

        public List<SelectItem> getTeacherDegreeItems() {
            return teacherDegreeItems;
        }

        public List<String> getSelectedTeacherDegrees() {
            return selectedTeacherDegrees;
        }

        public List<SelectItem> getTeacherSituationItems() {
            return teacherSituationItems;
        }

        public List<String> getSelectedTeacherSituations() {
            return selectedTeacherSituations;
        }

        public void setSelectedTeacherDepartments(List<String> selectedTeacherDepartments) {
            this.selectedTeacherDepartments = selectedTeacherDepartments;
        }

        public void setTeacherDegreeItems(List<SelectItem> teacherDegreeItems) {
            this.teacherDegreeItems = teacherDegreeItems;
        }

        public void setSelectedTeacherDegrees(List<String> selectedTeacherDegrees) {
            this.selectedTeacherDegrees = selectedTeacherDegrees;
        }

        public void setTeacherSituationItems(List<SelectItem> teacherSituationItems) {
            this.teacherSituationItems = teacherSituationItems;
        }

        public void setSelectedTeacherSituations(List<String> selectedTeacherSituations) {
            this.selectedTeacherSituations = selectedTeacherSituations;
        }

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

        public List<SelectItem> getTeacherDisciplineItems() {
            return teacherDisciplineItems;
        }

        public void setTeacherDisciplineItems(List<SelectItem> teacherDisciplineItems) {
            this.teacherDisciplineItems = teacherDisciplineItems;
        }

        public List<SelectItem> getTeacherItems() {
            return teacherItems;
        }

        public void setTeacherItems(List<SelectItem> teacherItems) {
            this.teacherItems = teacherItems;
        }

        public List<String> getSelectedTeachers() {
            return selectedTeachers;
        }

        public void setSelectedTeachers(List<String> selectedTeachers) {
            this.selectedTeachers = selectedTeachers;
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

        public List<String> getSelectedTeacherDisciplines() {
            return selectedTeacherDisciplines;
        }

        public void setSelectedTeacherDisciplines(List<String> selectedTeacherDisciplines) {
            this.selectedTeacherDisciplines = selectedTeacherDisciplines;
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

        public String[] getRefreshFilterSelected() {
            return refreshFilterSelected;
        }

        public void setRefreshFilterSelected(String[] refreshFilterSelected) {
            this.refreshFilterSelected = refreshFilterSelected;
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
