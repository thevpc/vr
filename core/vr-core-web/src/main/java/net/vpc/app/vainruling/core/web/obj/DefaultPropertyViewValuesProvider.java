/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.app.vainruling.core.web.obj.defaultimpl.EntityTypePropertyViewValuesProvider;
import net.vpc.app.vainruling.core.web.obj.defaultimpl.EnumTypePropertyViewValuesProvider;
import net.vpc.upa.Field;
import net.vpc.upa.KeyType;
import net.vpc.upa.NamedId;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EnumType;
import net.vpc.upa.types.ManyToOneType;

import java.util.List;

/**
 * @author vpc
 */
public class DefaultPropertyViewValuesProvider implements PropertyViewValuesProvider {

    EntityTypePropertyViewValuesProvider entityProvider = new EntityTypePropertyViewValuesProvider();
    EnumTypePropertyViewValuesProvider enumProvider = new EnumTypePropertyViewValuesProvider();

    @Override
    public List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType dt, ViewContext viewContext) {
        if (dt instanceof ManyToOneType) {

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
