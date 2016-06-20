package net.vpc.app.vainruling.plugins.academic.internship.service.model.planning;

import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.strings.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by vpc on 5/19/16.
 */
public class PlanningActivityTable {
    private List<PlanningActivity> activities = new ArrayList<>();
    private List<PlanningRoom> rooms = new ArrayList<>();
    private List<PlanningTime> times = new ArrayList<>();
    private List<String> examiners = new ArrayList<>();
    private List<String> chairs = new ArrayList<>();

    public List<PlanningActivity> getActivities() {
        return activities;
    }

    public PlanningActivityTable setActivities(List<PlanningActivity> activities) {
        this.activities = activities;
        return this;
    }

    public PlanningActivity getActivity(String internship) {
        for (PlanningActivity activity : activities) {
            if (activity.getInternship().equals(internship)) {
                return activity;
            }
        }
        return null;
    }

    public void addActivity(AcademicInternship academicInternship) {
        AcademicTeacher chair = academicInternship.getChairExaminer();
        AcademicTeacher examiner = academicInternship.getFirstExaminer();
        PlanningActivity activity = new PlanningActivity(new PlanningInternship(
                academicInternship.getId(),
                academicInternship.getCode(),
                academicInternship.getName(),
                academicInternship.getStudent().getContact().getFullName(),
                academicInternship.getMainDiscipline(),
                academicInternship.getSupervisor().getContact().getFullName()
        ));
        if (chair != null) {
            activity.setChair(chair.getContact().getFullName());
            activity.setFixedChair(true);
        }
        if (examiner != null) {
            activity.setExaminer(examiner.getContact().getFullName());
            activity.setFixedExaminer(true);
        }
        PlanningRoom location = null;
        PlanningTime time = null;
        if (!StringUtils.isEmpty(academicInternship.getExamLocation())) {
            activity.setFixedSpace(true);
            String examLocation = academicInternship.getExamLocation();
            for (PlanningRoom room : getRooms()) {
                if (room.getName().equals(examLocation)) {
                    location = room;
                    break;
                }
            }
        }
        if (academicInternship.getExamDate() != null) {
            activity.setFixedTime(true);
            PlanningTime t = new PlanningTime(academicInternship.getExamDate());
            for (PlanningTime room : getTimes()) {
                if (room.getName().equals(t.getName())) {
                    time = t;
                    break;
                }
            }
        }
        if (time != null || location != null) {
            activity.setSpaceTime(new PlanningSpaceTime(location, time));
        }
        getActivities().add(activity);
    }

    public void addActivity(PlanningInternship internship) {
        getActivities().add(new PlanningActivity(internship));
    }

    public PlanningRoom getRoom(String name) {
        for (PlanningRoom r : getRooms()) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    public PlanningTime getTime(String name) {
        for (PlanningTime t : getTimes()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public List<PlanningRoom> getRooms() {
        return rooms;
    }

    public PlanningActivityTable setRooms(List<PlanningRoom> rooms) {
        this.rooms = rooms;
        return this;
    }

    public List<PlanningSpaceTime> getSpaceTimes() {
        List<PlanningSpaceTime> spaceTimes = new ArrayList<>();
        TreeSet<PlanningTime> sortedTimes = new TreeSet<PlanningTime>(getTimes());
        TreeSet<PlanningRoom> sortedRooms = new TreeSet<PlanningRoom>(getRooms());
        for (PlanningTime t : sortedTimes) {
            for (PlanningRoom r : sortedRooms) {
                spaceTimes.add(new PlanningSpaceTime(r, t));
            }
        }
        return spaceTimes;
    }

    public List<PlanningTime> getTimes() {
        return times;
    }

    public PlanningActivityTable setTimes(List<PlanningTime> times) {
        this.times = times;
        return this;
    }

    public List<String> getExaminers() {
        return examiners;
    }

    public PlanningActivityTable setExaminers(List<String> examiners) {
        this.examiners = examiners;
        return this;
    }

    public List<String> getChairs() {
        return chairs;
    }

    public PlanningActivityTable setChairs(List<String> chairs) {
        this.chairs = chairs;
        return this;
    }

    public PlanningActivityTable setDefaultChairsAndExaminers() {
        HashSet<String> all = new HashSet<>();
        for (PlanningActivity a : getActivities()) {
            all.addAll(a.getInternship().getSupervisors());
        }
        setChairs(new ArrayList<String>(all));
        setExaminers(new ArrayList<String>(all));
        return this;
    }

    public void addGeneratedRooms(String roomPrefix, int count) {
        List<PlanningRoom> all = new ArrayList<>();
        for (int s = 1; s <= count; s++) {
            all.add(new PlanningRoom(roomPrefix + s, s));
        }
        getRooms().addAll(all);
    }

    public void addGeneratedRooms(String... roomNames) {
        List<PlanningRoom> all = new ArrayList<>();
        for (int s = 0; s < roomNames.length; s++) {
            all.add(new PlanningRoom(roomNames[s], s + 1));
        }
        getRooms().addAll(all);
    }

    public void addGeneratedTimes(String startDateOnly, int days, String startTimeOnly, int minutesPerSession, int sessionsPerDay) throws ParseException {
        SimpleDateFormat sdf = PlanningTime.DEFAULT_FORMAT;
        Date d = sdf.parse(startDateOnly + " " + startTimeOnly);
        addGeneratedTimes(d, days, minutesPerSession, sessionsPerDay);
    }

    public void addGeneratedTimes(String startDate, int days, int minutesPerSession, int sessionsPerDay) throws ParseException {
        SimpleDateFormat sdf = PlanningTime.DEFAULT_FORMAT;
        Date d = sdf.parse(startDate);
        addGeneratedTimes(d, days, minutesPerSession, sessionsPerDay);
    }

    public void addGeneratedTimes(Date d, int days, int minutesPerSession, int sessions) {
        List<PlanningTime> all = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        instance.setTime(d);
        int dayIndex = 0;
        int minutes = instance.get(Calendar.MINUTE);
        int hours = instance.get(Calendar.HOUR_OF_DAY);
        instance.set(Calendar.SECOND, 0);
        while (dayIndex < days) {
            if (instance.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                for (int s = 1; s <= sessions; s++) {
                    instance.set(Calendar.HOUR_OF_DAY, hours);
                    instance.set(Calendar.MINUTE, minutes);
                    instance.add(Calendar.MINUTE, (s - 1) * minutesPerSession);
                    all.add(new PlanningTime(instance.getTime()));
                }
                dayIndex++;
            }
            instance.add(Calendar.DAY_OF_YEAR, 1);
        }
        getTimes().addAll(all);
    }

    public PlanningActivityTable copy() {
        PlanningActivityTable copy = new PlanningActivityTable();
        for (PlanningActivity a1 : this.getActivities()) {
            copy.getActivities().add(a1.copy());
        }
        copy.setChairs(new ArrayList<String>(this.getChairs()));
        copy.setRooms(new ArrayList<PlanningRoom>(this.getRooms()));
        copy.setExaminers(new ArrayList<String>(this.getExaminers()));
        copy.setTimes(new ArrayList<PlanningTime>(this.getTimes()));
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningActivityTable)) return false;

        PlanningActivityTable that = (PlanningActivityTable) o;

        if (activities != null ? !activities.equals(that.activities) : that.activities != null) return false;
        if (rooms != null ? !rooms.equals(that.rooms) : that.rooms != null) return false;
        if (times != null ? !times.equals(that.times) : that.times != null) return false;
        if (examiners != null ? !examiners.equals(that.examiners) : that.examiners != null) return false;
        return !(chairs != null ? !chairs.equals(that.chairs) : that.chairs != null);

    }

    @Override
    public int hashCode() {
        int result = activities != null ? activities.hashCode() : 0;
        result = 31 * result + (rooms != null ? rooms.hashCode() : 0);
        result = 31 * result + (times != null ? times.hashCode() : 0);
        result = 31 * result + (examiners != null ? examiners.hashCode() : 0);
        result = 31 * result + (chairs != null ? chairs.hashCode() : 0);
        return result;
    }

    public List<String> getTeachers() {
        TreeSet<String> persons = new TreeSet<>();
        for (PlanningActivity activity : getActivities()) {
            persons.addAll(activity.getAllTeachers());
        }
        persons.addAll(getChairs());
        persons.addAll(getExaminers());
        return new ArrayList<>(persons);
    }
}
