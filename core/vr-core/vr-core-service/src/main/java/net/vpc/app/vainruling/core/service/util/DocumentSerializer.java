/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;
import net.vpc.upa.Document;
import net.vpc.upa.ObjectFactory;
import net.vpc.upa.UPA;

/**
 *
 * @author vpc
 */
public class DocumentSerializer implements JsonSerializer<Document>, JsonDeserializer<Document> {

    private ObjectFactory of;

    @Override
    public JsonElement serialize(Document src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.toMap(), Map.class);
    }

    @Override
    public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Object m = context.deserialize(json, Map.class);
        if (of == null) {
            of = UPA.getBootstrapFactory();
        }
        Document d = of.createObject(Document.class);
        d.setAll((Map<String, Object>) m);
        return d;
    }

}
