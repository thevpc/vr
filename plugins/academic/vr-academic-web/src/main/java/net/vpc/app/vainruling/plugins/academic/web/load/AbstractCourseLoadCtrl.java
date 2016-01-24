/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignmentInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherStat;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppDepartment;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.VrMenuManager;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.plugins.academic.service.StatCache;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;

/**
 *
 * @author vpc
 */
public abstract class AbstractCourseLoadCtrl {

    protected Model model = new Model();

    public AbstractCourseLoadCtrl() {
    }

    private void reset() {
        getModel().setMineS1(new ArrayList<AcademicCourseAssignmentInfo>());
        getModel().setMineS2(new ArrayList<AcademicCourseAssignmentInfo>());
        getModel().setOthers(new ArrayList<AcademicCourseAssignmentInfo>());
        getModel().setAll(new HashMap<Integer, AcademicCourseAssignmentInfo>());
        TeacherStat teacherStat = new TeacherStat();
        teacherStat.setTeacher(new AcademicTeacher());

        getModel().setStat(teacherStat);
    }

    public abstract AcademicTeacher getCurrentTeacher();

    @OnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();
    }

    public boolean isFiltered(String value) {
        return Arrays.asList(getModel().getOthersFilters()).indexOf(value) >= 0;
    }

    public Set<Integer> getFilters(String filterType) {
        HashSet<Integer> all = new HashSet<>();
        for (String f : getModel().getOthersFilters()) {
            if (f.startsWith(filterType + ":")) {
                String idString = f.substring((filterType + ":").length());
                all.add(Integer.parseInt(idString));
            }
        }
        return all;
    }

    public String gotoCourseAssignment(AcademicCourseAssignment a) {
        if (a != null) {
            ObjCtrl.Config c = new ObjCtrl.Config();
            c.entity = "AcademicCourseAssignment";
            c.id = String.valueOf(a.getId());
            return VrApp.getBean(VrMenuManager.class).gotoPage("obj", VrHelper.formatJSONObject(c));
        }
        return null;
    }

    public void onRefresh() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        List<SelectItem> allValidFilters = new ArrayList<>();
        for (AcademicSemester s : a.findSemesters()) {
            allValidFilters.add(new SelectItem("semester:" + s.getId(), s.getName()));
        }
        for (AcademicCourseType s : a.findCourseTypes()) {
            allValidFilters.add(new SelectItem("courseType:" + s.getId(), s.getName()));
        }
        for (AcademicClass s : a.findAcademicClasses()) {
            allValidFilters.add(new SelectItem("class:" + s.getId(), s.getName()));
        }
        getModel().setFilterSelectItems(allValidFilters.toArray(new SelectItem[allValidFilters.size()]));

        AcademicTeacher t = getCurrentTeacher();
        boolean assigned = isFiltered("assigned");
        boolean nonassigned = isFiltered("non-assigned");
        boolean intended = isFiltered("intended");
        boolean nonintended = isFiltered("non-intended");
        boolean conflict = isFiltered("conflict");
        
        if (!assigned && !nonassigned) {
            assigned = true;
            nonassigned = true;
        }
        if (!intended && !nonintended) {
            intended = true;
            nonintended = true;
        }

        StatCache cache = new StatCache();
        final Set<Integer> semesterFilter = getFilters("semester");
        final Set<Integer> classFilter = getFilters("class");
        final Set<Integer> courseTypeFilter = getFilters("courseType");

        reset();

        Map<Integer, AcademicCourseAssignmentInfo> all = new HashMap<>();
        for (AcademicCourseAssignmentInfo b : a.findCourseAssignmentsAndIntents(null, null, cache)) {
            all.put(b.getAssignment().getId(), b);
        }
        getModel().setAll(all);
        HashSet<Integer> visited = new HashSet<Integer>();
        if (t != null) {
            getModel().setMineS1(a.findCourseAssignmentsAndIntents(t.getId(), "S1", cache));
            getModel().setMineS2(a.findCourseAssignmentsAndIntents(t.getId(), "S2", cache));
            List<AcademicCourseAssignment> mine = new ArrayList<>();
            for (AcademicCourseAssignmentInfo m : getModel().getMineS1()) {
                mine.add(m.getAssignment());
                visited.add(m.getAssignment().getId());
            }
            for (AcademicCourseAssignmentInfo m : getModel().getMineS2()) {
                mine.add(m.getAssignment());
                visited.add(m.getAssignment().getId());
            }
            getModel().setStat(a.evalTeacherStat(t.getId(), null, null, mine, false, cache));
        }

        List<AcademicCourseAssignmentInfo> others = new ArrayList<>();
        for (AcademicCourseAssignmentInfo c : a.findCourseAssignmentsAndIntents(null, null, cache)) {
            if (!visited.contains(c.getAssignment().getId())) {
                boolean _assigned = c.isAssigned();
                HashSet<String> s = new HashSet<>(c.getIntentsSet());
                boolean _intended = s.size() > 0;
                boolean accepted = true;
                if (((assigned && _assigned) || (nonassigned && !_assigned))
                        && ((intended && _intended) || (nonintended && !_intended))) {
                    //ok
                } else {
                    accepted = false;
                }
                if (accepted && semesterFilter.size() > 0) {
                    if (!semesterFilter.contains(c.getAssignment().getCoursePlan().getSemester().getId())) {
                        accepted = false;
                    }
                }
                if (accepted && classFilter.size() > 0) {
                    if (!classFilter.contains(c.getAssignment().getCoursePlan().getStudentClass().getId())) {
                        accepted = false;
                    }
                }
                if (accepted && courseTypeFilter.size() > 0) {
                    if (!courseTypeFilter.contains(c.getAssignment().getCourseType().getId())) {
                        accepted = false;
                    }
                }
                if (accepted && conflict) {
                    //show only whith conflicts
                    if (c.getIntentsSet().isEmpty()) {
                        accepted = false;
                    } else if (c.getAssignment().getTeacher() != null) {
                        accepted = (c.getIntentsSet().size() == 1
                                && !c.getAssignment().getTeacher().getContact().getFullName().equals(c.getIntentsSet().toArray()[0]))
                                || c.getIntentsSet().size() > 1;
                    } else {
                        accepted = c.getIntentsSet().size() > 1;
                    }
                }
                if (accepted) {
                    others.add(c);
                }
            }
        }
        getModel().setOthers(others);

    }

    public void addToMine(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.addIntent(t.getId(), assignementId);
        }
        onRefresh();
    }

    public void removeFromMine(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.removeIntent(t.getId(), assignementId);
        }
        onRefresh();
    }

    public void removeAllIntents(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        if (assignementId != null) {
            a.removeAllIntents(assignementId);
        }
        onRefresh();
    }

    public void doUnAssign(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.removeTeacherAcademicCourseAssignment(assignementId);
        }
        onRefresh();
    }

    public void doAssign(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.addTeacherAcademicCourseAssignment(t.getId(), assignementId);
        }
        onRefresh();
    }

    public Model getModel() {
        return model;
    }

    public void onOthersFiltersChanged() {
        onRefresh();
    }

    public boolean isAllowedUpdateMineIntents(Integer assignementId) {
        UserSession userSession = VrApp.getBean(UserSession.class);
        if (userSession == null) {
            return false;
        }
        AppUser user = userSession.getUser();
        if (user == null) {
            return false;
        }
        if (userSession.isAdmin()) {
            return true;
        }
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher teacher = a.findTeacherByUser(user.getId());
        if (teacher == null) {
            return false;
        }
        if (assignementId != null) {
            AcademicCourseAssignmentInfo t0 = getModel().getAll().get(assignementId);
            AcademicCourseAssignment t = t0 == null ? null : t0.getAssignment();
            if (t != null) {
                AppDepartment d = t.getOwnerDepartment();
                if (d != null) {
                    if (userSession.allowed("Custom.Education.CourseLoadUpdateIntents")) {
                        AppDepartment d2 = user.getDepartment();
                        if (d2 != null && d2.getId() == d.getId()) {
                            return true;
                        }
                    }
                }
                d = (t.getCoursePlan() != null && t.getCoursePlan().getStudentClass() != null && t.getCoursePlan().getStudentClass().getProgram() != null) ? t.getCoursePlan().getStudentClass().getProgram().getDepartment() : null;
                if (d != null) {
                    if (userSession.allowed("Custom.Education.CourseLoadUpdateIntents")) {
                        AppDepartment d2 = user.getDepartment();
                        if (d2 != null && d2.getId() == d.getId()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isAllowedUpdateMineAssignments(Integer assignementId) {
        UserSession userSession = VrApp.getBean(UserSession.class);
        if (userSession == null) {
            return false;
        }
        AppUser user = userSession.getUser();
        if (user == null) {
            return false;
        }
        if (userSession.isAdmin()) {
            return true;
        }

        if (assignementId != null) {
            AcademicCourseAssignmentInfo t0 = getModel().getAll().get(assignementId);
            AcademicCourseAssignment t = t0 == null ? null : t0.getAssignment();
            if (t != null) {
                AppDepartment d = t.getOwnerDepartment();
                if (d != null) {
                    if (userSession.allowed("Custom.Education.CourseLoadUpdateAssignments")) {
                        AppDepartment d2 = userSession.getUser().getDepartment();
                        if (d2 != null && d2.getId() == d.getId()) {
                            return true;
                        }
                    }
                }
                d = (t.getCoursePlan() != null && t.getCoursePlan().getStudentClass() != null && t.getCoursePlan().getStudentClass().getProgram() != null) ? t.getCoursePlan().getStudentClass().getProgram().getDepartment() : null;
                if (d != null) {
                    if (userSession.allowed("Custom.Education.CourseLoadUpdateAssignments")) {
                        AppDepartment d2 = userSession.getUser().getDepartment();
                        if (d2 != null && d2.getId() == d.getId()) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    public void doAssignByIntent(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        if (assignementId != null) {
            AcademicCourseAssignmentInfo rr = getModel().getAll().get(assignementId);
            if (rr != null) {
                final Set<Integer> s0 = rr.getIntentsUserIdsSet();
                if (s0 != null && s0.size() > 0) {
                    List<Integer> s = new ArrayList<>(s0);
                    AcademicTeacher oldTeacher = rr.getAssignment().getTeacher();
                    int newTeacherId = -1;
                    if (oldTeacher == null) {
                        newTeacherId = s.get(0);
                    } else {
                        int lastPos = s.indexOf(oldTeacher.getId());
                        if (lastPos < 0) {
                            lastPos = 0;
                        } else {
                            lastPos = (lastPos + 1) % s.size();
                        }
                        newTeacherId = s.get(lastPos);
                    }
                    a.addTeacherAcademicCourseAssignment(newTeacherId, assignementId);
                }
                onRefresh();
            }
        }
    }

    public static class Model {

        List<AcademicCourseAssignmentInfo> mineS1 = new ArrayList<>();
        List<AcademicCourseAssignmentInfo> mineS2 = new ArrayList<>();
        List<AcademicCourseAssignmentInfo> others = new ArrayList<>();
        Map<Integer, AcademicCourseAssignmentInfo> all = new HashMap<>();
        TeacherStat stat;
        boolean nonIntentedOnly = false;
        AcademicCourseAssignmentInfo selectedFromOthers = null;
        AcademicCourseAssignmentInfo selectedFromMine1 = null;
        AcademicCourseAssignmentInfo selectedFromMine2 = null;
        boolean myDisciplineOnly = true;
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "c", "td", "tp", "pm"};
        String[] othersFilters = defaultFilters;
        SelectItem[] filterSelectItems = new SelectItem[0];

        public List<AcademicCourseAssignmentInfo> getMineS1() {
            return mineS1;
        }

        public void setMineS1(List<AcademicCourseAssignmentInfo> mineS1) {
            this.mineS1 = mineS1;
        }

        public List<AcademicCourseAssignmentInfo> getMineS2() {
            return mineS2;
        }

        public void setMineS2(List<AcademicCourseAssignmentInfo> mineS2) {
            this.mineS2 = mineS2;
        }

        public Map<Integer, AcademicCourseAssignmentInfo> getAll() {
            return all;
        }

        public void setAll(Map<Integer, AcademicCourseAssignmentInfo> all) {
            this.all = all;
        }

        public List<AcademicCourseAssignmentInfo> getOthers() {
            return others;
        }

        public void setOthers(List<AcademicCourseAssignmentInfo> others) {
            this.others = others;
        }

        public TeacherStat getStat() {
            return stat;
        }

        public void setStat(TeacherStat stat) {
            this.stat = stat;
        }

        public boolean isNonIntentedOnly() {
            return nonIntentedOnly;
        }

        public void setNonIntentedOnly(boolean nonIntentedOnly) {
            this.nonIntentedOnly = nonIntentedOnly;
        }

        public boolean isMyDisciplineOnly() {
            return myDisciplineOnly;
        }

        public void setMyDisciplineOnly(boolean myDisciplineOnly) {
            this.myDisciplineOnly = myDisciplineOnly;
        }

        public AcademicCourseAssignmentInfo getSelectedFromOthers() {
            return selectedFromOthers;
        }

        public void setSelectedFromOthers(AcademicCourseAssignmentInfo selectedFromOthers) {
            this.selectedFromOthers = selectedFromOthers;
        }

        public AcademicCourseAssignmentInfo getSelectedFromMine1() {
            return selectedFromMine1;
        }

        public void setSelectedFromMine1(AcademicCourseAssignmentInfo selectedFromMine1) {
            this.selectedFromMine1 = selectedFromMine1;
        }

        public AcademicCourseAssignmentInfo getSelectedFromMine2() {
            return selectedFromMine2;
        }

        public void setSelectedFromMine2(AcademicCourseAssignmentInfo selectedFromMine2) {
            this.selectedFromMine2 = selectedFromMine2;
        }

        public String[] getOthersFilters() {
            return othersFilters;
        }

        public void setOthersFilters(String[] othersFilters) {
            this.othersFilters = (othersFilters == null || othersFilters.length == 0) ? defaultFilters : othersFilters;
        }

        public SelectItem[] getFilterSelectItems() {
            return filterSelectItems;
        }

        public void setFilterSelectItems(SelectItem[] filterSelectItems) {
            this.filterSelectItems = filterSelectItems;
        }

    }
}
