/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.internship.service.AcademicInternshipPlugin;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

/**
 * internships for teachers
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Tous les Stages",
        menu = "/Education/Internship",
        securityKey = "Custom.Education.AllInternships",
        url = "modules/academic/internship/allinternships"
)
@ManagedBean
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
                (t!=null && t.getDepartment()!=null)?t.getDepartment().getId():-1,
                internshipTypeId,true);
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
