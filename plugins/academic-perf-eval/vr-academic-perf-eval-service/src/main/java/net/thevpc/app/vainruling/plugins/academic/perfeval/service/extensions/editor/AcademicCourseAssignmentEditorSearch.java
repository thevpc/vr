/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.perfeval.service.extensions.editor;

import java.util.List;

import net.thevpc.app.vainruling.VrEntityName;
import net.thevpc.app.vainruling.core.service.editor.VrEditorSearchBase;
import net.thevpc.app.vainruling.core.service.util.TextSearchFilter;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import org.springframework.stereotype.Component;
import net.thevpc.upa.Document;

/**
 *
 * @author vpc
 */
@VrEntityName("AcademicCourseAssignment")
@Component
public class AcademicCourseAssignmentEditorSearch extends VrEditorSearchBase {

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
