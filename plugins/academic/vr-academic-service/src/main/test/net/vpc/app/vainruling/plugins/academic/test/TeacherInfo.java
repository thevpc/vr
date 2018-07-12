package net.vpc.app.vainruling.plugins.academic.test;

import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningActivity;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningRoom;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningSpaceTime;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningTime;

import java.util.*;

class TeacherInfo {
    private String teacher;
    private int examinerCount;
    private int supervisorCount;
    private int chairCount;
    Set<PlanningTime> datetimes = new HashSet<>();
    Set<Date> datesOnly = new HashSet<>();
    Set<Date> timesOnly = new HashSet<>();
    Set<PlanningRoom> rooms = new HashSet<>();
    List<PlanningActivity> activities = new ArrayList<>();
    Map<String, ResourceCounter> counters = new HashMap<>();

    public TeacherInfo(String teacher) {
        this.teacher = teacher;
    }

    public void addSpaceTime(PlanningSpaceTime t) {
        datetimes.add(t.getTime());
        datesOnly.add(t.getTime().getDateOnly());
        timesOnly.add(t.getTime().getTimeOnly());
        rooms.add(t.getRoom());
    }

    public int getRoomsCount(){
        return rooms.size();
    }

    public List<PlanningActivity> getEnabledActivities() {
        List<PlanningActivity> list=new ArrayList<>();
        for (PlanningActivity activity : activities) {
            if(activity.isEnabled()){
               list.add(activity);
            }
        }
        return list;
    }
    public List<PlanningActivity> getActivities() {
        return activities;
    }

    public int getTimesCount(){
        return datetimes.size();
    }

    public int getHoursCount(){
        return timesOnly.size();
    }

    public int getDaysCount(){
        return datesOnly.size();
    }

    public long getTimeDistance(){
        PlanningTime[] planningTimes = datetimes.toArray(new PlanningTime[datetimes.size()]);
        long distance=0;
        for (int i = 0; i < planningTimes.length; i++) {
            for (int j = i+1; j < planningTimes.length; j++) {
                distance+=planningTimes[i].minutesDistance(planningTimes[j]);
            }
        }
        return distance;
    }

    public int getRoomsDistance(){
        PlanningRoom[] planningRooms = rooms.toArray(new PlanningRoom[rooms.size()]);
        int distance=0;
        for (int i = 0; i < planningRooms.length; i++) {
            for (int j = i+1; j < planningRooms.length; j++) {
                distance+=planningRooms[i].distance(planningRooms[j]);
            }
        }
        return distance;
    }

    public ResourceCounter getCounter(String type) {
        ResourceCounter a = counters.get(type);
        if (a == null) {
            a = new ResourceCounter(teacher, type);
            counters.put(type, a);
        }
        return a;
    }

    public String getTeacher() {
        return teacher;
    }

    public int getExaminerCount() {
        return examinerCount;
    }

    public TeacherInfo setExaminerCount(int examinerCount) {
        this.examinerCount = examinerCount;
        return this;
    }

    public int getSupervisorCount() {
        return supervisorCount;
    }

    public TeacherInfo setSupervisorCount(int supervisorCount) {
        this.supervisorCount = supervisorCount;
        return this;
    }

    public int getChairCount() {
        return chairCount;
    }

    public TeacherInfo setChairCount(int chairCount) {
        this.chairCount = chairCount;
        return this;
    }
}
