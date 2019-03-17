/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.CustomFormulaContext;
import net.vpc.upa.Document;
import net.vpc.upa.Entity;
import net.vpc.upa.events.PersistEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.NamedFormula;
import net.vpc.upa.config.OnPrePersist;
import net.vpc.upa.exceptions.UPAException;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicCourseAssignmentInterceptor {

    @NamedFormula
    public String academicCoursePlan_validationErrors_Formula(CustomFormulaContext ctx) {
        AcademicCoursePlan d = (AcademicCoursePlan) ctx.reloadUpdateObject();
        AcademicPlugin t = VrApp.getBean(AcademicPlugin.class);
        List<AcademicCourseAssignment> assignments = t.findAcademicCourseAssignmentListByCoursePlanId(d.getId());
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
            double s = assignment.getShareCount();
            if (g <= 0) {
                errors.add("Assignement group<=0");
            }
            if (s < 1) {
                errors.add("Assignement share<1");
            } 
            if (ac<0) {
                errors.add("Assignement (C) <0");
            } 
            if (atd<0) {
                errors.add("Assignement (TD) <0");
            } 
            if (atp<0) {
                errors.add("Assignement (TP) <0");
            } 
            if (apm<0) {
                errors.add("Assignement (PM) <0");
            } 
            if(g>0 && s>=1 && ac>=0 && atd>=0 && atp>=0 && apm>=0){
                c += ac * g / s;
                td += atd * g / s;
                tp += atp * g / s;
                pm += apm * g / s;
                if (assignment.getTeacher() != null) {
                    t_c += ac * g / s;
                    t_td += atd * g / s;
                    t_tp += atp * g / s;
                    t_pm += apm * g / s;
                } else {
                    if (ac == 0 && atd == 0 && atp == 0 && apm == 0) {
                        zeroAssignmentsCount++;
                    } else {
                        u_c += ac * g / s;
                        u_td += atd * g / s;
                        u_tp += atp * g / s;
                        u_pm += apm * g / s;
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
            errors.add("Assignements without teachers detected = " + err);
        }
        if (zeroAssignmentsCount > 0) {
            errors.add("Assignements without load detected = " + err);
        }
        if(errors.size()>0){
            return ""+StringUtils.join(";\n", errors);
        }
        return "";
    }

    @OnPrePersist
    public void onPrePersist(PersistEvent event) throws UPAException {
        Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicCourseAssignment.class)) {
            Document d = event.getPersistedDocument();
            AcademicCoursePlan coursePlan = d.get("coursePlan");
            if (coursePlan != null) {
                if (d.getDouble("valueC") == 0 && d.getDouble("valueTD") == 0 && d.getDouble("valueTP") == 0 && d.getDouble("valuePM") == 0) {
                    AcademicCourseType courseType = d.get("courseType");
                    if (courseType == null) {
                        d.setDouble("valueC", coursePlan.getValueC());
                        d.setDouble("valueTD", coursePlan.getValueTD());
                        d.setDouble("valueTP", coursePlan.getValueTP());
                        d.setDouble("valuePM", coursePlan.getValuePM());
                        d.setInt("groupCount", 1); //should be constant
                    } else if ("CM".equals(courseType.getName())) {
                        d.setDouble("valueC", coursePlan.getValueC());
                    } else if ("C".equals(courseType.getName())) {
                        d.setDouble("valueC", coursePlan.getValueC());
                        d.setDouble("valueTD", coursePlan.getValueTD());
                        d.setInt("groupCount", 1); //should be constant
                    } else if ("TD".equals(courseType.getName())) {
                        d.setDouble("valueTD", coursePlan.getValueTD());
                        d.setInt("groupCount", 2); //should be constant
                    } else if ("TP".equals(courseType.getName())) {
                        d.setDouble("valueTP", coursePlan.getValueTP());
                        d.setInt("groupCount", 2); //should be constant
                    } else if ("PS".equals(courseType.getName())) {
                        d.setDouble("valueTP", coursePlan.getValueTP());
                        d.setInt("groupCount", 2); //should be constant
                    } else if ("TPPM".equals(courseType.getName())) {
                        d.setDouble("valueTP", coursePlan.getValueTP());
                        d.setDouble("valuePM", coursePlan.getValuePM());
                        d.setInt("groupCount", 2); //should be constant
                    }
                }
            }
        }
    }

}
