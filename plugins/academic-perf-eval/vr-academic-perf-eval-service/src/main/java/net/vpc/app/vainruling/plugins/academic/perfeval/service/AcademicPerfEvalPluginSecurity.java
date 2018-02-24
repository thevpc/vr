package net.vpc.app.vainruling.plugins.academic.perfeval.service;

import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;

public class AcademicPerfEvalPluginSecurity {
    public static final String RIGHT_CUSTOM_ACADEMIC_STUDENT_FEEDBACK_LIST = "Custom.Academic.StudentFeedbackList";
    public static final String RIGHT_CUSTOM_ACADEMIC_STUDENT_FEEDBACK_EXAMPLE = "Custom.Academic.StudentFeedbackExample";
    public static final String[] RIGHTS_ACADEMIC_PERF_EVAL = VrPlatformUtils.getStringArrayConstantsValues(AcademicPerfEvalPluginSecurity.class,"RIGHT_*");
}
