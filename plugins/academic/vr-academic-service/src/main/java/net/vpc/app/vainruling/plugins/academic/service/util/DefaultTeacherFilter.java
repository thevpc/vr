package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 8/18/16.
 */
public class DefaultTeacherFilter implements TeacherFilter {
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

    @Override
    public boolean acceptTeacher(AcademicTeacher t) {
        if(!acceptedTeachers.isEmpty() && !acceptedTeachers.contains(t.getId())){
            return false;
        }
        if(!rejectedTeachers.isEmpty() && rejectedTeachers.contains(t.getId())){
            return false;
        }
        if(!acceptedOfficialDisciplines.isEmpty() && !acceptedOfficialDisciplines.contains(t.getOfficialDiscipline()==null ?null:t.getOfficialDiscipline().getId())){
            return false;
        }
        if(!rejectedOfficialDisciplines.isEmpty() && rejectedOfficialDisciplines.contains(t.getOfficialDiscipline()==null ?null:t.getOfficialDiscipline().getId())){
            return false;
        }
        if(!acceptedDegrees.isEmpty() && !acceptedDegrees.contains(t.getDegree()==null ?null:t.getDegree().getId())){
            return false;
        }
        if(!rejectedDegrees.isEmpty() && rejectedDegrees.contains(t.getDegree()==null ?null:t.getDegree().getId())){
            return false;
        }
        if(!acceptedDepartment.isEmpty() && !acceptedDepartment.contains(t.getDepartment()==null ?null:t.getDepartment().getId())){
            return false;
        }
        if(!rejectedDepartments.isEmpty() && rejectedDepartments.contains(t.getDepartment()==null ?null:t.getDepartment().getId())){
            return false;
        }
        if(!acceptedSituations.isEmpty() && !acceptedSituations.contains(t.getSituation()==null ?null:t.getSituation().getId())){
            return false;
        }
        if(!rejectedSituations.isEmpty() && rejectedSituations.contains(t.getSituation()==null ?null:t.getSituation().getId())){
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

}
