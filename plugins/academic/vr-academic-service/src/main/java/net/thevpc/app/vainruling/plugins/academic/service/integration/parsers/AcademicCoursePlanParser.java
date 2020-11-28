/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.integration.parsers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.integration.NameMap;

/**
 *
 * @author vpc
 */
public class AcademicCoursePlanParser {

    private Map<CpId, NameMap<Integer, AcademicCoursePlan>> cache = new HashMap<>();

    public static class CpId {

        Integer periodId;
        Integer semesterId;
        Integer programId;

        public CpId(Integer periodId, Integer semesterId, Integer programId) {
            this.periodId = periodId;
            this.semesterId = semesterId;
            this.programId = programId;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode(this.periodId);
            hash = 89 * hash + Objects.hashCode(this.semesterId);
            hash = 89 * hash + Objects.hashCode(this.programId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CpId other = (CpId) obj;
            if (!Objects.equals(this.periodId, other.periodId)) {
                return false;
            }
            if (!Objects.equals(this.semesterId, other.semesterId)) {
                return false;
            }
            if (!Objects.equals(this.programId, other.programId)) {
                return false;
            }
            return true;
        }
    }

    public AcademicCoursePlan get(Integer periodId, Integer semesterId, Integer programId, String className) {
        CpId id = new CpId(periodId, semesterId, programId);
        NameMap<Integer, AcademicCoursePlan> cc = cache.get(id);

        if (cc == null) {
            cc = new NameMap<>(true);
            cache.put(id, cc);
            for (AcademicCoursePlan cp : AcademicPlugin.get().findCoursePlans(periodId, semesterId, programId)) {
                cc.put(cp.getId(), cp,
                        new String[]{
                            cp.getName(),
                            cp.getCode(),
                            cp.getFullName()
                        },
                        cp.getOtherNames()
                );
            }
        }
        return cc.getByName(className);
    }

}
