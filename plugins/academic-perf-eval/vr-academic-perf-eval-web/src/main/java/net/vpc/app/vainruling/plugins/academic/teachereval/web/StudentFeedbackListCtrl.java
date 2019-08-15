/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.StatData;
import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudentStage;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;
import java.util.logging.Logger;
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
        url = "modules/academic/perfeval/student-feedback-list",
        securityKey = AcademicPerfEvalPluginSecurity.RIGHT_CUSTOM_ACADEMIC_STUDENT_FEEDBACK_LIST
)
public class StudentFeedbackListCtrl {

    private static final Logger log = Logger.getLogger(StudentFeedbackListCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    @Autowired
    private AcademicPlugin academic;
    @Autowired
    private AcademicPerfEvalPlugin feedback;
    private Model model = new Model();

    @OnPageLoad
    public void onLoad() {
        onReloadStudents();
    }

    public void onReloadStudents() {
        String studentsFilter=getModel().getStudentsFilter();
        if(StringUtils.isBlank(studentsFilter)){
            studentsFilter="Student";
        }
        ArrayList<SelectItem> students = new ArrayList<>();
        for (AcademicStudent academicStudent : academic.findStudents(studentsFilter, AcademicStudentStage.ATTENDING, null)) {
            students.add(FacesUtils.createSelectItem(String.valueOf(academicStudent.getId()),academicStudent.resolveFullTitle()));
        }
        getModel().setStudents(students);
        getModel().setSelectedStudent(null);
        onReloadFeedbacks();
    }

    public void onReloadFeedbacks() {
        Integer studentId = Convert.toInt(getModel().getSelectedStudent(), IntegerParserConfig.LENIENT_F);
        AcademicStudent s = studentId<=0?null:academic.findStudent(studentId);
        getModel().setFeedbacks(new ArrayList<AcademicFeedback>());
        getModel().setFeedbacks(new ArrayList<>());
        getModel().setFeedbackExts(new ArrayList<>());
        if (s != null) {
            for (AcademicFeedback f : feedback.findStudentFeedbacks(core.getCurrentPeriod().getId(),s.getId(), null/*false*/, null/*false*/, null/*true*/,null/*true*/,null)) {
                getModel().getFeedbacks().add(f);
                getModel().getFeedbackExts().add(new AcademicFeedbackExt(
                        f,
                        AcademicPerfEvalPlugin.get().evalStatData(Arrays.asList(f))

                ));
            }
        }
        getModel().setStats(AcademicPerfEvalPlugin.get().evalStatData(getModel().getFeedbacks()));
    }

    public static class AcademicFeedbackExt{
        private AcademicFeedback data;
        private double completion;

        public AcademicFeedbackExt(AcademicFeedback data, StatData completion) {
            this.data=data;
            this.completion=completion.getCountResponseCompletion();
        }

        public AcademicFeedbackExt(AcademicFeedback data, double completion) {
            this.data = data;
            this.completion = completion;
        }

        public AcademicFeedback getData() {
            return data;
        }

        public double getCompletion() {
            return completion;
        }
    }

    public Model getModel() {
        return model;
    }


    public static class Model {

        private String title;
        private String selectedFeedback;
        private String studentsFilter;
        private String selectedStudent;
        private List<AcademicFeedback> feedbacks = new ArrayList<AcademicFeedback>();
        private List<AcademicFeedbackExt> feedbackExts = new ArrayList<AcademicFeedbackExt>();
        private List<SelectItem> students = new ArrayList<SelectItem>();
        private AcademicFeedback feedback = null;
        private StatData stats = null;

        public StatData getStats() {
            return stats;
        }

        public void setStats(StatData stats) {
            this.stats = stats;
        }

        public String getStudentsFilter() {
            return studentsFilter;
        }

        public void setStudentsFilter(String studentsFilter) {
            this.studentsFilter = studentsFilter;
        }

        public String getSelectedStudent() {
            return selectedStudent;
        }

        public void setSelectedStudent(String selectedStudent) {
            this.selectedStudent = selectedStudent;
        }

        public List<SelectItem> getStudents() {
            return students;
        }

        public void setStudents(List<SelectItem> students) {
            this.students = students;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSelectedFeedback() {
            return selectedFeedback;
        }

        public void setSelectedFeedback(String selectedFeedback) {
            this.selectedFeedback = selectedFeedback;
        }

        public AcademicFeedback getFeedback() {
            return feedback;
        }

        public void setFeedback(AcademicFeedback feedback) {
            this.feedback = feedback;
        }

        public List<AcademicFeedback> getFeedbacks() {
            return feedbacks;
        }

        public void setFeedbacks(List<AcademicFeedback> feedbacks) {
            this.feedbacks = feedbacks;
        }

        public List<AcademicFeedbackExt> getFeedbackExts() {
            return feedbackExts;
        }

        public void setFeedbackExts(List<AcademicFeedbackExt> feedbackExts) {
            this.feedbackExts = feedbackExts;
        }
    }

}
