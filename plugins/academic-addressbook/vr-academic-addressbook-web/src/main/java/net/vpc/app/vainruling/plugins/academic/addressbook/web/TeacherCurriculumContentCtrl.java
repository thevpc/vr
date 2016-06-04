/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.addressbook.web;

import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import net.vpc.app.vainruling.plugins.academic.addressbook.service.AcademicAddressBookPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "CV Teacher",
        url = "public/academic/addressbook/teacherCvContent.xhtml"
)
@ManagedBean
public class TeacherCurriculumContentCtrl extends TeacherCurriculumCtrl {

    private static List<String> articles = Arrays.asList("about", "teaching", "research", "education", "projects", "extra");

    public String gotoNext() {
        Config cfg = getModel().getConfig();
        String o = cfg.contentType;
        int i = articles.indexOf(o);
        if (i < 0) {
            i = 0;
        } else {
            i = (i + 1) % articles.size();
        }
        return VrApp.getBean(VrMenuManager.class).gotoPage("teacherCurriculumContent", "{teacherId:" + cfg.teacherId + ",contentType:'" + articles.get(i) + "'}");
    }

    public String gotoPrevious() {
        Config cfg = getModel().getConfig();
        String o = cfg.contentType;
        int i = articles.indexOf(o);
        if (i < 0) {
            i = 0;
        } else {
            i = (i + 1) % articles.size();
        }
        return VrApp.getBean(VrMenuManager.class).gotoPage("teacherCurriculumContent", "{teacherId:" + cfg.teacherId + ",contentType:'" + articles.get(i) + "'}");
    }

    @OnPageLoad
    public void onLoad(Config config) {
        final int teacherId = config.teacherId;
        final AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        final AcademicAddressBookPlugin ad = VrApp.getBean(AcademicAddressBookPlugin.class);
        final AcademicTeacher t = ap.findTeacher(teacherId);
        if (t != null) {
            UPA.getContext().invokePrivileged(new Action<Object>(){

                @Override
                public Object run() {
                    ad.updateViewsCounterforTeacherCV(teacherId);
                    return null;
                }
                
            }, null);
        }
        super.onLoad(config);
    }

}
