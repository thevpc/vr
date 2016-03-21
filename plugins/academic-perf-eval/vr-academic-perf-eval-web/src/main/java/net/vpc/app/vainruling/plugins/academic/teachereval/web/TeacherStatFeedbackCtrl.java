/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.ValueCountSet;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackGroup;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackModel;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackQuestion;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackResponse;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.common.strings.StringUtils;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author vpc
 */
@ManagedBean
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Eval. Mes Enseignements",
        menu = "/Education/Evaluation",
        url = "modules/academic/perfeval/teacherstatfeedback",
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
        onReloadFeedbacks();
    }

    public void onReloadFeedbacks() {
        getModel().setValidate(false);
        AcademicTeacher s = academic.getCurrentTeacher();
        getModel().setAcademicCourseAssignmentList(new ArrayList<SelectItem>());
        if (s != null) {
            HashSet<String> ids = new HashSet<>();
            for (AcademicCourseAssignment f : feedback.findAssignmentsWithFeedbacks(s.getId(), false, false)) {
                getModel().getAcademicCourseAssignmentList().add(new SelectItem(String.valueOf(f.getId()), f.getFullName()));
                ids.add(String.valueOf(f.getId()));
            }
            if (!ids.contains(getModel().getSelectedAcademicCourseAssignment())) {
                getModel().setSelectedAcademicCourseAssignment(null);
                for (String id : ids) {
                    getModel().setSelectedAcademicCourseAssignment(id);
                    break;
                }
            }
        }
        onFilterChange();
    }

    public void onFilterChange() {
        getModel().setValidate(false);
        List<GroupView> rows = new ArrayList<>();
        if (!StringUtils.isEmpty(getModel().getSelectedAcademicCourseAssignment())) {
            getModel().setAcademicCourseAssignment(academic.findAcademicCourseAssignment(Integer.parseInt(getModel().getSelectedAcademicCourseAssignment())));
            List<AcademicFeedback> feedbacks = feedback.findAssignmentFeedbacks(getModel().getAcademicCourseAssignment().getId(), false, false);
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
                        q.getValues().touch("5");
                        questions.add(q);
                        questionsMap.put(r.getId(), q);
                    }
                    rows.add(row);
                }
                for (AcademicFeedback fb : feedbacks) {
                    //ValueCountSet
                    for (AcademicFeedbackResponse r : feedback.findStudentFeedbackResponses(fb.getId())) {
                        QuestionView qv = questionsMap.get(r.getQuestion().getId());
                        if (qv != null && !StringUtils.isEmpty(r.getResponse())) {
                            qv.getValues().inc(r.getResponse());
                        }
                    }
                }
                for (QuestionView value : questionsMap.values()) {
                    if (!value.getValues().isEmpty()) {
                        BarChartModel bmodel = new BarChartModel();
                        bmodel.setShadow(true);
                        ChartSeries boys = new ChartSeries();
//                        boys.setLabel("Boys");
                        for (String val : new TreeSet<String>((Set) value.getValues().keySet())) {
                            String vv = String.valueOf(val);
                            String vv0 = getModel().getValueTexts().get(vv);
                            if (vv0 != null) {
                                vv = vv0;
                            }
                            boys.set(vv, value.getValues().getCount(val));
                        }

                        bmodel.addSeries(boys);
                        value.setChart(bmodel);
                    }
                }
            }
        }
        getModel().setRows(rows);
    }

    public void onValidate() {

    }

    public static class QuestionView {

        AcademicFeedbackQuestion question;
        ValueCountSet values = new ValueCountSet();
        BarChartModel chart;

        public AcademicFeedbackQuestion getQuestion() {
            return question;
        }

        public void setQuestion(AcademicFeedbackQuestion question) {
            this.question = question;
        }

        public ValueCountSet getValues() {
            return values;
        }

        public void setValues(ValueCountSet values) {
            this.values = values;
        }

        public BarChartModel getChart() {
            return chart;
        }

        public void setChart(BarChartModel chart) {
            this.chart = chart;
        }

    }

    public static class GroupView {

        private String title;
        private List<QuestionView> questions;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<QuestionView> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionView> questions) {
            this.questions = questions;
        }

    }

    public static class Model {

        private String title;
        private String selectedAcademicCourseAssignment;
        private List<SelectItem> academicCourseAssignmentList = new ArrayList<SelectItem>();
        private AcademicCourseAssignment academicCourseAssignment = null;
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

        public List<SelectItem> getAcademicCourseAssignmentList() {
            return academicCourseAssignmentList;
        }

        public void setAcademicCourseAssignmentList(List<SelectItem> academicCourseAssignmentList) {
            this.academicCourseAssignmentList = academicCourseAssignmentList;
        }

        public String getSelectedAcademicCourseAssignment() {
            return selectedAcademicCourseAssignment;
        }

        public void setSelectedAcademicCourseAssignment(String selectedAcademicCourseAssignment) {
            this.selectedAcademicCourseAssignment = selectedAcademicCourseAssignment;
        }

        public List<GroupView> getRows() {
            return rows;
        }

        public void setRows(List<GroupView> rows) {
            this.rows = rows;
        }

        public AcademicCourseAssignment getAcademicCourseAssignment() {
            return academicCourseAssignment;
        }

        public void setAcademicCourseAssignment(AcademicCourseAssignment academicCourseAssignment) {
            this.academicCourseAssignment = academicCourseAssignment;
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

    }

    public Model getModel() {
        return model;
    }

}
