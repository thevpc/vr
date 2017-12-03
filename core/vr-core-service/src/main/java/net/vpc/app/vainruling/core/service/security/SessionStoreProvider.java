package net.vpc.app.vainruling.core.service.security;

import net.vpc.app.vainruling.core.service.PlatformSession;

import java.util.Map;

public interface SessionStoreProvider {
    SessionStore resolveSessionStore();
}
