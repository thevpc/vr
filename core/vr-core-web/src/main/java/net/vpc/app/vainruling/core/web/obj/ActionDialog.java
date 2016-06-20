/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;

import java.util.List;

/**
 * @author vpc
 */
public interface ActionDialog {

    boolean isEnabled(Class entityType, EditCtrlMode mode, Object value);

    public void openDialog(String actionId, List<String> itemIds);

    public void invoke(Class entityType, Object obj, Object[] args);

}
