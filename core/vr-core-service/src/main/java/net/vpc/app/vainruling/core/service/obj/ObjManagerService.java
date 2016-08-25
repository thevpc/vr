/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.obj;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.common.util.Chronometer;
import net.vpc.upa.*;
import net.vpc.upa.expressions.*;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.ManyToOneType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

/**
 * @author taha.bensalah@gmail.com
 */
@Service
public class ObjManagerService {

    @Autowired
    CorePlugin core;
    @Autowired
    TraceService trace;

    public Object resolveId(String entityName, Object t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        if (t instanceof Record) {
            return entity.getBuilder().recordToId((Record) t);
        }
        return entity.getBuilder().objectToId(t);
    }

    public void save(String entityName, Object t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Object id = resolveId(entityName, t);
        List<Field> pf = entity.getPrimaryFields();
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
            pu.persist(entityName, t);
//            trace.inserted(t, getClass(), Level.FINE);
        } else {
            pu.merge(entityName, t);
//            trace.updated(t, old, getClass(), Level.FINE);
        }
    }

    public void erase(String entityName, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        if (trace.accept(entity)) {
            trace.removed(entityName, pu.findById(entityName, id), entity.getParent().getPath(), Level.FINE);
        }
        pu.remove(entityName, RemoveOptions.forId(id));
    }

    public boolean isSoftRemovable(String entityName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        return entity.containsField("deleted");
    }

    public void remove(String entityName, Object id) {
        if (!isSoftRemovable(entityName)) {
            erase(entityName, id);
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Record t = pu.findRecordById(entityName, id);
        if (t != null) {
            //check if already soft deleted
            Boolean b = (Boolean) entity.getField("deleted").getValue(t);
            if (b != null && b.booleanValue()) {
                erase(entityName, id);
            } else {
                UserSession session = VrApp.getContext().getBean(UserSession.class);
                if (entity.containsField("deleted")) {
                    t.setBoolean("deleted", true);
                }
                if (entity.containsField("deletedBy")) {
                    t.setString("deletedBy", session.getUser().getLogin());
                }
                if (entity.containsField("deletedOn")) {
                    t.setDate("deletedOn", new Timestamp(System.currentTimeMillis()));
                }
                pu.merge(entityName, t);
                if (trace.accept(entity)) {
                    trace.softremoved(entityName, pu.findById(entityName, id), entity.getParent().getPath(), Level.FINE);
                }
            }
        }
    }

    public String getObjectName(String entityName, Object obj) {
        if (obj == null) {
            return "NO_NAME";
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Field mf = entity.getMainField();
        if (mf == null) {
            return obj.toString();
        }
        return String.valueOf(entity.getBuilder().objectToRecord(obj, true).getObject(mf.getName()));
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
//                Record r = entity.getBuilder().objectToRecord(t, true);
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
    public void archive(String entityName, Object object) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Object id = resolveId(entityName, object);
        Object t = pu.findById(entityName, id);
        if (t != null) {
            Record r = entity.getBuilder().objectToRecord(t, true);
            if (entity.containsField("archived")) {
                r.setBoolean("archived", true);
            }
//            Object old = pu.findById(type, id);
            pu.merge(entityName, t);
            if (trace.accept(entity)) {
                trace.archived(entityName, pu.findById(entityName, id), entity.getParent().getPath(), Level.FINE);
            }
//            trace.updated(t, old, getClass(), Level.FINE);
        }

//        invokeEntityAction(type, "Archive", object, null);
    }

    public Object find(Class type, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(type, id);
    }

    public Object find(String entityName, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(entityName, id);
    }

    public Record findRecord(String entityName, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findRecordById(entityName, id);
    }

    public boolean isArchivable(String entityName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        return entity.containsField("archived");
    }

    public List<Object> findAll(String entityName, Map<String, Object> criteria) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder cc = pu.createQueryBuilder(entityName)
                .orderBy(entity.getListOrder())
                .setEntityAlias("o");
        if (criteria != null) {
            for (Map.Entry<String, Object> entrySet : criteria.entrySet()) {
                cc.byExpression(new Equals(new UserExpression("o." + entrySet.getKey()), new Literal(entrySet.getValue(), null)));
            }
        }
        Chronometer c = new Chronometer();
        List<Object> entityList = cc
                .getResultList();
        entityList.size();
        c.stop();
        return entityList;
    }

    public List<NamedId> findAllNamedIds(Relationship r, Map<String, Object> criteria, Object currentInstance) {
        String entityName = r.getTargetEntity().getName();
        final String aliasName = "o";
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Select q = new Select();

        Field primaryField = entity.getPrimaryFields().get(0);
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
        q.where(r.createTargetListExpression(currentInstance, aliasName));
        q.where(where);
        Chronometer c = new Chronometer();
        List<NamedId> entityList = pu.createQuery(q)
                .getTypeList(NamedId.class);
        entityList.size();
        c.stop();
        return entityList;
    }

    public List<NamedId> findAllNamedIds(Entity r, Map<String, Object> criteria, Object currentInstance) {
        String entityName = r.getName();
        final String aliasName = "o";
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Select q = new Select();

        Field primaryField = entity.getPrimaryFields().get(0);
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
                .getTypeList(NamedId.class);
        entityList.size();
        c.stop();
        return entityList;
    }

    public long findCountByFilter(String entityName, String criteria, ObjSearch objSearch,Map<String,Object> parameters) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String qq = "Select count(1) from " + entityName + " o ";
        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName,parameters,"os");
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
        if(parameters!=null){
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                query.setParameter(pp.getKey(),pp.getValue());
            }
        }
        Number nn = (Number) query.getSingleValue();
        return nn.longValue();
    }

    public List<Object> findByFilter(String entityName, String criteria, ObjSearch objSearch,Map<String,Object> parameters) {
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
            String c = objSearch.createPreProcessingExpression(entityName,parameters,"os");
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
        if(parameters!=null){
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                q.setParameter(pp.getKey(),pp.getValue());
            }
        }
        List<Object> list = q.getResultList();
        if (objSearch != null) {
            list = objSearch.filterList(list, entityName);
        }
        return list;
    }

    public List<Record> findRecordsByFilter(String entityName, String criteria, ObjSearch objSearch,Map<String,Object> parameters) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder q = pu
                .createQueryBuilder(entityName)
                .setEntityAlias("o")
                .orderBy(entity.getListOrder());
        Expression filterExpression = null;
        if (!StringUtils.isEmpty(criteria)) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName,parameters,"os");
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
        if(parameters!=null){
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                q.setParameter(pp.getKey(),pp.getValue());
            }
        }
        List<Record> list = q.getRecordList();
        if (objSearch != null) {
            list = objSearch.filterList(list, entityName);
        }
        return list;
    }

    public List<Object> findAll(String type) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        return pu.createQueryBuilder(type)
                .orderBy(entity.getListOrder())
                .getResultList();
    }

    public List<Object> findByField(Class type, String field, Object value) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        DataType dt = entity.getField(field).getDataType();
        return pu.createQueryBuilder(type)
                .byExpression(new And(new Var(field), new Literal(value, dt)))
                .orderBy(entity.getListOrder())
                .getResultList();
    }

}
