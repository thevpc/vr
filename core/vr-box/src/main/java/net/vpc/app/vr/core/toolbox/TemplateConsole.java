package net.vpc.app.vr.core.toolbox;

public interface TemplateConsole {
    void println(String message, Object... params);
    String ask(String propName, String propertyTitle, StringValidator validator, String defaultValue);
}
