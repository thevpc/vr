package net.vpc.app.vainruling;

import java.util.List;
import net.vpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.vpc.app.vainruling.core.service.editor.ActionParam;

public interface VrEditorActionInvoke extends VrEditorActionProcessor {
    default ActionParam[] getParams() {
        return null;
    }

    ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args);
}
