/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.web.obj.ViewContext;
import net.vpc.upa.Field;
import net.vpc.upa.NamedId;
import net.vpc.upa.types.DataType;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public interface PropertyViewValuesProvider {
    List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType dt, ViewContext viewContext);
}
