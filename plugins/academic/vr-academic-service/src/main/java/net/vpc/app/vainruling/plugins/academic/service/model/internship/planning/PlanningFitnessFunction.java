package net.vpc.app.vainruling.plugins.academic.service.model.internship.planning;

import net.vpc.common.strings.StringUtils;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

import java.util.*;

/**
 * Created by vpc on 5/20/16.
 */
public class PlanningFitnessFunction extends FitnessFunction {
    PlanningActivityTableExt table;

    public PlanningFitnessFunction(PlanningActivityTableExt activityTable) {
        table = activityTable;
    }

    public static void main(String[] args) {
        System.out.println((int) '.');
    }

    @Override
    protected double evaluate(IChromosome iChromosome) {
        boolean doLog = false;
        if (doLog) {
            StringBuilder s = new StringBuilder();
            for (Gene g : iChromosome.getGenes()) {
                if (s.length() > 0) {
                    s.append(",");
                }
                s.append(g.getAllele());
            }
            table.unmarshall(iChromosome);
            FitnessValue v = evalTableFitness(table);
//            if(!v.valid){
//                return 0;
//            }
            if (v.valid) {
                System.out.println(">>>> " + s + " : " + v.value);
            } else {
                System.out.println(">>!  " + s + " : " + v.value + " :: " + v);
            }
            return v.value;
        } else {
            table.unmarshall(iChromosome);
            FitnessValue v = evalTableFitness(table);
            return v.value;
        }
    }

    public FitnessValue evalTableFitness() {
        return evalTableFitness(table);
    }

    public FitnessValue evalTableFitness(PlanningActivityTableExt table) {
        if (table == null) {
            return FitnessValue.zero("table");
        }

//        HashSet<String> teachers = new HashSet<>();
//        teachers.addAll(table.getTable().getChairs());
//        teachers.addAll(table.getTable().getExaminers());
//        for (PlanningActivity planningActivity : table.getTable().getActivities()) {
//            teachers.addAll(planningActivity.getSupervisors());
//        }
        List<FitnessValue> all = new ArrayList<>();
        if (table.isFindSpaceTime()) {
            all.add(evalSpaceTimeFitness(table));
        }
        if (table.isFindTeachers()) {
            all.add(evalTeachersFitness(table, false));
        }

        FitnessValue r = FitnessValue.create("table", all);
//        if(r.valid){
//            r=r.mul(100);
//        }
        return r;
    }

    private double evalPlanningRoomsSparseness(PlanningActivityTableExt table) {
        TreeSet<PlanningRoom> x = new TreeSet<>();
        for (PlanningActivity a : table.getTable().getActivities()) {
            if (a.getRoom() != null) {
                x.add(a.getRoom());
            }
        }
        int all = 0;
        for (PlanningRoom t : x) {
            all += table.getRoomIndex(t);
        }
        double n = table.getTable().getRooms().size();
        return ((n * (n + 1) / 2) - all) / n;
    }

    private double evalPlanningTimeSparseness(PlanningActivityTableExt table) {
        TreeSet<PlanningTime> x = new TreeSet<>();
        for (PlanningActivity a : table.getTable().getActivities()) {
            if (a.getSpaceTime() != null) {
                x.add(a.getTime());
            }
        }
        int all = 0;
        int numberOfDays = x.first().dayDistance(x.last());
        for (PlanningTime t : x) {
            all += table.getTimeIndex(t);
        }
        double n = table.getTable().getTimes().size();
        double value = ((n * (n + 1) / 2) - all) / n + (3.0 / (numberOfDays + 1));

        TreeSet<PlanningSpaceTime> y = new TreeSet<>();
        for (PlanningActivity a : table.getTable().getActivities()) {
            if (a.getSpaceTime() != null) {
                y.add(a.getSpaceTime());
            }
        }
//        int ally=0;
//        for (PlanningSpaceTime spaceTime : y) {
//            ally+= spaceTime.getIndex();
//        }
//        double ny = table.getSpaceTimes().size();
//        value+= ((ny*(ny+1)/2)-ally)/ny ;

        return value;
    }

    private FitnessValue evalSpaceTimeFitness(PlanningActivityTableExt table) {
        Map<TeacherTime, List<PlanningActivity>> teacherConflicts = new HashMap<>();
        for (PlanningActivity a : table.getTable().getActivities()) {
            if (a.getSpaceTime() != null) {
                for (String t : a.getAllTeachers()) {
                    TeacherTime k = new TeacherTime(t, a.getTime());
                    List<PlanningActivity> conflicts = teacherConflicts.get(k);
                    if (conflicts == null) {
                        conflicts = new ArrayList<>();
                        teacherConflicts.put(k, conflicts);
                    }
                    conflicts.add(a);
                }
            } else {
                return new FitnessValue("time", false, 0, Collections.EMPTY_LIST);
            }
        }
        int othersGood = 0;
        boolean pbm = false;
        List<FitnessValue> teachers = new ArrayList<>();
        for (Iterator<Map.Entry<TeacherTime, List<PlanningActivity>>> iterator = teacherConflicts.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<TeacherTime, List<PlanningActivity>> e = iterator.next();
            if (e.getValue().size() > 1) {
                pbm = true;
//                teachers.add(FitnessValue.invalid("teacher:" + e.getKey().getTeacher(), 1.0 / e.getValue().size()));
            } else {
                iterator.remove();
                othersGood++;
            }
        }
        if (pbm) {
            teachers.add(0, FitnessValue.invalid("teachers", othersGood));
        } else {
            if (othersGood > 0) {
                teachers.add(0, FitnessValue.valid("good-teachers", othersGood));
            }
        }


        return FitnessValue.create(
                "time",
//                FitnessValue.valid("time", evalPlanningTimeSparseness(table)),
//                FitnessValue.valid("rooms", evalPlanningRoomsSparseness(table)),
                FitnessValue.create("teachers", teachers)
        );
    }

    public double distance(double val, double max) {
        double distance = Math.abs(max - val);
        if (distance > max) {
            distance = max;
        }
        return distance - distance / max;
    }

    private FitnessValue evalTeachersFitness(PlanningActivityTableExt table, boolean fixedOnly) {
        Map<String, PlanningTeacherStats> stats = evalTeacherStats(table.getTable(), fixedOnly);
        double activitiesCount = table.getTable().getActivities().size();
        double teachersCount = table.getTable().getTeachers().size();
        List<FitnessValue> fitnessValues = new ArrayList<>();
        int validActivities = 0;
        int validTeachers = 0;
        for (PlanningActivity a : table.getTable().getActivities()) {
            Set<String> persons = findDistinctTeachers(a);
            if (persons.size() != (2 + a.getInternship().getSupervisors().size())) {
                fitnessValues.add(FitnessValue.invalid("activity:" + a.getInternship(), 0.25 * (persons.size()) / activitiesCount));
            } else {
                validActivities++;
            }
        }
        for (Map.Entry<String, PlanningTeacherStats> s : stats.entrySet()) {
            String teacherName = s.getKey();
            PlanningTeacherStats teacherStat = s.getValue();
            double b1 = Math.ceil(Math.abs(teacherStat.supervisor - teacherStat.chair));
            double b2 = Math.ceil(Math.abs(teacherStat.supervisor - teacherStat.examiner));
            double weight = Math.abs(2 * teacherStat.supervisor - (teacherStat.examiner + teacherStat.chair));
            weight = 1.0 / (weight + 1) / teachersCount;
            if (b1 != 0 || b2 != 0) {
                fitnessValues.add(FitnessValue.invalid("teacher:" + teacherName, weight));
            } else {
                validTeachers++;
            }
        }
        if (validActivities > 0) {
            fitnessValues.add(0, FitnessValue.valid("valid-activities", validActivities / activitiesCount));
        }
        if (validTeachers > 0) {
            fitnessValues.add(0, FitnessValue.valid("valid-teachers", validTeachers / teachersCount));
        }
        return FitnessValue.create("teachers", fitnessValues);
    }

    private Set<String> findDistinctTeachers(PlanningActivity a) {
        HashSet<String> teachers = new HashSet<>();
        if (!StringUtils.isEmpty(a.getExaminer())) {
            teachers.add(a.getExaminer());
        }
        if (!StringUtils.isEmpty(a.getChair())) {
            teachers.add(a.getChair());
        }
        if (a.getInternship().getSupervisors() != null) {
            for (String sup : a.getInternship().getSupervisors()) {
                if (!StringUtils.isEmpty(sup)) {
                    teachers.add(sup);
                }
            }
        }
        return teachers;
    }


    public Map<String, PlanningTeacherStats> evalTeacherStats(PlanningActivityTable t, boolean fixedOnly) {
        PlanningActivityTableExt.TeacherStatsMap m = new PlanningActivityTableExt.TeacherStatsMap();
        for (PlanningActivity a : t.getActivities()) {
            HashSet<String> teachers = new HashSet<>();
            if (!StringUtils.isEmpty(a.getExaminer())) {
                if (!fixedOnly || a.isFixedExaminer()) {
                    m.get(a.getExaminer()).examiner++;
                }
                teachers.add(a.getExaminer());
            }
            if (!StringUtils.isEmpty(a.getChair())) {
                if (!fixedOnly || a.isFixedChair()) {
                    m.get(a.getChair()).chair++;
                }
                teachers.add(a.getChair());
            }
            if (a.getInternship().getSupervisors() != null) {
                for (String sup : a.getInternship().getSupervisors()) {
                    if (!StringUtils.isEmpty(sup)) {
                        m.get(sup).supervisor += 1.0 / a.getInternship().getSupervisors().size();
                        teachers.add(sup);
                    }
                }
            }
            for (String teacher : teachers) {
                PlanningTeacherStats planningTeacherStats = m.get(teacher);
                planningTeacherStats.activities++;
                planningTeacherStats.chairBalance = planningTeacherStats.chair - planningTeacherStats.supervisor;
                planningTeacherStats.examinerBalance = planningTeacherStats.examiner - planningTeacherStats.supervisor;
                if (a.getSpaceTime() != null) {
                    planningTeacherStats.times.add(a.getSpaceTime().getTime());
                }
            }
        }
        for (Map.Entry<String, PlanningTeacherStats> e : m.m.entrySet()) {
            PlanningTeacherStats s = e.getValue();
            Set<Date> days = new HashSet<>();
            for (PlanningTime time : s.times) {
                days.add(time.getDay());
            }
            s.days = days.size();
        }
        return m.m;
    }
}
