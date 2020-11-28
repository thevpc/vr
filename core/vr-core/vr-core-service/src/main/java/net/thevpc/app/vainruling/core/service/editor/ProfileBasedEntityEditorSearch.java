package net.thevpc.app.vainruling.core.service.editor;

import net.thevpc.upa.Document;
import net.thevpc.upa.Entity;
import net.thevpc.upa.EntityBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.thevpc.upa.UPA;

/**
 * Created by vpc on 6/25/17.
 */
public abstract class ProfileBasedEntityEditorSearch extends VrEditorSearchBase {

    abstract protected List filterDocumentByProfileFilter(List objects, String profileSearchText);

    @Override
    public String getName() {
        return "#groupes";
    }

    @Override
    public String getTitle() {
        return "Chercher par groupes";
    }

    @Override
    public List filterDocumentList(List<Document> list, String entityName, String expression) {
        Entity entity = UPA.getPersistenceUnit().getEntity(entityName);
        List toFilterObjectList = new ArrayList(list.size());
        Map<Object, Object> map = new HashMap<>(list.size());
        EntityBuilder builder = entity.getBuilder();
        for (Object o : list) {
            Object o0 = o;
            Object id = null;
            if (o instanceof Document) {
                Document doc = (Document) o;
                id = builder.documentToId(doc);
                o = builder.documentToObject(doc);
            } else {
                id = builder.objectToId(o);
            }
            map.put(id, o0);
            toFilterObjectList.add(o);
        }
        List filteredObjectList = filterDocumentByProfileFilter(toFilterObjectList, expression);
        List documentsObjectList = new ArrayList(filteredObjectList.size());
        for (Object o : filteredObjectList) {
            Object o0 = o;
            Object id = null;
            if (o instanceof Document) {
                Document doc = (Document) o;
                id = builder.documentToId(doc);
                o = map.get(id);
            } else {
                id = builder.objectToId(o);
                o = map.get(id);
            }
            documentsObjectList.add(o);
        }
        return documentsObjectList;
    }
}
