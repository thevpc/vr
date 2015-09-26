/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj;

import net.vpc.app.vainruling.api.web.obj.defaultimpl.DefaultPropertyViewFactory;
import java.util.HashMap;
import java.util.Map;
import net.vpc.common.utils.ClassMap;
import net.vpc.upa.Field;
import net.vpc.upa.types.DataType;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class PropertyViewManager {

    private PropertyViewFactory defaultPropertyViewFactory = new DefaultPropertyViewFactory();
    private PropertyViewValuesProvider defaultPropertyViewValuesProvider = new DefaultPropertyViewValuesProvider();

    private final Map<String, PropertyViewValuesProvider> propertyViewValuesProviderByField = new HashMap<>();
    private final ClassMap<PropertyViewValuesProvider> propertyViewValuesProviderByDataType = new ClassMap<>(Object.class, PropertyViewValuesProvider.class);
    private final ClassMap<PropertyViewValuesProvider> propertyViewValuesProviderByPlatformType = new ClassMap<>(Object.class, PropertyViewValuesProvider.class);

    private final Map<String, PropertyViewFactory> propertyViewFactoryByField = new HashMap<>();
    private final ClassMap<PropertyViewFactory> propertyViewFactoryByDataType = new ClassMap<>(Object.class, PropertyViewFactory.class);
    private final ClassMap<PropertyViewFactory> propertyViewFactoryByPlatformType = new ClassMap<>(Object.class, PropertyViewFactory.class);

    public PropertyViewValuesProvider getPropertyViewValuesProvider(Field field, DataType dt) {
        PropertyViewValuesProvider v = null;
        v = propertyViewValuesProviderByField.get(field.getAbsoluteName());
        if (v != null) {
            return v;
        }
        v = propertyViewValuesProviderByDataType.get(field.getDataType().getClass());
        if (v != null) {
            return v;
        }
        v = propertyViewValuesProviderByPlatformType.get(field.getDataType().getPlatformType());
        if (v != null) {
            return v;
        }
        return defaultPropertyViewValuesProvider;
    }

    public PropertyViewFactory getPropertyViewFactory(Field field, DataType dt) {
        PropertyViewFactory v = null;
        v = propertyViewFactoryByField.get(field.getAbsoluteName());
        if (v != null) {
            return v;
        }
        v = propertyViewFactoryByDataType.get(field.getDataType().getClass());
        if (v != null) {
            return v;
        }
        v = propertyViewFactoryByPlatformType.get(field.getDataType().getPlatformType());
        if (v != null) {
            return v;
        }
        return defaultPropertyViewFactory;
    }

    public PropertyView[] createPropertyView(String componentId, Field field,Map<String,Object> configuration) {
        DataType dt = field.getDataType();
        return getPropertyViewFactory(field, dt).createPropertyView(componentId, field, dt, configuration,this);
    }

    public PropertyView[] createPropertyView(String componentId, DataType dt,Map<String,Object> configuration) {
        return getPropertyViewFactory(null, dt).createPropertyView(componentId, null, dt, configuration,this);
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
