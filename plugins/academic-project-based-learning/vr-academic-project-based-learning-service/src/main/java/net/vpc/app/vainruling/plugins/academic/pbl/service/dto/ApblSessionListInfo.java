package net.vpc.app.vainruling.plugins.academic.pbl.service.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 4/20/17.
 */
public class ApblSessionListInfo {
    private List<ApblTeacherInfo> teachers=new ArrayList<>();
    private List<ApblSessionInfo> sessions=new ArrayList<>();
    private int teamsCount;
    private double unitLoad;
    private double maxStudentCount;
    private double coachedStudentCount;
    private double teamedStudentCount;
    private double baseStudentCount;

    public List<ApblTeacherInfo> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<ApblTeacherInfo> teachers) {
        this.teachers = teachers;
    }

    public List<ApblSessionInfo> getSessions() {
        return sessions;
    }

    public void setSessions(List<ApblSessionInfo> sessions) {
        this.sessions = sessions;
    }

    public double getMaxStudentCount() {
        return maxStudentCount;
    }

    public void setMaxStudentCount(double maxStudentCount) {
        this.maxStudentCount = maxStudentCount;
    }

    public double getTeamedStudentCount() {
        return teamedStudentCount;
    }

    public void setTeamedStudentCount(double teamedStudentCount) {
        this.teamedStudentCount = teamedStudentCount;
    }

    public double getBaseStudentCount() {
        return baseStudentCount;
    }

    public void setBaseStudentCount(double baseStudentCount) {
        this.baseStudentCount = baseStudentCount;
    }

    public double getCoachedStudentCount() {
        return coachedStudentCount;
    }

    public void setCoachedStudentCount(double coachedStudentCount) {
        this.coachedStudentCount = coachedStudentCount;
    }

    public int getTeamsCount() {
        return teamsCount;
    }

    public void setTeamsCount(int teamsCount) {
        this.teamsCount = teamsCount;
    }

    public double getUnitLoad() {
        return unitLoad;
    }

    public void setUnitLoad(double unitLoad) {
        this.unitLoad = unitLoad;
    }
}
