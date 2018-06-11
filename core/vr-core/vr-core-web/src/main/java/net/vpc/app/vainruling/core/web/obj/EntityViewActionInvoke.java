package net.vpc.app.vainruling.core.web.obj;

import java.util.List;

public interface EntityViewActionInvoke extends EntityViewAction {
    default ActionParam[] getParams() {
        return null;
    }

    ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args);
}
