package net.vpc.app.vainruling.plugins.academic.internship.service.model.planning;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vpc on 5/19/16.
 */
public class PlanningTime implements Comparable<PlanningTime> {
    public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    //    private String day;
//    private String time;
//    private int index;
//    private int dayIndex;
//    private int timeIndex;
    private Date time;

    public PlanningTime(String date) {
        try {
            this.time = DEFAULT_FORMAT.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public PlanningTime(Date time) {
        this.time = time;
    }
    //    public int getDayIndex() {
//        return dayIndex;
//    }
//
//    public int getTimeIndex() {
//        return timeIndex;
//    }
//
//    public int getIndex() {
//        return index;
//    }
//
//    public String getDay() {
//        return day;
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public double distance(PlanningTime other) {
//        return Math.abs(this.getIndex()-other.getIndex());
//    }
//

    public String getName() {
        return DEFAULT_FORMAT.format(time);
    }

    public String getDayName() {
        return DATE_FORMAT.format(time);
    }

    public String getTimeName() {
        return TIME_FORMAT.format(time);
    }

    public Date getDay() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(time);
        instance.set(Calendar.MILLISECOND, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        return instance.getTime();
    }


    public Date getTime() {
        return time;
    }

    @Override
    public String toString() {
        return getName();
    }


    @Override
    public int compareTo(PlanningTime o) {
        return this.getTime().compareTo(o.getTime());
    }

    public int dayDistance(PlanningTime o) {
        return Days.daysBetween(new LocalDateTime(this.getTime()), new LocalDateTime(o.getTime())).getDays();
    }

    public int minutesDistance(PlanningTime o) {
        return Minutes.minutesBetween(new LocalDateTime(this.getTime()), new LocalDateTime(o.getTime())).getMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningTime)) return false;

        PlanningTime that = (PlanningTime) o;

        return !(time != null ? !time.equals(that.time) : that.time != null);

    }

    @Override
    public int hashCode() {
        return time != null ? time.hashCode() : 0;
    }
}
