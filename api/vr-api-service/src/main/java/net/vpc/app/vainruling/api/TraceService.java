/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api;

import net.vpc.app.vainruling.api.security.UserSession;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import net.vpc.app.vainruling.api.model.AppTrace;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.upa.Action;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.InvokeContext;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.Record;
import net.vpc.upa.UPA;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class TraceService {

    private UserSession getUserSession() {
        try {
            return VrApp.getContext().getBean(UserSession.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void trace(String action, String message, String data, String module, Level level) {
        trace(action, message, data, module, null, null, level);
    }

    public void inserted(Object o, String module, Level level) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        trace("added", e.getName() + " added", dump(o), module, e.getName(), e.getBuilder().entityToId(o), level);
    }

    public void updated(Object o, Object old, String module, Level level) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        trace("updated", e.getName() + " updated", dumpDiff(old, o, e, e.getFields()), module, e.getName(), e.getBuilder().entityToId(o), level);
    }

    public void removed(Object o, String module, Level level) {
        if (o != null) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            Entity e = pu.getEntity(o.getClass());
            trace("removed", e.getName() + " removed", dump(o), module, e.getName(), e.getBuilder().entityToId(o), level);
        }
    }

    public void softremoved(Object o, String module, Level level) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        trace("soft-removed", e.getName(), dump(o), module, e.getName(), e.getBuilder().entityToId(o), level);
    }

    public void archived(Object o, String module, Level level) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        trace("archived", e.getName(), dump(o), module, e.getName(), e.getBuilder().entityToId(o), level);
    }

    public void trace(String action, String message, String data, String module, String objectName, Object objectId, Level level) {
        UserSession us = getUserSession();
        AppUser u = us == null ? null : us.getUser();
        String login = u == null ? "anonymous" : u.getLogin();
        int userId = u == null ? -1 : u.getId();
        String ip = us == null ? "" : us.getClientIpAddress();
        trace(action, message, data, module, objectName, objectId, login, userId, level,ip);
    }

    public void trace(final String action, final String message, final String data, final String module, final String objectName, Object objectId, final String user, final int userId, final Level level, final String ip) {
        final PersistenceUnit pu = UPA.getPersistenceUnit();
        if (objectId instanceof Object[]) {
            objectId = Arrays.deepToString((Object[]) objectId);
        } else {
            objectId = String.valueOf(objectId);
        }
        final Object objectId2 = objectId;
        UPA.getContext().invokePrivileged(new Action<Object>() {

            @Override
            public Object run() {
                Entity entity = pu.getEntity(AppTrace.class);
                String message2 = VrHelper.strcut(message, entity, "message");
                String data2 = VrHelper.strcut(data, entity, "data");
                pu.persist(new AppTrace(message2, data2, module,
                        new Timestamp(System.currentTimeMillis()),
                        user,
                        userId,
                        level.intValue(),
                        level.getName(),
                        action,
                        objectName,
                        objectId2.toString(),
                        ip));
                return null;
            }
        }, new InvokeContext());
    }

    public String dump(Object o) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        return dump(o, e, e.getFields());
    }

    public String name(Object o) {
        if (o == null) {
            return "null";
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        Field main = e.getMainField();
        List<Field> names = null;
        if (main == null) {
            names = e.getPrimaryFields();
        } else {
            names = Arrays.asList(main);
        }
        return dump(o, e, names);
    }

    private String dump(Object o, Entity e, List<Field> fields) {
        StringBuilder b = new StringBuilder(e.getName()).append("{");
        Record rec = e.getBuilder().entityToRecord(o, true);
        boolean first = true;
        for (Field field : fields) {
            if (first) {
                first = false;
            } else {
                b.append(",");
            }
            b.append(field.getName());
            b.append(":");
            Object val = rec.getObject(field.getName());
            if (field.getRelationships().size() > 0) {
                val = name(val);
            } else {
                val = toStringValue(val);
            }
            b.append(val);
        }
        b.append("}");
        return b.toString();
    }

    private String toStringValue(Object val) {
        if (val == null) {
            return "null";
        } else if (val instanceof String) {
            StringBuilder b = new StringBuilder();
            for (char c : ((String) val).toCharArray()) {
                switch (c) {
                    case '\'': {
                        b.append("'");
                        break;
                    }
                    case '"': {
                        b.append("\\");
                        break;
                    }
                    case '\n': {
                        b.append("\\n");
                        break;
                    }
                    case '\t': {
                        b.append("\\t");
                        break;
                    }
                    case '\r': {
                        b.append("\\r");
                        break;
                    }
                    case '\f': {
                        b.append("\\f");
                        break;
                    }
                    default: {
                        b.append(c);
                    }
                }
            }
            return toQuotedString((String) val);
        } else {
            return String.valueOf(val);
        }
    }

    private String toQuotedString(String val) {
        return "'" + (val) + "'";
    }

    private String dumpDiff(Object o1, Object o2, Entity e, List<Field> fields) {
        StringBuilder b = new StringBuilder(e.getName()).append("{");
        Record rec1 = e.getBuilder().entityToRecord(o1, true);
        Record rec2 = e.getBuilder().entityToRecord(o2, true);
        boolean first = true;
        for (Field field : fields) {
            Object val1 = rec1.getObject(field.getName());
            if (field.getRelationships().size() > 0) {
                val1 = name(val1);
            } else {
                val1 = toStringValue(val1);
            }
            Object val2 = rec2.getObject(field.getName());
            if (field.getRelationships().size() > 0) {
                val2 = name(val2);
            } else {
                val2 = toStringValue(val2);
            }
            if (field.isId()) {
                if (first) {
                    first = false;
                } else {
                    b.append(",");
                }
                b.append(field.getName());
                b.append(":");
                b.append(val1);//.append("->").append(val2);
            } else if (!Objects.equals(val1, val2)) {
                if (first) {
                    first = false;
                } else {
                    b.append(",");
                }
                b.append(field.getName());
                b.append(":");
                b.append(val1).append("->").append(val2);
            }
        }
        b.append("}");
        return b.toString();
    }

//    private String dump(String name, Map<String, Object> fields) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        StringBuilder b = new StringBuilder(name).append("{");
//        for (Map.Entry<String, Object> field : fields.entrySet()) {
//            b.append(field.getKey());
//            b.append(":");
//            b.append(field.getValue());
//        }
//        b.append("}");
//        return b.toString();
//    }
}
