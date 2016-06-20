/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.upa.Field;
import net.vpc.upa.NamedId;
import net.vpc.upa.types.DataType;

import java.util.List;

/**
 * @author vpc
 */
public interface PropertyViewValuesProvider {
    public List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType dt, ViewContext viewContext);
}
