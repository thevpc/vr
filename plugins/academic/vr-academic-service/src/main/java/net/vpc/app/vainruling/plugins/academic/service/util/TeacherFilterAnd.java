package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 8/18/16.
 */
public class TeacherFilterAnd implements TeacherFilter {
    private final List<TeacherFilter> all=new ArrayList<>();
    public TeacherFilterAnd() {

    }
    public TeacherFilterAnd and(TeacherFilter other){
        if(other!=null){
            if(other instanceof TeacherFilterAnd){
                for (TeacherFilter o : ((TeacherFilterAnd) other).all) {
                    and(o);
                }
            }else{
                all.add(other);
            }
        }
        return this;
    }

    @Override
    public boolean acceptTeacher(AcademicTeacherPeriod t) {
        if(all.size()==0){
            return true;
        }
        for (TeacherFilter teacherFilter : all) {
            if(!teacherFilter.acceptTeacher(t)){
                return false;
            }
        }
        return true;
    }
}
