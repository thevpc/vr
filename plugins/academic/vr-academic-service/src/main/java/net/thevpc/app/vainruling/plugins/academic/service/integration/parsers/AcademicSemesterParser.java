/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.integration.parsers;

import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.integration.NameMap;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;

/**
 *
 * @author vpc
 */
public class AcademicSemesterParser {

    private NameMap<Integer, AcademicSemester> cache = null;

    public void invalidate() {
        cache = null;
    }

    public void prepare() {
        if (cache == null) {
            cache = new NameMap<>(false);
            for (AcademicSemester a : AcademicPlugin.get().findSemesters()) {
                cache.put(a.getId(), a, 
                        new String[]{
                    a.getName(),
                    a.getCode(),
                    a.getIndex() > 0 ? ("S" + a.getIndex()) : null
                });/*a.getOtherNames()*/;
            }
        }
    }

    public AcademicSemester get(String name) {
        prepare();
        return cache.getByName(name);
    }

}
