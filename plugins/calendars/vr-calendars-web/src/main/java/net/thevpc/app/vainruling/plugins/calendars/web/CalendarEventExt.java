/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.calendars.web;

import org.primefaces.model.ScheduleEvent;

/**
 *
 * @author vpc
 */
public interface CalendarEventExt extends ScheduleEvent {

    String getSimpleTitle();

    String getTypeName();
}
