package net.vpc.app.vainruling.core.service;

public interface PlatformSession {
    boolean isValid();
    boolean invalidate();
}
