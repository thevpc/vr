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
import net.thevpc.app.vr.plugins.academicprofile.model.AcademicTeacherCV;
import net.thevpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.web.AcademicCtrlUtils;
import net.thevpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "CV Teacher",
        url = "public/academic/addressbook/teacher-cv-index.xhtml"
)
public class TeacherCurriculumCtrl {

    private Model model = new Model();
    @Autowired
    private AcademicPlugin ap;
    private AcademicProfilePlugin apr;

    @VrOnPageLoad
    public void onLoad(Config config) {
        getModel().setConfig(config);
        onRefresh();
    }

    public String findTeacherPhoto(int id) {
        AcademicTeacher t = ap.findTeacher(id);
        String photo = t == null ? null : AcademicCtrlUtils.getTeacherAbsoluteWebPath(t.getId(), "WebSite/photo.jpg");
        return photo;
    }

    public void onRefresh() {
        AcademicTeacher t = ap.findTeacher(getModel().getConfig().teacherId);
        getModel().setTeacher(t);
        String photo = t == null ? null : AcademicCtrlUtils.getTeacherAbsoluteWebPath(t.getId(), "WebSite/photo.jpg");
        getModel().setPhotoUrl(photo);
        getModel().setTeacherCV(t == null ? null : apr.findOrCreateAcademicTeacherCV(t.getId()));
        if (StringUtils.isBlank(getModel().getTeacherCV().getExtraImage())) {
            String extraActivity = t == null ? null : AcademicCtrlUtils.getTeacherAbsoluteWebPath(t.getId(), "WebSite/extra-activity.jpg");
            if (!StringUtils.isBlank(extraActivity)) {
                getModel().getTeacherCV().setExtraImage(extraActivity);
            }

        }
        getModel().setContentText("");
        String emptyText = "More information will soon be available here";
        Vr vr = Vr.get();
        getModel().setContentType(getModel().getConfig().contentType);
        if (getModel().getTeacherCV() != null && getModel().getContentType() != null) {
            if (getModel().getContentType().equals("about")) {
                getModel().setContentTitle("About me");
                getModel().setContentText(vr.nvlstr(getModel().getTeacherCV().getAboutText(), emptyText));
            } else if (getModel().getContentType().equals("extra")) {
                getModel().setContentTitle(vr.nvlstr(getModel().getTeacherCV().getExtraTitle(), "Extra Activities"));
                getModel().setContentText(vr.nvlstr(getModel().getTeacherCV().getExtraText(), emptyText));
            } else if (getModel().getContentType().equals("education")) {
                getModel().setContentTitle("Education");
                getModel().setContentText(vr.nvlstr(getModel().getTeacherCV().getEducationText(), emptyText));
            } else if (getModel().getContentType().equals("research")) {
                getModel().setContentTitle("Research Activities");
                getModel().setContentText(vr.nvlstr(getModel().getTeacherCV().getResearchText(), emptyText));
            } else if (getModel().getContentType().equals("projects")) {
                getModel().setContentTitle("Projects and Experience");
                getModel().setContentText(vr.nvlstr(getModel().getTeacherCV().getProjectsText(), emptyText));
            } else if (getModel().getContentType().equals("teaching")) {
                getModel().setContentTitle("Teaching Activities");
                getModel().setContentText(vr.nvlstr(getModel().getTeacherCV().getTeachingText(), emptyText));
            }
        }
        getModel().setContentText((getModel().getContentText()));
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
