/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import net.vpc.app.vainruling.core.web.obj.ObjCtrl;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseFilter;
import net.vpc.app.vainruling.plugins.academic.service.StatCache;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherPeriodStat;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
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
        TeacherPeriodStat teacherStat = new TeacherPeriodStat();
        teacherStat.setTeacher(new AcademicTeacher());

        getModel().setStat(teacherStat);
    }

    public abstract AcademicTeacher getCurrentTeacher();

    public void onRefreshFiltersChanged() {
        onRefresh();
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        List<AppPeriod> navigatablePeriods = core.findNavigatablePeriods();
        AppPeriod mainPeriod = core.findAppConfig().getMainPeriod();
        getModel().setSelectedPeriod(null);
        getModel().getPeriods().clear();
        for (AppPeriod p : navigatablePeriods) {
            getModel().getPeriods().add(new SelectItem(String.valueOf(p.getId()), p.getName()));
            if (mainPeriod != null && p.getId() == mainPeriod.getId()) {
                getModel().setSelectedPeriod(String.valueOf(p.getId()));
            }
        }
        onPeriodChanged();
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

    public int getPeriodId() {
        String p = getModel().getSelectedPeriod();
        if (StringUtils.isEmpty(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig appConfig = core.findAppConfig();
            if(appConfig!=null && appConfig.getMainPeriod()!=null) {
                return appConfig.getMainPeriod().getId();
            }
            return -1;
        }
        return Integer.parseInt(p);
    }

    public void onPeriodChanged() {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AppDepartment userDepartment = getUserDepartment();
        int periodId = getPeriodId();
        List<SelectItem> refreshableFilers = new ArrayList<>();

        getModel().setEnableLoadEditing(
                (userDepartment == null || periodId<0)? false :
                        ap.getAppDepartmentPeriodRecord(periodId, userDepartment.getId()).getBoolean("enableLoadEditing", false)
        );

        refreshableFilers.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
        for (AcademicProgramType pt : ap.findProgramTypes()) {
            refreshableFilers.add(FacesUtils.createSelectItem("AcademicProgramType:" + pt.getId(), pt.getName(), "vr-checkbox"));
        }
        if(periodId>=-1) {
            for (String label : ap.findCoursePlanLabels(periodId)) {
                refreshableFilers.add(FacesUtils.createSelectItem("label:" + label, label, "vr-checkbox"));
                refreshableFilers.add(FacesUtils.createSelectItem("label:!" + label, "!" + label, "vr-checkbox"));
            }
        }
        getModel().setRefreshFilterItems(refreshableFilers);

        onRefresh();
    }


    public CourseFilter getCourseFilter() {
        HashSet<String> labels = new HashSet<>(Arrays.asList(getModel().getRefreshFilter()));
        CourseFilter c = CourseFilter.build(labels);
        getModel().setRefreshFilter(labels.toArray(new String[labels.size()]));
        return c;
    }


    public void onRefresh() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        int periodId = getPeriodId();
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        List<SelectItem> allValidFilters = new ArrayList<>();
        allValidFilters.add(FacesUtils.createSelectItem("assigned", "Modules Affectés", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("non-assigned", "Modules Non Affectés", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("intended", "Modules Demandés", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("non-intended", "Modules Non Demandés", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("conflict", "Modules En Conflits", "vr-checkbox"));
        for (AcademicSemester s : a.findSemesters()) {
            allValidFilters.add(FacesUtils.createSelectItem("semester:" + s.getId(), s.getName(), "vr-checkbox"));
        }
        for (AcademicCourseType s : a.findCourseTypes()) {
            allValidFilters.add(FacesUtils.createSelectItem("courseType:" + s.getId(), s.getName(), "vr-checkbox"));
        }
        for (AcademicClass s : a.findAcademicClasses()) {
            allValidFilters.add(FacesUtils.createSelectItem("class:" + s.getId(), s.getName(), "vr-checkbox"));
        }
        getModel().setFilterSelectItems(allValidFilters.toArray(new SelectItem[allValidFilters.size()]));

        AcademicTeacher t = getCurrentTeacher();
        getModel().setCurrentTeacher(t);
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
        CourseFilter courseFilter = getCourseFilter();
        for (AcademicCourseAssignmentInfo b : a.findCourseAssignmentsAndIntents(periodId, null, null, courseFilter, cache)) {
            all.put(b.getAssignment().getId(), b);
        }
        getModel().setAll(all);
        HashSet<Integer> visited = new HashSet<Integer>();
        if (t != null) {
            getModel().setMineS1(a.findCourseAssignmentsAndIntents(periodId, t.getId(), "S1", courseFilter, cache));
            getModel().setMineS2(a.findCourseAssignmentsAndIntents(periodId, t.getId(), "S2", courseFilter, cache));
            List<AcademicCourseAssignment> mine = new ArrayList<>();
            for (AcademicCourseAssignmentInfo m : getModel().getMineS1()) {
                mine.add(m.getAssignment());
                visited.add(m.getAssignment().getId());
            }
            for (AcademicCourseAssignmentInfo m : getModel().getMineS2()) {
                mine.add(m.getAssignment());
                visited.add(m.getAssignment().getId());
            }
            getModel().setStat(a.evalTeacherStat(periodId, t.getId(), null, null, mine, courseFilter.copy().setIncludeIntents(false), cache));
        }

        List<AcademicCourseAssignmentInfo> others = new ArrayList<>();
        for (AcademicCourseAssignmentInfo c : a.findCourseAssignmentsAndIntents(periodId, null, null, courseFilter, cache)) {
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
                    if (!semesterFilter.contains(c.getAssignment().getCoursePlan().getCourseLevel().getSemester().getId())) {
                        accepted = false;
                    }
                }
                if (accepted && classFilter.size() > 0) {
                    if (!classFilter.contains(c.getAssignment().getCoursePlan().getCourseLevel().getAcademicClass().getId())) {
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

    public void assignmentsToIntentsAll() {
        if (getModel().isEnableLoadEditing()) {
            ArrayList<AcademicCourseAssignmentInfo> assignmentInfos = new ArrayList<>();
            assignmentInfos.addAll(getModel().getMineS1());
            assignmentInfos.addAll(getModel().getMineS2());
            assignmentInfos.addAll(getModel().getOthers());
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            for (AcademicCourseAssignmentInfo aa : assignmentInfos) {
                AcademicTeacher t = aa.getAssignment().getTeacher();
                if (t != null) {
                    a.addIntent(t.getId(), aa.getAssignment().getId());
                    a.removeTeacherAcademicCourseAssignment(aa.getAssignment().getId());
                }
            }
        }
    }

    public void assignmentsToIntentsMine() {
        ArrayList<AcademicCourseAssignmentInfo> a = new ArrayList<>();
        a.addAll(getModel().getMineS1());
        a.addAll(getModel().getMineS2());
        for (AcademicCourseAssignmentInfo aa : a) {
            addToMine(aa.getAssignment().getId());
            doUnAssign(aa.getAssignment().getId());
        }
    }

    public void addToMine(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.addIntent(t.getId(), assignementId);
        }
        onRefresh();
    }

    public void removeFromMine(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
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
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.removeTeacherAcademicCourseAssignment(assignementId);
        }
        onRefresh();
    }

    public void doAssign(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
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

    public AppDepartment getUserDepartment() {
        //enableLoadEditing
        UserSession userSession = UserSession.getCurrentSession();
        if (userSession == null) {
            return null;
        }
        AppUser user = userSession.getUser();
        if (user == null) {
            return null;
        }
        return user.getDepartment();
    }

    public boolean isAllowedUpdateMineIntents(Integer assignementId) {
        UserSession userSession = UserSession.getCurrentSession();
        if (userSession == null) {
            return false;
        }
        AppUser user = userSession.getUser();
        if (user == null) {
            return false;
        }
        if (userSession.isSuperAdmin()) {
            return true;
        }

        AppPeriod period = VrApp.getBean(CorePlugin.class).findPeriod(getPeriodId());
        if (period == null || period.isReadOnly()) {
            return false;
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
                d = (t.getCoursePlan() != null && t.getCoursePlan().getCourseLevel().getAcademicClass() != null
                        && t.getCoursePlan().getCourseLevel().getAcademicClass().getProgram() != null) ?
                        t.getCoursePlan().getCourseLevel().getAcademicClass().getProgram().getDepartment() : null;
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
        UserSession userSession = UserSession.getCurrentSession();
        if (userSession == null) {
            return false;
        }
        AppUser user = userSession.getUser();
        if (user == null) {
            return false;
        }
        AppPeriod period = VrApp.getBean(CorePlugin.class).findPeriod(getPeriodId());

        if (userSession.isSuperAdmin()) {
            return true;
        }

        if (period == null || period.isReadOnly()) {
            return false;
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
                d = (t.getCoursePlan() != null && t.getCoursePlan().getCourseLevel().getAcademicClass() != null && t.getCoursePlan().getCourseLevel().getAcademicClass().getProgram() != null) ?
                        t.getCoursePlan().getCourseLevel().getAcademicClass().getProgram().getDepartment() : null;
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
        TeacherPeriodStat stat;
        boolean nonIntentedOnly = false;
        boolean enableLoadEditing = false;
        AcademicCourseAssignmentInfo selectedFromOthers = null;
        AcademicCourseAssignmentInfo selectedFromMine1 = null;
        AcademicCourseAssignmentInfo selectedFromMine2 = null;
        boolean myDisciplineOnly = true;
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "c", "td", "tp", "pm"};
        String[] othersFilters = defaultFilters;
        SelectItem[] filterSelectItems = new SelectItem[0];
        List<SelectItem> periods = new ArrayList<>();
        String selectedPeriod = null;
        String[] refreshFilter = {"intents"};
        List<SelectItem> refreshFilterItems;

        AcademicTeacher currentTeacher;

        public AcademicTeacher getCurrentTeacher() {
            return currentTeacher;
        }

        public void setCurrentTeacher(AcademicTeacher currentTeacher) {
            this.currentTeacher = currentTeacher;
        }

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

        public TeacherPeriodStat getStat() {
            return stat;
        }

        public void setStat(TeacherPeriodStat stat) {
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

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
        }

        public String getSelectedPeriod() {
            return selectedPeriod;
        }

        public void setSelectedPeriod(String selectedPeriod) {
            this.selectedPeriod = selectedPeriod;
        }

        public boolean isEnableLoadEditing() {
            return enableLoadEditing;
        }

        public void setEnableLoadEditing(boolean enableLoadEditing) {
            this.enableLoadEditing = enableLoadEditing;
        }

        public String[] getRefreshFilter() {
            return refreshFilter;
        }

        public void setRefreshFilter(String[] refreshFilter) {
            this.refreshFilter = refreshFilter;
        }

        public List<SelectItem> getRefreshFilterItems() {
            return refreshFilterItems;
        }

        public void setRefreshFilterItems(List<SelectItem> refreshFilterItems) {
            this.refreshFilterItems = refreshFilterItems;
        }
    }
}
