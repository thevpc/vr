/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.core;

import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.TraceService;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.QueryBuilder;
import net.vpc.upa.Record;
import net.vpc.upa.RemoveOptions;
import net.vpc.upa.UPA;
import net.vpc.upa.expressions.And;
import net.vpc.upa.expressions.Equals;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.Literal;
import net.vpc.upa.expressions.UserExpression;
import net.vpc.upa.expressions.Var;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class ObjManagerService {

    @Autowired
    CorePlugin core;
    @Autowired
    TraceService trace;

    public Object resolveId(Object t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(t.getClass());
        return entity.getBuilder().entityToId(t);
    }

    public void save(Object t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(t.getClass());
        Object id = entity.getBuilder().entityToId(t);
        List<Field> pf = entity.getPrimaryFields();
        boolean persist = false;
        if (id == null) {
            persist = true;
        } else if (pf.size() <= 1) {
            DataType dt = pf.get(0).getDataType();
            if (dt instanceof EntityType) {
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
            pu.persist(t);
//            trace.inserted(t, getClass(), Level.FINE);
        } else {
            pu.merge(t);
//            trace.updated(t, old, getClass(), Level.FINE);
        }
    }

    public void erase(String type, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        if (trace.accept(entity)) {
            trace.removed(pu.findById(type, id), entity.getParent().getPath(), Level.FINE);
        }
        pu.remove(type, RemoveOptions.forId(id));
    }

    public boolean isSoftRemovable(String type) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        return entity.containsField("deleted");
    }

    public void remove(String type, Object id) {
        if (!isSoftRemovable(type)) {
            erase(type, id);
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        Object t = (Object) pu.findById(type, id);
        if (t != null) {
            //check if already soft deleted
            Boolean b = (Boolean) entity.getBuilder().getProperty(t, "deleted");
            if (b != null && b.booleanValue()) {
                erase(type, id);
            } else {
                UserSession session = VrApp.getContext().getBean(UserSession.class);
                Record r = entity.getBuilder().entityToRecord(t, true);
                if (entity.containsField("deleted")) {
                    r.setBoolean("deleted", true);
                }
                if (entity.containsField("deletedBy")) {
                    r.setString("deletedBy", session.getUser().getLogin());
                }
                if (entity.containsField("deletedOn")) {
                    r.setDate("deletedOn", new Timestamp(System.currentTimeMillis()));
                }
                pu.merge(t);
                if (trace.accept(entity)) {
                    trace.softremoved(pu.findById(type, id), entity.getParent().getPath(), Level.FINE);
                }
            }
        }
    }

    public String getObjectName(Object obj) {
        if (obj == null) {
            return "NO_NAME";
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(obj.getClass());
        Field mf = entity.getMainField();
        if (mf == null) {
            return obj.toString();
        }
        return String.valueOf(entity.getBuilder().entityToRecord(obj, true).getObject(mf.getName()));
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
//                Record r = entity.getBuilder().entityToRecord(t, true);
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
    public void archive(String type, Object object) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        Object id = resolveId(object);
        Object t = pu.findById(type, id);
        if (t != null) {
            Record r = entity.getBuilder().entityToRecord(t, true);
            if (entity.containsField("archived")) {
                r.setBoolean("archived", true);
            }
//            Object old = pu.findById(type, id);
            pu.merge(t);
            if (trace.accept(entity)) {
                trace.archived(pu.findById(type, id), entity.getParent().getPath(), Level.FINE);
            }
//            trace.updated(t, old, getClass(), Level.FINE);
        }

//        invokeEntityAction(type, "Archive", object, null);
    }

    public Object find(Class type, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(type, id);
    }

    public Object find(String entity, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(entity, id);
    }

    public boolean isArchivable(String type) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        return entity.containsField("archived");
    }

    public List<Object> findAll(String type, Map<String, Object> criteria) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        QueryBuilder cc = pu.createQueryBuilder(type)
                .setOrder(entity.getListOrder())
                .setEntityAlias("o");
        if (criteria != null) {
            for (Map.Entry<String, Object> entrySet : criteria.entrySet()) {
                cc.addAndExpression(new Equals(new UserExpression("o." + entrySet.getKey()), new Literal(entrySet.getValue(), null)));
            }
        }
        return cc
                .getEntityList();
    }

    public long findCountByFilter(String entityName, String criteria, ObjSearch objSearch) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String qq = "Select count(1) from "+entityName+" o ";
        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName);
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            qq+=" where "+filterExpression;
        }
        Number nn=(Number)pu.createQuery(qq).getSingleValue();
        return nn.longValue();
    }

    public List<Object> findByFilter(String entityName, String criteria, ObjSearch objSearch) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder q = pu
                .createQueryBuilder(entityName)
                .setEntityAlias("o")
                .setOrder(entity.getListOrder());
        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName);
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            q.setExpression(filterExpression);

        }
        List<Object> list = q.getEntityList();
        if (objSearch != null) {
            list = objSearch.filterList(list, entityName);
        }
        return list;
    }

    public List<Object> findAll(String type) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        return pu.createQueryBuilder(type)
                .setOrder(entity.getListOrder())
                .getEntityList();
    }

    public List<Object> findByField(Class type, String field, Object value) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        DataType dt = entity.getField(field).getDataType();
        return pu.createQueryBuilder(type)
                .setExpression(new And(new Var(field), new Literal(value, dt)))
                .setOrder(entity.getListOrder())
                .getEntityList();
    }

}
