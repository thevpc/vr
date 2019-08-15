package net.vpc.app.vainruling.core.service.editor;

import java.util.List;

public interface EntityViewActionDialog extends EntityViewAction {
    void openDialog(String actionId, List<String> itemIds) ;
}
