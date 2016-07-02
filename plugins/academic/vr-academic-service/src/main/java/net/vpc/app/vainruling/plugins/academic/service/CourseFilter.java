package net.vpc.app.vainruling.plugins.academic.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vpc on 6/30/16.
 */
public class CourseFilter {
    private boolean includeIntents;
    private Set<Integer> programTypes;
    private Set<String> labels;

    public boolean isIncludeIntents() {
        return includeIntents;
    }

    public CourseFilter setIncludeIntents(boolean includeIntents) {
        this.includeIntents = includeIntents;
        return this;
    }

    public Set<Integer> getProgramTypes() {
        return programTypes;
    }

    public CourseFilter setProgramTypes(Set<Integer> programTypes) {
        this.programTypes = programTypes;
        return this;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public CourseFilter setLabels(Set<String> labels) {
        this.labels = labels;
        return this;
    }

    public CourseFilter copy(){
        return new CourseFilter()
                .setIncludeIntents(includeIntents)
                .setLabels(labels == null ? null : new HashSet<String>(labels))
                .setProgramTypes(programTypes == null ? null : new HashSet<Integer>(programTypes))
                ;
    }
}
