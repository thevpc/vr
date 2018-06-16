package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.obj.AutoFilterData;
import net.vpc.app.vainruling.core.service.obj.EntityObjSearchFactory;
import net.vpc.app.vainruling.core.service.obj.ObjSearch;
import net.vpc.app.vainruling.core.service.obj.ObjSimpleSearch;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.core.service.util.VrUPAUtils;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.upa.*;
import net.vpc.upa.expressions.*;
import net.vpc.upa.filters.EntityFilters;
import net.vpc.upa.types.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;

class CorePluginBodyDAOManager extends CorePluginBody {

    public <T> T findOrCreate(T o) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        return findOrCreate(o, e.getMainField().getName());
    }

    public <T> T findOrCreate(T o, String field) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        Object value = e.getBuilder().objectToDocument(o, true).getObject(field);
        T t = pu.createQueryBuilder(o.getClass()).setEntityAlias("o").byExpression(new Equals(new Var(new Var("o"), field), new Literal(value, e.getField(field).getDataType())))
                .getFirstResultOrNull();
        if (t == null) {
            pu.persist(o);
            return o;
        }
        return t;
    }

    public List<AutoFilterData> getEntityFilters(String entityName) {
        Entity entity = UPA.getPersistenceUnit().getEntity(entityName);
        List<AutoFilterData> autoFilterDatasAll = new ArrayList<>();
        List<AutoFilterData> autoFilterDatas = new ArrayList<>();
        String all = StringUtils.trim(entity.getProperties().getString("ui.auto-filters"));
        if(!all.isEmpty()){
            //this is a single value
            if(all.startsWith("{")){
                //VrUtils.parseJSONObject(all, AutoFilterData[].class)
                autoFilterDatasAll.add(VrUtils.parseJSONObject(all, AutoFilterData.class));
            }else{
                autoFilterDatasAll.addAll(Arrays.asList(VrUtils.parseJSONObject(all, AutoFilterData[].class)));
            }
        }
        for (int i = 0; i < autoFilterDatasAll.size(); i++) {
            autoFilterDatasAll.get(i).setBaseEntityName(entityName);
        }
        //
        for (Map.Entry<String, Object> entry : entity.getProperties().toMap().entrySet()) {
            if (entry.getKey().startsWith("ui.auto-filter.")) {
                String name = entry.getKey().substring("ui.auto-filter.".length());
                AutoFilterData d = VrUtils.parseJSONObject((String) entry.getValue(), AutoFilterData.class);
                d.setName(name);
                d.setBaseEntityName(entity.getName());
                autoFilterDatas.add(d);
            }
        }
        Collections.sort(autoFilterDatas);
        
        autoFilterDatasAll.addAll(autoFilterDatas);
        return autoFilterDatasAll;
    }

    public List<NamedId> getFieldValues(String entityName, String fieldName, Map<String, Object> constraints, Object currentInstance) {

        List<NamedId> all = new ArrayList<>();
        Entity e = UPA.getPersistenceUnit().getEntity(entityName);
        Field f = e.getField(fieldName);
        DataType t = f.getDataType();
        if (t instanceof BooleanType) {
            all.add(new NamedId(true, String.valueOf(true)));
            all.add(new NamedId(true, String.valueOf(false)));
        } else if (t instanceof EnumType) {
            I18n i18n = VrApp.getBean(I18n.class);
            for (Object value : ((EnumType) t).getValues()) {
                all.add(new NamedId(VrUPAUtils.objToJson(value, t).toString(), i18n.getEnum(value)));
            }
        } else if (t instanceof KeyType) {
            KeyType mtype = (KeyType) t;
            return findAllNamedIds(mtype.getEntity().getName(), constraints, currentInstance);
        } else if (t instanceof ManyToOneType) {
            ManyToOneType m = (ManyToOneType) t;
            final Entity me = m.getRelationship().getTargetEntity();
            if ((m.getRelationship() instanceof ManyToOneRelationship && ((ManyToOneRelationship) m.getRelationship()).getFilter() == null)
                    || (m.getRelationship() instanceof OneToOneRelationship)) {
                List<NamedId> cacheItem = findAllNamedIdsByRelationship(m.getRelationship().getName(), constraints, currentInstance);
                List<NamedId> cacheItem2 = new ArrayList<>(cacheItem.size());
                for (NamedId namedId : cacheItem) {
                    Object id = namedId.getId();
                    Object id2 = VrUPAUtils.objToJson(id, me.getDataType()).toString();
                    cacheItem2.add(new NamedId(id2, namedId.getName()));
                }
                return cacheItem2;
            }
            return findAllNamedIdsByRelationship(m.getRelationship().getName(), constraints, currentInstance);
        }
        return all;
    }

    public Object resolveId(String entityName, Object t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        t = VrUtils.convertDataObjectOrDocument(t, entity.getEntityType());
        if (t instanceof Document) {
            return entity.getBuilder().documentToId((Document) t);
        }
        return entity.getBuilder().objectToId(t);
    }

    public Object save(String entityName, Object t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (entityName == null) {
            entityName = pu.getEntity(t.getClass()).getName();
        }
        Entity entity = pu.getEntity(entityName);
        t = VrUtils.convertDataObjectOrDocument(t, entity.getEntityType());
        Object id = resolveId(entityName, t);
        List<Field> pf = entity.getIdFields();
        boolean persist = false;
        if (id == null) {
            persist = true;
        } else if (pf.size() <= 1) {
            DataType dt = pf.get(0).getDataType();
            if (dt instanceof ManyToOneType) {
                persist = entity.findById(id) == null;
            } else if (pf.size() == 1 && pf.get(0).getModifiers().contains(FieldModifier.PERSIST_SEQUENCE)) {
                persist = Objects.equals(dt.getDefaultUnspecifiedValue(), id);
            } else {
                persist = entity.findById(id) == null;
            }
        } else {
            persist = entity.findById(id) == null;
        }
        if (persist) {
            //should change this using a callback
            if (t instanceof AppPeriod) {
                AppPeriod a = (AppPeriod) t;
                a.setCreationTime(new DateTime());
                a.setSnapshotName(null);
            }

            pu.persist(entityName, t);
//            trace.inserted(t, getClass(), Level.FINE);
        } else {
            pu.merge(entityName, t);
//            trace.updated(t, old, getClass(), Level.FINE);
        }
        return t;
    }

    public RemoveTrace erase(String entityName, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        id = VrUtils.convertDataObject(id, entity.getIdType());
        if (getContext().getTrace().accept(entity)) {
            getContext().getTrace().removed(entityName, pu.findById(entityName, id), entity.getParent().getPath(), Level.FINE);
        }
        return pu.remove(entityName, RemoveOptions.forId(id));
    }

    public boolean isSoftRemovable(String entityName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        return entity.containsField("deleted");
    }

    public RemoveTrace remove(String entityName, Object id) {
        if (entityName == null) {
            entityName = UPA.getPersistenceUnit().getEntity(id.getClass()).getName();
        }
        if (!isSoftRemovable(entityName)) {
            return erase(entityName, id);
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        id = VrUtils.convertDataObject(id, entity.getIdType());
        Document t = pu.findDocumentById(entityName, id);
        if (t != null) {
            //check if already soft deleted
            Boolean b = (Boolean) entity.getField("deleted").getValue(t);
            if (b != null && b.booleanValue()) {
                return erase(entityName, id);
            } else {
                if (entity.containsField("deleted")) {
                    t.setBoolean("deleted", true);
                }
                if (entity.containsField("deletedBy")) {
                    String login = getContext().getCorePlugin().getCurrentUserLogin();
                    t.setString("deletedBy", login);
                }
                if (entity.containsField("deletedOn")) {
                    t.setDate("deletedOn", new Timestamp(System.currentTimeMillis()));
                }
                pu.merge(entityName, t);
                if (getContext().getTrace().accept(entity)) {
                    getContext().getTrace().softremoved(entityName, pu.findById(entityName, id), entity.getParent().getPath(), Level.FINE);
                }
                return null;
            }
        }
        return null;
    }

    public String getObjectName(String entityName, Object obj) {
        if (obj == null) {
            return "NO_NAME";
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        obj = VrUtils.convertDataObjectOrDocument(obj, entity.getEntityType());
        return String.valueOf(entity.getBuilder().objectToName(obj));
    }

    //    public boolean isEntityAction(String type, String action, Object object) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        Entity entity = pu.getEntity(type);
//        return VrApp.getBean(PluginManagerService.class).isEnabledEntityAction(entity.getEntityType(), action, object);
//    }
//
//    public ActionInfo[] getEntityActionList(String type, Object object) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        Entity entity = pu.getEntity(type);
//        return VrApp.getBean(PluginManagerService.class).getEntityActionList(entity.getEntityType(), object);
//    }
//    public Object invokeEntityAction(String type, String action, Object object, Object[] args) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        Entity entity = pu.getEntity(type);
//        if ("Archive".equals(action)) {
//            Object id = resolveId(object);
//            Object t = pu.findById(type, id);
//            if (t != null) {
//                Document r = entity.getBuilder().objectToDocument(t, true);
//                if (entity.containsField("archived")) {
//                    r.setBoolean("archived", true);
//                }
////            Object old = pu.findById(type, id);
//                pu.merge(t);
//                trace.archived(pu.findById(type, id), entity.getParent().getPath(), Level.FINE);
////            trace.updated(t, old, getClass(), Level.FINE);
//            }
//            return null;
//        }
//
//        return VrApp.getBean(PluginManagerService.class).invokeEntityAction(entity.getEntityType(), action, object, args);
//    }
    public boolean archive(String entityName, Object object) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        object = VrUtils.convertDataObjectOrDocument(object, entity.getEntityType());
        Object id = resolveId(entityName, object);
        Object t = pu.findById(entityName, id);
        if (t != null) {
            Document r = entity.getBuilder().objectToDocument(t, true);
            if (entity.containsField("archived")) {
                r.setBoolean("archived", true);
                pu.merge(entityName, t);
                if (getContext().getTrace().accept(entity)) {
                    getContext().getTrace().archived(entityName, pu.findById(entityName, id), entity.getParent().getPath(), Level.FINE);
                }
                return true;
            }
//            Object old = pu.findById(type, id);
//            trace.updated(t, old, getClass(), Level.FINE);
        }
        return false;
//        invokeEntityAction(type, "Archive", object, null);
    }

    public Object find(Class type, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        checkFindOne(entity.getName());
        id = VrUtils.convertDataObject(id, entity.getIdType());
        return pu.findById(type, id);
    }

    public Object find(String entityName, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        id = VrUtils.convertDataObject(id, entity.getIdType());
        checkFindOne(entity.getName());
        return pu.findById(entityName, id);
    }

    public Document findDocument(String entityName, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        id = VrUtils.convertDataObject(id, entity.getIdType());
        checkFindOne(entity.getName());
        return pu.findDocumentById(entityName, id);
    }

    public boolean isArchivable(String entityName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        return entity.containsField("archived");
    }

    public List<Object> findAll(String entityName, Map<String, Object> criteria) {
        checkFindMany(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder cc = pu.createQueryBuilder(entityName)
                .orderBy(entity.getListOrder())
                .setEntityAlias("o");
        if (criteria != null) {
            for (Map.Entry<String, Object> entrySet : criteria.entrySet()) {
                Field f = entity.getField(entrySet.getKey());
                Object value = entrySet.getValue();
                value = VrUtils.convertDataObjectOrDocument(value, f.getDataType().getPlatformType());
                cc.byExpression(new Equals(new UserExpression("o." + entrySet.getKey()), new Literal(value, null)));
            }
        }
        Chronometer c = new Chronometer();
        List<Object> entityList = cc
                .getResultList();
        entityList.size();
        c.stop();
        return entityList;
    }

    public List<NamedId> findAllNamedIdsByRelationship(String relationshipName, Map<String, Object> criteria, Object currentInstance) {
        Relationship r = UPA.getPersistenceUnit().getRelationship(relationshipName);
        final String aliasName = "o";
        currentInstance = VrUtils.convertDataObjectOrDocument(currentInstance, r.getSourceEntity().getEntityType());

        Expression relationExpression = r.createTargetListExpression(currentInstance, aliasName);
        return findAllNamedIds(r.getTargetEntity().getName(), criteria, relationExpression);
    }

    public List<NamedId> findAllNamedIds(String entityName, Map<String, Object> criteria, Expression condition) {
        checkNavigate(entityName);
        final String aliasName = "o";
        Entity entity = UPA.getPersistenceUnit().getEntity(entityName);

        PersistenceUnit pu = UPA.getPersistenceUnit();
        Select q = new Select();

        Field primaryField = entity.getIdFields().get(0);
        q.field(" o." + primaryField.getName(), "id");
        Field mainField = entity.getMainField();
        if (mainField == null) {
            mainField = primaryField;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(aliasName + "." + mainField.getName());
        while (mainField.getDataType() instanceof ManyToOneType) {
            Entity t = ((ManyToOneType) mainField.getDataType()).getRelationship().getTargetEntity();
            mainField = t.getMainField();
            sb.append("." + mainField.getName());
        }
        q.field(sb.toString(), "name");

        q.from(entity.getName(), aliasName);
        q.orderBy(entity.getListOrder());
        Expression where = null;
        if (criteria != null) {
            for (Map.Entry<String, Object> entrySet : criteria.entrySet()) {
                where = And.create(where, new Equals(new UserExpression(aliasName + "." + entrySet.getKey()), new Literal(entrySet.getValue(), null)));
            }
        }
        q.where(condition);
        q.where(where);
        Chronometer c = new Chronometer();
        List<NamedId> entityList = pu.createQuery(q)
                .getResultList(NamedId.class);
        entityList.size();
        c.stop();
        return entityList;
    }

    public List<NamedId> findAllNamedIds(String entityName, Map<String, Object> criteria, Object currentInstance) {
        checkNavigate(entityName);
        final String aliasName = "o";
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Select q = new Select();

        Field primaryField = entity.getIdFields().get(0);
        q.field(" o." + primaryField.getName(), "id");
        Field mainField = entity.getMainField();
        if (mainField == null) {
            mainField = primaryField;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(aliasName + "." + mainField.getName());
        while (mainField.getDataType() instanceof ManyToOneType) {
            Entity t = ((ManyToOneType) mainField.getDataType()).getRelationship().getTargetEntity();
            mainField = t.getMainField();
            sb.append("." + mainField.getName());
        }
        q.field(sb.toString(), "name");

        q.from(entityName, aliasName);
        q.orderBy(entity.getListOrder());
        Expression where = null;
        if (criteria != null) {
            for (Map.Entry<String, Object> entrySet : criteria.entrySet()) {
                where = And.create(where, new Equals(new UserExpression(aliasName + "." + entrySet.getKey()), new Literal(entrySet.getValue(), null)));
            }
        }
        q.where(where);
        Chronometer c = new Chronometer();
        List<NamedId> entityList = pu.createQuery(q)
                .getResultList(NamedId.class);
        entityList.size();
        c.stop();
        return entityList;
    }

    public long findCountByFilter(String entityName, String criteria, ObjSearch objSearch, Map<String, Object> parameters) {
        checkNavigate(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String qq = "Select count(1) from " + entityName + " o ";
        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName, parameters, "os");
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            qq += " where " + filterExpression;
        }
        Query query = pu.createQuery(qq);
        if (parameters != null) {
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                query.setParameter(pp.getKey(), pp.getValue());
            }
        }
        Number nn = (Number) query.getSingleValue();
        return nn.longValue();
    }

    public List<Object> findByFilter(String entityName, String criteria, ObjSearch objSearch, Map<String, Object> parameters) {
        checkFindMany(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder q = pu
                .createQueryBuilder(entityName)
                .setEntityAlias("o")
                .orderBy(entity.getListOrder());
        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName, parameters, "os");
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            q.byExpression(filterExpression);
        }
        if (parameters != null) {
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                q.setParameter(pp.getKey(), pp.getValue());
            }
        }
        List<Object> list = q.getResultList();
        if (objSearch != null) {
            list = objSearch.filterList(list, entityName);
        }
        return list;
    }

    public List<Document> findDocumentsByFilter(String entityName, String criteria, ObjSearch objSearch, String textSearch, Map<String, Object> parameters) {
        checkFindMany(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder q = pu
                .createQueryBuilder(entityName)
                .setEntityAlias("o")
                .orderBy(entity.getListOrder());
        Expression filterExpression = null;
        if (!org.apache.commons.lang.StringUtils.isEmpty(criteria)) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName, parameters, "os");
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            q.byExpression(filterExpression);

        }
        if (parameters != null) {
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                q.setParameter(pp.getKey(), pp.getValue());
            }
        }
        Object appPropertyValue = getContext().getCorePlugin().getAppPropertyValue("System.MaxLoadedObjects", null);
        if (appPropertyValue == null) {
            appPropertyValue = 7000;
        }
        q.setTop(Convert.toInt(appPropertyValue, IntegerParserConfig.LENIENT_F));
        List<Document> list = q.getDocumentList();
        if (objSearch != null) {
            list = objSearch.filterList(list, entityName);
        }
        if (!StringUtils.isEmpty(textSearch)) {

            list = createSearch(null, entity.getName(), textSearch).filterList(list, entityName);
        }
        return list;
    }

    public String createSearchHelperString(String name, String entityName) {
        String d="Tapez ici les mots clés de recherche.";
        if(StringUtils.isEmpty(entityName)){
            return d;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        String f = entity.getProperties().getString(UIConstants.ENTITY_TEXT_SEARCH_FACTORY);
        if (StringUtils.isEmpty(f)) {
            return d;
        }
        EntityObjSearchFactory g = null;
        try {
            g = (EntityObjSearchFactory) Class.forName(f).newInstance();
            String s=g.createHelperString(name, entity);
            if(StringUtils.isEmpty(s)){
                s=d;
            }
            return s;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public ObjSearch createSearch(String name, String entityName, String expression) {
        if (StringUtils.isEmpty(expression)) {
            return new ObjSimpleSearch(null);
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        String f = entity.getProperties().getString(UIConstants.ENTITY_TEXT_SEARCH_FACTORY);
        if (StringUtils.isEmpty(f)) {
            return new ObjSimpleSearch(expression);
        }
        EntityObjSearchFactory g = null;
        try {
            g = (EntityObjSearchFactory) Class.forName(f).newInstance();
            ObjSearch objSearch = g.create(name, entity, expression);
            if (objSearch == null) {
                return new ObjSimpleSearch(expression);
            }
            return objSearch;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Object> findAll(String entityName) {
        checkFindMany(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findAll(entityName);
    }

    public List<Object> findByField(Class type, String field, Object value) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        checkFindMany(entity.getName());
        DataType dt = entity.getField(field).getDataType();
        return pu.createQueryBuilder(type).setEntityAlias("o")
                .byExpression(new And(new Var(new Var("o"), field), new Literal(value, dt)))
                .orderBy(entity.getListOrder())
                .getResultList();
    }

    public void updateEntityFormulas(String entityName) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
        Entity entity = persistenceUnit.getEntity(entityName);
        TraceService.get().trace("updateFormulas", "start updateFormulas", entityName, entity.getParent().getPath(), Level.INFO);
        try {
            persistenceUnit.updateFormulas(EntityFilters.byName(entityName), null);
            TraceService.get().trace("updateFormulas", "succeeded updateFormulas", entityName, entity.getParent().getPath(), Level.INFO);
        } catch (RuntimeException ex) {
            TraceService.get().trace("updateFormulas", "failed updateFormulas : " + ex.toString(), entityName, entity.getParent().getPath(), Level.WARNING);
            throw ex;
        }
    }

    private void checkNavigate(String entityName) {
        if (!CorePlugin.get().isCurrentSessionAdmin()) {
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(entityName);
        if (!pu.getSecurityManager().isAllowedNavigate(e)) {
            throw new SecurityException("Not Allowed");
        }
    }

    private void checkFindMany(String entityName) {
        if (!CorePlugin.get().isCurrentSessionAdmin()) {
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(entityName);
        if (!pu.getSecurityManager().isAllowedLoad(e) || !pu.getSecurityManager().isAllowedNavigate(e)) {
            throw new SecurityException("Not Allowed");
        }
    }

    private void checkFindOne(String entityName) {
        if (!CorePlugin.get().isCurrentSessionAdmin()) {
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(entityName);
        if (!pu.getSecurityManager().isAllowedLoad(e)) {
            throw new SecurityException("Not Allowed");
        }
    }
}
