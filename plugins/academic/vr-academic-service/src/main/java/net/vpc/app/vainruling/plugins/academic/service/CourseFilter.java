package net.vpc.app.vainruling.plugins.academic.service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 6/30/16.
 */
public class CourseFilter {
    private boolean includeIntents;
    private Set<Integer> programTypes;
    private Set<String> labels;

    public static CourseFilter build(Set<String> refreshFilter) {
        CourseFilter filter = new CourseFilter();
        HashSet<String> labels = new HashSet<>();
        filter.setLabels(labels);

        HashSet<String> newlabels = new HashSet<>();
        for (String rf : refreshFilter) {
            if (rf.equals("intents")) {
                filter.setIncludeIntents(true);
                newlabels.add(rf);
            } else if (rf.startsWith("label:")) {
                String labName = rf.substring(rf.indexOf(":") + 1);
                labels.add(labName);
                newlabels.add(rf);
                String labNamep = null;
                String labNamen = null;
                if (labName.startsWith("!")) {
                    labNamep = labName.substring(1);
                    labNamen = labName;
                } else {
                    labNamep = labName;
                    labNamen = "!" + labName;
                }
                if (labels.contains(labNamep) && labels.contains(labNamen)) {
                    labels.remove(labNamep);
                    labels.remove(labNamen);
                    newlabels.remove("label:" + labNamep);
                    newlabels.remove("label:" + labNamen);
                }
            } else if (rf.startsWith("AcademicProgramType:")) {
                Set<Integer> types = filter.getProgramTypes();
                if (types == null) {
                    types = new HashSet<>();
                    filter.setProgramTypes(types);
                }
                types.add(Integer.parseInt(rf.substring(rf.indexOf(":") + 1)));
                newlabels.add(rf);

            }
        }
        refreshFilter.clear();
        refreshFilter.addAll(newlabels);
        return filter;
    }

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

    public CourseFilter copy() {
        return new CourseFilter()
                .setIncludeIntents(includeIntents)
                .setLabels(labels == null ? null : new HashSet<String>(labels))
                .setProgramTypes(programTypes == null ? null : new HashSet<Integer>(programTypes))
                ;
    }
}
