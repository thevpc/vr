package net.thevpc.app.vainruling.plugins.tasks.service;

import net.thevpc.app.vainruling.core.service.util.VrPlatformUtils;

public class TaskPluginSecurity {
    public static final String PREFIX_RIGHT_CUSTOM_TODO = "Custom.Todo.";
    public static final String[] RIGHTS_TASKS = VrPlatformUtils.getStringArrayConstantsValues(TaskPluginSecurity.class,"RIGHT_*");
}
