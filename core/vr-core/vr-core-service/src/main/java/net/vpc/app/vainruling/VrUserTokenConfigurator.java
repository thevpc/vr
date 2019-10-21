package net.vpc.app.vainruling;

import net.vpc.app.vainruling.core.service.security.UserToken;

public interface VrUserTokenConfigurator {
    void preConfigure(UserToken s);

    void postConfigure(UserToken s);
}
