package net.vpc.app.vr.core.toolbox;

public interface IOTemplater {
    String askForString(String propName,StringValidator validator,String defaultValue);
}
