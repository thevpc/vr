/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.FRow;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.FeedbackForm;
import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedbackSession;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Fiches Eval. enseignements",
        menu = "/Education/Evaluation",
        url = "modules/academic/perfeval/student-feedback2",
        securityKey = "Admin"
//        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENT_FEEDBACK
)
public class StudentFeedback2Ctrl {

    private static final Logger log = Logger.getLogger(StudentFeedback2Ctrl.class.getName());
    @Autowired
    protected CorePlugin core;
    @Autowired
    protected AcademicPlugin academic;
    @Autowired
    protected AcademicPerfEvalPlugin feedback;
    protected Model model = new Model();

    @OnPageLoad
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
        getModel().setClasses(
                academic.findAcademicClasses().stream().map(x -> {
                    return FacesUtils.createSelectItem(String.valueOf(x.getId()), x.getName());
                }).collect(Collectors.toList())
        );
        getModel().setPeriodId(VrWebHelper.revalidateSelectItemId(getModel().getClasses(), getModel().getPeriodId()));
        getModel().setClassId(VrWebHelper.revalidateSelectItemId(getModel().getClasses(), getModel().getClassId()));
        onClassChange();
    }

    public void onClassChange() {
        String id = getModel().getClassId();
        if (!StringUtils.isBlank(id)) {
            getModel().setStudents(academic.findStudentsByClass(Integer.parseInt(id), 1, 2, 3)
                    .stream().map(x -> {
                        return FacesUtils.createSelectItem(String.valueOf(x.getId()), x.getUser().getFullName());
                    }).collect(Collectors.toList())
            );
            getModel().setStudentId(VrWebHelper.revalidateSelectItemId(getModel().getStudents(), getModel().getStudentId()));
        } else {
            getModel().setStudents(new ArrayList<>());
            getModel().setStudentId(null);
        }
        onStudentOrPeriodChange();
    }

    protected List<AcademicFeedbackSession> findSessions() {
        return feedback.findAllWritableSessions();
    }

    protected List<AcademicFeedback> findStudentFeedbacks(int periodId, int studentId) {
        return feedback.findStudentFeedbacks(periodId, studentId, null, null, null, null, null);
    }

    public void onStudentOrPeriodChange() {
        AcademicStudent s = StringUtils.isBlank(getModel().getStudentId()) ? null : academic.findStudent(Integer.parseInt(getModel().getStudentId()));
        AppPeriod p = StringUtils.isBlank(getModel().getPeriodId()) ? null : core.findPeriod(Integer.parseInt(getModel().getPeriodId()));
        getModel().setFeedbacks(new ArrayList<SelectItem>());
        if (s != null && p != null) {
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

    public void onUpdatePeriod() {
        onStudentOrPeriodChange();
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private String title;
        private String selectedFeedback;
        private String periodId;
        private String classId;
        private String studentId;
        private List<SelectItem> feedbacks = new ArrayList<SelectItem>();
        private List<SelectItem> periods = new ArrayList<SelectItem>();
        private List<SelectItem> classes = new ArrayList<SelectItem>();
        private List<SelectItem> students = new ArrayList<SelectItem>();
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

        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public List<SelectItem> getClasses() {
            return classes;
        }

        public void setClasses(List<SelectItem> classes) {
            this.classes = classes;
        }

        public List<SelectItem> getStudents() {
            return students;
        }

        public void setStudents(List<SelectItem> students) {
            this.students = students;
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
