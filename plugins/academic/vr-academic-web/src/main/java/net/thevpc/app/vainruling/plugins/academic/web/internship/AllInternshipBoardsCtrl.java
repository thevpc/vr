/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.web.internship;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.plugins.academic.model.internship.AcademicInternshipInfo;
import net.thevpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternshipBoard;
import net.thevpc.app.vainruling.plugins.academic.model.internship.ext.AcademicInternshipExtList;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

import java.util.ArrayList;
import java.util.List;
import net.thevpc.app.vainruling.VrPage;

/**
 * internships for teachers
 *
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Tous les Stages",
        menu = "/Education/Projects/Internships",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ALL_INTERNSHIPS,
        url = "modules/academic/internship/all-internships"
)
public class AllInternshipBoardsCtrl extends MyInternshipBoardsCtrl {

    @VrOnPageLoad
    public void onPageLoad() {
        super.onPageLoad();
    }

    @Override
    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByTeacherAndBoard(int teacherId, int boardId) {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        AcademicPlugin pi = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = p.findTeacher(teacherId);
        if (t != null && t.getUser().getDepartment() != null) {
            return pi.findEnabledInternshipBoardsByDepartment(-1,t.getUser().getDepartment().getId(),true);
        }
        return new ArrayList<>();
    }

    @Override
    public AcademicInternshipExtList findActualInternshipsByTeacherAndBoard(int teacherId, int boardId, int internshipTypeId) {
        AcademicPlugin pi = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        return pi.findInternshipsByTeacherExt(-1, (t != null && t.getUser().getDepartment() != null) ? t.getUser().getDepartment().getId() : -1, -1, internshipTypeId, boardId,
                true);
    }

    public void addToMine(AcademicInternshipInfo ii) {
        AcademicTeacher c = getCurrentTeacher();
        if (c != null) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            p.addSupervisorIntent(ii.getInternship().getId(), c.getId());
            ii.rewrap(getCurrentTeacher());
        }
    }

    public void removeFromMine(AcademicInternshipInfo ii) {
        AcademicTeacher c = getCurrentTeacher();
        if (c != null) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            p.removeSupervisorIntent(ii.getInternship().getId(), c.getId());
            ii.rewrap(getCurrentTeacher());
        }
    }

}
