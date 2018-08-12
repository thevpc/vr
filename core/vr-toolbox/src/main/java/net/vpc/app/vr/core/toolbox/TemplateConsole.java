package net.vpc.app.vr.core.toolbox;

public interface TemplateConsole {
    void println(String message);
    String ask(String propName, StringValidator validator, String defaultValue);
}
