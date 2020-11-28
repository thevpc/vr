/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.integration.parsers;

import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.integration.NameMap;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

/**
 *
 * @author vpc
 */
public class AcademicTeacherParser {

    private NameMap<Integer, AcademicTeacher> cache = null;

    public void invalidate() {
        cache = null;
    }

    public void prepare() {
        if (cache == null) {
            cache = new NameMap<>(false);
            for (AcademicTeacher a : AcademicPlugin.get().findTeachers()) {
                cache.put(a.getId(), a, new String[]{
                    a.getUser().getFullName(), 
                    a.getUser().getFullTitle(), 
                    a.getUser().getLogin()
                }, a.getOtherNames());
            }
        }
    }

    public AcademicTeacher get(String name) {
        prepare();
        return cache.getByName(name);
    }

}
