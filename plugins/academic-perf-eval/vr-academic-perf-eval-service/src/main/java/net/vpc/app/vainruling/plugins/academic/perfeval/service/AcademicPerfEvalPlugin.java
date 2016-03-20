/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service;

import java.util.List;
import net.vpc.app.vainruling.api.AppPlugin;
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

    public AcademicFeedback findFeedback(int feedbackId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicFeedback.class, feedbackId);
    }

    public List<AcademicFeedback> findStudentFeedbacks(int studentId, Boolean validated, Boolean archived) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select f from AcademicFeedback f where f.studentId= :studentId "
                + (validated != null ? (" and f.validated=" + validated) : "")
                + (archived != null ? (" and f.archived=" + archived) : "")
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
