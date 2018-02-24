package net.vpc.app.vainruling.core.service;

public class PluginBody<T, R extends PluginBodyContext<T>> {
    private R context;

    public R getContext() {
        return context;
    }

    public void setContext(R context) {
        this.context = context;
    }

    public void onInstall() {

    }

    public void onStart() {

    }

    public void onPrepare() {
    }

}
