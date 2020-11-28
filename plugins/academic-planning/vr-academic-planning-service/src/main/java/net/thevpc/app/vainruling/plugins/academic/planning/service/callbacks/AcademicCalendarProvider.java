package net.thevpc.app.vainruling.plugins.academic.planning.service.callbacks;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPlugin;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.thevpc.upa.Action;
import net.thevpc.upa.UPA;
import org.springframework.stereotype.Component;

import java.util.*;
import net.thevpc.app.vainruling.plugins.calendars.service.AppWeekCalendarProvider;

@Component
public class AcademicCalendarProvider implements AppWeekCalendarProvider {
    @Override
    public Set<Integer> retainUsersWithPublicWeekCalendars(Set<Integer> users) {
        AcademicPlugin academicPlugin=AcademicPlugin.get();
        AcademicPlanningPlugin academicPlanningPlugin=AcademicPlanningPlugin.get();
        AppPeriod mainPeriod = CorePlugin.get().getCurrentPeriod();
        HashSet<Integer> all = new HashSet<>();
        HashSet<Integer> teachers = new HashSet<>();
        HashSet<Integer> students = new HashSet<>();
        Map<Integer, AcademicTeacher> userToTeachers = new HashMap<>();
        Map<Integer, AcademicStudent> userToStudents = new HashMap<>();
        List<AcademicTeacher> teacherList =  UPA.getPersistenceUnit().invokePrivileged(new Action<List<AcademicTeacher>>() {
            @Override
            public List<AcademicTeacher> run() {
                return academicPlugin.findEnabledTeachers(mainPeriod.getId());
            }
        });

        for (AcademicTeacher teacher : teacherList) {
            AppUser u = teacher.getUser();
            if (u != null && users.contains(u.getId())) {
                userToTeachers.put(u.getId(), teacher);
                teachers.add(teacher.getId());
            }
        }
        List<AcademicStudent> studentList = UPA.getPersistenceUnit().invokePrivileged(new Action<List<AcademicStudent>>() {
            @Override
            public List<AcademicStudent> run() {
                return academicPlugin.findStudents();
            }
        });
        for (AcademicStudent student : studentList) {
            AppUser u = student.getUser();
            if (u != null && users.contains(u.getId())) {
                userToStudents.put(u.getId(), student);
                students.add(student.getId());
            }
        }
//        for (Integer user : users) {
//            AcademicTeacher u = academicPlugin.findTeacherByUser(user);
//            if(u!=null) {
//                teachers.add(u.getId());
//            }
//            AcademicStudent s = academicPlugin.findStudentByUser(user);
//            if(s!=null) {
//                students.add(s.getId());
//            }
//        }
        for (Integer id : academicPlanningPlugin.retainTeachersWithPublicCalendars(teachers)) {
            all.add(academicPlugin.findTeacher(id).getUser().getId());
        }
        for (Integer id : academicPlanningPlugin.retainStudentsWithPublicCalendars(students)) {
            all.add(academicPlugin.findStudent(id).getUser().getId());
        }
        return all;
    }

    @Override
    public List<WeekCalendar> findWeekCalendars(String type, String key) {
        AcademicPlanningPlugin academicPlanningPlugin=AcademicPlanningPlugin.get();
        if ("class-calendar".equals(type)) {
            WeekCalendar calendarWeek = academicPlanningPlugin.loadClassPlanning(key);
            if (calendarWeek != null) {
                return Arrays.asList(calendarWeek);
            }
        }
        if ("teacher-calendar".equals(type)) {
            WeekCalendar calendarWeek = academicPlanningPlugin.loadTeacherPlanning(Integer.parseInt(key));
            if (calendarWeek != null) {
                return Arrays.asList(calendarWeek);
            }
        }
        if ("student-calendar".equals(type)) {
            return academicPlanningPlugin.loadStudentPlanningList(Integer.parseInt(key));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<WeekCalendar> findUserPrivateWeekCalendars(int userId) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<WeekCalendar> findUserPublicWeekCalendars(int userId) {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<List<WeekCalendar>>() {
            @Override
            public List<WeekCalendar> run() {
                AcademicPlugin academicPlugin=AcademicPlugin.get();
                AcademicPlanningPlugin academicPlanningPlugin=AcademicPlanningPlugin.get();
                AcademicTeacher teacherByUser = academicPlugin.findTeacherByUser(userId);
                AcademicStudent student = academicPlugin.findStudentByUser(userId);
                List<WeekCalendar> all = new ArrayList<>();
                if (teacherByUser != null) {
                    WeekCalendar e = academicPlanningPlugin.loadTeacherPlanning(teacherByUser.getId());
                    if (e != null) {
                        all.add(e);
                    }
                }
                if (student != null) {
                    List<WeekCalendar> e = academicPlanningPlugin.loadStudentPlanningList(student.getId());
                    if (e != null) {
                        for (WeekCalendar calendarWeek : e) {
                            if (calendarWeek != null) {
                                all.add(calendarWeek);
                            }
                        }
                    }
                }
                return all;
            }
        });
    }

}
