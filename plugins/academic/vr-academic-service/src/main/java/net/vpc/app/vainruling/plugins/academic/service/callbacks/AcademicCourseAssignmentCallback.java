/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.upa.Document;
import net.vpc.upa.Entity;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPrePersist;
import net.vpc.upa.exceptions.UPAException;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicCourseAssignmentCallback {


    @OnPrePersist
    public void onPrePersist(PersistEvent event) throws UPAException {
        Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicCourseAssignment.class)) {
            Document d = event.getPersistedDocument();
            AcademicCoursePlan coursePlan=d.get("coursePlan");
            if (coursePlan != null) {
                if (d.getDouble("valueC") == 0 && d.getDouble("valueTD") == 0 && d.getDouble("valueTP") == 0 && d.getDouble("valuePM") == 0) {
                    AcademicCourseType courseType = d.get("courseType");
                    if (courseType == null) {
                        d.setDouble("valueC",coursePlan.getValueC());
                        d.setDouble("valueTD",coursePlan.getValueTD());
                        d.setDouble("valueTP",coursePlan.getValueTP());
                        d.setDouble("valuePM",coursePlan.getValuePM());
                        d.setInt("groupCount",1); //should be constant
                    } else if ("CM".equals(courseType.getName())) {
                        d.setDouble("valueC",coursePlan.getValueC());
                    } else if ("C".equals(courseType.getName())) {
                        d.setDouble("valueC",coursePlan.getValueC());
                        d.setDouble("valueTD",coursePlan.getValueTD());
                        d.setInt("groupCount",1); //should be constant
                    } else if ("TD".equals(courseType.getName())) {
                        d.setDouble("valueTD",coursePlan.getValueTD());
                        d.setInt("groupCount",2); //should be constant
                    } else if ("TP".equals(courseType.getName())) {
                        d.setDouble("valueTP",coursePlan.getValueTP());
                        d.setInt("groupCount",2); //should be constant
                    } else if ("PS".equals(courseType.getName())) {
                        d.setDouble("valueTP",coursePlan.getValueTP());
                        d.setInt("groupCount",2); //should be constant
                    } else if ("TPPM".equals(courseType.getName())) {
                        d.setDouble("valueTP",coursePlan.getValueTP());
                        d.setDouble("valuePM",coursePlan.getValuePM());
                        d.setInt("groupCount",2); //should be constant
                    }
                }
            }
        }
    }


}
