package net.vpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 11/13/16.
 */
public class ApblTeamInfo {
    private List<AcademicStudent> students;
    private ProjectNode project;
    private TeamNode team;
    private AcademicTeacher coach;
    private List<ProjectNode> projects=new ArrayList<>();
    private List<AcademicTeacher> coaches=new ArrayList<>();
    private boolean interClasses;
    private boolean interDepartments;

    public int getProjectCount() {
        return projects.size();
    }

    public int getCoachCount() {
        return coaches.size();
    }

    public ProjectNode getProject() {
        return project;
    }

    public void setProject(ProjectNode project) {
        this.project = project;
    }

    public List<AcademicStudent> getStudents() {
        return students;
    }

    public void setStudents(List<AcademicStudent> students) {
        this.students = students;
    }

    public List<ProjectNode> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectNode> projects) {
        this.projects = projects;
    }
    

    public TeamNode getTeam() {
        return team;
    }

    public void setTeam(TeamNode team) {
        this.team = team;
    }

    public AcademicTeacher getCoach() {
        return coach;
    }

    public void setCoach(AcademicTeacher coach) {
        this.coach = coach;
    }

    public List<AcademicTeacher> getCoaches() {
        return coaches;
    }

    public void setCoaches(List<AcademicTeacher> coaches) {
        this.coaches = coaches;
    }

    public boolean isInterClasses() {
        return interClasses;
    }

    public ApblTeamInfo setInterClasses(boolean interClasses) {
        this.interClasses = interClasses;
        return this;
    }

    public boolean isInvalid() {
        if(team==null || team.getTeam()==null){
           return true;
        }
        if(project==null || project.getProject()==null){
            return true;
        }
        if(coach==null){
            return true;
        }
        return false;
    }

    public boolean isErrNoTeam(){
        return team == null || team.getTeam() == null;
    }

    public boolean isErrNoProject(){
        return project == null || project.getProject() == null;
    }

    public boolean isErrNoCoach(){
        return (coach==null);
    }

    public String getInvalidObservations() {
        StringBuilder sb=new StringBuilder();
        if(team==null || team.getTeam()==null){
            if(sb.length()>0){
                sb.append(", ");
            }
            sb.append("Sans Equipe");
        }
        if(project==null || project.getProject()==null){
            if(sb.length()>0){
                sb.append(", ");
            }
            sb.append("Sans Projet");
        }
        if(coach==null){
            if(sb.length()>0){
                sb.append(", ");
            }
            sb.append("Sans Coach");
        }
        return sb.toString();
    }
    public boolean isInterDepartments() {
        return interDepartments;
    }

    public ApblTeamInfo setInterDepartments(boolean interDepartments) {
        this.interDepartments = interDepartments;
        return this;
    }
}
