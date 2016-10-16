/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service;

import net.vpc.app.vainruling.core.service.*;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.*;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.servicemodel.FeedbacksStats;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.upa.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@AppPlugin
public class AcademicPerfEvalPlugin {
    private static final Logger log = Logger.getLogger(AcademicPerfEvalPlugin.class.getName());

    public void saveResponse(AcademicFeedbackResponse r) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        //do some checkings?
        pu.merge(r);
    }

    @Start
    private void onStart() {
        VrApp.getBean(CorePlugin.class).createRight("Custom.Academic.StudentFeedback", "Custom.Academic.StudentFeedback");
        VrApp.getBean(CorePlugin.class).createRight("Custom.Academic.TeacherStatFeedback", "Custom.Academic.TeacherStatFeedback");
    }

    public AcademicFeedback findFeedback(int feedbackId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicFeedback.class, feedbackId);
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

    public List<AcademicCourseAssignment> findAssignmentsWithFeedbacks(int teacherId, Boolean validated, Boolean archived, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (enabled != null && enabled) {
            Boolean ok = (Boolean) VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPerfEvalPlugin.EnableTeacherFeedbacks", null, true);
            if (ok != null && !ok.booleanValue()) {
                return Collections.EMPTY_LIST;
            }
        }
//        return pu.createQuery("Select u from AcademicCourseAssignment u where u.teacherId=:teacherId and exists(Select f from AcademicFeedback f where f.courseId= u.id) "
        return pu.createQuery("Select u from AcademicCourseAssignment u where u.teacherId=:teacherId and u.id in (Select f.courseId from AcademicFeedback f where 1=1 "
                        + (validated != null ? (" and f.validated=" + validated) : "")
                        + (archived != null ? (" and f.archived=" + archived) : "")
                        + ")"
                        + (enabled != null ? (" and u.enableCourseFeedback=" + enabled) : "")
        )
                .setParameter("teacherId", teacherId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<AcademicCoursePlan> findAcademicCoursePlansWithFeedbacks(int teacherId, Boolean validated, Boolean archived, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (enabled != null && enabled) {
            Boolean ok = (Boolean) VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPerfEvalPlugin.EnableTeacherFeedbacks", null, true);
            if (ok != null && !ok.booleanValue()) {
                return Collections.EMPTY_LIST;
            }
        }
        return pu.createQuery("Select x from AcademicCoursePlan x where x.id in (Select u.coursePlanId from AcademicCourseAssignment u " +
                        " where u.teacherId=:teacherId " +
                        " and u.id in (Select f.courseId from AcademicFeedback f where 1=1 "
                        + (validated != null ? (" and f.validated=" + validated) : "")
                        + (archived != null ? (" and f.archived=" + archived) : "")
                        + ")"
                        + (enabled != null ? (" and u.enableCourseFeedback=" + enabled) : "")
                        + ")"
        )
                .setParameter("teacherId", teacherId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<AcademicCourseType> findAcademicCourseTypesWithFeedbacks(int teacherId, Boolean validated, Boolean archived, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (enabled != null && enabled) {
            Boolean ok = (Boolean) VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPerfEvalPlugin.EnableTeacherFeedbacks", null, true);
            if (ok != null && !ok.booleanValue()) {
                return Collections.EMPTY_LIST;
            }
        }
        return pu.createQuery("Select x from AcademicCourseType x where exists(Select u from AcademicCourseAssignment u " +
                        " where u.teacherId=:teacherId " +
                        " and  u.courseTypeId=x.id " +
                        " and u.id in (Select f.courseId from AcademicFeedback f where 1=1 "
                        + (validated != null ? (" and f.validated=" + validated) : "")
                        + (archived != null ? (" and f.archived=" + archived) : "")
                        + ")"
                        + (enabled != null ? (" and u.enableCourseFeedback=" + enabled) : "")
                        + ")"
        )
                .setParameter("teacherId", teacherId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<AcademicFeedback> findAssignmentFeedbacks(int academicCourseAssignmentId, Boolean validated, Boolean archived) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedback f where f.courseId= :academicCourseAssignmentId "
                        + (validated != null ? (" and f.validated=" + validated) : "")
                        + (archived != null ? (" and f.archived=" + archived) : "")
                        + " order by f.name"
        )
                .setParameter("academicCourseAssignmentId", academicCourseAssignmentId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<AcademicFeedback> findFeedbacks(
            Integer coursePlanId,
            Integer teacherId,
            Integer courseTypeId,
            Integer classId,
            Boolean validated,
            Boolean archived
    ) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        //AcademicCoursePlan coursePlan
        StringBuilder sb = new StringBuilder();
        QueryBuilder q = pu.createQueryBuilder("AcademicFeedback");
        q.setEntityAlias("f");
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
            AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
            AcademicClass cc = aca.findAcademicClass(classId);
            if (cc == null) {
                return Collections.emptyList();
            }
            Set<Integer> classes = aca.findAcademicDownHierarchyIdList(cc.getId(), null);
            if (classes.isEmpty()) {
                return Collections.emptyList();
            }
            StringBuilder csb = new StringBuilder();
            int i=0;
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
        q.setHint(QueryHints.NAVIGATION_DEPTH, 3);
        return q.getResultList();
    }

    public List<AcademicFeedback> findStudentFeedbacks(int studentId, Boolean validated, Boolean archived, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (enabled != null && enabled) {
            Boolean ok = UPA.getPersistenceUnit().invokePrivileged(new Action<Boolean>() {
                @Override
                public Boolean run() {
                    return (Boolean) VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPerfEvalPlugin.EnableStudentFeedbacks", null, true);
                }

            });
            if (ok != null && !ok.booleanValue()) {
                return Collections.EMPTY_LIST;
            }
        }
        return pu.createQuery("Select f from AcademicFeedback f where f.studentId= :studentId "
                        + (validated != null ? (" and f.validated=" + validated) : "")
                        + (archived != null ? (" and f.archived=" + archived) : "")
                        + (enabled != null ? (" and f.course.enableCourseFeedback=" + enabled) : "")
                        + " order by f.name"
        )
                .setParameter("studentId", studentId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

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

    public List<AcademicCourseAssignment> findEvaluatableAssignments(int[] classes) {
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
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where " +
                        " (a.subClassId in (" + classIdsString + ") or a.coursePlan.courseLevel.academicClassId in (" + classIdsString + ")) " +
                        " and a.teacher.allowCourseFeedback=true " +
                        " and a.coursePlan.allowCourseFeedback=true "
        ).getResultList();
    }

    public void generateStudentsFeedbackForm(final int academicFeedbackModelId, final String studentProfileFilter) {
        final PersistenceUnit pu = UPA.getPersistenceUnit();
        final AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        final AcademicFeedbackModel academicFeedbackModel = pu.findById(AcademicFeedbackModel.class, academicFeedbackModelId);
        if (academicFeedbackModel == null) {
            return;
        }
        TraceService traceService = TraceService.get();
        try {
            traceService.trace("generateStudentsFeedbackForm", "start generating",
                    "model='" + academicFeedbackModel.getName() + "' for '" + studentProfileFilter + "'",
                    AcademicPerfEvalPlugin.class.getSimpleName(),
                    java.util.logging.Level.INFO
            );
            TraceService.makeSilenced(new VoidAction() {
                                          @Override
                                          public void run() {
                                              List<AcademicFeedbackQuestion> academicFeedbackQuestions = pu.createQuery("Select u from AcademicFeedbackQuestion u where u.parent.modelId=:id")
                                                      .setParameter("id", academicFeedbackModelId)
                                                      .getResultList();

                                              for (AcademicStudent s : a.findStudents(studentProfileFilter, "x.allowCourseFeedback=true")) {
                                                  //allowCourseFeedback
                                                  boolean allowCourseFeedback;
                                                  List<AcademicClass> classes = a.findStudentClasses(s.getId(), true, false);
                                                  int[] classesId = new int[classes.size()];
                                                  for (int i = 0; i < classesId.length; i++) {
                                                      classesId[i] = classes.get(i).getId();
                                                  }
                                                  List<AcademicCourseAssignment> assignements = findEvaluatableAssignments(classesId);
                                                  Set<Integer> existingAssignementIds = pu.createQuery("Select f.course.id from AcademicFeedback f where f.studentId=:studentId")
                                                          .setParameter("studentId", s.getId())
                                                          .getValueSet(0);
                                                  for (AcademicCourseAssignment assignement : assignements) {
                                                      if (!existingAssignementIds.contains(assignement.getId())) {
                                                          if (!"PFE-PFE".equals(assignement.getName())) {
                                                              //AcademicCoursePlan
                                                              //check if feedback is still there:
                                                              AcademicFeedback f = new AcademicFeedback();
                                                              f.setArchived(false);
                                                              f.setCourse(assignement);
                                                              f.setStudent(s);
                                                              f.setModel(academicFeedbackModel);
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
                                                  }
                                              }
                                          }
                                      }
            ).run();
            traceService.trace("generateStudentsFeedbackForm", "generating finished successfully",
                    "model='" + academicFeedbackModel.getName() + "' for '" + studentProfileFilter + "'",
                    AcademicPerfEvalPlugin.class.getSimpleName(),
                    java.util.logging.Level.INFO
            );
        } catch (Exception ex) {
            log.log(java.util.logging.Level.SEVERE, "Error generating StudentsFeedbackForm", ex);
            traceService.trace("generateStudentsFeedbackForm", "generating finished successfully",
                    "model='" + academicFeedbackModel.getName() + "' for '" + studentProfileFilter + "' : " + ex.getMessage(),
                    AcademicPerfEvalPlugin.class.getSimpleName(),
                    java.util.logging.Level.SEVERE
            );

        }

    }
}


