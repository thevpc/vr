package net.vpc.app.vainruling.plugins.calendars.service;

import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;

public class CalendarsPluginSecurity {
    public static final String RIGHT_CUSTOM_EDUCATION_MY_PLANNING = "Custom.Education.MyPlanning";
    public static final String RIGHT_CUSTOM_EDUCATION_USER_CALENDARS = "Custom.Education.UserCalendars";
    public static final String[] RIGHTS_ACADEMIC = VrPlatformUtils.getStringArrayConstantsValues(CalendarsPluginSecurity.class,"RIGHT_*");
}
