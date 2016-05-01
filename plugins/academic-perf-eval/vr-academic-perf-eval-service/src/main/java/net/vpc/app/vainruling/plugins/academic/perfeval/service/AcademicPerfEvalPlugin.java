/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service;

import java.util.Collections;
import java.util.List;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.Start;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedback;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackGroup;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackModel;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackQuestion;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackResponse;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

/**
 *
 * @author vpc
 */
@AppPlugin(dependsOn = "academicPlugin", version = "1.0")
public class AcademicPerfEvalPlugin {

    public void saveResponse(AcademicFeedbackResponse r) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        //do some checkings?
        pu.merge(r);
    }

    @Start
    public void onStart() {
        VrApp.getBean(CorePlugin.class).createRight("Custom.Academic.StudentFeedback", "Custom.Academic.StudentFeedback");
        VrApp.getBean(CorePlugin.class).createRight("Custom.Academic.StudentFeedback", "Custom.Academic.TeacherStatFeedback");
    }

    public AcademicFeedback findFeedback(int feedbackId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicFeedback.class, feedbackId);
    }

    public List<AcademicCourseAssignment> findAssignmentsWithFeedbacks(int teacherId, Boolean validated, Boolean archived,Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if(enabled!=null && enabled){
            Boolean ok=(Boolean)VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPerfEvalPlugin.EnableTeacherFeedbacks", null, true);
            if(ok!=null && !ok.booleanValue()){
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
                .setHint("navigationDepth", 3)
                .getEntityList();
    }

    public List<AcademicFeedback> findAssignmentFeedbacks(int academicCourseAssignmentId, Boolean validated, Boolean archived) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedback f where f.courseId= :academicCourseAssignmentId "
                + (validated != null ? (" and f.validated=" + validated) : "")
                + (archived != null ? (" and f.archived=" + archived) : "")
                + " order by f.name"
        )
                .setParameter("academicCourseAssignmentId", academicCourseAssignmentId)
                .setHint("navigationDepth", 3)
                .getEntityList();
    }

    public List<AcademicFeedback> findStudentFeedbacks(int studentId, Boolean validated, Boolean archived,Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if(enabled!=null && enabled){
            Boolean ok=(Boolean)UPA.getContext().invokePrivileged(new Action<Boolean>(){
                @Override
                public Boolean run() {
                    return (Boolean)VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPerfEvalPlugin.EnableStudentFeedbacks", null, true);
                }
                
            });
            if(ok!=null && !ok.booleanValue()){
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
                .setHint("navigationDepth", 3)
                .getEntityList();
    }

    public List<AcademicFeedbackResponse> findStudentFeedbackResponses(int feedbackId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackResponse f where f.feedbackId=:feedbackId")
                .setParameter("feedbackId", feedbackId)
                .getEntityList();
    }

    public List<AcademicFeedbackQuestion> findStudentFeedbackQuestionsByModel(int feedbackModelId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackQuestion f where f.parent.modelId=:feedbackModelId order by f.parentId, f.position")
                .setParameter("feedbackModelId", feedbackModelId)
                .getEntityList();
    }

    public List<AcademicFeedbackResponse> findStudentFeedbackResponsesByGroup(int feedbackId, int groupId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackResponse f where f.feedbackId=:feedbackId  and f.parentId=:parentId")
                .setParameter("parentId", groupId)
                .getEntityList();
    }

    public List<AcademicFeedbackGroup> findStudentFeedbackGroups(int feedbackModelId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedbackGroup f where f.modelId=:modelId order by f.position")
                .setParameter("modelId", feedbackModelId)
                .getEntityList();
    }

    public List<AcademicFeedbackQuestion> findAcademicFeedbackQuestionByGroup(int groupId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicFeedbackQuestion u where u.parentId=:id order by u.position")
                .setParameter("id", groupId)
                .getEntityList();
    }

    public void generatedStudentsFeedbackForm(int academicFeedbackModelId, String studentProfileFilter) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicFeedbackModel academicFeedbackModel = pu.findById(AcademicFeedbackModel.class, academicFeedbackModelId);
        if (academicFeedbackModel == null) {
            return;
        }
        List<AcademicFeedbackQuestion> academicFeedbackQuestions = pu.createQuery("Select u from AcademicFeedbackQuestion u where u.parent.modelId=:id")
                .setParameter("id", academicFeedbackModelId)
                .getEntityList();

        for (AcademicStudent s : a.findStudents(studentProfileFilter)) {
            //allowCourseFeedback
            boolean allowCourseFeedback = pu.createQuery("Select u.allowCourseFeedback from AcademicStudent u where u.id=:id")
                    .setParameter("id", s.getId())
                    .getBoolean();
            if (allowCourseFeedback) {
                List<AcademicClass> classes = a.findStudentClasses(s.getId());
                for (AcademicClass c : classes) {
                    List<AcademicCourseAssignment> assignements = a.findCourseAssignmentsByClass(c.getId());
                    for (AcademicCourseAssignment assignement : assignements) {
                        if (!"PFE-PFE".equals(assignement.getName())) {
                            AcademicTeacher t = assignement.getTeacher();
                            if (t != null) {
                                allowCourseFeedback = pu.createQuery("Select u.allowCourseFeedback from AcademicTeacher u where u.id=:id")
                                        .setParameter("id", t.getId())
                                        .getBoolean();
                                if (allowCourseFeedback) {
                                    //AcademicCoursePlan   
                                    AcademicCoursePlan cp = assignement.getCoursePlan();
                                    if (cp != null) {
                                        allowCourseFeedback = pu.createQuery("Select u.allowCourseFeedback from AcademicCoursePlan u where u.id=:id")
                                                .setParameter("id", cp.getId())
                                                .getBoolean();

                                        if (allowCourseFeedback) {
                                            //check if feedback is still there:
                                            AcademicFeedback f = pu.createQuery("Select f from AcademicFeedback f where f.courseId=:assignementId and f.studentId=:studentId")
                                                    .setParameter("assignementId", assignement.getId())
                                                    .setParameter("studentId", s.getId())
                                                    .getEntity();
                                            if (f == null) {
                                                //okkay now generate it
                                                f = new AcademicFeedback();
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
                    }
                }
            }
        }
    }

}
