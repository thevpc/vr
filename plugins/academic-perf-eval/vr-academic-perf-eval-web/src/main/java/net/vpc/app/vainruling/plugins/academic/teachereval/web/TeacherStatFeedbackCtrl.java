/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.ValueCountSet;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.GroupView;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.QuestionView;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.StatData;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.common.strings.StringUtils;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Stats Eval. Enseignements",
        menu = "/Education/Evaluation",
        url = "modules/academic/perfeval/teacher-stat-feedback",
        securityKey = "Custom.Academic.TeacherStatFeedback"
)
public class TeacherStatFeedbackCtrl {

    private static final Logger log = Logger.getLogger(TeacherStatFeedbackCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    @Autowired
    private AcademicPlugin academic;
    @Autowired
    private AcademicPerfEvalPlugin feedback;
    private Model model = new Model();
    private List<GroupCondition> conditions = new ArrayList<>();

    public TeacherStatFeedbackCtrl() {
        conditions.add(new TeacheGroupCondition());
        conditions.add(new AssignmentGroupCondition());
        conditions.add(new CourseGroupCondition());
        conditions.add(new CourseTypeGroupCondition());
        conditions.add(new ClassGroupCondition());
    }

    public int getSelectedFilterIntId() {
        String selectedFilter = getModel().getSelectedFilter();
        if (StringUtils.isEmpty(selectedFilter)) {
            return -1;
        }
        int pos = selectedFilter.indexOf(":");
//            String filterType = selectedFilter.substring(0, pos);
        String idString = selectedFilter.substring(pos + 1);
        return Integer.parseInt(idString);
    }

    @OnPageLoad
    public void onLoad() {
        getModel().setValidate(false);
        getModel().setTeacherListEnabled(academic.isUserSessionManager());
        getModel().setFilterTeachersEnabled(academic.isUserSessionManager());

        ArrayList<SelectItem> filterTypesList = new ArrayList<>();
        for (GroupCondition condition : conditions) {
            filterTypesList.add(new SelectItem(condition.getId(), condition.getLabel()));
        }
        getModel().setFilterTypesList(filterTypesList);
        getModel().setFilterType((String) filterTypesList.get(0).getValue());


        ArrayList<SelectItem> items = new ArrayList<>();
        List<AppPeriod> periods = core.findNavigatablePeriods();
        for (AppPeriod t : periods) {
            String n = t.getName();
            items.add(new SelectItem(String.valueOf(t.getId()), n));
        }
        getModel().setPeriods(items);

        onChangePeriod();
    }

    public void onChangePeriod() {
        onReloadFilterByType();
    }

    public GroupCondition getSelectedGroupCondition() {
        String filterType = getModel().getSelectedFilterType();
        if (!StringUtils.isEmpty(filterType)) {
            for (GroupCondition condition : conditions) {
                if (condition.getId().equals(filterType)) {
                    return condition;
                }
            }
        }
        return null;
    }

    public void onReloadFilterByType() {
        int periodId = -1;
        String selectedPeriodString = getModel().getSelectedPeriod();
        if (!StringUtils.isEmpty(selectedPeriodString)) {
            periodId = (Integer.parseInt(selectedPeriodString));
        }


        getModel().setFilterTeachersEnabled(false);
        GroupCondition cond = getSelectedGroupCondition();
        getModel().setTeachersList(new ArrayList<>());
        if (cond != null) {
            cond.onSelected();
        }
        onReloadFeedbacks();
    }

    protected void reloadTeachersList() {
        ArrayList<SelectItem> academicTeachers = new ArrayList<>();
        if (getModel().isFilterTeachersEnabled()) {
            List<AcademicTeacher> teachersWithFeedbacks = feedback.findTeachersWithFeedbacks(getSelectedPeriodId(), getModel().getValidatedFilter(), false, true);
            for (AcademicTeacher f : teachersWithFeedbacks) {
                if (academic.isManagerOf(f)) {
                    academicTeachers.add(new SelectItem(String.valueOf(f.getId()), f.getContact().getFullTitle()));
                }
            }
        }
        getModel().setTeachersList(academicTeachers);
    }

    public void onReloadFeedbacks() {

        ArrayList<SelectItem> theList = new ArrayList<>();
        getModel().setFilterList(theList);


        getModel().setFilterList(new ArrayList<>());
        GroupCondition cond = getSelectedGroupCondition();
        if (cond != null) {
            cond.onPrepareModel();
        }
        onFilterChange();
    }

    public void onFilterChange() {
        getModel().setValidate(false);
        StatData statData = feedback.evalStatData(findAcademicFeedbacks());
        statData.setGlobalChart(createBarChartModel(statData.getGlobalValues()));
        for (GroupView row : statData.getGroupedQuestionsList()) {
            row.setChart(createBarChartModel(row.getValues()));
            for (QuestionView questionView : row.getQuestions()) {
                questionView.setChart(createBarChartModel(questionView.getValues()));
            }
        }

        getModel().setStatData(statData);
    }

//    public List<AcademicFeedback> findAcademicFeedbacksByAssignment(int id) {
//        AcademicCourseAssignment a = academic.findAcademicCourseAssignment(id);
//        if (a == null) {
//            return Collections.emptyList();
//        }
//        return feedback.findAssignmentFeedbacks(a.getId(), getModel().getValidatedFilter(), false);
//    }

    public List<AcademicFeedback> findAcademicFeedbacks() {
        GroupCondition selectedGroupCondition = getSelectedGroupCondition();
        if (selectedGroupCondition == null) {
            return new ArrayList<>();
        }
        return selectedGroupCondition.findFeedbacks();
    }


    private BarChartModel createBarChartModel(ValueCountSet valueCountSet) {
        if (valueCountSet.isEmpty()) {
            return null;
        }
        BarChartModel bmodel = new BarChartModel();
        bmodel.setShadow(true);
        bmodel.setLegendCols(1);
        bmodel.setLegendPlacement(LegendPlacement.OUTSIDE);
        bmodel.setLegendPosition("e");
        bmodel.setBarMargin(0);
        bmodel.setBarPadding(0);
//                        boys.setLabel("Boys");
        for (String val : new TreeSet<String>((Set) valueCountSet.keySet())) {
            ChartSeries boys = new ChartSeries();
            String vv = String.valueOf(val);
            String vv0 = getModel().getValueTexts().get(vv);
            if (vv0 != null) {
                vv = vv0;
            }
            boys.set(" ", valueCountSet.getCount(val));
            boys.setLabel(vv);
            bmodel.addSeries(boys);
        }
        return bmodel;
    }

    public int getSelectedOwnerDepartmentId() {
        UserSession sm = core.getUserSession();
        if (core.isSessionAdmin()) {
            return -1;
        }
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user == null || user.getDepartment() == null) {
            return 99999;
        }
        return user.getDepartment().getId();
    }

    public int getSelectedPeriodId() {
        int periodId = -1;
        String selectedPeriodString = getModel().getSelectedPeriod();
        if (!StringUtils.isEmpty(selectedPeriodString)) {
            periodId = (Integer.parseInt(selectedPeriodString));
        }
        return periodId;
    }

    public int getSelectedTeacherId() {
        AcademicTeacher s = null;
        if (getModel().isTeacherListEnabled()) {
            String selectedTeacherString = getModel().getSelectedTeacher();
            if (!StringUtils.isEmpty(selectedTeacherString)) {
                s = academic.findTeacher(Integer.parseInt(selectedTeacherString));
            }
        } else {
            s = academic.getCurrentTeacher();
        }
        return s == null ? -1 : s.getId();
    }

    public String getSearchTitle() {
        AppPeriod period = core.findPeriod(getSelectedPeriodId());
        GroupCondition c = getSelectedGroupCondition();
        if (c == null) {
            return "Toutes les evals" + (period == null ? "" : (", " + period.getName()));
        }
        return c.getSearchTitle() + (period == null ? "" : (", " + period.getName()));
    }

    public void onValidate() {

    }

    public Model getModel() {
        return model;
    }

    private void revalidateSelectedFilter() {
        List<SelectItem> filterList = getModel().getFilterList();
        HashSet<String> ids = new HashSet<>();
        for (SelectItem selectItem : filterList) {
            Object value = selectItem.getValue();
            if (value != null) {
                ids.add(String.valueOf(value));
            }
        }
        if (!ids.contains(getModel().getSelectedFilter())) {
            getModel().setSelectedFilter(null);
            for (String id : ids) {
                getModel().setSelectedFilter(id);
                break;
            }
        }
    }

    public interface GroupCondition {
        String getId();

        String getLabel();

        void onSelected();

        void onPrepareModel();

        String getSearchTitle();

        List<AcademicFeedback> findFeedbacks();
    }

    public static class Model {

        private String title;
        private boolean filterTeachersEnabled = false;
        private boolean teacherListEnabled = false;
        private String selectedTeacher;
        private String selectedPeriod;
        private String selectedFilterType = "assignement";
        private List<SelectItem> teachersList = new ArrayList<SelectItem>();
        private List<SelectItem> filterTypesList = new ArrayList<SelectItem>();

        private Boolean validatedFilter;
        private String filterType;
        private String selectedFilter;
        private List<SelectItem> periods = new ArrayList<SelectItem>();
        private List<SelectItem> filterList = new ArrayList<SelectItem>();

        private boolean validate = false;
        private Map<String, String> valueTexts = new HashMap<>();
        private StatData statData = new StatData();

        public Model() {
            valueTexts.put("1", "Tout a fait en desaccord");
            valueTexts.put("2", "Plutot en desaccord");
            valueTexts.put("3", "Plutot en accord");
            valueTexts.put("4", "Tout a fait en accord");
        }

        public StatData getStatData() {
            return statData;
        }

        public void setStatData(StatData statData) {
            this.statData = statData;
        }

        public boolean isFilterTeachersEnabled() {
            return filterTeachersEnabled;
        }

        public void setFilterTeachersEnabled(boolean filterTeachersEnabled) {
            this.filterTeachersEnabled = filterTeachersEnabled;
        }

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
        }

        public String getSelectedPeriod() {
            return selectedPeriod;
        }

        public void setSelectedPeriod(String selectedPeriod) {
            this.selectedPeriod = selectedPeriod;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<SelectItem> getFilterList() {
            return filterList;
        }

        public void setFilterList(List<SelectItem> filterList) {
            this.filterList = filterList;
        }

        public String getSelectedFilter() {
            return selectedFilter;
        }

        public void setSelectedFilter(String selectedFilter) {
            this.selectedFilter = selectedFilter;
        }

        public boolean isValidate() {
            return validate;
        }

        public void setValidate(boolean validate) {
            this.validate = validate;
        }

        public Map<String, String> getValueTexts() {
            return valueTexts;
        }

        public void setValueTexts(Map<String, String> valueTexts) {
            this.valueTexts = valueTexts;
        }

        public boolean isTeacherListEnabled() {
            return teacherListEnabled;
        }

        public Model setTeacherListEnabled(boolean teacherListEnabled) {
            this.teacherListEnabled = teacherListEnabled;
            return this;
        }

        public String getSelectedTeacher() {
            return selectedTeacher;
        }

        public void setSelectedTeacher(String selectedTeacher) {
            this.selectedTeacher = selectedTeacher;
        }

        public List<SelectItem> getTeachersList() {
            return teachersList;
        }

        public void setTeachersList(List<SelectItem> teachersList) {
            this.teachersList = teachersList;
        }

        public String getFilterType() {
            return filterType;
        }

        public Model setFilterType(String filterType) {
            this.filterType = filterType;
            return this;
        }

        public Boolean getValidatedFilter() {
            return validatedFilter;
        }

        public Model setValidatedFilter(Boolean validatedFilter) {
            this.validatedFilter = validatedFilter;
            return this;
        }

        public String getSelectedFilterType() {
            return selectedFilterType;
        }

        public void setSelectedFilterType(String selectedFilterType) {
            this.selectedFilterType = selectedFilterType;
        }

        public List<SelectItem> getFilterTypesList() {
            return filterTypesList;
        }

        public void setFilterTypesList(List<SelectItem> filterTypesList) {
            this.filterTypesList = filterTypesList;
        }

    }

    public abstract class AbstractGroupCondition implements GroupCondition {
        private String id;
        private String label;

        public AbstractGroupCondition(String id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    public class TeacheGroupCondition extends AbstractGroupCondition {
        public TeacheGroupCondition() {
            super("teacher", "Enseignent");
        }

        @Override
        public String getSearchTitle() {
            int teacherId = getSelectedFilterIntId();
            if (teacherId < 0) {
                return "Tous les Enseignants";
            }
            AcademicTeacher teacher = academic.findTeacher(teacherId);
            String validName = academic.getValidName(teacher);
            return teacher == null ? "Enseignant Inconnu" : validName;
        }

        @Override
        public void onSelected() {
            getModel().setFilterTeachersEnabled(false);
        }

        @Override
        public void onPrepareModel() {
            ArrayList<SelectItem> theList = new ArrayList<>();
            int periodId = getSelectedPeriodId();
            for (AcademicTeacher f : feedback.findTeachersWithFeedbacks(periodId, getModel().getValidatedFilter(), false, true)) {
                if (academic.isManagerOf(f)) {
                    theList.add(new SelectItem(getId() + ":" + String.valueOf(f.getId()), f.getContact().getFullTitle()));
                }
            }
            getModel().setFilterList(theList);
            revalidateSelectedFilter();
        }

        @Override
        public List<AcademicFeedback> findFeedbacks() {
            int teacherId = getSelectedFilterIntId();
            int selectedOwnerDepartmentId = getSelectedOwnerDepartmentId();
            return feedback.findFeedbacks(
                    getSelectedPeriodId(),
                    null,
                    selectedOwnerDepartmentId < 0 ? null : selectedOwnerDepartmentId,
                    null,
                    teacherId < 0 ? null : teacherId,
                    null,
                    null,
                    getModel().getValidatedFilter(),
                    false,
                    true,
                    null
            );
        }
    }

    public class ClassGroupCondition extends AbstractGroupCondition {
        public ClassGroupCondition() {
            super("class", "Classe");
        }

        @Override
        public void onSelected() {
            getModel().setFilterTeachersEnabled(getModel().isTeacherListEnabled());
            reloadTeachersList();
        }

        @Override
        public String getSearchTitle() {
            StringBuilder sb = new StringBuilder();
            int teacherId = getSelectedTeacherId();
            if (teacherId < 0) {
                sb.append("Tous les Enseignants");
            } else {
                AcademicTeacher teacher = academic.findTeacher(teacherId);
                String validName = academic.getValidName(teacher);
                sb.append(teacher == null ? "Enseignant Inconnu" : validName);
            }
            sb.append(", ");
            int classId = getSelectedFilterIntId();
            if (classId < 0) {
                sb.append("Toutes les classes");
            } else {
                AcademicClass className = academic.findAcademicClass(classId);
                sb.append(className == null ? "Classe Inconnue" : className.getName());
            }
            return sb.toString();
        }

        @Override
        public void onPrepareModel() {
            ArrayList<SelectItem> theList = new ArrayList<>();
            int periodId = getSelectedPeriodId();
            for (AcademicClass f : feedback.findClassesWithFeedbacks(periodId, getModel().getValidatedFilter(), false, true)) {
//                boolean admin=core.isSessionAdmin();
//                if(!admin)
//                try{
//                    f.getProgram().getDepartment().getId()==core.getUserSession().getUser().getDepartment().getId()
//
//                }catch (NullPointerException ex){
//
//                }
                theList.add(new SelectItem(getId() + ":" + String.valueOf(f.getId()), f.getName()));
            }
            getModel().setFilterList(theList);
            revalidateSelectedFilter();
        }

        @Override
        public List<AcademicFeedback> findFeedbacks() {
            int classId = getSelectedFilterIntId();
            int selectedOwnerDepartmentId = getSelectedOwnerDepartmentId();
            return feedback.findFeedbacks(
                    getSelectedPeriodId(),
                    null,
                    selectedOwnerDepartmentId < 0 ? null : selectedOwnerDepartmentId,
                    null,
                    getSelectedTeacherId(),
                    null,
                    classId < 0 ? null : classId,
                    getModel().getValidatedFilter(),
                    false,true,
                    null
            );
        }
    }

    public class AssignmentGroupCondition extends AbstractGroupCondition {
        public AssignmentGroupCondition() {
            super("assignment", "Enseignement");
        }

        @Override
        public void onSelected() {
            getModel().setFilterTeachersEnabled(getModel().isTeacherListEnabled());
            reloadTeachersList();
        }

        @Override
        public void onPrepareModel() {
            ArrayList<SelectItem> theList = new ArrayList<>();
            int periodId = getSelectedPeriodId();
            int teacherId = getSelectedTeacherId();
            for (AcademicCourseAssignment f : feedback.findAssignmentsWithFeedbacks(periodId, teacherId, getModel().getValidatedFilter(), false, true)) {
                if (f.getTeacher() != null && academic.isManagerOf(f.getTeacher())) {
                    theList.add(new SelectItem(getId() + ":" + String.valueOf(f.getId()), f.getFullName()));
                }
            }
            getModel().setFilterList(theList);
            revalidateSelectedFilter();
        }

        @Override
        public List<AcademicFeedback> findFeedbacks() {
            String selectedFilter = getModel().getSelectedFilter();
            if (selectedFilter == null) {
                return Collections.EMPTY_LIST;
            }
            int pos = selectedFilter.indexOf(":");
//            String filterType = selectedFilter.substring(0, pos);
            String idString = selectedFilter.substring(pos + 1);
            int id = Integer.parseInt(idString);
            return feedback.findAssignmentFeedbacks(id, getModel().getValidatedFilter(), false);
        }

        @Override
        public String getSearchTitle() {
            StringBuilder sb = new StringBuilder();
            int assignementId = getSelectedTeacherId();
            if (assignementId < 0) {
                sb.append("Tous les Enseignements");
            } else {
                AcademicCourseAssignment assignement = academic.findCourseAssignment(assignementId);
                sb.append(assignement == null ? "Enseignement Inconnu" : assignement.getFullName());
            }
            return sb.toString();
        }
    }

    public class CourseGroupCondition extends AbstractGroupCondition {
        public CourseGroupCondition() {
            super("course", "Cours");
        }


        @Override
        public void onSelected() {
            getModel().setFilterTeachersEnabled(getModel().isTeacherListEnabled());
            reloadTeachersList();
        }

        @Override
        public void onPrepareModel() {
            ArrayList<SelectItem> theList = new ArrayList<>();
            int periodId = getSelectedPeriodId();
            int teacherId = getSelectedTeacherId();
            for (AcademicCoursePlan f : feedback.findAcademicCoursePlansWithFeedbacks(periodId, teacherId, getModel().getValidatedFilter(), false, true)) {
                boolean accept=core.isSessionAdmin();
                if(!accept) {
                    AppDepartment deptId = null;
                    try {
                        deptId = f.getCourseLevel().getAcademicClass().getProgram().getDepartment();
                    } catch (NullPointerException ex) {
                        //
                    }
                    accept=academic.isManagerOf(deptId);
                }
                if(!accept) {
                    for (AcademicCourseAssignment ca : academic.findCourseAssignmentsByPlan(f.getId())) {
                        if(ca.getOwnerDepartment()!=null){
                            accept=academic.isManagerOf(ca.getOwnerDepartment());
                            if(accept){
                                break;
                            }
                        }
                    }
                }
                if(accept) {
                    theList.add(new SelectItem(getId() + ":" + String.valueOf(f.getId()), f.getFullName()));
                }
            }
            getModel().setFilterList(theList);
            revalidateSelectedFilter();
        }

        @Override
        public List<AcademicFeedback> findFeedbacks() {
            int teacherId = getSelectedTeacherId();
            int courseId = getSelectedFilterIntId();
            int selectedOwnerDepartmentId = getSelectedOwnerDepartmentId();
            return feedback.findFeedbacks(
                    getSelectedPeriodId(),
                    null,
                    selectedOwnerDepartmentId < 0 ? null : selectedOwnerDepartmentId,
                    courseId <= 0 ? null : courseId,
                    teacherId < 0 ? null : teacherId,
                    null,
                    null,
                    getModel().getValidatedFilter(),
                    false,true,
                    null
            );
        }

        @Override
        public String getSearchTitle() {
            StringBuilder sb = new StringBuilder();
            int teacherId = getSelectedTeacherId();
            if (teacherId < 0) {
                sb.append("Tous les Enseignants");
            } else {
                AcademicTeacher teacher = academic.findTeacher(teacherId);
                String validName = academic.getValidName(teacher);
                sb.append(teacher == null ? "Enseignant Inconnu" : validName);
            }
            sb.append(", ");
            int classId = getSelectedFilterIntId();
            if (classId < 0) {
                sb.append("Tous les cours");
            } else {
                AcademicCoursePlan className = academic.findCoursePlan(classId);
                sb.append(className == null ? "Cours Inconnu" : className.getName());
            }
            return sb.toString();
        }
    }

    public class CourseTypeGroupCondition extends AbstractGroupCondition {
        public CourseTypeGroupCondition() {
            super("courseType", "Type Enseignement");
        }

        @Override
        public void onSelected() {
            getModel().setFilterTeachersEnabled(getModel().isTeacherListEnabled());
            reloadTeachersList();
        }

        @Override
        public void onPrepareModel() {
            ArrayList<SelectItem> theList = new ArrayList<>();
            int periodId = getSelectedPeriodId();
            int teacherId = getSelectedTeacherId();
            for (AcademicCourseType f : feedback.findAcademicCourseTypesWithFeedbacks(periodId, teacherId, getModel().getValidatedFilter(), false, true)) {
                theList.add(new SelectItem(getId() + ":" + String.valueOf(f.getId()), f.getName()));
            }
            getModel().setFilterList(theList);
            revalidateSelectedFilter();
        }

        @Override
        public List<AcademicFeedback> findFeedbacks() {
            int teacherId = getSelectedTeacherId();
            int courseTypeId = (getSelectedFilterIntId());
            int selectedOwnerDepartmentId = getSelectedOwnerDepartmentId();
            return feedback.findFeedbacks(
                    getSelectedPeriodId(),
                    null,
                    selectedOwnerDepartmentId < 0 ? null : selectedOwnerDepartmentId,
                    null,
                    teacherId < 0 ? null : teacherId,
                    courseTypeId < 0 ? null : courseTypeId,
                    null,
                    getModel().getValidatedFilter(),
                    false,true,
                    null
            );
        }

        @Override
        public String getSearchTitle() {
            StringBuilder sb = new StringBuilder();
            int teacherId = getSelectedTeacherId();
            if (teacherId < 0) {
                sb.append("Tous les Enseignants");
            } else {
                AcademicTeacher teacher = academic.findTeacher(teacherId);
                String validName = academic.getValidName(teacher);
                sb.append(teacher == null ? "Enseignant Inconnu" : validName);
            }
            sb.append(", ");
            int classId = getSelectedFilterIntId();
            if (classId < 0) {
                sb.append("Tous les types");
            } else {
                AcademicCourseType className = academic.findCourseType(classId);
                sb.append(className == null ? "Type Cours Inconnu" : className.getName());
            }
            return sb.toString();
        }
    }

}
