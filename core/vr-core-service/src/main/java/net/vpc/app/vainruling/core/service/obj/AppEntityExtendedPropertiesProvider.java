/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.obj;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author vpc
 */
public interface AppEntityExtendedPropertiesProvider {
    public Map<String,Object> getExtendedPropertyValues(Object o);
    public Set<String> getExtendedPropertyNames(Class o);
}
