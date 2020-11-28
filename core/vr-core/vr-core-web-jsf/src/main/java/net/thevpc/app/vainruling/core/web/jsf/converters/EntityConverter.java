package net.thevpc.app.vainruling.core.web.jsf.converters;

import net.thevpc.app.vainruling.core.service.util.VrUPAUtils;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.upa.Entity;
import net.thevpc.upa.UPA;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class EntityConverter implements Converter {
    private final String entityName;

    public EntityConverter(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Entity e = UPA.getPersistenceUnit().getEntity(entityName);
        if(value==null){
            return null;
        }
        if(value.equals(Vr.NULL_VALUE_STR)){
            return Vr.NullSelected;
        }
        Object o = VrUPAUtils.jsonToObj(value, e.getDataType());
        if(o==null){
            return null;
        }
        Object oo = e.findById(e.getBuilder().objectToId(o));
        return oo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value instanceof String){
            return (String) value;
        }
        if(value==null){
            return "";
        }
        Entity e = UPA.getPersistenceUnit().getEntity(entityName);
        return VrUPAUtils.objToJson(value,e.getDataType()).toString();
    }
}
