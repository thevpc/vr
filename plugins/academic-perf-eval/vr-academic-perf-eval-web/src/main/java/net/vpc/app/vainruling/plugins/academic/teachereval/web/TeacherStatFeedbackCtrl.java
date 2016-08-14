/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.*;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
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
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Stats Eval. Enseignements",
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

    @OnPageLoad
    public void onLoad() {
        getModel().setValidate(false);
        getModel().setTeacherListEnabled(academic.isUserSessionManager());
        ArrayList<SelectItem> academicTeachers = new ArrayList<>();
        for (AcademicTeacher f : academic.findTeachers()) {
            academicTeachers.add(new SelectItem(String.valueOf(f.getId()), f.getContact().getFullTitle()));
        }
        getModel().setTeachersList(academicTeachers);

        ArrayList<SelectItem> filterTypesList = new ArrayList<>();
        filterTypesList.add(new SelectItem("teacher", "Enseignant"));
        filterTypesList.add(new SelectItem("assignment", "Enseignement"));
        filterTypesList.add(new SelectItem("course", "Cours"));
        getModel().setFilterTypesList(filterTypesList);
        getModel().setFilterType("teacher");
        onReloadFeedbacks();
    }

    public void onReloadFeedbacks() {

        ArrayList<SelectItem> theList = new ArrayList<>();
        getModel().setFilterList(theList);
        AcademicTeacher s = null;
        if (getModel().isTeacherListEnabled()) {
            String selectedTeacherString = getModel().getSelectedTeacher();
            if (!StringUtils.isEmpty(selectedTeacherString)) {
                s = academic.findTeacher(Integer.parseInt(selectedTeacherString));
            }
        } else {
            s = academic.getCurrentTeacher();
        }
        if (s != null) {
            HashSet<String> ids = new HashSet<>();

            String filterType = getModel().getSelectedFilterType();
            if ("assignment".equals(filterType)) {
                for (AcademicCourseAssignment f : feedback.findAssignmentsWithFeedbacks(s.getId(), getModel().getValidatedFilter(), false, true)) {
                    theList.add(new SelectItem(filterType + ":" + String.valueOf(f.getId()), f.getFullName()));
                    ids.add(filterType + ":" + String.valueOf(f.getId()));
                }
            } else if ("course".equals(filterType)) {
                for (AcademicCoursePlan f : feedback.findAcademicCoursePlansWithFeedbacks(s.getId(), getModel().getValidatedFilter(), false, true)) {
                    theList.add(new SelectItem(filterType + ":" + String.valueOf(f.getId()), f.getFullName()));
                    ids.add(filterType + ":" + String.valueOf(f.getId()));
                }
            } else if ("courseType".equals(filterType)) {
                for (AcademicCourseType f : feedback.findAcademicCourseTypesWithFeedbacks(s.getId(), getModel().getValidatedFilter(), false, true)) {
                    theList.add(new SelectItem(filterType + ":" + String.valueOf(f.getId()), f.getName()));
                    ids.add(filterType + ":" + String.valueOf(f.getId()));
                }
            } else if ("teacher".equals(filterType)) {
                for (AcademicCourseType f : feedback.findAcademicCourseTypesWithFeedbacks(s.getId(), getModel().getValidatedFilter(), false, true)) {
                    //do nothing
//                    theList.add(new SelectItem(filterType + ":" + String.valueOf(f.getId()), f.getName()));
//                    ids.add(filterType + ":" + String.valueOf(f.getId()));
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
        onFilterChange();
    }

    public void onFilterChange() {
        getModel().setValidate(false);
        getModel().setRows(evaluateGroupViews(findAcademicFeedbacks()));
    }

    public List<AcademicFeedback> findAcademicFeedbacksByAssignment(int id) {
        AcademicCourseAssignment a = academic.findAcademicCourseAssignment(id);
        if (a == null) {
            return Collections.emptyList();
        }
        return feedback.findAssignmentFeedbacks(a.getId(), getModel().getValidatedFilter(), false);
    }

    public List<AcademicFeedback> findAcademicFeedbacks() {
        String selectedFilter = getModel().getSelectedFilter();
        if (StringUtils.isEmpty(selectedFilter)) {
            return Collections.emptyList();
        }
        int pos = selectedFilter.indexOf(":");
        String filterType = selectedFilter.substring(0, pos);
        String idString = selectedFilter.substring(pos + 1);
        if ("assignment".equals(filterType)) {
            int id = Integer.parseInt(idString);
            return feedback.findAssignmentFeedbacks(id, getModel().getValidatedFilter(), false);
        } else if ("course".equals(filterType)) {
            int teacherId = -1;
            if (getModel().isTeacherListEnabled()) {
                String selectedTeacher = getModel().getSelectedTeacher();
                if (!StringUtils.isEmpty(selectedTeacher)) {
                    teacherId = Integer.parseInt(selectedTeacher);
                }
            } else {
                teacherId = academic.getCurrentTeacher().getId();
            }
            int courseId = Integer.parseInt(idString);
            return feedback.findFeedbacks(
                    courseId,
                    teacherId,
                    null,
                    null,
                    getModel().getValidatedFilter(),
                    false
            );
        } else if ("courseType".equals(filterType)) {
            int teacherId = -1;
            if (getModel().isTeacherListEnabled()) {
                String selectedTeacher = getModel().getSelectedTeacher();
                if (!StringUtils.isEmpty(selectedTeacher)) {
                    teacherId = Integer.parseInt(selectedTeacher);
                }
            } else {
                teacherId = academic.getCurrentTeacher().getId();
            }
            int courseTypeId = Integer.parseInt(idString);
            return feedback.findFeedbacks(
                    null,
                    teacherId,
                    courseTypeId,
                    null,
                    getModel().getValidatedFilter(),
                    false
            );
        } else if ("teacher".equals(filterType)) {
            int teacherId = -1;
            if (getModel().isTeacherListEnabled()) {
                String selectedTeacher = getModel().getSelectedTeacher();
                if (!StringUtils.isEmpty(selectedTeacher)) {
                    teacherId = Integer.parseInt(selectedTeacher);
                }
            } else {
                teacherId = academic.getCurrentTeacher().getId();
            }
            return feedback.findFeedbacks(
                    null,
                    teacherId,
                    null,
                    null,
                    getModel().getValidatedFilter(),
                    false
            );
        } else {
            return Collections.emptyList();
        }
    }

    public List<GroupView> evaluateGroupViews(List<AcademicFeedback> feedbacks) {
        int countFeedbacks = feedbacks.size();
        int countQuestions = 0;
        int countValidResponses = 0;
        double countResponseCompletion = 0;
        List<GroupView> rows = new ArrayList<>();
        if (feedbacks.size() > 0) {
            AcademicFeedbackModel fmodel = feedbacks.get(0).getModel();
            List<AcademicFeedbackGroup> groups = feedback.findStudentFeedbackGroups(fmodel.getId());
            Map<Integer, QuestionView> questionsMap = new HashMap<Integer, QuestionView>();

            for (AcademicFeedbackGroup group : groups) {
                GroupView row = new GroupView();
                row.setTitle(group.getName());
                ArrayList<QuestionView> questions = new ArrayList<QuestionView>();
                row.setQuestions(questions);
                for (AcademicFeedbackQuestion r : feedback.findAcademicFeedbackQuestionByGroup(group.getId())) {
                    QuestionView q = new QuestionView();
                    q.setQuestion(r);
                    q.getValues().touch("1");
                    q.getValues().touch("2");
                    q.getValues().touch("3");
                    q.getValues().touch("4");
                    questions.add(q);
                    questionsMap.put(r.getId(), q);
                }
                countQuestions += questions.size();
                rows.add(row);
            }
            int[] feedbacksIds = new int[feedbacks.size()];
            for (int i = 0; i < feedbacks.size(); i++) {
                feedbacksIds[i] = feedbacks.get(i).getId();
            }
            for (AcademicFeedbackResponse r : feedback.findStudentFeedbackResponses(feedbacksIds)) {
                QuestionView qv = questionsMap.get(r.getQuestion().getId());
                if (qv != null && !StringUtils.isEmpty(r.getResponse())) {
                    qv.getValues().inc(r.getResponse());
                    countValidResponses++;
                }
            }
            for (QuestionView value : questionsMap.values()) {
                if (!value.getValues().isEmpty()) {
                    BarChartModel bmodel = new BarChartModel();
                    bmodel.setShadow(true);
                    bmodel.setLegendCols(1);
                    bmodel.setLegendPlacement(LegendPlacement.OUTSIDE);
                    bmodel.setLegendPosition("e");
                    bmodel.setBarMargin(0);
                    bmodel.setBarPadding(0);
//                        boys.setLabel("Boys");
                    for (String val : new TreeSet<String>((Set) value.getValues().keySet())) {
                        ChartSeries boys = new ChartSeries();
                        String vv = String.valueOf(val);
                        String vv0 = getModel().getValueTexts().get(vv);
                        if (vv0 != null) {
                            vv = vv0;
                        }
                        boys.set(" ", value.getValues().getCount(val));
                        boys.setLabel(vv);
                        bmodel.addSeries(boys);
                    }

                    value.setChart(bmodel);
                }
            }
        }
        if (countQuestions != 0 && countFeedbacks != 0) {
            countResponseCompletion = (countValidResponses * 100) / (countQuestions * countFeedbacks);
        }
        getModel().setCountFeedbacks(countFeedbacks);
        getModel().setCountQuestions(countQuestions);
        getModel().setCountValidResponses(countValidResponses);
        getModel().setCountResponseCompletion(countResponseCompletion);
        return rows;
    }

    public void onValidate() {

    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private String title;
        private boolean teacherListEnabled = false;
        private String selectedTeacher;
        private String selectedFilterType = "assignement";
        private List<SelectItem> teachersList = new ArrayList<SelectItem>();
        private List<SelectItem> filterTypesList = new ArrayList<SelectItem>();

        private int countFeedbacks;
        private int countQuestions;
        private int countValidResponses;
        private double countResponseCompletion;
        private Boolean validatedFilter;
        private String filterType;
        private String selectedFilter;
        private List<SelectItem> filterList = new ArrayList<SelectItem>();
        private List<GroupView> rows = new ArrayList<>();
        private boolean validate = false;
        private Map<String, String> valueTexts = new HashMap<>();

        public Model() {
            valueTexts.put("1", "+");
            valueTexts.put("2", "++");
            valueTexts.put("3", "+++");
            valueTexts.put("4", "++++");
            valueTexts.put("5", "+++++");
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

        public List<GroupView> getRows() {
            return rows;
        }

        public void setRows(List<GroupView> rows) {
            this.rows = rows;
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

        public int getCountFeedbacks() {
            return countFeedbacks;
        }

        public Model setCountFeedbacks(int countFeedbacks) {
            this.countFeedbacks = countFeedbacks;
            return this;
        }

        public int getCountQuestions() {
            return countQuestions;
        }

        public Model setCountQuestions(int countQuestions) {
            this.countQuestions = countQuestions;
            return this;
        }

        public int getCountValidResponses() {
            return countValidResponses;
        }

        public Model setCountValidResponses(int countValidResponses) {
            this.countValidResponses = countValidResponses;
            return this;
        }

        public double getCountResponseCompletion() {
            return countResponseCompletion;
        }

        public Model setCountResponseCompletion(double countResponseCompletion) {
            this.countResponseCompletion = countResponseCompletion;
            return this;
        }
    }

}
