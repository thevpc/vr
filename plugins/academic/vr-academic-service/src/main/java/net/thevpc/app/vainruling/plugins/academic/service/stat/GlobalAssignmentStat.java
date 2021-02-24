/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.stat;

import java.util.Comparator;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherDegree;
import net.thevpc.app.vainruling.plugins.academic.service.util.AcademicCourseAssignmentIdConverter;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherSituation;
import net.thevpc.common.collections.Collections2;
import net.thevpc.common.collections.KeyValueList;

import java.util.HashMap;
import java.util.Map;
import net.thevpc.common.strings.StringUtils;

/**
 * @author taha.bensalah@gmail.com
 */
public class GlobalAssignmentStat {

    public static final Comparator<GlobalAssignmentStat> NAME_COMPARATOR = new Comparator<GlobalAssignmentStat>() {

        public String getStringValue(GlobalAssignmentStat o1) {
            if (o1 == null) {
                return "";
            }
            AcademicSemester p = o1.getSemester();
            AcademicTeacherSituation s = o1.getSituation();
            AcademicTeacherDegree d = o1.getDegree();
            return StringUtils.trim(p == null ? null : p.getName())
                    + ";" + StringUtils.trim(s == null ? null : s.getName())
                    + ";" + StringUtils.trim(d == null ? null : d.getName());
        }

        @Override
        public int compare(GlobalAssignmentStat o1, GlobalAssignmentStat o2) {
            return getStringValue(o1).compareTo(getStringValue(o2));
        }
    };

    private AcademicTeacherSituation situation;
    private AcademicTeacherDegree degree;
    private AcademicSemester semester;
    private int confirmedTeacherAssignmentCount = 0;
    private LoadValue confirmedTeacherAssignment = new LoadValue();
    private LoadValue value = new LoadValue();
    private LoadValue due = new LoadValue();
    private LoadValue extra = new LoadValue();
    private LoadValue targetEquiv = new LoadValue();

    private LoadValue dueWeek = new LoadValue();
    private LoadValue valueWeek = new LoadValue();
    private LoadValue extraWeek = new LoadValue();

    private LoadValue avgValue = new LoadValue();
    private LoadValue avgValueWeek = new LoadValue();
    private LoadValue avgExtra = new LoadValue();
    private LoadValue avgExtraWeek = new LoadValue();

    private LoadValue targetAssignmentsLoad = new LoadValue();
    private LoadValue missingAssignmentsLoad = new LoadValue();
    private double weeks;
    private double maxWeeks;
    private double teachersCount;
    private Map<Integer, TeacherStat> teachers = new HashMap<Integer, TeacherStat>();
    private KeyValueList<Integer, AcademicCourseAssignment> targetAssignmentsMap = Collections2.keyValueList(AcademicCourseAssignmentIdConverter.INSTANCE);
    private KeyValueList<Integer, AcademicCourseAssignment> missingAssignmentsMap = Collections2.keyValueList(AcademicCourseAssignmentIdConverter.INSTANCE);

    public GlobalAssignmentStat() {
    }

    public KeyValueList<Integer, AcademicCourseAssignment> getTargetAssignmentsMap() {
        return targetAssignmentsMap;
    }

    public KeyValueList<Integer, AcademicCourseAssignment> getMissingAssignmentsMap() {
        return missingAssignmentsMap;
    }

    public LoadValue getTargetAssignmentsLoad() {
        return targetAssignmentsLoad;
    }

    public LoadValue getMissingAssignmentsLoad() {
        return missingAssignmentsLoad;
    }

    public LoadValue getDue() {
        return due;
    }

    public LoadValue getExtra() {
        return extra;
    }

    public LoadValue getDueWeek() {
        return dueWeek;
    }

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
    }

    public AcademicTeacherDegree getDegree() {
        return degree;
    }

    public void setDegree(AcademicTeacherDegree degree) {
        this.degree = degree;
    }

    public AcademicTeacherSituation getSituation() {
        return situation;
    }

    public void setSituation(AcademicTeacherSituation situation) {
        this.situation = situation;
    }

    public LoadValue getExtraWeek() {
        return extraWeek;
    }

//    public void setExtraWeek(LoadValue extraWeek) {
//        this.extraWeek = extraWeek;
//    }
    public LoadValue getValue() {
        return value;
    }

//    public void setValue(LoadValue value) {
//        this.value = value;
//    }
    public LoadValue getValueWeek() {
        return valueWeek;
    }

//    public void setValueWeek(LoadValue valueWeek) {
//        this.valueWeek = valueWeek;
//    }
    public double getWeeks() {
        return weeks;
    }

    public void setWeeks(double weeks) {
        this.weeks = weeks;
    }

    public double getMaxWeeks() {
        return maxWeeks;
    }

    public void setMaxWeeks(double maxWeeks) {
        this.maxWeeks = maxWeeks;
    }

    public double getTeachersCount() {
        return teachersCount;
    }

    public void setTeachersCount(double teachersCount) {
        this.teachersCount = teachersCount;
    }

    public Map<Integer, TeacherStat> getTeachers() {
        return teachers;
    }

    public void setTeachers(Map<Integer, TeacherStat> teachers) {
        this.teachers = teachers;
    }

    public LoadValue getAvgValue() {
        return avgValue;
    }

    public LoadValue getAvgExtra() {
        return avgExtra;
    }

    public LoadValue getAvgValueWeek() {
        return avgValueWeek;
    }

    public LoadValue getAvgExtraWeek() {
        return avgExtraWeek;
    }

//    public void setAvgExtraWeek(LoadValue avgExtraWeek) {
//        this.avgExtraWeek = avgExtraWeek;
//    }
    public LoadValue getTargetEquiv() {
        return targetEquiv;
    }

    public void setTargetEquiv(LoadValue targetEquiv) {
        this.targetEquiv = targetEquiv;
    }

    public void addTeacherStat(TeacherBaseStat semLoad) {
        GlobalAssignmentStat y = this;
        y.getValue().add(semLoad.getValue());
        y.getExtra().add(semLoad.getExtra());
        y.getDue().add(semLoad.getDue());

        y.getDueWeek().add(semLoad.getDueWeek());
        y.getValueWeek().add(semLoad.getValueWeek());
        y.getExtraWeek().add(semLoad.getExtraWeek());

        y.setMaxWeeks(y.getMaxWeeks() + semLoad.getMaxWeeks());
        y.setWeeks(y.getWeeks() + semLoad.getWeeks());
        if (!y.getTeachers().containsKey(semLoad.getTeacher().getId())) {
            y.getTeachers().put(semLoad.getTeacher().getId(), new TeacherStat(semLoad.getTeacher()));
            y.setTeachersCount(y.getTeachers().size());
        }
        int teachersSize = y.getTeachers().size();
        y.getAvgValue().set(semLoad.getValue().copy().div(teachersSize));
        y.getAvgExtra().set(semLoad.getExtraWeek().copy().div(teachersSize));
        y.getAvgValueWeek().set(semLoad.getValueWeek().copy().div(teachersSize));
        y.getAvgExtraWeek().set(semLoad.getExtraWeek().copy().div(teachersSize));
    }

    @Override
    public String toString() {
        return "GlobalAssignmentStat{"
                + "situation=" + situation
                + ", degree=" + degree
                + ", semester=" + semester
                + '}';
    }
}
