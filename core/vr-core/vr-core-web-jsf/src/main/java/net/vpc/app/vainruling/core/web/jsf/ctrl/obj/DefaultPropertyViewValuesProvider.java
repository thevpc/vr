/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.editor.ViewContext;
import net.vpc.upa.Field;
import net.vpc.upa.KeyType;
import net.vpc.upa.NamedId;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EnumType;
import net.vpc.upa.types.ManyToOneType;

import java.util.List;
import net.vpc.upa.exceptions.UnsupportedUPAFeatureException;

/**
 * @author taha.bensalah@gmail.com
 */
public class DefaultPropertyViewValuesProvider implements PropertyViewValuesProvider {

    ManyToOneTypePropertyViewValuesProvider manyToOneTypeProvider = new ManyToOneTypePropertyViewValuesProvider();
    KeyTypePropertyViewValuesProvider keyTypeProvider = new KeyTypePropertyViewValuesProvider();
    EnumTypePropertyViewValuesProvider enumProvider = new EnumTypePropertyViewValuesProvider();

    @Override
    public List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType dt, ViewContext viewContext) {
        if (dt instanceof ManyToOneType) {
            return manyToOneTypeProvider.resolveValues(propertyView, field, dt, viewContext);
        }
        if (dt instanceof KeyType) {
            return keyTypeProvider.resolveValues(propertyView, field, dt, viewContext);
        }
        if (dt instanceof EnumType) {
            return enumProvider.resolveValues(propertyView, field, dt, viewContext);
        }
        throw new UnsupportedUPAFeatureException("Not supported yet.");
    }

}
