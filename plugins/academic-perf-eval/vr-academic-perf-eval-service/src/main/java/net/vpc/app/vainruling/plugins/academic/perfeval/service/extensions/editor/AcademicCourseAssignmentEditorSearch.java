/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.extensions.editor;

import java.util.List;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import org.springframework.stereotype.Component;
import net.vpc.app.vainruling.VrEntityName;
import net.vpc.app.vainruling.core.service.util.TextSearchFilter;
import net.vpc.upa.Document;
import net.vpc.app.vainruling.core.service.editor.VrEditorSearchBase;

/**
 *
 * @author vpc
 */
@VrEntityName("AcademicCourseAssignment")
@Component
public class AcademicCourseAssignmentEditorSearch extends VrEditorSearchBase{

    @Override
    public String getName() {
        return "#nonunique";
    }

    @Override
    public String getTitle() {
        return "Afficher les doublons";
    }
    

    @Override
    public List filterDocumentList(List<Document> list, String entityName, String expression) {
        list = AcademicPlugin.Extra.filterNonUniqueAcademicCourseAssignmentDocuments(list);
        return TextSearchFilter.filterList(list, expression, entityName);
    }

}
