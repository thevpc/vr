package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudentStage;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

@Service
public class AcademicPluginCompletionProvider implements CompletionProvider {
    @Override
    public List<String> getCompletionLists(int monitorUserId) {
        return Arrays.asList("Contact");
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
                case "Contact": {
                    String categoryName = "Infos Contact";
                    if (objectType == null) {
                        List<AcademicStudent> students = AcademicPlugin.get().findStudents(null, AcademicStudentStage.ATTENDING);
                        for (AcademicStudent student : students) {
                            all.addAll(findCompletions(monitorUserId, category, "AcademicStudent", student, minLevel));
                        }
                    } else {
                        switch (objectType) {
                            case "AcademicStudent": {
                                String objectTypeName = "Eleve Ingenieur";
                                if (objectId != null) {
                                    AcademicStudent c = null;
                                    if (objectId instanceof AcademicStudent) {
                                        c = (AcademicStudent) objectId;
                                    } else {
                                        c = AcademicPlugin.get().findStudent(Convert.toInt(objectId));
                                    }
                                    ValidatorProgressHelper h = new ValidatorProgressHelper();
                                    h.checkNotDefault(c.resolveContact().getFirstName(),"Missing FirstName");
                                    h.checkNotDefault(c.resolveContact().getLastName(),"Missing LastName");
                                    h.checkNotDefault(c.resolveContact().getEmail(),"Missing Email");
                                    h.checkNotDefault(c.resolveContact().getPhone1(),"Missing Phone1");
                                    h.checkNotDefault(c.resolveContact().getCivility(),"Missing Civility");
                                    h.checkNotDefault(c.resolveContact().getGender(),"Missing Gender");
                                    h.checkNotDefault(c.getDepartment(),"Missing Department");
                                    h.checkNotDefault(c.getBaccalaureateClass(),"Missing BaccalaureateClass");
                                    h.checkNotDefault(c.getBaccalaureateScore(),"Missing BaccalaureateScore");
                                    h.checkNotDefault(c.getPreClassType(),"Missing PreClassType");
                                    h.checkNotDefault(c.getPreClass(),"Missing PreClass");
                                    h.checkNotDefault(c.getPreClassRank()>0?c.getPreClassRank():c.getPreClassRank2(),"Missing PreClassRank");
                                    h.checkNotDefault(c.getPreClassRankByProgram(),"Missing PreClassRankByProgram");
                                    h.checkNotDefault(c.getPreClassScore(),"Missing PreClassScore");
                                    h.check(c.getPreClassChoice1() != null || !StringUtils.isEmpty(c.getPreClassChoice1Other()));
                                    h.check(c.getPreClassChoice2() != null || !StringUtils.isEmpty(c.getPreClassChoice2Other()));
                                    h.check(c.getPreClassChoice3() != null || !StringUtils.isEmpty(c.getPreClassChoice3Other()));
                                    if (h.getCompletionPercent() < 100) {
                                        if (Level.SEVERE.intValue() >= minLevel.intValue()) {
                                            all.add(new DefaultCompletionInfo(
                                                    category,
                                                    categoryName,
                                                    c.getId(),
                                                    c.resolveFullName(),
                                                    objectType,
                                                    objectTypeName,
                                                    (float) h.getCompletionPercent(),
                                                    "info Contact manquante",
                                                    Level.SEVERE,
                                                    new String[]{
                                                            c.getDepartment() == null ? null : c.getDepartment().getName(),
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
                                                    Level.SEVERE,
                                                    new String[]{
                                                            c.getDepartment() == null ? null : c.getDepartment().getName(),
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
                                break;
                            }
                        }

                    }
                    break;
                }
            }
        }
        return all;
    }
}
