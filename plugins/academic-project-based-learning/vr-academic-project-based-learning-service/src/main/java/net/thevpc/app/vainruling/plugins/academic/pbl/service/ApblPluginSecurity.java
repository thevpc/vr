package net.thevpc.app.vainruling.plugins.academic.pbl.service;

import net.thevpc.app.vainruling.core.service.util.VrPlatformUtils;

public class ApblPluginSecurity {

    public static final String RIGHT_CUSTOM_EDUCATION_APBL_APPLY_LOAD = "Custom.Education.Apbl.ApplyLoad";
    public static final String[] RIGHTS_ACADEMIC = VrPlatformUtils.getStringArrayConstantsValues(ApblPluginSecurity.class,"RIGHT_*");
}
