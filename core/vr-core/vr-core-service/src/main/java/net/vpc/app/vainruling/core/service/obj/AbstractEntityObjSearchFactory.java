package net.vpc.app.vainruling.core.service.obj;

import net.vpc.upa.Document;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 6/25/17.
 */
public abstract class AbstractEntityObjSearchFactory implements EntityObjSearchFactory {

    @Override
    public String createHelperString(String name, Entity entity) {
        StringBuilder sb = new StringBuilder("Tapez ici les mots clés de recherche.");
        sb.append(" Vous pouvez utiliser profile:__EXPR__ pour filtrer selon le groupe utilisateur");
        return sb.toString();
    }

    @Override
    public ObjSearch create(String name, Entity entity, String expression) {
        if (expression == null) {
            return null;
        }
        if (expression.startsWith("profile:")) {
            return new ObjSearchImpl(name, entity, expression);
        }
        return null;
    }

    abstract protected List filterContactsByProfileFilter0(List objects, String profileSearchText);

    private class ObjSearchImpl extends ObjSearch {

        private final Entity entity;
        private final String expression;

        public ObjSearchImpl(String name, Entity entity, String expression) {
            super(name);
            this.entity = entity;
            this.expression = expression;
        }

        @Override
        public List filterList(List list, String entityName) {
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
            List filteredObjectList = filterContactsByProfileFilter0(toFilterObjectList, expression.substring("profile:".length()));
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
}
