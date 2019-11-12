/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration.parsers;

import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.core.service.integration.NameMap;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseType;

/**
 *
 * @author vpc
 */
public class AcademicCourseTypeParser {

    private NameMap<Integer, AcademicCourseType> cache = null;

    public void invalidate() {
        cache = null;
    }

    public void prepare() {
        if (cache == null) {
            cache = new NameMap<>(false);
            for (AcademicCourseType a : AcademicPlugin.get().findCourseTypes()) {
                cache.put(a.getId(), a, new String[]{
                    a.getName()
                });
            }
        }
    }

    public AcademicCourseType get(String name) {
        prepare();
        return cache.getByName(name);
    }

}
