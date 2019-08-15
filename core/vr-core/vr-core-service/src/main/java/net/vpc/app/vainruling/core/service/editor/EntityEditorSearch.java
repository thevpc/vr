/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.editor;


import java.util.List;
import java.util.Map;
import net.vpc.upa.Document;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class EntityEditorSearch {
    private String name;

    public EntityEditorSearch(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String createPreProcessingExpression(String entityName,Map<String,Object> parameters,String paramPrefix) {
        return null;
    }

    public List filterDocumentList(List<Document> list, String entityName) {
        return list;
    }

}
