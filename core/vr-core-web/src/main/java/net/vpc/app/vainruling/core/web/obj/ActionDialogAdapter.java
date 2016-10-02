/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.service.util.PlatformReflector;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class ActionDialogAdapter {

    private final ActionDialog instance;
    Class entityType;
    boolean dialog;
    boolean confirm;
    String actionName;
    String label;
    String style;

    public ActionDialogAdapter(ActionDialog instance) {
        this.instance = instance;
        EntityAction a = (EntityAction) PlatformReflector.getTargetClass(instance).getAnnotation(EntityAction.class);
        entityType = a.entityType();
        dialog = a.dialog();
        confirm = a.confirm();
        actionName = a.actionName();
        style = a.actionLabel();
        actionName = StringUtils.isEmpty(actionName) ? PlatformReflector.getTargetClass(instance).getSimpleName() : actionName;
        label = a.actionLabel();
        label = StringUtils.isEmpty(label) ? actionName : label;
    }

    public boolean acceptEntity(String entityName) {
        if (Void.class.equals(entityType)) {
            return true;
        }
        return entityType.getSimpleName().equals(entityName);
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
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

    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        boolean b = instance.isEnabled(entityType, mode, value);
        if (b) {
            PersistenceUnit e = UPA.getPersistenceUnit();
            return e.getSecurityManager().isAllowedKey(e.getEntity(entityType), getId());
        }
        return b;
    }

    public void openDialog(String actionId, List<String> itemIds) {
        instance.openDialog(actionId, itemIds);
    }

    public void invoke(Class entityType, Object obj, List<String> selectedIdStrings,Object[] args) {
        instance.invoke(entityType, obj, selectedIdStrings, args);
    }

}
