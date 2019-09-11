/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.dto;

import java.util.Date;
import net.vpc.app.vainruling.plugins.academic.service.util.RowHelper;

/**
 *
 * @author vpc
 */
public class PlanningTimeSerializable {

    private Date dateTime;
    private int dayIndex;
    private int hourIndex;

    public PlanningTimeSerializable load(RowHelper values, int offset) {
        dateTime = values.getDate(0 + offset);
        dayIndex = values.getInt(1 + offset, -1);
        hourIndex = values.getInt(2 + offset, -1);
        return this;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public int getHourIndex() {
        return hourIndex;
    }
    
}
