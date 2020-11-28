/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.dto;

import java.util.List;
import java.util.Map;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfoByVisitor;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.service.stat.TeacherPeriodStat;
import net.thevpc.app.vainruling.plugins.academic.service.stat.TeacherSemesterStat;

//    public static class AcademicCourseAssignmentInfoByVisitor extends SelectableObject<AcademicCourseAssignmentInfoByVisitor> {

//
//        public AcademicCourseAssignmentInfoByVisitor(AcademicCourseAssignmentInfoByVisitor value) {
//            super(value, false);
//        }
//
//        public AcademicCourseAssignmentInfoByVisitor(AcademicCourseAssignmentInfo value, int visitor) {
//            this(new AcademicCourseAssignmentInfoByVisitor(value, visitor));
//        }
//    }
public class TeacherPeriodStatExt {

    private TeacherPeriodStat val;
    private TeacherSemesterStatExt[] semesters;
    private List<AcademicCourseAssignmentInfoByVisitor> assignments;

    public TeacherPeriodStatExt(TeacherPeriodStat val, Map<Integer, AcademicCourseAssignmentInfoByVisitor> all) {
        this.val = val;
        TeacherSemesterStat[] sems = val.getSemesters();
        this.semesters = new TeacherSemesterStatExt[sems.length];
        for (int i = 0; i < this.semesters.length; i++) {
            this.semesters[i] = new TeacherSemesterStatExt(sems[i], all);
        }
        assignments = AcademicPlugin.wrap(val.getAssignments(), all);
    }

    public TeacherPeriodStat getVal() {
        return val;
    }

    public TeacherSemesterStatExt[] getSemesters() {
        return semesters;
    }

    public List<AcademicCourseAssignmentInfoByVisitor> getAssignments() {
        return assignments;
    }
    
}
