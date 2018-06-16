/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.upa.AccessMode;

/**
 * @author taha.bensalah@gmail.com
 */
public interface EntityViewAction {

    default boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return true;
    }



}
