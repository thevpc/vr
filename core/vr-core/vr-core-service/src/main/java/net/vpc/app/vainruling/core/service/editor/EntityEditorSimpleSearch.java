/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.editor;

import net.vpc.app.vainruling.VrEditorSearch;
import net.vpc.app.vainruling.core.service.util.TextSearchFilter;
import net.vpc.upa.Document;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class EntityEditorSimpleSearch extends VrEditorSearch {

    private TextSearchFilter textSearch;
    private String entityName;

    public EntityEditorSimpleSearch() {
        this(null);
    }

    public EntityEditorSimpleSearch(String expression) {
        super("expr");
        this.textSearch = TextSearchFilter.forEntity(expression, entityName);
    }

    public String getExpression() {
        return textSearch == null ? null : textSearch.getExpression();
    }

    public void setExpression(String expression) {
        textSearch.setExpression(expression);
    }

    @Override
    public List filterDocumentList(List<Document> list, String entityName) {
        this.entityName = entityName;
        List oldList = super.filterDocumentList(list, entityName);
        return textSearch == null ? oldList : textSearch.filterList(oldList);
    }
}
