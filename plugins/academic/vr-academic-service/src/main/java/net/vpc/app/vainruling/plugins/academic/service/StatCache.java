/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseIntent;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.QueryHints;
import net.vpc.upa.UPA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vpc
 */
public class StatCache {


    private List<AcademicTeacher> academicTeacherList;
    private Map<Integer, AcademicTeacher> academicTeacherMap;
    private Map<Integer, AcademicSemester> academicSemesterMap;
    private Map<String, AcademicTeacherDegree> academicTeacherDegreesByCodeMap;
    private Integer semesterMaxWeeks;
    private List<AcademicSemester> academicSemesterList;
    private Map<Integer, PeriodCache> periods = new HashMap<>();

    public static <K, V> Map<K, V> toMap(List<V> entityList) {
        Map<K, V> m = new HashMap<>();
        PersistenceUnit u = UPA.getPersistenceUnit();
        Entity entity = null;
        for (V e : entityList) {
            if (entity == null) {
                entity = u.getEntity(e.getClass());
            }
            K k = (K) entity.getBuilder().objectToId(e);
            m.put(k, e);
        }
        return m;
    }

    public PeriodCache forPeriod(int periodId) {
        PeriodCache periodCache = periods.get(periodId);
        if (periodCache == null) {
            periodCache = new PeriodCache(periodId, this);
            periods.put(periodId, periodCache);
        }
        return periodCache;
    }

    public int getSemesterMaxWeeks() {
        if (semesterMaxWeeks == null) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            semesterMaxWeeks = p.getSemesterMaxWeeks();
        }
        return semesterMaxWeeks;
    }

    public Map<Integer, AcademicSemester> getAcademicSemesterMap() {
        if (academicSemesterMap == null) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            academicSemesterList = p.findSemesters();
            Map<Integer, AcademicSemester> m = toMap(academicSemesterList);
            academicSemesterMap = m;
        }

        return academicSemesterMap;
    }

    public List<AcademicSemester> getAcademicSemesterList() {
        if (academicSemesterList == null) {
            getAcademicSemesterMap();//
        }
        return academicSemesterList;
    }

    public List<AcademicTeacher> getAcademicTeacherList() {
        if (academicTeacherList == null) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            academicTeacherList = p.findTeachers();
        }
        return academicTeacherList;
    }

    public Map<Integer, AcademicTeacher> getAcademicTeacherMap() {
        if (academicTeacherMap == null) {
            academicTeacherMap = toMap(getAcademicTeacherList());
        }
        return academicTeacherMap;
    }

    public Map<String, AcademicTeacherDegree> getAcademicTeacherDegreesByCodeMap() {
        if (academicTeacherDegreesByCodeMap == null) {
            Map<String, AcademicTeacherDegree> m = new HashMap<>();
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);

            for (AcademicTeacherDegree d : p.findTeacherDegrees()) {
                m.put(d.getCode(), d);
            }
            academicTeacherDegreesByCodeMap = m;
        }
        return academicTeacherDegreesByCodeMap;
    }


    public static class PeriodCache {
        StatCache global;
        int periodId;
        private Map<Integer, List<AcademicCourseAssignment>> academicCourseAssignmentListByTeacherId;
        private List<AcademicTeacherSemestrialLoad> academicTeacherSemestrialLoadList;
        private Map<Integer, List<AcademicTeacherSemestrialLoad>> academicTeacherSemestrialLoadByTeacherIdMap;
        private Map<Integer, AcademicCourseAssignment> academicCourseAssignmentMap;
        private List<AcademicCourseIntent> academicCourseIntentList;
        private Map<Integer, List<AcademicCourseIntent>> academicCourseIntentByTeacherId;
        private Map<Integer, List<AcademicCourseIntent>> academicCourseIntentByAssignmentId;
        private List<AcademicCourseAssignment> academicCourseAssignmentList;

        public PeriodCache(int periodId, StatCache global) {
            this.global = global;
            this.periodId = periodId;
        }

        public Map<Integer, AcademicCourseAssignment> getAcademicCourseAssignmentMap() {
            if (academicCourseAssignmentMap == null) {
                AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
                Map<Integer, AcademicCourseAssignment> m = toMap(getAcademicCourseAssignmentList());
                academicCourseAssignmentMap = (m);
            }
            return academicCourseAssignmentMap;
        }


        public List<AcademicCourseAssignment> getAcademicCourseAssignmentList() {
            if (academicCourseAssignmentList == null) {
                AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
                academicCourseAssignmentList = p.findCourseAssignments(periodId);
            }
            return academicCourseAssignmentList;
        }


        public List<AcademicCourseIntent> getAcademicCourseIntentList() {
            if (academicCourseIntentList == null) {
//            academicCourseIntentList = UPA.getPersistenceUnit().createQuery("Select u from AcademicCourseIntent u")
//                    .setHint(QueryHints.NAVIGATION_DEPTH, 5)
//                    .getEntityList();
                academicCourseIntentList = UPA.getPersistenceUnit().createQuery("Select u from AcademicCourseIntent u where u.assignment.coursePlan.periodId=:periodId")
                        .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                        .setParameter("periodId", periodId)
                        .getEntityList();
            }
            return academicCourseIntentList;
        }

        public Map<Integer, List<AcademicCourseIntent>> getAcademicCourseIntentByTeacherId() {
            if (academicCourseIntentByTeacherId == null) {
                Map<Integer, List<AcademicCourseIntent>> m = new HashMap<Integer, List<AcademicCourseIntent>>();
                for (AcademicCourseIntent e : getAcademicCourseIntentList()) {
                    int t = e.getTeacher().getId();
                    List<AcademicCourseIntent> list = m.get(t);
                    if (list == null) {
                        list = new ArrayList<AcademicCourseIntent>();
                        m.put(t, list);
                    }
                    list.add(e);
                }
                academicCourseIntentByTeacherId = m;
            }
            return academicCourseIntentByTeacherId;
        }

        public Map<Integer, List<AcademicCourseIntent>> getAcademicCourseIntentByAssignmentId() {
            if (academicCourseIntentByAssignmentId == null) {
                Map<Integer, List<AcademicCourseIntent>> m = new HashMap<Integer, List<AcademicCourseIntent>>();
                for (AcademicCourseIntent e : getAcademicCourseIntentList()) {
                    int t = e.getAssignment().getId();
                    List<AcademicCourseIntent> list = m.get(t);
                    if (list == null) {
                        list = new ArrayList<AcademicCourseIntent>();
                        m.put(t, list);
                    }
                    list.add(e);
                }
                academicCourseIntentByAssignmentId = m;
            }
            return academicCourseIntentByAssignmentId;
        }

        public Map<Integer, List<AcademicCourseAssignment>> getAcademicCourseAssignmentListByTeacherId() {
            if (academicCourseAssignmentListByTeacherId == null) {
                Map<Integer, List<AcademicCourseAssignment>> m = new HashMap<>();
                for (AcademicCourseAssignment a : getAcademicCourseAssignmentList()) {
                    if (a.getTeacher() == null) {
                        //ignore
                        System.out.println("No assignment for " + a);
                    } else {
                        int t = a.getTeacher().getId();
                        List<AcademicCourseAssignment> list = m.get(t);
                        if (list == null) {
                            list = new ArrayList<AcademicCourseAssignment>();
                            m.put(t, list);
                        }
                        list.add(a);
                    }
                }
                academicCourseAssignmentListByTeacherId = m;
            }
            return academicCourseAssignmentListByTeacherId;
        }

        public List<AcademicCourseAssignment> getAcademicCourseAssignmentsByTeacherAndSemester(Integer teacher, String semester) {
            List<AcademicCourseAssignment> m = new ArrayList<>();
            if (teacher == null) {
                for (AcademicCourseAssignment value : getAcademicCourseAssignmentList()) {
                    if (semester == null || (value.getCoursePlan().getCourseLevel().getSemester() != null && value.getCoursePlan().getCourseLevel().getSemester().getName().equals(semester))) {
                        m.add(value);
                    }
                }
            } else {
                List<AcademicCourseAssignment> list = getAcademicCourseAssignmentListByTeacherId().get(teacher);
                if (list == null) {
                    AcademicTeacher tt = global.getAcademicTeacherMap().get(teacher);
                    if (tt != null) {
                        if (!tt.isEnabled()) {
                            //this is okkay!
                        } else {
                            System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                        }
                    } else {
                        System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                    }
                } else {
                    AcademicTeacher tt = global.getAcademicTeacherMap().get(teacher);
                    if (tt != null && !tt.isEnabled()) {
                        System.out.println("Found assignments for teacherId=" + teacher + " : " + tt + " but he/she seems to be not enabled!");
                    }
                    for (AcademicCourseAssignment value : list) {
                        if (semester == null || (value.getCoursePlan().getCourseLevel().getSemester() != null && value.getCoursePlan().getCourseLevel().getSemester().getName().equals(semester))) {
                            m.add(value);
                        }
                    }
                }
            }
            return m;
        }

        public List<AcademicTeacherSemestrialLoad> getAcademicTeacherSemestrialLoadList() {
            if (academicTeacherSemestrialLoadList == null) {
                AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
                academicTeacherSemestrialLoadList = p.findTeacherSemestrialLoadsByPeriod(periodId);
            }
            return academicTeacherSemestrialLoadList;
        }

        public Map<Integer, List<AcademicTeacherSemestrialLoad>> getAcademicTeacherSemestrialLoadByTeacherIdMap() {
            if (academicTeacherSemestrialLoadByTeacherIdMap == null) {
                Map<Integer, List<AcademicTeacherSemestrialLoad>> m = new HashMap<>();
                for (AcademicTeacherSemestrialLoad a : getAcademicTeacherSemestrialLoadList()) {
                    int t = a.getTeacher().getId();
                    List<AcademicTeacherSemestrialLoad> list = m.get(t);
                    if (list == null) {
                        list = new ArrayList<AcademicTeacherSemestrialLoad>();
                        m.put(t, list);
                    }
                    list.add(a);
                }
                academicTeacherSemestrialLoadByTeacherIdMap = m;
            }
            return academicTeacherSemestrialLoadByTeacherIdMap;
        }

        public List<AcademicCourseIntent> getAcademicCourseIntentByTeacherAndSemester(Integer teacher, String semester) {
            List<AcademicCourseIntent> m = new ArrayList<>();
            if (teacher == null) {
                for (AcademicCourseIntent value : getAcademicCourseIntentList()) {
                    if (semester == null || (value.getAssignment().getCoursePlan().getCourseLevel().getSemester() != null && value.getAssignment().getCoursePlan().getCourseLevel().getSemester().getName().equals(semester))) {
                        m.add(value);
                    }
                }
            } else {
                List<AcademicCourseIntent> list = getAcademicCourseIntentByTeacherId().get(teacher);
                if (list == null) {
                    //System.out.println("No intents for " + teacher + " : " + getAcademicTeacherMap().get(teacher));
                } else {
                    for (AcademicCourseIntent value : list) {
                        if (semester == null || (value.getAssignment().getCoursePlan().getCourseLevel().getSemester() != null && value.getAssignment().getCoursePlan().getCourseLevel().getSemester().getName().equals(semester))) {
                            m.add(value);
                        }
                    }
                }
            }
            return m;
        }

        public List<AcademicCourseIntent> getAcademicCourseIntentByAssignmentAndSemester(Integer assignmentId, String semester) {
            List<AcademicCourseIntent> m = new ArrayList<>();
            if (assignmentId == null) {
                for (AcademicCourseIntent value : getAcademicCourseIntentList()) {
                    if (semester == null || (value.getAssignment().getCoursePlan().getCourseLevel().getSemester() != null && value.getAssignment().getCoursePlan().getCourseLevel().getSemester().getName().equals(semester))) {
                        m.add(value);
                    }
                }
            } else {
                List<AcademicCourseIntent> list = getAcademicCourseIntentByAssignmentId().get(assignmentId);
                if (list == null) {
//                System.out.println("No intents for " + assignmentId + " : assignment=" + assignmentId);
                } else {
                    for (AcademicCourseIntent value : list) {
                        if (semester == null || (value.getAssignment().getCoursePlan().getCourseLevel().getSemester() != null
                                && value.getAssignment().getCoursePlan().getCourseLevel().getSemester().getName().equals(semester))) {
                            m.add(value);
                        }
                    }
                }
            }
            return m;
        }
    }
}
