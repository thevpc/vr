package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 8/18/16.
 */
public class DefaultTeacherPeriodFilter implements TeacherPeriodFilter {
    private Boolean enabled;
    private final Set<Integer> acceptedTeachers = new HashSet<>();
    private final Set<Integer> acceptedOfficialDisciplines = new HashSet<>();
    private final Set<Integer> acceptedDegrees = new HashSet<>();
    private final Set<Integer> acceptedSituations = new HashSet<>();
    private final Set<Integer> acceptedDepartment = new HashSet<>();
    private final Set<Integer> rejectedTeachers = new HashSet<>();
    private final Set<Integer> rejectedOfficialDisciplines = new HashSet<>();
    private final Set<Integer> rejectedDegrees = new HashSet<>();
    private final Set<Integer> rejectedSituations = new HashSet<>();
    private final Set<Integer> rejectedDepartments = new HashSet<>();

    public DefaultTeacherPeriodFilter() {
    }

    public boolean acceptOfficialDiscipline(AcademicOfficialDiscipline officialDiscipline) {
        if (!acceptedOfficialDisciplines.isEmpty() && !acceptedOfficialDisciplines.contains(officialDiscipline == null ? null : officialDiscipline.getId())) {
            return false;
        }
        if (!rejectedOfficialDisciplines.isEmpty() && rejectedOfficialDisciplines.contains(officialDiscipline == null ? null : officialDiscipline.getId())) {
            return false;
        }
        return true;
    }

    public boolean acceptTeacherDegree(AcademicTeacherDegree degree) {
        if (!acceptedDegrees.isEmpty() && !acceptedDegrees.contains(degree == null ? null : degree.getId())) {
            return false;
        }
        if (!rejectedDegrees.isEmpty() && rejectedDegrees.contains(degree == null ? null : degree.getId())) {
            return false;
        }
        return true;
    }

    public boolean acceptTeacherSituation(AcademicTeacherSituation situation) {
        if (!acceptedSituations.isEmpty() && !acceptedSituations.contains(situation == null ? null : situation.getId())) {
            return false;
        }
        if (!rejectedSituations.isEmpty() && rejectedSituations.contains(situation == null ? null : situation.getId())) {
            return false;
        }
        return true;
    }

    public boolean acceptDepartment(AppDepartment department) {
        if (!acceptedDepartment.isEmpty() && !acceptedDepartment.contains(department == null ? null : department.getId())) {
            return false;
        }
        if (!rejectedDepartments.isEmpty() && rejectedDepartments.contains(department == null ? null : department.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean acceptTeacher(AcademicTeacherPeriod t) {
        if (enabled != null) {
            if (enabled.booleanValue() != t.isEnabled()) {
                return false;
            }
        }
        if (!acceptedTeachers.isEmpty() && !acceptedTeachers.contains(t.getId())) {
            return false;
        }
        if (!rejectedTeachers.isEmpty() && rejectedTeachers.contains(t.getId())) {
            return false;
        }
        if (!acceptOfficialDiscipline(t.getTeacher().getOfficialDiscipline())) {
            return false;
        }
        AcademicTeacherDegree degree = t.getDegree();
        if (degree == null) {
            degree = t.getTeacher().getDegree();
        }
        if (!acceptTeacherDegree(degree)) {
            return false;
        }
        AppDepartment department = t.getDepartment();
        if (department == null) {
            department = t.getTeacher().getUser().getDepartment();
        }
        if (!acceptDepartment(department)) {
            return false;
        }
        AcademicTeacherSituation situation = t.getSituation();
        if (situation == null) {
            situation = t.getTeacher().getSituation();
        }
        if (!acceptTeacherSituation(situation)) {
            return false;
        }
        return true;
    }

    public DefaultTeacherPeriodFilter addAcceptedTeacher(Integer id) {
        acceptedTeachers.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter addAcceptedOfficialDisciplines(Integer id) {
        acceptedOfficialDisciplines.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter addAcceptedDegree(Integer id) {
        acceptedDegrees.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter addAcceptedSituation(Integer id) {
        acceptedSituations.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter addAcceptedDepartment(Integer id) {
        acceptedDepartment.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter addRejectedTeacher(Integer id) {
        rejectedTeachers.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter addRejectedOfficialDiscipline(Integer id) {
        rejectedOfficialDisciplines.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter addRejectedDegree(Integer id) {
        rejectedDegrees.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter addRejectedSituation(Integer id) {
        rejectedSituations.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter addRejectedDepartment(Integer id) {
        rejectedDepartments.add(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeAcceptedTeacher(Integer id) {
        acceptedTeachers.remove(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeAcceptedOfficialDisciplines(Integer id) {
        acceptedOfficialDisciplines.remove(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeAcceptedDegree(Integer id) {
        acceptedDegrees.remove(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeAcceptedSituation(Integer id) {
        acceptedSituations.remove(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeAcceptedDepartment(Integer id) {
        acceptedDepartment.remove(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeRejectedTeacher(Integer id) {
        rejectedTeachers.remove(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeRejectedOfficialDiscipline(Integer id) {
        rejectedOfficialDisciplines.remove(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeRejectedDegree(Integer id) {
        rejectedDegrees.remove(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeRejectedSituation(Integer id) {
        rejectedSituations.remove(id);
        return this;
    }

    public DefaultTeacherPeriodFilter removeRejectedDepartment(Integer id) {
        rejectedDepartments.remove(id);
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public DefaultTeacherPeriodFilter setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
