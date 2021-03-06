/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions;

import java.util.List;

import net.thevpc.app.vainruling.VrMaintenanceAction;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppConfig;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherSemestrialLoad;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class AcademicMaintenanceAction implements VrMaintenanceAction {
    @Autowired
    private CorePlugin core;
    
    @Autowired
    private AcademicPlugin aca;
    
    @Override
    public String getName() {
        return "Academic Maintenance";
    }

    @Override
    public void run() {
        AppConfig appConfig = core.getCurrentConfig();
        if (appConfig != null) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            AppPeriod mainPeriod = appConfig.getMainPeriod();
            if (mainPeriod != null) {
                List<AcademicCoursePlan> academicCoursePlanList = pu.findAll(AcademicCoursePlan.class);
                for (AcademicCoursePlan p : academicCoursePlanList) {
                    if (p.getPeriod() == null) {
                        p.setPeriod(mainPeriod);
                        pu.createUpdateQuery(p).update("period").execute();
                    }
                }
                List<AcademicTeacherSemestrialLoad> academicTeacherSemestrialLoadList = pu.findAll(AcademicTeacherSemestrialLoad.class);
                for (AcademicTeacherSemestrialLoad p : academicTeacherSemestrialLoadList) {
                    if (p.getPeriod() == null) {
                        p.setPeriod(mainPeriod);
                        pu.createUpdateQuery(p).update("period").execute();
                    }
                }
                for (AcademicTeacher academicTeacher : aca.findTeachers()) {
                    aca.updateTeacherPeriod(mainPeriod.getId(), academicTeacher.getId(), -1);
                }
            }
        }
    }

}
