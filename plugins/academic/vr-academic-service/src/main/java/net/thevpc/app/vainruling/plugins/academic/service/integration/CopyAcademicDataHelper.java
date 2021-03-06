package net.thevpc.app.vainruling.plugins.academic.service.integration;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseIntent;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseGroup;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherSemestrialLoad;
import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.common.collections.Collections2;
import net.thevpc.common.collections.KeyValueList;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.common.collections.MapUtils;

/**
 * Created by vpc on 6/25/16.
 */
public class CopyAcademicDataHelper {

    public static final Logger log = Logger.getLogger(CopyAcademicDataHelper.class.getName());

    private Map<Integer, AcademicTeacherSemestrialLoad> _copyAcademicDataAcademicTeacherSemestrialLoad(int fromPeriodId, AppPeriod toPeriod, PersistenceUnit pu) {
        final AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        Map<Integer, AcademicTeacherSemestrialLoad> oldIdToNewObject = new HashMap<>();
//copy AcademicTeacherSemestrialLoad
        Function<AcademicTeacherSemestrialLoad, String> converter = (AcademicTeacherSemestrialLoad value) -> value.getTeacher().getId() + "-" + value.getSemester();
        KeyValueList<String, AcademicTeacherSemestrialLoad> fromList = Collections2.keyValueList(
                converter
        ).appendAll(p.findTeacherSemestrialLoadsByPeriod(fromPeriodId));

        KeyValueList<String, AcademicTeacherSemestrialLoad> toList = Collections2.keyValueList(
                converter
        ).appendAll(p.findTeacherSemestrialLoadsByPeriod(toPeriod.getId()));
        for (AcademicTeacherSemestrialLoad v : fromList) {
            if (!toList.containsKey(converter.apply(v))) {
                AcademicTeacherSemestrialLoad v2 = pu.copyObject(v);
                v2.setId(0);
                v2.setPeriod(toPeriod);
                pu.persist(v2);
                oldIdToNewObject.put(v.getId(), v2);
            } else {
                oldIdToNewObject.put(v.getId(), toList.getByValue(v));
            }
        }
        return oldIdToNewObject;
    }

    private Map<Integer, AcademicCoursePlan> _copyAcademicDataAcademicCoursePlan(int fromPeriodId, AppPeriod toPeriod, Map<Integer, AcademicCourseGroup> groupsByOlId, PersistenceUnit pu) {
        final AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        Map<Integer, AcademicCoursePlan> oldIdToNewObject = new HashMap<>();
        Function<AcademicCoursePlan, String> converter = AcademicCoursePlan::getFullName;
        KeyValueList<String, AcademicCoursePlan> fromList = Collections2.keyValueList(
                converter
        ).appendAll(p.findCoursePlans(fromPeriodId));

        KeyValueList<String, AcademicCoursePlan> toList = Collections2.keyValueList(
                converter
        ).appendAll(p.findCoursePlans(toPeriod.getId()));
        for (AcademicCoursePlan v : fromList) {
            if (!toList.containsKey(converter.apply(v))) {
                AcademicCoursePlan v2 = pu.copyObject(v);
                v2.setId(0);
                v2.setPeriod(toPeriod);
                v2.setCourseGroup(v2.getCourseGroup() == null ? null : groupsByOlId.get(v2.getCourseGroup().getId()));
                pu.persist(v2);
                oldIdToNewObject.put(v.getId(), v2);
            } else {
                oldIdToNewObject.put(v.getId(), toList.getByValue(v));
            }
        }
        return oldIdToNewObject;
    }

    private Map<Integer, AcademicCourseGroup> _copyAcademicDataAcademicCourseGroup(int fromPeriodId, AppPeriod toPeriod, PersistenceUnit pu) {
        final AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        Map<Integer, AcademicCourseGroup> oldIdToNewObject = new HashMap<>();
        Function<AcademicCourseGroup, String> converter = new Function<AcademicCourseGroup, String>() {
            @Override
            public String apply(AcademicCourseGroup value) {
                return value.getAcademicClass().getName() + "-" + value.getName();
            }
        };
        KeyValueList<String, AcademicCourseGroup> fromList = Collections2.keyValueList(
                converter
        ).appendAll(p.findCourseGroups(fromPeriodId));

        KeyValueList<String, AcademicCourseGroup> toList = Collections2.keyValueList(
                converter
        ).appendAll(p.findCourseGroups(toPeriod.getId()));
        for (AcademicCourseGroup v : fromList) {
            if (!toList.containsKey(converter.apply(v))) {
                AcademicCourseGroup v2 = pu.copyObject(v);
                v2.setId(0);
                v2.setPeriod(toPeriod);
                pu.persist(v2);
                oldIdToNewObject.put(v.getId(), v2);
            } else {
                oldIdToNewObject.put(v.getId(), toList.getByValue(v));
            }
        }
        return oldIdToNewObject;
    }

    //    public static void main(String[] args) {
////        System.out.println(generateDiscriminator(8,null));
////        System.out.println(generateDiscriminator(8,"g"));
////        System.out.println(generateDiscriminator(8,"auto-"));
////        System.out.println(generateDiscriminator(8,"auto-9"));
////        System.out.println(generateDiscriminator(8,"a-auto-9"));
////        System.out.println(generateDiscriminator(8,"-auto-9"));
////        System.out.println(generateDiscriminator(8,"-auto-9-"));
//        System.out.println(generateDiscriminator(8,"-auto--9"));
//    }
//    private static String generateDiscriminator(int index,String base){
//        if(StringUtils.isBlank(base)){
//            return "auto-"+(index);
//        }else if(base.startsWith("auto-")){
//            if(base.equals("auto-")){
//                return "auto-"+(index);
//            }
//            int oldIndex=Convert.toInt(base.substring("auto-".length()), IntegerParserConfig.LENIENT);
//            if(oldIndex>0){
//                return "auto-"+(index);
//            }
//            return base.trim()+"-auto-"+(index);
//        }else {
//            int x=base.indexOf("-auto-");
//            if(x<0){
//                return base.trim()+"-auto-"+(index);
//            }else {
//                int oldIndex=Convert.toInt(base.substring(x+("-auto-".length())), IntegerParserConfig.LENIENT);
//                if(oldIndex<=0) {
//                    return base.trim() + "-auto-" + (index );
//                }else{
//                    return base.substring(0,x+("-auto-".length())) + (index );
//                }
//            }
//        }
//    }
    private Map<Integer, AcademicCourseAssignment> _copyAcademicDataAcademicCourseAssignment(int fromPeriodId, AppPeriod toPeriod, Map<Integer, AcademicCoursePlan> coursePlanMap, PersistenceUnit pu) {
        final AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        CorePlugin core = CorePlugin.get();
        AppPeriod fromPeriodObj = core.findPeriod(fromPeriodId);
        Map<Integer, AcademicCourseAssignment> oldIdToNewObject = new HashMap<>();
        Function<AcademicCourseAssignment, String> converter = AcademicCourseAssignment::getFullName;
        List<AcademicCourseAssignment> courseAssignments = p.findCourseAssignments(fromPeriodId);

        if (true) {
            HashMap<String, AcademicCourseAssignment> visited = new HashMap<>();
            HashSet<String> reported = new HashSet<>();
            StringBuilder error = new StringBuilder();
            error.append("Some Course Assignments have similar names. Here are the problems").append("\n");
            for (AcademicCourseAssignment assignment : courseAssignments) {
                String item = converter.apply(assignment);
                if (visited.containsKey(item)) {
                    if (!reported.contains(item)) {
                        reported.add(item);
                        AcademicCourseAssignment a0 = visited.get(item);
                        error.append("\t " + a0.getId() + " : " + item).append("\n");
                    }
                    error.append("\t " + assignment.getId() + " : " + item).append("\n");
                } else {
                    visited.put(item, assignment);
                }
            }
            if (reported.size() > 0) {
                TraceService.get().trace("Academic.copy-academic-assignments", "error", MapUtils.map(
                        "from", fromPeriodObj.getName(),
                        "to", toPeriod.getName(),
                        "error", error.toString()), "Academic", Level.SEVERE);
                throw new IllegalArgumentException("Some Course Assignments have similar names. Check log for details");
            }
        }

        KeyValueList<String, AcademicCourseAssignment> fromList = Collections2.keyValueList(
                converter
        ).appendAll(courseAssignments);

        KeyValueList<String, AcademicCourseAssignment> toList = Collections2.keyValueList(
                converter
        ).appendAll(p.findCourseAssignments(toPeriod.getId()));
        if (fromList.size() != courseAssignments.size()) {
            HashMap<String, AcademicCourseAssignment> visited = new HashMap<>();
            HashSet<String> reported = new HashSet<>();
            StringBuilder error = new StringBuilder();
            error.append("Some Course Assignments have similar names. Here are the problems").append("\n");
            for (AcademicCourseAssignment assignment : courseAssignments) {
                String item = converter.apply(assignment);
                if (visited.containsKey(item)) {
                    if (!reported.contains(item)) {
                        reported.add(item);
                        AcademicCourseAssignment a0 = visited.get(item);
                        error.append("\t " + a0.getId() + " : " + item).append("\n");
                    }
                    error.append("\t " + assignment.getId() + " : " + item).append("\n");
                } else {
                    visited.put(item, assignment);
                }
            }
            TraceService.get().trace("Academic.copy-academic-assignments", "error", MapUtils.map(
                    "from", fromPeriodObj.getName(),
                    "to", toPeriod.getName(),
                    "error", error.toString()), "Academic", Level.SEVERE);
            throw new IllegalArgumentException("Some Course Assignments have similar names. Check log for details");
        }
        for (AcademicCourseAssignment v : fromList) {
            if (!toList.containsMappedValue(v)) {
                AcademicCourseAssignment v2 = pu.copyObject(v);
                v2.setId(0);
                if (v2.getCoursePlan() != null) {
                    v2.setCoursePlan(coursePlanMap.get(v2.getCoursePlan().getId()));
                }
                pu.persist(v2);
                oldIdToNewObject.put(v.getId(), v2);
            } else {
                oldIdToNewObject.put(v.getId(), toList.getByValue(v));
            }
        }
        TraceService.get().trace("Academic.copy-academic-assignments", "success", MapUtils.map(
                "from", fromPeriodObj.getName(),
                "to", toPeriod.getName()
        ), "Academic", Level.SEVERE);
        return oldIdToNewObject;
    }

    private Map<Integer, AcademicCourseIntent> _copyAcademicDataAcademicCourseIntent(int fromPeriodId, AppPeriod toPeriod, Map<Integer, AcademicCourseAssignment> courseAssignmentsMap, PersistenceUnit pu) {
        final AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        Map<Integer, AcademicCourseIntent> oldIdToNewObject = new HashMap<>();
        Function<AcademicCourseIntent, String> converter = (AcademicCourseIntent value) -> value.getTeacher().getId() + ":" + value.getAssignment().getFullName();
        KeyValueList<String, AcademicCourseIntent> fromList = Collections2.keyValueList(
                converter
        ).appendAll(p.findCourseIntents(fromPeriodId));

        KeyValueList<String, AcademicCourseIntent> toList = Collections2.keyValueList(
                converter
        ).appendAll(p.findCourseIntents(toPeriod.getId()));
        for (AcademicCourseIntent v : fromList) {
            if (!toList.containsMappedValue(v)) {
                AcademicCourseIntent v2 = pu.copyObject(v);
                v2.setId(0);
                if (v2.getAssignment() != null) {
                    v2.setAssignment(courseAssignmentsMap.get(v2.getAssignment().getId()));
                }
                pu.persist(v2);
                oldIdToNewObject.put(v.getId(), v2);
            } else {
                oldIdToNewObject.put(v.getId(), toList.getByValue(v));
            }
        }
        return oldIdToNewObject;
    }

    public void copyAcademicData(int fromPeriodId, int toPeriodId) {
        final AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppPeriod toPeriod = VrApp.getBean(CorePlugin.class).findPeriod(toPeriodId);

        Map<Integer, AcademicCourseGroup> academicCourseGroupMap = _copyAcademicDataAcademicCourseGroup(fromPeriodId, toPeriod, pu);
        Map<Integer, AcademicCoursePlan> academicCoursePlanMap = _copyAcademicDataAcademicCoursePlan(fromPeriodId, toPeriod, academicCourseGroupMap, pu);
        academicCourseGroupMap.clear();
        for (AcademicTeacher old : p.findTeachers()) {
            p.updateTeacherPeriod(toPeriodId, old.getId(), fromPeriodId);
        }
        _copyAcademicDataAcademicTeacherSemestrialLoad(fromPeriodId, toPeriod, pu);
        Map<Integer, AcademicCourseAssignment> oldCoursePlanAssignmentsMap = _copyAcademicDataAcademicCourseAssignment(fromPeriodId, toPeriod, academicCoursePlanMap, pu);
        academicCoursePlanMap.clear();
        _copyAcademicDataAcademicCourseIntent(fromPeriodId, toPeriod, oldCoursePlanAssignmentsMap, pu);
        oldCoursePlanAssignmentsMap.clear();
    }

}
