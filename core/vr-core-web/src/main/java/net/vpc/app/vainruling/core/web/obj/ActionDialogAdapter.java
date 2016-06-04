/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import java.util.List;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.service.util.Reflector;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

/**
 *
 * @author vpc
 */
public class ActionDialogAdapter {

    private final ActionDialog instance;
    Class entityType;
    boolean dialog;
    String actionName;
    String label;
    String style;

    public ActionDialogAdapter(ActionDialog instance) {
        this.instance = instance;
        EntityAction a = (EntityAction) Reflector.getTargetClass(instance).getAnnotation(EntityAction.class);
        entityType = a.entityType();
        dialog = a.dialog();
        actionName = a.actionName();
        style = a.actionLabel();
        actionName = StringUtils.isEmpty(actionName) ? Reflector.getTargetClass(instance).getSimpleName() : actionName;
        label = a.actionLabel();
        label = StringUtils.isEmpty(label) ? actionName : label;
    }

    public boolean acceptEntity(String entityName) {
        if (Void.class.equals(entityType)) {
            return true;
        }
        return entityType.getSimpleName().equals(entityName);
    }

    public boolean isDialog() {
        return dialog;
    }

    public String getLabel() {
        return label;
    }

    public String getStyle() {
        return style;
    }

    public String getId() {
        return actionName;
    }

    public boolean isEnabled(Class entityType, EditCtrlMode mode,Object value) {
        boolean b = instance.isEnabled(entityType, mode,value);
        if (b) {
            PersistenceUnit e = UPA.getPersistenceUnit();
            return e.getSecurityManager().isAllowedKey(e.getEntity(entityType), getId());
        }
        return b;
    }

    public void openDialog(String actionId, List<String> itemIds) {
        instance.openDialog(actionId, itemIds);
    }

    public void invoke(Class entityType, Object obj, Object[] args) {
        instance.invoke(entityType, obj, args);
    }

}
