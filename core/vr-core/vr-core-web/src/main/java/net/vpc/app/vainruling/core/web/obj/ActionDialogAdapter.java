/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.service.util.PlatformReflector;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.AccessMode;
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
        actionName = StringUtils.isEmpty(actionName) ? (
                entityType.getSimpleName()+".Action."+
                PlatformReflector.getTargetClass(instance).getSimpleName()
        ) : actionName;
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

    public boolean isEnabled(Class entityType, AccessMode mode, Object value) {
        boolean b = instance.isEnabled(getId(), entityType, mode, value);
        if (b) {
            PersistenceUnit e = UPA.getPersistenceUnit();
//            return e.getSecurityManager().isAllowedKey(e.getEntity(entityType), getId());
            return e.getSecurityManager().isAllowedKey(getId());
        }
        return b;
    }

    public void openDialog(List<String> itemIds) {
        instance.openDialog(getId(), itemIds);
    }

    public ActionDialogResult invoke(Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        return instance.invoke(getId(), entityType, obj, selectedIdStrings, args);
    }

}
