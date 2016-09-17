package net.vpc.app.vainruling.plugins.calendars.service;


import net.vpc.app.vainruling.plugins.calendars.service.model.PlanningData;

import java.util.List;
import java.util.Set;

/**
 * Created by vpc on 9/15/16.
 */
public interface VrPlanningProvider {
    public List<PlanningData> loadUserPlannings(int userId);

    public List<PlanningData> loadCalendars(String type, String key);
}
