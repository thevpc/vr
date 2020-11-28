/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.web.load;

import javax.faces.bean.ManagedBean;

import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Ma charge",
        url = "modules/academic/my-course-load",
        menu = "/Education/Load",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_COURSE_LOAD
)
@ManagedBean
public class MyCourseLoadCtrl extends AbstractCourseLoadCtrl {

    public MyCourseLoadCtrl() {
        getModel().setDisplayOtherModules(false);
    }

    @Override
    public boolean isDeparmentManagedOnly() {
        return true;
    }

    @Override
    public AcademicTeacher getCurrentTeacher() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentTeacher();
    }

}
