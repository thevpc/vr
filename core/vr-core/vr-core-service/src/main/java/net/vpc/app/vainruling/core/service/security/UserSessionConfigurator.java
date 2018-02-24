package net.vpc.app.vainruling.core.service.security;

public interface UserSessionConfigurator {
    void preConfigure(UserToken s);

    void postConfigure(UserToken s);
}
