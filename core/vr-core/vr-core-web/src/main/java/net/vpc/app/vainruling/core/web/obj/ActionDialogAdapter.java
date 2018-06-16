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

import net.vpc.app.vainruling.core.service.util.I18n;

/**
 * @author taha.bensalah@gmail.com
 */
public class ActionDialogAdapter {

    private final EntityViewAction instance;
    private Class entityType;
    private boolean confirm;
    private String actionName;
    private String actionMessage;
    private String actionTitle;
    private String label;
    private String description;
    private String style;

    public ActionDialogAdapter(EntityViewAction instance) {
        this.instance = instance;
        EntityAction a = (EntityAction) PlatformReflector.getTargetClass(instance).getAnnotation(EntityAction.class);
        entityType = a.entityType();
        if (!(instance instanceof EntityViewActionDialog) 
                && !(instance instanceof EntityViewActionInvoke)
                && !(instance instanceof EntityViewActionGoto)
                ) {
            throw new IllegalArgumentException("Unexpected");
        }
        confirm = a.confirm();
        actionName = a.actionName();
        style = a.actionStyle();
        I18n i18n = I18n.get();
        String simpleName = PlatformReflector.getTargetClass(instance).getSimpleName();
        actionName = actionName.isEmpty() ? PlatformReflector.getTargetClass(instance).getSimpleName() : actionName;
        actionTitle = i18n.get(simpleName + ".title");
        actionMessage = i18n.get(simpleName + ".message");
        description = i18n.get(simpleName + ".description");
        label = i18n.get(simpleName + ".label");
        label = StringUtils.isEmpty(label) ? actionName : label;
        
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    

    public boolean isDialog() {
        return instance instanceof EntityViewActionDialog;
    }

    public boolean isInvoke() {
        return instance instanceof EntityViewActionInvoke;
    }
    
    public boolean isGoto() {
        return instance instanceof EntityViewActionGoto;
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
        ((EntityViewActionDialog) instance).openDialog(getId(), itemIds);
    }

    public ActionParam[] getParams() {
        return ((EntityViewActionInvoke) instance).getParams();
    }

    public ActionDialogResult invoke(Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        return ((EntityViewActionInvoke) instance).invoke(getId(), entityType, obj, selectedIdStrings, args);
    }

    public String[] getCommand(List<String> itemIds) {
        if(instance instanceof EntityViewActionGoto){
            return ((EntityViewActionGoto)instance).getCommand(getId(), itemIds);
        }
        return new String[0];
    }

}
