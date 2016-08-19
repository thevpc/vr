package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgramType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vpc on 6/30/16.
 */
public class DefaultCourseAssignmentFilter implements CourseAssignmentFilter {
    private Set<Integer> acceptedProgramTypes = new HashSet<>();
    private Set<Integer> rejectedProgramTypes = new HashSet<>();
    private Set<String> acceptedLabels = new HashSet<>();
    private Set<String> rejectedLabels = new HashSet<>();

    public static DefaultCourseAssignmentFilter build(Set<String> refreshFilter) {
        DefaultCourseAssignmentFilter filter = new DefaultCourseAssignmentFilter();

        HashSet<String> unknownlabels = new HashSet<>();
        for (String rf : refreshFilter) {
            if (rf.startsWith("label:")) {
                String labName = rf.substring(rf.indexOf(":") + 1);
                filter.addLabelExpression(labName);
            } else if (rf.startsWith("AcademicProgramType:")) {
                filter.addAcceptedProgramType(Integer.parseInt(rf.substring(rf.indexOf(":") + 1)));
            } else {
                unknownlabels.add(rf);
            }
        }

        HashSet<String> intersection = new HashSet<>(filter.rejectedLabels);
        intersection.retainAll(filter.acceptedLabels);
        for (String s : intersection) {
            filter.acceptedLabels.remove(s);
            filter.rejectedLabels.remove(s);
        }

        //rebuild refreshFilter
        refreshFilter.clear();
        refreshFilter.addAll(unknownlabels);
        for (Integer id : filter.acceptedProgramTypes) {
            refreshFilter.add("AcademicProgramType:" + id);
        }
        for (String id : filter.acceptedLabels) {
            refreshFilter.add("label:" + id);
        }
        for (String id : filter.rejectedLabels) {
            refreshFilter.add("label:!" + id);
        }
        return filter;
    }

    public Set<Integer> getAcceptedProgramTypes() {
        return acceptedProgramTypes;
    }

    private DefaultCourseAssignmentFilter setAcceptedProgramTypes(Set<Integer> acceptedProgramTypes) {
        this.acceptedProgramTypes = acceptedProgramTypes;
        return this;
    }

    public Set<String> getAcceptedLabels() {
        return acceptedLabels;
    }

    private DefaultCourseAssignmentFilter setAcceptedLabels(Set<String> acceptedLabels) {
        this.acceptedLabels = acceptedLabels;
        return this;
    }

    public Set<Integer> getRejectedProgramTypes() {
        return rejectedProgramTypes;
    }

    public Set<String> getRejectedLabels() {
        return rejectedLabels;
    }

    public DefaultCourseAssignmentFilter copy() {
        return new DefaultCourseAssignmentFilter()
                .setAcceptedLabels(new HashSet<String>(acceptedLabels))
                .setRejectedLabels(new HashSet<String>(rejectedLabels))
                .setAcceptedProgramTypes(new HashSet<Integer>(acceptedProgramTypes))
                .setRejectedProgramTypes(new HashSet<Integer>(rejectedProgramTypes))
                ;
    }

    public boolean acceptAssignment(AcademicCourseAssignment a) {
        if (this.getAcceptedLabels().size() > 0) {
            Set<String> foundLabels = buildCoursePlanLabelsFromString(a.getCoursePlan().getLabels());
            for (String lab : this.getAcceptedLabels()) {
                if (!foundLabels.contains(lab)) {
                    return false;
                }
            }
            for (String lab : this.getRejectedLabels()) {
                if (foundLabels.contains(lab)) {
                    return false;
                }
            }
        }
        if (this.getAcceptedProgramTypes().size() > 0) {
            AcademicProgramType t = a.getCoursePlan().getCourseLevel().getAcademicClass().getProgram().getProgramType();
            if (t == null) {
                throw new RuntimeException("Null Program Type");
            }
            if (!this.getAcceptedProgramTypes().contains(t.getId())) {
                return false;
            }
        }
        if (this.getRejectedProgramTypes().size() > 0) {
            AcademicProgramType t = a.getCoursePlan().getCourseLevel().getAcademicClass().getProgram().getProgramType();
            if (t == null) {
                throw new RuntimeException("Null Program Type");
            }
            if (this.getRejectedProgramTypes().contains(t.getId())) {
                return false;
            }
        }
        return (true);
    }

    public Set<String> buildCoursePlanLabelsFromString(String string) {
        HashSet<String> labels = new HashSet<>();
        if (string != null) {
            for (String s : string.split(",|;| |:")) {
                if (s.length() > 0) {
                    labels.add(s);
                }
            }
        }
        return labels;
    }

    public DefaultCourseAssignmentFilter addAcceptedProgramType(Integer id) {
        acceptedProgramTypes.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedProgramType(Integer id) {
        acceptedProgramTypes.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addAcceptedLabel(String label) {
        acceptedLabels.add(label);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedLabel(String label) {
        acceptedLabels.remove(label);
        return this;
    }

    public DefaultCourseAssignmentFilter addRejectedLabel(String label) {
        rejectedLabels.add(label);
        return this;
    }

    public DefaultCourseAssignmentFilter removeRejectedLabel(String label) {
        rejectedLabels.remove(label);
        return this;
    }

    public DefaultCourseAssignmentFilter addLabelExpression(String labelExpression) {
        if (labelExpression.startsWith("!")) {
            rejectedLabels.add(labelExpression.substring(1));
        } else {
            acceptedLabels.add(labelExpression);
        }
        return this;
    }

    private DefaultCourseAssignmentFilter setRejectedProgramTypes(Set<Integer> rejectedProgramTypes) {
        this.rejectedProgramTypes = rejectedProgramTypes;
        return this;
    }

    private DefaultCourseAssignmentFilter setRejectedLabels(Set<String> rejectedLabels) {
        this.rejectedLabels = rejectedLabels;
        return this;
    }
}
