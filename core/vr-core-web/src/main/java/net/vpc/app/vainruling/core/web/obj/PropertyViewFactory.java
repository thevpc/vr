/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import java.util.Map;
import net.vpc.upa.Field;
import net.vpc.upa.types.DataType;

/**
 *
 * @author vpc
 */
public interface PropertyViewFactory {

    public PropertyView[] createPropertyView(String componentId, Field field, DataType dt, Map<String, Object> configuration, PropertyViewManager manager, ViewContext viewContext);
}
