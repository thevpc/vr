/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.web.obj.ViewContext;
import net.vpc.upa.Field;
import net.vpc.upa.types.DataType;

import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
public interface PropertyViewFactory {

    PropertyView[] createPropertyView(String componentId, Field field, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext);

    PropertyView[] createPropertyView(String componentId, DataType dt, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext);
//    public PropertyView[] createPropertyViews(String componentId, Field field, DataType dt, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext);
}
