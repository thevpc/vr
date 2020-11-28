package net.thevpc.app.vainruling;

import net.thevpc.app.vainruling.core.service.security.UserToken;

public interface VrUserTokenConfigurator {
    void preConfigure(UserToken s);

    void postConfigure(UserToken s);
}
