/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicFormerStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.EmploymentDelay;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Record;
import net.vpc.upa.UPA;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPrePersist;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicInternshipPersistenceUnitCallback {


    @OnPrePersist
    public void onPrePersist(PersistEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicFormerStudent.class)) {
            onPreAcademicFormerStudentCreated(entity, event.getPersistedObject());
        }
    }


    private void onPreAcademicFormerStudentCreated(net.vpc.upa.Entity entity, Object updatesObject) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicInternshipPlugin ish = VrApp.getBean(AcademicInternshipPlugin.class);

        if (updatesObject instanceof AcademicFormerStudent) {

            updatesObject=entity.getBuilder().objectToRecord(updatesObject);
        }

        if (updatesObject instanceof Record) {
            Record u=(Record) updatesObject;
            AcademicStudent s = u.getObject("student");
            if (s != null) {
                AcademicInternship pfe = ish.findStudentPFE(s.getId());
                if (pfe != null) {
                    if (StringUtils.isEmpty(u.getString("graduationProjectTitle"))) {
                        u.setObject("graduationProjectTitle",pfe.getName());
                    }
                    if (StringUtils.isEmpty(u.getString("graduationProjectSummary"))) {
                        u.setObject("graduationProjectSummary",pfe.getDescription());
                    }
                    if (StringUtils.isEmpty(u.getString("graduationProjectSupervisor"))) {
                        StringBuilder sb = new StringBuilder();
                        if (pfe.getSupervisor() != null) {
                            sb.append(pfe.getSupervisor().getContact().getFullName());
                        }
                        if (pfe.getSecondSupervisor() != null) {
                            if (sb.length() > 0) {
                                sb.append(", ");
                            }
                            sb.append(pfe.getSecondSupervisor().getContact().getFullName());
                        }
                        u.setObject("graduationProjectSupervisor",sb.toString());
                    }
                    if (StringUtils.isEmpty(u.getString("graduationProjectJury"))) {
                        StringBuilder sb = new StringBuilder();
                        if (pfe.getChairExaminer() != null) {
                            sb.append("Pr:").append(pfe.getChairExaminer().getContact().getFullName());
                        }
                        if (pfe.getFirstExaminer() != null) {
                            if (sb.length() > 0) {
                                sb.append(", ");
                            }
                            sb.append("R:").append(pfe.getFirstExaminer().getContact().getFullName());
                        }
                        if (pfe.getSecondExaminer() != null) {
                            if (sb.length() > 0) {
                                sb.append(", ");
                            }
                            sb.append("R:").append(pfe.getSecondExaminer().getContact().getFullName());
                        }
                        u.setObject("graduationProjectJury",sb.toString());
                        if (pfe.isPreEmployment()) {
                            u.setObject("employmentDelay",EmploymentDelay.INTERNSHIP);
                        }
                    }
                }
            }
        }

    }
}
