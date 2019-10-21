package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.plugins.academic.model.internship.AcademicInternshipInfo;
import net.vpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternship;
import net.vpc.upa.Entity;
import net.vpc.upa.UPA;
import net.vpc.app.vainruling.VrCompletionService;
import net.vpc.app.vainruling.VrCompletionInfo;

@Service
public class AcademicPluginCompletionProvider implements VrCompletionService {

    @Override
    public List<String> getCompletionLists(int monitorUserId) {
        return Arrays.asList("Contact", "Internship");
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
                case "Contact": {
                    findCompletionsFillContacts(monitorUserId, objectType, objectId, minLevel, all);
                    break;
                }
                case "Internship": {
                    findCompletionsFillPFE(monitorUserId, objectType, objectId, minLevel, all);
                    break;
                }
            }
        }
        return all;
    }

    public void findCompletionsFillPFE(int monitorUserId, String objectType, Object objectId, Level minLevel, List<VrCompletionInfo> all) {
        CorePlugin core = CorePlugin.get();
        AcademicPlugin aca = AcademicPlugin.get();
        if (objectType != null) {
            switch (objectType) {
                case "AcademicStudent": {
                    findCompletionsFillPFEStudent(monitorUserId, objectId, minLevel, all);
                    break;
                }
            }
        } else {
//            if (core.isCurrentSessionAdmin()) {
//                List<AcademicStudent> students = UPA.getContext().invokePrivileged(new Action<List<AcademicStudent>>() {
//                    @Override
//                    public List<AcademicStudent> run() {
//                        return AcademicPlugin.get().findStudents(null, AcademicStudentStage.ATTENDING);
//                    }
//
//                });
//
//                for (AcademicStudent student : students) {
//                    all.addAll(findCompletions(monitorUserId, "Internship", "AcademicStudent", student, minLevel));
//                }
//                List<AcademicTeacher> teachers = UPA.getContext().invokePrivileged(new Action<List<AcademicTeacher>>() {
//                    @Override
//                    public List<AcademicTeacher> run() {
//                        return AcademicPlugin.get().findTeachers();
//                    }
//
//                });
//
//                for (AcademicTeacher teacher : teachers) {
//                    all.addAll(findCompletions(monitorUserId, "Contact", "AcademicTeacher", teacher, minLevel));
//                }
//            }
        }
    }

    public void findCompletionsFillContacts(int monitorUserId, String objectType, Object objectId, Level minLevel, List<VrCompletionInfo> all) {
        findCompletionsFillContactStudent(monitorUserId, objectId, minLevel, all);
        if (objectType == null || objectType.equals("AcademicStudent")) {
            findCompletionsFillPFEStudent(monitorUserId, objectId, minLevel, all);
        }
    }

    public void findCompletionsFillContactTeacher(int monitorUserId, Object objectId, Level minLevel, List<VrCompletionInfo> all) {

    }

    public void findCompletionsFillPFEStudent(int monitorUserId, Object objectId, Level minLevel, List<VrCompletionInfo> all) {
        AcademicPlugin ac = AcademicPlugin.get();
        if (objectId == null) {
            objectId = monitorUserId;
        }
        AcademicStudent c = ac.findStudentByUser(monitorUserId);
        if (c == null) {
            return;
        }
        String category = "Internship";
        String categoryName = "Infos Contact";
        String objectType = "AppStudent";
        String objectTypeName = "Eleve Ingenieur";
        boolean sameUser = false;
        List<AcademicInternship> internships = ac.findActualInternshipsByStudent(c.getId());
        for (AcademicInternship internship : internships) {
            List<String> errors = AcademicInternshipInfo.checkErrors(internship);
            if (!errors.isEmpty()) {
                StringBuilder details = new StringBuilder();
                String dept = c.getUser().getDepartment() == null ? null : c.getUser().getDepartment().getName();
                String prg = c.getLastClass1() == null ? null : c.getLastClass1().getProgram() == null ? null : c.getLastClass1().getProgram().getName();
                String cls = c.getLastClass1() == null ? null : c.getLastClass1().getName();
                if (sameUser) {
                    details.append("Vos informations de PFE sont <span class=\"badge badge-danger\">manquantes</span> :");
                } else {
                    details.append("Les informations de PFE <strong>" + c.resolveFullName() + " (" + dept + " " + prg + " " + cls + ") </strong> sont <span class=\"badge badge-danger\">manquantes</span> :");
                }
                details.append("<ul>\n");
                for (String object : errors) {
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
                        "Info PFE manquante",
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

    public void findCompletionsFillContactStudent(int monitorUserId, Object objectId, Level minLevel, List<VrCompletionInfo> all) {

        String category = "Contact";
        String categoryName = "Infos Contact";
        String objectType = "AcademicStudent";

        String objectTypeName = "Eleve Ingenieur";
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
            if (c != null) {
                boolean sameUser = monitorUserId == c.getUser().getId();
                ValidatorProgressHelper h = new ValidatorProgressHelper();
                Entity userEntity = UPA.getPersistenceUnit().getEntity("AppUser");
                Entity studentEntity = UPA.getPersistenceUnit().getEntity("AcademicStudent");
                h.checkNotDefault(c.getUser().getFirstName(), userEntity.getField("firstName").getTitle());
                h.checkNotDefault(c.getUser().getLastName(), userEntity.getField("lastName").getTitle());
                h.checkNotDefault(c.getUser().getEmail(), userEntity.getField("email").getTitle());
                h.checkNotDefault(c.getUser().getPhone1(), userEntity.getField("phone1").getTitle());
                h.checkNotDefault(c.getUser().getCivility(), userEntity.getField("civility").getTitle());
                h.checkNotDefault(c.getUser().getGender(), userEntity.getField("gender").getTitle());
                h.checkNotDefault(c.getUser().getDepartment(), userEntity.getField("department").getTitle());
                h.checkNotDefault(c.getBaccalaureateClass(), studentEntity.getField("baccalaureateClass").getTitle());
                h.checkNotDefault(c.getBaccalaureateScore(), studentEntity.getField("baccalaureateScore").getTitle());
                h.checkNotDefault(c.getPreClassType(), studentEntity.getField("preClassType").getTitle());
                h.checkNotDefault(c.getPreClass(), studentEntity.getField("preClass").getTitle());
                h.checkNotDefault(CorePlugin.get().existsUserPhoto(c.getUser().getId()), "photo");
                if (sameUser) {
                    h.checkNotDefault(c.getPreClassRank() > 0 ? c.getPreClassRank() : c.getPreClassRank2(), studentEntity.getField("preClassRank").getTitle());
                    h.checkNotDefault(c.getPreClassRankByProgram(), studentEntity.getField("preClassRankByProgram").getTitle());
                    h.checkNotDefault(c.getPreClassScore(), studentEntity.getField("preClassScore").getTitle());
                    h.check(c.getPreClassChoice1() != null || !StringUtils.isBlank(c.getPreClassChoice1Other()));
                    h.check(c.getPreClassChoice2() != null || !StringUtils.isBlank(c.getPreClassChoice2Other()));
                    h.check(c.getPreClassChoice3() != null || !StringUtils.isBlank(c.getPreClassChoice3Other()));
                }
                if (h.getCompletionPercent() < 100) {
                    if (Level.SEVERE.intValue() >= minLevel.intValue()) {
                        StringBuilder details = new StringBuilder();
                        String dept = c.getUser().getDepartment() == null ? null : c.getUser().getDepartment().getName();
                        String prg = c.getLastClass1() == null ? null : c.getLastClass1().getProgram() == null ? null : c.getLastClass1().getProgram().getName();
                        String cls = c.getLastClass1() == null ? null : c.getLastClass1().getName();
                        if (sameUser) {
                            details.append("Vos informations de contact sont <span class=\"badge badge-danger\">manquantes</span> :");
                        } else {
                            details.append("Les informations de <strong>" + c.resolveFullName() + " (" + dept + " " + prg + " " + cls + ") </strong> sont <span class=\"badge badge-danger\">manquantes</span> :");
                        }
                        details.append("<ul>\n");
                        for (String object : h.getErrorMessages()) {
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
                                (float) h.getCompletionPercent(),
                                "Info Contact manquante",
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
                } else {
                    if (Level.FINEST.intValue() >= minLevel.intValue()) {
                        all.add(new DefaultCompletionInfo(
                                category,
                                categoryName,
                                c.getId(),
                                c.resolveFullName(),
                                objectType,
                                objectTypeName,
                                (float) h.getCompletionPercent(),
                                "info Contact manquante",
                                "Erreur inconnue",
                                Level.SEVERE,
                                new String[]{
                                    c.getUser().getDepartment() == null ? null : c.getUser().getDepartment().getName(),
                                    c.getLastClass1() == null ? null : c.getLastClass1().getProgram() == null ? null : c.getLastClass1().getProgram().getName(),
                                    c.getLastClass1() == null ? null : c.getLastClass1().getName()
                                },
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
