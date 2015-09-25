/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import java.util.List;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewValuesProvider;
import net.vpc.upa.Field;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EnumType;

/**
 *
 * @author vpc
 */
public class EnumTypePropertyViewValuesProvider implements PropertyViewValuesProvider {

    @Override
    public List<Object> resolveValues(PropertyView propertyView, Field field, DataType dt) {
        EnumType t = (EnumType) dt;
        return t.getValues();
    }

}
