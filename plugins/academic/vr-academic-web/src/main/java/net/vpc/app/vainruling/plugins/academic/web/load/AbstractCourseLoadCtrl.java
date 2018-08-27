/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.plugins.academic.service.dto.TeacherPeriodStatExt;
import net.vpc.app.vainruling.plugins.academic.service.dto.TeacherSemesterStatExt;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.obj.ObjConfig;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherPeriodStat;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.util.Chronometer;

import javax.faces.model.SelectItem;
import java.util.*;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.plugins.academic.service.dto.TeacherLoadInfoFilter;
import net.vpc.app.vainruling.plugins.academic.service.dto.TeacherLoadInfo;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class AbstractCourseLoadCtrl {

    protected Model model = new Model();
    protected TeacherLoadFilterComponent teacherFilter = new TeacherLoadFilterComponent();
    protected CourseLoadFilterComponent courseFilter = new CourseLoadFilterComponent();
    protected CourseLoadFilterComponent othersCourseFilter = new CourseLoadFilterComponent();

    public AbstractCourseLoadCtrl() {
    }

    public TeacherLoadFilterComponent getTeacherFilter() {
        return teacherFilter;
    }

    public CourseLoadFilterComponent getCourseFilter() {
        return courseFilter;
    }

    public CourseLoadFilterComponent getOthersCourseFilter() {
        return othersCourseFilter;
    }

    private void reset() {
//        getModel().setMineS1(new ArrayList<AcademicCourseAssignmentInfo>());
//        getModel().setMineS2(new ArrayList<AcademicCourseAssignmentInfo>());
        getModel().setOthers(new ArrayList<AcademicCourseAssignmentInfoByVisitor>());
        getModel().setAll(new HashMap<Integer, AcademicCourseAssignmentInfoByVisitor>());
        TeacherPeriodStat teacherStat = new TeacherPeriodStat();
        teacherStat.setTeacher(new AcademicTeacher());

        getModel().setStat(new TeacherPeriodStatExt(teacherStat, getModel().getAll()));
    }

    public abstract AcademicTeacher getCurrentTeacher();

    public void onRefreshFiltersChanged() {
        onRefresh();
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        List<AppPeriod> navigatablePeriods = core.findNavigatablePeriods();
//        AppPeriod mainPeriod = core.getCurrentPeriod();
        onInit();
        onChangePeriod();
    }

    public void onInit() {
        getTeacherFilter().onInit();
        getCourseFilter().onInit();
        getOthersCourseFilter().onInit();
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
            ObjConfig c = new ObjConfig();
            c.entity = "AcademicCourseAssignment";
            c.id = String.valueOf(a.getId());
            return Vr.get().gotoPage("obj", VrUtils.formatJSONObject(c));
        }
        return null;
    }

    public void onChangePeriod() {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AppDepartment userDepartment = getUserDepartment();
        int periodId = getTeacherFilter().getPeriodId();

        getModel().setEnableLoadEditing(
                (userDepartment == null || periodId < 0) ? false
                        : ap.getAppDepartmentPeriodRecord(periodId, userDepartment.getId()).getBoolean("enableLoadEditing", false)
        );

        getTeacherFilter().onChangePeriod();
        getCourseFilter().onChangePeriod();

        List<SelectItem> refreshableFilters = new ArrayList<>();
        refreshableFilters.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
        refreshableFilters.add(FacesUtils.createSelectItem("deviation-week", "Balance/Sem", "vr-checkbox"));
        refreshableFilters.add(FacesUtils.createSelectItem("deviation-extra", "Balance/Supp", "vr-checkbox"));
        refreshableFilters.add(FacesUtils.createSelectItem("extra-abs", "Supp ABS", "vr-checkbox"));
        getCourseFilter().getModel().setRefreshFilterItems(refreshableFilters);

        refreshableFilters = new ArrayList<>();
        refreshableFilters.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
        getOthersCourseFilter().getModel().setRefreshFilterItems(refreshableFilters);

        getOthersCourseFilter().onChangePeriod();
        onChangeOther();
    }

    public void onDoNothing() {
        System.out.println("....");
    }

    public void onChangeOther() {
        onRefresh();
    }

    public void onChangeMultipleSelection() {
        //do nothing
    }

    public void onRefresh() {
        Chronometer chronometer = new Chronometer();
        int periodId = getTeacherFilter().getPeriodId();
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        List<SelectItem> allCurrFilters = new ArrayList<>();
        allCurrFilters.add(FacesUtils.createSelectItem("collaborators", "Collaborateurs", "vr-checkbox"));
        allCurrFilters.add(FacesUtils.createSelectItem("rooms", "Salles", "vr-checkbox"));
        getModel().setCurrentTeacherFiltersSelectItems(allCurrFilters);
        List<SelectItem> allOtherFilters = new ArrayList<>();
        allOtherFilters.add(FacesUtils.createSelectItem("assigned", "Modules Affectés", "vr-checkbox"));
        allOtherFilters.add(FacesUtils.createSelectItem("non-assigned", "Modules Non Affectés", "vr-checkbox"));
        allOtherFilters.add(FacesUtils.createSelectItem("intended", "Modules Demandés", "vr-checkbox"));
        allOtherFilters.add(FacesUtils.createSelectItem("non-intended", "Modules Non Demandés", "vr-checkbox"));
        allOtherFilters.add(FacesUtils.createSelectItem("conflict", "Modules En Conflits", "vr-checkbox"));
        allOtherFilters.add(FacesUtils.createSelectItem("collaborators", "Collaborateurs", "vr-checkbox"));
        allOtherFilters.add(FacesUtils.createSelectItem("rooms", "Salles", "vr-checkbox"));
        allOtherFilters.add(FacesUtils.createSelectItem("locked", "Modules vérouillés", "vr-checkbox"));
        allOtherFilters.add(FacesUtils.createSelectItem("unlocked", "Modules non vérouillés", "vr-checkbox"));

        getModel().setOthersFiltersSelectItems(allOtherFilters);

        AcademicTeacher teacher = getCurrentTeacher();
        getModel().setCurrentTeacher(teacher);
        reset();

        TeacherLoadInfoFilter ff = new TeacherLoadInfoFilter();
        ff.setOtherCourseAssignmentFilter(getOthersCourseFilter().getCourseAssignmentFilter());
        ff.setTeacherCourseAssignmentFilter(getCourseFilter().getCourseAssignmentFilter());
        ff.setDeviationConfig(getCourseFilter().getDeviationConfig());
        ff.setTeacherId(teacher == null ? -1 : teacher.getId());
        ff.setPeriodId(periodId);
        ff.setFilterConflict(isFiltered("conflict"));
        ff.setFilterAssigned(isFiltered("assigned"));
        ff.setFilterNonAssigned(isFiltered("non-assigned"));
        ff.setFilterIntended(isFiltered("intended"));
        ff.setFilterNonIntended(isFiltered("non-intended"));
        ff.setFilterLocked(isFiltered("locked"));
        ff.setFilterUnlocked(isFiltered("unlocked"));
        TeacherLoadInfo result = a.getTeacherLoadInfo(ff);
        getModel().setAll(result.getAll());
        getModel().setStat(result.getStat());
        getModel().setNonFilteredOthers(result.getNonFilteredOthers());
        applyOthersTextFilter();
        chronometer.stop();
        System.out.println(chronometer);
    }

    public void applyOthersTextFilter() {
        getModel().setOthers(new TextSearchFilter(getModel().getOthersTextFilter(),
                new ObjectToMapConverter() {
            @Override
            public Map<String, Object> convert(Object o) {
                Map<String, Object> m = new HashMap();
//                        m.putAll(DefaultObjectToMapConverter.INSTANCE.convert(o));
                AcademicCourseAssignmentInfoByVisitor sa = (AcademicCourseAssignmentInfoByVisitor) o;
                m.putAll(DefaultObjectToMapConverter.INSTANCE.convert(sa.getValue().getAssignment()));
                int keyIndex = 1;
                for (TeacherAssignmentChunck chunck : sa.getValue().getAssignmentChunck().getChuncks().values()) {
                    m.put("key" + keyIndex, chunck.getTeacherName());
                    keyIndex++;
                }
                for (TeacherAssignmentChunck chunck : sa.getValue().getCourseChunck().getChuncks().values()) {
                    m.put("key" + keyIndex, chunck.getTeacherName());
                    keyIndex++;
                }
                return m;
            }
        }
        ).filterList(getModel().getNonFilteredOthers()));
    }

    public void assignmentsToIntentsAll() {
        if (getModel().isEnableLoadEditing()) {
            ArrayList<AcademicCourseAssignmentInfoByVisitor> assignmentInfos = new ArrayList<>();
            assignmentInfos.addAll(getModel().getStat().getAssignments());
            assignmentInfos.addAll(getModel().getOthers());
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            for (AcademicCourseAssignmentInfoByVisitor aa : assignmentInfos) {
                AcademicCourseAssignment assignment = aa.getValue().getAssignment();
                AcademicTeacher t = assignment.getTeacher();
                if (t != null) {
                    if (isAllowedUpdateMineIntents(assignment.getId())) {
                        a.addIntent(t.getId(), assignment.getId());
                        a.removeCourseAssignment(assignment.getId(), false, false);
                    }
                }
            }
        }
    }

    public void intentsToAssignmentsAll() {
        if (getModel().isEnableLoadEditing()) {
            ArrayList<AcademicCourseAssignmentInfoByVisitor> assignmentInfos = new ArrayList<>();
            assignmentInfos.addAll(getModel().getStat().getAssignments());
            assignmentInfos.addAll(getModel().getOthers());
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            for (AcademicCourseAssignmentInfoByVisitor aa : assignmentInfos) {
                AcademicTeacher t = aa.getAssignment().getTeacher();
                if (t == null) {
                    for (Integer uid : aa.getAssignmentChunck().getChuncks().keySet()) {
                        a.addCourseAssignment(uid, aa.getAssignment().getId());
                        break;
                    }
                }
            }
        }
    }

    public void assignmentsToIntentsMine() {
        for (AcademicCourseAssignmentInfoByVisitor aa : getModel().getStat().getAssignments()) {
            addToMine(aa.getValue().getAssignment().getId());
            doUnAssign(aa.getValue().getAssignment().getId());
        }
    }

    public void intentsToAssignmentsMine() {
        for (AcademicCourseAssignmentInfoByVisitor aa : getModel().getStat().getAssignments()) {
            doAssign(aa.getValue().getAssignment().getId());
        }
    }

    public void removeAllIntentsMine() {
        for (AcademicCourseAssignmentInfoByVisitor aa : getModel().getStat().getAssignments()) {
            removeFromMine(aa.getValue().getAssignment().getId());
        }
    }

    public void removeAllAssignmentsMine() {
        for (AcademicCourseAssignmentInfoByVisitor aa : getModel().getStat().getAssignments()) {
            doUnAssign(aa.getValue().getAssignment().getId());
        }
    }

    public void addToMineOtherSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (AcademicCourseAssignmentInfoByVisitor s : getModel().getOthers()) {
                if (s.isSelected()) {
                    int assignementId = s.getValue().getAssignment().getId();
                    a.addIntent(t.getId(), assignementId);
                }
            }
            onRefresh();
        }
    }

    public void addProposalToMineOtherSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (AcademicCourseAssignmentInfoByVisitor s : getModel().getOthers()) {
                if (s.isSelected()) {
                    int assignementId = s.getValue().getAssignment().getId();
                    a.addProposal(t.getId(), assignementId);
                }
            }
            onRefresh();
        }
    }

    public void addToMineCurrSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (TeacherSemesterStatExt teacherSemesterStatExt : getModel().getStat().getSemesters()) {
                for (AcademicCourseAssignmentInfoByVisitor s : teacherSemesterStatExt.getAssignments()) {
                    if (s.isSelected()) {
                        int assignementId = s.getValue().getAssignment().getId();
                        a.addIntent(t.getId(), assignementId);
                    }
                }
            }
            onRefresh();
        }
    }

    public void addProposalToMineCurrSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (TeacherSemesterStatExt teacherSemesterStatExt : getModel().getStat().getSemesters()) {
                for (AcademicCourseAssignmentInfoByVisitor s : teacherSemesterStatExt.getAssignments()) {
                    if (s.isSelected()) {
                        int assignementId = s.getValue().getAssignment().getId();
                        a.addProposal(t.getId(), assignementId);
                    }
                }
            }
            onRefresh();
        }
    }

    public void addWishToMineCurrSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (TeacherSemesterStatExt teacherSemesterStatExt : getModel().getStat().getSemesters()) {
                for (AcademicCourseAssignmentInfoByVisitor s : teacherSemesterStatExt.getAssignments()) {
                    if (s.isSelected()) {
                        int assignementId = s.getValue().getAssignment().getId();
                        a.addWish(t.getId(), assignementId);
                    }
                }
            }
            onRefresh();
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

    public void addWishToMine(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.addWish(t.getId(), assignementId);
        }
        onRefresh();
    }

    public void removeFromMineCurrSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (TeacherSemesterStatExt teacherSemesterStatExt : getModel().getStat().getSemesters()) {
                for (AcademicCourseAssignmentInfoByVisitor s : teacherSemesterStatExt.getAssignments()) {
                    if (s.isSelected()) {
                        int assignementId = s.getValue().getAssignment().getId();
                        a.removeIntent(t.getId(), assignementId);
                    }
                }
            }
            onRefresh();
        }
    }

    public void removeProposalFromMineCurrSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (TeacherSemesterStatExt teacherSemesterStatExt : getModel().getStat().getSemesters()) {
                for (AcademicCourseAssignmentInfoByVisitor s : teacherSemesterStatExt.getAssignments()) {
                    if (s.isSelected()) {
                        int assignementId = s.getValue().getAssignment().getId();
                        a.removeProposal(t.getId(), assignementId);
                    }
                }
            }
            onRefresh();
        }
    }

    public void removeWishFromMineCurrSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (TeacherSemesterStatExt teacherSemesterStatExt : getModel().getStat().getSemesters()) {
                for (AcademicCourseAssignmentInfoByVisitor s : teacherSemesterStatExt.getAssignments()) {
                    if (s.isSelected()) {
                        int assignementId = s.getValue().getAssignment().getId();
                        a.removeWish(t.getId(), assignementId);
                    }
                }
            }
            onRefresh();
        }
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

    public void removeAllIntentsSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
            if (s.isSelected()) {
                a.removeAllIntents(s.getValue().getAssignment().getId());
            }
        }
        onRefresh();
    }

    public void removeAllProposalsSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
            if (s.isSelected()) {
                a.removeAllProposal(s.getValue().getAssignment().getId());
            }
        }
        onRefresh();
    }

    public void removeAllWishesSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
            if (s.isSelected()) {
                a.removeAllWishes(s.getValue().getAssignment().getId());
            }
        }
        onRefresh();
    }

    public void doDeleteAssignment(Integer assignementId) {
        if (CorePlugin.get().isCurrentSessionAdminOrManager()) {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            a.removeCourseAssignment(assignementId, true, false);
        }
    }

    public void doUnAssign(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.removeCourseAssignment(assignementId, false, true);
        }
        onRefresh();
    }

    public void doAssign(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.addCourseAssignment(t.getId(), assignementId);
        }
        onRefresh();
    }

    public void doAssignSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
                if (s.isSelected()) {
                    int assignementId = s.getValue().getAssignment().getId();
                    a.addCourseAssignment(t.getId(), assignementId);
                }
            }
        }
        onRefresh();
    }

    public void doSwitchLockAssignment(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        if (CorePlugin.get().isCurrentSessionAdminOrManager()) {
            if (assignementId != null) {
                AcademicCourseAssignment assignment = a.findCourseAssignment(assignementId);
                if (assignment != null) {
                    assignment.setLocked(!assignment.isLocked());
                    a.updateCourseAssignment(assignment);
                }
            }
            onRefresh();
        }
    }

    public void doSwitchLockAssignmentSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        if (CorePlugin.get().isCurrentSessionAdminOrManager()) {
            for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
                if (s.isSelected()) {
                    AcademicCourseAssignment assignment = s.getValue().getAssignment();
                    assignment.setLocked(!assignment.isLocked());
                    a.updateCourseAssignment(assignment);
                }
            }
            onRefresh();
        }
    }

    public void doUnAssignSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
//        AcademicTeacher t = getModel().getCurrentTeacher();
//        if (t != null) {
        for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
            if (s.isSelected()) {
                int assignementId = s.getValue().getAssignment().getId();
                a.removeCourseAssignment(assignementId, false, true);
            }
        }
//        }
        onRefresh();
    }

    public void doDeleteSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        if (CorePlugin.get().isCurrentSessionAdminOrManager()) {
//        AcademicTeacher t = getModel().getCurrentTeacher();
//        if (t != null) {
            for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
                if (s.isSelected()) {
                    int assignementId = s.getValue().getAssignment().getId();
                    a.removeCourseAssignment(assignementId, true, false);
                }
            }
//        }
            onRefresh();
        }
    }

    public Model getModel() {
        return model;
    }

    public void onCurrentTeacherFiltersChanged() {
        onRefresh();
    }

    public void onOthersFiltersChanged() {
        onRefresh();
    }

    public AppDepartment getUserDepartment() {
        //enableLoadEditing
        AppUser user = VrApp.getBean(CorePlugin.class).getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getDepartment();
    }

    public boolean isAllowedUpdateMineIntents(Integer assignmentId) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        Integer userId = core.getCurrentUserId();
        if (userId == null) {
            return false;
        }
        if (core.isCurrentSessionAdmin()) {
            return true;
        }

        AppPeriod period = core.findPeriod(getTeacherFilter().getPeriodId());
        if (period == null || period.isReadOnly()) {
            return false;
        }
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher teacher = a.findTeacherByUser(userId);
        if (teacher == null) {
            return false;
        }
        if (assignmentId != null) {
            AcademicCourseAssignmentInfoByVisitor t0 = getModel().getAll().get(assignmentId);
            AcademicCourseAssignment t = t0 == null ? null : t0.getValue().getAssignment();
            if (t != null) {
                AppUser user = core.findUser(userId);
                if (user != null) {
                    AppDepartment d = t.getOwnerDepartment();
                    if (d != null) {
                        if (core.isCurrentAllowed(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_INTENTS)) {
                            AppDepartment d2 = user.getDepartment();
                            if (d2 != null && d2.getId() == d.getId()) {
                                return true;
                            }
                        }
                    }
                    d = t.resolveDepartment();
                    if (d != null) {
                        if (core.isCurrentAllowed(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_INTENTS)) {
                            AppDepartment d2 = user.getDepartment();
                            if (d2 != null && d2.getId() == d.getId()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isAllowedUpdateMineAssignments(Integer assignementId) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        Integer userId = core.getCurrentUserId();
        if (userId == null) {
            return false;
        }
        AppPeriod period = core.findPeriod(getTeacherFilter().getPeriodId());

        if (core.isCurrentSessionAdmin()) {
            return true;
        }

        if (period == null || period.isReadOnly()) {
            return false;
        }

        if (assignementId != null) {
            AcademicCourseAssignmentInfoByVisitor t0 = getModel().getAll().get(assignementId);
            AcademicCourseAssignment t = t0 == null ? null : t0.getValue().getAssignment();
            AppUser u = core.findUser(userId);
            if (u != null && t != null) {
                AppDepartment d = t.getOwnerDepartment();
                if (d != null) {
                    if (core.isCurrentAllowed(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_ASSIGNMENTS)) {
                        AppDepartment d2 = u.getDepartment();
                        if (d2 != null && d2.getId() == d.getId()) {
                            return true;
                        }
                    }
                }
                d = t.resolveDepartment();
                if (d != null) {
                    if (core.isCurrentAllowed(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_ASSIGNMENTS)) {
                        AppDepartment d2 = u.getDepartment();
                        if (d2 != null && d2.getId() == d.getId()) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    public void doAssignByIntentSelected() {
        for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
            if (s.isSelected()) {
                int assignementId = s.getValue().getAssignment().getId();
                AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);

                AcademicCourseAssignmentInfoByVisitor rr = getModel().getAll().get(assignementId);
                if (rr != null) {
                    final Set<Integer> s0 = rr.getValue().getAssignmentChunck().getChuncks().keySet();
                    if (s0.size() > 0) {
                        List<Integer> selId = new ArrayList<>(s0);
                        AcademicTeacher oldTeacher = rr.getValue().getAssignment().getTeacher();
                        int newTeacherId = -1;
                        if (oldTeacher == null) {
                            newTeacherId = selId.get(0);
                        } else {
                            int lastPos = selId.indexOf(oldTeacher.getId());
                            if (lastPos < 0) {
                                lastPos = 0;
                            } else {
                                lastPos = (lastPos + 1) % selId.size();
                            }
                            newTeacherId = selId.get(lastPos);
                        }
                        a.addCourseAssignment(newTeacherId, assignementId);
                    }
                    onRefresh();
                }
            }
        }
    }

    public void doAssignByIntent(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        if (assignementId != null) {
            AcademicCourseAssignmentInfoByVisitor rr = getModel().getAll().get(assignementId);
            if (rr != null) {
                final Set<Integer> s0 = rr.getValue().getAssignmentChunck().getChuncks().keySet();
                if (s0.size() > 0) {
                    List<Integer> s = new ArrayList<>(s0);
                    AcademicTeacher oldTeacher = rr.getValue().getAssignment().getTeacher();
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
                    a.addCourseAssignment(newTeacherId, assignementId);
                }
                onRefresh();
            }
        }
    }

    public static String evalIntentsString(AcademicCourseAssignmentInfo a, int visitorTeacherId) {
        return a.getAssignmentChunck().toStringByTeacher(visitorTeacherId);
//        AcademicPlugin academicPlugin = AcademicPlugin.get();
//        AcademicTeacher visitorTeacher = visitorTeacherId >= 0 ? academicPlugin.findTeacher(visitorTeacherId) : null;
//        StringBuilder sb = new StringBuilder();
//        AcademicTeacher assignmentTeacher = a.getAssignment().getTeacher();
//        if (assignmentTeacher != null) {
//            AcademicTeacher t = assignmentTeacher;
//            if (visitorTeacherId != t.getId()) {
//                String name = academicPlugin.getValidName(t);
//                sb.append(name).append(" (*)");
//            }
//        }
//        for (String i : a.getIntentsSet()) {
//            if ((
//                    assignmentTeacher != null
//                            && assignmentTeacher.getContact() != null
//                            && i.equals(assignmentTeacher.resolveFullName())
//            )
//                    ||
//                    (
//                            visitorTeacher != null
//                                    && visitorTeacher.getContact() != null
//                                    && i.equals(visitorTeacher.resolveFullName())
//                    )) {
//                //ignore
//            } else {
//                if (sb.length() > 0) {
//                    sb.append(", ");
//                }
//                sb.append(i);
//            }
//        }
//
//        return sb.toString();
    }

    public void onSwitchDisplayOtherModules() {
        getModel().setDisplayOtherModules(!getModel().isDisplayOtherModules());
    }

    public static class Model {

        //        List<AcademicCourseAssignmentInfo> mineS1 = new ArrayList<>();
//        List<AcademicCourseAssignmentInfo> mineS2 = new ArrayList<>();
        List<AcademicCourseAssignmentInfoByVisitor> others = new ArrayList<>();
        List<AcademicCourseAssignmentInfoByVisitor> nonFilteredOthers = new ArrayList<>();
        Map<Integer, AcademicCourseAssignmentInfoByVisitor> all = new HashMap<>();
        TeacherPeriodStatExt stat;
        boolean nonIntentedOnly = false;
        boolean multipleSelection = true;
        boolean enableLoadEditing = false;
        boolean displayOtherModules = false;
        AcademicCourseAssignmentInfo selectedFromOthers = null;
        AcademicCourseAssignmentInfo selectedFromMine1 = null;
        AcademicCourseAssignmentInfo selectedFromMine2 = null;
        boolean myDisciplineOnly = true;
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "c", "td", "tp", "pm"};
        String[] othersFilters = defaultFilters;
        String[] currentTeacherFilters = defaultFilters;
        List<SelectItem> filterSelectItems = new ArrayList<>();
        List<SelectItem> otherFilterSelectItems = new ArrayList<>();
        String othersTextFilter;

        AcademicTeacher currentTeacher;

        public String[] getCurrentTeacherFilters() {
            return currentTeacherFilters;
        }

        public void setCurrentTeacherFilters(String[] currentTeacherFilters) {
            this.currentTeacherFilters = currentTeacherFilters;
        }

        public AcademicTeacher getCurrentTeacher() {
            return currentTeacher;
        }

        public void setCurrentTeacher(AcademicTeacher currentTeacher) {
            this.currentTeacher = currentTeacher;
        }

        public boolean isOthersFilterSelected(String value) {
            return Arrays.asList(getOthersFilters()).indexOf(value) >= 0;
        }

        public boolean isCurrentTeacherFilter(String value) {
            return Arrays.asList(getCurrentTeacherFilters()).indexOf(value) >= 0;
        }

        public boolean isMultipleSelection() {
            return multipleSelection;
        }

        public void setMultipleSelection(boolean multipleSelection) {
            this.multipleSelection = multipleSelection;
        }

        public List<SelectItem> getOthersFiltersSelectItems() {
            return otherFilterSelectItems;
        }

        public void setOthersFiltersSelectItems(List<SelectItem> othersFiltersSelectItems) {
            this.otherFilterSelectItems = othersFiltersSelectItems;
        }
//        public List<AcademicCourseAssignmentInfo> getMineS1() {
//            return mineS1;
//        }
//
//        public void setMineS1(List<AcademicCourseAssignmentInfo> mineS1) {
//            this.mineS1 = mineS1;
//        }
//
//        public List<AcademicCourseAssignmentInfo> getMineS2() {
//            return mineS2;
//        }
//
//        public void setMineS2(List<AcademicCourseAssignmentInfo> mineS2) {
//            this.mineS2 = mineS2;
//        }

        public Map<Integer, AcademicCourseAssignmentInfoByVisitor> getAll() {
            return all;
        }

        public void setAll(Map<Integer, AcademicCourseAssignmentInfoByVisitor> all) {
            this.all = all;
        }

        public List<AcademicCourseAssignmentInfoByVisitor> getNonFilteredOthers() {
            return nonFilteredOthers;
        }

        public Model setNonFilteredOthers(List<AcademicCourseAssignmentInfoByVisitor> nonFilteredOthers) {
            this.nonFilteredOthers = nonFilteredOthers;
            return this;
        }

        public double getOthersSumC() {
            double v = 0;
            for (AcademicCourseAssignmentInfoByVisitor other : others) {
                v += other.getValue().getAssignment().getValueC();
            }
            return v;
        }

        public double getOthersSumTD() {
            double v = 0;
            for (AcademicCourseAssignmentInfoByVisitor other : others) {
                v += other.getValue().getAssignment().getValueTD();
            }
            return v;
        }

        public double getOthersSumTP() {
            double v = 0;
            for (AcademicCourseAssignmentInfoByVisitor other : others) {
                v += other.getValue().getAssignment().getValueTP();
            }
            return v;
        }

        public double getOthersSumPM() {
            double v = 0;
            for (AcademicCourseAssignmentInfoByVisitor other : others) {
                v += other.getValue().getAssignment().getValuePM();
            }
            return v;
        }

        public List<AcademicCourseAssignmentInfoByVisitor> getOthers() {
            return others;
        }

        public void setOthers(List<AcademicCourseAssignmentInfoByVisitor> others) {
            this.others = others;
        }

        public TeacherPeriodStatExt getStat() {
            return stat;
        }

        public void setStat(TeacherPeriodStatExt stat) {
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

        public List<SelectItem> getCurrentTeacherFiltersSelectItems() {
            return filterSelectItems;
        }

        public void setCurrentTeacherFiltersSelectItems(List<SelectItem> currentTeacherFiltersSelectItems) {
            this.filterSelectItems = currentTeacherFiltersSelectItems;
        }

        public boolean isEnableLoadEditing() {
            return enableLoadEditing;
        }

        public void setEnableLoadEditing(boolean enableLoadEditing) {
            this.enableLoadEditing = enableLoadEditing;
        }

        public boolean isDisplayOtherModules() {
            return displayOtherModules;
        }

        public void setDisplayOtherModules(boolean displayOtherModules) {
            this.displayOtherModules = displayOtherModules;
        }

        public String getOthersTextFilter() {
            return othersTextFilter;
        }

        public void setOthersTextFilter(String othersTextFilter) {
            this.othersTextFilter = othersTextFilter;
        }
    }
}
