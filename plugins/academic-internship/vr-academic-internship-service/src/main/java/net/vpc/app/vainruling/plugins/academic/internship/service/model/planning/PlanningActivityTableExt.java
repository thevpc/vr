package net.vpc.app.vainruling.plugins.academic.internship.service.model.planning;

import net.vpc.common.strings.StringUtils;
import org.jgap.*;

import java.util.*;

/**
 * Created by vpc on 5/20/16.
 */
public class PlanningActivityTableExt {
    private PlanningActivityTable table;
    private List<PlanningSpaceTime> spaceTimes = new ArrayList<>();
    private List<ChromosomeMarshaller> marshallers = new ArrayList<>();
    private Set<String> teachers = new HashSet<>();
    private Map<PlanningTime,Integer> timeIndexes = new HashMap<>();
    private Map<String,TeacherIndex> teacherIndexes = new HashMap<>();
    boolean findTeachers;
    boolean findSpaceTime;
    private PlanningTime startTime;
    private PlanningTime endTime;
    private Configuration conf;

    public PlanningActivityTableExt(PlanningActivityTable table) {
        this(table, true, true, null);
    }

    public PlanningActivityTableExt(PlanningActivityTable table, boolean findTeachers, boolean findSpaceTime,Configuration conf) {
        this.table = table.copy();
        this.conf = conf;
        this.findTeachers = findTeachers;
        this.findSpaceTime = findSpaceTime;
        refresh();
    }

    public void refresh(){
        startTime=null;
        TreeSet<PlanningTime> sortedTimes = new TreeSet<PlanningTime>(table.getTimes());
        TreeSet<PlanningRoom> sortedRooms = new TreeSet<PlanningRoom>(table.getRooms());
        int index = 1;
        for (PlanningTime t : sortedTimes) {
            for (PlanningRoom r : sortedRooms) {
                spaceTimes.add(new PlanningSpaceTime(r, t));
            }
        }
        for (String s : table.getChairs()) {
            if (!StringUtils.isEmpty(s)) {
                teachers.add(s);
            }
        }
        for (String s : table.getExaminers()) {
            if (!StringUtils.isEmpty(s)) {
                teachers.add(s);
            }
        }
        for (PlanningActivity a : table.getActivities()) {
            if (!StringUtils.isEmpty(a.getChair())) {
                teachers.add(a.getChair());
            }
            if (!StringUtils.isEmpty(a.getExaminer())) {
                teachers.add(a.getExaminer());
            }
            if (a.getInternship().getSupervisors() != null) {
                for (String s : a.getInternship().getSupervisors()) {
                    if (!StringUtils.isEmpty(s)) {
                        teachers.add(s);
                    }
                }
            }
        }
        marshallers.clear();
        if(conf!=null) {
            for (int i = 0; i < getTable().getActivities().size(); i++) {
                PlanningActivity activity = getTable().getActivities().get(i);
                if (isFindTeachers()) {
                    if(StringUtils.isEmpty(activity.getChair()) || !activity.isFixedChair()) {
                        marshallers.add(new ChairMarshaller(i, marshallers.size(), conf));
                    }
                    if(StringUtils.isEmpty(activity.getExaminer())|| !activity.isFixedExaminer()) {
                        marshallers.add(new ExaminerMarshaller(i, marshallers.size(), conf));
                    }
                }
                if (isFindSpaceTime()) {
                    if(activity.getSpaceTime()==null|| !activity.isFixedSpace()|| !activity.isFixedTime()) {
                        marshallers.add(new SpaceTimeMarshaller(i, marshallers.size(), conf));
                    }
                }
            }
        }
        for (String s : teachers) {
            teacherIndexes.put(s,new TeacherIndex());
        }
        for (int i = 0; i < table.getChairs().size(); i++) {
            String s=table.getChairs().get(i);
            teacherIndexes.get(s).setChair(i);
        }
        for (int i = 0; i < table.getExaminers().size(); i++) {
            String s=table.getExaminers().get(i);
            teacherIndexes.get(s).setExaminer(i);
        }
    }

    public int getChairIndex(String teacher){
        return teacherIndexes.get(teacher).chair;
    }

    public int getExaminerIndex(String teacher){
        return teacherIndexes.get(teacher).examiner;
    }

    public PlanningActivityTable getTable() {
        return table;
    }

    public List<ChromosomeMarshaller> getMarshallers() {
        return marshallers;
    }

    public List<PlanningSpaceTime> getSpaceTimes() {
        return spaceTimes;
    }

    public boolean isFindTeachers() {
        return findTeachers;
    }

    public boolean isFindSpaceTime() {
        return findSpaceTime;
    }

    Chromosome marshall(Configuration conf) throws InvalidConfigurationException {
        List<Gene> sampleGenes = new ArrayList<>();
        for (ChromosomeMarshaller marshaller : getMarshallers()) {
            sampleGenes.add(marshaller.marshall(this));
        }
        return new Chromosome(conf, sampleGenes.toArray(new Gene[sampleGenes.size()]));
    }

    void unmarshall(IChromosome iChromosome) {
        for (ChromosomeMarshaller marshaller : getMarshallers()) {
            marshaller.unmarshall(iChromosome, this);
        }
    }

    public static class TeacherStatsMap{
        Map<String,PlanningTeacherStats> m=new HashMap<>();
        public PlanningTeacherStats get(String t){
            PlanningTeacherStats st = m.get(t);
            if(st==null){
                st=new PlanningTeacherStats();
                st.teacherName=t;
                m.put(t,st);
            }
            return st;
        }
    }

    public List<PlanningActivity> getExaminerActivities(String teacher) {
        List<PlanningActivity> list=new ArrayList<>();
        for (PlanningActivity activity : getActivities()) {
            if(Objects.equals(activity.getExaminer(),teacher)){
                list.add(activity);
            }
        }
        return list;
    }

    public List<PlanningActivity> getChairActivities(String teacher) {
        List<PlanningActivity> list=new ArrayList<>();
        for (PlanningActivity activity : getActivities()) {
            if(Objects.equals(activity.getChair(),teacher)){
                list.add(activity);
            }
        }
        return list;
    }

    public List<PlanningActivity> getSupervisorActivities(String teacher) {
        List<PlanningActivity> list=new ArrayList<>();
        for (PlanningActivity activity : getActivities()) {
            for (String s : activity.getInternship().getSupervisors()) {
                if(Objects.equals(s,teacher)){
                    list.add(activity);
                }
            }
        }
        return list;
    }

    public Set<String> getTeachers() {
        return teachers;
    }

    private void updateTimeCache(){
        if(startTime==null){
            TreeSet<PlanningTime> x=new TreeSet<>(table.getTimes());
            startTime = x.first();
            endTime = x.last();
            int index=0;
            for (PlanningTime planningTime : x) {
                timeIndexes.put(planningTime,index);
                index++;
            }
        }
    }

    public int getRoomIndex(PlanningRoom time){
        return time.getIndex();
    }

    public int getTimeIndex(PlanningTime time){
        updateTimeCache();
        return timeIndexes.get(time);
    }

    public PlanningTime getStartTime(){
        updateTimeCache();
        return startTime;
    }


    public List<PlanningActivity> getActivities(){
        return getTable().getActivities();
    }

    public PlanningTime getEndTime(){
        updateTimeCache();
        return endTime;
    }

   public int getTimePeriodInDays(){
        updateTimeCache();
        return startTime.dayDistance(endTime);
   }
    public static class TeacherIndex{
        int chair=-1;
        int examiner=-1;

        public TeacherIndex() {
        }

        public int getChair() {
            return chair;
        }

        public void setChair(int chair) {
            this.chair = chair;
        }

        public int getExaminer() {
            return examiner;
        }

        public void setExaminer(int examiner) {
            this.examiner = examiner;
        }
    }
}
