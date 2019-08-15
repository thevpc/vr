/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.extensions.editor;

import java.util.List;
import net.vpc.app.vainruling.core.service.editor.ForEntity;
import net.vpc.app.vainruling.core.service.editor.HashtagObjSearchFactory;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.upa.Entity;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@ForEntity("AcademicCourseAssignment")
@Component
public class AcademicCourseAssignmentObjSearchFactory extends HashtagObjSearchFactory {

    public AcademicCourseAssignmentObjSearchFactory() {
        super("#nonunique", "non unique assignments");
    }

    @Override
    public List filterDocumentListByTag(List list, String name, Entity entity, String expression) {
        return AcademicPlugin.Extra.filterNonUniqueAcademicCourseAssignmentDocuments(list);
    }

}
