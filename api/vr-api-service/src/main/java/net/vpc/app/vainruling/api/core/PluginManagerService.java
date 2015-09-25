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
public interface PluginManagerService {

    public Plugin[] getPlugins() ;

    public ActionInfo[] getEntityActionList(Class entityType, Object obj) ;

    public <T> T invokeEntityAction(Class entityType, String actionName, Object obj,Object[] args) ;

    public boolean isEnabledEntityAction(Class entityType, String actionName, Object obj) ;
}
