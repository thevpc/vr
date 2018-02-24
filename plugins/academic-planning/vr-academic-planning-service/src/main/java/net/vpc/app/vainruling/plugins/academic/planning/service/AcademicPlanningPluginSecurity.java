package net.vpc.app.vainruling.plugins.academic.planning.service;

import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;

public class AcademicPlanningPluginSecurity {
    public static final String RIGHT_CUSTOM_EDUCATION_TEACHER_PLANNING = "Custom.Education.TeacherPlanning";
    public static final String RIGHT_CUSTOM_EDUCATION_CLASS_PLANNING = "Custom.Education.ClassPlanning";
    public static final String[] RIGHTS_ACADEMIC_PLANNING = VrPlatformUtils.getStringArrayConstantsValues(AcademicPlanningPluginSecurity.class,"RIGHT_*");
}
