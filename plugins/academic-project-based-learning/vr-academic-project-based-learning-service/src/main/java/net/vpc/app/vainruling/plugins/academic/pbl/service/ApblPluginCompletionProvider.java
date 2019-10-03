package net.vpc.app.vainruling.plugins.academic.pbl.service;

import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.common.util.Convert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.ApblStudentInfo;
import net.vpc.app.vainruling.plugins.academic.pbl.model.ApblSession;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;

@Service
public class ApblPluginCompletionProvider implements CompletionProvider {

    @Override
    public List<String> getCompletionLists(int monitorUserId) {
        return Arrays.asList("AcademicStudent");
    }

    @Override
    public List<CompletionInfo> findCompletions(int monitorUserId, String category, String objectType, Object objectId, Level minLevel) {
        if (minLevel == null) {
            minLevel = Level.ALL;
        }
        List<CompletionInfo> all = new ArrayList<>();
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

    public void findCompletionsFillContacts(int monitorUserId, String objectType, Object objectId, Level minLevel, List<CompletionInfo> all) {
        if (objectType != null) {
            switch (objectType) {
                case "AcademicStudent": {
                    findCompletionsFillContactStudent(monitorUserId, objectId, minLevel, all);
                    break;
                }
            }
        }
    }

    public void findCompletionsFillContactTeacher(int monitorUserId, Object objectId, Level minLevel, List<CompletionInfo> all) {

    }

    public void findCompletionsFillContactStudent(int monitorUserId, Object objectId, Level minLevel, List<CompletionInfo> all) {

        String category = "Contact";
        String categoryName = "Infos Contact";
        String objectType = "AcademicStudent";

        String objectTypeName = "Eleve Ingenieur";
        AcademicPlugin ac = VrApp.getBean(AcademicPlugin.class);
        ApblPlugin ap = VrApp.getBean(ApblPlugin.class);
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
            boolean sameUser = monitorUserId == c.getUser().getId();
            int actualStudent = c.getId();
            final CorePlugin core = CorePlugin.get();
            List<ApblSession> os = ap.findOpenSessions();
            for (ApblSession o : os) {
                final String memberProfiles = o.getMemberProfiles();
                if (core.isUserMatchesProfileFilter(c.getUser().getId(), memberProfiles)) {
                    ApblStudentInfo ff = UPA.getContext().invokePrivileged(new Action<ApblStudentInfo>() {
                        @Override
                        public ApblStudentInfo run() {
                            return ap.findStudentInfos(new int[]{o.getId()}, actualStudent);
                        }
                    });
                    if (ff != null) {
                        List<String> messages = new ArrayList<>();
                        if (ff.isErrNoCoach()) {
                            messages.add("Aucun Coach n'est défini");
                        }
                        if (ff.isErrNoProject()) {
                            messages.add("Aucun Sujet n'est défini");
                        }
                        if (ff.isErrNoTeam()) {
                            messages.add("Aucune Equipé n'est définie");
                        }
                        if (ff.isErrTooManyTeams()) {
                            messages.add("Vous faites partie de plusieurs équipes à la fois :" + ff.getTeams());
                        }
                        if (Level.SEVERE.intValue() >= minLevel.intValue() && !messages.isEmpty()) {
                            StringBuilder details = new StringBuilder();
                            String dept = c.getUser().getDepartment() == null ? "" : c.getUser().getDepartment().getName();
                            String prg = c.getLastClass1() == null ? "" : c.getLastClass1().getProgram() == null ? null : c.getLastClass1().getProgram().getName();
                            String cls = c.getLastClass1() == null ? "" : c.getLastClass1().getName();
                            if (sameUser) {
                                details.append("Vos informations de <strong>" + o.getName() + "</strong>"
                                        + " sont <span class=\"badge badge-danger\">incorrectes</span> :");
                            } else {
                                details.append("Les informations de <strong>" + o.getName() + "</strong>"
                                        + " pour <strong>" + c.resolveFullName() + " (" + dept + " " + prg + " " + cls + ") </strong> sont <span class=\"badge badge-danger\">incorrectes</span> :");
                            }
                            details.append("<ul>\n");
                            for (String object : messages) {
                                details.append("<li>" + object + "</li>\n");
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
                                    "Projet Innovation",
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
    }

}
