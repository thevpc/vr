/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.editor.ViewContext;
import net.thevpc.upa.Field;
import net.thevpc.upa.KeyType;
import net.thevpc.upa.NamedId;
import net.thevpc.upa.types.DataType;
import net.thevpc.upa.types.EnumType;
import net.thevpc.upa.types.ManyToOneType;

import java.util.List;
import net.thevpc.upa.exceptions.UnsupportedUPAFeatureException;

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
