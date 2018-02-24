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

    boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value);

    void openDialog(String actionId, List<String> itemIds);

    ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args);

}
