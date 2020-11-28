/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.perfeval.service.callbacks;

import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.upa.DefaultFieldBuilder;
import net.thevpc.upa.Entity;
import net.thevpc.upa.MissingStrategy;
import net.thevpc.upa.Section;
import net.thevpc.upa.events.EntityEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.OnPrePrepare;
import net.thevpc.upa.types.BooleanType;

/**
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 */
@Callback
public class AcademicPerfEvalFeature {

    @OnPrePrepare
    public void onPreInitEntity(EntityEvent event) {
        Entity entity = event.getEntity();
        String entityName = entity.getName();
        if (entityName.equals(AcademicTeacher.class.getSimpleName())
                || entityName.equals(AcademicCoursePlan.class.getSimpleName())
                || entityName.equals(AcademicStudent.class.getSimpleName())) {
            if (entity.findField("allowCourseFeedback") == null) {
                Section tracking = entity.getSection("Eval", MissingStrategy.CREATE, 0);
                tracking.addField(
                        new DefaultFieldBuilder().setName("allowCourseFeedback")
                                .setDataType(BooleanType.BOOLEAN)
                                .setDefaultObject(!entityName.equals(AcademicTeacher.class.getSimpleName()))
                );
            }
        } else if (entityName.equals(AcademicCourseAssignment.class.getSimpleName())) {
            if (entity.findField("enableCourseFeedback") == null) {
                Section tracking = entity.getSection("Eval", MissingStrategy.CREATE, 0);
                tracking.addField(new DefaultFieldBuilder().setName("enableCourseFeedback")
                        .setDataType(BooleanType.BOOLEAN)
                        .setDefaultObject(true)
                );
            }
        }
    }
}
