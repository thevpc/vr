package net.vpc.app.vainruling.core.service.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.vpc.upa.*;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.IdEnumerationExpression;
import net.vpc.upa.expressions.Var;
import net.vpc.upa.types.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public class VrUPAUtils {
    private static SimpleDateFormat UNIVERSAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static void storeUpdatedIds(UpdateEvent event) {
        Expression expr = event.getFilterExpression();
        Entity entity = event.getEntity();
        if (expr instanceof IdEnumerationExpression) {
            IdEnumerationExpression k = (IdEnumerationExpression) expr;
            expr = new IdEnumerationExpression(k.getIds(), new Var(entity.getName()));
        }
        PersistenceUnit pu = event.getPersistenceUnit();
        List old = pu.createQueryBuilder(entity.getName()).byExpression(expr).getIdList();
        old.size();//force load!
        event.getContext().setObject("updated_ids_" + entity.getName(), old);
    }

    public static <T> List<T> loadUpdatedIds(UpdateEvent event) {
        List object = (List) event.getContext().getObject("updated_ids_" + event.getEntity().getName());
        if (object == null) {
            throw new IllegalArgumentException("Not Loaded");
        }
        return object;
    }

    public static String idToString(Object id, Entity entity) {
        if (id == null) {
            return "";
        }
        List<PrimitiveField> idPrimitiveFields = entity.getIdPrimitiveFields();
        if (idPrimitiveFields.size() == 1) {
            return objToJson(id, idPrimitiveFields.get(0).getDataType()).toString();
        }
        JsonArray all = new JsonArray();
        for (PrimitiveField idPrimitiveField : idPrimitiveFields) {
            all.add(objToJson(id, idPrimitiveField.getDataType()));
        }
        return all.toString();
    }

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

    public static Object stringToId(String id, Entity entity) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        Gson g = new Gson();
        JsonElement jsonElement = g.fromJson(id, JsonElement.class);

        List<Field> idFields = entity.getIdFields();
        List<PrimitiveField> idPrimitiveFields = entity.getIdPrimitiveFields();
        if (idFields.size() == 1) {
            Field field = idFields.get(0);
            DataType dataType = field.getDataType();
            if (field.isManyToOne()) {
                List<Object> vals=new ArrayList<>();
                if(jsonElement instanceof JsonArray) {
                    for (PrimitiveField idPrimitiveField : idPrimitiveFields) {
                        vals.add(jsonPrimitiveToValue(jsonElement, idPrimitiveField.getDataType()));
                    }
                    return entity.getBuilder().primitiveIdToId(vals.toArray());
                }else if (idPrimitiveFields.size()==1){
                    return jsonPrimitiveToValue(jsonElement, idPrimitiveFields.get(0).getDataType());
                }else{
                    throw new IllegalArgumentException("Unexpected");
                }
            } else {
                return jsonPrimitiveToValue(jsonElement, dataType);
            }
        } else {
            throw new IllegalArgumentException("Not Supported yet");
        }
    }


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

    public static JsonElement objToJson(Object value, DataType type) {
        if (value == null) {
            return new JsonPrimitive("");
        }
        if (type instanceof ManyToOneType) {
            Relationship r = ((ManyToOneType) type).getRelationship();
            PrimitiveId primitiveId = r.getTargetEntity().getBuilder().idToPrimitiveId(value);
            if (primitiveId == null) {
                return new JsonPrimitive("");
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
        } else {
            throw new IllegalArgumentException("Unsupported");
        }
    }
    public static Relationship getManyToOnePrimitiveRelationShip(Field field){
        if(!(field.getDataType() instanceof ManyToOneType) &&
                field.getManyToOneRelationships().size()>0 &&
                field.getManyToOneRelationships().get(0).getSourceRole().getEntityField()!=null) {
            return field.getManyToOneRelationships().get(0);
        }
        return null;
    }

    public static boolean sameId(Object a,Object b) {
        if(a==b){
            return true;
        }
        if(a==null || b==null){
            return false;
        }
        if(a instanceof Object[]){
            if(b instanceof Object[]){
                return Arrays.equals((Object[]) a,(Object[]) b);
            }else{
                return false;
            }
        }
        if(b instanceof Object[]){
            return false;
        }
        return a.equals(b);
    }

}
