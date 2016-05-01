/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.core;

/**
 *
 * @author vpc
 */
public class PluginActionEvent {
    private Class entityType;
    private String actionName;
    private Object object;
    private Object[] arguments;

    public PluginActionEvent(Class entityType, String actionName, Object object, Object[] arguments) {
        this.entityType = entityType;
        this.actionName = actionName;
        this.object = object;
        this.arguments = arguments;
    }

    public Class getEntityType() {
        return entityType;
    }

    public String getActionName() {
        return actionName;
    }

    public Object getObject() {
        return object;
    }

    public Object[] getArguments() {
        return arguments;
    }
    
}
