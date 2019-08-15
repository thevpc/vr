package net.vpc.app.vainruling.plugins.academic.teachereval.web;

import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedbackSession;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;

import java.util.List;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;

@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Fiches Eval. enseignements",
        menu = "/Education/Evaluation",
        url = "modules/academic/perfeval/student-feedback-history",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENT_FEEDBACK_HISTORY
)
public class StudentFeedbackHistoryCtrl extends StudentFeedbackCtrl {

    protected List<AcademicFeedbackSession> findSessions() {
        return feedback.findAllSessions();
    }

    protected List<AcademicFeedback> findStudentFeedbacks(int periodId, int studentId) {
        return feedback.findStudentFeedbacks(periodId, studentId, true, null, null, null, null);
    }

    @Override
    public void onValidate() {

    }

    @Override
    public void onSave() {
    }

    @Override
    public void onSaveAndValidate() {

    }
}
