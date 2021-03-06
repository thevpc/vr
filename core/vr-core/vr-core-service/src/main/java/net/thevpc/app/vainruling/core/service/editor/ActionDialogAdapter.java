/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.editor;

import net.thevpc.app.vainruling.core.service.util.PlatformReflector;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;

import net.thevpc.app.vainruling.core.service.util.I18n;
import net.thevpc.app.vainruling.VrEditorActionDialog;
import net.thevpc.app.vainruling.VrEditorActionGoto;
import net.thevpc.app.vainruling.VrEditorActionInvoke;
import net.thevpc.app.vainruling.VrEditorActionProcessor;
import net.thevpc.app.vainruling.VrEditorAction;

/**
 * @author taha.bensalah@gmail.com
 */
public class ActionDialogAdapter {

    private final VrEditorActionProcessor instance;
    private String entityType;
    private boolean confirm;
    private String actionName;
    private String actionMessage;
    private String actionTitle;
    private String label;
    private String description;
    private String icon;

    public ActionDialogAdapter(VrEditorActionProcessor instance) {
        this.instance = instance;
        VrEditorAction a = (VrEditorAction) PlatformReflector.getTargetClass(instance).getAnnotation(VrEditorAction.class);
        entityType = a.entityName();
        if (!(instance instanceof VrEditorActionDialog)
                && !(instance instanceof VrEditorActionInvoke)
                && !(instance instanceof VrEditorActionGoto)) {
            throw new IllegalArgumentException("Unexpected");
        }
        confirm = a.confirm();
        actionName = a.actionName();
        icon = a.actionIcon();
        I18n i18n = I18n.get();
        String simpleName = PlatformReflector.getTargetClass(instance).getSimpleName();
        actionName = actionName.isEmpty() ? PlatformReflector.getTargetClass(instance).getSimpleName() : actionName;
        actionTitle = i18n.get(simpleName + ".title");
        actionMessage = i18n.get(simpleName + ".message");
        description = i18n.get(simpleName + ".description");
        label = i18n.get(simpleName + ".label");
        label = StringUtils.isBlank(label) ? actionName : label;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDialog() {
        return instance instanceof VrEditorActionDialog;
    }

    public boolean isInvoke() {
        return instance instanceof VrEditorActionInvoke;
    }

    public boolean isGoto() {
        return instance instanceof VrEditorActionGoto;
    }

    public String getActionTitle() {
        return actionTitle;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public String getActionName() {
        return actionName;
    }

    public boolean acceptEntity(String entityName) {
        if (StringUtils.isBlank(entityType)) {
            return true;
        }
        return entityType.equals(entityName);
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public String getId() {
        return actionName;
    }

    public boolean isEnabled(String entityType, VrAccessMode mode, Object value) {
        boolean b = instance.isEnabled(getId(), entityType, mode, value);
        if (b) {
            PersistenceUnit e = UPA.getPersistenceUnit();
//            return e.getSecurityManager().isAllowedKey(e.getEntity(entityType), getId());
            return e.getSecurityManager().isAllowedKey(getId());
        }
        return b;
    }

    public void openDialog(List<String> itemIds) {
        ((VrEditorActionDialog) instance).openDialog(getId(), itemIds);
    }

    public ActionParam[] getParams() {
        return ((VrEditorActionInvoke) instance).getParams();
    }

    public ActionDialogResult invoke(String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        return ((VrEditorActionInvoke) instance).invoke(getId(), entityType, obj, selectedIdStrings, args);
    }

    public String[] getCommand(List<String> itemIds) {
        if (instance instanceof VrEditorActionGoto) {
            return ((VrEditorActionGoto) instance).getCommand(getId(), itemIds);
        }
        return new String[0];
    }

}
