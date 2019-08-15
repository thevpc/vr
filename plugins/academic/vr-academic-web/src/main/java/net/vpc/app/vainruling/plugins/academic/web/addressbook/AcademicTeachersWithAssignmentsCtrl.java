package net.vpc.app.vainruling.plugins.academic.web.addressbook;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

import java.util.List;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;

@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Stats Stages",
        menu = "/Contact",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_WORKING,
        url = "modules/academic/addressbook/teachers-with-assignments"
)
public class AcademicTeachersWithAssignmentsCtrl {
    private Model model = new Model();


    @OnPageLoad
    public void onPageLoad() {
        getModel().setPeriod(CorePlugin.get().getCurrentPeriod());
        AcademicPlugin academicPlugin = AcademicPlugin.get();
        getModel().setSemester(academicPlugin.getCurrentSemester());
        AcademicTeacher currentTeacher = academicPlugin.getCurrentTeacher();
        getModel().setTeacherDepartment(null);
        getModel().setAssignmentDepartment(null);
        if(currentTeacher!=null && currentTeacher.getUser().getDepartment()!=null){
            getModel().setTeacherDepartment(currentTeacher.getUser().getDepartment());
        }
        onRefresh();
    }

    public void onRefresh() {
        AcademicPlugin academicPlugin = AcademicPlugin.get();
        getModel().setTeachers(
                academicPlugin.findTeachersWithAssignmentsOrIntents(
                        getModel().getPeriod()==null?-1:getModel().getPeriod().getId(),
                        getModel().getSemester()==null?-1:getModel().getSemester().getId(),
                        true,true,
                        getModel().getTeacherDepartment()==null?-1:getModel().getTeacherDepartment().getId(),
                        getModel().getAssignmentDepartment()==null?-1:getModel().getAssignmentDepartment().getId()
                )
        );
    }

    public Model getModel() {
        return model;
    }

    public static class Model{
        private AppPeriod period;
        private AcademicSemester semester;
        private AppDepartment teacherDepartment;
        private AppDepartment assignmentDepartment;
        private List<AcademicTeacher> teachers;

        public AppDepartment getTeacherDepartment() {
            return teacherDepartment;
        }

        public void setTeacherDepartment(AppDepartment teacherDepartment) {
            this.teacherDepartment = teacherDepartment;
        }

        public AppDepartment getAssignmentDepartment() {
            return assignmentDepartment;
        }

        public void setAssignmentDepartment(AppDepartment assignmentDepartment) {
            this.assignmentDepartment = assignmentDepartment;
        }

        public AppPeriod getPeriod() {
            return period;
        }

        public void setPeriod(AppPeriod period) {
            this.period = period;
        }

        public AcademicSemester getSemester() {
            return semester;
        }

        public void setSemester(AcademicSemester semester) {
            this.semester = semester;
        }

        public List<AcademicTeacher> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<AcademicTeacher> teachers) {
            this.teachers = teachers;
        }
    }
}
