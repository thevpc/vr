/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj;

import java.util.List;
import net.vpc.upa.Field;
import net.vpc.upa.types.DataType;

/**
 *
 * @author vpc
 */
public interface PropertyViewValuesProvider {
    public List<Object> resolveValues(PropertyView propertyView, Field field, DataType dt);
}
