package net.vpc.app.vainruling.plugins.academic.service.stat.impl.groups;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.stats.KPIGroup;
import net.vpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.vpc.app.vainruling.core.service.stats.StringArrayKPIGroup;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vpc on 8/29/16.
 */
public class ClassGroupBy implements KPIGroupBy<AcademicCourseAssignmentInfo> {
    private static StringArrayKPIGroup NON_ASSIGNED = new StringArrayKPIGroup("<<No Class>>", null);
    @Override
    public List<KPIGroup> createGroups(AcademicCourseAssignmentInfo assignment) {
        List<AcademicClass> classes=new ArrayList<>();
        AcademicClass cls = assignment.resolveAcademicClass();
        if(cls !=null){
            classes.add(cls);
        }
        List<AcademicClass> superClasses = AcademicPlugin.get().findAcademicUpHierarchyList(
                classes.toArray(new AcademicClass[classes.size()]),
                null
        );
        if(superClasses.size()==0){
            return Arrays.asList(NON_ASSIGNED);
        }else {
            ArrayList<KPIGroup> gg=new ArrayList<>();
            for (AcademicClass t : superClasses) {
                gg.add(new StringArrayKPIGroup(t.getName(),t,t.getId()));
            }
            return gg;
        }
    }

}
