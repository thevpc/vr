/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.upa.AccessMode;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public interface ActionDialog {

    default boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return true;
    }

    default void openDialog(String actionId, List<String> itemIds) {

    }

    default ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        return new ActionDialogResult(ActionDialogResultPostProcess.VOID);
    }

}
