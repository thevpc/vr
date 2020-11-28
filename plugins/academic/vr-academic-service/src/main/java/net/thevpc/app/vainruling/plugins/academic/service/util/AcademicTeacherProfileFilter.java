package net.thevpc.app.vainruling.plugins.academic.service.util;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.model.AppUserType;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.filters.ObjectFilter;

import java.util.HashSet;
import java.util.List;

public class AcademicTeacherProfileFilter implements ObjectFilter<AcademicTeacher> {
    private HashSet<Integer> goodUsers = null;

    public AcademicTeacherProfileFilter(String studentProfileFilter) {
        if (!StringUtils.isBlank(studentProfileFilter)) {
            goodUsers = new HashSet<Integer>();
            CorePlugin core = CorePlugin.get();
            AppUserType studentType = core.findUserType("Student");
            List<AppUser> users = VrApp.getBean(CorePlugin.class).findUsersByProfileFilter(studentProfileFilter, studentType.getId(),null);
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
