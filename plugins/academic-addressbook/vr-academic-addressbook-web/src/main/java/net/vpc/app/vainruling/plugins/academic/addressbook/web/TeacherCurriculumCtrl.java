/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.addressbook.web;

import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.api.web.util.JsfCtrl;
import net.vpc.app.vainruling.plugins.academic.addressbook.service.AcademicAddressBookPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.addressbook.service.model.AcademicTeacherCV;
import net.vpc.app.vainruling.plugins.academic.web.AcademicCtrlUtils;
import net.vpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "CV Teacher",
        url = "public/academic/addressbook/teacherCvIndex.xhtml"
)
@ManagedBean
public class TeacherCurriculumCtrl {

    private Model model = new Model();

    @OnPageLoad
    public void onLoad(Config config) {
        getModel().setConfig(config);
        onRefresh();
    }

    public String findTeacherPhoto(int id) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(id);
        String photo = t == null ? null : AcademicCtrlUtils.getTeacherAbsoluteWebPath(t.getId(), "WebSite/photo.jpg");
        return photo;
    }

    public void onRefresh() {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicAddressBookPlugin ad = VrApp.getBean(AcademicAddressBookPlugin.class);
        AcademicTeacher t = ap.findTeacher(getModel().getConfig().teacherId);
        getModel().setTeacher(t);
        String photo = t == null ? null : AcademicCtrlUtils.getTeacherAbsoluteWebPath(t.getId(), "WebSite/photo.jpg");
        getModel().setPhotoUrl(photo);
        getModel().setTeacherCV(t == null ? null : ad.findOrCreateAcademicTeacherCV(t.getId()));
        if (StringUtils.isEmpty(getModel().getTeacherCV().getExtraImage())) {
            String extraActivity = t == null ? null : AcademicCtrlUtils.getTeacherAbsoluteWebPath(t.getId(), "WebSite/extra-activity.jpg");
            if (!StringUtils.isEmpty(extraActivity)) {
                getModel().getTeacherCV().setExtraImage(extraActivity);
            }

        }
        getModel().setContentText("");
        String emptyText = "More information will soon be available here";
        JsfCtrl jsfCtrl = VrApp.getBean(JsfCtrl.class);
        getModel().setContentType(getModel().getConfig().contentType);
        if (getModel().getTeacherCV() != null && getModel().getContentType() != null) {
            if (getModel().getContentType().equals("about")) {
                getModel().setContentTitle("About me");
                getModel().setContentText(jsfCtrl.nvlstr(getModel().getTeacherCV().getAboutText(), emptyText));
            } else if (getModel().getContentType().equals("extra")) {
                getModel().setContentTitle(jsfCtrl.nvlstr(getModel().getTeacherCV().getExtraTitle(), "Extra Activities"));
                getModel().setContentText(jsfCtrl.nvlstr(getModel().getTeacherCV().getExtraText(), emptyText));
            } else if (getModel().getContentType().equals("education")) {
                getModel().setContentTitle("Education");
                getModel().setContentText(jsfCtrl.nvlstr(getModel().getTeacherCV().getEducationText(), emptyText));
            } else if (getModel().getContentType().equals("research")) {
                getModel().setContentTitle("Research Activities");
                getModel().setContentText(jsfCtrl.nvlstr(getModel().getTeacherCV().getResearchText(), emptyText));
            } else if (getModel().getContentType().equals("projects")) {
                getModel().setContentTitle("Projects and Experience");
                getModel().setContentText(jsfCtrl.nvlstr(getModel().getTeacherCV().getProjectsText(), emptyText));
            } else if (getModel().getContentType().equals("teaching")) {
                getModel().setContentTitle("Teaching Activities");
                getModel().setContentText(jsfCtrl.nvlstr(getModel().getTeacherCV().getTeachingText(), emptyText));
            }
        }
        getModel().setContentText(VrHelper.extratPureHTML(getModel().getContentText()));
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public static class Config {

        int teacherId;
        String contentType;
    }

    public static class Model {

        private AcademicTeacher teacher;
        private AcademicTeacherCV teacherCV;
        private Config config;
        private String photoUrl;
        private String contentType;
        private String contentText;
        private String contentTitle;

        public AcademicTeacher getTeacher() {
            return teacher;
        }

        public void setTeacher(AcademicTeacher teacher) {
            this.teacher = teacher;
        }

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public AcademicTeacherCV getTeacherCV() {
            return teacherCV;
        }

        public void setTeacherCV(AcademicTeacherCV teacherCV) {
            this.teacherCV = teacherCV;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentText() {
            return contentText;
        }

        public void setContentText(String contentText) {
            this.contentText = contentText;
        }

        public String getContentTitle() {
            return contentTitle;
        }

        public void setContentTitle(String contentTitle) {
            this.contentTitle = contentTitle;
        }

    }
}
