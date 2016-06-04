/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.app.vainruling.core.service.obj.NamedId;
import net.vpc.app.vainruling.core.web.obj.defaultimpl.EnumTypePropertyViewValuesProvider;
import net.vpc.app.vainruling.core.web.obj.defaultimpl.EntityTypePropertyViewValuesProvider;
import java.util.List;
import net.vpc.upa.Field;
import net.vpc.upa.KeyType;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EntityType;
import net.vpc.upa.types.EnumType;

/**
 *
 * @author vpc
 */
public class DefaultPropertyViewValuesProvider implements PropertyViewValuesProvider {

    EntityTypePropertyViewValuesProvider entityProvider = new EntityTypePropertyViewValuesProvider();
    EnumTypePropertyViewValuesProvider enumProvider = new EnumTypePropertyViewValuesProvider();

    @Override
    public List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType dt, ViewContext viewContext) {
        if (dt instanceof EntityType) {
            return entityProvider.resolveValues(propertyView, field, dt, viewContext);
        }
        if (dt instanceof KeyType) {
            return entityProvider.resolveValues(propertyView, field, dt, viewContext);
        }
        if (dt instanceof EnumType) {
            return enumProvider.resolveValues(propertyView, field, dt, viewContext);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
