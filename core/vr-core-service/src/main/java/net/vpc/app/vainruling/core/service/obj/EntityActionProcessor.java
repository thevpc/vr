/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.obj;

/**
 *
 * @author vpc
 */
public interface EntityActionProcessor {

    public ActionInfo getInfo();

    public <T> T invoke(Class entityType, Object obj, Object[] args);

    public boolean isEnabled(Class entityType, Object obj);
}
