/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.pbl.web;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.plugins.academic.pbl.model.ApblSession;
import net.thevpc.app.vainruling.plugins.academic.pbl.service.ApblPlugin;
import net.thevpc.app.vainruling.plugins.academic.pbl.service.ApblPluginSecurity;
import net.thevpc.app.vainruling.plugins.academic.pbl.service.dto.ApblSessionListInfo;
import net.thevpc.app.vainruling.plugins.academic.pbl.service.dto.ApblStudentInfo;
import net.thevpc.app.vainruling.plugins.academic.pbl.service.dto.ApblTeacherInfo;
import net.thevpc.app.vainruling.plugins.academic.pbl.service.dto.ApblTeamInfo;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.strings.StringComparator;
import net.thevpc.common.strings.StringComparators;
import net.thevpc.common.strings.StringTransforms;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.util.Convert;
import net.thevpc.upa.UPA;
import net.thevpc.upa.filters.ObjectFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;

import net.thevpc.common.util.IntegerParserConfig;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = ""),
            @VrPathItem(title = "APP", css = "fa-dashboard", ctrl = ""),},
        url = "modules/academic/pbl/app-status",
        menu = "/Education/Projects/Apbl",
        securityKey = "Custom.Education.Apbl.AppStatus",
        declareSecurityKeys = "Custom.Education.Apbl.ApplyLoad"
)
@ManagedBean
public class AcademicAppStatusCtrl {

    public static final String RIGHT_FILESYSTEM_WRITE = "Custom.FileSystem.Write";
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
        double i = getValidStudentsCount() * 100.0;
        return max == 0 ? 0 : i / max;
    }

    public String[] getSelectedSessions() {
        if (getModel().isMultipleSessionSelection()) {
            return getModel().getSelectedSessions();
        }
        String currentSessionId = getModel().getCurrentSessionId();
        return StringUtils.isBlank(currentSessionId) ? new String[0] : new String[]{currentSessionId};
    }

    public String getPreferredFileName() {
        String[] selectedSessions = getSelectedSessions();
        int[] selectedSessionsInts = Convert.toPrimitiveIntArray(selectedSessions, null);
        StringBuilder sb = new StringBuilder();
        if (selectedSessionsInts.length == 0) {
//            List<ApblSession> availableSessions = apbl.findAvailableSessions();
//            selectedSessionsInts = new int[availableSessions.size()];
//            int i = 0;
//            for (ApblSession availableSession : availableSessions) {
//                selectedSessionsInts[i] = availableSession.getId();
//                i++;
//            }
            sb.append("empty-session");
        } else {
            for (int selectedSessionsInt : selectedSessionsInts) {
                ApblSession session0 = apbl.findSession(selectedSessionsInt);
                if (session0 != null) {
                    if (sb.length() > 0) {
                        sb.append("__");
                    }
                    sb.append(session0.getName());
                }
            }
        }
        return core.getPreferredFileName(sb.toString());
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
        int count = getInterDepartmentCount(validOnly);
        double i = (double) count * 100.0;
        int max = validOnly ? getValidStudentsCount() : getModel().getFilteredStudents().size();
        return max == 0 ? 0 : i / max;
    }

    public int getHomogenousCount(boolean validOnly) {
        int count = 0;
        for (ApblStudentInfo studentInfo : getModel().getFilteredStudents()) {
            if (!validOnly || studentInfo.isInvalid()) {
                if (!studentInfo.isInterDepartments() && !studentInfo.isInterClasses()) {
                    count++;
                }
            }
        }
        return count;
    }

    public double getHomogenousPercent(boolean validOnly) {
        int count = getHomogenousCount(validOnly);
        double i = (double) count * 100.0;
        int max = validOnly ? getValidStudentsCount() : getModel().getFilteredStudents().size();
        return max == 0 ? 0 : i / max;
    }

    public int getInterClassCount(boolean validOnly) {
        int count = 0;
        for (ApblStudentInfo studentInfo : getModel().getFilteredStudents()) {
            if (!validOnly || studentInfo.isInvalid()) {
                if (!studentInfo.isInterDepartments() && studentInfo.isInterClasses()) {
                    count++;
                }
            }
        }
        return count;
    }

    public double getInterClassPercent(boolean validOnly) {
        int count = getInterClassCount(validOnly);
        for (ApblStudentInfo studentInfo : getModel().getFilteredStudents()) {
            if (!studentInfo.isInterDepartments() && studentInfo.isInterClasses()) {
                count++;
            }
        }
        double i = (double) count * 100.0;
        int max = validOnly ? getValidStudentsCount() : getModel().getFilteredStudents().size();
        return max == 0 ? 0 : i / max;
    }

    public int getValidStudentsCount() {
        int count = 0;
        for (ApblStudentInfo studentInfo : getModel().getFilteredStudents()) {
            if (!studentInfo.isInvalid()) {
                count++;
            }
        }
        return count;
    }

    public Model getModel() {
        return model;
    }

    @VrOnPageLoad
    private void onPageLoad() {
        reloadPeriods();
    }

    public void reloadSessions() {
        getModel().getSessions().clear();
        String id = getModel().getCurrentPeriodId();
        for (ApblSession session : apbl.findAvailableSessions()) {
            if(String.valueOf(session.getPeriod().getId()).equals(id)) {
                getModel().getSessions().add(FacesUtils.createSelectItem(String.valueOf(session.getId()), session.getName()));
            }
        }
        getModel().getDepartments().clear();
        for (AppDepartment dep : core.findDepartments()) {
            getModel().getDepartments().add(FacesUtils.createSelectItem(String.valueOf(dep.getId()), dep.getName()));
        }

        reloadTeacherAndStudentInfos();
    }

    public void reloadPeriods() {
        getModel().getPeriods().clear();
        Map<Integer, AppPeriod> p=new LinkedHashMap<>() ;
        String old=getModel().getCurrentPeriodId();
        getModel().setCurrentPeriodId(null);
        for (ApblSession session : apbl.findAvailableSessions()) {
            p.put(session.getPeriod().getId(),session.getPeriod());
            if(String.valueOf(session.getPeriod().getId()).equals(old)){
                getModel().setCurrentPeriodId(old);
            }
        }
        if(getModel().getCurrentPeriodId()==null){
            for (AppPeriod value : p.values()) {
                getModel().setCurrentPeriodId(String.valueOf(value.getId()));
                break;
            }
        }
        for (AppPeriod value : p.values()) {
            getModel().getPeriods().add(FacesUtils.createSelectItem(String.valueOf(value.getId()), value.getName()));
        }
        reloadSessions();
    }

    public void onChangeSelectedSession() {
        reloadTeacherAndStudentInfos();
    }
    public void onChangeSelectedPeriod() {
        reloadSessions();
    }

    public void reloadTeacherAndStudentInfos() {
        String[] selectedSessions = getSelectedSessions();
        int[] selectedSessionsInts = Convert.toPrimitiveIntArray(selectedSessions, null);
        if (selectedSessionsInts.length == 0) {
//            List<ApblSession> availableSessions = apbl.findAvailableSessions();
//            selectedSessionsInts = new int[availableSessions.size()];
//            int i = 0;
//            for (ApblSession availableSession : availableSessions) {
//                selectedSessionsInts[i] = availableSession.getId();
//                i++;
//            }

        }

        getModel().setTeachers(apbl.findTeacherInfos(selectedSessionsInts, false, new ObjectFilter<AcademicTeacher>() {
            @Override
            public boolean accept(AcademicTeacher value) {
                AppDepartment d = value.getUser().getDepartment();
                String selectedDepartment = getModel().getSelectedDepartment();
                if (!(StringUtils.isBlank(selectedDepartment) || (d != null && selectedDepartment.equals("" + d.getId())))) {
                    return false;
                }
                return true;
            }
        }));
        List<ApblStudentInfo> studentInfos = apbl.findStudentInfos(selectedSessionsInts, true, new ObjectFilter<AcademicStudent>() {
            @Override
            public boolean accept(AcademicStudent value) {
                AppDepartment d = value.getUser().getDepartment();
                String selectedDepartment = getModel().getSelectedDepartment();
                if (!(StringUtils.isBlank(selectedDepartment) || (d != null && selectedDepartment.equals("" + d.getId())))) {
                    return false;
                }
                return true;
            }
        });
        studentInfos.removeIf(a
                -> (getModel().isFilterStudentsNoCoach() && !a.isErrNoCoach())
                || (getModel().isFilterStudentsNoProject() && !a.isErrNoProject())
                || (getModel().isFilterStudentsNoTeam() && !a.isErrNoTeam())
                || (getModel().isFilterStudentsMultiTeam() && !a.isErrTooManyTeams())
        );
        getModel().setStudents(studentInfos);

        onSearchTeachersByText();
        onSearchStudentsByText();
    }

    public void onSearchTeachersByText() {
        StringComparator filter = StringComparators.ilikepart(getModel().getTeachersFilterText()).apply(StringTransforms.UNIFORM);
        getModel().setFilteredTeachers(
                VrUtils.filterList(
                        getModel().getTeachers().getTeachers(),
                        value -> filter.matches(
                                value.getTeacher().resolveFullTitle()
                        )
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
                                value.getStudent().resolveFullTitle()
                                + " "
                                + (value.getCoach() != null ? value.getCoach().resolveFullTitle() : "")
                                + " "
                                + (value.getTeam() != null && value.getTeam().getTeam() != null ? value.getTeam().getTeam().getName() : "")
                                + " "
                                + ((value.getProject() != null && value.getProject().getProject() != null) ? value.getProject().getProject().getName() : "")
                                + " "
                                + (value.getStudent().getLastClass1() != null ? value.getStudent().getLastClass1().getName() : "")
                                + " "
                                + (value.getInvalidObservations())
                        );
                    }
                }
                )
        );
    }

    public void onRemoveLoadAllSelected() {
        String[] selectedSessions = getSelectedSessions();
        for (String selectedSession : selectedSessions) {
            Integer sessionId = Convert.toInt(selectedSession, IntegerParserConfig.LENIENT_F);
            apbl.removeSessionLoad(sessionId);
        }
        if (selectedSessions.length > 0) {
            FacesUtils.addInfoMessage("Application reussie");
        }
    }

    public void onRemoveLoadAll() {
        List<SelectItem> selectedSessions = getModel().getSessions();
        for (SelectItem selectedSession : selectedSessions) {
            Integer sessionId = Convert.toInt(selectedSession.getValue(), IntegerParserConfig.LENIENT_F);
            apbl.removeSessionLoad(sessionId);
        }
        if (selectedSessions.size() > 0) {
            FacesUtils.addInfoMessage("Application reussie");
        }
    }

    public void onApplyLoadAllSelected() {
        String[] selectedSessions = getSelectedSessions();
        for (String selectedSession : selectedSessions) {
            HashSet<Integer> accepted = new HashSet<>();
            for (ApblTeacherInfo t : getModel().getTeachers().getTeachers()) {
                if (t.getTeacher() != null) {
                    accepted.add(t.getTeacher().getId());
                }
            }
            Integer sessionId = Convert.toInt(selectedSession, IntegerParserConfig.LENIENT_F);
            apbl.removeSessionLoad(sessionId);
            apbl.applyTeacherLoad(sessionId, new ObjectFilter<AcademicTeacher>() {
                @Override
                public boolean accept(AcademicTeacher value) {
                    return accepted.contains(value.getId());
                }
            });
        }
        if (selectedSessions.length > 0) {
            FacesUtils.addInfoMessage("Application reussie");
        }
    }

    public void onApplyLoadAll() {
        List<SelectItem> selectedSessions = getModel().getSessions();
        for (SelectItem selectedSession : selectedSessions) {
//            HashSet<Integer> accepted = new HashSet<>();
//            for (ApblTeacherInfo t : getModel().getTeachers().getTeachers()) {
//                if (t.getTeacher() != null) {
//                    accepted.add(t.getTeacher().getId());
//                }
//            }
            Integer sessionId = Convert.toInt(selectedSession.getValue(), IntegerParserConfig.LENIENT_F);
            apbl.removeSessionLoad(sessionId);
            apbl.applyTeacherLoad(sessionId, null);
        }
        if (selectedSessions.size() > 0) {
            FacesUtils.addInfoMessage("Application reussie");
        }
    }

    public void onApplyLoad(int teacherId) {
        String[] selectedSessions = getSelectedSessions();
        HashSet<String> names=new HashSet<>();
        for (String selectedSession : selectedSessions) {
            AcademicTeacher t = academic.findTeacher(teacherId);
            if (t != null) {
                apbl.applyTeacherLoad(Convert.toInt(selectedSession, IntegerParserConfig.LENIENT_F), new ObjectFilter<AcademicTeacher>() {
                    @Override
                    public boolean accept(AcademicTeacher value) {
                        return value.getId() == teacherId;
                    }
                });
                names.add(t.resolveFullName());
            }
        }
        if (selectedSessions.length > 0) {
            FacesUtils.addInfoMessage("Application reussie : " + names);
        }
    }

    public boolean isEnabledButton(String id) {
        switch (StringUtils.trim(id)) {
            case "ApplyLoad": {
                return UPA.getPersistenceUnit().getSecurityManager().isAllowedKey(ApblPluginSecurity.RIGHT_CUSTOM_EDUCATION_APBL_APPLY_LOAD);
            }
            case "RemoveLoad": {
                return UPA.getPersistenceUnit().getSecurityManager().isAllowedKey(ApblPluginSecurity.RIGHT_CUSTOM_EDUCATION_APBL_APPLY_LOAD);
            }
        }
        return false;
    }

    public class Model {

        private ApblSessionListInfo teachers = new ApblSessionListInfo();
        private List<ApblStudentInfo> students = new ArrayList<>();
        private List<ApblTeacherInfo> filteredTeachers = new ArrayList<>();
        private List<ApblStudentInfo> filteredStudents = new ArrayList<>();
        private List<ApblTeamInfo> filteredTeams = new ArrayList<>();
        private List<SelectItem> periods = new ArrayList<>();
        private List<SelectItem> sessions = new ArrayList<>();
        private List<SelectItem> departments = new ArrayList<>();
        private String[] selectedSessions = new String[0];
        private String currentPeriodId = null;
        private String currentSessionId = null;
        private String selectedDepartment = null;
        private String studentsFilterText = null;
        private String teachersFilterText = null;
        private int activeTabIndex = 0;
        private boolean multipleSessionSelection = false;
        private boolean filterStudentsNoCoach = false;
        private boolean filterStudentsNoTeam = false;
        private boolean filterStudentsMultiTeam = false;
        private boolean filterStudentsNoProject = false;

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
        }

        public String getCurrentPeriodId() {
            return currentPeriodId;
        }

        public void setCurrentPeriodId(String currentPeriodId) {
            this.currentPeriodId = currentPeriodId;
        }

        public List<ApblTeamInfo> getFilteredTeams() {
            return filteredTeams;
        }

        public void setFilteredTeams(List<ApblTeamInfo> filteredTeams) {
            this.filteredTeams = filteredTeams;
        }

        public boolean isFilterStudentsNoCoach() {
            return filterStudentsNoCoach;
        }

        public void setFilterStudentsNoCoach(boolean filterStudentsNoCoach) {
            this.filterStudentsNoCoach = filterStudentsNoCoach;
        }

        public boolean isFilterStudentsNoTeam() {
            return filterStudentsNoTeam;
        }

        public void setFilterStudentsNoTeam(boolean filterStudentsNoTeam) {
            this.filterStudentsNoTeam = filterStudentsNoTeam;
        }

        public boolean isFilterStudentsMultiTeam() {
            return filterStudentsMultiTeam;
        }

        public void setFilterStudentsMultiTeam(boolean filterStudentsMultiTeam) {
            this.filterStudentsMultiTeam = filterStudentsMultiTeam;
        }

        public boolean isFilterStudentsNoProject() {
            return filterStudentsNoProject;
        }

        public void setFilterStudentsNoProject(boolean filterStudentsNoProject) {
            this.filterStudentsNoProject = filterStudentsNoProject;
        }

        public boolean isMultipleSessionSelection() {
            return multipleSessionSelection;
        }

        public void setMultipleSessionSelection(boolean multipleSessionSelection) {
            this.multipleSessionSelection = multipleSessionSelection;
        }

        public ApblSessionListInfo getTeachers() {
            return teachers;
        }

        public void setTeachers(ApblSessionListInfo teachers) {
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

        public String getCurrentSessionId() {
            return currentSessionId;
        }

        public void setCurrentSessionId(String currentSessionId) {
            this.currentSessionId = currentSessionId;
        }
    }
}
