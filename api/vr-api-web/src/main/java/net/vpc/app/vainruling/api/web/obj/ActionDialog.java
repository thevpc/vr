/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj;

import java.util.List;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;

/**
 *
 * @author vpc
 */
public interface ActionDialog {

    boolean isEnabled(Class entityType, EditCtrlMode mode,Object value);

    public void openDialog(String actionId, List<String> itemIds);

    public void invoke(Class entityType, Object obj, Object[] args);

}
