/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.editor;

import net.vpc.app.vainruling.core.service.util.TextSearchFilter;
import net.vpc.app.vainruling.core.service.util.ObjectToMapConverter;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Document;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
public class EntityEditorSimpleSearch extends EntityEditorSearch {

    private TextSearchFilter textSearch;
    private String entityName;

    public EntityEditorSimpleSearch() {
        super("expr");
        this.textSearch=new TextSearchFilter(
                null,
                new ObjectToMapConverter() {
                    @Override
                    public Map<String, Object> convert(Object o) {
                        return EntityEditorSimpleSearch.this.toStringRecord(o,entityName);
                    }
                }
        );
    }

    public EntityEditorSimpleSearch(String expression) {
        super("expr");
        this.textSearch=new TextSearchFilter(
                expression,
                new ObjectToMapConverter() {
                    @Override
                    public Map<String, Object> convert(Object o) {
                        return EntityEditorSimpleSearch.this.toStringRecord(o,entityName);
                    }
                }
        );
    }
    public String getExpression() {
        return textSearch==null?null:textSearch.getExpression();
    }

    public void setExpression(String expression) {
        textSearch.setExpression(expression);
    }

    @Override
    public List filterDocumentList(List<Document> list, String entityName) {
        this.entityName=entityName;
        List oldList = super.filterDocumentList(list, entityName);
        return textSearch==null?oldList:textSearch.filterList(oldList);
    }


    private Map<String, Object> toStringRecord(Object o, String entityName) {
        Map<String, Object> words = new HashMap<>();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Document r = (o instanceof Document) ? ((Document) o) : pu.getEntity(entityName).getBuilder().objectToDocument(o, true);
        if (r != null) {
            for (Map.Entry<String, Object> entry : r.entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                if (v != null) {
                    Entity ve = pu.findEntity(v.getClass());
                    if (ve != null) {
                        Object mv = ve.getBuilder().getMainValue(v);
                        String v2 = String.valueOf(mv);
                        if (!StringUtils.isBlank(v2)) {
                            words.put(k, (v2));
                        }
                    } else if (v instanceof String) {
                        if (!StringUtils.isBlank(v.toString())) {
                            words.put(k, (v));
                        }
                    } else {
                        words.put(k, (v));
                    }
                }
            }
        }
        return words;
    }
}
