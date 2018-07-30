package net.vpc.app.vr.core.toolbox;

@FunctionalInterface
public interface PropertyProvider {
    String getProperty(String propertyName);
}
