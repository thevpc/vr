/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackGroup;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackModel;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackQuestion;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackResponse;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.common.strings.StringUtils;
import org.primefaces.component.slider.Slider;
import org.primefaces.event.SlideEndEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
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
//        title = "Fiches Eval. enseignements",
        menu = "/Education/Evaluation",
        url = "modules/academic/perfeval/student-feedback-example",
        securityKey = "Custom.Academic.StudentFeedbackExample"
)
public class StudentFeedbackExampleCtrl {

    private static final Logger log = Logger.getLogger(StudentFeedbackExampleCtrl.class.getName());
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

    public void onSlideEnd(SlideEndEvent event) {
        Slider s = (Slider) event.getSource();
        for (UIComponent uiComponent : s.getParent().getChildren()) {
            if (uiComponent.getId().equals("responseId")) {
                UIInput input = (UIInput) uiComponent;
                Object value = input.getValue();
                int id = Integer.parseInt("" + value);
                for (Row row : getModel().getRows()) {
                    for (Question question : row.getQuestions()) {
                        AcademicFeedbackResponse response = question.getResponse();
                        if (response.getId() == id) {
                            response.setResponse("" + event.getValue());
                            return;
                        }
                    }
                }
                //uiComponent.get
            }
        }
    }

    public void onReloadFeedbacks() {
        getModel().setFeedbacks(new ArrayList<SelectItem>());
        HashSet<String> ids = new HashSet<>();
        for (AcademicFeedbackModel f : feedback.findAcademicFeedbackModels()) {
            getModel().getFeedbacks().add(new SelectItem(String.valueOf(f.getId()), f.getName()));
            ids.add(String.valueOf(f.getId()));
        }
        if (!ids.contains(getModel().getSelectedFeedback())) {
            getModel().setSelectedFeedback(null);
            for (String id : ids) {
                getModel().setSelectedFeedback(id);
                break;
            }
        }

        onFeedbackChange();
    }

    public void onFeedbackChange() {
        List<Row> rows = new ArrayList<>();
        if (!StringUtils.isEmpty(getModel().getSelectedFeedback())) {
            getModel().setFeedback(feedback.findFeedbackModel(Integer.parseInt(getModel().getSelectedFeedback())));
            List<AcademicFeedbackGroup> groups = feedback.findStudentFeedbackGroups(getModel().getFeedback().getId());
            Map<Integer, Question> questionsMap = new HashMap<>();
            Map<Integer, Row> groupsMap = new HashMap<>();
            for (AcademicFeedbackGroup group : groups) {
                Row row = new Row();
                row.setTitle(group.getName());
                ArrayList<Question> questions = new ArrayList<Question>();
                row.setQuestions(questions);
                groupsMap.put(group.getId(), row);
                rows.add(row);
            }
            int fakeResponseId=0;
            for (AcademicFeedbackQuestion fquestion : feedback.findStudentFeedbackQuestionsByModel(getModel().getFeedback().getId())) {
                Question q = new Question();
                AcademicFeedbackResponse resp = new AcademicFeedbackResponse();
                resp.setId(fakeResponseId++);
                resp.setQuestion(fquestion);
                q.setResponse(resp);
                questionsMap.put(fquestion.getId(), q);
                Row gg = groupsMap.get(fquestion.getParent().getId());
                if (gg != null) {
                    gg.getQuestions().add(q);
                }
            }

        }
        getModel().setRows(rows);
    }

    /**
     * simple Hook
     */
    public void onValidate() {
        onSave();
    }

    /**
     * simple Hook
     */
    public void onSave() {
    }

    /**
     * simple Hook
     */
    public void onSaveAndValidate() {
    }

    public Model getModel() {
        return model;
    }

    public static class Question {

        AcademicFeedbackResponse response;

        public AcademicFeedbackResponse getResponse() {
            return response;
        }

        public void setResponse(AcademicFeedbackResponse response) {
            this.response = response;
        }

    }

    public static class Row {

        private String title;
        private List<Question> questions;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }

    }

    public static class Model {

        private String title;
        private String selectedFeedback;
        private List<SelectItem> feedbacks = new ArrayList<SelectItem>();
        private AcademicFeedbackModel feedback = null;
        private List<Row> rows = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<SelectItem> getFeedbacks() {
            return feedbacks;
        }

        public void setFeedbacks(List<SelectItem> feedbacks) {
            this.feedbacks = feedbacks;
        }

        public String getSelectedFeedback() {
            return selectedFeedback;
        }

        public void setSelectedFeedback(String selectedFeedback) {
            this.selectedFeedback = selectedFeedback;
        }

        public List<Row> getRows() {
            return rows;
        }

        public void setRows(List<Row> rows) {
            this.rows = rows;
        }

        public AcademicFeedbackModel getFeedback() {
            return feedback;
        }

        public void setFeedback(AcademicFeedbackModel feedback) {
            this.feedback = feedback;
        }

    }

}
