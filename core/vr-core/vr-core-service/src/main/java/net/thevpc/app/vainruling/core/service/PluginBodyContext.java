package net.thevpc.app.vainruling.core.service;

public abstract class PluginBodyContext<T> {
    private T plugin;

    public PluginBodyContext(T plugin) {
        this.plugin = plugin;
    }

    public T getPlugin(){
        return plugin;
    }
}
