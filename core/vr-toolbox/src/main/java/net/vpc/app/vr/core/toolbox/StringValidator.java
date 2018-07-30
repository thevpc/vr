package net.vpc.app.vr.core.toolbox;

@FunctionalInterface
public interface StringValidator {
    boolean validate(String value);
}
