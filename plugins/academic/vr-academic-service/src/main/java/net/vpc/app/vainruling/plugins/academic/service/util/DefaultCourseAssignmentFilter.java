package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgramType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 6/30/16.
 */
public class DefaultCourseAssignmentFilter implements CourseAssignmentFilter {
    private Set<Integer> acceptedDepartments = new HashSet<>();
    private Set<Integer> rejectedDepartments = new HashSet<>();
    private Set<Integer> acceptedOwnerDepartments = new HashSet<>();
    private Set<Integer> rejectedOwnerDepartments = new HashSet<>();
    private Set<Integer> acceptedProgramTypes = new HashSet<>();
    private Set<Integer> rejectedProgramTypes = new HashSet<>();
    private Set<Integer> acceptedSemesters = new HashSet<>();
    private Set<Integer> rejectedSemesters = new HashSet<>();
    private Set<Integer> acceptedCourseTypes = new HashSet<>();
    private Set<Integer> rejectedCourseTypes = new HashSet<>();
    private Set<Integer> acceptedClasses = new HashSet<>();
    private Set<Integer> rejectedClasses = new HashSet<>();
    private Set<String> acceptedLabels = new HashSet<>();
    private Set<String> rejectedLabels = new HashSet<>();
    private boolean acceptIntents = true;
    private boolean acceptAssignments = true;
    private boolean acceptNoTeacher = true;

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

    public boolean isAcceptNoTeacher() {
        return acceptNoTeacher;
    }

    public DefaultCourseAssignmentFilter setAcceptNoTeacher(boolean acceptNoTeacher) {
        this.acceptNoTeacher = acceptNoTeacher;
        return this;
    }

    public boolean isAcceptIntents() {
        return acceptIntents;
    }

    public DefaultCourseAssignmentFilter setAcceptIntents(boolean acceptIntents) {
        this.acceptIntents = acceptIntents;
        return this;
    }

    public boolean isAcceptAssignments() {
        return acceptAssignments;
    }

    public DefaultCourseAssignmentFilter setAcceptAssignments(boolean acceptAssignments) {
        this.acceptAssignments = acceptAssignments;
        return this;
    }

    @Override
    public boolean lookupIntents() {
        return isAcceptIntents();
    }

    public Set<Integer> getAcceptedProgramTypes() {
        return acceptedProgramTypes;
    }

    private DefaultCourseAssignmentFilter setAcceptedProgramTypes(Set<Integer> acceptedProgramTypes) {
        this.acceptedProgramTypes = acceptedProgramTypes;
        return this;
    }

    public Set<Integer> getAcceptedDepartments() {
        return acceptedDepartments;
    }

    public void setAcceptedDepartments(Set<Integer> acceptedDepartments) {
        this.acceptedDepartments = acceptedDepartments;
    }

    public Set<Integer> getRejectedDepartments() {
        return rejectedDepartments;
    }

    public void setRejectedDepartments(Set<Integer> rejectedDepartments) {
        this.rejectedDepartments = rejectedDepartments;
    }

    public Set<Integer> getAcceptedOwnerDepartments() {
        return acceptedOwnerDepartments;
    }

    public void setAcceptedOwnerDepartments(Set<Integer> acceptedOwnerDepartments) {
        this.acceptedOwnerDepartments = acceptedOwnerDepartments;
    }

    public Set<Integer> getRejectedOwnerDepartments() {
        return rejectedOwnerDepartments;
    }

    public void setRejectedOwnerDepartments(Set<Integer> rejectedOwnerDepartments) {
        this.rejectedOwnerDepartments = rejectedOwnerDepartments;
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

    public Set<Integer> getAcceptedSemesters() {
        return acceptedSemesters;
    }

    public DefaultCourseAssignmentFilter setAcceptedSemesters(Set<Integer> acceptedSemesters) {
        this.acceptedSemesters = acceptedSemesters;
        return this;
    }

    public Set<Integer> getRejectedSemesters() {
        return rejectedSemesters;
    }

    public DefaultCourseAssignmentFilter setRejectedSemesters(Set<Integer> rejectedSemesters) {
        this.rejectedSemesters = rejectedSemesters;
        return this;
    }

    public Set<Integer> getAcceptedCourseTypes() {
        return acceptedCourseTypes;
    }

    public DefaultCourseAssignmentFilter setAcceptedCourseTypes(Set<Integer> acceptedCourseTypes) {
        this.acceptedCourseTypes = acceptedCourseTypes;
        return this;
    }

    public Set<Integer> getRejectedCourseTypes() {
        return rejectedCourseTypes;
    }

    public DefaultCourseAssignmentFilter setRejectedCourseTypes(Set<Integer> rejectedCourseTypes) {
        this.rejectedCourseTypes = rejectedCourseTypes;
        return this;
    }

    public Set<Integer> getAcceptedClasses() {
        return acceptedClasses;
    }

    public DefaultCourseAssignmentFilter setAcceptedClasses(Set<Integer> acceptedClasses) {
        this.acceptedClasses = acceptedClasses;
        return this;
    }

    public Set<Integer> getRejectedClasses() {
        return rejectedClasses;
    }

    public DefaultCourseAssignmentFilter setRejectedClasses(Set<Integer> rejectedClasses) {
        this.rejectedClasses = rejectedClasses;
        return this;
    }

    //    public DefaultCourseAssignmentFilter copy() {
//        return new DefaultCourseAssignmentFilter()
//                .setAcceptedLabels(new HashSet<String>(acceptedLabels))
//                .setRejectedLabels(new HashSet<String>(rejectedLabels))
//                .setAcceptedProgramTypes(new HashSet<Integer>(acceptedProgramTypes))
//                .setRejectedProgramTypes(new HashSet<Integer>(rejectedProgramTypes))
//                ;
//    }


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

    public DefaultCourseAssignmentFilter addAcceptedDepartment(Integer id) {
        acceptedDepartments.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedDepartment(Integer id) {
        acceptedDepartments.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addRejectedDepartment(Integer id) {
        rejectedDepartments.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeRejectedDepartment(Integer id) {
        rejectedDepartments.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addAcceptedOwnerDepartment(Integer id) {
        acceptedOwnerDepartments.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedOwnerDepartment(Integer id) {
        acceptedOwnerDepartments.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addRejectedOwnerDepartment(Integer id) {
        rejectedOwnerDepartments.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeRejectedOwnerDepartment(Integer id) {
        rejectedOwnerDepartments.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addAcceptedSemester(Integer id) {
        if(id!=null) {
            acceptedSemesters.add(id);
        }
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedSemester(Integer id) {
        acceptedSemesters.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addAcceptedCourseType(Integer id) {
        acceptedCourseTypes.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedCourseType(Integer id) {
        acceptedCourseTypes.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addAcceptedClass(Integer id) {
        acceptedClasses.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedClass(Integer id) {
        acceptedClasses.remove(id);
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

    public boolean acceptDepartment(AppDepartment a) {
        if (this.getAcceptedDepartments().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (!this.getAcceptedDepartments().contains(a.getId())) {
                return false;
            }
        }
        if (this.getRejectedDepartments().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (this.getRejectedDepartments().contains(a.getId())) {
                return false;
            }
        }
        return true;
    }

    public boolean acceptOwnerDepartment(AppDepartment a) {
        if (this.getAcceptedOwnerDepartments().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (!this.getAcceptedOwnerDepartments().contains(a.getId())) {
                return false;
            }
        }
        if (this.getRejectedOwnerDepartments().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (this.getRejectedOwnerDepartments().contains(a.getId())) {
                return false;
            }
        }
        return true;
    }

    public boolean acceptAssignment(AcademicCourseAssignment a) {
//        if(!isAcceptIntents() && a.getTeacher()==null){
//                return false;
//        }
//        if(!isAcceptAssignments() && a.getTeacher()!=null){
//            return false;
//        }
        if(!isAcceptNoTeacher() && a.getTeacher()!=null){
            return false;
        }
        if (this.getAcceptedLabels().size() > 0 || this.getRejectedLabels().size() > 0) {
            Set<String> foundLabels = buildCoursePlanLabelsFromString(a.getCoursePlan().getLabels());
            if (this.getAcceptedLabels().size() > 0) {
                for (String lab : this.getAcceptedLabels()) {
                    if (!foundLabels.contains(lab)) {
                        return false;
                    }
                }
            }
            if (this.getRejectedLabels().size() > 0) {
                for (String lab : this.getRejectedLabels()) {
                    if (foundLabels.contains(lab)) {
                        return false;
                    }
                }
            }
        }

        AppDepartment department = a.resolveDepartment();
        if (!acceptDepartment(department)) {
            return false;
        }
        if (!acceptOwnerDepartment(a.getOwnerDepartment())) {
            return false;
        }
        if (this.getAcceptedProgramTypes().size() > 0) {
            AcademicProgramType t = a.resolveProgramType();
            if (t == null) {
                throw new RuntimeException("Null Program Type");
            }
            if (!this.getAcceptedProgramTypes().contains(t.getId())) {
                return false;
            }
        }
        if (this.getRejectedProgramTypes().size() > 0) {
            AcademicProgramType t = a.resolveProgramType();
            if (t == null) {
                throw new RuntimeException("Null Program Type");
            }
            if (this.getRejectedProgramTypes().contains(t.getId())) {
                return false;
            }
        }

        if (this.getAcceptedSemesters().size() > 0) {
            AcademicSemester t = a.resolveSemester();
            if (t == null) {
                throw new RuntimeException("Null Semester");
            }
            if (!this.getAcceptedSemesters().contains(t.getId())) {
                return false;
            }
        }
        if (this.getRejectedSemesters().size() > 0) {
            AcademicSemester t = a.resolveSemester();
            if (t == null) {
                throw new RuntimeException("Null Semester");
            }
            if (this.getRejectedSemesters().contains(t.getId())) {
                return false;
            }
        }

        if (this.getAcceptedCourseTypes().size() > 0) {
            AcademicCourseType t = a.getCourseType();
            if (t == null) {
                throw new RuntimeException("Null CourseType");
            }
            if (!this.getAcceptedCourseTypes().contains(t.getId())) {
                return false;
            }
        }
        if (this.getRejectedCourseTypes().size() > 0) {
            AcademicCourseType t = a.getCourseType();
            if (t == null) {
                throw new RuntimeException("Null CourseType");
            }
            if (this.getRejectedCourseTypes().contains(t.getId())) {
                return false;
            }
        }
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);

        Set<Integer> acceptedTree = new HashSet<>();
        acceptedTree.addAll(this.getAcceptedClasses());
        for (Integer cc : this.getAcceptedClasses()) {
            for (Integer academicClass : academicPlugin.findAcademicDownHierarchyIdList(cc, null)) {
                acceptedTree.add(academicClass);
            }
        }

        Set<Integer> rejectedTree = new HashSet<>();
        acceptedTree.addAll(this.getRejectedClasses());
        for (Integer cc : this.getRejectedClasses()) {
            for (Integer academicClass : academicPlugin.findAcademicDownHierarchyIdList(cc, null)) {
                rejectedTree.add(academicClass);
            }
        }

        if (this.getAcceptedClasses().size() > 0) {
            AcademicClass t = a.resolveAcademicClass();
            if (t == null) {
                throw new RuntimeException("Null Class");
            }
            if (!acceptedTree.contains(t.getId())) {
                return false;
            }
        }

//        boolean _assigned = a.isAssigned();
//        HashSet<String> s = new HashSet<>(c.getIntentsSet());
//        boolean _intended = s.size() > 0;
//        boolean accepted = true;
//        if (((assigned && _assigned) || (nonassigned && !_assigned))
//                && ((intended && _intended) || (nonintended && !_intended))) {
//            //ok
//        } else {
//            accepted = false;
//        }
//        if (accepted && semesterFilter.size() > 0) {
//            if (!semesterFilter.contains(c.getAssignment().getCoursePlan().getCourseLevel().getSemester().getId())) {
//                accepted = false;
//            }
//        }
//        if (accepted && classFilter.size() > 0) {
//            if (!classFilter.contains(c.getAssignment().getCoursePlan().getCourseLevel().getAcademicClass().getId())) {
//                accepted = false;
//            }
//        }
//        if (accepted && courseTypeFilter.size() > 0) {
//            if (!courseTypeFilter.contains(c.getAssignment().getCourseType().getId())) {
//                accepted = false;
//            }
//        }
//        if (accepted && conflict) {
//            //show only whith conflicts
//            if (c.getIntentsSet().isEmpty()) {
//                accepted = false;
//            } else if (c.getAssignment().getTeacher() != null) {
//                accepted = (c.getIntentsSet().size() == 1
//                        && !c.getAssignment().getTeacher().getContact().getFullName().equals(c.getIntentsSet().toArray()[0]))
//                        || c.getIntentsSet().size() > 1;
//            } else {
//                accepted = c.getIntentsSet().size() > 1;
//            }
//        }
//        if (accepted) {
//            others.add(c);
//        }

        return (true);
    }
}
