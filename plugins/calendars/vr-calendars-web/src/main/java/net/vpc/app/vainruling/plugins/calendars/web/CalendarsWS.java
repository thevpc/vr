package net.vpc.app.vainruling.plugins.calendars.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarWeek;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by vpc on 9/15/16.
 */
@Component
@Path("/calendars")
public class CalendarsWS {
    @Context
    private HttpServletRequest request;
    @GET
    @Transactional
    @Path("/my-calendars")
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarWeek[] findMyCalenders(){
        List<CalendarWeek> calendarWeeks = VrApp.getBean(CalendarsPlugin.class).loadCalendars("my-calendars", "");
        return calendarWeeks.toArray(new CalendarWeek[calendarWeeks.size()]);
    }
}
