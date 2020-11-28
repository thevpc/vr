/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.web.load;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfoByVisitor;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;
import net.thevpc.app.vainruling.plugins.academic.service.dto.TeacherPeriodStatExt;
import net.thevpc.app.vainruling.plugins.academic.service.dto.TeacherSemesterStatExt;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.app.vainruling.core.service.editor.EditorConfig;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.util.Chronometer;

import javax.faces.model.SelectItem;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.thevpc.app.vainruling.plugins.academic.service.dto.TeacherLoadInfoFilter;
import net.thevpc.app.vainruling.plugins.academic.service.dto.TeacherLoadInfo;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherPeriod;
import net.thevpc.upa.Document;
import net.thevpc.upa.UPA;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class AbstractCourseLoadCtrl {

    private static final Logger LOG = Logger.getLogger(AbstractCourseLoadCtrl.class.getName());
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

    public boolean isDeparmentManagedOnly() {
        return false;
    }

    private void reset() {
//        getModel().setMineS1(new ArrayList<AcademicCourseAssignmentInfo>());
//        getModel().setMineS2(new ArrayList<AcademicCourseAssignmentInfo>());
        getModel().setTeacherLoadInfoResult(new TeacherLoadInfo(-1));
    }

    public void onConfirmLoad() {
        try {
            int periodId = getTeacherFilter().getPeriodId();
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            AcademicTeacher teacher = getCurrentTeacher();
            getModel().setCurrentTeacher(teacher);
            if (teacher != null) {
                AcademicTeacherPeriod o = a.findTeacherPeriod(periodId, teacher.getId());
                if (o != null) {
                    o.setLoadConfirmed(true);
                    UPA.getPersistenceUnit().merge(o);
                    getModel().setLoadConfirmed(true);
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public int getCurrentTeacherId() {
        final AcademicTeacher t = getCurrentTeacher();
        return t == null ? -1 : t.getId();
    }

    public abstract AcademicTeacher getCurrentTeacher();

    public void onRefreshFiltersChanged() {
        onRefresh();
    }

    @VrOnPageLoad
    public void onRefresh(String cmd) {
        try {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
//        List<AppPeriod> navigatablePeriods = core.findNavigatablePeriods();
//        AppPeriod mainPeriod = core.getCurrentPeriod();
            onInit();
            onChangePeriod();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void onInit() {
        try {
            getTeacherFilter().onInit();
            getCourseFilter().onInit();
            getOthersCourseFilter().onInit();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
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
            EditorConfig c = new EditorConfig();
            c.entity = "AcademicCourseAssignment";
            c.id = String.valueOf(a.getId());
            return Vr.get().gotoPage("editor", VrUtils.formatJSONObject(c));
        }
        return null;
    }

    public void onChangePeriod() {
        try {
            AcademicPlugin academic = VrApp.getBean(AcademicPlugin.class);
//        AppDepartment userDepartment = getUserDepartment();
            int periodId = getTeacherFilter().getPeriodId();

            int departmentId = getTeacherDepartment();
            boolean enableLoadConfirmation = false;
            boolean enableLoadEditing = false;
            boolean loadConfirmed = false;
            if (periodId >= 0 && departmentId >= 0) {
                final Document p = academic.getDepartmentPeriodDocument(periodId, departmentId);
                if (p != null) {
                    enableLoadConfirmation = p.getBoolean("enableLoadConfirmation", false);
                    enableLoadEditing = p.getBoolean("enableLoadEditing", false);
                }
            }
            getModel().setEnableLoadConfirmation(enableLoadConfirmation);
            getModel().setEnableLoadEditing(enableLoadEditing);
            int teacherId = getCurrentTeacherId();
            if (periodId >= 0 && teacherId >= 0) {
                AcademicTeacherPeriod tp = academic.findTeacherPeriod(periodId, teacherId);
                loadConfirmed = tp.isLoadConfirmed();
            }
            getModel().setLoadConfirmed(loadConfirmed);

            getTeacherFilter().onChangePeriod();
            getCourseFilter().onChangePeriod();

            List<SelectItem> refreshableFilters = new ArrayList<>();
            refreshableFilters.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
            refreshableFilters.add(FacesUtils.createSelectItem("proposals", "Inclure Propositions", "vr-checkbox"));
            refreshableFilters.add(FacesUtils.createSelectItem("deviation-week", "Balance/Sem", "vr-checkbox"));
            refreshableFilters.add(FacesUtils.createSelectItem("deviation-extra", "Balance/Supp", "vr-checkbox"));
            refreshableFilters.add(FacesUtils.createSelectItem("extra-abs", "Supp ABS", "vr-checkbox"));
            getCourseFilter().getModel().setRefreshFilterItems(refreshableFilters);

            refreshableFilters = new ArrayList<>();
            refreshableFilters.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
            refreshableFilters.add(FacesUtils.createSelectItem("proposals", "Inclure Propositions", "vr-checkbox"));
            getOthersCourseFilter().getModel().setRefreshFilterItems(refreshableFilters);

            getOthersCourseFilter().onChangePeriod();
            onChangeOther();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
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
        try {
            Chronometer chronometer = Chronometer.start();
            final int teacherDepartment = getTeacherDepartment();
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
            if (teacher != null) {
                AcademicTeacherPeriod o = a.findTeacherPeriod(periodId, teacher.getId());
                getModel().setLoadConfirmed(o != null && o.isLoadConfirmed());
            } else {
                getModel().setLoadConfirmed(false);
            }
            reset();
            DefaultCourseAssignmentFilter othersCourseAssignmentFilter = getOthersCourseFilter().getCourseAssignmentFilter();
            if (isDeparmentManagedOnly()) {
                if (teacherDepartment >= 0 && !CorePlugin.get().isCurrentSessionAdmin()) {
                    othersCourseAssignmentFilter.setAcceptedOwnerDepartments(
                            new HashSet<Integer>(Arrays.asList(teacherDepartment))
                    );
                }
            }
            TeacherLoadInfoFilter ff = new TeacherLoadInfoFilter();
            ff.setOtherCourseAssignmentFilter(othersCourseAssignmentFilter);
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
            getModel().setTeacherLoadInfoResult(result);
            applyOthersTextFilter();
            chronometer.stop();
            System.out.println(chronometer);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void applyOthersTextFilter() {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            a.teacherLoadInfoApplyTextFilter(getModel().getTeacherLoadInfoResult(), getModel().getOthersTextFilter());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void assignmentsToIntentsAll() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void intentsToAssignmentsAll() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void assignmentsToIntentsMine() {
        try {
            for (AcademicCourseAssignmentInfoByVisitor aa : getModel().getStat().getAssignments()) {
                addToMine(aa.getValue().getAssignment().getId());
                doUnAssign(aa.getValue().getAssignment().getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void intentsToAssignmentsMine() {
        try {
            for (AcademicCourseAssignmentInfoByVisitor aa : getModel().getStat().getAssignments()) {
                doAssign(aa.getValue().getAssignment().getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeAllIntentsMine() {
        try {
            for (AcademicCourseAssignmentInfoByVisitor aa : getModel().getStat().getAssignments()) {
                removeFromMine(aa.getValue().getAssignment().getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeAllAssignmentsMine() {
        try {
            for (AcademicCourseAssignmentInfoByVisitor aa : getModel().getStat().getAssignments()) {
                doUnAssign(aa.getValue().getAssignment().getId());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void addToMineOtherSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void addProposalToMineOtherSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void addToMineCurrSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void addProposalToMineCurrSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void addWishToMineCurrSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void addToMine(Integer assignementId) {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            AcademicTeacher t = getModel().getCurrentTeacher();
            if (t != null && assignementId != null) {
                a.addIntent(t.getId(), assignementId);
            }
            onRefresh();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void addWishToMine(Integer assignementId) {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            AcademicTeacher t = getModel().getCurrentTeacher();
            if (t != null && assignementId != null) {
                a.addWish(t.getId(), assignementId);
            }
            onRefresh();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeFromMineCurrSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeProposalFromMineCurrSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeWishFromMineCurrSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeFromMine(Integer assignementId) {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            AcademicTeacher t = getModel().getCurrentTeacher();
            if (t != null && assignementId != null) {
                a.removeIntent(t.getId(), assignementId);
            }
            onRefresh();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeAllIntents(Integer assignementId) {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            if (assignementId != null) {
                a.removeAllIntents(assignementId);
            }
            onRefresh();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeAllIntentsSelected() {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
                if (s.isSelected()) {
                    a.removeAllIntents(s.getValue().getAssignment().getId());
                }
            }
            onRefresh();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeAllProposalsSelected() {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
                if (s.isSelected()) {
                    a.removeAllProposal(s.getValue().getAssignment().getId());
                }
            }
            onRefresh();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void removeAllWishesSelected() {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            for (AcademicCourseAssignmentInfoByVisitor s : getModel().getAll().values()) {
                if (s.isSelected()) {
                    a.removeAllWishes(s.getValue().getAssignment().getId());
                }
            }
            onRefresh();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void doDeleteAssignment(Integer assignementId) {
        try {
            if (CorePlugin.get().isCurrentSessionAdminOrManager()) {
                AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
                a.removeCourseAssignment(assignementId, true, false);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void doUnAssign(Integer assignementId) {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            AcademicTeacher t = getModel().getCurrentTeacher();
            if (t != null && assignementId != null) {
                a.removeCourseAssignment(assignementId, false, true);
            }
            onRefresh();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void doAssign(Integer assignementId) {
        try {
            AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
            AcademicTeacher t = getModel().getCurrentTeacher();
            if (t != null && assignementId != null) {
                a.addCourseAssignment(t.getId(), assignementId);
            }
            onRefresh();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void doAssignSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void doSwitchLockAssignment(Integer assignementId) {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void doSwitchLockAssignmentSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void doUnAssignSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void doDeleteSelected() {
        try {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
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

    public int getTeacherDepartment() {
        final AcademicTeacher currentTeacher = getCurrentTeacher();
        if (currentTeacher != null && currentTeacher.getUser() != null && currentTeacher.getUser().getDepartment() != null) {
            return currentTeacher.getUser().getDepartment().getId();
        }
        return -1;
    }

    public int getUserDepartmentId() {
        final AppDepartment d = getUserDepartment();
        return d == null ? -1 : d.getId();
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
        return AcademicPlugin.get().teacherLoadInfoIsAllowedUpdateMineIntents(getModel().getTeacherLoadInfoResult(), assignmentId);
    }

    public boolean isAllowedUpdateMineAssignments(Integer assignementId) {
        return AcademicPlugin.get().teacherLoadInfoIsAllowedUpdateMineAssignments(getModel().getTeacherLoadInfoResult(), assignementId);
    }

    public void doAssignByIntentSelected() {
        if (VrApp.getBean(AcademicPlugin.class).teacherLoadInfoDoAssignByIntentSelected(getModel().getTeacherLoadInfoResult())) {
            onRefresh();
        }
    }

    public void doAssignByIntent(Integer assignementId) {
        if (VrApp.getBean(AcademicPlugin.class).teacherLoadInfoDoAssignByIntent(getModel().getTeacherLoadInfoResult(), assignementId)) {
            onRefresh();
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
        TeacherLoadInfo result = new TeacherLoadInfo(-1);
//        List<AcademicCourseAssignmentInfoByVisitor> others = new ArrayList<>();
//        List<AcademicCourseAssignmentInfoByVisitor> nonFilteredOthers = new ArrayList<>();
//        Map<Integer, AcademicCourseAssignmentInfoByVisitor> all = new HashMap<>();
//        TeacherPeriodStatExt stat;
        boolean nonIntentedOnly = false;
        boolean multipleSelection = true;
        boolean enableLoadEditing = false;
        boolean enableLoadConfirmation = false;
        boolean displayOtherModules = false;
        boolean loadConfirmed = false;
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

        public boolean isLoadConfirmed() {
            return loadConfirmed;
        }

        public void setLoadConfirmed(boolean loadConfirmed) {
            this.loadConfirmed = loadConfirmed;
        }

        public String[] getCurrentTeacherFilters() {
            return currentTeacherFilters;
        }

        public boolean isEnableLoadConfirmation() {
            return enableLoadConfirmation;
        }

        public void setEnableLoadConfirmation(boolean enableLoadConfirmation) {
            this.enableLoadConfirmation = enableLoadConfirmation;
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
            return result.getAll();
        }

        public void setTeacherLoadInfoResult(TeacherLoadInfo result) {
            this.result = result;
        }

        public List<AcademicCourseAssignmentInfoByVisitor> getNonFilteredOthers() {
            return result.getNonFilteredOthers();
        }

        public TeacherLoadInfo getTeacherLoadInfoResult() {
            return result;
        }

//        public Model setNonFilteredOthers(List<AcademicCourseAssignmentInfoByVisitor> nonFilteredOthers) {
//            this.nonFilteredOthers = nonFilteredOthers;
//            return this;
//        }
        public double getOthersSumC() {
            return result.getLoadSum().getC();
        }

        public double getOthersSumTD() {
            return result.getLoadSum().getTd();
        }

        public double getOthersSumTP() {
            return result.getLoadSum().getTp();
        }

        public double getOthersSumPM() {
            return result.getLoadSum().getPm();
        }

        public double getOthersSumMA() {
            return result.getMaLoad();
        }

        public List<AcademicCourseAssignmentInfoByVisitor> getOthers() {
            return result.getOthers();
        }

        public TeacherPeriodStatExt getStat() {
            return result.getStat();
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
