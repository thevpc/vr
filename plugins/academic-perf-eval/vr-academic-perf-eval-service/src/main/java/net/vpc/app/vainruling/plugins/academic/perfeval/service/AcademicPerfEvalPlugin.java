/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.dto.*;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.*;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.servicemodel.FeedbacksStats;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudentStage;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;
import net.vpc.app.vainruling.core.service.util.JsonUtils;
import net.vpc.common.util.MapUtils;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin
public class AcademicPerfEvalPlugin {

    private static final Logger log = Logger.getLogger(AcademicPerfEvalPlugin.class.getName());
    private static final String[] VALID_RESPONSES_ARRAY = {"1", "2", "3", "4"};
    private static final Set<String> VALID_RESPONSES = new HashSet(Arrays.asList(VALID_RESPONSES_ARRAY));
    @Autowired
    private AcademicPlugin academic;

    public static AcademicPerfEvalPlugin get() {
        return VrApp.getBean(AcademicPerfEvalPlugin.class);
    }

    public void saveResponse(AcademicFeedbackResponse r) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String response = r.getResponse();
        if (response != null) {
            response = response.trim();
            if (!VALID_RESPONSES.contains(response)) {
                r.setResponse(null);
            }
        }
        //do some checkings?
        pu.merge(r);
    }

    @Start
    private void onStart() {
        VrApp.getBean(CorePlugin.class).createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENT_FEEDBACK, AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENT_FEEDBACK);
        VrApp.getBean(CorePlugin.class).createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_STAT_FEEDBACK, AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_STAT_FEEDBACK);
        Map<String, AcademicFeedbackSession> sessions = new HashMap<>();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (AcademicFeedbackSession session : pu.<AcademicFeedbackSession>findAll(AcademicFeedbackSession.class)) {
            int periodId = session.getPeriod().getId();
            int semesterId = session.getSemester().getId();
            String key = periodId + ";" + semesterId;
            sessions.put(key, session);
        }
        for (AcademicFeedback academicFeedback : findFeedbacks(null, null, null, null, null, null, null, null, null, null, null)) {
            if (academicFeedback.getSession() == null) {
                AcademicCoursePlan coursePlan = academicFeedback.getCourse().getCoursePlan();
                //reload it
                coursePlan = pu.findById(AcademicCoursePlan.class, coursePlan.getId());
                AppPeriod period = coursePlan.getPeriod();
                AcademicSemester semester = coursePlan.getCourseLevel().getSemester();
                if (period != null && semester != null) {
                    int periodId = period.getId();
                    int semesterId = semester.getId();
                    String key = periodId + ";" + semesterId;
                    AcademicFeedbackSession session = sessions.get(key);
                    if (session == null) {
                        session = new AcademicFeedbackSession();
                        session.setName(period.getName() + "-" + semester.getCode());
                        session.setPeriod(period);
                        session.setSemester(semester);
                        pu.persist(session);
                        sessions.put(key, session);
                    }
                    academicFeedback.setSession(session);
                    pu.merge(academicFeedback);
                } else {
                    System.out.println("Why : " + academicFeedback.getCourse());
                }
            }
        }
    }

    public List<AcademicFeedbackSession> findAllReadableSessions() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQueryBuilder(AcademicFeedbackSession.class)
                .byField("read", true)
                .<AcademicFeedbackSession>getResultList();
    }

    public List<AcademicFeedbackSession> findAllWritableSessions() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQueryBuilder(AcademicFeedbackSession.class)
                .byField("write", true)
                .<AcademicFeedbackSession>getResultList();
    }

    public List<AcademicFeedbackSession> findAllSessions() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findAll(AcademicFeedbackSession.class);
    }

    public AcademicFeedback findFeedback(int feedbackId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicFeedback.class, feedbackId);
    }

    public AcademicFeedbackModel findFeedbackModel(int feedbackId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicFeedbackModel.class, feedbackId);
    }

    public FeedbacksStats findFeedbacksStats(
            Integer teacherId,
            Integer periodId,
            Integer semesterIndex,
            Boolean validated,
            Boolean archived,
            Boolean enabled
    ) {
//        select
//                (select count(1) from ACADEMIC_FEEDBACK_RESPONSE) `all`,
//        (select count(1) from ACADEMIC_FEEDBACK_RESPONSE where RESPONSE is not NULL) ok,
//                (select count(1) from ACADEMIC_FEEDBACK_RESPONSE where RESPONSE is NULL) ko,
//                (select count(1) from ACADEMIC_FEEDBACK_RESPONSE where RESPONSE is not NULL)*100/(select count(1) from ACADEMIC_FEEDBACK_RESPONSE) `ok%`,
//        (select count(1) from ACADEMIC_FEEDBACK_RESPONSE where RESPONSE is NULL)*100/(select count(1) from ACADEMIC_FEEDBACK_RESPONSE) `ko%`
        return null;
    }

    public List<AcademicTeacher> findTeachersWithFeedbacks(int periodId, Boolean validated, Boolean archived, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (!checkEnableTeacherFeedbacks(enabled)) return Collections.EMPTY_LIST;
//        return pu.createQuery("Select u from AcademicCourseAssignment u where u.teacherId=:teacherId and exists(Select f from AcademicFeedback f where f.courseId= u.id) "
        return pu.createQuery("Select distinct u.teacher from AcademicCourseAssignment u where u.coursePlan.periodId=:periodId and u.id in (Select f.courseId from AcademicFeedback f where u.teacherId != null and 1=1 "
                + (validated != null ? (" and f.validated=" + validated) : "")
                + (archived != null ? (" and f.archived=" + archived) : "")
                + ")"
                + (enabled != null ? (" and u.enableCourseFeedback=" + enabled) : "")
        )
                .setParameter("periodId", periodId)
                //                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<AcademicClass> findClassesWithFeedbacks(int periodId, Boolean validated, Boolean archived, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (!checkEnableTeacherFeedbacks(enabled)) return Collections.EMPTY_LIST;
//        return pu.createQuery("Select distinct u.coursePlan.courseLevel.academicClass from AcademicCourseAssignment u where u.coursePlan.periodId=:periodId and u.id in (Select f.courseId from AcademicFeedback f where u.teacherId != null and 1=1 "
        return pu.createQuery("Select distinct u.coursePlan.courseLevel.academicClass from AcademicCourseAssignment u where u.coursePlan.periodId=:periodId and u.id in (Select f.courseId from AcademicFeedback f where u.teacherId != null and 1=1 "
                + (validated != null ? (" and f.validated=" + validated) : "")
                + (archived != null ? (" and f.archived=" + archived) : "")
                + ")"
                + (enabled != null ? (" and u.enableCourseFeedback=" + enabled) : "")
        )
                .setParameter("periodId", periodId)
                //                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<AcademicCourseAssignment> findAssignmentsWithFeedbacks(int periodId, int teacherId, Boolean validated, Boolean archived, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (!checkEnableTeacherFeedbacks(enabled)) return Collections.EMPTY_LIST;
//        return pu.createQuery("Select u from AcademicCourseAssignment u where u.teacherId=:teacherId and exists(Select f from AcademicFeedback f where f.courseId= u.id) "
        Query query = pu.createQuery("Select u from AcademicCourseAssignment u where u.coursePlan.periodId=:periodId "
                + (teacherId < 0 ? "" : " and u.teacherId=:teacherId ")
                + "and u.id in (Select f.courseId from AcademicFeedback f where 1=1 "
                + (validated != null ? (" and f.validated=" + validated) : "")
                + (archived != null ? (" and f.archived=" + archived) : "")
                + ")"
                + (enabled != null ? (" and u.enableCourseFeedback=" + enabled) : "")
        )
                .setParameter("periodId", periodId) //                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3);
                ;
        if (teacherId >= 0) {
            query.setParameter("teacherId", teacherId);
        }
        return query
                .getResultList();
    }

    public List<AcademicCoursePlan> findAcademicCoursePlansWithFeedbacks(int periodId, int teacherId, Boolean validated, Boolean archived, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (!checkEnableTeacherFeedbacks(enabled)) return Collections.EMPTY_LIST;
        Query query = pu.createQuery("Select x from AcademicCoursePlan x where x.periodId=:periodId and x.id in (Select u.coursePlanId from AcademicCourseAssignment u "
                + " where u.coursePlan.periodId=:periodId"
                + (teacherId < 0 ? "" : " and u.teacherId=:teacherId ")
                + " and u.id in (Select f.courseId from AcademicFeedback f where  1=1  "
                + (validated != null ? (" and f.validated=" + validated) : "")
                + (archived != null ? (" and f.archived=" + archived) : "")
                + ")"
                + (enabled != null ? (" and u.enableCourseFeedback=" + enabled) : "")
                + ")"
        )
                .setParameter("periodId", periodId) //                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                ;
        if (teacherId >= 0) {
            query.setParameter("teacherId", teacherId);
        }
        return query
                .getResultList();
    }

    public List<AcademicCourseType> findAcademicCourseTypesWithFeedbacks(int periodId, int teacherId, Boolean validated, Boolean archived, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (!checkEnableTeacherFeedbacks(enabled)) return Collections.EMPTY_LIST;
        Query query = pu.createQuery("Select x from AcademicCourseType x where exists((Select u.id from AcademicCourseAssignment u "
                + " where  u.coursePlan.periodId=:periodId "
                + (teacherId < 0 ? "" : " and u.teacherId=:teacherId ")
                + " and  u.courseTypeId=x.id "
                + " and u.id in ((Select f.courseId from AcademicFeedback f where 1=1 "
                + (validated != null ? (" and f.validated=" + validated) : "")
                + (archived != null ? (" and f.archived=" + archived) : "")
                + "))"
                + (enabled != null ? (" and u.enableCourseFeedback=" + enabled) : "")
                + "))"
        )
                .setParameter("periodId", periodId) //                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                ;
        if (teacherId >= 0) {
            query.setParameter("teacherId", teacherId);
        }
        return query
                .getResultList();
    }

//    private boolean checkEnableTeacherFeedbacks(Boolean enabled) {
//        if (enabled != null && enabled) {
//            Boolean ok = (Boolean) VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPerfEvalPlugin.EnableTeacherFeedbacks", null, true);
//            if (ok != null && !ok.booleanValue()) {
//                return false;
//            }
//        }
//        return true;
//    }
    public List<AcademicFeedback> findAssignmentFeedbacks(int academicCourseAssignmentId, Boolean validated, Boolean archived) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedback f where f.courseId= :academicCourseAssignmentId "
                + (validated != null ? (" and f.validated=" + validated) : "")
                + (archived != null ? (" and f.archived=" + archived) : "")
                + " order by f.name"
        )
                .setParameter("academicCourseAssignmentId", academicCourseAssignmentId)
                //                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public AcademicFeedback findStudentAssignmentFeedback(int academicCourseAssignmentId, int student) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedback f where f.courseId= :academicCourseAssignmentId "
                + " and f.studentId=:student"
                + " order by f.name"
        )
                .setParameter("academicCourseAssignmentId", academicCourseAssignmentId)
                .setParameter("student", student)
                .getSingleResultOrNull();
    }

    public List<AcademicFeedback> findFeedbacks(
            Integer periodId,
            Integer departmentId,
            Integer ownerDepartmentId,
            Integer coursePlanId,
            Integer teacherId,
            Integer courseTypeId,
            Integer classId,
            Boolean validated,
            Boolean archived,
            Boolean readable,
            Boolean writable
    ) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        //AcademicCoursePlan coursePlan
        QueryBuilder q = pu.createQueryBuilder("AcademicFeedback");
        q.setEntityAlias("f");
        if (periodId != null) {
            q.byExpression("f.course.coursePlan.periodId=:periodId");
            q.setParameter("periodId", periodId);
        }
        if (readable != null) {
            q.byExpression("f.session.read=:read");
            q.setParameter("read", readable);
        }
        if (writable != null) {
            q.byExpression("f.session.write=:write");
            q.setParameter("write", writable);
        }
        if (departmentId != null) {
            q.byExpression("f.course.coursePlan.courseLevel.academicClass.program.department=:departmentId");
            q.setParameter("departmentId", departmentId);
        }
        if (ownerDepartmentId != null) {
            q.byExpression("f.course.ownerDepartmentId=:ownerDepartmentId");
            q.setParameter("ownerDepartmentId", ownerDepartmentId);
        }
        if (teacherId != null) {
            q.byExpression("f.course.teacherId=:teacherId");
            q.setParameter("teacherId", teacherId);
        }
        if (coursePlanId != null) {
            q.byExpression("f.course.coursePlan.id= :coursePlanId");
            q.setParameter("coursePlanId", coursePlanId);
        }
        if (courseTypeId != null) {
            q.byExpression("f.course.courseTypeId= :courseTypeId");
            q.setParameter("courseTypeId", courseTypeId);
        }
        if (classId != null) {
            AcademicClass cc = academic.findAcademicClass(classId);
            if (cc == null) {
                return Collections.emptyList();
            }
            Set<Integer> classes = academic.findAcademicDownHierarchyIdList(cc.getId(), null);
            if (classes.isEmpty()) {
                return Collections.emptyList();
            }
            StringBuilder csb = new StringBuilder();
            int i = 0;
            for (Integer aClass : classes) {
                if (i > 0) {
                    csb.append(",");
                }
                csb.append(aClass);
                i++;
            }
            q.byExpression("(f.course.subClassId in (" + csb + ") or f.course.coursePlan.courseLevel.academicClassId in (" + csb + ") )");
        }
        if (validated != null) {
            q.byExpression("f.validated=" + validated);
        }
        if (archived != null) {
            q.byExpression("f.archived=" + archived);
        }
//        q.setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3);
        return q.getResultList();
    }

    public List<AcademicFeedbackModel> findAcademicFeedbackModels() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackModel f").getResultList();
    }

    public List<AcademicFeedback> findStudentFeedbacks(int periodId, int studentId, Boolean validated, Boolean archived, Boolean enabled, Boolean readable, Boolean writable) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (!checkEnableStudentFeedbacks(enabled)) return Collections.EMPTY_LIST;
        return pu.createQuery("Select f from AcademicFeedback f where f.studentId= :studentId and f.course.coursePlan.periodId=:periodId"
                + (readable != null ? (" and f.session.read=" + readable) : "")
                + (writable != null ? (" and f.session.write=" + writable) : "")
                + (validated != null ? (" and f.validated=" + validated) : "")
                + (archived != null ? (" and f.archived=" + archived) : "")
                + (enabled != null ? (" and f.course.enableCourseFeedback=" + enabled) : "")
                + " order by f.name"
        )
                .setParameter("studentId", studentId)
                .setParameter("periodId", periodId)
                //                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                .getResultList();
    }

//    private boolean checkEnableStudentFeedbacks(Boolean enabled) {
//        if (enabled != null && enabled) {
//            Boolean ok = UPA.getPersistenceUnit().invokePrivileged(new Action<Boolean>() {
//                @Override
//                public Boolean run() {
//                    return (Boolean) VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPerfEvalPlugin.EnableStudentFeedbacks", null, true);
//                }
//
//            });
//            if (ok != null && !ok.booleanValue()) {
//                return false;
//            }
//        }
//        return true;
//    }
    public List<AcademicFeedbackResponse> findStudentFeedbackResponses(int feedbackId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackResponse f where f.feedbackId=:feedbackId")
                .setParameter("feedbackId", feedbackId)
                .getResultList();
    }

    public List<AcademicFeedbackResponse> findStudentFeedbackResponses(int[] feedbackIds) {
        if (feedbackIds.length == 0) {
            return Collections.emptyList();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(feedbackIds[0]);
        for (int i = 1; i < feedbackIds.length; i++) {
            sb.append(",");
            sb.append(feedbackIds[i]);
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackResponse f where f.feedbackId in (" + sb + ")")
                .getResultList();
    }

    public List<AcademicFeedbackQuestion> findStudentFeedbackQuestionsByModel(int feedbackModelId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackQuestion f where f.parent.modelId=:feedbackModelId order by f.parentId, f.position")
                .setParameter("feedbackModelId", feedbackModelId)
                .getResultList();
    }

    public List<AcademicFeedbackResponse> findStudentFeedbackResponsesByGroup(int feedbackId, int groupId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackResponse f where f.feedbackId=:feedbackId  and f.parentId=:parentId")
                .setParameter("parentId", groupId)
                .setParameter("feedbackId", feedbackId)
                .getResultList();
    }

    public List<AcademicFeedbackGroup> findStudentFeedbackGroups(int feedbackModelId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackGroup f where f.modelId=:modelId order by f.position")
                .setParameter("modelId", feedbackModelId)
                .getResultList();
    }

    public List<AcademicFeedbackQuestion> findAcademicFeedbackQuestionByGroup(int groupId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicFeedbackQuestion u where u.parentId=:id order by u.position")
                .setParameter("id", groupId)
                .getResultList();
    }

    public List<AcademicCourseAssignment> findEvaluatableAssignments(int periodId, int semesterId, int[] classes) {
        if (classes.length == 0) {
            return Collections.EMPTY_LIST;
        }
        StringBuilder classIdsString = new StringBuilder();
        for (int i = 0; i < classes.length; i++) {
            if (i > 0) {
                classIdsString.append(",");
            }
            classIdsString.append(classes[i]);
        }
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where a.coursePlan.periodId=:periodId "
                + (semesterId < 0 ? "" : " and a.coursePlan.courseLevel.semesterId=:semesterId ")
                + (classIdsString.length() == 0 ? "" : " and (a.subClassId in (" + classIdsString + ") or (a.subClassId = null and a.coursePlan.courseLevel.academicClassId in (" + classIdsString + "))) ")
                + " and a.teacher.allowCourseFeedback=true "
                + " and a.coursePlan.allowCourseFeedback=true "
        )
                .setParameter("semesterId", semesterId, semesterId >= 0)
                .setParameter("periodId", periodId)
                .getResultList();
    }

    public void generateStudentsFeedbackForm(final int academicFeedbackModelId, int academicFeedbackSessionId, final String studentProfileFilter) {
        final PersistenceUnit pu = UPA.getPersistenceUnit();
        final AcademicFeedbackModel academicFeedbackModel = pu.findById(AcademicFeedbackModel.class, academicFeedbackModelId);
        final AcademicFeedbackSession academicFeedbackSession = pu.findById(AcademicFeedbackSession.class, academicFeedbackSessionId);
        if (academicFeedbackModel == null) {
            return;
        }
        if (academicFeedbackSession == null) {
            return;
        }
        TraceService traceService = TraceService.get();
        Map<String, Object> traceParams = MapUtils.map("model", academicFeedbackModel.getName(), "filter", studentProfileFilter);
        String traceData = JsonUtils.jsonMap("model", academicFeedbackModel.getName(), "filter", studentProfileFilter);
        try {
            traceService.trace("Academic.generate-students-feedback-form", "start", traceParams, traceData,
                    "/Education/Evaluation",
                    java.util.logging.Level.INFO
            );
            TraceService.makeSilenced(new VoidAction() {
                @Override
                public void run() {
                    List<AcademicFeedbackQuestion> academicFeedbackQuestions = pu.createQuery("Select u from AcademicFeedbackQuestion u where u.parent.modelId=:id")
                            .setParameter("id", academicFeedbackModelId)
                            .getResultList();

                    for (AcademicStudent s : academic.findStudents(studentProfileFilter, AcademicStudentStage.ATTENDING, "x.allowCourseFeedback=true")) {
                        //allowCourseFeedback
                        boolean allowCourseFeedback;

                        List<AcademicClass> allClasses = academic.findStudentClasses(s.getId(), false, true);

                        Set<Integer> myClasses = new HashSet<>();
                        for (AcademicClass mc : academic.findStudentClasses(s.getId(), false, false)) {
                            myClasses.add(mc.getId());
                        }
                        int[] classesId = new int[allClasses.size()];
                        for (int i = 0; i < classesId.length; i++) {
                            classesId[i] = allClasses.get(i).getId();
                        }
                        List<AcademicCourseAssignment> assignements = findEvaluatableAssignments(academicFeedbackSession.getPeriod().getId(), academicFeedbackSession.getSemester().getId(), classesId);
                        Set<Integer> existingAssignementIds = pu.createQuery("Select f.course.id from AcademicFeedback f where f.course.coursePlan.periodId=:periodId and f.studentId=:studentId")
                                .setParameter("periodId", academicFeedbackSession.getPeriod().getId())
                                .setParameter("studentId", s.getId())
                                .getValueSet(0);
                        for (AcademicCourseAssignment assignement : assignements) {
                            //if(myClasses.contains(assignement.resolveAcademicClass().getId())){
                            if (!existingAssignementIds.contains(assignement.getId())) {
                                if (!"PFE-PFE".equals(assignement.getName())) {
                                    //AcademicCoursePlan
                                    //check if feedback is still there:
                                    AcademicFeedback f = new AcademicFeedback();
                                    f.setArchived(false);
                                    f.setCourse(assignement);
                                    f.setStudent(s);
                                    f.setModel(academicFeedbackModel);
                                    f.setSession(academicFeedbackSession);
                                    pu.persist(f);

                                    for (AcademicFeedbackQuestion q : academicFeedbackQuestions) {
                                        AcademicFeedbackResponse r = new AcademicFeedbackResponse();
                                        r.setFeedback(f);
                                        r.setQuestion(q);
                                        r.setValid(false);
                                        pu.persist(r);
                                    }
                                }
                            }
//                                                      }else{
//                                                          AcademicFeedback old = findStudentAssignmentFeedback(assignement.getId(), s.getId());
//                                                          if(old!=null) {
//                                                              CorePlugin.get().remove("AcademicFeedback", old.getId());
//                                                          }
//                                                      }
                        }
                    }
                }
            }
            ).run();
            traceService.trace("generateStudentsFeedbackForm", "success", traceParams,
                    traceData,
                    "/Education/Evaluation",
                    java.util.logging.Level.INFO
            );
        } catch (Exception ex) {
            traceParams = MapUtils.map("model", academicFeedbackModel.getName(), "filter", studentProfileFilter, "error", ex.getMessage());
            traceData = JsonUtils.jsonMap("model", academicFeedbackModel.getName(), "filter", studentProfileFilter, "error", ex.getMessage());
            traceService.trace("generateStudentsFeedbackForm", "error", traceParams, traceData,
                    "/Education/Evaluation",
                    java.util.logging.Level.SEVERE
            );

        }

    }

    public StatData evalStatData(List<AcademicFeedback> feedbacks) {
        StatData statData = new StatData();

        int countFeedbacks = feedbacks.size();
        int countQuestions = 0;
        int countValidResponses = 0;
        double countResponseCompletion = 0;
        List<GroupView> rows = new ArrayList<>();
        StatCountSet globalValues = new StatCountSet();
        globalValues.touchAllArr(VALID_RESPONSES_ARRAY);
        if (feedbacks.size() > 0) {
            AcademicFeedbackModel fmodel = feedbacks.get(0).getModel();
            List<AcademicFeedbackGroup> groups = findStudentFeedbackGroups(fmodel.getId());
            Map<Integer, QuestionView> questionsMap = new HashMap<Integer, QuestionView>();
            Map<Integer, GroupView> groupsMap = new HashMap<Integer, GroupView>();

            for (AcademicFeedbackGroup group : groups) {
                GroupView row = new GroupView();
                row.getValues().touchAllArr(VALID_RESPONSES_ARRAY);
                row.setTitle(group.getName());
                ArrayList<QuestionView> questions = new ArrayList<QuestionView>();
                row.setQuestions(questions);
                groupsMap.put(group.getId(), row);
                for (AcademicFeedbackQuestion r : findAcademicFeedbackQuestionByGroup(group.getId())) {
                    QuestionView q = new QuestionView();
                    q.setQuestion(r);
                    q.getValues().touchAllArr(VALID_RESPONSES_ARRAY);
                    questions.add(q);
                    questionsMap.put(r.getId(), q);
                }
                countQuestions += questions.size();
                rows.add(row);
            }
            int[] feedbacksIds = new int[feedbacks.size()];
            for (int i = 0; i < feedbacks.size(); i++) {
                feedbacksIds[i] = feedbacks.get(i).getId();
            }
            for (AcademicFeedbackResponse r : findStudentFeedbackResponses(feedbacksIds)) {
                if (!StringUtils.isEmpty(r.getResponse())) {
                    countValidResponses++;
                    QuestionView qv = questionsMap.get(r.getQuestion().getId());
                    int studentId = r.getFeedback().getStudent().getId();
                    if (qv != null) {
                        qv.getValues().inc(r.getResponse());
                        qv.getValues().getStudents().add(studentId);
                    }
                    GroupView gv = groupsMap.get(r.getQuestion().getParent().getId());
                    if (gv != null) {
                        gv.getValues().inc(r.getResponse());
                        gv.getValues().getStudents().add(studentId);
                    }
                    globalValues.inc(r.getResponse());
                    globalValues.getStudents().add(studentId);
                }
            }
        }
        if (countQuestions != 0 && countFeedbacks != 0) {
            countResponseCompletion = (countValidResponses * 100) / (countQuestions * countFeedbacks);
        }
        statData.setCountFeedbacks(countFeedbacks);
        statData.setCountQuestions(countQuestions);
        statData.setCountValidResponses(countValidResponses);
        statData.setCountResponseCompletion(countResponseCompletion);
        statData.setGroupedQuestionsList(rows);
        statData.setGlobalValues(globalValues);

        ////////////////////////////////////
        //eval Student List completion Status
        ////////////////////////////////////
        Map<Integer, Studentinfo> studentinfoMap = new HashMap<>();
        for (AcademicFeedback academicFeedback : feedbacks) {
            Studentinfo studentinfo = studentinfoMap.get(academicFeedback.getStudent().getId());
            if (studentinfo == null) {
                studentinfo = new Studentinfo();
                studentinfo.setStudent(academicFeedback.getStudent());
                studentinfoMap.put(academicFeedback.getStudent().getId(), studentinfo);
            }
            studentinfo.setMaxValidation(studentinfo.getMaxValidation() + 1);
            if (academicFeedback.isValidated()) {
                studentinfo.setValidated(studentinfo.getValidated() + 1);
            }
        }
        int[] feedbacksIds = new int[feedbacks.size()];
        for (int i = 0; i < feedbacks.size(); i++) {
            feedbacksIds[i] = feedbacks.get(i).getId();
        }
        for (AcademicFeedbackResponse r : findStudentFeedbackResponses(feedbacksIds)) {
            Studentinfo studentinfo = studentinfoMap.get(r.getFeedback().getStudent().getId());
            studentinfo.setMaxAnswers(studentinfo.getMaxAnswers() + 1);
            if (!StringUtils.isEmpty(r.getResponse())) {
                studentinfo.setAnswers(studentinfo.getAnswers() + 1);
            }
        }

        List<Studentinfo> studentinfoList = new ArrayList<>(studentinfoMap.values());
        Collections.sort(studentinfoList, new Comparator<Studentinfo>() {
            @Override
            public int compare(Studentinfo o1, Studentinfo o2) {
                int x = Long.compare(o1.getAnswers(), o2.getAnswers());
                if (x == 0) {
                    x = academic.getValidName(o1.getStudent()).compareTo(academic.getValidName(o2.getStudent()));
                }
                return x;
            }
        });
        statData.setStudentinfos(studentinfoList);
        return statData;
    }

    public FeedbackForm createFeedbackForm(int feedbackId, int feedbackModelId) {
        FeedbackForm ff = new FeedbackForm();
        List<FRow> rows = ff.getRows();
        if (feedbackId >= 0) {
            AcademicFeedback feedback = findFeedback(feedbackId);
            ff.setFeedback(feedback);
            List<AcademicFeedbackGroup> groups = findStudentFeedbackGroups(feedback.getModel().getId());
            Map<Integer, FQuestion> questionsMap = new HashMap<>();
            Map<Integer, FRow> groupsMap = new HashMap<>();
            for (AcademicFeedbackGroup group : groups) {
                FRow row = new FRow();
                row.setTitle(group.getName());
                ArrayList<FQuestion> questions = new ArrayList<FQuestion>();
                row.setQuestions(questions);
                groupsMap.put(group.getId(), row);
                rows.add(row);
            }

            for (AcademicFeedbackQuestion r : findStudentFeedbackQuestionsByModel(feedback.getModel().getId())) {
                FQuestion q = new FQuestion();
                questionsMap.put(r.getId(), q);
                FRow gg = groupsMap.get(r.getParent().getId());
                if (gg != null) {
                    gg.getQuestions().add(q);
                }
            }
            for (AcademicFeedbackResponse r : findStudentFeedbackResponses(feedbackId)) {
                FQuestion qq = questionsMap.get(r.getQuestion().getId());
                if (qq != null) {
                    qq.setResponse(r);
                }
            }
        }
        return ff;
    }

}
