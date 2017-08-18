/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import net.vpc.app.vainruling.core.web.obj.ObjCtrl;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.stat.DeviationConfig;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherPeriodStat;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherSemesterStat;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.util.Chronometer;
import org.apache.commons.collections.map.HashedMap;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class AbstractCourseLoadCtrl {

    protected Model model = new Model();
    protected TeacherLoadFilterComponent teacherFilter = new TeacherLoadFilterComponent();
    protected CourseLoadFilterComponent courseFilter = new CourseLoadFilterComponent();

    public AbstractCourseLoadCtrl() {
    }

    public TeacherLoadFilterComponent getTeacherFilter() {
        return teacherFilter;
    }

    public CourseLoadFilterComponent getCourseFilter() {
        return courseFilter;
    }

    private void reset() {
//        getModel().setMineS1(new ArrayList<AcademicCourseAssignmentInfo>());
//        getModel().setMineS2(new ArrayList<AcademicCourseAssignmentInfo>());
        getModel().setOthers(new ArrayList<SelectableAssignment>());
        getModel().setAll(new HashMap<Integer, SelectableAssignment>());
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

    public void onInit(){
        getTeacherFilter().onInit();
        getCourseFilter().onInit();
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
            return VrApp.getBean(VrMenuManager.class).gotoPage("obj", VrUtils.formatJSONObject(c));
        }
        return null;
    }


    public void onChangePeriod() {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AppDepartment userDepartment = getUserDepartment();
        int periodId = getTeacherFilter().getPeriodId();

        getModel().setEnableLoadEditing(
                (userDepartment == null || periodId < 0) ? false :
                        ap.getAppDepartmentPeriodRecord(periodId, userDepartment.getId()).getBoolean("enableLoadEditing", false)
        );

        getTeacherFilter().onChangePeriod();
        getCourseFilter().onChangePeriod();
        onChangeOther();
    }


    public void onDoNothing() {
        System.out.println("....");
    }
    public void onChangeOther() {
        onRefresh();
    }

    public void onRefresh() {
        Chronometer chronometer = new Chronometer();
        int periodId = getTeacherFilter().getPeriodId();
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        List<SelectItem> allValidFilters = new ArrayList<>();
        allValidFilters.add(FacesUtils.createSelectItem("assigned", "Modules Affectés", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("non-assigned", "Modules Non Affectés", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("intended", "Modules Demandés", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("non-intended", "Modules Non Demandés", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("conflict", "Modules En Conflits", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("multiple-selection", "Selection Multiple", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("collaborators", "Collaborateurs", "vr-checkbox"));
        allValidFilters.add(FacesUtils.createSelectItem("rooms", "Salles", "vr-checkbox"));
        getModel().setFilterSelectItems(allValidFilters);

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

        reset();

        Map<Integer, SelectableAssignment> all = new HashMap<>();
        DefaultCourseAssignmentFilter allCourseAssignmentFilter = getCourseFilter().getCourseAssignmentFilter();
        allCourseAssignmentFilter.setAcceptAssignments(true).setAcceptIntents(true).setAcceptNoTeacher(true);
        DeviationConfig deviationConfig = getCourseFilter().getDeviationConfig();
//        boolean includeIntents = !isFiltered("no-current-intents");
        List<AcademicCourseAssignmentInfo> allCourseAssignmentsAndIntents = a.findCourseAssignmentsAndIntents(periodId, null, allCourseAssignmentFilter);
        int id = t == null ? -1 : t.getId();
        for (AcademicCourseAssignmentInfo b : allCourseAssignmentsAndIntents) {
            all.put(b.getAssignment().getId(), new SelectableAssignment(b, id));
        }
        getModel().setAll(all);
        HashSet<Integer> visited = new HashSet<Integer>();
        if (t != null) {
            DefaultCourseAssignmentFilter courseAssignmentFilter = getCourseFilter().getCourseAssignmentFilter();
            TeacherPeriodStat stat = a.evalTeacherStat(periodId, id, courseAssignmentFilter, deviationConfig,null);
            for (TeacherSemesterStat teacherSemesterStat : stat.getSemesters()) {
                for (AcademicCourseAssignmentInfo m : teacherSemesterStat.getAssignments()) {
                    visited.add(m.getAssignment().getId());
                }
            }
            getModel().setStat(new TeacherPeriodStatExt(stat, all));
        }

        List<AcademicCourseAssignmentInfo> others = new ArrayList<>();
        for (AcademicCourseAssignmentInfo c : allCourseAssignmentsAndIntents) {
            if (!visited.contains(c.getAssignment().getId())) {
                boolean _assigned = c.isAssigned();
                Map<Integer, TeacherAssignmentChunck> chuncks = c.getAssignmentChunck().getChuncks();
                int chunk_size = chuncks.size();
                boolean _intended = chunk_size > 0;
                boolean accepted = true;
                if (((assigned && _assigned) || (nonassigned && !_assigned))
                        && ((intended && _intended) || (nonintended && !_intended))) {
                    //ok
                } else {
                    accepted = false;
                }
                if (accepted && conflict) {
                    //show only with conflicts
                    if (chuncks.isEmpty()) {
                        accepted = false;
                    } else if (c.getAssignment().getTeacher() != null) {
                        if(chunk_size > 1){
                            accepted=true;
                        }else if(chunk_size == 1){
                            TeacherAssignmentChunck first=(TeacherAssignmentChunck) chuncks.values().toArray()[0];
                            accepted=c.getAssignment().getTeacher().getId()!=first.getTeacherId();
                        }
                    } else {
                        accepted = chunk_size > 1;
                    }
                }
                if (accepted) {
                    UserSession userSession = UserSession.get();
                    boolean powerUser = userSession!=null && (userSession.isManagerOrAdmin());
                    if((c.getAssignment().isLocked() || c.getAssignment().getCoursePlan().isLocked()) && !powerUser){
                        //dont add
                    }else {
                        others.add(c);
                    }
                }
            }
        }

        getModel().setNonFilteredOthers(wrap(others, all));
        applyOthersTextFilter();
        chronometer.stop();
        System.out.println(chronometer);
    }

    public void applyOthersTextFilter(){
        getModel().setOthers(new TextSearchFilter(getModel().getOthersTextFilter(),
                new ObjectToMapConverter() {
                    @Override
                    public Map<String, Object> convert(Object o) {
                        Map<String, Object> m=new HashMap();
//                        m.putAll(DefaultObjectToMapConverter.INSTANCE.convert(o));
                        SelectableAssignment sa=(SelectableAssignment) o;
                        m.putAll(DefaultObjectToMapConverter.INSTANCE.convert(sa.getValue().getAssignment()));
                        int keyIndex=1;
                        for (TeacherAssignmentChunck chunck : sa.getValue().getAssignmentChunck().getChuncks().values()) {
                            m.put("key"+keyIndex,chunck.getTeacherName());
                            keyIndex++;
                        }
                        for (TeacherAssignmentChunck chunck : sa.getValue().getCourseChunck().getChuncks().values()) {
                            m.put("key"+keyIndex,chunck.getTeacherName());
                            keyIndex++;
                        }
                        return m;
                    }
                }
        ).filterList(getModel().getNonFilteredOthers()));
    }

    public void assignmentsToIntentsAll() {
        if (getModel().isEnableLoadEditing()) {
            ArrayList<SelectableAssignment> assignmentInfos = new ArrayList<>();
            assignmentInfos.addAll(getModel().getStat().getAssignments());
            assignmentInfos.addAll(getModel().getOthers());
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            for (SelectableAssignment aa : assignmentInfos) {
                AcademicCourseAssignment assignment = aa.getValue().getAssignment();
                AcademicTeacher t = assignment.getTeacher();
                if (t != null) {
                    if (isAllowedUpdateMineIntents(assignment.getId())) {
                        a.addIntent(t.getId(), assignment.getId());
                        a.removeCourseAssignment(assignment.getId(),false);
                    }
                }
            }
        }
    }

    public void intentsToAssignmentsAll() {
        if (getModel().isEnableLoadEditing()) {
            ArrayList<SelectableAssignment> assignmentInfos = new ArrayList<>();
            assignmentInfos.addAll(getModel().getStat().getAssignments());
            assignmentInfos.addAll(getModel().getOthers());
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            for (SelectableAssignment aa : assignmentInfos) {
                AcademicCourseAssignmentInfo assignement = aa.getValue();
                AcademicTeacher t = assignement.getAssignment().getTeacher();
                if (t == null) {
                    for (Integer uid : assignement.getAssignmentChunck().getChuncks().keySet()) {
                        a.addCourseAssignment(uid, assignement.getAssignment().getId());
                        break;
                    }
                }
            }
        }
    }

    public void assignmentsToIntentsMine() {
        for (SelectableAssignment aa : getModel().getStat().getAssignments()) {
            addToMine(aa.getValue().getAssignment().getId());
            doUnAssign(aa.getValue().getAssignment().getId());
        }
    }

    public void intentsToAssignmentsMine() {
        for (SelectableAssignment aa : getModel().getStat().getAssignments()) {
            doAssign(aa.getValue().getAssignment().getId());
        }
    }

    public void removeAllIntentsMine() {
        for (SelectableAssignment aa : getModel().getStat().getAssignments()) {
            removeFromMine(aa.getValue().getAssignment().getId());
        }
    }

    public void removeAllAssignmentsMine() {
        for (SelectableAssignment aa : getModel().getStat().getAssignments()) {
            doUnAssign(aa.getValue().getAssignment().getId());
        }
    }

    public void addToMineSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (SelectableAssignment s : getModel().getOthers()) {
                if (s.isSelected()) {
                    int assignementId = s.getValue().getAssignment().getId();
                    a.addIntent(t.getId(), assignementId);
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

    public void removeFromMineSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null) {
            for (TeacherSemesterStatExt teacherSemesterStatExt : getModel().getStat().getSemesters()) {
                for (SelectableAssignment s : teacherSemesterStatExt.getAssignments()) {
                    if (s.isSelected()) {
                        int assignementId = s.getValue().getAssignment().getId();
                        a.removeIntent(t.getId(), assignementId);
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
        for (SelectableAssignment s : getModel().getAll().values()) {
            if (s.isSelected()) {
                a.removeAllIntents(s.getValue().getAssignment().getId());
            }
        }
        onRefresh();
    }

    public void doDeleteAssignment(Integer assignementId) {
        if(UserSession.get().isManagerOrAdmin()){
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            a.removeCourseAssignment(assignementId,true);
        }
    }
    public void doUnAssign(Integer assignementId) {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getModel().getCurrentTeacher();
        if (t != null && assignementId != null) {
            a.removeCourseAssignment(assignementId,false);
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
            for (SelectableAssignment s : getModel().getAll().values()) {
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
        if(UserSession.get().isManagerOrAdmin()) {
            if (assignementId != null) {
                AcademicCourseAssignment assignment = a.findCourseAssignment(assignementId);
                if(assignment!=null) {
                    assignment.setLocked(!assignment.isLocked());
                    a.updateCourseAssignment(assignment);
                }
            }
            onRefresh();
        }
    }

    public void doSwitchLockAssignmentSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        if(UserSession.get().isManagerOrAdmin()) {
            for (SelectableAssignment s : getModel().getAll().values()) {
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
            for (SelectableAssignment s : getModel().getAll().values()) {
                if (s.isSelected()) {
                    int assignementId = s.getValue().getAssignment().getId();
                    a.removeCourseAssignment(assignementId,false);
                }
            }
//        }
        onRefresh();
    }

    public void doDeleteSelected() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        if(UserSession.get().isManagerOrAdmin()) {
//        AcademicTeacher t = getModel().getCurrentTeacher();
//        if (t != null) {
            for (SelectableAssignment s : getModel().getAll().values()) {
                if (s.isSelected()) {
                    int assignementId = s.getValue().getAssignment().getId();
                    a.removeCourseAssignment(assignementId, true);
                }
            }
//        }
            onRefresh();
        }
    }

    public Model getModel() {
        return model;
    }

    public void onOthersFiltersChanged() {
        onRefresh();
    }

    public AppDepartment getUserDepartment() {
        //enableLoadEditing
        UserSession userSession = VrApp.getBean(CorePlugin.class).getUserSession();
        if (userSession == null) {
            return null;
        }
        AppUser user = userSession.getUser();
        if (user == null) {
            return null;
        }
        return user.getDepartment();
    }

    public boolean isAllowedUpdateMineIntents(Integer assignmentId) {
        UserSession userSession = VrApp.getBean(CorePlugin.class).getUserSession();
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

        AppPeriod period = VrApp.getBean(CorePlugin.class).findPeriod(getTeacherFilter().getPeriodId());
        if (period == null || period.isReadOnly()) {
            return false;
        }
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher teacher = a.findTeacherByUser(user.getId());
        if (teacher == null) {
            return false;
        }
        if (assignmentId != null) {
            SelectableAssignment t0 = getModel().getAll().get(assignmentId);
            AcademicCourseAssignment t = t0 == null ? null : t0.getValue().getAssignment();
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
                d = t.resolveDepartment();
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
        UserSession userSession = VrApp.getBean(CorePlugin.class).getUserSession();
        if (userSession == null) {
            return false;
        }
        AppUser user = userSession.getUser();
        if (user == null) {
            return false;
        }
        AppPeriod period = VrApp.getBean(CorePlugin.class).findPeriod(getTeacherFilter().getPeriodId());

        if (userSession.isSuperAdmin()) {
            return true;
        }

        if (period == null || period.isReadOnly()) {
            return false;
        }


        if (assignementId != null) {
            SelectableAssignment t0 = getModel().getAll().get(assignementId);
            AcademicCourseAssignment t = t0 == null ? null : t0.getValue().getAssignment();
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
                d = t.resolveDepartment();
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

    private static List<SelectableAssignment> wrap(List<AcademicCourseAssignmentInfo> val, Map<Integer, SelectableAssignment> all) {
        List<SelectableAssignment> assignments;
        assignments = new ArrayList<>();
        for (AcademicCourseAssignmentInfo a : val) {
            SelectableAssignment e = all.get(a.getAssignment().getId());
            if (e == null) {
                throw new RuntimeException();
            }
            assignments.add(e);
        }
        return assignments;
    }

    public void doAssignByIntentSelected() {
        for (SelectableAssignment s : getModel().getAll().values()) {
            if (s.isSelected()) {
                int assignementId = s.getValue().getAssignment().getId();
                AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);

                SelectableAssignment rr = getModel().getAll().get(assignementId);
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
            SelectableAssignment rr = getModel().getAll().get(assignementId);
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
//                            && i.equals(assignmentTeacher.getContact().getFullName())
//            )
//                    ||
//                    (
//                            visitorTeacher != null
//                                    && visitorTeacher.getContact() != null
//                                    && i.equals(visitorTeacher.getContact().getFullName())
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

    public void onSwitchDisplayOtherModules(){
        getModel().setDisplayOtherModules(!getModel().isDisplayOtherModules());
    }

    public static class SelectableAssignment extends SelectableObject<AcademicCourseAssignmentInfo> {
        private String intents;
        private String rooms;
        private String courseIntents;
        private AcademicClass academicClass;
        private boolean currentAssigned;
        private boolean otherAssigned;
        private boolean noneAssigned;

        public SelectableAssignment(AcademicCourseAssignmentInfo value, int visitor) {
            super(value, false);
            this.intents = value.getAssignmentChunck().toStringByTeacher(visitor);
            this.courseIntents = value.getCourseChunck().toStringByTeacher(visitor);
            academicClass=value.resolveAcademicClass();
            AcademicCourseType courseType = value.getAssignment().getCourseType();
            if(courseType!=null && courseType.getName().equalsIgnoreCase("C")) {
                rooms = value.getAssignment().getCoursePlan().getRoomConstraintsC();
            }else if(courseType!=null && courseType.getName().equalsIgnoreCase("TP")){
                rooms=value.getAssignment().getCoursePlan().getRoomConstraintsTP();
            }
            if(value.isAssigned() && value.getAssignment().getTeacher()!=null){
                if(value.getAssignment().getTeacher().getId()==visitor){
                   currentAssigned=true;
                }else{
                    otherAssigned=true;
                }
            }else{
                noneAssigned=true;
            }
        }

        public String getRooms() {
            return rooms;
        }

        public AcademicClass getAcademicClass() {
            return academicClass;
        }

        public String getIntents() {
            return intents;
        }

        public String getCourseIntents() {
            return courseIntents;
        }

        public SelectableAssignment setCourseIntents(String courseIntents) {
            this.courseIntents = courseIntents;
            return this;
        }

        public void setSelected(boolean selected) {
            super.setSelected(selected);
        }

        public boolean isCurrentAssigned() {
            return currentAssigned;
        }

        public boolean isOtherAssigned() {
            return otherAssigned;
        }

        public boolean isNoneAssigned() {
            return noneAssigned;
        }
    }

    public static class TeacherPeriodStatExt {
        private TeacherPeriodStat val;
        private TeacherSemesterStatExt[] semesters;
        private List<SelectableAssignment> assignments;

        public TeacherPeriodStatExt(TeacherPeriodStat val, Map<Integer, SelectableAssignment> all) {
            this.val = val;
            TeacherSemesterStat[] sems = val.getSemesters();
            this.semesters = new TeacherSemesterStatExt[sems.length];
            for (int i = 0; i < this.semesters.length; i++) {
                this.semesters[i] = new TeacherSemesterStatExt(sems[i], all);
            }
            assignments = wrap(val.getAssignments(), all);
        }

        public TeacherPeriodStat getVal() {
            return val;
        }

        public TeacherSemesterStatExt[] getSemesters() {
            return semesters;
        }

        public List<SelectableAssignment> getAssignments() {
            return assignments;
        }
    }

    public static class TeacherSemesterStatExt {
        private TeacherSemesterStat val;
        private List<SelectableAssignment> assignments;

        public TeacherSemesterStatExt(TeacherSemesterStat val, Map<Integer, SelectableAssignment> all) {
            this.val = val;
            assignments = wrap(val.getAssignments(), all);
        }

        public TeacherSemesterStat getVal() {
            return val;
        }

        public List<SelectableAssignment> getAssignments() {
            return assignments;
        }
    }

    public static class Model {

        //        List<AcademicCourseAssignmentInfo> mineS1 = new ArrayList<>();
//        List<AcademicCourseAssignmentInfo> mineS2 = new ArrayList<>();
        List<SelectableAssignment> others = new ArrayList<>();
        List<SelectableAssignment> nonFilteredOthers = new ArrayList<>();
        Map<Integer, SelectableAssignment> all = new HashMap<>();
        TeacherPeriodStatExt stat;
        boolean nonIntentedOnly = false;
        boolean multipleSelection = false;
        boolean enableLoadEditing = false;
        boolean displayOtherModules = false;
        AcademicCourseAssignmentInfo selectedFromOthers = null;
        AcademicCourseAssignmentInfo selectedFromMine1 = null;
        AcademicCourseAssignmentInfo selectedFromMine2 = null;
        boolean myDisciplineOnly = true;
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "c", "td", "tp", "pm"};
        String[] othersFilters = defaultFilters;
        List<SelectItem> filterSelectItems = new ArrayList<>();
        String othersTextFilter;

        AcademicTeacher currentTeacher;

        public AcademicTeacher getCurrentTeacher() {
            return currentTeacher;
        }

        public void setCurrentTeacher(AcademicTeacher currentTeacher) {
            this.currentTeacher = currentTeacher;
        }

        public boolean isFilterSelected(String value) {
            return Arrays.asList(getOthersFilters()).indexOf(value) >= 0;
        }

        public boolean isMultipleSelection() {
            return multipleSelection;
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

        public Map<Integer, SelectableAssignment> getAll() {
            return all;
        }

        public void setAll(Map<Integer, SelectableAssignment> all) {
            this.all = all;
        }

        public List<SelectableAssignment> getNonFilteredOthers() {
            return nonFilteredOthers;
        }

        public Model setNonFilteredOthers(List<SelectableAssignment> nonFilteredOthers) {
            this.nonFilteredOthers = nonFilteredOthers;
            return this;
        }

        public List<SelectableAssignment> getOthers() {
            return others;
        }

        public void setOthers(List<SelectableAssignment> others) {
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
            multipleSelection = isFilterSelected("multiple-selection");
        }

        public List<SelectItem> getFilterSelectItems() {
            return filterSelectItems;
        }

        public void setFilterSelectItems(List<SelectItem> filterSelectItems) {
            this.filterSelectItems = filterSelectItems;
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
