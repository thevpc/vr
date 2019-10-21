package net.vpc.app.vainruling;

import java.util.List;
import java.util.logging.Level;
import net.vpc.app.vainruling.core.service.util.CompletionInfoAction;

public interface VrCompletionInfo {
    String getCategory();

    Object getObjectId();

    String[] getFilters();

    String getFilter(int index);

    String getObjectName();

    String getObjectType();

    float getCompletion();

    String getMessage();
    
    String getContent();

    Level getMessageLevel();

    List<CompletionInfoAction> getActions();

    public String getObjectTypeName();

    public String getCategoryName();
}
