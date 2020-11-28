/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.editor.ViewContext;
import net.thevpc.upa.Field;
import net.thevpc.upa.types.DataType;

import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
public interface PropertyViewFactory {

    PropertyView[] createPropertyView(String componentId, Field field, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext);

    PropertyView[] createPropertyView(String componentId, DataType dt, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext);
//    public PropertyView[] createPropertyViews(String componentId, Field field, DataType dt, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext);
}
