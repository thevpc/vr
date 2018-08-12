package net.vpc.app.vr.core.toolbox;

public interface TemplateConsole {
    String askForString(String propName,StringValidator validator,String defaultValue);
}
