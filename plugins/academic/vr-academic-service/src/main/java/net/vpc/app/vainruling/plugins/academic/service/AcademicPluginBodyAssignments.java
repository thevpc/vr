package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionTable;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicTeacherSemestrialLoad;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment1;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment2;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseIntent;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionRow;
import net.vpc.app.vainruling.plugins.academic.model.current.TeacherAssignmentChunck;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.academic.service.integration.AcademicConversionTableHelper;
import net.vpc.app.vainruling.plugins.academic.service.integration.TeacherGenerationHelper;
import net.vpc.app.vainruling.plugins.academic.service.stat.*;
import net.vpc.app.vainruling.plugins.academic.service.util.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.util.CourseAssignmentFilterAnd;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.*;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import net.vpc.common.mon.ProgressMonitor;
import net.vpc.upa.types.DateTime;

public class AcademicPluginBodyAssignments extends AcademicPluginBody {

    private static final Logger log = Logger.getLogger(AcademicPluginBodyAssignments.class.getName());
    public static final Function<AcademicCourseAssignment, Integer> academicCourseAssignmentIdConverter = AcademicCourseAssignment::getId;

    private static TeacherGenerationHelper teacherGenerationHelper = new TeacherGenerationHelper();

    private CacheService cacheService;
    CorePlugin core;

    @Override
    public void onStart() {
        cacheService = CacheService.get();
        core = CorePlugin.get();
        core.addProfileRight("Teacher", AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ASSIGNMENTS);
        core.createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_INTENTS, "Mettre à jours les voeux de autres");
        core.createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_INTENTS, "Mettre à jours les affectations");
        core.createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_INTENTS, "Mettre à jours les voeux de autres");
        core.createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_ASSIGNMENTS, "Mettre à jours les affectations");

    }

    public void dupCourseAssignment(int assignmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = pu.findById(AcademicCourseAssignment.class, assignmentId);
        AcademicPluginSecurity.requireManageableCourseAssignment(a);
        String d = a.getDiscriminator();
        if (StringUtils.isBlank(d)) {
            d = "";
        } else {
            d = d + "-";
        }
        a.setDiscriminator(d + "1");
        pu.merge(a);

        a.setId(0);
        a.setDiscriminator(d + "2");
        pu.persist(a);
    }

    public void splitGroupCourseAssignment(int assignmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = pu.findById(AcademicCourseAssignment.class, assignmentId);
        if (a != null && a.getGroupCount() > 0) {
            AcademicPluginSecurity.requireManageableCourseAssignment(a);
            String d = a.getDiscriminator();
            if (StringUtils.isBlank(d)) {
                d = "";
            } else {
                d = d + "-";
            }
            double g = a.getGroupCount();
            a.setGroupCount(g / 2.0);
            a.setDiscriminator(d + "G1");
            pu.merge(a);

            //create a perfect copy!
            a.setId(0);
            a.setDiscriminator(d + "G2");
            pu.persist(a);
        }
    }

    public void addCourseAssignment(AcademicCourseAssignment assignment) {
        AcademicPluginSecurity.requireUpdatableCourseAssignment(assignment);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.persist(assignment);
    }

    public void updateCourseAssignment(AcademicCourseAssignment assignment) {
        AcademicPluginSecurity.requireUpdatableCourseAssignment(assignment);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.merge(assignment);
    }

    public void addCourseAssignment(int teacherId, int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = pu.findById(AcademicCourseAssignment.class, assignementId);
        AcademicPluginSecurity.requireUpdatableCourseAssignment(a);
        a.setTeacher(getContext().getPlugin().findTeacher(teacherId));
        pu.merge(a);
    }

    public void removeCourseAssignment(int assignementId, boolean hardRemoval, boolean switchToIntent) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = pu.findById(AcademicCourseAssignment.class, assignementId);
        if (a != null) {
            AcademicPluginSecurity.requireUpdatableCourseAssignment(a);
            if (hardRemoval) {
                pu.remove(a);
            } else {
                AcademicTeacher teacher = a.getTeacher();
                a.setTeacher(null);
                pu.merge(a);
                if (teacher != null && switchToIntent) {
                    addIntent(teacher.getId(), a.getId());
                }
            }
        }
//        cacheService.get(AcademicCourseAssignment.class).invalidate();
    }

    public void addIntent(int teacherId, int assignementId) {
        boolean samePerson = AcademicPlugin.get().isCurrentTeacher(teacherId);
        AcademicPluginSecurity.requireTeacherOrManager(teacherId);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment assignment = pu.findById(AcademicCourseAssignment.class, assignementId);
        AcademicCourseIntent i = pu.createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId and a.assignmentId=:assignementId")
                .setParameter("teacherId", teacherId)
                .setParameter("assignementId", assignementId)
                .getFirstResultOrNull();
        if (i == null) {
            i = new AcademicCourseIntent();
            i.setTeacher(getContext().getPlugin().findTeacher(teacherId));
            i.setAssignment(assignment);
            if (samePerson) {
                i.setWish(true);
                if (i.isProposal()) {
                    i.setAcceptedWish(true);
                    i.setWishDate(new DateTime());
                }
                if (i.getTeacher() == null || i.getAssignment() == null) {
                    throw new RuntimeException("Error");
                }
                AcademicCourseIntent i2 = i;
                pu.invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        pu.persist(i2);
                    }
                });
            } else {
                i.setProposal(true);
                if (i.isWish()) {
                    i.setAcceptedProposal(true);
                    i.setProposalDate(new DateTime());
                }
                if (i.getTeacher() == null || i.getAssignment() == null) {
                    throw new RuntimeException("Error");
                }
                pu.persist(i);
            }

        } else {
            if (samePerson) {
                if (!i.isWish()) {
                    i.setWish(true);
                    if (i.isProposal()) {
                        i.setAcceptedWish(true);
                        i.setWishDate(new DateTime());
                    }
                    AcademicCourseIntent i2=i;
                    pu.invokePrivileged(new VoidAction() {
                        @Override
                        public void run() {
                            pu.merge(i2);
                        }
                    });
                }
            } else {
                if (!i.isProposal()) {
                    i.setProposal(true);
                    if (i.isWish()) {
                        i.setAcceptedProposal(true);
                        i.setProposalDate(new DateTime());
                    }
                    pu.merge(i);
                }
            }
        }
    }

    public void addProposal(int teacherId, int assignementId) {
        AcademicPluginSecurity.requireHeadOfDepartment(getContext().getPlugin().findTeacher(teacherId));
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment assignment = pu.findById(AcademicCourseAssignment.class, assignementId);
        AcademicCourseIntent i = pu.createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId and a.assignmentId=:assignementId")
                .setParameter("teacherId", teacherId)
                .setParameter("assignementId", assignementId)
                .getFirstResultOrNull();
        if (i == null) {
            i = new AcademicCourseIntent();
            i.setTeacher(getContext().getPlugin().findTeacher(teacherId));
            i.setAssignment(assignment);
            i.setProposal(true);
            if (i.getTeacher() == null || i.getAssignment() == null) {
                throw new RuntimeException("Error");
            }
            pu.persist(i);
        } else {
            if (!i.isProposal()) {
                i.setProposal(true);
                if (i.isWish()) {
                    i.setAcceptedProposal(true);
                    i.setProposalDate(new DateTime());
                }
                pu.merge(i);
            }
        }
    }

    public void addWish(int teacherId, int assignementId) {
        AcademicPluginSecurity.requireTeacherOrManager(teacherId);
//        AcademicPluginSecurity.requireHeadOfDepartment(getContext().getPlugin().findTeacher(teacherId));
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment assignment = pu.findById(AcademicCourseAssignment.class, assignementId);
        AcademicCourseIntent i = pu.createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId and a.assignmentId=:assignementId")
                .setParameter("teacherId", teacherId)
                .setParameter("assignementId", assignementId)
                .getFirstResultOrNull();

        pu.invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                if (i == null) {
                    AcademicCourseIntent i2 = new AcademicCourseIntent();
                    i2.setTeacher(getContext().getPlugin().findTeacher(teacherId));
                    i2.setAssignment(assignment);
                    i2.setWish(true);
                    if (i2.isProposal()) {
                        i2.setAcceptedWish(true);
                        i2.setWishDate(new DateTime());
                    }
                    if (i2.getTeacher() == null || i.getAssignment() == null) {
                        throw new RuntimeException("Error");
                    }
                    pu.persist(i2);
                } else {
                    if (!i.isWish()) {
                        i.setWish(true);
                        if (i.isProposal()) {
                            i.setAcceptedWish(true);
                            i.setWishDate(new DateTime());
                        }
                        pu.merge(i);
                    }
                }
            }
        });
    }

    public void removeWish(int teacherId, int assignementId) {
        AcademicPluginSecurity.requireHeadOfDepartment(getContext().getPlugin().findTeacher(teacherId));
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseIntent i = pu.createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId and a.assignmentId=:assignementId")
                .setParameter("teacherId", teacherId)
                .setParameter("assignementId", assignementId)
                .getFirstResultOrNull();
        if (i != null) {
            pu.invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    if (i.isWish()) {
                        i.setWish(false);
                        i.setAcceptedWish(false);
                        i.setWishDate(null);
                        if (!i.isWish() && !i.isProposal()) {
                            pu.remove(i);
                        } else {
                            pu.merge(i);
                        }
                    }
                }
            });
        }
    }

    public void removeIntent(int teacherId, int assignementId) {
        boolean samePerson = AcademicPlugin.get().isCurrentTeacher(teacherId);
        if (!samePerson) {
            AcademicPluginSecurity.requireTeacherOrManager(teacherId);
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseIntent i = pu.createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId and a.assignmentId=:assignementId")
                .setParameter("teacherId", teacherId)
                .setParameter("assignementId", assignementId)
                .getFirstResultOrNull();
        if (i != null) {
            if (samePerson) {
                if (i.isWish()) {
                    i.setWish(false);
                    i.setAcceptedWish(false);
                    i.setWishDate(null);
                    pu.invokePrivileged(new VoidAction() {
                        @Override
                        public void run() {
                            if (!i.isWish() && !i.isProposal()) {
                                pu.remove(i);
                            } else {
                                pu.merge(i);
                            }
                        }
                    });
                }
            } else {
                if (i.isProposal()) {
                    i.setProposal(false);
                    i.setAcceptedProposal(false);
                    i.setProposalDate(null);
                    if (!i.isWish() && !i.isProposal()) {
                        pu.remove(i);
                    } else {
                        pu.merge(i);
                    }
                }
            }
        }
    }

    public void removeProposal(int teacherId, int assignementId) {
        AcademicPluginSecurity.requireTeacherOrManager(teacherId);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseIntent i = pu.createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId and a.assignmentId=:assignementId")
                .setParameter("teacherId", teacherId)
                .setParameter("assignementId", assignementId)
                .getFirstResultOrNull();
        if (i != null) {
            if (i.isProposal()) {
                i.setProposal(false);
                i.setAcceptedProposal(false);
                i.setProposalDate(null);
                if (!i.isWish() && !i.isProposal()) {
                    pu.remove(i);
                } else {
                    pu.merge(i);
                }
            }
        }
    }

    public void removeAllIntents(int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment assignment = pu.findById(AcademicCourseAssignment.class, assignementId);
        if (assignment != null) {
            AcademicTeacher t = assignment.getTeacher();
            AcademicPluginSecurity.requireTeacherOrManager(t == null ? -1 : t.getId());
            List<AcademicCourseIntent> intentList = pu.createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignementId")
                    .setParameter("assignementId", assignementId)
                    .getResultList();
            for (AcademicCourseIntent ii : intentList) {
                removeIntent(ii.getTeacher().getId(), assignementId);
            }
        }
    }

    public void removeAllProposals(int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment assignment = pu.findById(AcademicCourseAssignment.class, assignementId);
        if (assignment != null) {
            AcademicTeacher t = assignment.getTeacher();
            AcademicPluginSecurity.requireTeacherOrManager(t == null ? -1 : t.getId());
            List<AcademicCourseIntent> intentList = pu.createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignementId")
                    .setParameter("assignementId", assignementId)
                    .getResultList();
            for (AcademicCourseIntent ii : intentList) {
                removeProposal(ii.getTeacher().getId(), assignementId);
            }
        }
    }

    public void removeAllWishes(int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment assignment = pu.findById(AcademicCourseAssignment.class, assignementId);
        if (assignment != null) {
            AcademicTeacher t = assignment.getTeacher();
            AcademicPluginSecurity.requireTeacherOrManager(t == null ? -1 : t.getId());
            List<AcademicCourseIntent> intentList = pu.createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignementId")
                    .setParameter("assignementId", assignementId)
                    .getResultList();
            for (AcademicCourseIntent ii : intentList) {
                removeWish(ii.getTeacher().getId(), assignementId);
            }
        }
    }

    public AcademicCourseAssignment findAcademicCourseAssignment(int assignmentId) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        AcademicCourseAssignment assignment = UPA.getPersistenceUnit().findById(AcademicCourseAssignment.class, assignmentId);
//        AcademicPluginSecurity.requireUpdatableCourseAssignment(assignment);
        return assignment;
    }

    public List<AcademicCourseAssignment> findAcademicCourseAssignments(int periodId) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
//        cacheService.get(AcademicCourseAssignment.class).invalidate();
        return cacheService.get(AcademicCourseAssignment.class).getProperty("findAcademicCourseAssignments:" + periodId, new Action<List<AcademicCourseAssignment>>() {
            @Override
            public List<AcademicCourseAssignment> run() {
                List<AcademicCourseAssignment> list = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where a.coursePlan.periodId=:periodId")
                        .setParameter("periodId", periodId)
                        //                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                        .getResultList();
                return list;
            }
        });
    }

    public List<AcademicCourseIntent> findCourseIntentsByAssignment(int periodId, int assignment, Integer semester, CourseAssignmentFilter filter) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        return getAcademicCourseIntentByAssignmentAndSemester(periodId, assignment, semester);
//        List<AcademicCourseIntent> intents = null;
//        intents = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignment")
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
//                .setParameter("assignment", assignment)
//                .getResultList();
//        List<AcademicCourseIntent> m = new ArrayList<>();
//        for (AcademicCourseIntent value : intents) {
//            AcademicSemester semester1 = value.getAssignment().getCoursePlan().getCourseLevel().getSemester();
//            if (semester == null || (semester1 != null
//                    && semester1.getId() == (semester))) {
//                if (filter == null || filter.acceptAssignment(value.getAssignment())) {
//                    m.add(value);
//                }
//            }
//        }
//        return m;
    }

    public List<AcademicCourseIntent> findCourseIntentsByAssignment(int assignment) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        List<AcademicCourseIntent> intents = null;
        intents = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignment")
                //                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                .setParameter("assignment", assignment)
                .getResultList();
        return intents;
    }

    public List<AcademicCourseIntent> findCourseIntentsByTeacher(int periodId, Integer teacher, CourseAssignmentFilter filter) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        List<AcademicCourseIntent> intents = new ArrayList<>();
        if (teacher == null) {
            for (AcademicCourseIntent value : findCourseIntents(periodId)) {
                if (filter == null || filter.acceptAssignment(new AcademicCourseAssignment2(value))) {
                    intents.add(value);
                }
            }
        } else {
            List<AcademicCourseIntent> list = getAcademicCourseIntentByTeacherId(periodId).get(teacher);
            if (list == null) {
                //System.out.println("No intents for " + teacher + " : " + getAcademicTeacherMap().get(teacher));
            } else {
                for (AcademicCourseIntent value : list) {
                    if (filter == null || filter.acceptAssignment(new AcademicCourseAssignment2(value))) {
                        intents.add(value);
                    }
                }
            }
        }
        return intents;
    }

    private List<AcademicCourseAssignment> filterAssignments(List<AcademicCourseAssignment> base, CourseAssignmentFilter filter) {
        if (filter == null) {
            return base;
        }
        List<AcademicCourseAssignment> ret = new ArrayList<>();
        for (AcademicCourseAssignment academicCourseAssignment : base) {
            if (filter.acceptAssignment(new AcademicCourseAssignment1(academicCourseAssignment))) {
                ret.add(academicCourseAssignment);
            }
        }
        return ret;
    }

    public List<AcademicCourseAssignment> findCourseAssignments(int periodId, Integer teacher, CourseAssignmentFilter filter) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        List<AcademicCourseAssignment> base = null;
        if (filter == null || filter.lookupIntents()) {
            List<AcademicCourseAssignment> all = new ArrayList<>();
            for (AcademicCourseAssignmentInfo i : findCourseAssignmentsAndIntents(periodId, teacher, filter)) {
                all.add(i.getAssignment());
            }
            base = all;
        } else {
            List<AcademicCourseAssignment> m = new ArrayList<>();
            if (teacher == null) {
                for (AcademicCourseAssignment value : findAcademicCourseAssignments(periodId)) {
                    if (filter.acceptAssignment(new AcademicCourseAssignment1(value))) {
                        m.add(value);
                    }
                }
            } else {
                List<AcademicCourseAssignment> list = findAcademicCourseAssignmentListGroupByByTeacherId(periodId).get(teacher);
                AcademicTeacher tt = getContext().getPlugin().findTeacher(teacher);
                AcademicTeacherPeriod tp = tt == null ? null : getContext().getPlugin().findTeacherPeriod(periodId, tt.getId());
                if (list == null) {
                    if (tt != null) {
                        if (!tp.isEnabled()) {
                            //this is okkay!
                        } else {
                            //System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                        }
                    } else {
                        //System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                    }
                } else {
                    if (tt != null && !tp.isEnabled()) {
                        System.out.println("Found assignments for teacherId=" + teacher + " : " + tt + " but he/she seems to be not enabled in " + core.findPeriod(periodId).getName());
                    }
                    for (AcademicCourseAssignment value : list) {
                        if (filter == null || filter.acceptAssignment(new AcademicCourseAssignment1(value))) {
                            m.add(value);
                        }
                    }
                }
            }
            return m;
        }
        return base;
    }

    public List<AcademicCourseAssignmentInfo> findCourseAssignmentsAndIntents(int periodId, Integer teacher, CourseAssignmentFilter filter) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        List<AcademicCourseAssignmentInfo> all = new ArrayList<>();
        if (filter != null && !filter.lookupIntents()) {
            if (teacher == null) {
                for (AcademicCourseAssignment value : findAcademicCourseAssignments(periodId)) {
                    if (filter == null || filter.acceptAssignment(new AcademicCourseAssignment1(value))) {
                        AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                        b.setAssigned(value.getTeacher() != null);
                        b.setAssignment(value);
                        all.add(b);
                    }
                }
            } else {
                List<AcademicCourseAssignment> list = findAcademicCourseAssignmentListGroupByByTeacherId(periodId).get(teacher);
                AcademicTeacher tt = getContext().getPlugin().findTeacher(teacher);
                AcademicTeacherPeriod tp = tt == null ? null : getContext().getPlugin().findTeacherPeriod(periodId, tt.getId());
                if (list == null) {
                    if (tt != null) {
                        if (!tp.isEnabled()) {
                            //this is okkay!
                        } else {
                            //System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                        }
                    } else {
                        //System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                    }
                } else {
                    if (tt != null && !tp.isEnabled()) {
                        System.out.println("Found assignments for teacherId=" + teacher + " : " + tt + " but he/she seems to be not enabled in " + core.findPeriod(periodId).getName());
                    }
                    for (AcademicCourseAssignment value : list) {
                        if (filter == null || filter.acceptAssignment(new AcademicCourseAssignment1(value))) {
                            AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                            b.setAssigned(value.getTeacher() != null);
                            b.setAssignment(value);
                            all.add(b);
                        }
                    }
                }
            }
        } else {
            HashSet<Integer> visited = new HashSet<>();
            for (AcademicCourseAssignment a : findCourseAssignments(periodId, teacher, new CourseAssignmentFilterAnd().and(filter).and(new DefaultCourseAssignmentFilter().setAcceptIntents(false)))) {
                if (!visited.contains(a.getId())) {
                    visited.add(a.getId());
                    AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                    b.setAssigned(a.getTeacher() != null);
                    b.setAssignment(a);
                    all.add(b);
                }
            }
            Map<Integer, List<AcademicCourseIntent>> intentsByAssignment = new HashMap<>();
            for (AcademicCourseIntent a : findCourseIntentsByTeacher(periodId, teacher, filter)) {
                int assId = a.getAssignment().getId();
                List<AcademicCourseIntent> other = intentsByAssignment.get(assId);
                if (other == null) {
                    other = new ArrayList<>();
                    intentsByAssignment.put(assId, other);
                }
                other.add(a);
                if (!visited.contains(assId)) {
                    visited.add(assId);
                    AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                    b.setAssigned(false);
                    b.setAssignment(a.getAssignment());
//                    b.setCurrentProposal(a.isProposal());
//                    b.setCurrentWish(a.isWish());
//                    TeacherAssignmentChunck c = new TeacherAssignmentChunck(teacher == null ? -1 : teacher, "");
//                    c.setAcceptedProposal(a.isAcceptedProposal());
//                    c.setAcceptedWish(a.isAcceptedWish());
//                    c.setAssigned(a.getAssignment().getTeacher() != null);
//                    c.setProposal(a.isProposal());
//                    c.setWish(a.isWish());
//                    b.setCurrentStatusString(c.getStatusString());
//                    b.setCurrentStatusLocalizedString(c.getStatusLocalizedString());
                    all.add(b);
                }
            }
            for (AcademicCourseAssignmentInfo a : all) {
                List<AcademicCourseIntent> intentObjects = intentsByAssignment.get(a.getAssignment().getId());
//            TreeSet<String> allIntents = new TreeSet<>();
//            TreeSet<Integer> allIntentIds = new TreeSet<>();
                if (intentObjects != null) {
                    for (AcademicCourseIntent b1 : intentObjects) {
//                    if (teacher == null || (teacher.intValue() != b1.getTeacher().getId())) {
                        String n = getContext().getPlugin().getValidName(b1.getTeacher());
                        Map<Integer, TeacherAssignmentChunck> chuncks = a.getAssignmentChunck().getChuncks();
                        TeacherAssignmentChunck c = chuncks.get(b1.getTeacher().getId());
                        if (c == null) {
                            c = new TeacherAssignmentChunck(b1.getTeacher().getId(), n);
                            c.setProposal(b1.isProposal());
                            c.setWish(b1.isWish());
                            c.setAcceptedProposal(b1.isAcceptedProposal());
                            c.setAcceptedWish(b1.isAcceptedWish());
                            chuncks.put(b1.getTeacher().getId(), c);
                        } else {
                            c.setProposal(b1.isProposal());
                            c.setWish(b1.isWish());
                            c.setAcceptedProposal(b1.isAcceptedProposal());
                            c.setAcceptedWish(b1.isAcceptedWish());
                        }
//                    }
                    }
                }
                if (a.getAssignment().getTeacher() != null) {
                    AcademicTeacher teacher1 = a.getAssignment().getTeacher();
                    String n = getContext().getPlugin().getValidName(teacher1);
                    Map<Integer, TeacherAssignmentChunck> chuncks = a.getAssignmentChunck().getChuncks();
                    TeacherAssignmentChunck c = chuncks.get(teacher1.getId());
                    if (c == null) {
                        c = new TeacherAssignmentChunck(teacher1.getId(), n);
                        c.setAssigned(true);
                        chuncks.put(teacher1.getId(), c);
                    } else {
                        c.setAssigned(true);
                    }
                }

                AcademicCoursePlan coursePlan = a.getAssignment().getCoursePlan();
                List<AcademicCourseAssignment> other = coursePlan == null ? Collections.EMPTY_LIST : findCourseAssignmentsByCoursePlan(coursePlan.getId());
                for (AcademicCourseAssignment academicCourseAssignment : other) {
                    AcademicTeacher teacher1 = academicCourseAssignment.getTeacher();
                    if (teacher1 != null) {
                        String n = getContext().getPlugin().getValidName(teacher1);
                        if (academicCourseAssignment.getId() != a.getAssignment().getId()) {
                            Map<Integer, TeacherAssignmentChunck> achuncks = a.getAssignmentChunck().getChuncks();
                            if (!achuncks.containsKey(teacher1.getId())) {
                                Map<Integer, TeacherAssignmentChunck> chuncks = a.getCourseChunck().getChuncks();
                                TeacherAssignmentChunck c = chuncks.get(teacher1.getId());
                                if (c == null) {
                                    c = new TeacherAssignmentChunck(teacher1.getId(), n);
                                    c.setAssigned(true);
                                    chuncks.put(teacher1.getId(), c);
                                } else {
                                    c.setAssigned(true);
                                }
                            }
                        }
                    }
                }
                List<AcademicCourseIntent> otherIntents = coursePlan == null ? Collections.EMPTY_LIST : findCourseIntentsByCoursePlan(coursePlan.getId());
                for (AcademicCourseIntent intent : otherIntents) {
                    AcademicTeacher teacher1 = intent.getTeacher();
                    if (teacher1 != null) {
                        String n = getContext().getPlugin().getValidName(teacher1);
                        if (intent.getId() != a.getAssignment().getId()) {
                            Map<Integer, TeacherAssignmentChunck> achuncks = a.getAssignmentChunck().getChuncks();
                            if (!achuncks.containsKey(teacher1.getId())) {
                                Map<Integer, TeacherAssignmentChunck> chuncks = a.getCourseChunck().getChuncks();
                                TeacherAssignmentChunck c = chuncks.get(teacher1.getId());
                                if (c == null) {
                                    c = new TeacherAssignmentChunck(teacher1.getId(), n);
                                    c.setProposal(intent.isProposal());
                                    c.setWish(intent.isWish());
                                    c.setAcceptedProposal(intent.isAcceptedProposal());
                                    c.setAcceptedWish(intent.isAcceptedWish());
                                    chuncks.put(teacher1.getId(), c);
                                } else {
                                    c.setProposal(intent.isProposal());
                                    c.setWish(intent.isWish());
                                    c.setAcceptedProposal(intent.isAcceptedProposal());
                                    c.setAcceptedWish(intent.isAcceptedWish());
                                }
                            }
                        }
                    }
                }

//            a.setIntentsSet(allIntents);
//            a.setIntents(sb.toString());
//            a.setIntentsTeacherIdsSet(allIntentIds);
            }
            Collections.sort(all, new Comparator<AcademicCourseAssignmentInfo>() {

                @Override
                public int compare(AcademicCourseAssignmentInfo o1, AcademicCourseAssignmentInfo o2) {
                    AcademicCourseAssignment a1 = o1.getAssignment();
                    AcademicCourseAssignment a2 = o2.getAssignment();
//                String s1 = a1.getCoursePlan().getName();
//                String s2 = a2.getCoursePlan().getName();
                    String s1 = StringUtils.nonNull(a1.getFullName());
                    String s2 = StringUtils.nonNull(a2.getFullName());
                    return s1.compareTo(s2);
                }
            });
        }
        //apply current teacher info for chunks
        AcademicTeacher currentTeacher = AcademicPlugin.get().getCurrentTeacher();
        int currentTeacherId = currentTeacher == null ? -1 : currentTeacher.getId();
        for (AcademicCourseAssignmentInfo info : all) {
            TeacherAssignmentChunck r = info.getAssignmentChunck().getForTeacher(currentTeacherId);
//            if (r != null) {
//                info.setCurrentWish(r.isWish());
//                info.setCurrentProposal(r.isProposal());
//                info.setCurrentStatusString(r.getStatusString());
//                info.setCurrentStatusLocalizedString(r.getStatusLocalizedString());
//            } else {
//                info.setCurrentStatusString("");
//                info.setCurrentStatusLocalizedString("");
//                info.setCurrentWish(false);
//                info.setCurrentProposal(false);
//            }
        }
        return all;
    }

    private void copyDeviationFrom(TeacherSemesterStat one, List<TeacherSemesterStat> list) {
        for (TeacherSemesterStat semesterStat : list) {
            if (semesterStat.getTeacher().getId() == one.getTeacher().getId() && semesterStat.getSemester().getId() == one.getSemester().getId()) {
                one.setPopulation(semesterStat.getPopulation());
                break;
            }
        }
    }

    private void copyDeviationFrom(TeacherPeriodStat one, List<TeacherPeriodStat> list) {
        for (TeacherPeriodStat semesterStat : list) {
            if (semesterStat.getTeacher().getId() == one.getTeacher().getId()) {
                one.setPopulation(semesterStat.getPopulation());
                break;
            }
        }
    }

    public AcademicCourseAssignment findCourseAssignment(int courseAssignmentId) {
        return (AcademicCourseAssignment) UPA.getPersistenceUnit().findById(AcademicCourseAssignment.class, courseAssignmentId);
    }

    public AcademicCourseAssignment findCourseAssignment(int coursePlanId, Integer subClassId, String discriminator) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where "
                + "a.coursePlanId=:coursePlanId "
                + "and a.subClassId=:subClassId "
                + "and a.discriminator=:discriminator "
        )
                .setParameter("coursePlanId", coursePlanId)
                .setParameter("subClassId", subClassId)
                .setParameter("discriminator", discriminator)
                .getSingleResultOrNull();
    }

    public void generateTeacherAssignmentDocumentsFolder(int periodId) {
        CorePluginSecurity.requireAdmin();
        generateTeacherAssignmentDocumentsFolder(periodId, "/Documents/Services/Supports Pedagogiques/Par Enseignant");
    }

    public void generateTeacherAssignmentDocumentsFolder(int periodId, String path) {
        CorePluginSecurity.requireAdmin();
        UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                for (AcademicCourseAssignment a : findAcademicCourseAssignments(periodId)) {
                    if (a.getTeacher() != null && a.getTeacher().getUser() != null) {
                        String n = VrUtils.toValidFileName(a.getTeacher().resolveFullName());
                        String c = VrUtils.toValidFileName(a.getFullName());
                        VFile r = CorePlugin.get().getRootFileSystem().get(path + "/" + n + "/" + c);
                        r.mkdirs();
                    }
                }
            }
        });
    }

    public MapList<Integer, AcademicCourseAssignment> findCourseAssignments(int periodId, CourseAssignmentFilter filter) {
        MapList<Integer, AcademicCourseAssignment> courseAssignments = findCourseAssignments(periodId);
        if (filter == null) {
            return courseAssignments;
        }
        MapList<Integer, AcademicCourseAssignment> m = new DefaultMapList<Integer, AcademicCourseAssignment>(
                academicCourseAssignmentIdConverter
        );
        for (AcademicCourseAssignment academicCourseAssignment : courseAssignments) {
            if (filter.acceptAssignment(new AcademicCourseAssignment1(academicCourseAssignment))) {
                m.add(academicCourseAssignment);
            }
        }
        return m;
    }

    public MapList<Integer, AcademicCourseAssignment> findCourseAssignments(int periodId) {
        return cacheService.get(AcademicCourseAssignment.class)
                .getProperty("findCourseAssignments:" + periodId, new Action<MapList<Integer, AcademicCourseAssignment>>() {
                    @Override
                    public MapList<Integer, AcademicCourseAssignment> run() {
                        List<AcademicCourseAssignment> assignments = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where a.coursePlan.periodId=:periodId "
                                + " order by a.coursePlan.courseLevel.semester.code,a.coursePlan.courseLevel.academicClass.program.name,a.name,a.courseType.name")
                                .setParameter("periodId", periodId)
                                //                                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                                .getResultList();

                        return CollectionUtils.unmodifiableMapList(new DefaultMapList<Integer, AcademicCourseAssignment>(
                                assignments,
                                academicCourseAssignmentIdConverter
                        ));
                    }
                });
    }

//    public List<AcademicCourseIntent> findCourseIntents(int periodId) {
//        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.assignment.coursePlan.periodId=:periodId")
//                .setParameter("periodId", periodId)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
//                .getResultList();
//    }
    public List<AcademicCourseIntent> findCourseIntents(int periodId) {
        return cacheService.get(AcademicCourseIntent.class)
                .getProperty("findCourseIntents:" + periodId, new Action<List<AcademicCourseIntent>>() {
                    @Override
                    public List<AcademicCourseIntent> run() {
                        return UPA.getPersistenceUnit().createQuery("Select u from AcademicCourseIntent u where u.assignment.coursePlan.periodId=:periodId")
                                //                                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 4)
                                .setParameter("periodId", periodId)
                                .getResultList();
                    }
                });
    }

    public List<AcademicCourseIntent> findCourseIntentsByCoursePlan(int coursePlanId) {
        return cacheService.get(AcademicCourseIntent.class)
                .getProperty("findCourseIntentsByCoursePlan:" + coursePlanId, new Action<List<AcademicCourseIntent>>() {
                    @Override
                    public List<AcademicCourseIntent> run() {
                        return UPA.getPersistenceUnit().createQuery("Select u from AcademicCourseIntent u where u.assignment.coursePlanId=:coursePlanId")
                                //                                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 4)
                                .setParameter("coursePlanId", coursePlanId)
                                .getResultList();
                    }
                });
    }

    public List<AcademicCourseAssignment> findCourseAssignmentsByCoursePlan(int planId) {
        return cacheService.get(AcademicCourseAssignment.class)
                .getProperty("findCourseAssignmentsByCoursePlan:" + planId, new Action<List<AcademicCourseAssignment>>() {
                    @Override
                    public List<AcademicCourseAssignment> run() {
                        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where a.coursePlanId=:v")
                                .setParameter("v", planId)
                                .getResultList();
                    }
                });
    }

    public List<AcademicCourseAssignment> findCourseAssignmentsByClass(int periodId, int classId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where "
                + "(a.subClassId=:v or a.coursePlan.courseLevel.academicClassId=:v)"
                + " and a.coursePlan.periodId=:periodId"
        )
                .setParameter("periodId", periodId)
                .setParameter("v", classId).getResultList();
    }

    public List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoadsByTeacher(int periodId, int teacherId) {
        Map<Integer, List<AcademicTeacherSemestrialLoad>> map = cacheService.get(AcademicTeacherSemestrialLoad.class)
                .getProperty("findTeacherSemestrialLoadsByTeacher:" + periodId, new Action<Map<Integer, List<AcademicTeacherSemestrialLoad>>>() {
                    @Override
                    public Map<Integer, List<AcademicTeacherSemestrialLoad>> run() {
                        Map<Integer, List<AcademicTeacherSemestrialLoad>> map = new HashMap<Integer, List<AcademicTeacherSemestrialLoad>>();
                        for (AcademicTeacherSemestrialLoad load : findTeacherSemestrialLoadsByPeriod(periodId)) {
                            List<AcademicTeacherSemestrialLoad> list = map.get(load.getTeacher().getId());
                            if (list == null) {
                                list = new ArrayList<AcademicTeacherSemestrialLoad>();
                                map.put(load.getTeacher().getId(), list);
                            }
                            list.add(load);
                        }
                        return map;
                    }
                });
        List<AcademicTeacherSemestrialLoad> list = map.get(teacherId);
        if (list == null) {
            return Collections.EMPTY_LIST;
        }
        return list;
//
//
//        return UPA.getPersistenceUnit().createQuery("Select a from AcademicTeacherSemestrialLoad a where a.teacherId=:t and a.periodId=:periodId")
//                .setParameter("t", teacherId)
//                .setParameter("periodId", periodId)
//                .getResultList();
    }

    public List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoadsByPeriod(int periodId) {
        return cacheService.get(AcademicTeacherSemestrialLoad.class)
                .getProperty("findTeacherSemestrialLoadsByPeriod:" + periodId, new Action<List<AcademicTeacherSemestrialLoad>>() {
                    @Override
                    public List<AcademicTeacherSemestrialLoad> run() {
                        return UPA.getPersistenceUnit().createQuery("Select a from AcademicTeacherSemestrialLoad a where a.periodId=:periodId")
                                .setParameter("periodId", periodId)
                                .getResultList();
                    }
                });
    }

    public List<AcademicCourseIntent> getAcademicCourseIntentByAssignmentAndSemester(int periodId, Integer assignmentId, Integer semester) {
        return cacheService.get(AcademicCourseIntent.class).getProperty("getAcademicCourseIntentByAssignmentAndSemester:" + periodId + ":" + assignmentId + ":" + semester,
                new Action<List<AcademicCourseIntent>>() {
            @Override
            public List<AcademicCourseIntent> run() {
                List<AcademicCourseIntent> m = new ArrayList<>();
                if (assignmentId == null) {
                    for (AcademicCourseIntent value : findCourseIntents(periodId)) {
                        AcademicSemester semester1 = value.resolveSemester();
                        if (semester == null || (semester1 != null && semester1.getId() == (semester))) {
                            m.add(value);
                        }
                    }
                } else {
                    List<AcademicCourseIntent> list = getAcademicCourseIntentByAssignmentId(periodId).get(assignmentId);
                    if (list == null) {
//                System.out.println("No intents for " + assignmentId + " : assignment=" + assignmentId);
                    } else {
                        for (AcademicCourseIntent value : list) {
                            AcademicSemester semester1 = value.resolveSemester();
                            if (semester == null || (semester1 != null
                                    && semester1.getId() == (semester))) {
                                m.add(value);
                            }
                        }
                    }
                }
                return m;
            }
        }
        );

    }

    public Map<Integer, List<AcademicCourseIntent>> getAcademicCourseIntentByTeacherId(int periodId) {

        return cacheService.get(AcademicCourseIntent.class).getProperty("getAcademicCourseIntentByTeacherId:" + periodId,
                new Action<Map<Integer, List<AcademicCourseIntent>>>() {
            @Override
            public Map<Integer, List<AcademicCourseIntent>> run() {
                Map<Integer, List<AcademicCourseIntent>> m = new HashMap<Integer, List<AcademicCourseIntent>>();
                for (AcademicCourseIntent e : findCourseIntents(periodId)) {
                    int t = e.getTeacher().getId();
                    List<AcademicCourseIntent> list = m.get(t);
                    if (list == null) {
                        list = new ArrayList<AcademicCourseIntent>();
                        m.put(t, list);
                    }
                    list.add(e);
                }
                return m;
            }
        }
        );
    }

    public Map<Integer, List<AcademicCourseAssignment>> findAcademicCourseAssignmentListGroupByByTeacherId(int periodId) {
        return cacheService.get(AcademicCourseAssignment.class).getProperty("findAcademicCourseAssignmentListGroupByTeacherId:" + periodId,
                new Action<Map<Integer, List<AcademicCourseAssignment>>>() {
            @Override
            public Map<Integer, List<AcademicCourseAssignment>> run() {
                Map<Integer, List<AcademicCourseAssignment>> m = new HashMap<>();
                for (AcademicCourseAssignment a : findAcademicCourseAssignments(periodId)) {
                    if (a.getTeacher() == null) {
                        //ignore
                        //System.out.println("No assignment for " + a);
                    } else {
                        int t = a.getTeacher().getId();
                        List<AcademicCourseAssignment> list = m.get(t);
                        if (list == null) {
                            list = new ArrayList<AcademicCourseAssignment>();
                            m.put(t, list);
                        }
                        list.add(a);
                    }
                }
                return m;
            }
        }
        );
    }

    public Map<Integer, List<AcademicCourseIntent>> getAcademicCourseIntentByAssignmentId(int periodId) {

        return cacheService.get(AcademicCourseIntent.class).getProperty("getAcademicCourseIntentByAssignmentId:" + periodId,
                new Action<Map<Integer, List<AcademicCourseIntent>>>() {
            @Override
            public Map<Integer, List<AcademicCourseIntent>> run() {
                Map<Integer, List<AcademicCourseIntent>> m = new HashMap<Integer, List<AcademicCourseIntent>>();
                for (AcademicCourseIntent e : findCourseIntents(periodId)) {
                    int t = e.getAssignment().getId();
                    List<AcademicCourseIntent> list = m.get(t);
                    if (list == null) {
                        list = new ArrayList<AcademicCourseIntent>();
                        m.put(t, list);
                    }
                    list.add(e);
                }
                return m;
            }
        }
        );
    }

    public AcademicConversionTableHelper findConversionTableByPeriodId(int periodId) {
        Integer id = VrApp.getBean(CacheService.class).get(AppPeriod.class)
                .getProperty("loadConversionTableId", new Action<Integer>() {
                    @Override
                    public Integer run() {
                        return UPA.getPersistenceUnit()
                                .createQuery("Select a.loadConversionTableId from AppPeriod a where a.id=:id")
                                .setParameter("id", periodId)
                                .getInteger();
                    }
                });
//                if(a.getL)

        if (id == null) {
            throw new IllegalArgumentException("Missing Conversion Table for Period " + periodId + " : " + VrApp.getBean(CorePlugin.class).findPeriod(periodId));
        }
        return findConversionTableById(id);
    }

    public AcademicConversionTableHelper findConversionTableById(int id) {
        return cacheService.get(AcademicTeacherSemestrialLoad.class)
                .getProperty("findConversionTableById:" + id, new Action<AcademicConversionTableHelper>() {
                    @Override
                    public AcademicConversionTableHelper run() {
                        PersistenceUnit pu = UPA.getPersistenceUnit();
                        AcademicLoadConversionTable t = pu.findById(AcademicLoadConversionTable.class, id);
                        if (t == null) {
                            return null;
                        }
                        List<AcademicLoadConversionRow> rows = pu
                                .createQueryBuilder(AcademicLoadConversionRow.class)
                                .byField("conversionTableId", t.getId())
                                .getResultList();
                        AcademicConversionTableHelper h = new AcademicConversionTableHelper(t);
                        for (AcademicLoadConversionRow row : rows) {
                            h.add(row);
                        }
                        return h;
                    }
                });
    }

    public void generateTeachingLoad(int periodId, CourseAssignmentFilter courseAssignmentFilter, String version0, String oldVersion, ProgressMonitor monitor) throws IOException {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE);
        teacherGenerationHelper.generateTeachingLoad(periodId, courseAssignmentFilter, version0, oldVersion, monitor);
    }

    public List<AcademicCourseAssignment> findAcademicCourseAssignmentListByCoursePlanId(int coursePlanId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select c from AcademicCourseAssignment c where c.coursePlanId=:coursePlanId")
                .setParameter("coursePlanId", coursePlanId)
                .getResultList();
    }
}
