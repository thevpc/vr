package net.vpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vpc on 11/13/16.
 */
public class ApblTeacherInfo {
    private AcademicTeacher teacher;
    private double studentsCount;
    private double load;
    private List<TeamNode> teams=new ArrayList<>();
    private Set<AcademicStudent> students=new HashSet<>();

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public ApblTeacherInfo setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
        return this;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public int getTeamsCount() {
        return teams.size();
    }

    public double getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(double studentsCount) {
        this.studentsCount = studentsCount;
    }

    public List<TeamNode> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamNode> teams) {
        this.teams = teams;
    }

    public Set<AcademicStudent> getStudents() {
        return students;
    }

    public void setStudents(Set<AcademicStudent> students) {
        this.students = students;
    }
}
