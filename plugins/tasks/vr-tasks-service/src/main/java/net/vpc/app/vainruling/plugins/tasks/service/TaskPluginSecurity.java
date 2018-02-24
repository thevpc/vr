package net.vpc.app.vainruling.plugins.tasks.service;

import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;

public class TaskPluginSecurity {
    public static final String PREFIX_RIGHT_CUSTOM_TODO = "Custom.Todo.";
    public static final String[] RIGHTS_TASKS = VrPlatformUtils.getStringArrayConstantsValues(TaskPluginSecurity.class,"RIGHT_*");
}
