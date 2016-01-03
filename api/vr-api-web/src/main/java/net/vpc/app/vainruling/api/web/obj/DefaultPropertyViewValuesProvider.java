/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj;

import net.vpc.app.vainruling.api.web.obj.defaultimpl.EnumTypePropertyViewValuesProvider;
import net.vpc.app.vainruling.api.web.obj.defaultimpl.EntityTypePropertyViewValuesProvider;
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
    public List<Object> resolveValues(PropertyView propertyView, Field field, DataType dt) {
        if (dt instanceof EntityType) {
            return entityProvider.resolveValues(propertyView, field, dt);
        }
        if (dt instanceof KeyType) {
            return entityProvider.resolveValues(propertyView, field, dt);
        }
        if (dt instanceof EnumType) {
            return enumProvider.resolveValues(propertyView, field, dt);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
