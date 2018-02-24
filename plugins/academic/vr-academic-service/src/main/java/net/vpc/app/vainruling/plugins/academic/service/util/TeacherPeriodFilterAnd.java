package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 8/18/16.
 */
public class TeacherPeriodFilterAnd implements TeacherPeriodFilter {
    private final List<TeacherPeriodFilter> all=new ArrayList<>();
    public TeacherPeriodFilterAnd() {

    }
    public TeacherPeriodFilterAnd and(TeacherPeriodFilter other){
        if(other!=null){
            if(other instanceof TeacherPeriodFilterAnd){
                for (TeacherPeriodFilter o : ((TeacherPeriodFilterAnd) other).all) {
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
        for (TeacherPeriodFilter teacherFilter : all) {
            if(!teacherFilter.acceptTeacher(t)){
                return false;
            }
        }
        return true;
    }
}
