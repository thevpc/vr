package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.filters.ObjectFilter;

import java.util.HashSet;
import java.util.List;

public class AcademicTeacherProfileFilter implements ObjectFilter<AcademicTeacher> {
    private HashSet<Integer> goodUsers = null;

    public AcademicTeacherProfileFilter(String studentProfileFilter) {
        if (!StringUtils.isEmpty(studentProfileFilter)) {
            goodUsers = new HashSet<Integer>();
            CorePlugin core = CorePlugin.get();
            AppUserType studentType = core.findUserType("Student");
            List<AppUser> users = VrApp.getBean(CorePlugin.class).findUsersByProfileFilter(studentProfileFilter, studentType.getId());
            for (AppUser user : users) {
                goodUsers.add(user.getId());
            }
        } else {
            goodUsers = null;
        }
    }

    @Override
    public boolean accept(AcademicTeacher value) {
        if(goodUsers==null){
            return true;
        }
        AppUser u = value.getUser();
        if (u != null && goodUsers.contains(u.getId())) {
            return true;
        }
        return false;
    }
}
