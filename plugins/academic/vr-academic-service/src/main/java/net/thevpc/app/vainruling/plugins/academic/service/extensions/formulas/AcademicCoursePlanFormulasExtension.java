/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions.formulas;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.upa.CustomFormulaContext;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.NamedFormula;

/**
 *
 * @author vpc
 */
@Callback
public class AcademicCoursePlanFormulasExtension {

    @NamedFormula
    public String academicCoursePlan_teachers_Formula(CustomFormulaContext ctx) {
        AcademicCoursePlan d = (AcademicCoursePlan) ctx.reloadUpdateObject();
        AcademicPlugin t = VrApp.getBean(AcademicPlugin.class);
        return t.findAssignments(null,d.getId(), null, null, null, null,null)
                .stream().map(x -> x.getTeacher() == null ? null : x.getTeacher().getUser().getFullName())
                .filter(x -> x != null).distinct().sorted().collect(Collectors.joining(", "));
    }

    @NamedFormula
    public String academicCoursePlan_validationErrors_Formula(CustomFormulaContext ctx) {
        AcademicCoursePlan d = (AcademicCoursePlan) ctx.reloadUpdateObject();
        AcademicPlugin t = VrApp.getBean(AcademicPlugin.class);
        List<AcademicCourseAssignment> assignments = t.findAssignments(null,d.getId(), null, null, null, null,null);
        Set<String> errors = new TreeSet<String>();

        if (d.getValueC() < 0) {
            errors.add("Course Error : C < 0");
        } else if (d.getValueC() > 0 && d.getGroupCountC() == 0) {
            errors.add("Course Error : Groupe(C) = 0");
        } else if (d.getGroupCountC() < 0) {
            errors.add("Course Error : Groupe(C) < 0");
        }

        if (d.getValueTD() < 0) {
            errors.add("Course TD < 0");
        } else if (d.getValueTD() > 0 && d.getGroupCountTD() == 0) {
            errors.add("Course Group(TD) = 0");
        } else if (d.getGroupCountTD() < 0) {
            errors.add("Course Group(TD) < 0");
        }

        if (d.getValueTP() < 0) {
            errors.add("Course TP < 0");
        } else if (d.getValueTP() > 0 && d.getGroupCountTP() == 0) {
            errors.add("Course Group(TP) = 0");
        } else if (d.getGroupCountTP() < 0) {
            errors.add("Course Group(TP) < 0");
        }

        if (d.getValuePM() < 0) {
            errors.add("Course PM < 0");
        } else if (d.getValuePM() > 0 && d.getGroupCountPM() == 0) {
            errors.add("Course Group(PM) = 0");
        } else if (d.getGroupCountPM() < 0) {
            errors.add("Course Group(PM) < 0");
        }

        double c = 0;
        double td = 0;
        double tp = 0;
        double pm = 0;

        double t_c = 0;
        double t_td = 0;
        double t_tp = 0;
        double t_pm = 0;

        double u_c = 0;
        double u_td = 0;
        double u_tp = 0;
        double u_pm = 0;
        int zeroAssignmentsCount = 0;
        int noTeacherAssignementsCount = 0;

        for (AcademicCourseAssignment assignment : assignments) {
            double ac = assignment.getValueC();
            double atd = assignment.getValueTD();
            double atp = assignment.getValueTP();
            double apm = assignment.getValuePM();
            double g = assignment.getGroupCount();
            if (g <= 0) {
                errors.add("Assignement group<=0");
            }
            if (ac < 0) {
                errors.add("Assignement (C) <0");
            }
            if (atd < 0) {
                errors.add("Assignement (TD) <0");
            }
            if (atp < 0) {
                errors.add("Assignement (TP) <0");
            }
            if (apm < 0) {
                errors.add("Assignement (PM) <0");
            }
            if (g > 0 && ac >= 0 && atd >= 0 && atp >= 0 && apm >= 0) {
                c += ac * g;
                td += atd * g;
                tp += atp * g;
                pm += apm * g;
                if (assignment.getTeacher() != null) {
                    t_c += ac * g;
                    t_td += atd * g;
                    t_tp += atp * g;
                    t_pm += apm * g;
                } else {
                    if (ac == 0 && atd == 0 && atp == 0 && apm == 0) {
                        zeroAssignmentsCount++;
                    } else {
                        u_c += ac * g;
                        u_td += atd * g;
                        u_tp += atp * g;
                        u_pm += apm * g;
                        noTeacherAssignementsCount++;
                    }
                }
            }
        }
        double epsilon = 1E-3;
        double err = 1E-3;
        if ((err = VrUtils.compareLenientV(d.getValueC() * d.getGroupCountC(), c, epsilon)) != 0) {
            if (err < 0) {
                errors.add("Assignement extra (C) detected = " + (-err));
            } else {
                errors.add("Assignement missing (C) detected = " + err);
            }
        }
        if ((err = VrUtils.compareLenientV(d.getValueTD() * d.getGroupCountTD(), td, epsilon)) != 0) {
            if (err < 0) {
                errors.add("Assignement extra (TD) detected = " + (-err));
            } else {
                errors.add("Assignement missing (TD) detected = " + err);
            }
        }
        if ((err = VrUtils.compareLenientV(d.getValueTP() * d.getGroupCountTP(), tp, epsilon)) != 0) {
            if (err < 0) {
                errors.add("Assignement extra (TP) detected = " + (-err));
            } else {
                errors.add("Assignement missing (TP) detected = " + err);
            }
        }
        if ((err = VrUtils.compareLenientV(d.getValuePM() * d.getGroupCountPM(), pm, epsilon)) != 0) {
            if (err < 0) {
                errors.add("Assignement extra (PM) detected = " + (-err));
            } else {
                errors.add("Assignement missing (PM) detected = " + err);
            }
        }
        if ((err = VrUtils.compareLenientV(d.getValueC() * d.getGroupCountC(), t_c, epsilon)) != 0) {
            if (err < 0) {
                errors.add("Assignement extra Teacher (C) detected = " + (-err));
            } else {
                errors.add("Assignement missing Teacher (C) detected = " + err);
            }
        }
        if ((err = VrUtils.compareLenientV(d.getValueTD() * d.getGroupCountTD(), t_td, epsilon)) != 0) {
            if (err < 0) {
                errors.add("Assignement extra Teacher (TD) detected = " + (-err));
            } else {
                errors.add("Assignement missing Teacher (TD) detected = " + err);
            }
        }
        if ((err = VrUtils.compareLenientV(d.getValueTP() * d.getGroupCountTP(), t_tp, epsilon)) != 0) {
            if (err < 0) {
                errors.add("Assignement extra Teacher (TP) detected = " + (-err));
            } else {
                errors.add("Assignement missing Teacher (TP) detected = " + err);
            }
        }
        if ((err = VrUtils.compareLenientV(d.getValuePM() * d.getGroupCountPM(), t_pm, epsilon)) != 0) {
            if (err < 0) {
                errors.add("Assignement extra Teacher (PM) detected = " + (-err));
            } else {
                errors.add("Assignement missing Teacher (PM) detected = " + err);
            }
        }
        if (noTeacherAssignementsCount > 0) {
            errors.add("Assignements without teachers detected = " + noTeacherAssignementsCount);
        }
        if (zeroAssignmentsCount > 0) {
            errors.add("Assignements without load detected = " + zeroAssignmentsCount);
        }
        if (errors.size() > 0) {
            return errors.stream().collect(Collectors.joining(";\n"));
        }
        return "";
    }
}
