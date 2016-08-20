package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 8/18/16.
 */
public class DefaultTeacherFilter implements TeacherFilter {
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

    public DefaultTeacherFilter() {
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
            department = t.getTeacher().getDepartment();
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

    public DefaultTeacherFilter addAcceptedTeacher(Integer id) {
        acceptedTeachers.add(id);
        return this;
    }

    public DefaultTeacherFilter addAcceptedOfficialDisciplines(Integer id) {
        acceptedOfficialDisciplines.add(id);
        return this;
    }

    public DefaultTeacherFilter addAcceptedDegree(Integer id) {
        acceptedDegrees.add(id);
        return this;
    }

    public DefaultTeacherFilter addAcceptedSituation(Integer id) {
        acceptedSituations.add(id);
        return this;
    }

    public DefaultTeacherFilter addAcceptedDepartment(Integer id) {
        acceptedDepartment.add(id);
        return this;
    }

    public DefaultTeacherFilter addRejectedTeacher(Integer id) {
        rejectedTeachers.add(id);
        return this;
    }

    public DefaultTeacherFilter addRejectedOfficialDiscipline(Integer id) {
        rejectedOfficialDisciplines.add(id);
        return this;
    }

    public DefaultTeacherFilter addRejectedDegree(Integer id) {
        rejectedDegrees.add(id);
        return this;
    }

    public DefaultTeacherFilter addRejectedSituation(Integer id) {
        rejectedSituations.add(id);
        return this;
    }

    public DefaultTeacherFilter addRejectedDepartment(Integer id) {
        rejectedDepartments.add(id);
        return this;
    }

    public DefaultTeacherFilter removeAcceptedTeacher(Integer id) {
        acceptedTeachers.remove(id);
        return this;
    }

    public DefaultTeacherFilter removeAcceptedOfficialDisciplines(Integer id) {
        acceptedOfficialDisciplines.remove(id);
        return this;
    }

    public DefaultTeacherFilter removeAcceptedDegree(Integer id) {
        acceptedDegrees.remove(id);
        return this;
    }

    public DefaultTeacherFilter removeAcceptedSituation(Integer id) {
        acceptedSituations.remove(id);
        return this;
    }

    public DefaultTeacherFilter removeAcceptedDepartment(Integer id) {
        acceptedDepartment.remove(id);
        return this;
    }

    public DefaultTeacherFilter removeRejectedTeacher(Integer id) {
        rejectedTeachers.remove(id);
        return this;
    }

    public DefaultTeacherFilter removeRejectedOfficialDiscipline(Integer id) {
        rejectedOfficialDisciplines.remove(id);
        return this;
    }

    public DefaultTeacherFilter removeRejectedDegree(Integer id) {
        rejectedDegrees.remove(id);
        return this;
    }

    public DefaultTeacherFilter removeRejectedSituation(Integer id) {
        rejectedSituations.remove(id);
        return this;
    }

    public DefaultTeacherFilter removeRejectedDepartment(Integer id) {
        rejectedDepartments.remove(id);
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public DefaultTeacherFilter setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
