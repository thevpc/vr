package net.vpc.app.vainruling.plugins.calendars.model;

import java.util.List;

/**
 * @author oussama
 */
public class RuntimeAppCalendarEvent extends AppCalendarEvent {

    private int runtimeId;
    private List<RuntimeAppCalendarProperty> properties;

    public int getRuntimeId() {
        return runtimeId;
    }

    public void setRuntimeId(int runtimeId) {
        this.runtimeId = runtimeId;
    }

    public List<RuntimeAppCalendarProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<RuntimeAppCalendarProperty> properties) {
        this.properties = properties;
    }

}
