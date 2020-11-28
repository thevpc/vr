/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.teachereval.web;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.util.ValueCountSet;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.app.vainruling.plugins.academic.perfeval.service.dto.GroupView;
import net.thevpc.app.vainruling.plugins.academic.perfeval.service.dto.QuestionView;
import net.thevpc.app.vainruling.plugins.academic.perfeval.service.dto.StatData;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.thevpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedback;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseType;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.strings.StringUtils;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;
import java.util.logging.Logger;
import net.thevpc.common.util.Convert;
import net.thevpc.common.util.IntegerParserConfig;
import org.springframework.stereotype.Controller;
import net.thevpc.app.vainruling.VrPage;
import org.primefaces.model.chart.PieChartModel;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Stats Eval. Enseignements",
        menu = "/Education/Evaluation",
        url = "modules/academic/perfeval/teacher-stat-feedback",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_STAT_FEEDBACK
)
@Controller
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
    private AllGroupCondition allCondition = new AllGroupCondition();

    public TeacherStatFeedbackCtrl() {
        conditions.add(new TeacheGroupCondition());
        conditions.add(new AssignmentGroupCondition());
        conditions.add(new CourseGroupCondition());
        conditions.add(new CourseTypeGroupCondition());
        conditions.add(new ClassGroupCondition());
    }

    public int getSelectedFilterIntId() {
        String selectedFilter = getModel().getSelectedFilter();
        if (StringUtils.isBlank(selectedFilter)) {
            return -1;
        }
        int pos = selectedFilter.indexOf(":");
//            String filterType = selectedFilter.substring(0, pos);
        String idString = selectedFilter.substring(pos + 1);
        return Integer.parseInt(idString);
    }

    @VrOnPageLoad
    public void onLoad() {
        getModel().setValidate(false);
        getModel().setTeacherListEnabled(AcademicPluginSecurity.isUserSessionManager());
        getModel().setFilterTeachersEnabled(AcademicPluginSecurity.isUserSessionManager());

        ArrayList<SelectItem> filterTypesList = new ArrayList<>();
        for (GroupCondition condition : conditions) {
            filterTypesList.add(FacesUtils.createSelectItem(condition.getId(), condition.getLabel()));
        }
        getModel().setFilterTypesList(filterTypesList);
        getModel().setFilterType((String) filterTypesList.get(0).getValue());

        ArrayList<SelectItem> items = new ArrayList<>();
        List<AppPeriod> periods = core.findNavigatablePeriods();
        for (AppPeriod t : periods) {
            String n = t.getName();
            items.add(FacesUtils.createSelectItem(String.valueOf(t.getId()), n));
        }
        getModel().setPeriods(items);
        CorePlugin core = CorePlugin.get();
        getModel().setShowStudentProgress(
                core.isCurrentSessionAdmin()
                || core.hasProfile("HeadOfDepartment")
        );
        onChangePeriod();
    }

    public void onChangePeriod() {
        onReloadFilterByType();
    }

    public GroupCondition getSelectedGroupCondition() {
        String filterType = getModel().getSelectedFilterType();
        if (!StringUtils.isBlank(filterType)) {
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
        if (!StringUtils.isBlank(selectedPeriodString)) {
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
                if (AcademicPluginSecurity.isManagerOf(f)) {
                    academicTeachers.add(FacesUtils.createSelectItem(String.valueOf(f.getId()), f.resolveFullTitle()));
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
        statData.setGlobalChartType(getModel().getChartType());
        statData.setGlobalChart(createChartModel(statData.getGlobalChartType(), statData.getGlobalValues()));
        for (GroupView row : statData.getGroupedQuestionsList()) {
            row.setChartType("bar"/*getModel().getChartType()*/);
            row.setChart(createChartModel(row.getChartType(), row.getValues()));
            for (QuestionView questionView : row.getQuestions()) {
                questionView.setChartType("bar"/*getModel().getChartType()*/);
                questionView.setChart(createChartModel(questionView.getChartType(), questionView.getValues()));
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
            return allCondition.findFeedbacks();
        }
        return selectedGroupCondition.findFeedbacks();
    }

    private Object createChartModel(String type, ValueCountSet valueCountSet) {
        switch (StringUtils.trim(type).toLowerCase()) {
            case "pie":
                return createPieChartModel(valueCountSet);
            case "bar":
                return createBarChartModel(valueCountSet);
        }
        return null;
    }

    private PieChartModel createPieChartModel(ValueCountSet valueCountSet) {
        PieChartModel pieModel1 = new PieChartModel();

        for (String val : new TreeSet<String>((Set) valueCountSet.keySet())) {
            String vv = String.valueOf(val);
            String vv0 = getModel().getValueTexts().get(vv);
            if (vv0 != null) {
                vv = vv0;
            }
            pieModel1.set(vv, valueCountSet.getCountPercent(val));
        }
//        pieModel1.setTitle("Simple Pie");
        pieModel1.setLegendPosition("w");
        pieModel1.setShadow(false);
        return pieModel1;
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
            boys.set(" ", valueCountSet.getCountPercent(val));
            boys.setLabel(vv);
            bmodel.addSeries(boys);
        }
        return bmodel;
    }

    public int getSelectedOwnerDepartmentId() {
        if (core.isCurrentSessionAdmin()) {
            return -1;
        }
        AppUser user = core.getCurrentUser();
        if (user == null || user.getDepartment() == null) {
            return 99999;
        }
        return user.getDepartment().getId();
    }

    public int getSelectedPeriodId() {
        int periodId = -1;
        String selectedPeriodString = getModel().getSelectedPeriod();
        if (!StringUtils.isBlank(selectedPeriodString)) {
            periodId = (Integer.parseInt(selectedPeriodString));
        }
        return periodId;
    }

    public int getSelectedTeacherId() {
        AcademicTeacher s = null;
        if (getModel().isTeacherListEnabled()) {
            String selectedTeacherString = getModel().getSelectedTeacher();
            if (!StringUtils.isBlank(selectedTeacherString)) {
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

        private boolean showStudentProgress;
        private Boolean validatedFilter;
        private String filterType;
        private String selectedFilter;
        private List<SelectItem> periods = new ArrayList<SelectItem>();
        private List<SelectItem> filterList = new ArrayList<SelectItem>();

        private boolean validate = false;
        private Map<String, String> valueTexts = new HashMap<>();
        private StatData statData = new StatData();
        private String chartType = "pie";

        public Model() {
            valueTexts.put("1", "Tout a fait en desaccord");
            valueTexts.put("2", "Plutot en desaccord");
            valueTexts.put("3", "Plutot en accord");
            valueTexts.put("4", "Tout a fait en accord");
        }

        public String getChartType() {
            return chartType;
        }

        public void setChartType(String chartType) {
            this.chartType = chartType;
        }

        public boolean isShowStudentProgress() {
            return showStudentProgress;
        }

        public void setShowStudentProgress(boolean showStudentProgress) {
            this.showStudentProgress = showStudentProgress;
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
            super("teacher", "Enseignant");
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
                if (AcademicPluginSecurity.isManagerOf(f)) {
                    theList.add(FacesUtils.createSelectItem(getId() + ":" + String.valueOf(f.getId()), f.resolveFullTitle()));
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
                    evalReadableFlag(),
                    null
            );
        }
    }

    public class AllGroupCondition extends AbstractGroupCondition {

        public AllGroupCondition() {
            super("all", "Tout");
        }

        @Override
        public String getSearchTitle() {
            return "Tout";
        }

        @Override
        public void onSelected() {
            //
        }

        @Override
        public void onPrepareModel() {
            //
        }

        @Override
        public List<AcademicFeedback> findFeedbacks() {
            int selectedOwnerDepartmentId = getSelectedOwnerDepartmentId();
            return feedback.findFeedbacks(
                    getSelectedPeriodId(),
                    null,
                    selectedOwnerDepartmentId < 0 ? null : selectedOwnerDepartmentId,
                    null,
                    null,
                    null,
                    null,
                    getModel().getValidatedFilter(),
                    false,
                    evalReadableFlag(),
                    null
            );
        }
    }

    private Boolean evalReadableFlag() {
        return getModel().isShowStudentProgress() ? null : Boolean.TRUE;
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
//                    f.getProgram().getDepartment().getId()==core.getCurrentUser().getDepartment().getId()
//
//                }catch (NullPointerException ex){
//
//                }
                theList.add(FacesUtils.createSelectItem(getId() + ":" + String.valueOf(f.getId()), f.getName()));
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
                    getSelectedTeacherId() < 0 ? null : getSelectedTeacherId(),
                    null,
                    classId < 0 ? null : classId,
                    getModel().getValidatedFilter(),
                    false, evalReadableFlag(),
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
                if (f.getTeacher() != null && AcademicPluginSecurity.isManagerOf(f.getTeacher())) {
                    theList.add(FacesUtils.createSelectItem(getId() + ":" + String.valueOf(f.getId()), f.getFullName()));
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
            int id = Convert.toInt(idString, IntegerParserConfig.LENIENT_F);
            return feedback.findAssignmentFeedbacks(id, getModel().getValidatedFilter(), false);
        }

        @Override
        public String getSearchTitle() {
            StringBuilder sb = new StringBuilder();
            int assignementId = getSelectedFilterIntId();
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
                boolean accept = core.isCurrentSessionAdmin();
                if (!accept) {
                    AppDepartment deptId = f.resolveDepartment();
                    accept = AcademicPluginSecurity.isManagerOf(deptId);
                }
                if (!accept) {
                    for (AcademicCourseAssignment ca : academic.findCourseAssignmentsByCoursePlan(f.getId())) {
                        if (ca.getOwnerDepartment() != null) {
                            accept = AcademicPluginSecurity.isManagerOf(ca.getOwnerDepartment());
                            if (accept) {
                                break;
                            }
                        }
                    }
                }
                if (accept) {
                    theList.add(FacesUtils.createSelectItem(getId() + ":" + String.valueOf(f.getId()), f.getFullName()));
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
                    false, evalReadableFlag(),
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
                theList.add(FacesUtils.createSelectItem(getId() + ":" + String.valueOf(f.getId()), f.getName()));
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
                    false, evalReadableFlag(),
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
