package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.current.IAcademicCourseAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 8/18/16.
 */
public class CourseAssignmentFilterAnd implements CourseAssignmentFilter {
    private final List<CourseAssignmentFilter> all=new ArrayList<>();
    public CourseAssignmentFilterAnd() {

    }
    public CourseAssignmentFilterAnd and(CourseAssignmentFilter other){
        if(other!=null){
            if(other instanceof CourseAssignmentFilterAnd){
                for (CourseAssignmentFilter o : ((CourseAssignmentFilterAnd) other).all) {
                    and(o);
                }
            }else{
                all.add(other);
            }
        }
        return this;
    }

    @Override
    public boolean acceptAssignment(IAcademicCourseAssignment t) {
        if(all.size()==0){
            return true;
        }
        for (CourseAssignmentFilter teacherFilter : all) {
            if(!teacherFilter.acceptAssignment(t)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean lookupIntents() {
        if(all.size()==0){
            return false;
        }
        for (CourseAssignmentFilter teacherFilter : all) {
            if(!teacherFilter.lookupIntents()){
                return false;
            }
        }
        return true;
    }
}
