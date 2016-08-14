/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.internship.service.AcademicInternshipPlugin;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

import java.util.ArrayList;
import java.util.List;

/**
 * internships for teachers
 *
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Tous les Stages",
        menu = "/Education/Internship",
        securityKey = "Custom.Education.AllInternships",
        url = "modules/academic/internship/all-internships"
)
public class AllInternshipBoardsCtrl extends MyInternshipBoardsCtrl {

    @OnPageLoad
    public void onPageLoad() {
        super.onPageLoad();
    }

    @Override
    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByTeacherAndBoard(int teacherId, int boardId) {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        AcademicInternshipPlugin pi = VrApp.getBean(AcademicInternshipPlugin.class);
        AcademicTeacher t = p.findTeacher(teacherId);
        if (t != null && t.getDepartment() != null) {
            return pi.findEnabledInternshipBoardsByDepartment(t.getDepartment().getId());
        }
        return new ArrayList<>();
    }

    @Override
    public AcademicInternshipExtList findActualInternshipsByTeacherAndBoard(int teacherId, int boardId, int internshipTypeId) {
        AcademicInternshipPlugin pi = VrApp.getBean(AcademicInternshipPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        return pi.findInternshipsByTeacherExt(-1, boardId,
                (t != null && t.getDepartment() != null) ? t.getDepartment().getId() : -1,
                internshipTypeId, true);
    }

    public void addToMine(AcademicInternshipInfo ii) {
        AcademicTeacher c = getCurrentTeacher();
        if (c != null) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            p.addSupervisorIntent(ii.getInternship().getId(), c.getId());
            rewrap(ii);
        }
    }

    public void removeFromMine(AcademicInternshipInfo ii) {
        AcademicTeacher c = getCurrentTeacher();
        if (c != null) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            p.removeSupervisorIntent(ii.getInternship().getId(), c.getId());
            rewrap(ii);
        }
    }

}
