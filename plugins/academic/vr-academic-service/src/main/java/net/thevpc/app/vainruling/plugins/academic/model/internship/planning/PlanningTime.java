package net.thevpc.app.vainruling.plugins.academic.model.internship.planning;

import net.thevpc.common.util.MutableDate;
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
    public static final SimpleDateFormat TOSTRING_DATEONLY_FORMAT = new SimpleDateFormat("EEEE yyyy-MM-dd");
    public static final SimpleDateFormat TOSTRING_FORMAT = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    //    private String day;
//    private String time;
//    private int index;
//    private int dayIndex;
//    private int timeIndex;
    private Date time;
    private int dayIndex;
    private int hourIndex;

    public PlanningTime(PlanningTime other) {
        this.time=other.time;
        this.dayIndex=other.dayIndex;
        this.hourIndex=other.hourIndex;
    }

    public PlanningTime(Date time, int dayIndex, int hourIndex) {
        this.dayIndex = dayIndex;
        this.hourIndex = hourIndex;
        if(time==null){
            this.time = new MutableDate().setYear(2000).setMonth(1).setDayOfMonth(dayIndex).setHour(hourIndex).getDateTime();
        }
    }

    public PlanningTime(String date) {
        this.dayIndex = -1;
        this.hourIndex = -1;
        try {
            this.time = DEFAULT_FORMAT.parse(date);
        } catch (ParseException e) {
            // other format
            // day index / hour index
            // example T1.1
            String d2 = date.toUpperCase();
            if (d2.startsWith("T") && d2.indexOf(".") > 0) {
                int d = Integer.parseInt(d2.substring(1, d2.indexOf('.')).trim());
                int h = Integer.parseInt(d2.substring(d2.indexOf('.') + 1).trim());
                MutableDate md = new MutableDate();
                md.clear();
                md.addDaysOfMonth(d);
                md.addHoursOfDay(h);
                md.setYear(2018);
                this.dayIndex = d;
                this.hourIndex = h;
                this.time = md.getDateTime();
            } else {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public PlanningTime setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
        return this;
    }

    public int getHourIndex() {
        return hourIndex;
    }

    public PlanningTime setHourIndex(int hourIndex) {
        this.hourIndex = hourIndex;
        return this;
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
        return TOSTRING_FORMAT.format(time);
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


    public Date getDateOnly() {
        return new MutableDate(time).clearTime().getDateTime();
    }

    public Date getTimeOnly() {
        return new MutableDate(time).clearDate().getDateTime();
    }

    public Date getDateTime() {
        return time;
    }

    @Override
    public String toString() {
        return getName();
    }


    @Override
    public int compareTo(PlanningTime o) {
        return this.getDateTime().compareTo(o.getDateTime());
    }

    public int dayDistance(PlanningTime o) {
        return Math.abs(Days.daysBetween(new LocalDateTime(this.getDateTime()), new LocalDateTime(o.getDateTime())).getDays());
    }

    public int minutesDistance(PlanningTime o) {
        return Math.abs(Minutes.minutesBetween(new LocalDateTime(this.getDateTime()), new LocalDateTime(o.getDateTime())).getMinutes());
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
