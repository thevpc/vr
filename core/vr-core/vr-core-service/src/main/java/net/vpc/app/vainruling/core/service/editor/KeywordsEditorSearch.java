/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.editor;

import java.util.List;
import net.vpc.app.vainruling.VrEntityName;
import net.vpc.app.vainruling.core.service.util.TextSearchFilter;
import net.vpc.upa.Document;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@VrEntityName("*")
@Component
public class KeywordsEditorSearch extends VrEditorSearchBase {

    @Override
    public String getName() {
        return "Mots Clefs";
    }

    @Override
    public List filterDocumentList(List<Document> list, String entityName, String expression) {
        return TextSearchFilter.filterList(list, expression, entityName);
    }

    @Override
    public String getTitle() {
        return "Chercher par mots clefs";
    }

}
