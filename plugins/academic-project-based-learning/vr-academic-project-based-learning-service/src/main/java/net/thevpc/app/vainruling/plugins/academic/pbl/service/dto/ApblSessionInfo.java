package net.thevpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.thevpc.app.vainruling.plugins.academic.pbl.model.ApblSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 4/20/17.
 */
public class ApblSessionInfo {
    private List<ApblTeacherInfo> teachers=new ArrayList<>();
    private ApblSession session;
    private int teamsCount;
    private double maxStudentCount;
    private double teamedStudentCount;
    private double coachedStudentCount;
    private double baseStudentCount;
    private double unitLoad;

    public double getUnitLoad() {
        return unitLoad;
    }

    public void setUnitLoad(double unitLoad) {
        this.unitLoad = unitLoad;
    }

    public List<ApblTeacherInfo> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<ApblTeacherInfo> teachers) {
        this.teachers = teachers;
    }

    public ApblSession getSession() {
        return session;
    }

    public void setSession(ApblSession session) {
        this.session = session;
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
}
