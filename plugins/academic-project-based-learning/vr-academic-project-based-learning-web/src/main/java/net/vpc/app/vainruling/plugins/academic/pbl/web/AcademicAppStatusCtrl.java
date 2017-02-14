/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.pbl.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.pbl.service.ApblPlugin;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.ApblStudentInfo;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.ApblTeacherInfo;
import net.vpc.app.vainruling.plugins.academic.pbl.service.model.*;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.strings.StringComparator;
import net.vpc.common.strings.StringComparators;
import net.vpc.common.strings.StringTransforms;
import net.vpc.common.util.Convert;
import net.vpc.upa.filters.ObjectFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = ""),
                @UPathItem(title = "APP", css = "fa-dashboard", ctrl = ""),
        },
        url = "modules/academic/pbl/app-status",
        menu = "/Education/Projects/Apbl",
        securityKey = "Custom.Education.Apbl.AppStatus"
)
public class AcademicAppStatusCtrl {
    public static final Logger log = Logger.getLogger(AcademicAppProjectsCtrl.class.getName());
    @Autowired
    private ApblPlugin apbl;
    @Autowired
    private CorePlugin core;
    @Autowired
    private AcademicPlugin academic;

    private Model model = new Model();

    public double getValidStudentsPercent() {
        int max = getModel().getFilteredStudents().size();
        double i = getValidStudentsCount()* 100.0;
        return max==0?0:i/max;
    }

    public int getInterDepartmentCount(boolean validOnly) {
        int count = 0;
        for (ApblStudentInfo studentInfo : getModel().getFilteredStudents()) {
            if (studentInfo.isInterDepartments()) {
                count++;
            }
        }
        return count;
    }
    public double getInterDepartmentPercent(boolean validOnly) {
        int count=getInterDepartmentCount(validOnly);
        double i=(double)count * 100.0;
        int max = validOnly? getValidStudentsCount(): getModel().getFilteredStudents().size();
        return max==0?0:i/max;
    }

    public int getHomogenousCount(boolean validOnly) {
        int count=0;
        for (ApblStudentInfo studentInfo : getModel().getFilteredStudents()) {
            if(!validOnly || studentInfo.isInvalid()) {
                if (!studentInfo.isInterDepartments() && !studentInfo.isInterClasses()) {
                    count++;
                }
            }
        }
        return count;
    }

    public double getHomogenousPercent(boolean validOnly) {
        int count=getHomogenousCount(validOnly);
        double i=(double)count * 100.0;
        int max = validOnly? getValidStudentsCount(): getModel().getFilteredStudents().size();
        return max==0?0:i/max;
    }

    public int getInterClassCount(boolean validOnly) {
        int count=0;
        for (ApblStudentInfo studentInfo : getModel().getFilteredStudents()) {
            if(!validOnly || studentInfo.isInvalid()) {
                if (!studentInfo.isInterDepartments() && studentInfo.isInterClasses()) {
                    count++;
                }
            }
        }
        return count;
    }

    public double getInterClassPercent(boolean validOnly) {
        int count=getInterClassCount(validOnly);
        for (ApblStudentInfo studentInfo : getModel().getFilteredStudents()) {
            if(!studentInfo.isInterDepartments() && studentInfo.isInterClasses()){
                count++;
            }
        }
        double i=(double)count* 100.0;
        int max = validOnly? getValidStudentsCount(): getModel().getFilteredStudents().size();
        return max==0?0:i/max;
    }

    public int getValidStudentsCount() {
        int count=0;
        for (ApblStudentInfo studentInfo : getModel().getFilteredStudents()) {
            if(!studentInfo.isInvalid()){
                count++;
            }
        }
        return count;
    }

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    private void onPageLoad() {
        reloadSessions();
    }


    public void reloadSessions() {
        getModel().getSessions().clear();
        for (ApblSession session : apbl.findAvailableSessions()) {
            getModel().getSessions().add(new SelectItem(session.getId(), session.getName()));
        }

        getModel().getDepartments().clear();
        for (AppDepartment dep : core.findDepartments()) {
            getModel().getDepartments().add(new SelectItem(dep.getId(), dep.getName()));
        }

        reloadTeacherAndStudentInfos();
    }

    public void onChangeSelectedSession() {
        reloadTeacherAndStudentInfos();
    }

    public void reloadTeacherAndStudentInfos() {
        String[] selectedSessions = getModel().getSelectedSessions();
        int[] selectedSessionsInts = Convert.toPrimitiveIntArray(selectedSessions, null);
        if (selectedSessionsInts.length == 0) {
            List<ApblSession> availableSessions = apbl.findAvailableSessions();
            selectedSessionsInts = new int[availableSessions.size()];
            int i = 0;
            for (ApblSession availableSession : availableSessions) {
                selectedSessionsInts[i] = availableSession.getId();
                i++;
            }
        }
        getModel().setTeachers(apbl.findTeacherInfos(selectedSessionsInts, false, new ObjectFilter<AcademicTeacher>() {
            @Override
            public boolean accept(AcademicTeacher value) {
                AppDepartment d = value.getDepartment();
                String selectedDepartment = getModel().getSelectedDepartment();
                if (!(StringUtils.isEmpty(selectedDepartment) || (d != null && selectedDepartment.equals("" + d.getId())))) {
                    return false;
                }
                return true;
            }
        }));
        getModel().setStudents(apbl.findStudentInfos(selectedSessionsInts, true, new ObjectFilter<AcademicStudent>() {
            @Override
            public boolean accept(AcademicStudent value) {
                AppDepartment d = value.getDepartment();
                String selectedDepartment = getModel().getSelectedDepartment();
                if (!(StringUtils.isEmpty(selectedDepartment) || (d != null && selectedDepartment.equals("" + d.getId())))) {
                    return false;
                }
                return true;
            }
        }));
        onSearchTeachersByText();
        onSearchStudentsByText();
    }


    public void onSearchTeachersByText() {
        StringComparator filter = StringComparators.ilikepart(getModel().getTeachersFilterText()).apply(StringTransforms.UNIFORM);
        getModel().setFilteredTeachers(
                VrUtils.filterList(
                        getModel().getTeachers(),
                        new ObjectFilter<ApblTeacherInfo>() {
                            @Override
                            public boolean accept(ApblTeacherInfo value) {
                                return filter.matches(
                                        value.getTeacher().getContact().getFullTitle()
                                );
                            }
                        }
                )
        );
    }

    public void onSearchStudentsByText() {
        StringComparator filter = StringComparators.ilikepart(getModel().getStudentsFilterText()).apply(StringTransforms.UNIFORM);
        getModel().setFilteredStudents(
                VrUtils.filterList(
                        getModel().getStudents(),
                        new ObjectFilter<ApblStudentInfo>() {
                            @Override
                            public boolean accept(ApblStudentInfo value) {
                                return filter.matches(
                                        value.getStudent().getContact().getFullTitle()
                                        +" "
                                        + (value.getCoach()!=null?value.getCoach().getContact().getFullTitle():"")
                                        +" "
                                        + (value.getTeam()!=null && value.getTeam().getTeam()!=null ?value.getTeam().getTeam().getName():"")
                                        +" "
                                        + ((value.getProject()!=null && value.getProject().getProject()!=null) ?value.getProject().getProject().getName():"")
                                        +" "
                                        + (value.getStudent().getLastClass1()!=null ?value.getStudent().getLastClass1().getName():"")
                                        +" "
                                        + (value.getInvalidObservations())
                                );
                            }
                        }
                )
        );
    }

    public class Model {
        private List<ApblTeacherInfo> teachers = new ArrayList<>();
        private List<ApblStudentInfo> students = new ArrayList<>();
        private List<ApblTeacherInfo> filteredTeachers = new ArrayList<>();
        private List<ApblStudentInfo> filteredStudents = new ArrayList<>();
        private List<SelectItem> sessions = new ArrayList<>();
        private List<SelectItem> departments = new ArrayList<>();
        private String[] selectedSessions = new String[0];
        private String selectedDepartment = null;
        private String studentsFilterText = null;
        private String teachersFilterText = null;
        private int activeTabIndex = 0;

        public List<ApblTeacherInfo> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<ApblTeacherInfo> teachers) {
            this.teachers = teachers;
        }

        public List<ApblStudentInfo> getStudents() {
            return students;
        }

        public void setStudents(List<ApblStudentInfo> students) {
            this.students = students;
        }

        public List<ApblTeacherInfo> getFilteredTeachers() {
            return filteredTeachers;
        }

        public void setFilteredTeachers(List<ApblTeacherInfo> filteredTeachers) {
            this.filteredTeachers = filteredTeachers;
        }

        public List<ApblStudentInfo> getFilteredStudents() {
            return filteredStudents;
        }

        public void setFilteredStudents(List<ApblStudentInfo> filteredStudents) {
            this.filteredStudents = filteredStudents;
        }

        public List<SelectItem> getSessions() {
            return sessions;
        }

        public void setSessions(List<SelectItem> sessions) {
            this.sessions = sessions;
        }

        public List<SelectItem> getDepartments() {
            return departments;
        }

        public void setDepartments(List<SelectItem> departments) {
            this.departments = departments;
        }

        public String[] getSelectedSessions() {
            return selectedSessions;
        }

        public void setSelectedSessions(String[] selectedSessions) {
            this.selectedSessions = selectedSessions;
        }

        public String getSelectedDepartment() {
            return selectedDepartment;
        }

        public void setSelectedDepartment(String selectedDepartment) {
            this.selectedDepartment = selectedDepartment;
        }

        public String getStudentsFilterText() {
            return studentsFilterText;
        }

        public void setStudentsFilterText(String studentsFilterText) {
            this.studentsFilterText = studentsFilterText;
        }

        public String getTeachersFilterText() {
            return teachersFilterText;
        }

        public void setTeachersFilterText(String teachersFilterText) {
            this.teachersFilterText = teachersFilterText;
        }

        public int getActiveTabIndex() {
            return activeTabIndex;
        }

        public void setActiveTabIndex(int activeTabIndex) {
            this.activeTabIndex = activeTabIndex;
        }
    }
}
