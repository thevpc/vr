/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.planning.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarClass;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarTeacher;
import net.thevpc.app.vainruling.plugins.academic.service.integration.parsers.AcademicParsers;

/**
 *
 * @author vpc
 */
public class CalendarAssignmentsMappingBuilder {

    private final AcademicParsers parsers = new AcademicParsers();
    private AppPeriod period;
    private AcademicSemester semester;
    private final Map<String, CalendarClass> classes = new HashMap<>();
    private final Map<String, CalendarTeacher> teachers = new HashMap<>();
    private final Map<CpId, CalendarCoursePlan> coursePlans = new HashMap<>();

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
    }

    public CalendarClass findCalendarClass(String name) {
        CalendarClass o = classes.get(name);
        if (o == null) {
            classes.put(name, o = new CalendarClass(name, parsers.getClasses().get(name)));
        }
        return o;
    }

    public CalendarTeacher findCalendarTeacher(String name) {
        CalendarTeacher o = teachers.get(name);
        if (o == null) {
            teachers.put(name, o = new CalendarTeacher(name, parsers.getTeachers().get(name)));
        }
        return o;
    }

    public CalendarCoursePlan findCalendarCoursePlanByClass(int classId, String name) {
        AcademicClass cls = parsers.getClasses().getById(classId);
        String normalizeName = VrUtils.normalizeName(name);
        CpId cc;
        if (cls == null) {
            cc = new CpId(period.getId(), semester.getId(), -1, normalizeName);
        } else {
            cc = new CpId(period.getId(), semester.getId(), cls.getProgram().getId(), normalizeName);
        }
        CalendarCoursePlan o = coursePlans.get(cc);
        if (o == null) {
            coursePlans.put(cc, o = new CalendarCoursePlan(name, parsers.getCoursePlans().get(period.getId(), semester.getId(), cc.programId, name)));
        }
        return o;
    }

    public AcademicParsers getParsers() {
        return parsers;
    }

    public static class CpId {

        Integer periodId;
        Integer semesterId;
        Integer programId;
        String name;

        public CpId(Integer periodId, Integer semesterId, Integer programId, String name) {
            this.periodId = periodId;
            this.semesterId = semesterId;
            this.programId = programId;
            this.name = name;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode(this.periodId);
            hash = 29 * hash + Objects.hashCode(this.semesterId);
            hash = 29 * hash + Objects.hashCode(this.programId);
            hash = 29 * hash + Objects.hashCode(this.name);
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
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
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
}
