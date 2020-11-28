package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.Document;
import net.thevpc.upa.Entity;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vpc on 10/30/16.
 */
public class DefaultObjectToMapConverter implements ObjectToMapConverter{
    public static final ObjectToMapConverter INSTANCE=new DefaultObjectToMapConverter();
    @Override
    public Map<String, Object> convert(Object o) {
        if(o==null){
            return null;
        }
        if(o instanceof Map){
            return (Map) o;
        }
        if(o instanceof Document){
            return ((Document) o).toMap();
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.findEntity(o.getClass());
        Map<String, Object> words = new HashMap<>();
        if(entity!=null){
            Document r = (o instanceof Document) ? ((Document) o) : entity.getBuilder().objectToDocument(o, true);
            if (r != null) {
                for (Map.Entry<String, Object> entry : r.entrySet()) {
                    String k = entry.getKey();
                    Object v = entry.getValue();
                    if (v != null) {
                        Entity ve = pu.findEntity(v.getClass());
                        if (ve != null) {
                            Object mv = ve.getBuilder().getMainValue(v);
                            String v2 = String.valueOf(mv);
                            if (!StringUtils.isBlank(v2)) {
                                words.put(k, (v2));
                            }
                        } else if (v instanceof String) {
                            if (!StringUtils.isBlank(v.toString())) {
                                words.put(k, (v));
                            }
                        } else {
                            words.put(k, (v));
                        }
                    }
                }
            }
            return words;
        }else{
            for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(o.getClass())) {
                propertyDescriptor.getReadMethod().setAccessible(true);
                try {
                    words.put(propertyDescriptor.getName(),propertyDescriptor.getReadMethod().invoke(o));
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return words;
    }
}
