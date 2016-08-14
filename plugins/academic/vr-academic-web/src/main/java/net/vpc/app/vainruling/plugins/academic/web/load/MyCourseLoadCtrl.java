/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Ma charge",
        url = "modules/academic/my-course-load",
        menu = "/Education/Load",
        securityKey = "Custom.Education.MyCourseLoad"
)
public class MyCourseLoadCtrl extends AbstractCourseLoadCtrl {

    public MyCourseLoadCtrl() {
    }

    public AcademicTeacher getCurrentTeacher() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentTeacher();
    }

}
