/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.editor;

import java.util.List;
import net.vpc.app.vainruling.core.service.editor.EntityEditorSearch;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Entity;
import net.vpc.app.vainruling.core.service.editor.EntityEditorSearchFactory;

/**
 *
 * @author vpc
 */
public abstract class HashtagObjSearchFactory implements EntityEditorSearchFactory {

    private String description;
    private String tag;

    public HashtagObjSearchFactory(String hashTag, String description) {
        this.description = description;
        this.tag = hashTag;
        if (!hashTag.startsWith("#")) {
            throw new IllegalArgumentException("Bad tag " + hashTag);
        }
        if (description == null) {
            StringBuilder sb = new StringBuilder("Tapez ici les mots clés de recherche.");
            sb.append(" Vous pouvez utiliser " + tag + " pour filtrer selon " + description);
            description = sb.toString();
        }
        this.description = description;
    }

    @Override
    public String createHelperString(String name, Entity entity) {
        return description;
    }

    @Override
    public EntityEditorSearch create(String name, Entity entity, String expression) {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        expression = expression.trim();
        if (expression.startsWith(tag)) {
            return new ObjSearchImpl(name, entity, expression);
        }
        return null;
    }

    public abstract List filterDocumentListByTag(List list, String name, Entity entity, String expression);

    private class ObjSearchImpl extends EntityEditorSearch {

        private HashtagObjSearchFactory factory;
        private String expression;
        private Entity entity;
        private String name;

        public ObjSearchImpl(String name, Entity entity, String expression) {
            super(name);
            this.expression = expression;
            this.name = name;
            this.entity = entity;
        }

        @Override
        public List filterDocumentList(List list, String entityName) {
            return filterDocumentListByTag(list, getName(), entity, expression);
        }
    }

}
