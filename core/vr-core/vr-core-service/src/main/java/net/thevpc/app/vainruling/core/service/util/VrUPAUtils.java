package net.thevpc.app.vainruling.core.service.util;

import com.google.gson.*;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.upa.*;
import net.thevpc.upa.types.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import net.thevpc.common.strings.StringUtils;

/**
 * Created by vpc on 9/5/16.
 */
public class VrUPAUtils {
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(VrUPAUtils.class.getName());

    private static final SimpleDateFormat UNIVERSAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static Object jsonPrimitiveToValue(JsonElement jsonElement, DataType dataType) {
        if (dataType instanceof IntType) {
            JsonPrimitive p = (JsonPrimitive) jsonElement;
            return p.getAsInt();
        }
        if (dataType instanceof LongType) {
            JsonPrimitive p = (JsonPrimitive) jsonElement;
            return p.getAsLong();
        }
        if (dataType instanceof StringType) {
            JsonPrimitive p = (JsonPrimitive) jsonElement;
            return p.getAsString();
        }
        throw new IllegalArgumentException("Not Supported yet");
    }

//    public static Object stringToId__(String id, Entity entity) {
//        if (id == null || id.isEmpty()) {
//            return null;
//        }
//        Gson g = new Gson();
//        JsonElement jsonElement = g.fromJson(id, JsonElement.class);
//
//        List<Field> idFields = entity.getIdFields();
//        List<PrimitiveField> idPrimitiveFields = entity.getIdPrimitiveFields();
//        if (idFields.size() == 1) {
//            Field field = idFields.get(0);
//            DataType dataType = field.getDataType();
//            if (field.isManyToOne()) {
//                List<Object> vals = new ArrayList<>();
//                if (jsonElement instanceof JsonArray) {
//                    for (PrimitiveField idPrimitiveField : idPrimitiveFields) {
//                        vals.add(jsonPrimitiveToValue(jsonElement, idPrimitiveField.getDataType()));
//                    }
//                    return entity.getBuilder().primitiveIdToId(vals.toArray());
//                } else if (idPrimitiveFields.size() == 1) {
//                    return jsonPrimitiveToValue(jsonElement, idPrimitiveFields.get(0).getDataType());
//                } else {
//                    throw new IllegalArgumentException("Unexpected");
//                }
//            } else {
//                return jsonPrimitiveToValue(jsonElement, dataType);
//            }
//        } else {
//            throw new IllegalArgumentException("Not Supported yet");
//        }
//    }
//    protected static Object strToObj(String value,DataType type) {
//        if(value==null || value.length()==0){
//            return null;
//        }
//        if(type instanceof ManyToOneType) {
//            Gson g=new Gson();
//            Object[] objects = g.fromJson(value, Object[].class);
//            return objects;
//        }else if(type instanceof IntType){
//            return Convert.toInt(value);
//        }else if(type instanceof LongType){
//            return Convert.toLong(value);
//        }else if(type instanceof StringType){
//            return value;
//        }else{
//            throw new IllegalArgumentException("Unsupported");
//        }
//    }
//    public static void check(Object o,DataType t) {
//        JsonElement r = objToJson(o, t);
//        String rs = r.toString();
//        Object v=jsonToObj(rs, t);
//        if(!Objects.equals(o,v)){
//            throw new IllegalArgumentException("bad "+t+" : "+o+" "+v+" "+rs);
//        }else{
//            System.out.println("okkay "+t+" : "+o+" "+v+" "+rs);
//        }
//    }
//    public static void main(String[] args) {
//        System.out.println(jsonToObj("\'2\'", TypesFactory.STRING));
//        JsonPrimitive t = new JsonPrimitive("");
//        System.out.println("#"+t+"#");
//        System.out.println("#"+t.getAsString()+"#");
//        System.out.println(t.getAsInt());
//        check("test",TypesFactory.STRING);
//        check(1.2, TypesFactory.DOUBLE);
//        check(ConditionType.EXPRESSION,new EnumType(ConditionType.class,true));
//        check(true,TypesFactory.BOOLEAN);
//    }
    public static Object jsonToObj(String value, DataType type) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            if (type instanceof StringType) {
                return "";
            }
            return null;
        }
        JsonElement jsonElement = JsonUtils.parse(value, JsonElement.class);
        return jsonToObj(jsonElement, type);
    }

    public static Object jsonToObj(JsonElement value, DataType type) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonNull) {
            return null;
        }
        if (value.isJsonPrimitive() && value.getAsString().isEmpty()) {
            return null;
        }
        if (type instanceof ManyToOneType) {
            Relationship r = ((ManyToOneType) type).getRelationship();
            Entity entity = r.getTargetEntity();
            Object o = jsonToObj(value, entity.getDataType());
            return o;
        } else if (type instanceof KeyType) {
            Entity entity = ((KeyType) type).getEntity();

            List<Field> idFields = entity.getIdFields();
            List<PrimitiveField> idPrimitiveFields = entity.getIdPrimitiveFields();
            if (idFields.size() == 1) {
                Field field = idFields.get(0);
                DataType dataType = field.getDataType();
                if (field.isManyToOne()) {
                    List<Object> vals = new ArrayList<>();
                    if (value instanceof JsonArray) {
                        for (PrimitiveField idPrimitiveField : idPrimitiveFields) {
                            vals.add(jsonToObj(value, idPrimitiveField.getDataType()));
                        }
                        return entity.getBuilder().idToObject(entity.getBuilder().primitiveIdToId(vals.toArray()));
                    } else if (idPrimitiveFields.size() == 1) {
                        return entity.getBuilder().idToObject(jsonToObj(value, idPrimitiveFields.get(0).getDataType()));
                    } else {
                        throw new IllegalArgumentException("Unexpected");
                    }
                } else {
                    return entity.getBuilder().idToObject(jsonPrimitiveToValue(value, dataType));
                }
            } else {
                throw new IllegalArgumentException("Multi id entities not Supported yet");
            }
        } else if (type instanceof IntType) {
            if (value instanceof JsonPrimitive) {
                if (value.toString().equals("\"\"")) {
                    return null;
                }
                return value.getAsInt();
            }
        } else if (type instanceof LongType) {
            if (value instanceof JsonPrimitive) {
                if (value.toString().equals("\"\"")) {
                    return null;
                }
                return value.getAsLong();
            }
        } else if (type instanceof DoubleType) {
            if (value instanceof JsonPrimitive) {
                if (value.toString().equals("\"\"")) {
                    return null;
                }
                return value.getAsDouble();
            }
        } else if (type instanceof BooleanType) {
            if (value instanceof JsonPrimitive) {
                if (value.toString().equals("\"\"")) {
                    return null;
                }
                return value.getAsBoolean();
            }
        } else if (type instanceof StringType) {
            if (value instanceof JsonPrimitive) {
                return value.getAsString();
            }
        } else if (type instanceof TemporalType) {
            if (value instanceof JsonPrimitive) {
                String s = value.getAsString();
                if (value.toString().equals("\"\"")) {
                    return null;
                }
                try {
                    return ((TemporalType) type).validateDate(UNIVERSAL_DATE_FORMAT.parse(s));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        } else if (type instanceof EnumType) {
            if (value instanceof JsonPrimitive) {
                String s = value.getAsString();
                if (value.toString().equals("\"\"")) {
                    return null;
                }
                return ((EnumType) type).parse(s);
            }
        } else {
            throw new IllegalArgumentException("Unsupported json serialization of type " + type + " " + (type == null ? "" : ("  : " + type.getClass().getName())));
        }
        throw new IllegalArgumentException("Unsupported json serialization of type " + type + " " + (type == null ? "" : ("  : " + type.getClass().getName())));
    }

    public static JsonElement objToJson(Object value, DataType type) {
        if (value == null) {
            return new JsonPrimitive("");
        }
        if (type instanceof ManyToOneType) {
            Relationship manyToOneRelationship = ((ManyToOneType) type).getRelationship();
            if (manyToOneRelationship.getTargetEntity().isInstance(value)) {
                value = manyToOneRelationship.getTargetEntity().getBuilder().objectToPrimitiveId(value);
            }
            return objToJson(value, ((ManyToOneType) type).getRelationship().getTargetEntity().getDataType());
        } else if (type instanceof KeyType) {
            Entity entity = ((KeyType) type).getEntity();
            if (entity.isInstance(value)) {
                value = entity.getBuilder().objectToId(value);
            }
            PrimitiveId primitiveId = (value instanceof PrimitiveId) ? (PrimitiveId) value : entity.getBuilder().idToPrimitiveId(value);
            if (primitiveId == null) {
                return new JsonPrimitive("");
            }
            if (primitiveId.size() == 1) {
                return objToJson(primitiveId.getValue(0), primitiveId.getField(0).getDataType());
            }
            JsonArray all = new JsonArray();
            for (int i = 0; i < primitiveId.size(); i++) {
                all.add(objToJson(primitiveId.getValue(i), primitiveId.getField(i).getDataType()));
            }
            return all;
        } else if (type instanceof IntType) {
            return new JsonPrimitive((Number) value);
        } else if (type instanceof LongType) {
            return new JsonPrimitive((Number) value);
        } else if (type instanceof DoubleType) {
            return new JsonPrimitive((Number) value);
        } else if (type instanceof BooleanType) {
            return new JsonPrimitive((Boolean) value);
        } else if (type instanceof StringType) {
            return new JsonPrimitive((String) value);
        } else if (type instanceof TemporalType) {
            return new JsonPrimitive(UNIVERSAL_DATE_FORMAT.format(value));
        } else if (type instanceof EnumType) {
            return new JsonPrimitive(value.toString());
        } else {
            throw new IllegalArgumentException("Unsupported json serialization of type " + type + (type == null ? "" : ("  : " + type.getClass().getName())));
        }
    }

    public static Relationship getManyToOnePrimitiveRelationShip(Field field) {
        if (!(field.getDataType() instanceof ManyToOneType)
                && field.getManyToOneRelationships().size() > 0
                && field.getManyToOneRelationships().get(0).getSourceRole().getEntityField() != null) {
            return field.getManyToOneRelationships().get(0);
        }
        return null;
    }

    public static boolean sameId(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof Object[]) {
            if (b instanceof Object[]) {
                return Arrays.equals((Object[]) a, (Object[]) b);
            } else {
                return false;
            }
        }
        if (b instanceof Object[]) {
            return false;
        }
        return a.equals(b);
    }

    public static String getEntityListLabel(Entity entity) {
        String orNull = VrApp.getBean(I18n.class).getOrNull("Entity." + entity.getName() + ".ListTitle", new Arg("name", entity.getName()), new Arg("title", entity.getTitle()));
        if (orNull == null) {
            orNull = entity.getTitle() + "s";
        }
        return orNull;
    }

    public static String resolveLabel(RelationshipRole rols) {
        String orNull = VrApp.getBean(I18n.class).getOrNull(rols);
        if (orNull == null) {
            Entity entity = rols.getEntity();
            orNull = VrApp.getBean(I18n.class).getOrNull("Entity." + entity.getName() + ".ListTitle", new Arg("name", entity.getName()), new Arg("title", entity.getTitle()));
            if (orNull == null) {
                orNull = VrApp.getBean(I18n.class).getOrNull(entity);
            }
        }
        if (orNull == null) {
            orNull = rols.getTitle();
        }
        return orNull;
    }

    public static Object findById2(Entity ee,Object value) {
        if(value==null){
            return null;
        }
        PersistenceUnit pu = ee.getPersistenceUnit();
        if (!ee.getIdType().isAssignableFrom(value.getClass()) && pu.containsEntity(ee.getIdType())) {
            Entity ee2 = ee.getIdFields().get(0).getManyToOneRelationship().getTargetEntity();
            if (ee2 == null) {
                return ee.findById(value);
            }
            Object i = ee2.findById(value);
            return ee.findById(i);
        }else{
            return ee.findById(value);
        }

    }
    
    
    public static <T> T resolveCachedEntityPropertyInstance(Entity entity,String key,Class<T> clazz) {
        String objCacheKey = "cache."+key;
        String nullCacheKey = "cache."+key+"null";
        PersistenceUnit pu = UPA.getPersistenceUnit();
        T oldProvider = entity.getProperties().getObject(objCacheKey);
        if (oldProvider != null) {
            return oldProvider;
        }
        boolean noProvider = entity.getProperties().getBoolean(nullCacheKey, false);
        if (noProvider) {
            return null;
        }
        
        T value = null;
        value = null;
        String p = StringUtils.trimToNull(entity.getProperties().getString(key));
        if (!StringUtils.isBlank(p)) {
            try {
                Class<?> pp = Class.forName(p);
                value = (T) VrApp.getBean(pp);//pp.newInstance();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, " Unable to create of "+key+"("+clazz.getSimpleName()+") for entity " + entity.getName() + " as type " + p, e);
            }
        }
        if (value == null) {
            entity.getProperties().setBoolean(nullCacheKey, true);
        } else {
            entity.getProperties().setObject(objCacheKey, value);
        }
        return value;
            //
    }
}
