package net.thevpc.app.vainruling.plugins.academic.service.util;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicProgramType;
import net.thevpc.app.vainruling.plugins.academic.model.current.IAcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherDegree;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseType;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicOfficialDiscipline;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherSituation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseCore;

/**
 * Created by vpc on 6/30/16.
 */
public class DefaultCourseAssignmentFilter implements CourseAssignmentFilter {

    private Set<Integer> acceptedTeacherSituations = new HashSet<>();
    private Set<Integer> rejectedTeacherSituations = new HashSet<>();
    private Set<Integer> acceptedTeachers = new HashSet<>();
    private Set<Integer> rejectedTeachers = new HashSet<>();
    private Set<Integer> acceptedTeacherDepartments = new HashSet<>();
    private Set<Integer> rejectedTeacherDepartments = new HashSet<>();
    private Set<Integer> acceptedTeacherDegrees = new HashSet<>();
    private Set<Integer> rejectedTeacherDegrees = new HashSet<>();
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
    private final AssignmentFilter<AcademicCourseCore> assignmentCourseCore = new AssignmentFilter<AcademicCourseCore>(this,
            x -> x.getCoursePlan() == null ? null : x.getCoursePlan().getCourseCore(), x -> x.getId());
    private final AssignmentFilter<AcademicOfficialDiscipline> teacherOfficialDiscipline = new AssignmentFilter<AcademicOfficialDiscipline>(this,
            x -> x.getTeacher() == null ? null : x.getTeacher().getOfficialDiscipline(), x -> x.getId());
    private final AssignmentFilter<AcademicOfficialDiscipline> courseOfficialDiscipline = new AssignmentFilter<AcademicOfficialDiscipline>(this,
            x -> x.getCoursePlan() == null ? null : x.getCoursePlan().getOfficialDiscipline(), x -> x.getId());
    private Boolean acceptWish = null;
    private Boolean acceptProposals = null;
    private boolean acceptIntents = true;
    private Boolean acceptAssignments = true;
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

    public AssignmentFilter<AcademicCourseCore> assignmentCourseCore() {
        return assignmentCourseCore;
    }

    public Set<Integer> getAcceptedTeacherSituations() {
        return acceptedTeacherSituations;
    }

    public Set<Integer> getRejectedTeacherSituations() {
        return rejectedTeacherSituations;
    }

    public Set<Integer> getAcceptedTeachers() {
        return acceptedTeachers;
    }

    public Set<Integer> getRejectedTeachers() {
        return rejectedTeachers;
    }

    public Set<Integer> getAcceptedTeacherDepartments() {
        return acceptedTeacherDepartments;
    }

    public Set<Integer> getRejectedTeacherDepartments() {
        return rejectedTeacherDepartments;
    }

    public Set<Integer> getAcceptedTeacherDegrees() {
        return acceptedTeacherDegrees;
    }

    public Set<Integer> getRejectedTeacherDegrees() {
        return rejectedTeacherDegrees;
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

    public Boolean getAcceptAssignments() {
        return acceptAssignments;
    }

    public DefaultCourseAssignmentFilter setAcceptAssignments(Boolean acceptAssignments) {
        this.acceptAssignments = acceptAssignments;
        return this;
    }

    @Override
    public boolean lookupIntents() {
        return isAcceptIntents()
                || getAcceptWish() != null
                || getAcceptProposals() != null;
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

    public DefaultCourseAssignmentFilter addAcceptedTeacherDiscipline(Integer id) {
        teacherOfficialDiscipline.addAcceptedId(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addAcceptedCourseDiscipline(Integer id) {
        courseOfficialDiscipline.addAcceptedId(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addAcceptedTeacherSituation(Integer id) {
        acceptedTeacherSituations.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedTeacherSituation(Integer id) {
        acceptedTeacherSituations.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addRejectedTeacherSituation(Integer id) {
        rejectedTeacherSituations.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeRejectedTeacherSituation(Integer id) {
        rejectedTeacherSituations.remove(id);
        return this;
    }
    ////////////////////////////////////////////

    public DefaultCourseAssignmentFilter addAcceptedTeacher(Integer id) {
        acceptedTeachers.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedTeacher(Integer id) {
        acceptedTeachers.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addRejectedTeacher(Integer id) {
        rejectedTeachers.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeRejectedTeacher(Integer id) {
        rejectedTeachers.remove(id);
        return this;
    }
    ////////////////////////////////////////////

    public DefaultCourseAssignmentFilter addAcceptedTeacherDepartment(Integer id) {
        acceptedTeacherDepartments.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedTeacherDepartment(Integer id) {
        acceptedTeacherDepartments.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addRejectedTeacherDepartment(Integer id) {
        rejectedTeacherDepartments.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeRejectedTeacherDepartment(Integer id) {
        rejectedTeacherDepartments.remove(id);
        return this;
    }
    ////////////////////////////////////////////

    public DefaultCourseAssignmentFilter addAcceptedTeacherDegree(Integer id) {
        acceptedTeacherDegrees.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeAcceptedTeacherDegree(Integer id) {
        acceptedTeacherDegrees.remove(id);
        return this;
    }

    public DefaultCourseAssignmentFilter addRejectedTeacherDegree(Integer id) {
        rejectedTeacherDegrees.add(id);
        return this;
    }

    public DefaultCourseAssignmentFilter removeRejectedTeacherDegree(Integer id) {
        rejectedTeacherDegrees.remove(id);
        return this;
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
        if (id != null) {
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

    public boolean acceptTeacherDepartment(AppDepartment a) {
        if (this.getAcceptedTeacherDepartments().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (!this.getAcceptedTeacherDepartments().contains(a.getId())) {
                return false;
            }
        }
        if (this.getRejectedTeacherDepartments().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (this.getRejectedTeacherDepartments().contains(a.getId())) {
                return false;
            }
        }
        return true;
    }

    public boolean acceptTeacherSituation(AcademicTeacherSituation a) {
        if (this.getAcceptedTeacherSituations().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (!this.getAcceptedTeacherSituations().contains(a.getId())) {
                return false;
            }
        }
        if (this.getRejectedTeacherSituations().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (this.getRejectedTeacherSituations().contains(a.getId())) {
                return false;
            }
        }
        return true;
    }

    public boolean acceptTeacher(AcademicTeacher a) {
        if (this.getAcceptedTeachers().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (!this.getAcceptedTeachers().contains(a.getId())) {
                return false;
            }
        }
        if (this.getRejectedTeachers().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (this.getRejectedTeachers().contains(a.getId())) {
                return false;
            }
        }
        return true;
    }

    public boolean acceptTeacherDegree(AcademicTeacherDegree a) {
        if (this.getAcceptedTeacherDegrees().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (!this.getAcceptedTeacherDegrees().contains(a.getId())) {
                return false;
            }
        }
        if (this.getRejectedTeacherDegrees().size() > 0) {
            if (a == null) {
                return false;
//                throw new RuntimeException("Null AppDepartment");
            }
            if (this.getRejectedTeacherDegrees().contains(a.getId())) {
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

    @Override
    public boolean acceptAssignment(IAcademicCourseAssignment a) {
//        if(!isAcceptIntents() && a.getTeacher()==null){
//                return false;
//        }
//        if(!isAcceptAssignments() && a.getTeacher()!=null){
//            return false;
//        }
        AcademicTeacher teacher = a.getTeacher();

        if (!isAcceptNoTeacher() && teacher != null) {
            return false;
        }
        AppDepartment teacherDepartment = null;
        AcademicTeacherSituation teacherSituation = null;
        AcademicTeacherDegree teacherDegree = null;
        AcademicOfficialDiscipline teacherDiscipline = null;
        if (teacher != null) {
            teacherDepartment = teacher.getUser().getDepartment();
            teacherSituation = teacher.getSituation();
            teacherDegree = teacher.getDegree();
            teacherDiscipline = teacher.getOfficialDiscipline();
        }
        if (!acceptTeacherDegree(teacherDegree)) {
            return false;
        }
        if (!acceptTeacherDegree(teacherDegree)) {
            return false;
        }
        if (!acceptTeacher(teacher)) {
            return false;
        }
        if (!acceptTeacherSituation(teacherSituation)) {
            return false;
        }
        if (!acceptTeacherDepartment(teacherDepartment)) {
            return false;
        }
        if (!acceptTeacherDepartment(teacherDepartment)) {
            return false;
        }
        if (!teacherOfficialDiscipline.accept(a)) {
            return false;
        }
        if (!courseOfficialDiscipline.accept(a)) {
            return false;
        }

        AcademicCoursePlan coursePlan = a.getCoursePlan();
        if (this.getAcceptedLabels().size() > 0 || this.getRejectedLabels().size() > 0) {
            Set<String> foundLabels = buildCoursePlanLabelsFromString(coursePlan.getLabels());
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
        //both effective department and explicitly specified owner are owner departments
        if (!acceptOwnerDepartment(a.getOwnerDepartment()) && !acceptOwnerDepartment(department)) {
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
            for (Integer academicClass : academicPlugin.findClassDownHierarchyIdList(cc, null)) {
                acceptedTree.add(academicClass);
            }
        }

        Set<Integer> rejectedTree = new HashSet<>();
        acceptedTree.addAll(this.getRejectedClasses());
        for (Integer cc : this.getRejectedClasses()) {
            for (Integer academicClass : academicPlugin.findClassDownHierarchyIdList(cc, null)) {
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
//        boolean acceptAssignOrWishOrProposal=false;
//        if(getAcceptAssignments()==null ||  getAcceptAssignments()==a.isAssigned()){
//            acceptAssignOrWishOrProposal=true;
//        }
//        if (this.getAcceptWish()==null || (a.supportsWish() && this.getAcceptWish()==a.isWish())) {
//            acceptAssignOrWishOrProposal=true;
//        }
//        if (this.getAcceptProposals()==null || (a.supportsProposals() && this.getAcceptProposals()==a.isProposal())) {
//            acceptAssignOrWishOrProposal=true;
//        }
//        if(!acceptAssignOrWishOrProposal){
//            return false;
//        }
        return (true);
    }

    public Boolean getAcceptWish() {
        return acceptWish;
    }

    public void setAcceptWish(Boolean acceptWish) {
        this.acceptWish = acceptWish;
    }

    public Boolean getAcceptProposals() {
        return acceptProposals;
    }

    public void setAcceptProposals(Boolean acceptProposals) {
        this.acceptProposals = acceptProposals;
    }

    public static class AssignmentFilter<T> extends SubFilter<IAcademicCourseAssignment, T, Integer> {

        public AssignmentFilter(DefaultCourseAssignmentFilter back, Function<IAcademicCourseAssignment, T> valInfoExtractor, Function<T, Integer> valIdExtractor) {
            super(back, valInfoExtractor, valIdExtractor);
        }
    }

    public static class SubFilter<N, T, I> {

        private final Set<I> accepted = new HashSet<>();
        private final Set<I> rejected = new HashSet<>();
        private final DefaultCourseAssignmentFilter back;
        private final Function<N, T> valInfoExtractor;
        private final Function<T, I> valIdExtractor;

        public SubFilter(DefaultCourseAssignmentFilter back, Function<N, T> valInfoExtractor, Function<T, I> valIdExtractor) {
            this.back = back;
            this.valInfoExtractor = valInfoExtractor;
            this.valIdExtractor = valIdExtractor;
        }

        public DefaultCourseAssignmentFilter build() {
            return back;
        }

        public DefaultCourseAssignmentFilter and() {
            return back;
        }

        private I toId(T t) {
            return t == null ? null : valIdExtractor.apply(t);
        }

        public I extractId(N i) {
            return toId(valInfoExtractor.apply(i));
        }

        public SubFilter<N, T, I> addAccepted(T v) {
            return addAcceptedId(toId(v));
        }

        public SubFilter<N, T, I> addRejected(T v) {
            return addRejectedId(toId(v));
        }

        public SubFilter<N, T, I> addAcceptedId(I i) {
            accepted.add(i);
            return this;
        }

        public SubFilter<N, T, I> addRejectedId(I i) {
            rejected.add(i);
            return this;
        }

        public SubFilter<N, T, I> removeAccepted(T v) {
            return removeAcceptedId(toId(v));
        }

        public SubFilter<N, T, I> removeRejected(T v) {
            return removeRejectedId(toId(v));
        }

        public SubFilter<N, T, I> removeAcceptedId(I i) {
            accepted.remove(i);
            return this;
        }

        public SubFilter<N, T, I> removeRejectedId(I i) {
            rejected.add(i);
            return this;
        }

        public boolean accept(N a) {
            return checkId(extractId(a));
        }

        private boolean checkObject(T a) {
            return checkId(toId(a));
        }

        private boolean checkId(I a) {
            if (this.accepted.size() > 0) {
                if (a == null) {
                    return false;
//                throw new RuntimeException("Null AppDepartment");
                }
                if (!this.accepted.contains(a)) {
                    return false;
                }
            }
            if (this.rejected.size() > 0) {
                if (a == null) {
                    return false;
//                throw new RuntimeException("Null AppDepartment");
                }
                if (this.rejected.contains(a)) {
                    return false;
                }
            }
            return true;
        }
    }
}
