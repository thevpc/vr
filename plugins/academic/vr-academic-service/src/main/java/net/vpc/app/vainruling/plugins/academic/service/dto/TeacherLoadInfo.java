/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.dto;

import java.util.List;
import java.util.Map;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignmentInfoByVisitor;

/**
 *
 * @author vpc
 */
public class TeacherLoadInfo {

    private Map<Integer, AcademicCourseAssignmentInfoByVisitor> all;
    private TeacherPeriodStatExt stat;
    private List<AcademicCourseAssignmentInfoByVisitor> nonFilteredOthers;

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
    
    
}
