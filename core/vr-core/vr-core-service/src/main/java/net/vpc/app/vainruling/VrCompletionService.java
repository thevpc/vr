package net.vpc.app.vainruling;

import java.util.List;
import java.util.logging.Level;

public interface VrCompletionService {

    List<String> getCompletionLists(int monitorUserId);

    List<VrCompletionInfo> findCompletions(int monitorUserId, String category, String objectType, Object objectId, Level minLevel);
}
