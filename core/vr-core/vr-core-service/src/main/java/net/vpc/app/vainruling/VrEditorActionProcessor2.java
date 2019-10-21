/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling;

import net.vpc.app.vainruling.core.service.editor.ActionInfo;

/**
 * @author taha.bensalah@gmail.com
 */
public interface VrEditorActionProcessor2 {

    ActionInfo getInfo();

    <T> T invoke(Class entityType, Object obj, Object[] args);

    boolean isEnabled(Class entityType, Object obj);
}
