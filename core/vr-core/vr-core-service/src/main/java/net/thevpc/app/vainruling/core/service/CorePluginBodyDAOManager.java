package net.thevpc.app.vainruling.core.service;

import net.thevpc.app.vainruling.VrEditorSearch;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.core.service.editor.AutoFilterData;
import net.thevpc.app.vainruling.core.service.util.I18n;
import net.thevpc.app.vainruling.core.service.util.VrUPAUtils;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.time.Chronometer;
import net.thevpc.common.util.Convert;
import net.thevpc.common.util.IntegerParserConfig;
import net.thevpc.upa.*;
import net.thevpc.upa.expressions.*;
import net.thevpc.upa.filters.EntityFilters;
import net.thevpc.upa.types.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.common.collections.MapUtils;
import net.thevpc.app.vainruling.core.service.editor.KeywordsEditorSearch;
import net.thevpc.app.vainruling.core.service.model.AppGender;
import net.thevpc.app.vainruling.core.service.model.AppUserType;
import net.thevpc.app.vainruling.core.service.util.ManyToManyMap;

class CorePluginBodyDAOManager extends CorePluginBody {

    private ManyToManyMap<String, String> vrEditorSearchFactoryToEntity = new ManyToManyMap<>();
    private Map<String, List<VrEditorSearch>> entityToVrEditorSearchFactoryList = new HashMap<>();

    public VrEditorSearch getEditorSearch(String beanName) {
        if (StringUtils.isBlank(beanName)) {
            return VrApp.getBean(KeywordsEditorSearch.class);
        }
        return VrApp.getBean(beanName);
    }

    public List<VrEditorSearch> findEntityEditorSearchs(String entityName) {
        LinkedHashSet<String> a = new LinkedHashSet<>(vrEditorSearchFactoryToEntity.getSecondValues(entityName));
        a.addAll(vrEditorSearchFactoryToEntity.getSecondValues(""));
        return a.stream().map(x -> (VrEditorSearch) VrApp.getBean(x)).collect(Collectors.toList());
    }

    public void addEntityEditorSearch(String entityName, String beanName) {
        VrEditorSearch c = getEditorSearch(beanName);
        if (StringUtils.isBlank(entityName) || entityName.equals("*")) {
            entityToVrEditorSearchFactoryList.clear();
            vrEditorSearchFactoryToEntity.add("", beanName);
        } else {
            entityToVrEditorSearchFactoryList.remove(entityName);
            vrEditorSearchFactoryToEntity.add(entityName, beanName);
        }
    }

    public <T> T findOrCreate(T o) {
        String fname = null;
        if (o == null) {
            throw new IllegalArgumentException("Null Object");
        }
        if (o instanceof AppUserType) {
            fname = "code";
        } else if (o instanceof AppUser) {
            fname = "login";
        } else if (o instanceof AppGender) {
            fname = "code";
        } else {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            Entity e = pu.getEntity(o.getClass());
            fname = e.getMainField().getName();
        }
        return findOrCreate(o, fname);
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

    public AutoFilterData getEntityAutoFilter(String entityName, String autoFilterName) {
        return getEntityFiltersMap(entityName).get(autoFilterName);
    }

    public Map<String, AutoFilterData> getEntityFiltersMap(String entityName) {
        Entity entity = UPA.getPersistenceUnit().getEntity(entityName);
        Map<String, AutoFilterData> cache = (Map<String, AutoFilterData>) entity.getProperties().getObject("cache.ui.auto-filters.map");
        if (cache != null) {
            for (AutoFilterData autoFilterData : cache.values()) {
                autoFilterData.setDefaultSelectedValue(getEntityAutoFilterDefaultSelectedValue(autoFilterData));
            }
            return cache;
        }
        Map<String, AutoFilterData> m = new HashMap<>();
        for (AutoFilterData d : getEntityAutoFilters(entityName)) {
            m.put(d.getName(), d);
        }
        cache = Collections.unmodifiableMap(m);
        entity.getProperties().setObject("cache.ui.auto-filters.map", cache);
        return cache;
    }

    public AutoFilterData[] getEntityAutoFilters(String entityName) {
        Entity entity = UPA.getPersistenceUnit().getEntity(entityName);
        AutoFilterData[] cache = (AutoFilterData[]) entity.getProperties().getObject("cache.ui.auto-filters.list");
        if (cache != null) {
            for (AutoFilterData autoFilterData : cache) {
                autoFilterData.setDefaultSelectedValue(getEntityAutoFilterDefaultSelectedValue(autoFilterData));
            }
            return Arrays.copyOf(cache, cache.length);
        }
        List<AutoFilterData> autoFilterDatasAll = new ArrayList<>();
        String all = StringUtils.trim(entity.getProperties().getString("ui.auto-filters"));
        if (!all.isEmpty()) {
            //this is a single value
            if (all.startsWith("{")) {
                AutoFilterData d = VrUtils.parseJSONObject(all, AutoFilterData.class);
                if (d != null) {
                    d.setEntityName(entityName);
                    d.setFormatType(getEntityAutoFilterFormatType(d));
                    autoFilterDatasAll.add(d);
                }
            } else {
                AutoFilterData[] all2 = VrUtils.parseJSONObject(all, AutoFilterData[].class);
                if (all2 != null) {
                    for (AutoFilterData d : all2) {
                        if (d != null) {
                            d.setEntityName(entityName);
                            d.setFormatType(getEntityAutoFilterFormatType(d));
                            autoFilterDatasAll.add(d);
                        }
                    }
                }
            }
        }
        //
        for (Map.Entry<String, Object> entry : entity.getProperties().toMap().entrySet()) {
            if (entry.getKey().startsWith("ui.auto-filter.")) {
                String name = entry.getKey().substring("ui.auto-filter.".length());
                AutoFilterData d = VrUtils.parseJSONObject((String) entry.getValue(), AutoFilterData.class);
                d.setName(name);
                d.setEntityName(entity.getName());
                d.setFormatType(getEntityAutoFilterFormatType(d));
                autoFilterDatasAll.add(d);
            }
        }
        for (AutoFilterData autoFilterData : autoFilterDatasAll) {
            autoFilterData.setDefaultSelectedValue(getEntityAutoFilterDefaultSelectedValue(autoFilterData));
        }
        VrUtils.sortPreserveIndex(autoFilterDatasAll, null);
        cache = autoFilterDatasAll.toArray(new AutoFilterData[autoFilterDatasAll.size()]);
        entity.getProperties().setObject("cache.ui.auto-filters.list", cache);
        return Arrays.copyOf(cache, cache.length);
    }

    public String getEntityAutoFilterFormatType(String entityName, String autoFilterName) {
        AutoFilterData a = getEntityAutoFilter(entityName, autoFilterName);
        return CorePluginBodyDAOManager.this.getEntityAutoFilterFormatType(a);
    }

    public String getEntityAutoFilterFormatType(AutoFilterData a) {
        DataType curr = getEntityAutoFilterDataType(a);
        if (curr instanceof KeyType) {
            return "key";
        } else if (curr instanceof EnumType) {
            return "enum";
        } else if (curr instanceof StringType) {
            return "string";
        } else if (curr instanceof BooleanType) {
            return "boolean";
        } else if (curr instanceof YearType || (curr instanceof TemporalType && "year".equalsIgnoreCase(a.getFormatType()))) {
            return "year";
        } else if (curr instanceof MonthType || (curr instanceof TemporalType && "month".equalsIgnoreCase(a.getFormatType()))) {
            return "month";
        } else if (curr instanceof DateType || (curr instanceof TemporalType && "date".equalsIgnoreCase(a.getFormatType()))) {
            return "date";
        } else if (curr instanceof TimeType || (curr instanceof TemporalType && "time".equalsIgnoreCase(a.getFormatType()))) {
            return "time";
        } else if (curr instanceof TemporalType) {
            return "temporal";
        } else {
            throw new IllegalArgumentException("Unsupported DataType " + (curr == null ? "null" : curr.getClass().getSimpleName()) + " for entity auto filter " + a.getEntityName() + "." + a.getName());
        }
    }

    public DataType getEntityAutoFilterDataType(String entityName, String autoFilterName) {
        AutoFilterData a = getEntityAutoFilter(entityName, autoFilterName);
        return getEntityAutoFilterDataType(a);
    }

    public DataType getEntityAutoFilterDataType(AutoFilterData a) {
        String expr = a.getExpr();
        if (!expr.matches("[a-zA-Z0-9.]+")) {
            throw new IllegalArgumentException("Invalid Auto Filter Expr " + expr + " for Entity " + a.getEntityName());
        }
        try {
            Entity entity = UPA.getPersistenceUnit().getEntity(a.getEntityName());
//            I18n i18n = VrApp.getBean(I18n.class);
            DataType curr = entity.getDataType();
            String evalLabel = entity.getTitle();
            String[] splitted = expr.split("\\.");
            for (int i = 0; i < splitted.length; i++) {
                String s = splitted[i];
                if (i == 0 && s.equals("this")) {
                    //ignore
                } else if (curr instanceof KeyType) {
                    Field field = ((KeyType) curr).getEntity().getField(s);
                    evalLabel = field.getTitle();
                    if (field.isManyToOne()) {
                        Entity targetEntity = field.getManyToOneRelationship().getTargetEntity();
                        if (targetEntity.isHierarchical()) {
                            //TODO process hierarchical search
                        }
                        curr = (KeyType) targetEntity.getDataType();
                    } else if (field.getDataType() instanceof EnumType) {
                        curr = field.getDataType();
                    } else {
                        curr = field.getDataType();
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported entity auto filter expression " + expr + " for entity auto filter " + a.getEntityName() + "." + a.getName());
                }
            }
            if (StringUtils.isBlank(a.getLabel())) {
//            String label = i18n.getOrNull("UI.Entity." + entity.getName() + ".ui.auto-filter.class");
                a.setLabel(evalLabel);
//                        if(StringUtils.isBlank(label)){
//                            if(curr instanceof KeyType){
//                                autoFilterData.setLabel(i18n.get(((KeyType) curr).getEntity()));
//                            }else {
//                                autoFilterData.setLabel(autoFilterData.getName());
//                            }
//                        }
            }
            return curr;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid Auto Filter Expr " + expr + " for Entity " + a.getEntityName(), ex);
        }
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

    public Object save(Object t) {
        return save(null, t);
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
        } else {
            pu.merge(entityName, t);
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

    public RemoveTrace remove(Class entityType, Object id) {
        return remove(UPA.getPersistenceUnit().getEntity(entityType).getName(), id);
    }

    public RemoveTrace remove(String entityName, Object id) {
        if (entityName == null) {
            //TODO REMOVE THIS!!!!
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
        Chronometer c = Chronometer.start();
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
        sb.append(aliasName + ".").append(mainField.getName());
        while (mainField.getDataType() instanceof ManyToOneType) {
            Entity t = ((ManyToOneType) mainField.getDataType()).getRelationship().getTargetEntity();
            mainField = t.getMainField();
            sb.append(".").append(mainField.getName());
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
        Chronometer c = Chronometer.start();
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
        Chronometer c = Chronometer.start();
        List<NamedId> entityList = pu.createQuery(q)
                .getResultList(NamedId.class);
        entityList.size();
        c.stop();
        return entityList;
    }

    public List<NamedId> findAllNamedIds(String entityName, String criteria, String textSearchType, String textSearchExpression, Map<String, Object> parameters) {
        checkNavigate(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        final String aliasName = "o";

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

        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        VrEditorSearch u = null;
        if (!StringUtils.isBlank(textSearchType)) {
            u = getEditorSearch(textSearchType);
        }
        if (u != null) {
            String c = u.createPreProcessingExpression(entityName, parameters, "os");
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            q.where(filterExpression);
        }
        Query query = pu.createQuery(q.toString());
        if (parameters != null) {
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                query.setParameter(pp.getKey(), pp.getValue());
            }
        }
        Chronometer c = Chronometer.start();
        List<NamedId> entityList = pu.createQuery(q)
                .getResultList(NamedId.class);
        entityList.size();
        c.stop();
        return entityList;
    }

    public List<Document> findAllDocuments(String entityName, String[] fields, String criteria, String textSearchType, String textSearchExpression, Map<String, Object> parameters) {
        checkNavigate(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        final String aliasName = "o";

        Entity entity = pu.getEntity(entityName);
        Select q = new Select();

        if (fields == null || fields.length == 0) {
            q.field(aliasName);
        } else {
            for (String field : fields) {
                q.field(aliasName + "." + field);
            }
        }
        q.from(entityName, aliasName);
        q.orderBy(entity.getListOrder());

        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        VrEditorSearch u = null;
        if (!StringUtils.isBlank(textSearchType)) {
            u = getEditorSearch(textSearchType);
        }
        if (u != null) {
            String c = u.createPreProcessingExpression(entityName, parameters, "os");
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            q.where(filterExpression);
        }
        Query query = pu.createQuery(q.toString());
        if (parameters != null) {
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                query.setParameter(pp.getKey(), pp.getValue());
            }
        }
        Chronometer c = Chronometer.start();
        List<Document> entityList = query
                .getDocumentList();
        entityList.size();
        c.stop();
        return entityList;
    }

    public long findCountByFilter(String entityName, String criteria, String textSearchType, String textSearchExpression, Map<String, Object> parameters) {
        checkNavigate(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String qq = "Select count(1) from " + entityName + " o ";
        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        VrEditorSearch u = null;
        if (!StringUtils.isBlank(textSearchType)) {
            u = getEditorSearch(textSearchType);
        }
        if (u != null) {
            String c = u.createPreProcessingExpression(entityName, parameters, "os");
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

//    public List<Object> findByFilter(String entityName, String criteria, ObjSearch objSearch, Map<String, Object> parameters) {
//        checkFindMany(entityName);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        Entity entity = pu.getEntity(entityName);
//        QueryBuilder q = pu
//                .createQueryBuilder(entityName)
//                .setEntityAlias("o")
//                .orderBy(entity.getListOrder());
//        Expression filterExpression = null;
//        if (criteria != null) {
//            filterExpression = new UserExpression(criteria);
//        }
//        if (objSearch != null) {
//            String c = objSearch.createPreProcessingExpression(entityName, parameters, "os");
//            if (c != null) {
//                if (filterExpression == null) {
//                    filterExpression = new UserExpression(c);
//                } else {
//                    filterExpression = new And(filterExpression, new UserExpression(c));
//                }
//            }
//        }
//        if (filterExpression != null) {
//            q.byExpression(filterExpression);
//        }
//        if (parameters != null) {
//            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
//                q.setParameter(pp.getKey(), pp.getValue());
//            }
//        }
//        List<Object> list = q.getResultList();
//        if (objSearch != null) {
//            list = objSearch.filterList(list, entityName);
//        }
//        return list;
//    }
    public List<Document> findDocumentsByAutoFilter(String entityName, Map<String, String> autoFilterValues, String textSearch, String exprFilter) {
        String _listFilter = exprFilter;
        if (!StringUtils.isBlank(_listFilter)) {
            _listFilter = "(" + _listFilter + ")";
        } else {
            _listFilter = "";
        }
        HashMap<String, Object> parameters = new HashMap<>();
        int autoFilterIndex = 1;
        if (autoFilterValues != null) {
            for (AutoFilterData o : getEntityAutoFilters(entityName)) {
                String filterExpression = createEntityAutoFilterExpression(entityName, o.getName(), parameters, "af" + autoFilterIndex, autoFilterValues.get(o.getName()));
                if (!StringUtils.isBlank(filterExpression)) {
                    if (!StringUtils.isBlank(_listFilter)) {
                        _listFilter += " and ";
                    }
                    _listFilter += "(" + filterExpression + ")";
                }
                autoFilterIndex++;
            }
        }
        return findDocumentsByFilter(entityName, _listFilter, null, textSearch, parameters);
    }

    public List<Document> findDocumentsByFilter(String entityName, String criteria, String searchType, String textSearch, Map<String, Object> parameters) {
        checkFindMany(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder q = pu
                .createQueryBuilder(entityName)
                .setEntityAlias("o")
                .orderBy(entity.getListOrder());
        Expression filterExpression = null;
        if (!org.apache.commons.lang.StringUtils.isBlank(criteria)) {
            filterExpression = new UserExpression(criteria);
        }
        VrEditorSearch searchFactory = getEditorSearch(searchType);
        String c = searchFactory.createPreProcessingExpression(entityName, parameters, "os");
        if (c != null) {
            if (filterExpression == null) {
                filterExpression = new UserExpression(c);
            } else {
                filterExpression = new And(filterExpression, new UserExpression(c));
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
        list = searchFactory.filterDocumentList(list, entityName, textSearch);
        return list;
    }

//    public VrEditorSearchFactory resolveEntityEditorSearchFactory(String entityName, String searchTypeId) {
//        Map<String, VrEditorSearchFactory> p = resolveEntityEditorSearchFactory(entityName);
//        if (p == null) {
//            throw new IllegalArgumentException("Invalid Entity " + entityName);
//        }
//        VrEditorSearchFactory u = p.get(searchTypeId);
//        if (u != null) {
//            throw new IllegalArgumentException("Invalid searchType " + searchTypeId);
//        }
//        return VrUPAUtils.resolveCachedEntityPropertyInstance(UPA.getPersistenceUnit().getEntity(entityName), UIConstants.ENTITY_TEXT_SEARCH_FACTORY, VrEditorSearchFactory.class);
//    }
//
//    public String createSearchHelperString(String name, String entityName, String searchTypeId) {
//        String d = "Tapez ici les mots clés de recherche.";
//        if (StringUtils.isBlank(entityName)) {
//            return d;
//        }
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        Entity entity = pu.getEntity(entityName);
//        VrEditorSearchFactory g = getEditorSearchFactory(searchTypeId);
//        String s = null;
//        if (g != null) {
//            s = g.createHelperString(name, entity);
//        }
//        if (StringUtils.isBlank(s)) {
//            s = d;
//        }
//        return s;
//    }
//    public VrEditorSearch createSearch(String name, String entityName, String expression, String searchTypeId) {
//        if (StringUtils.isBlank(expression)) {
//            return new EntityEditorSimpleSearch(null);
//        }
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        Entity entity = pu.getEntity(entityName);
//        VrEditorSearchFactory g = getEditorSearchFactory(searchTypeId);
//        VrEditorSearch objSearch = null;
//        if (g != null) {
//            objSearch = g.create(name, entity, expression);
//        }
//        if (objSearch == null) {
//            return new EntityEditorSimpleSearch(expression);
//        }
//        return objSearch;
//    }
    public List<Object> findAll(String entityName) {
        checkFindMany(entityName);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findAll(entityName);
    }

    public List<Object> findByField(Class type, String field, Object value) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        checkFindMany(entity.getName());
        return pu.findAllByField(type, field, value);
    }

    public void updateObjectFormulas(String entityName, Object id) {
        PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
        persistenceUnit.updateFormulas(entityName, id);
    }

    public void updateObjectValue(String entityName, Object id) {
        if (id != null) {
            PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
            Entity entity = persistenceUnit.getEntity(entityName);
            final Document doc = entity.findDocumentById(id);
            if (doc != null) {
                entity.merge(doc);
            }
        }
    }

    public void updateEntityFormulas(String entityName) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
        Entity entity = persistenceUnit.getEntity(entityName);
        Map<String, Object> msg = MapUtils.map("name", entityName, "title", entity.getTitle());
        TraceService.get().trace("System.entities.entity-updated-formulas", "start", msg,
                entity.getParent().getPath(), Level.INFO);
        try {
            persistenceUnit.updateAllFormulas(EntityFilters.byName(entityName), null);
            TraceService.get().trace("System.entities.entity-updated-formulas", "success", msg, entity.getParent().getPath(), Level.INFO);
        } catch (RuntimeException ex) {
            msg = MapUtils.map("name", entityName, "title", entity.getTitle(), "error", ex.getMessage());
            TraceService.get().trace("System.entities.entity-updated-formulas", "error", msg, entity.getParent().getPath(), Level.WARNING);
            throw ex;
        }
    }

    public void updateAllEntitiesFormulas() {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
        Map<String, Object> msg = MapUtils.map();
        TraceService.get().trace("System.entities.entity-updated-formulas", "start", msg,
                "/System", Level.INFO);
        try {
            persistenceUnit.updateAllFormulas();
            TraceService.get().trace("System.entities.entity-updated-formulas", "success", msg, "/System", Level.INFO);
        } catch (RuntimeException ex) {
            TraceService.get().trace("System.entities.entity-updated-formulas", "error", msg, "/System", Level.WARNING);
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

    public String createEntityAutoFilterExpression(String entityName, String autoFilterName, Map<String, Object> parameters, String paramPrefix, String selectedString) {
        AutoFilterData a = getEntityAutoFilter(entityName, autoFilterName);
        if (a == null) {
            return null;
        }
        DataType dataType = getEntityAutoFilterDataType(entityName, autoFilterName);
        if (!StringUtils.isBlank(selectedString)) {
            if ("<<null>>".equals(selectedString)) {
                switch (a.getFormatType()) {
                    case "key":
                    case "enum":
                    case "boolean":
                    case "string":
                    case "year":
                    case "month":
                    case "date":
                    case "time":
                    case "temporal": {
                        parameters.put(paramPrefix, null);
                        return a.getExpr() + "=:" + paramPrefix;
                    }
                    default: {
                        throw new IllegalArgumentException("Unsupported entity auto filter Format Type " + a.getFormatType() + " for entity auto filter " + a.getEntityName() + "." + a.getName());
                    }
                }
            } else {
                switch (a.getFormatType()) {
                    case "key": {
                        int id = Convert.toInt(selectedString, IntegerParserConfig.LENIENT_F);
                        Entity entity = ((KeyType) dataType).getEntity();
                        Object entityInstance = entity.findById(id);
                        parameters.put(paramPrefix, entityInstance);
                        //IsHierarchyDescendant(:p,a,Node)
                        if (entity.isHierarchical()) {
                            return "IsHierarchyDescendant(:" + paramPrefix + " , " + a.getExpr() + "," + entity.getName() + ")";
                        } else {
                            return a.getExpr() + "=:" + paramPrefix;
                        }
                    }
                    case "enum": {
                        if (selectedString.startsWith("\"") && selectedString.endsWith("\"") && selectedString.length() >= 2) {
                            selectedString = selectedString.substring(1, selectedString.length() - 1);
                        }
                        parameters.put(paramPrefix, ((EnumType) dataType).parse(selectedString));
                        return a.getExpr() + "=:" + paramPrefix;
                    }
                    case "string": {
                        parameters.put(paramPrefix, selectedString);
                        return a.getExpr() + "=:" + paramPrefix;
                    }
                    case "boolean": {
                        parameters.put(paramPrefix, Boolean.valueOf(selectedString));
                        return a.getExpr() + "=:" + paramPrefix;
                    }
                    case "year": {
                        parameters.put(paramPrefix, Integer.valueOf(selectedString));
                        return "year(" + a.getExpr() + ")=:" + paramPrefix;
                    }
                    case "month": {
                        net.thevpc.upa.types.Month m = net.thevpc.upa.types.Month.valueOf(selectedString);
                        parameters.put(paramPrefix + "_a", m.getYearValue());
                        parameters.put(paramPrefix + "_b", m.getMonthValue());
                        return "( (year(" + a.getExpr() + ")=:" + paramPrefix + "_a) and (month(" + a.getExpr() + ")=:" + paramPrefix + "_b)";
                    }
                    case "date": {
                        net.thevpc.upa.types.Date m = net.thevpc.upa.types.Date.valueOf(selectedString);
                        parameters.put(paramPrefix + "_a", m.getYearValue());
                        parameters.put(paramPrefix + "_b", m.getMonthValue());
                        parameters.put(paramPrefix + "_c", m.getDateValue());
                        return "( (year(" + a.getExpr() + ")=:" + paramPrefix + "_a) and (month(" + a.getExpr() + ")=:" + paramPrefix + "_b)  and (day(" + a.getExpr() + ")=:" + paramPrefix + "_c)";
                    }
                    case "time": {
                        net.thevpc.upa.types.Time m = net.thevpc.upa.types.Time.valueOf(selectedString);
                        parameters.put(paramPrefix + "_a", m.getHourValue());
                        parameters.put(paramPrefix + "_b", m.getMinuteValue());
                        parameters.put(paramPrefix + "_c", m.getSecondValue());
                        return "( (hour(" + a.getExpr() + ")=:" + paramPrefix + "_a) and (minute(" + a.getExpr() + ")=:" + paramPrefix + "_b)  and (second(" + a.getExpr() + ")=:" + paramPrefix + "_c)";
                    }
                    case "temporal": {
                        net.thevpc.upa.types.Date m = net.thevpc.upa.types.Date.valueOf(selectedString);
                        parameters.put(paramPrefix + "_a", m.getYearValue());
                        parameters.put(paramPrefix + "_b", m.getMonthValue());
                        parameters.put(paramPrefix + "_c", m.getDateValue());
                        return "( (year(" + a.getExpr() + ")=:" + paramPrefix + "_a) and (month(" + a.getExpr() + ")=:" + paramPrefix + "_b)  and (day(" + a.getExpr() + ")=:" + paramPrefix + "_c)";
                    }
                    default: {
                        throw new IllegalArgumentException("Unsupported entity auto filter Format Type " + a.getFormatType() + " for entity auto filter " + a.getEntityName() + "." + a.getName());
                    }
                }
            }
        }
        return null;
    }

    public NamedId getEntityAutoFilterDefaultSelectedValue(String entityName, String autoFilterName) {
        AutoFilterData autoFilterData = getEntityAutoFilter(entityName, autoFilterName);
        return getEntityAutoFilterDefaultSelectedValue(autoFilterData);
    }

    public NamedId getEntityAutoFilterDefaultSelectedValue(AutoFilterData autoFilterData) {
        String initial = autoFilterData.getInitial();
        DataType filterType = getEntityAutoFilterDataType(autoFilterData);
        if (initial != null) {
            if ("".equals(initial)) {
                return null;
            }
            switch (autoFilterData.getFormatType()) {
                case "enum": {
                    EnumType t = (EnumType) filterType;
                    Object value = t.parse(initial);
                    I18n i18n = I18n.get();
                    return (new NamedId(VrUPAUtils.objToJson(value, t).toString(), i18n.getEnum(value)));
                }
                case "string": {
                    return (new NamedId(initial, initial));
                }
            }
            throw new IllegalArgumentException("Not Supported yet initial value " + initial + " for auto-filter : " + autoFilterData.getEntityName() + "." + autoFilterData.getName());
        }
        if (filterType instanceof KeyType) {
            Entity ee = ((KeyType) filterType).getEntity();
            Object defaultSelection = null;
            if (ee.getEntityType().equals(AppPeriod.class)) {
                defaultSelection = getContext().getCorePlugin().getCurrentPeriod();
            } else if (ee.getEntityType().equals(AppDepartment.class)) {
                AppUser u = getContext().getCorePlugin().getCurrentUser();
                defaultSelection = u == null ? null : u.getDepartment();
            } else if (ee.getEntityType().equals(AppUser.class)) {
                AppUser u = getContext().getCorePlugin().getCurrentUser();
                defaultSelection = u == null ? null : u;
            }
            if (defaultSelection != null) {
                Object theId = ee.getBuilder().objectToId(defaultSelection);
                return new NamedId(theId, String.valueOf(theId));
            }
        }
        if (autoFilterData.getEntityName().equals("AppTrace")) {
            if (autoFilterData.getName().equals("user")) {
                AppUser u = getContext().getCorePlugin().getCurrentUser();
                String n = u == null ? null : u.getLogin();
                return new NamedId(n, n);
            }
        }
        return null;
    }

    public List<NamedId> getEntityAutoFilterValues(String entityName, String autoFilterName) {
        AutoFilterData a = getEntityAutoFilter(entityName, autoFilterName);
        DataType curr = getEntityAutoFilterDataType(entityName, autoFilterName);
        List<NamedId> f = new ArrayList<>();
        f.add(new NamedId(String.valueOf(""), "-------"));
        f.add(new NamedId(String.valueOf("<<null>>"), "<valeur vide>"));
        switch (a.getFormatType()) {
            case "key": {
                f.addAll(getDataTypeValues(curr));
                break;
            }
            case "enum": {
                f.addAll(getDataTypeValues(curr));
                break;
            }
            case "string": {
                List<String> values = UPA.getPersistenceUnit().createQuery("Select distinct(" + a.getExpr() + ") from " + a.getEntityName())
                        .getResultList();
                Collections.sort(values, VrUtils.NULL_AS_EMPTY_STRING_COMPARATOR);
                for (String value : values) {
                    if (value != null) {
                        f.add(new NamedId(value, value));
                    }
                }
                break;
            }
            case "boolean": {
                f.add(new NamedId(true, "oui"));
                f.add(new NamedId(false, "non"));
                break;
            }
            case "year": {
                List<java.util.Date> values = UPA.getPersistenceUnit().createQuery("Select distinct(" + a.getExpr() + ") from " + a.getEntityName())
                        .getResultList();
                SortedSet<net.thevpc.upa.types.Year> values2 = new TreeSet<net.thevpc.upa.types.Year>();
                for (java.util.Date value : values) {
                    if (value != null) {
                        values2.add(new net.thevpc.upa.types.Year(value));
                    } else {
                        values2.add(null);
                    }
                }
                for (net.thevpc.upa.types.Year value : values2) {
                    if (value != null) {
                        f.add(new NamedId(value, value.toString()));
                    }
                }
                break;
            }
            case "month": {
                List<java.util.Date> values = UPA.getPersistenceUnit().createQuery("Select distinct(" + a.getExpr() + ") from " + a.getEntityName())
                        .getResultList();
                SortedSet<net.thevpc.upa.types.Month> values2 = new TreeSet<net.thevpc.upa.types.Month>();
                for (java.util.Date value : values) {
                    if (value != null) {
                        values2.add(new net.thevpc.upa.types.Month(value));
                    } else {
                        values2.add(null);
                    }
                }
                for (net.thevpc.upa.types.Month value : values2) {
                    if (value != null) {
                        f.add(new NamedId(value, value.toString()));
                    }
                }
                break;
            }
            case "date": {
                List<java.util.Date> values = UPA.getPersistenceUnit().createQuery("Select distinct(" + a.getExpr() + ") from " + a.getEntityName())
                        .getResultList();
                SortedSet<net.thevpc.upa.types.Date> values2 = new TreeSet<net.thevpc.upa.types.Date>();
                for (java.util.Date value : values) {
                    if (value != null) {
                        values2.add(new net.thevpc.upa.types.Date(value));
                    } else {
                        values2.add(null);
                    }
                }
                for (net.thevpc.upa.types.Date value : values2) {
                    if (value != null) {
                        f.add(new NamedId(value, value.toString()));
                    }
                }
                break;
            }
            case "time": {
                List<java.util.Date> values = UPA.getPersistenceUnit().createQuery("Select distinct(" + a.getExpr() + ") from " + a.getEntityName())
                        .getResultList();
                SortedSet<net.thevpc.upa.types.Time> values2 = new TreeSet<net.thevpc.upa.types.Time>();
                for (java.util.Date value : values) {
                    if (value != null) {
                        values2.add(new net.thevpc.upa.types.Time(value));
                    } else {
                        values2.add(null);
                    }
                }
                for (net.thevpc.upa.types.Time value : values2) {
                    if (value != null) {
                        f.add(new NamedId(value, value.toString()));
                    }
                }
                break;
            }
            case "temporal": {
                List<java.util.Date> values = UPA.getPersistenceUnit().createQuery("Select distinct(" + a.getExpr() + ") from " + a.getEntityName())
                        .getResultList();
                SortedSet<net.thevpc.upa.types.Date> values2 = new TreeSet<net.thevpc.upa.types.Date>();
                for (java.util.Date value : values) {
                    if (value != null) {
                        values2.add(new net.thevpc.upa.types.Date(value));
                    } else {
                        values2.add(null);
                    }
                }
                for (net.thevpc.upa.types.Date value : values2) {
                    if (value != null) {
                        f.add(new NamedId(value, value.toString()));
                    }
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported Format Type " + a.getFormatType() + " for entity auto filter " + a.getEntityName() + "." + a.getName());
            }
        }
        return f;
    }

    public List<NamedId> getFieldValues(String entityName, String fieldName) {
        return getDataTypeValues(UPA.getPersistenceUnit().getEntity(entityName).getField(fieldName).getDataType());
    }

    public List<NamedId> getDataTypeValues(DataType type) {
        if (type instanceof EnumType) {
            EnumType t = (EnumType) type;
            I18n i18n = VrApp.getBean(I18n.class);
            List<NamedId> list = new ArrayList<>();
            for (Object value : t.getValues()) {
                list.add(new NamedId(VrUPAUtils.objToJson(value, type).toString(), i18n.getEnum(value)));
            }
            return list;
        }
        if (type instanceof KeyType) {
//            List<PropertyView> updatablePropertyViews = new ArrayList<>();
//            List<PropertyView> dependentPropertyViews = new ArrayList<>();
//            String componentId = "unknown";

//            if (propertyView != null) {
//                updatablePropertyViews = propertyView.getUpdatablePropertyViews();
//                dependentPropertyViews = propertyView.getDependentPropertyViews();
//                componentId = propertyView.getComponentId();
//            }
//            final EditorCtrl editorCtrl = VrApp.getBean(EditorCtrl.class);
            Document currentDoc = null;//editorCtrl.getModel().getCurrentDocument();

            final Map<String, Object> constraints = new HashMap<>();
//            Map<String, Object> currentValues = null;//editorCtrl.currentViewToMap();
//            for (PropertyView dependentPropertyView : dependentPropertyViews) {
//                Object v = currentValues.get(dependentPropertyView.getComponentId());
//                if (v != null) {
//                    ManyToOneTypePropertyView etpv = (ManyToOneTypePropertyView) dependentPropertyView;
//                    Entity me = etpv.getTargetEntity();
//                    Object mid = (v instanceof NamedId) ? ((NamedId) v).getId() : me.getBuilder().objectToId(v);
//                    String expr = etpv.getComponentId().substring(componentId.length() + 1);
//                    constraints.put(expr + "." + me.getIdFields().get(0).getName(), mid);
//                }
//            }
            final KeyType mtype = (KeyType) type;
            final Entity me = mtype.getEntity();
//            return viewContext.getCacheItem("EntityPropertyViewValuesProvider." + me.getName() + ":" + constraints, new Action<List<NamedId>>() {
//                @Override
//                public List<NamedId> run() {
            return findAllNamedIds(mtype.getEntity().getName(), constraints, currentDoc);
//                }
//            });
        }
        if (type instanceof ManyToOneType) {
//            ManyToOneTypePropertyView ev = (ManyToOneTypePropertyView) propertyView;
//            List<PropertyView> updatablePropertyViews = propertyView.getUpdatablePropertyViews();
//            List<PropertyView> dependentPropertyViews = propertyView.getDependentPropertyViews();

//            CorePlugin core = VrApp.getBean(CorePlugin.class);
//            final EditorCtrl editorCtrl = VrApp.getBean(EditorCtrl.class);
//            Map<String, Object> currentValues = editorCtrl.currentViewToMap();
            Document currentDoc = null;//editorCtrl.getModel().getCurrentDocument();
            final Map<String, Object> constraints = new HashMap<>();
//            for (PropertyView dependentPropertyView : dependentPropertyViews) {
//                Object v = currentValues.get(dependentPropertyView.getComponentId());
//                if (v != null) {
//                    ManyToOneTypePropertyView etpv = (ManyToOneTypePropertyView) dependentPropertyView;
//                    Entity me = etpv.getTargetEntity();
//                    Object mid = (v instanceof NamedId) ? ((NamedId) v).getId() : me.getBuilder().objectToId(v);
//                    String expr = etpv.getComponentId().substring(propertyView.getComponentId().length() + 1);
//                    constraints.put(expr + "." + me.getIdFields().get(0).getName(), mid);
//                }
//            }
            final ManyToOneType mtype = (ManyToOneType) type;
            PersistenceUnit pu = UPA.getPersistenceUnit();
            final Entity me = pu.getEntity(mtype.getTargetEntityName());
            if (!(mtype.getRelationship() instanceof ManyToOneRelationship && ((ManyToOneRelationship) mtype.getRelationship()).getFilter() != null)) {
//                List<NamedId> cacheItem = viewContext.getCacheItem("EntityPropertyViewValuesProvider." + me.getName() + ":" + constraints, new Action<List<NamedId>>() {
//                    @Override
//                    public List<NamedId> run() {
                List<NamedId> cacheItem = findAllNamedIdsByRelationship(mtype.getRelationship().getName(), constraints, currentDoc);
//                    }
//                });
                List<NamedId> cacheItem2 = new ArrayList<>(cacheItem.size());
                for (NamedId namedId : cacheItem) {
                    Object id = namedId.getId();
                    Object id2 = VrUPAUtils.objToJson(id, me.getDataType()).toString();
                    cacheItem2.add(new NamedId(id2, namedId.getName()));
                }
                return cacheItem2;
            }
            return findAllNamedIdsByRelationship(mtype.getRelationship().getName(), constraints, currentDoc);

        }
        return null;
    }
}
