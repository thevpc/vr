package net.vpc.app.vainruling.core.service.security;

public interface UserSessionConfigurator {
    void preConfigure(UserSession s);

    void postConfigure(UserSession s);
}
