/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.extensions;

import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedbackSession;
import net.vpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedback;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.vpc.upa.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.extensions.VrMaintenanceAction;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import org.springframework.stereotype.Service;

/**
 * @author taha.bensalah@gmail.com
 */
@Service
public class AcademicPerfEvalMaintenanceAction implements VrMaintenanceAction{

    private static final Logger log = Logger.getLogger(AcademicPerfEvalMaintenanceAction.class.getName());
    private static final String[] VALID_RESPONSES_ARRAY = {"1", "2", "3", "4"};
    private static final Set<String> VALID_RESPONSES = new HashSet(Arrays.asList(VALID_RESPONSES_ARRAY));
    @Autowired
    private AcademicPlugin academic;
    @Autowired
    private AcademicPerfEvalPlugin perf;

    @Override
    public String getName() {
        return "Evaluations Maintenance";
    }

    @Override
    public void run() {
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
        for (AcademicFeedback academicFeedback : perf.findFeedbacks(null, null, null, null, null, null, null, null, null, null, null)) {
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

}
