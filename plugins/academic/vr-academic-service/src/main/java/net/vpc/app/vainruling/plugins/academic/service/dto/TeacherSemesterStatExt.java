/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.dto;

import java.util.List;
import java.util.Map;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignmentInfoByVisitor;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherSemesterStat;

/**
 *
 * @author vpc
 */
public class TeacherSemesterStatExt {
    
    private TeacherSemesterStat val;
    private List<AcademicCourseAssignmentInfoByVisitor> assignments;

    public TeacherSemesterStatExt(TeacherSemesterStat val, Map<Integer, AcademicCourseAssignmentInfoByVisitor> all) {
        this.val = val;
        assignments = AcademicPlugin.wrap(val.getAssignments(), all);
    }

    public TeacherSemesterStat getVal() {
        return val;
    }

    public List<AcademicCourseAssignmentInfoByVisitor> getAssignments() {
        return assignments;
    }
    
}
