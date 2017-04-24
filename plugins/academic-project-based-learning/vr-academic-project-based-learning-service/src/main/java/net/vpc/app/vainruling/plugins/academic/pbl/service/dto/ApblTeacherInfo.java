package net.vpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

import java.util.*;

/**
 * Created by vpc on 11/13/16.
 */
public class ApblTeacherInfo {
    private AcademicTeacher teacher;
    private double studentsCount;
    private double load;
    private List<TeamNode> teams=new ArrayList<>();
    private Set<AcademicStudent> students=new HashSet<>();
    private Map<Integer,Double> programsLoadById=new HashMap<>();
    private Map<String,Double> programsLoadByName=new HashMap<>();

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

    public double getStudentsCountByProgram(String programName) {
        int x=0;
        for (AcademicStudent student : students) {
            if(
                    (student.getLastClass1()!=null && student.getLastClass1().getProgram()!=null && student.getLastClass1().getProgram().getName().equals(programName))
                    ||(student.getLastClass2()!=null && student.getLastClass2().getProgram()!=null && student.getLastClass2().getProgram().getName().equals(programName))
                            ||(student.getLastClass3()!=null && student.getLastClass3().getProgram()!=null && student.getLastClass3().getProgram().getName().equals(programName))
                    ){
                x++;
            }
        }
        return x;
    }

    public Map<Integer, Double> getProgramsLoadById() {
        return programsLoadById;
    }

    public void setProgramsLoadById(Map<Integer, Double> programsLoadById) {
        this.programsLoadById = programsLoadById;
    }

    public Map<String, Double> getProgramsLoadByName() {
        return programsLoadByName;
    }

    public void setProgramsLoadByName(Map<String, Double> programsLoadByName) {
        this.programsLoadByName = programsLoadByName;
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
