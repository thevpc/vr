/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.callbacks;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.upa.Entity;
import net.vpc.upa.Section;
import net.vpc.upa.callbacks.EntityEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnCreate;
import net.vpc.upa.types.BooleanType;

/**
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 */
@Callback
public class AcademicPerfEvalFeature {

    @OnCreate
    public void entityAdded(EntityEvent event) {
        Entity entity = event.getEntity();
        String entityName = entity.getName();
        if (entityName.equals(AcademicTeacher.class.getSimpleName())
                || entityName.equals(AcademicCoursePlan.class.getSimpleName())
                || entityName.equals(AcademicStudent.class.getSimpleName())) {
            Section tracking = entity.addSection("Eval");
            tracking.addField("allowCourseFeedback", null, null, BooleanType.BOOLEAN)
                    .setDefaultObject(!entityName.equals(AcademicTeacher.class.getSimpleName()));
        }
    }
}
