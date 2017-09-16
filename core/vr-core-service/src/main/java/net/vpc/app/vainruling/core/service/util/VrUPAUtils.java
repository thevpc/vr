package net.vpc.app.vainruling.core.service.util;

import com.google.gson.*;
import net.vpc.common.util.Convert;
import net.vpc.upa.*;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.IdEnumerationExpression;
import net.vpc.upa.expressions.Var;
import net.vpc.upa.types.*;
import org.aspectj.weaver.TypeFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            expr = new IdEnumerationExpression(k.getIds(), new Var("this"));
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
        Gson g = new Gson();
        JsonElement jsonElement = g.fromJson(value, JsonElement.class);
        return jsonToObj(jsonElement, type);
    }

    public static Object jsonToObj(JsonElement value, DataType type) {
        if (value instanceof JsonNull) {
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
                if(value.toString().equals("\"\"")){
                    return null;
                }
                return value.getAsInt();
            }
        } else if (type instanceof LongType) {
            if (value instanceof JsonPrimitive) {
                if(value.toString().equals("\"\"")){
                    return null;
                }
                return value.getAsLong();
            }
        } else if (type instanceof DoubleType) {
            if (value instanceof JsonPrimitive) {
                if(value.toString().equals("\"\"")){
                    return null;
                }
                return value.getAsDouble();
            }
        } else if (type instanceof BooleanType) {
            if (value instanceof JsonPrimitive) {
                if(value.toString().equals("\"\"")){
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
                if(value.toString().equals("\"\"")){
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
                if(value.toString().equals("\"\"")){
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
            if(manyToOneRelationship.getTargetEntity().isInstance(value)){
                value=manyToOneRelationship.getTargetEntity().getBuilder().objectToPrimitiveId(value);
            }
            return objToJson(value,((ManyToOneType) type).getRelationship().getTargetEntity().getDataType());
        }else if (type instanceof KeyType) {
            Entity entity=((KeyType) type).getEntity();
            if(entity.isInstance(value)){
                value=entity.getBuilder().objectToId(value);
            }
            PrimitiveId primitiveId = (value instanceof PrimitiveId) ? (PrimitiveId) value:entity.getBuilder().idToPrimitiveId(value);
            if (primitiveId == null) {
                return new JsonPrimitive("");
            }
            if(primitiveId.size()==1){
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
        if (!(field.getDataType() instanceof ManyToOneType) &&
                field.getManyToOneRelationships().size() > 0 &&
                field.getManyToOneRelationships().get(0).getSourceRole().getEntityField() != null) {
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

}
