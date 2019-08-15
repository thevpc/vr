/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.editor;

import java.util.Map;
import java.util.Set;

/**
 * @author taha.bensalah@gmail.com
 */
public interface AppEntityExtendedPropertiesProvider {

    Map<String, Object> getExtendedPropertyValues(Object o);

    Set<String> getExtendedPropertyNames(Class o);
}
