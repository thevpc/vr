package net.vpc.app.vainruling.core.service.editor;

import java.util.List;

public interface EntityViewActionGoto extends EntityViewAction {
    String[] getCommand(String actionId, List<String> itemIds) ;
}
