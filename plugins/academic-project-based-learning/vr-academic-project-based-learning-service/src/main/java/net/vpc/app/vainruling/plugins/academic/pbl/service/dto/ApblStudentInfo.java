package net.vpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 11/13/16.
 */
public class ApblStudentInfo {
    private AcademicStudent student;
    private ProjectNode project;
    private TeamNode team;
    private AcademicTeacher coach;
    private List<TeamNode> teams=new ArrayList<>();
    private List<ProjectNode> projects=new ArrayList<>();
    private List<AcademicTeacher> coaches=new ArrayList<>();
    private boolean interClasses;
    private boolean interDepartments;

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }

    public int getTeamsCount() {
        return teams.size();
    }

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

    public List<TeamNode> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamNode> teams) {
        this.teams = teams;
    }

    public List<ProjectNode> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectNode> projects) {
        this.projects = projects;
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

    public ApblStudentInfo setInterClasses(boolean interClasses) {
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
        if(teams.size()>1){
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

    public boolean isErrTooManyTeams(){
        return teams.size()>1;
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
        if(teams.size()>1){
            if(sb.length()>0){
                sb.append(", ");
            }
            sb.append("Plusieurs Equipes");
        }
        return sb.toString();
    }
    public boolean isInterDepartments() {
        return interDepartments;
    }

    public ApblStudentInfo setInterDepartments(boolean interDepartments) {
        this.interDepartments = interDepartments;
        return this;
    }
}
