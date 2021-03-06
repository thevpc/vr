package net.thevpc.app.vainruling.plugins.academic.service.util;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.model.AppUserType;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.filters.ObjectFilter;

import java.util.HashSet;
import java.util.List;

public class AcademicStudentProfileFilter implements ObjectFilter<AcademicStudent> {

    private HashSet<Integer> goodUsers = null;

    public AcademicStudentProfileFilter(String studentProfileFilter) {
        if (!StringUtils.isBlank(studentProfileFilter)) {
            goodUsers = new HashSet<Integer>();
            CorePlugin core = CorePlugin.get();
            AppUserType studentType = core.findUserType("Student");
            List<AppUser> users = VrApp.getBean(CorePlugin.class).findUsersByProfileFilter(studentProfileFilter, studentType.getId(), null);
            for (AppUser user : users) {
                goodUsers.add(user.getId());
            }
        } else {
            goodUsers = null;
        }
    }

    @Override
    public boolean accept(AcademicStudent value) {
        if (goodUsers == null) {
            return true;
        }
        AppUser u = value.getUser();
        if (u != null && goodUsers.contains(u.getId())) {
            return true;
        }
        return false;
    }
}
