package net.vpc.app.vainruling.core.service;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginBody<T, R extends PluginBodyContext<T>> {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(PluginBody.class.getName());

    private R context;
    private PluginBodyPhase phase = PluginBodyPhase.INIT;
    private Throwable lastError;

    public R getContext() {
        return context;
    }

    public void setContext(R context) {
        this.context = context;
    }

    public void start() {
        switch (phase) {
//            case INIT:
            case START_DIRTY:
            case STARTED:
//            case INSTALLED:
            case INSTALL_DIRTY: {
                return;
            }
        }
        try {
            onStart();
            phase = PluginBodyPhase.STARTED;
        } catch (Throwable ex) {
            lastError = ex;
            phase = PluginBodyPhase.START_DIRTY;
            log.log(Level.SEVERE, "Unable to Start " + getClass().getSimpleName(), ex);
        }
    }

    public void install() {
        switch (phase) {
//            case INIT:
            case START_DIRTY:
//            case STARTED:
            case INSTALLED:
            case INSTALL_DIRTY: {
                return;
            }
        }
        try {
            onInstall();
            phase = PluginBodyPhase.INSTALLED;
        } catch (Throwable ex) {
            lastError = ex;
            phase = PluginBodyPhase.INSTALL_DIRTY;
            log.log(Level.SEVERE, "Unable to Install " + getClass().getSimpleName(), ex);
        }
    }

    public PluginBodyPhase getPhase() {
        return phase;
    }

    public Throwable getLastError() {
        return lastError;
    }

    protected void onInstall() {

    }

    protected void onStart() {

    }

}
