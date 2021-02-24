/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.editor.ViewContext;
import net.thevpc.common.collections.ClassMap;
import net.thevpc.upa.Entity;
import net.thevpc.upa.Field;
import net.thevpc.upa.UPA;
import net.thevpc.upa.types.DataType;
import net.thevpc.upa.types.DataTypeFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
@Service
public class PropertyViewManager {

    private final Map<String, PropertyViewValuesProvider> propertyViewValuesProviderByField = new HashMap<>();
    private final ClassMap<PropertyViewValuesProvider> propertyViewValuesProviderByDataType = new ClassMap<>(Object.class, PropertyViewValuesProvider.class);
    private final ClassMap<PropertyViewValuesProvider> propertyViewValuesProviderByPlatformType = new ClassMap<>(Object.class, PropertyViewValuesProvider.class);
    private final Map<String, PropertyViewFactory> propertyViewFactoryByField = new HashMap<>();
    private final ClassMap<PropertyViewFactory> propertyViewFactoryByDataType = new ClassMap<>(Object.class, PropertyViewFactory.class);
    private final ClassMap<PropertyViewFactory> propertyViewFactoryByPlatformType = new ClassMap<>(Object.class, PropertyViewFactory.class);
    private PropertyViewFactory defaultPropertyViewFactory = new DefaultPropertyViewFactory();
    private PropertyViewValuesProvider defaultPropertyViewValuesProvider = new DefaultPropertyViewValuesProvider();

    public PropertyViewValuesProvider getPropertyViewValuesProvider(Field field) {
        return getPropertyViewValuesProvider(field, null);
    }

    public PropertyViewValuesProvider getPropertyViewValuesProvider(DataType dt) {
        return getPropertyViewValuesProvider(null, dt);
    }

    public PropertyViewValuesProvider getPropertyViewValuesProvider(Field field, DataType dt) {
        PropertyViewValuesProvider v = null;
        if (field != null) {
            v = propertyViewValuesProviderByField.get(field.getAbsoluteName());
            if (v != null) {
                return v;
            }
        }
        DataType dtok = dt != null ? dt : field != null ? field.getDataType() : null;
        if (dtok != null) {
            v = propertyViewValuesProviderByDataType.get(dtok.getClass());
            if (v != null) {
                return v;
            }
            v = propertyViewValuesProviderByPlatformType.get(dtok.getPlatformType());
            if (v != null) {
                return v;
            }
        }
        return defaultPropertyViewValuesProvider;
    }

    public PropertyViewFactory getPropertyViewFactory(Field field) {
        return getPropertyViewFactory(field, null);
    }

    public PropertyViewFactory getPropertyViewFactory(DataType dt) {
        return getPropertyViewFactory(null, dt);
    }

    private PropertyViewFactory getPropertyViewFactory(Field field, DataType dt) {
        PropertyViewFactory v = null;
        if (field != null) {
            v = propertyViewFactoryByField.get(field.getAbsoluteName());
            if (v != null) {
                return v;
            }
        }
        DataType dtok = dt != null ? dt : field != null ? field.getDataType() : null;
        if (dtok != null) {
            v = propertyViewFactoryByDataType.get(dtok.getClass());
            if (v != null) {
                return v;
            }
            v = propertyViewFactoryByPlatformType.get(dtok.getPlatformType());
            if (v != null) {
                return v;
            }
        }
        return defaultPropertyViewFactory;
    }

    public PropertyView createPropertyView(String componentId, Field field, Map<String, Object> configuration, ViewContext viewContext) {
        PropertyView[] propertyViews = createPropertyViews(componentId, field, configuration, viewContext);
        if(propertyViews.length==1){
            return propertyViews[0];
        }
        if(propertyViews.length==0){
            throw new RuntimeException("No Property view could be created");
        }
        throw new RuntimeException("Too many Property views");
    }
    public PropertyView[] createPropertyViews(String componentId, Field field, Map<String, Object> configuration, ViewContext viewContext) {
        return getPropertyViewFactory(field).createPropertyView(componentId, field, configuration, this, viewContext);
    }

    public PropertyView createPropertyView(Class dt) {
        return createPropertyView(dt.getSimpleName(),dt,null,null);
    }

    public PropertyView createPropertyView(String componentId, Class dt, Map<String, Object> configuration, ViewContext viewContext) {
        PropertyView[] propertyViews = createPropertyViews(componentId, dt, configuration, viewContext);
        if(propertyViews.length==1){
            return propertyViews[0];
        }
        if(propertyViews.length==0){
            throw new RuntimeException("No Property view could be created");
        }
        throw new RuntimeException("Too many Property views");
    }

    public PropertyView[] createPropertyViews(String componentId, Class dt, Map<String, Object> configuration, ViewContext viewContext) {
        if (UPA.getPersistenceUnit().findEntity(dt) != null) {
            Entity ee = UPA.getPersistenceUnit().findEntity(dt);
            return createPropertyViews(componentId, ee.getDataType(), configuration, viewContext);
        }
        return createPropertyViews(componentId, DataTypeFactory.forPlatformType(dt), configuration, viewContext);
    }

    public PropertyView[] createPropertyViews(String componentId, DataType dt, Map<String, Object> configuration, ViewContext viewContext) {
        if(viewContext==null){
            viewContext=new ViewContext();
        }
        return getPropertyViewFactory(dt).createPropertyView(componentId, dt, configuration, this, viewContext);
    }

    public void registerPropertyViewFactory(Field f, PropertyViewFactory factory) {
        if (factory == null) {
            propertyViewFactoryByField.remove(f.getAbsoluteName());
        } else {
            propertyViewFactoryByField.put(f.getAbsoluteName(), factory);
        }
    }

    public void registerPropertyViewFactory(DataType f, PropertyViewFactory factory) {
        if (factory == null) {
            propertyViewFactoryByDataType.remove(f.getClass());
        } else {
            propertyViewFactoryByDataType.put(f.getClass(), factory);
        }
    }

    public void registerPropertyViewFactory(Class f, PropertyViewFactory factory) {
        if (factory == null) {
            propertyViewFactoryByPlatformType.remove(f);
        } else {
            propertyViewFactoryByPlatformType.put(f, factory);
        }
    }

    public void registerPropertyViewValuesProvider(Field f, PropertyViewValuesProvider factory) {
        if (factory == null) {
            propertyViewValuesProviderByField.remove(f.getAbsoluteName());
        } else {
            propertyViewValuesProviderByField.put(f.getAbsoluteName(), factory);
        }
    }

    public void registerPropertyViewValuesProvider(DataType f, PropertyViewValuesProvider factory) {
        if (factory == null) {
            propertyViewValuesProviderByDataType.remove(f.getClass());
        } else {
            propertyViewValuesProviderByDataType.put(f.getClass(), factory);
        }
    }

    public void registerPropertyViewValuesProvider(Class f, PropertyViewValuesProvider factory) {
        if (factory == null) {
            propertyViewValuesProviderByPlatformType.remove(f);
        } else {
            propertyViewValuesProviderByPlatformType.put(f, factory);
        }
    }

    public PropertyViewFactory getDefaultPropertyViewFactory() {
        return defaultPropertyViewFactory;
    }

    public void setDefaultPropertyViewFactory(PropertyViewFactory defaultPropertyViewFactory) {
        this.defaultPropertyViewFactory = defaultPropertyViewFactory;
    }

    public PropertyViewValuesProvider getDefaultPropertyViewValuesProvider() {
        return defaultPropertyViewValuesProvider;
    }

    public void setDefaultPropertyViewValuesProvider(PropertyViewValuesProvider defaultPropertyViewValuesProvider) {
        this.defaultPropertyViewValuesProvider = defaultPropertyViewValuesProvider;
    }

}
