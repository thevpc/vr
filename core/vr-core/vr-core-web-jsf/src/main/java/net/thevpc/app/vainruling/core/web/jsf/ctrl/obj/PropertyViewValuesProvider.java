/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.thevpc.app.vainruling.core.service.editor.ViewContext;
import net.thevpc.upa.Field;
import net.thevpc.upa.NamedId;
import net.thevpc.upa.types.DataType;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public interface PropertyViewValuesProvider {
    List<NamedId> resolveValues(PropertyView propertyView, Field field, DataType dt, ViewContext viewContext);
}
