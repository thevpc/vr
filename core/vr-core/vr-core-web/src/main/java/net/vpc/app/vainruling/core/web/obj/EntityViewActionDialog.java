package net.vpc.app.vainruling.core.web.obj;

import java.util.List;

public interface EntityViewActionDialog extends EntityViewAction {
    void openDialog(String actionId, List<String> itemIds) ;
}
