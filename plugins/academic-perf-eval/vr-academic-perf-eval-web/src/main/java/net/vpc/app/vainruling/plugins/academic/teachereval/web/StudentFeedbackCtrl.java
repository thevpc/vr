/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.FQuestion;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.FRow;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.FeedbackForm;
import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedbackResponse;
import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedbackSession;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.UPA;
import org.primefaces.component.slider.Slider;
import org.primefaces.event.SlideEndEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrPathItem;
import net.vpc.app.vainruling.VrOnPageLoad;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Fiches Eval. enseignements",
        menu = "/Education/Evaluation",
        url = "modules/academic/perfeval/student-feedback",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENT_FEEDBACK
)
public class StudentFeedbackCtrl {

    private static final Logger log = Logger.getLogger(StudentFeedbackCtrl.class.getName());
    @Autowired
    protected CorePlugin core;
    @Autowired
    protected AcademicPlugin academic;
    @Autowired
    protected AcademicPerfEvalPlugin feedback;
    protected Model model = new Model();

    @VrOnPageLoad
    public void onLoad() {
        ArrayList<SelectItem> items = new ArrayList<>();
        HashSet<Integer> visitedPeriods = new HashSet<>();
        for (AcademicFeedbackSession f : findSessions()) {
            if (f.getPeriod() != null && !visitedPeriods.contains(f.getPeriod().getId())) {
                visitedPeriods.add(f.getPeriod().getId());
                String n = f.getPeriod().getName();
                items.add(FacesUtils.createSelectItem(String.valueOf(f.getPeriod().getId()), n));
            }
        }
        getModel().setPeriods(items);
        onReloadFeedbacks();
    }

    protected List<AcademicFeedbackSession> findSessions() {
        return feedback.findAllWritableSessions();
    }

    protected List<AcademicFeedback> findStudentFeedbacks(int periodId,int studentId){
        return feedback.findStudentFeedbacks(periodId, studentId, false, false, null, null, true);
    }

    public void onSlideEnd(SlideEndEvent event) {
        Slider s = (Slider) event.getSource();
        for (UIComponent uiComponent : s.getParent().getChildren()) {
            if (uiComponent.getId().equals("responseId")) {
                UIInput input = (UIInput) uiComponent;
                Object value = input.getValue();
                int id = Integer.parseInt("" + value);
                for (FRow row : getModel().getRows()) {
                    for (FQuestion question : row.getQuestions()) {
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
        AcademicStudent s = academic.getCurrentStudent();
        getModel().setFeedbacks(new ArrayList<SelectItem>());
        AppPeriod p = null;
        if (StringUtils.isBlank(getModel().getPeriodId())) {
            p = core.getCurrentPeriod();
            //
        } else {
            p = core.findPeriod(Integer.parseInt(getModel().getPeriodId()));
        }
        if (p == null) {
            p = core.getCurrentPeriod();
        }
        if (s != null) {
            HashSet<String> ids = new HashSet<>();
            for (AcademicFeedback f : findStudentFeedbacks(p.getId(), s.getId())) {
                getModel().getFeedbacks().add(FacesUtils.createSelectItem(String.valueOf(f.getId()), f.getCourse().getFullName() + " - " + academic.getValidName(f.getCourse().getTeacher())));
                ids.add(String.valueOf(f.getId()));
            }
            if (!ids.contains(getModel().getSelectedFeedback())) {
                getModel().setSelectedFeedback(null);
                for (String id : ids) {
                    getModel().setSelectedFeedback(id);
                    break;
                }
            }
        }
        onFeedbackChange();
    }

    public void onFeedbackChange() {
        FeedbackForm form;
        if (StringUtils.isBlank(getModel().getSelectedFeedback())) {
            form = new FeedbackForm();
        } else {
            form = feedback.createFeedbackForm(Integer.parseInt(getModel().getSelectedFeedback()), -1);
        }
        getModel().setFeedback(form.getFeedback());
        getModel().setRows(form.getRows());
    }

    public void onValidate() {
        onSave();
    }

    public void onSave() {
        for (FRow row : getModel().getRows()) {
            for (FQuestion question : row.getQuestions()) {
                feedback.saveResponse(question.getResponse());
            }
        }
        FacesUtils.addInfoMessage("Formulaire enregistré");
    }

    public void onSaveAndValidate() {
        onSave();
        boolean allvalid = true;
        for (FRow row : getModel().getRows()) {
            for (FQuestion question : row.getQuestions()) {
                if (StringUtils.isBlank(question.getResponse().getResponse())) {
                    allvalid = false;
                }
            }
        }
        if (allvalid) {
            getModel().getFeedback().setValidated(true);
            UPA.getPersistenceUnit().merge(getModel().getFeedback());
            getModel().setSelectedFeedback(null);
            getModel().setFeedback(null);
            onReloadFeedbacks();
            FacesUtils.addInfoMessage("Formulaire validé");
            if (getModel().getFeedbacks().isEmpty()) {
                FacesUtils.addInfoMessage("Merci beaucoup pour votre implication. Toutes les fiches sont validées");
            }
        } else {
            FacesUtils.addErrorMessage("Merci de repondre à toutes les questions avant de valider");
        }
    }

    public void onUpdatePeriod() {
        onReloadFeedbacks();
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private String title;
        private String selectedFeedback;
        private String periodId;
        private List<SelectItem> feedbacks = new ArrayList<SelectItem>();
        private List<SelectItem> periods = new ArrayList<SelectItem>();
        private AcademicFeedback feedback = null;
        private List<FRow> rows = new ArrayList<>();

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
        }

        public String getPeriodId() {
            return periodId;
        }

        public void setPeriodId(String periodId) {
            this.periodId = periodId;
        }

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

        public List<FRow> getRows() {
            return rows;
        }

        public void setRows(List<FRow> rows) {
            this.rows = rows;
        }

        public AcademicFeedback getFeedback() {
            return feedback;
        }

        public void setFeedback(AcademicFeedback feedback) {
            this.feedback = feedback;
        }

    }

}
