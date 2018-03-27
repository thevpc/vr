package net.vpc.app.vainruling.core.service.util;

import java.util.Calendar;

public enum PeriodType {

    MILLISECOND(Calendar.MILLISECOND),
    SECOND(Calendar.SECOND),
    HOUR(Calendar.HOUR),
    DAY(Calendar.DAY_OF_YEAR),
    WEEK(Calendar.WEEK_OF_YEAR),
    MONTH(Calendar.MONTH),
    YEAR(Calendar.YEAR);

    private int calendarId;

    PeriodType(int calendarId) {
        this.calendarId = calendarId;
    }

    public int getCalendarId() {
        return calendarId;
    }
}
