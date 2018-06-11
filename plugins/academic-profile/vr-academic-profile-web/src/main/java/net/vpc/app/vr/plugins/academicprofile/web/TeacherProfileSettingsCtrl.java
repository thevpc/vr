/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.plugins.academicprofile.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.web.*;
import net.vpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.core.web.jsf.ctrl.DocumentUploadListener;
import net.vpc.app.vainruling.core.web.jsf.ctrl.DocumentsCtrl;
import net.vpc.app.vainruling.core.web.jsf.ctrl.FileUploadEventHandler;
import net.vpc.app.vainruling.core.web.jsf.ctrl.dialog.DocumentsUploadDialogCtrl;
import net.vpc.app.vainruling.core.web.themes.VrTheme;
import net.vpc.app.vainruling.core.web.themes.VrThemeFace;
import net.vpc.app.vainruling.core.web.themes.VrThemeFactory;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicCVSection;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCV;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCVItem;
import net.vpc.common.io.PathInfo;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileFilter;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@VrController(
        breadcrumb = {
            @UPathItem(title = "Paramètres", css = "fa-dashboard", ctrl = "")},
        title = "Mon profil enseignant",
        menu = "/Config",
        url = "modules/academic/profile/teacher-profile-settings",
        replacementFor = "myProfileCtrl",
        securityKey = "Custom.TeacherProfileSettings",
        priority = 2
)
public class TeacherProfileSettingsCtrl implements DocumentUploadListener, VrActionEnabler {

    private static final Logger log = Logger.getLogger(TeacherProfileSettingsCtrl.class.getName());
    @Autowired
    private AcademicProfilePlugin app;
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @Override
    public boolean isEnabled(VrActionInfo data) {
        return AcademicPlugin.get().getCurrentTeacher() != null;
    }

    @OnPageLoad
    private void onPageReload() {
        Vr vr = Vr.get();
        getModel().setContact(core.getCurrentUser().getContact());
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher currentTeacher = ap.getCurrentTeacher();
        if (currentTeacher != null) {
            getModel().setTeacher(currentTeacher);
            getModel().setTeacherCV(app.findOrCreateAcademicTeacherCV(getModel().getTeacher().getId()));
            ///a verifier
            getModel().setCourseSection(app.findAcademicCVSectionByName("Course"));
            getModel().setEducationSection(app.findAcademicCVSectionByName("Education"));
            getModel().setProjectSection(app.findAcademicCVSectionByName("Project"));
            getModel().setExperienceSection(app.findAcademicCVSectionByName("Experience"));
            getModel().setResearchSection(app.findAcademicCVSectionByName("Research"));

            getModel().setCourseList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().courseSection.getId()));
            getModel().setEducationList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().educationSection.getId()));
            getModel().setExperienceList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().experienceSection.getId()));
            getModel().setProjectList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().projectSection.getId()));
            getModel().setResearchList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().researchSection.getId()));
        } else {
            getModel().setTeacher(null);
            getModel().setTeacherCV(null);
            ///a verifier
            getModel().setCourseSection(null);
            getModel().setEducationSection(null);
            getModel().setProjectSection(null);
            getModel().setExperienceSection(null);
            getModel().setResearchSection(null);

            getModel().setCourseList(null);
            getModel().setEducationList(null);
            getModel().setExperienceList(null);
            getModel().setProjectList(null);
            getModel().setResearchList(null);
        }

        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);

        getModel().getPublicThemes().clear();
        getModel().getPublicThemes().add(FacesUtils.createSelectItem("", "<Default>", null));
        for (VrTheme vrTheme : tfactory.getThemes(VrThemeFace.PUBLIC)) {
            getModel().getPublicThemes().add(FacesUtils.createSelectItem(vrTheme.getId(), vrTheme.getName(), null));
        }
        getModel().setPublicTheme(CorePlugin.get().getCurrentUserPublicTheme());

        getModel().getPrivateThemes().clear();
        getModel().getPrivateThemes().add(FacesUtils.createSelectItem("", "<Default>", null));
        for (VrTheme vrTheme : tfactory.getThemes(VrThemeFace.PRIVATE)) {
            getModel().getPrivateThemes().add(FacesUtils.createSelectItem(vrTheme.getId(), vrTheme.getName(), null));
        }
        getModel().setPrivateTheme(CorePlugin.get().getCurrentUserPrivateTheme());

        List<SelectItem> list = null;

        list = new ArrayList<>();
        for (AppCompany x : core.findCompanies()) {
            list.add(FacesUtils.createSelectItem(String.valueOf(x.getId()), x.getName()));
        }
        getModel().setCompanyItems(list);

    }

    public void onSelectTheme() {
        final CorePlugin t = VrApp.getBean(CorePlugin.class);
        try {
            t.setCurrentUserPublicTheme(getModel().getPublicTheme());
            t.setCurrentUserPrivateTheme(getModel().getPrivateTheme());
            FacesUtils.addInfoMessage("Theme mis a jour");
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex.getMessage());
        }
    }

    public void onSaveNewItem(AcademicCVSection section) {
        RequestContext.getCurrentInstance().closeDialog(null);
        getModel().item.setTeacherCV(getModel().teacherCV);
        getModel().item.setSection(section);
        //add section !
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().item);
                    FacesUtils.addInfoMessage("Opération d'ajout réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
        onPageReload();
    }

    public void onCloseDialog() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void updateBasicInformationSection() {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.updateContactInformations(getModel().contact);
                    app.updateTeacherCVInformations(getModel().teacherCV);
                    FacesUtils.addInfoMessage("Modifications enregistrées");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }

    public void updateAboutSection() {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.updateContactInformations(getModel().contact);
                    FacesUtils.addInfoMessage("Modifications enregistrées");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }

    public void updateCvItem(AcademicTeacherCVItem item) {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.updateCvItem(item);
                    FacesUtils.addInfoMessage("Modifications enregistrées");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
        onPageReload();
    }

    public void deleteCvItem(AcademicTeacherCVItem item) {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.removeTeacherCvItem(item.getId());
                    FacesUtils.addInfoMessage("Opération de suppression réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
        onPageReload();
    }

    public void onNewExperience() {
        getModel().setItem(new AcademicTeacherCVItem());
        Map<String, Object> options = new HashMap<String, Object>();
        new DialogBuilder("/modules/academic/profile/add-teacher-experience-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
    }

    public void onNewEducation() {
        getModel().setItem(new AcademicTeacherCVItem());
        new DialogBuilder("/modules/academic/profile/add-teacher-education-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
    }

    public void onNewProject() {
        getModel().setItem(new AcademicTeacherCVItem());
        new DialogBuilder("/modules/academic/profile/add-teacher-project-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
    }

    public void onNewCourse() {
        getModel().setItem(new AcademicTeacherCVItem());
        new DialogBuilder("/modules/academic/profile/add-teacher-course-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
    }

    public void onNewResearch() {
        getModel().setItem(new AcademicTeacherCVItem());
        new DialogBuilder("/modules/academic/profile/add-teacher-research-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
    }

    public void onRequestUploadPhoto() {
        getModel().setUploadingPhoto(true);
        getModel().setUploadingCV(false);
        Vr.get().openUploadDialog(new DocumentsUploadDialogCtrl.Config()
                .setExtensions("jpg,png,jpeg")
                .setSizeLimit(1024 * 1024),
                this);
    }

    public void onRequestUploadCV() {
        getModel().setUploadingPhoto(false);
        getModel().setUploadingCV(true);
        Vr.get().openUploadDialog(new DocumentsUploadDialogCtrl.Config()
                .setExtensions("pdf")
                .setSizeLimit(10 * 1024 * 1024),
                this);
    }

    public void onUpload(FileUploadEvent event) {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    if (getModel().isUploadingCV()) {
                        AcademicTeacher c = AcademicPlugin.get().getCurrentTeacher();
                        String e = event.getFile().getFileName().toLowerCase();
                        if (e.endsWith(".pdf")) {
                            String cvFile = c.getUser().getLogin() + "-cv.pdf";
                            VFile filePath = CorePlugin.get().getUserFolder(c.getUser().getLogin()).get("/Config/" + cvFile);
                            if (filePath != null) {
                                filePath.getParentFile().mkdirs();
                                VFile[] old = filePath.getParentFile().listFiles(new VFileFilterByNameIgnoreCase(cvFile));
                                for (VFile f : old) {
                                    f.delete();
                                }
                                CorePlugin.get().uploadFile(filePath, new FileUploadEventHandler(event));
                                FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
                            }
                        } else {
                            FacesUtils.addErrorMessage(event.getFile().getFileName() + " has invalid extension");
                        }
                    } else if (getModel().isUploadingPhoto()) {
                        AcademicTeacher c = AcademicPlugin.get().getCurrentTeacher();
                        String e = PathInfo.create(event.getFile().getFileName()).getExtensionPart();
                        if (e.equals("jpeg")) {
                            e = "jpg";
                        }
                        if (e.equals("png") || e.equals("jpg")) {
                            VFile filePath = CorePlugin.get().getUserFolder(c.getUser().getLogin()).get("/Config/photo." + e);
                            if (filePath != null) {
                                filePath.getParentFile().mkdirs();
                                VFile[] old = filePath.getParentFile().listFiles(new VFileFilterPhoto());
                                for (VFile f : old) {
                                    f.delete();
                                }
                                CorePlugin.get().uploadFile(filePath, new FileUploadEventHandler(event));
                                FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
                            }
                        } else {
                            FacesUtils.addErrorMessage(event.getFile().getFileName() + " has invalid extension");
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                    FacesUtils.addErrorMessage(ex, event.getFile().getFileName() + " uploading failed.");
                    return;
                }

                FacesUtils.addErrorMessage(event.getFile().getFileName() + " uploading failed.");
            }

        });

    }

    private static class VFileFilterByNameIgnoreCase implements VFileFilter {

        private final String cvFile;

        public VFileFilterByNameIgnoreCase(String cvFile) {
            this.cvFile = cvFile;
        }

        @Override
        public boolean accept(VFile pathname) {
            if (pathname.getName().equalsIgnoreCase(cvFile)) {
                return true;
            }
            return false;
        }
    }

    private static class VFileFilterPhoto implements VFileFilter {

        public VFileFilterPhoto() {
        }

        @Override
        public boolean accept(VFile pathname) {
            if (pathname.getName().equalsIgnoreCase("photo.png")
                    || pathname.getName().equalsIgnoreCase("photo.jpg")
                    || pathname.getName().equalsIgnoreCase("photo.jpeg")) {
                return true;
            }
            return false;
        }
    }

    public static class Model {

        private AcademicTeacher teacher;
        private AppContact contact;
        private AcademicTeacherCV teacherCV;

        private boolean uploadingPhoto;
        private boolean uploadingCV;

        private String publicTheme;
        private List<SelectItem> publicThemes = new ArrayList<>();

        private String privateTheme;
        private List<SelectItem> privateThemes = new ArrayList<>();

        private List<SelectItem> companyItems = new ArrayList<>();

        private AcademicCVSection experienceSection;
        private AcademicCVSection educationSection;
        private AcademicCVSection projectSection;
        private AcademicCVSection researchSection;
        private AcademicCVSection courseSection;

        private List<AcademicTeacherCVItem> experienceList = new ArrayList<>();
        private List<AcademicTeacherCVItem> educationList = new ArrayList<>();
        private List<AcademicTeacherCVItem> projectList = new ArrayList<>();
        private List<AcademicTeacherCVItem> researchList = new ArrayList<>();
        private List<AcademicTeacherCVItem> courseList = new ArrayList<>();

        private AcademicTeacherCVItem item;

        public String getPublicTheme() {
            return publicTheme;
        }

        public void setPublicTheme(String publicTheme) {
            this.publicTheme = publicTheme;
        }

        public List<SelectItem> getPublicThemes() {
            return publicThemes;
        }

        public void setPublicThemes(List<SelectItem> publicThemes) {
            this.publicThemes = publicThemes;
        }

        public String getPrivateTheme() {
            return privateTheme;
        }

        public void setPrivateTheme(String privateTheme) {
            this.privateTheme = privateTheme;
        }

        public List<SelectItem> getPrivateThemes() {
            return privateThemes;
        }

        public void setPrivateThemes(List<SelectItem> privateThemes) {
            this.privateThemes = privateThemes;
        }

        public AcademicTeacher getTeacher() {
            return teacher;
        }

        public void setTeacher(AcademicTeacher teacher) {
            this.teacher = teacher;
        }

        public AcademicTeacherCV getTeacherCV() {
            return teacherCV;
        }

        public void setTeacherCV(AcademicTeacherCV teacherCV) {
            this.teacherCV = teacherCV;
        }

        public AppContact getContact() {
            return contact;
        }

        public void setContact(AppContact contact) {
            this.contact = contact;
        }

        public List<SelectItem> getCompanyItems() {
            return companyItems;
        }

        public void setCompanyItems(List<SelectItem> companyItems) {
            this.companyItems = companyItems;
        }

        public List<AcademicTeacherCVItem> getExperienceList() {
            return experienceList;
        }

        public void setExperienceList(List<AcademicTeacherCVItem> experienceList) {
            this.experienceList = experienceList;
        }

        public List<AcademicTeacherCVItem> getEducationList() {
            return educationList;
        }

        public void setEducationList(List<AcademicTeacherCVItem> educationList) {
            this.educationList = educationList;
        }

        public List<AcademicTeacherCVItem> getProjectList() {
            return projectList;
        }

        public void setProjectList(List<AcademicTeacherCVItem> projectList) {
            this.projectList = projectList;
        }

        public List<AcademicTeacherCVItem> getResearchList() {
            return researchList;
        }

        public void setResearchList(List<AcademicTeacherCVItem> researchList) {
            this.researchList = researchList;
        }

        public List<AcademicTeacherCVItem> getCourseList() {
            return courseList;
        }

        public void setCourseList(List<AcademicTeacherCVItem> courseList) {
            this.courseList = courseList;
        }

        public boolean isUploadingPhoto() {
            return uploadingPhoto;
        }

        public void setUploadingPhoto(boolean uploadingPhoto) {
            this.uploadingPhoto = uploadingPhoto;
        }

        public boolean isUploadingCV() {
            return uploadingCV;
        }

        public void setUploadingCV(boolean uploadingCV) {
            this.uploadingCV = uploadingCV;
        }

        public AcademicCVSection getExperienceSection() {
            return experienceSection;
        }

        public void setExperienceSection(AcademicCVSection experienceSection) {
            this.experienceSection = experienceSection;
        }

        public AcademicCVSection getEducationSection() {
            return educationSection;
        }

        public void setEducationSection(AcademicCVSection educationSection) {
            this.educationSection = educationSection;
        }

        public AcademicCVSection getProjectSection() {
            return projectSection;
        }

        public void setProjectSection(AcademicCVSection projectSection) {
            this.projectSection = projectSection;
        }

        public AcademicCVSection getResearchSection() {
            return researchSection;
        }

        public void setResearchSection(AcademicCVSection researchSection) {
            this.researchSection = researchSection;
        }

        public AcademicCVSection getCourseSection() {
            return courseSection;
        }

        public void setCourseSection(AcademicCVSection courseSection) {
            this.courseSection = courseSection;
        }

        public AcademicTeacherCVItem getItem() {
            return item;
        }

        public void setItem(AcademicTeacherCVItem item) {
            this.item = item;
        }

    }
}
