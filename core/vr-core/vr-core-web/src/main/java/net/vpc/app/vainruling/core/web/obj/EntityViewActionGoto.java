package net.vpc.app.vainruling.core.web.obj;

import java.util.List;

public interface EntityViewActionGoto extends EntityViewAction {
    String[] getCommand(String actionId, List<String> itemIds) ;
}
