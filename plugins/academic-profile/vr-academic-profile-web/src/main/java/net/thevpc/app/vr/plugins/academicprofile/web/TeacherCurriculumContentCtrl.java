/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vr.plugins.academicprofile.web;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.Action;
import net.thevpc.upa.UPA;

import java.util.Arrays;
import java.util.List;

import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "CV Teacher",
        url = "public/academic/addressbook/teacher-cv-content.xhtml"
)
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
        return Vr.get().gotoPage("teacherCurriculumContent", "{teacherId:" + cfg.teacherId + ",contentType:'" + articles.get(i) + "'}");
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
        return Vr.get().gotoPage("teacherCurriculumContent", "{teacherId:" + cfg.teacherId + ",contentType:'" + articles.get(i) + "'}");
    }

    @VrOnPageLoad
    public void onLoad(Config config) {
        final int teacherId = config.teacherId;
        final AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        final AcademicProfilePlugin apr = VrApp.getBean(AcademicProfilePlugin.class);
        final AcademicTeacher t = ap.findTeacher(teacherId);
        if (t != null) {
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    apr.updateViewsCounterForTeacherCV(teacherId);
                    return null;
                }

            }, null);
        }
        super.onLoad(config);
    }

}
