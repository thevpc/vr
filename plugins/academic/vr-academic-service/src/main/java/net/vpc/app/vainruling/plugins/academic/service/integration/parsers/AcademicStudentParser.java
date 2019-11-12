/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration.parsers;

import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.core.service.integration.NameMap;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudentStage;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

/**
 *
 * @author vpc
 */
public class AcademicStudentParser {

    private NameMap<Integer, AcademicStudent> cache = null;

    public void invalidate() {
        cache = null;
    }

    public void prepare() {
        if (cache == null) {
            cache = new NameMap<>(false);
            for (AcademicStudent a : AcademicPlugin.get().findStudents(null, AcademicStudentStage.ATTENDING)) {
                cache.put(a.getId(), a, new String[]{
                    a.getUser().getFullName(),
                    a.getUser().getFullTitle(),
                    a.getUser().getLogin()
                });/*a.getOtherNames()*/;
            }
        }
    }

    public AcademicStudent get(String name) {
        prepare();
        return cache.getByName(name);
    }

}
