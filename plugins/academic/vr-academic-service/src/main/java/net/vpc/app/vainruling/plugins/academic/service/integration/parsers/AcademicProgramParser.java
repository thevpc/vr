/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration.parsers;

import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.core.service.integration.NameMap;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicProgram;

/**
 *
 * @author vpc
 */
public class AcademicProgramParser {

    private NameMap<Integer, AcademicProgram> cache = null;

    public void invalidate() {
        cache = null;
    }

    public void prepare() {
        if (cache == null) {
            cache = new NameMap<>(false);
            for (AcademicProgram a : AcademicPlugin.get().findPrograms()) {
                cache.put(a.getId(), a, a.getName(),null/*, a.getOtherNames()*/);
            }
        }
    }

    public AcademicProgram get(String name) {
        prepare();
        return cache.getByName(name);
    }

}
