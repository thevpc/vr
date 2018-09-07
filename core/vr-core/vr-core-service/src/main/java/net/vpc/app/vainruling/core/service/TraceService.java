/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.model.AppTrace;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.*;
import net.vpc.upa.bulk.DataWriter;
import net.vpc.upa.bulk.ImportExportManager;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.ManyToOneType;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.common.util.MapUtils;

/**
 * @author taha.bensalah@gmail.com
 */
@Service
public class TraceService {

    public static final Logger log = Logger.getLogger(TraceService.class.getName());
    private static final ThreadLocal<Integer> SILENCE = new ThreadLocal<>();

    private static void enterSilence() {
        Integer i = SILENCE.get();
        if (i == null) {
            SILENCE.set(1);
        } else {
            SILENCE.set(i + 1);
        }
    }

    private static void exitSilence() {
        Integer i = SILENCE.get();
        if (i == null) {
            //SILENCE.set(1);
        } else {
            SILENCE.set(i - 1);
        }
    }

    public static boolean isSilenced() {
        Integer i = SILENCE.get();
        return (i != null && i.intValue() > 0);
    }

    public static Runnable makeSilenced(final Runnable r) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    enterSilence();
                    r.run();
                } finally {
                    exitSilence();
                }
            }
        };
    }

    public static <T> Action<T> makeSilenced(final Action<T> r) {
        return new Action<T>() {

            @Override
            public T run() {
                T t = null;
                try {
                    enterSilence();
                    t = r.run();
                } finally {
                    exitSilence();
                }
                return t;
            }
        };
    }

    public static VoidAction makeSilenced(final VoidAction r) {
        return new VoidAction() {

            @Override
            public void run() {
                try {
                    enterSilence();
                    r.run();
                } finally {
                    exitSilence();
                }
            }
        };
    }

    public static void runSilenced(final VoidAction r) {
        runSilenced(r);
    }

    public static void runSilenced(final Runnable r) {
        runSilenced(r);
    }

    public static <T> T runSilenced(final Action<T> r) {
        return makeSilenced(r).run();
    }

    public static TraceService get() {
        return VrApp.getBean(TraceService.class);
    }

//    private UserSession getCurrentSession() {
//        try {
//            return VrApp.getContext().getBean(UserSession.class);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//    private AppUser getCurrentUser() {
//        try {
//            return VrApp.getContext().getBean(CorePlugin.class).getCurrentUser();
//        } catch (Exception e) {
//            return null;
//        }
//    }
    public boolean accept(Entity entity) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && !entity.getEntityType().equals(AppTrace.class);
    }

    public void trace(String action, String extraKey, Map<String, Object> messageParams, String data, String module, Level level) {
        String msgKey = (extraKey == null) ? (action + ".trace.message") : (action + "." + extraKey + ".trace.message");
        trace(action, I18n.get().get(msgKey, messageParams), data, module, level);
    }
    public void trace(String action, String extraKey, Map<String, Object> messageParams, String data, String module, String objectName, Object objectId, Level level) {
        String msgKey = (extraKey == null) ? (action + ".trace.message") : (action + "." + extraKey + ".trace.message");
        trace(action, I18n.get().get("trace." + msgKey + ".message", messageParams), data, module, objectName, objectId, level);
    }

    @Deprecated
    private void trace(String action, String message, String data, String module, Level level) {
        if (isSilenced()) {
            return;
        }
        trace(action, message, data, module, null, null, level);
    }

    public void inserted(String entityName, Object o, String module, Level level) {
        if (isSilenced()) {
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(entityName);
        Object id = e.getBuilder().objectToId(o);
        String name = e.getBuilder().objectToName(o);
        trace("System.entities.entity-added", "success", MapUtils.map("name", e.getName(), "title", e.getTitle(), "value", name, "id", id), dump(o), module, e.getName(), e.getBuilder().objectToId(o), level);
    }

    public void updated(String entityName, Object o, Object old, String module, Level level) {
        if (isSilenced()) {
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(entityName);
        Object id = e.getBuilder().objectToId(o);
        String name = e.getBuilder().objectToName(o);
        trace("System.entities.entity-updated", "success",
                MapUtils.map("name", e.getName(), "title", e.getTitle(), "value", name, "id", id, "fields", buildUpdatedFieldsString(old, o, e)),
                 dumpDiff(old, o, e), module, e.getName(), e.getBuilder().objectToId(o), level);
    }

    public void removed(String entityName, Object o, String module, Level level) {
        if (isSilenced()) {
            return;
        }
        if (o != null) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            Entity e = pu.getEntity(entityName);
            Object id = e.getBuilder().objectToId(o);
            String name = e.getBuilder().objectToName(o);
            trace("System.entities.entity-removed", "success",MapUtils.map("name", e.getName(), "title", e.getTitle(), "value", name, "id", id), dump(o), module, e.getName(), e.getBuilder().objectToId(o), level);
        }
    }

    public void softremoved(String entityName, Object o, String module, Level level) {
        if (isSilenced()) {
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(entityName);
        Object id = e.getBuilder().objectToId(o);
        String name = e.getBuilder().objectToName(o);
        trace("System.entities.entity-soft-removed", "success",MapUtils.map("name", e.getName(), "title", e.getTitle(), "value", name, "id", id), dump(o), module, e.getName(), e.getBuilder().objectToId(o), level);
    }

    public void archived(String entityName, Object o, String module, Level level) {
        if (isSilenced()) {
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(entityName);
        Object id = e.getBuilder().objectToId(o);
        String name = e.getBuilder().objectToName(o);
        trace("System.entities.entity-archived", "success",MapUtils.map("name", e.getName(), "title", e.getTitle(), "value", name, "id", id), dump(o), module, e.getName(), e.getBuilder().objectToId(o), level);
    }


    @Deprecated
    public void trace(String action, String message, String data, String module, String objectName, Object objectId, Level level) {
        if (isSilenced()) {
            return;
        }
        UserToken us = CorePlugin.get().getCurrentToken();
        String login = us == null ? "anonymous" : us.getUserLogin();
        int userId = us == null ? -1 : us.getUserId() == null ? -1 : us.getUserId();
        String ip = us == null ? "" : us.getIpAddress();
        trace(action, message, data, module, objectName, objectId, login, userId, level, ip);
    }

    public void trace(final String action, final String message, final String data, final String module, final String objectName, Object objectId, final String user, final int userId, final Level level, final String ip) {
        if (isSilenced()) {
            return;
        }
        final PersistenceUnit pu = UPA.getPersistenceUnit();
        if (objectId instanceof Object[]) {
            objectId = Arrays.deepToString((Object[]) objectId);
        } else {
            objectId = String.valueOf(objectId);
        }
        final Object objectId2 = objectId;
        UPA.getContext().invokePrivileged(makeSilenced(new VoidAction() {

            @Override
            public void run() {
                Entity entity = pu.getEntity(AppTrace.class);
                log.log(level, message);
                String message2 = VrUtils.strcut(message, entity, "message");
                String data2 = VrUtils.strcut(data, entity, "data");
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
            }
        }));
    }

    public String dump(Object o) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        return dump(o, e, e.getFields());
    }

    public String name(Object o, Entity e) {
        if (o == null) {
            return "null";
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Field main = e.getMainField();
        List<Field> names = null;
        if (main == null) {
            names = e.getIdFields();
        } else {
            names = Arrays.asList(main);
        }
        return dump(o, e, names);
    }

    private String dump(Object o, Entity e, List<Field> fields) {
        StringBuilder b = new StringBuilder(e.getName()).append("{");
        Document rec = e.getBuilder().objectToDocument(o, true);
        boolean first = true;
        for (Field field : fields) {
            if (first) {
                first = false;
            } else {
                b.append(",");
            }
            String fieldName = field.getName();
            b.append(fieldName);
            b.append(":");
            String val = getFieldStringValue(rec.getObject(fieldName), field);
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
            return toQuotedString(b.toString());
        } else {
            return String.valueOf(val);
        }
    }

    private String toQuotedString(String val) {
        return "'" + (val) + "'";
    }

    private String getFieldStringValue(Object val, Field field) {
        if (val instanceof Expression) {
            return "<expr>";
        }
        DataType dt = field.getDataType();
        if (dt instanceof ManyToOneType) {
            ManyToOneType et = (ManyToOneType) dt;
            Entity tentity = et.getRelationship().getTargetRole().getEntity();
            return name(val, tentity);
        } else {
            return toStringValue(val);
        }
    }

    private String buildUpdatedFieldsString(Object o1, Object o2, Entity e) {
        Set<String> fields = new HashSet<>();
        Document rec1 = e.getBuilder().objectToDocument(o1, true);
        Document rec2 = e.getBuilder().objectToDocument(o2, true);
        Set<String> otherTracedFields = new TreeSet<>(rec2.keySet());
        Set<String> mainTracedFields = new TreeSet<>();
        //always add
        for (Field field : e.getIdFields()) {
            mainTracedFields.add(field.getName());
        }
        otherTracedFields.removeAll(mainTracedFields);
        for (String fieldName : otherTracedFields) {
            Field field = e.findField(fieldName);
            if (field != null) {
                String val1 = getFieldStringValue(rec1.getObject(fieldName), field);
                String val2 = getFieldStringValue(rec2.getObject(fieldName), field);
                if (!Objects.equals(val1, val2)) {
                    fields.add(field.getTitle());
                }
            }
        }
        return StringUtils.join(",", fields);
    }

    private String dumpDiff(Object o1, Object o2, Entity e) {
        StringBuilder b = new StringBuilder(e.getName()).append("{");
        Document rec1 = e.getBuilder().objectToDocument(o1, true);
        Document rec2 = e.getBuilder().objectToDocument(o2, true);
        boolean first = true;

        Set<String> otherTracedFields = new TreeSet<>(rec2.keySet());
        Set<String> mainTracedFields = new TreeSet<>();
        //always add
        for (Field field : e.getIdFields()) {
            mainTracedFields.add(field.getName());
        }
        Field mainField = e.getMainField();
        if (mainField != null) {
            mainTracedFields.add(mainField.getName());
        }
        otherTracedFields.removeAll(mainTracedFields);

        for (String fieldName : mainTracedFields) {
            Field field = e.findField(fieldName);
            if (field != null) {
                String val1 = getFieldStringValue(rec1.getObject(fieldName), field);
                String val2 = getFieldStringValue(rec2.getObject(fieldName), field);
                if (!rec2.isSet(fieldName)) {
                    val2 = val1;
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
                } else {
                    if (first) {
                        first = false;
                    } else {
                        b.append(",");
                    }
                    b.append(field.getName());
                    b.append(":");
                    b.append(val1);
                }
            }
        }
        for (String fieldName : otherTracedFields) {
            Field field = e.findField(fieldName);
            if (field != null) {
                String val1 = getFieldStringValue(rec1.getObject(fieldName), field);
                String val2 = getFieldStringValue(rec2.getObject(fieldName), field);
                if (!Objects.equals(val1, val2)) {
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
        }
        b.append("}");
        return b.toString();
    }

    public void archiveLogs(int fromLastDays) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        instance.add(Calendar.DAY_OF_YEAR, -fromLastDays + 1);
        Map<String, Object> params = new HashedMap();
        params.put("dte", instance.getTime());
        archiveLogs("x.time<:dte and (x.action='added' or x.action='updated' or x.action='removed' or x.action='soft-removed')", params, "vr-app-crud-*.log", -1);
        archiveLogs("x.time<:dte and (x.action='visit-page' or x.action='visit.page')", params, "vr-app-visit-page-*.log", -1);
        archiveLogs("x.time<:dte and x.action='login'", params, "vr-app-login-*.log", -1);
        archiveLogs("x.time<:dte ", params, "vr-app-other-*.log", -1);
    }

    public void archiveLogs(String filter, Map<String, Object> params, String fileNamePattern, int rowsPerFile0) {
        log.info("Start archiveLogs with filter : " + filter + " -- " + params);
        System.out.println("Start archiveLogs with filter : " + filter + " -- " + params);
        UPA.getContext().invokePrivileged(makeSilenced(new VoidAction() {
            @Override
            public void run() {
                int rowsPerFile = rowsPerFile0 <= 9 ? 100000 : rowsPerFile0;
                PersistenceUnit pu = UPA.getPersistenceUnit();
                String filter0 = filter;
                if (StringUtils.isEmpty(filter0)) {
                    filter0 = "";
                } else {
                    filter0 = " AND " + filter0;
                }
                List<AppTrace> all = pu.createQuery("Select x from AppTrace x where 1=1 " + filter0)
                        .setParameters(params)
                        .setLazyListLoadingEnabled(true)
                        .getResultList();
                int currRows = -1;
                VFile logsFolder = VrApp.getBean(CorePlugin.class).getRootFileSystem().get("Logs");
                logsFolder.mkdirs();
////                String rootFolder = VrApp.getBean(CorePlugin.class).getNativeFileSystemPath();
////                String rootLog = (StringUtils.isEmpty(rootFolder) ? System.getProperty("user.home") : rootFolder) + VrPlatformUtils.SLASH+ "Logs";
//                new File(VrPlatformUtils.validatePath(rootLog)).mkdirs();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.S");
                DataWriter f = null;
                ImportExportManager importExportManager = pu.getImportExportManager();
                try {
                    if (StringUtils.isEmpty(fileNamePattern)) {
                        for (AppTrace t : all) {
                            pu.remove(t);
                        }
                    } else {
                        for (AppTrace t : all) {
                            if (currRows < 0) {
                                currRows = rowsPerFile;
                                if (f != null) {
                                    f.close();
                                }
                                String name = fileNamePattern.replace("*", sdf.format(new Date()));
                                f = importExportManager.createTextCSVFormatter(logsFolder.get(name).getOutputStream())
                                        .setSupportsDoubleQuote(true)
                                        .setDataRowConverter(importExportManager.createEntityConverter(AppTrace.class.getSimpleName(), null))
                                        .createWriter();
                            } else {
                                currRows--;
                            }
                            f.writeObject(t);
                            pu.remove(t);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (f != null) {
                    f.close();
                }
            }
        }));
        log.info("End archiveLogs with filter : " + filter + " -- " + params);
        System.out.println("End archiveLogs with filter : " + filter + " -- " + params);
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
