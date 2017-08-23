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
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
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
//        title = "Fiches Eval. enseignements",
        menu = "/Education/Evaluation",
        url = "modules/academic/perfeval/student-feedback-list",
        securityKey = "Custom.Academic.StudentFeedbackList"
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
        if(StringUtils.isEmpty(studentsFilter)){
            getModel().setStudents(new ArrayList<SelectItem>());
            getModel().setSelectedStudent(null);
        }else{
            ArrayList<SelectItem> students = new ArrayList<>();
            for (AcademicStudent academicStudent : academic.findStudents(studentsFilter, null)) {
                students.add(new SelectItem(String.valueOf(academicStudent.getId()),academicStudent.resolveFullTitle()));
            }
            getModel().setStudents(students);
            getModel().setSelectedStudent(null);
        }
        onReloadFeedbacks();
    }

    public void onReloadFeedbacks() {
        Integer studentId = Convert.toInt(getModel().getSelectedStudent(), IntegerParserConfig.LENIENT_F);
        AcademicStudent s = studentId<=0?null:academic.findStudent(studentId);
        getModel().setFeedbacks(new ArrayList<AcademicFeedback>());
        if (s != null) {
            for (AcademicFeedback f : feedback.findStudentFeedbacks(core.getCurrentPeriod().getId(),s.getId(), false, false, true,true,null)) {
                getModel().getFeedbacks().add(f);
            }
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
        private List<SelectItem> students = new ArrayList<SelectItem>();
        private AcademicFeedback feedback = null;

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
    }

}
