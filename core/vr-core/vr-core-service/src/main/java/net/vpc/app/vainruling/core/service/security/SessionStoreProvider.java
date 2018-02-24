package net.vpc.app.vainruling.core.service.security;

public interface SessionStoreProvider {
    SessionStore resolveSessionStore();
}
