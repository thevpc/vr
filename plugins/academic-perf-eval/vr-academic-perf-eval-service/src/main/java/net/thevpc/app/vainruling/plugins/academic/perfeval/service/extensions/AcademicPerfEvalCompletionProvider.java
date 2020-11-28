package net.thevpc.app.vainruling.plugins.academic.perfeval.service.extensions;

import net.thevpc.app.vainruling.VrCompletionInfo;
import net.thevpc.app.vainruling.VrCompletionService;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.util.DefaultCompletionInfo;
import net.thevpc.app.vainruling.core.service.util.DefaultCompletionInfoAction;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.common.util.Convert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.perfeval.model.AcademicFeedback;
import net.thevpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;

@Service
public class AcademicPerfEvalCompletionProvider implements VrCompletionService {

    @Override
    public List<String> getCompletionLists(int monitorUserId) {
        return Arrays.asList("AcademicStudent");
    }

    @Override
    public List<VrCompletionInfo> findCompletions(int monitorUserId, String category, String objectType, Object objectId, Level minLevel) {
        if (minLevel == null) {
            minLevel = Level.ALL;
        }
        List<VrCompletionInfo> all = new ArrayList<>();
        if (category == null) {
            for (String cat : getCompletionLists(monitorUserId)) {
                all.addAll(findCompletions(monitorUserId, cat, objectType, objectId, minLevel));
            }
        } else {
            switch (category) {
                case "AcademicStudent": {
                    if (objectType == null) {
                        objectType = "AcademicStudent";
                    }
                    findCompletionsFillContacts(monitorUserId, objectType, objectId, minLevel, all);
                    break;
                }
            }
        }
        return all;
    }

    public void findCompletionsFillContacts(int monitorUserId, String objectType, Object objectId, Level minLevel, List<VrCompletionInfo> all) {
        if (objectType != null) {
            switch (objectType) {
                case "AcademicStudent": {
                    findCompletionsFillContactStudent(monitorUserId, objectId, minLevel, all);
                    break;
                }
            }
        }
    }

    public void findCompletionsFillContactStudent(int monitorUserId, Object objectId, Level minLevel, List<VrCompletionInfo> all) {

        String category = "Contact";
        String categoryName = "Infos Contact";
        String objectType = "AcademicStudent";

        String objectTypeName = "Eleve Ingenieur";
        AcademicPlugin ac = VrApp.getBean(AcademicPlugin.class);
        AcademicPerfEvalPlugin feedback = VrApp.getBean(AcademicPerfEvalPlugin.class);
        if (objectId == null /*&& ac.getCurrentStudent() != null*/) {
            objectId = monitorUserId;//ac.getCurrentStudent().getId();
        }
        if (objectId != null) {
            AcademicStudent c = null;
            if (objectId instanceof AcademicStudent) {
                c = (AcademicStudent) objectId;
            } else {
                c = AcademicPlugin.get().findStudentByUser(Convert.toInt(objectId));
            }
            if (c == null) {
                return;
            }
            boolean sameUser = monitorUserId == CorePlugin.get().getCurrentUserId();
            List<AcademicFeedback> fb = feedback.findStudentFeedbacks(null, c.getId(), false, false, null, null, true);
            if (fb.size() > 0) {
                if (Level.SEVERE.intValue() >= minLevel.intValue()) {
                    StringBuilder details = new StringBuilder();
                    String dept = c.getUser().getDepartment() == null ? "" : c.getUser().getDepartment().getName();
                    String prg = c.getLastClass1() == null ? "" : c.getLastClass1().getProgram() == null ? null : c.getLastClass1().getProgram().getName();
                    String cls = c.getLastClass1() == null ? "" : c.getLastClass1().getName();
                    if (sameUser) {
                        details.append("Vos évaluation sont <span class=\"badge badge-danger\">manquantes</span> :");
                    } else {
                        details.append("Les evaluations de <strong>" + c.resolveFullName() + " (" + dept + " " + prg + " " + cls + ") </strong> sont <span class=\"badge badge-danger\">manquantes</span> :");
                    }
                    details.append("<ul>\n");
                    for (AcademicFeedback item : fb) {
                        details.append("<li>" + item.getName() + "</li>\n");
                    }
                    details.append("</ul>\n");
                    all.add(new DefaultCompletionInfo(
                            category,
                            categoryName,
                            c.getId(),
                            c.resolveFullName(),
                            objectType,
                            objectTypeName,
                            0,
                            "Evaluation",
                            details.toString(),
                            Level.SEVERE,
                            new String[]{
                                dept, prg, cls},
                            Arrays.asList(new DefaultCompletionInfoAction(
                                    "corriger",
                                    "",
                                    ""
                            ))
                    ));
                }
            }
        }
    }

}
