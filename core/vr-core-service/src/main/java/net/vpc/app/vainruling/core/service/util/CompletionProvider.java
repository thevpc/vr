package net.vpc.app.vainruling.core.service.util;

import java.util.List;
import java.util.logging.Level;

public interface CompletionProvider {
    List<String> getCompletionLists(int monitorUserId);

    List<CompletionInfo> findCompletions(int monitorUserId, String category, String objectType, Object objectId, Level minLevel);
}
