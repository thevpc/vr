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
    private Map<Integer,Double> programSessionsLoadById =new HashMap<>();
//    private Map<String,Double> programsLoadByName=new HashMap<>();

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

    public Map<Integer, Double> getProgramSessionsLoadById() {
        return programSessionsLoadById;
    }

    public void setProgramSessionsLoadById(Map<Integer, Double> programSessionsLoadById) {
        this.programSessionsLoadById = programSessionsLoadById;
    }

//    public double getProgramLoadByName(String programName) {
//        Double d = programsLoadByName.get(programName);
//        if(d==null){
//            return 0.0;
//        }
//        return d;
//    }
//
//    public double addProgramLoadByName(String programName,double value) {
//        Double d = programsLoadByName.get(programName);
//        if(d==null){
//            d=value;
//        }else{
//            d=d+value;
//        }
//        programsLoadByName.put(programName,d);
//        return d;
//    }
//    public void setProgramLoadByName(String programName,double value) {
//        programsLoadByName.put(programName,value);
//    }
//    public Map<String, Double> getProgramsLoadByName() {
//        return programsLoadByName;
//    }
//
//    public void setProgramsLoadByName(Map<String, Double> programsLoadByName) {
//        this.programsLoadByName = programsLoadByName;
//    }

    public void setProgramSessionLoadById(int id, double value) {
        programSessionsLoadById.put(id,value);
    }

    public double getProgramSessionLoadById(int id) {
        Double d = programSessionsLoadById.get(id);
        if(d==null){
            return 0.0;
        }
        return d;
    }

    public double addProgramSessionLoadById(int id, double value) {
        Double d = programSessionsLoadById.get(id);
        if(d==null){
            d=value;
        }else{
            d=d+value;
        }
        programSessionsLoadById.put(id,d);
        return d;
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
