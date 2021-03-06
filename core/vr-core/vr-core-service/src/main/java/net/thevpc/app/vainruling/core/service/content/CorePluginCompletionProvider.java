package net.thevpc.app.vainruling.core.service.content;

import java.text.SimpleDateFormat;

import net.thevpc.app.vainruling.VrCompletionInfo;
import net.thevpc.app.vainruling.VrCompletionService;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.util.DefaultCompletionInfo;
import net.thevpc.app.vainruling.core.service.util.DefaultCompletionInfoAction;
import net.thevpc.app.vainruling.core.service.util.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Service
public class CorePluginCompletionProvider implements VrCompletionService {

    @Override
    public List<String> getCompletionLists(int monitorUserId) {
        return Arrays.asList("Access");
    }

    @Override
    public List<VrCompletionInfo> findCompletions(int monitorUserId, String category, String objectType, Object objectId, Level minLevel) {
        if (minLevel == null) {
            minLevel = Level.ALL;
        }
        List<VrCompletionInfo> all = new ArrayList<>();
        if (category != null) {
            switch (category) {
                case "Access": {
                    findCompletionsFillAccess(monitorUserId, objectType, objectId, minLevel, all);
                    break;
                }
            }
        }
        return all;
    }

    public void findCompletionsFillAccess(int monitorUserId, String objectType, Object objectId, Level minLevel, List<VrCompletionInfo> all) {
        CorePlugin core = CorePlugin.get();
        if (objectType == null) {
            objectType = "AppUser";
        }
        switch (objectType) {
            case "AppUser": {
                if (minLevel == null || minLevel.intValue() <= Level.SEVERE.intValue()) {
                    if (objectId == null) {
                        objectId = monitorUserId;
                    }
                    AppUser o = core.findUser((Integer) objectId);
                    Map<Date, Integer> s = core.findLatestDayLoginsCount(o.getLogin(), true, false, 30);

                    StringBuilder details = new StringBuilder();
                    details.append("Vos dernières connexion :");
                    details.append("<ul>\n");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    for (Map.Entry<Date, Integer> e : s.entrySet()) {
                        details.append("<li>" + sdf.format(e.getKey()) + " : " + e.getValue() + "</li>\n");
                    }
                    details.append("</ul>\n");
                    all.add(new DefaultCompletionInfo(
                            "Access",
                            "Access",
                            o.getId(),
                            o.getFullName(),
                            objectType,
                            "Utilisateur",
                            (float) 1,
                            "Connexions",
                            details.toString(),
                            Level.SEVERE,
                            new String[]{
                                o.getDepartment() == null ? null : o.getDepartment().getName(),},
                            Arrays.asList(new DefaultCompletionInfoAction(
                                    "corriger",
                                    "",
                                    ""
                            ))
                    ));
                }
                break;
            }
        }
    }
}
