/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions.data;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseType;
import net.thevpc.upa.Document;
import net.thevpc.upa.Entity;
import net.thevpc.upa.events.PersistEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.OnPrePersist;
import net.thevpc.upa.exceptions.UPAException;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicCourseAssignmentDataExtension {

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
