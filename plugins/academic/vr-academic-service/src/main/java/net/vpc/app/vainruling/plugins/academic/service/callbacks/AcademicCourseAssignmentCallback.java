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
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.NamedFormula;
import net.vpc.upa.config.OnPrePersist;
import net.vpc.upa.exceptions.UPAException;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicCourseAssignmentCallback {


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
            errors.add("Course Error : TD < 0");
        } else if (d.getValueTD() > 0 && d.getGroupCountTD() == 0) {
            errors.add("Course Error : Groupe(TD) = 0");
        } else if (d.getGroupCountTD() < 0) {
            errors.add("Course Error : Groupe(TD) < 0");
        }

        if (d.getValueTP() < 0) {
            errors.add("Course Error : TP < 0");
        } else if (d.getValueTP() > 0 && d.getGroupCountTP() == 0) {
            errors.add("Course Error : Groupe(TP) = 0");
        } else if (d.getGroupCountTP() < 0) {
            errors.add("Course Error : Groupe(TP) < 0");
        }

        if (d.getValuePM() < 0) {
            errors.add("Course Error : PM < 0");
        } else if (d.getValuePM() > 0 && d.getGroupCountPM() == 0) {
            errors.add("Course Error : Groupe(PM) = 0");
        } else if (d.getGroupCountPM() < 0) {
            errors.add("Course Error : Groupe(PM) < 0");
        }

        double c = 0;
        double td = 0;
        double tp = 0;
        double pm = 0;
        for (AcademicCourseAssignment assignment : assignments) {
            double ac = assignment.getValueC();
            double atd = assignment.getValueTD();
            double atp = assignment.getValueTP();
            double apm = assignment.getValuePM();
            double g = assignment.getGroupCount();
            double s = assignment.getShareCount();
            if (g <= 0) {
                errors.add("Assignment Error : group<=0");
            } else if (s < 1) {
                errors.add("Assignment Error : share<1");
            } else {
                c += ac * g / s;
                td += atd * g / s;
                tp += atp * g / s;
                pm += apm * g / s;
            }
        }
        double epsilon = 1E-3;
        double err = 1E-3;
        if ((err = VrUtils.compareLenientV(d.getValueC() * d.getGroupCountC(), c, epsilon)) != 0) {
            errors.add("Assignment Error : delta(C) = " + err);
        }
        if ((err = VrUtils.compareLenientV(d.getValueTD() * d.getGroupCountTD(), td, epsilon)) != 0) {
            errors.add("Assignment Error :  delta(TD) = " + err);
        }
        if ((err = VrUtils.compareLenientV(d.getValueTP() * d.getGroupCountTP(), tp, epsilon)) != 0) {
            errors.add("Assignment Error :  delta(TP) = " + err);
        }
        if ((err = VrUtils.compareLenientV(d.getValuePM() * d.getGroupCountPM(), pm, epsilon)) != 0) {
            errors.add("Assignment Error :  delta(PM) = " + err);
        }
        return StringUtils.join("\n", errors);
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
