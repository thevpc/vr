package net.vpc.app.vainruling.core.service.editor;

import java.util.List;

public interface EntityViewActionInvoke extends EntityViewAction {
    default ActionParam[] getParams() {
        return null;
    }

    ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args);
}
