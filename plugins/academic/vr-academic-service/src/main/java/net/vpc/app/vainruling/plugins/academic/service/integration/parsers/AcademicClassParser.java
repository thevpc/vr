/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration.parsers;

import net.vpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.core.service.integration.NameMap;

/**
 *
 * @author vpc
 */
public class AcademicClassParser {

    private NameMap<Integer, AcademicClass> cache = null;

    public void invalidate() {
        cache = null;
    }

    public void prepare() {
        if (cache == null) {
            cache = new NameMap<>(false);
            for (AcademicClass a : AcademicPlugin.get().findAcademicClasses()) {
                cache.put(a.getId(), a, a.getName(), a.getOtherNames());
            }
        }
    }

    public AcademicClass getById(int id) {
        prepare();
        return cache.getById(id);
    }
    
    public AcademicClass get(String className) {
        prepare();
        return cache.getByName(className);
    }

}
