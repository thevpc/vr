package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;

public class AcademicPluginSecurity {
    public static final String RIGHT_CUSTOM_EDUCATION_RESET = "Custom.Education.Reset";
    public static final String RIGHT_CUSTOM_EDUCATION_READ_LOAD = "Custom.Education.TeacherLoad.Read";
    public static final String RIGHT_CUSTOM_EDUCATION_CONFIG_READ = "Custom.Education.Config.Read";
    public static final String RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE = "Custom.Education.Config.Write";
    public static final String RIGHT_CUSTOM_EDUCATION_CONFIG_IMPORT = "Custom.Education.Import";
    public static final String RIGHT_CUSTOM_EDUCATION_HISTORY_READ = "Custom.Education.History.Read";
    public static final String RIGHT_CUSTOM_EDUCATION_HISTORY_RESET = "Custom.Education.History.Reset";
    public static final String RIGHT_CUSTOM_EDUCATION_TEACHER = "Custom.Education.Teachers";
    public static final String RIGHT_CUSTOM_EDUCATION_STUDENTS = "Custom.Education.Students";
    public static final String RIGHT_CUSTOM_EDUCATION_ASSIGNMENTS = "Custom.Education.Assignments";
    public static final String RIGHT_CUSTOM_EDUCATION_TEACHER_COURSE_LOAD = "Custom.Education.TeacherCourseLoad";
    public static final String RIGHT_CUSTOM_EDUCATION_GLOBAL_STAT = "Custom.Education.GlobalStat";
    public static final String RIGHT_CUSTOM_EDUCATION_ALL_TEACHERS_COURSE_LOAD = "Custom.Education.AllTeachersCourseLoad";
    public static final String RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_INTENTS = "Custom.Education.CourseLoadUpdateIntents";
    public static final String RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_ASSIGNMENTS = "Custom.Education.CourseLoadUpdateAssignments";
    public static final String RIGHT_CUSTOM_EDUCATION_MY_COURSE_LOAD="Custom.Education.MyCourseLoad";
    public static final String RIGHT_CUSTOM_EDUCATION_MY_INTERNSHIPS = "Custom.Education.MyInternships";
    public static final String RIGHT_CUSTOM_EDUCATION_MY_INTERNSHIP_BOARDS = "Custom.Education.MyInternshipBoards";
    public static final String RIGHT_CUSTOM_EDUCATION_ALL_INTERNSHIPS = "Custom.Education.AllInternships";
    public static final String RIGHT_CUSTOM_EDUCATION_INTERNSHIP_BOARDS_STAT = "Custom.Education.InternshipBoardsStat";
    public static final String RIGHT_CUSTOM_EDUCATION_STUDENT_FEEDBACK = "Custom.Academic.StudentFeedback";
    public static final String RIGHT_CUSTOM_EDUCATION_STUDENT_FEEDBACK_HISTORY = "Custom.Academic.StudentFeedbackHistory";
    public static final String RIGHT_CUSTOM_EDUCATION_TEACHER_STAT_FEEDBACK = "Custom.Academic.TeacherStatFeedback";
    public static final String RIGHT_CUSTOM_EDUCATION_TEACHER_WORKING = "Custom.Academic.WorkingTeachers";

    public static final String[] RIGHTS_ACADEMIC = VrPlatformUtils.getStringArrayConstantsValues(AcademicPluginSecurity.class,"RIGHT_*");

    public static void requireTeacherOrManager(int teacher) {
        if (!isTeacherOrManager(teacher)) {
            throw new SecurityException("Not Allowed");
        }
    }

    public static boolean isTeacherOrManager(int teacher) {
        CorePlugin core = CorePlugin.get();
        if (core.isCurrentSessionAdmin()) {
            return true;
        }
        AcademicPlugin academic = AcademicPlugin.get();
        AcademicTeacher r = academic.getCurrentTeacher();
        if (r != null) {
            if (teacher <= 0 || r.getId() == teacher) {
                return true;
            }
            AppDepartment d = r.getDepartment();
            if (d != null) {
                return core.isCurrentSessionAdminOrManagerOf(d.getId());
            }
        }
        return false;
    }

    public static void requireManageableCourseAssignment(AcademicCourseAssignment a) {
        if (!isManageable(a)) {
            throw new SecurityException("Not Allowed");
        }
    }

    public static boolean isManageable(AcademicCourseAssignment a) {
        CorePlugin core = CorePlugin.get();
        if (a == null) {
            return true;
        }
        if (core.isCurrentSessionAdmin()) {
            return true;
        }
        if (a == null) {
            return false;
        }
        AppDepartment d = a.getOwnerDepartment();
        if (d != null) {
            if (core.isCurrentSessionAdminOrManagerOf(d.getId())) {
                return true;
            }
            return false;
        }
        d = a.resolveDepartment();
        if (d != null) {
            if (core.isCurrentSessionAdminOrManagerOf(d.getId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUserSessionManager() {
        CorePlugin core = CorePlugin.get();
        AppUser user = core.getCurrentUser();
        if (user == null || user.getDepartment() == null) {
            return false;
        }
        for (AppProfile u : core.findProfilesByUser(user.getId())) {
            String name = u.getName();
            if ("HeadOfDepartment".equals(name)) {
                //check if same department
                return true;
            }
            if ("DirectorOfStudies".equals(name)) {
                //check if same department
                return true;
            }
            if ("Director".equals(name)) {
                //check if same department
                return true;
            }
        }
        return false;
    }

    public static boolean isManagerOf(AppUser targetUser) {
        CorePlugin core = CorePlugin.get();
        AppUser user = core.getCurrentUser();
        if (user != null && targetUser != null && user.getId() == targetUser.getId()) {
            return true;
        }
        AppDepartment targetDept = (targetUser == null) ? null : targetUser.getDepartment();
        return isManagerOf(targetDept);
    }

    public static boolean isManagerOf(AcademicTeacher teacher) {
        CorePlugin core = CorePlugin.get();
        AppUser user = core.getCurrentUser();
        if (user != null && teacher.getUser() != null && user.getId() == teacher.getUser().getId()) {
            return true;
        }
        AppDepartment targetDept = (teacher == null) ? null : teacher.getDepartment();
        if (targetDept == null) {
            targetDept = (teacher == null || teacher.getUser() == null) ? null : teacher.getUser().getDepartment();
        }
        return isManagerOf(targetDept);
    }

    public static boolean isManagerOf(AcademicStudent student) {
        CorePlugin core = CorePlugin.get();
        AppUser user = core.getCurrentUser();
        if (user != null && student.getUser() != null && user.getId() == student.getUser().getId()) {
            return true;
        }

        AppDepartment targetDept = (student == null) ? null : student.getDepartment();
        if (targetDept == null) {
            targetDept = (student == null || student.getUser() == null) ? null : student.getUser().getDepartment();
        }
        return isManagerOf(targetDept);
    }

    public static boolean isManagerOf(AppDepartment department) {
        CorePlugin core = CorePlugin.get();
        AppUser user = core.getCurrentUser();
        if (user == null || user.getDepartment() == null) {
            return false;
        }
        int dept = user == null || user.getDepartment() == null ? -1 : user.getDepartment().getId();
        for (AppProfile u : core.findProfilesByUser(user.getId())) {
            String name = u.getName();
            if ("HeadOfDepartment".equals(name)) {
                if (dept != -1 && department != null && dept == department.getId()) {
                    //check if same department
                    return true;
                }
            }
            if ("DirectorOfStudies".equals(name)) {
                //check if same department
                return true;
            }
            if ("Director".equals(name)) {
                //check if same department
                return true;
            }
        }
        return false;
    }
}
