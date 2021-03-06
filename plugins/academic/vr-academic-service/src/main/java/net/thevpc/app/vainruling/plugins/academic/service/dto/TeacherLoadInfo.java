/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfoByVisitor;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.service.stat.LoadValue;
import net.thevpc.app.vainruling.plugins.academic.service.stat.TeacherPeriodStat;

/**
 *
 * @author vpc
 */
public class TeacherLoadInfo {

    private Map<Integer, AcademicCourseAssignmentInfoByVisitor> all;
    private TeacherPeriodStatExt stat;
    private List<AcademicCourseAssignmentInfoByVisitor> nonFilteredOthers;
    private List<AcademicCourseAssignmentInfoByVisitor> visibleOthers;

    private List<AcademicCourseAssignmentInfoByVisitor> others;
    private LoadValue loadSum = new LoadValue();
    private double maLoad = 0;
    private int periodId = -1;
    private int maxVisibleOthers = 100;
    boolean notAllOtherDataVisible;

    public TeacherLoadInfo(int periodId) {
        this.periodId = periodId;
        this.nonFilteredOthers = (new ArrayList<AcademicCourseAssignmentInfoByVisitor>());
        this.visibleOthers = (new ArrayList<AcademicCourseAssignmentInfoByVisitor>());
        this.others = (new ArrayList<AcademicCourseAssignmentInfoByVisitor>());
        this.all = (new HashMap<Integer, AcademicCourseAssignmentInfoByVisitor>());
        TeacherPeriodStat teacherStat = new TeacherPeriodStat();
        teacherStat.setTeacher(new AcademicTeacher());
        this.stat = (new TeacherPeriodStatExt(teacherStat, this.getAll()));
    }

    public boolean isNotAllOtherDataVisible() {
        return notAllOtherDataVisible;
    }

    public void setNotAllOtherDataVisible(boolean notAllOtherDataVisible) {
        this.notAllOtherDataVisible = notAllOtherDataVisible;
    }

    public int getMaxVisibleOthers() {
        return maxVisibleOthers;
    }

    public void setMaxVisibleOthers(int maxVisibleOthers) {
        this.maxVisibleOthers = maxVisibleOthers;
    }

    public List<AcademicCourseAssignmentInfoByVisitor> getVisibleOthers() {
        return visibleOthers;
    }

    public void setVisibleOthers(List<AcademicCourseAssignmentInfoByVisitor> visibleOthers) {
        this.visibleOthers = visibleOthers;
    }

    public Map<Integer, AcademicCourseAssignmentInfoByVisitor> getAll() {
        return all;
    }

    public void setAll(Map<Integer, AcademicCourseAssignmentInfoByVisitor> all) {
        this.all = all;
    }

    public TeacherPeriodStatExt getStat() {
        return stat;
    }

    public void setStat(TeacherPeriodStatExt stat) {
        this.stat = stat;
    }

    public List<AcademicCourseAssignmentInfoByVisitor> getNonFilteredOthers() {
        return nonFilteredOthers;
    }

    public void setNonFilteredOthers(List<AcademicCourseAssignmentInfoByVisitor> nonFilteredOthers) {
        this.nonFilteredOthers = nonFilteredOthers;
    }

    public LoadValue getLoadSum() {
        return loadSum;
    }

    public double getMaLoad() {
        return maLoad;
    }

    public int getPeriodId() {
        return periodId;
    }

    public List<AcademicCourseAssignmentInfoByVisitor> getOthers() {
        return others;
    }

    public void setOthers(List<AcademicCourseAssignmentInfoByVisitor> others) {
        this.others = others;
    }

    public void setLoadSum(LoadValue loadSum) {
        this.loadSum = loadSum;
    }

    public void setMaLoad(double maLoad) {
        this.maLoad = maLoad;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

}
