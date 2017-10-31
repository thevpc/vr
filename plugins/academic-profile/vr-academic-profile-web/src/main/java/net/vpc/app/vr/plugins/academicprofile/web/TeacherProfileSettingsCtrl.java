/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.plugins.academicprofile.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.fs.files.DocumentsCtrl;
import net.vpc.app.vainruling.core.web.themes.VrTheme;
import net.vpc.app.vainruling.core.web.themes.VrThemeFactory;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCV;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicTeacherCVItem;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import net.vpc.app.vainruling.core.web.util.FileUploadEventHandler;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicCVSection;
import net.vpc.common.io.PathInfo;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileFilter;
import org.primefaces.event.FileUploadEvent;

//@VrController(
//        breadcrumb = {
//            @UPathItem(title = "Paramètres", css = "fa-dashboard", ctrl = "")},
//        title = "Mon profil enseignant",
//        menu = "/Config",
//        url = "modules/academic/profile/teacher-profile-settings"
////securityKey = "Custom.TeacherProfileSettings"
//)
public class TeacherProfileSettingsCtrl {

    @Autowired
    private AcademicProfilePlugin app;
    @Autowired
    private CorePlugin cp;

    private static final Logger log = Logger.getLogger(TeacherProfileSettingsCtrl.class.getName());

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    private void onPageReload() {
        Vr vr = Vr.get();
        getModel().setContact(vr.getUserSession().getUser().getContact());
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        getModel().setTeacher(ap.findTeacherByUser(vr.getUserSession().getUser().getId()));
        getModel().setTeacherCV(app.findOrCreateAcademicTeacherCV(getModel().getTeacher().getId()));
        ///a verifier
        getModel().setCourseSection(app.findAcademicCVSectionByName("Course"));
        getModel().setEducationSection(app.findAcademicCVSectionByName("Education"));
        getModel().setProjectSection(app.findAcademicCVSectionByName("Project"));
        getModel().setExperienceSection(app.findAcademicCVSectionByName("Experience"));
        getModel().setResearchSection(app.findAcademicCVSectionByName("Research"));
        
       /* getModel().setCourseList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().courseSection.getId()));
        getModel().setEducationList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().educationSection.getId()));
        getModel().setExperienceList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().experienceSection.getId()));
        getModel().setProjectList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().projectSection.getId()));
        getModel().setResearchList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), getModel().researchSection.getId()));
        */
       
        getModel().setExperienceList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), 1));
        getModel().setCourseList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), 1));
        getModel().setProjectList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), 1));
        getModel().setResearchList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), 1));
        getModel().setEducationList(app.findTeacherCvItemsBySection(getModel().teacherCV.getTeacher().getId(), 1));
        ///
        
        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
        getModel().getThemes().clear();
        getModel().getThemes().add(FacesUtils.createSelectItem("", "<Default>", null));
        for (VrTheme vrTheme : tfactory.getThemes()) {
            getModel().getThemes().add(FacesUtils.createSelectItem(vrTheme.getId(), vrTheme.getName(), null));
        }
        getModel().setTheme(CorePlugin.get().getCurrentUserTheme());

        List<SelectItem> list = null;

        list = new ArrayList<>();
        for (AppCompany x : cp.findCompanies()) {
            list.add(new SelectItem(x.getId(), x.getName()));
        }
        getModel().setCompanyItems(list);

    }

    public void onSelectTheme() {
        final CorePlugin t = VrApp.getBean(CorePlugin.class);
        try {
            t.setCurrentUserTheme(getModel().getTheme());
            FacesUtils.addInfoMessage("Theme mis a jour");
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex.getMessage());
        }
    }

    public void addNewCourseItem() {
        getModel().courseItem.setTeacherCV(getModel().teacherCV);
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().courseItem);
                    FacesUtils.addInfoMessage("Opération d'ajout d'un nouveau cours réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
        onPageReload();
    }

    public void onSaveNewEducationItem() {
        getModel().researchItem.setTeacherCV(getModel().teacherCV);
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().educationItem);
                    FacesUtils.addInfoMessage("Opération d'ajout d'une nouvelle formation réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
        onPageReload();
    }

    public void addNewExpeienceItem() {
        getModel().experienceItem.setTeacherCV(getModel().teacherCV);
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().experienceItem);
                    FacesUtils.addInfoMessage("Opération d'ajout d'une nouvelle expérience réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
        onPageReload();
    }

    public void addNewProjectItem() {
        getModel().projectItem.setTeacherCV(getModel().teacherCV);
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().projectItem);
                    FacesUtils.addInfoMessage("Opération d'ajout d'un nouveau projet réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
        onPageReload();
    }

    public void addNewResearchItem() {
        getModel().researchItem.setTeacherCV(getModel().teacherCV);
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.createAcademicTeacherCVItem(getModel().researchItem);
                    FacesUtils.addInfoMessage("Opération d'ajout d'un nouveau axe de recherche réussie");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
        onPageReload();
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

    public void onNewExperience(){
        ///
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/profile/add-teacher-experience-dialog", options, null);
    }

    public void onNewEducation(){
        ///
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/profile/add-teacher-education-dialog", options, null);
    }

    public void onNewProject(){
        ///
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/profile/add-teacher-project-dialog", options, null);
    }

    public void onNewCourse(){
        ///
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/profile/add-teacher-course-dialog", options, null);
    }

    public void onNewResearch(){
        ///
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/profile/add-teacher-research-dialog", options, null);
    }

    public void onRequestUploadPhoto() {
        getModel().setUploadingPhoto(true);
        getModel().setUploadingCV(false);
        /* Vr.get().openUploadDialog(new DocumentsUploadDialogCtrl.Config()
                .setExtensions("jpg,png,jpeg")
                .setSizeLimit(1024*1024)
                , this);*/
    }

    public void onRequestUploadCV() {
        getModel().setUploadingPhoto(false);
        getModel().setUploadingCV(true);
        /* Vr.get().openUploadDialog(new DocumentsUploadDialogCtrl.Config()
                        .setExtensions("pdf")
                        .setSizeLimit(10*1024*1024)
                , this);*/
    }

    public void onUpload(FileUploadEvent event) {
        try {
            if (getModel().isUploadingCV()) {
                AcademicTeacher c = AcademicPlugin.get().getCurrentTeacher();
                String e = PathInfo.create(event.getFile().getFileName()).getExtensionPart();
                if (e.equals("pdf")) {
                    String cvFile = c.getUser().getLogin() + "-cv.pdf";
                    VFile filePath = CorePlugin.get().getUserFolder(c.getUser().getLogin()).get("/Config/" + cvFile);
                    if (filePath != null) {
                        filePath.getParentFile().mkdirs();
                        VFile[] old = filePath.getParentFile().listFiles(new VFileFilter() {
                            @Override
                            public boolean accept(VFile pathname) {
                                if (pathname.getName().equalsIgnoreCase(cvFile)) {
                                    return true;
                                }
                                return false;
                            }
                        });
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
                        VFile[] old = filePath.getParentFile().listFiles(new VFileFilter() {
                            @Override
                            public boolean accept(VFile pathname) {
                                if (pathname.getName().equalsIgnoreCase("photo.png")
                                        || pathname.getName().equalsIgnoreCase("photo.jpg")
                                        || pathname.getName().equalsIgnoreCase("photo.jpeg")) {
                                    return true;
                                }
                                return false;
                            }
                        });
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

    public static class Model {

        private AcademicTeacher teacher;
        private AppContact contact;
        private AcademicTeacherCV teacherCV;

        private boolean uploadingPhoto;
        private boolean uploadingCV;

        private String theme;
        private List<SelectItem> themes = new ArrayList<>();

        List<SelectItem> companyItems = new ArrayList<>();
        
        AcademicCVSection experienceSection;
        AcademicCVSection educationSection;
        AcademicCVSection projectSection;
        AcademicCVSection researchSection;
        AcademicCVSection courseSection;
        
        List<AcademicTeacherCVItem> experienceList = new ArrayList<>();
        List<AcademicTeacherCVItem> educationList = new ArrayList<>();
        List<AcademicTeacherCVItem> projectList = new ArrayList<>();
        List<AcademicTeacherCVItem> researchList = new ArrayList<>();
        List<AcademicTeacherCVItem> courseList = new ArrayList<>();
       
        
        private AcademicTeacherCVItem courseItem = new AcademicTeacherCVItem();
        private AcademicTeacherCVItem educationItem = new AcademicTeacherCVItem();
        private AcademicTeacherCVItem experienceItem = new AcademicTeacherCVItem();
        private AcademicTeacherCVItem projectItem = new AcademicTeacherCVItem();
        private AcademicTeacherCVItem researchItem = new AcademicTeacherCVItem();

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public List<SelectItem> getThemes() {
            return themes;
        }

        public void setThemes(List<SelectItem> themes) {
            this.themes = themes;
        }

        public AcademicTeacherCVItem getExperienceItem() {
            return experienceItem;
        }

        public void setExperienceItem(AcademicTeacherCVItem experienceItem) {
            this.experienceItem = experienceItem;
        }

        public AcademicTeacherCVItem getResearchItem() {
            return researchItem;
        }

        public void setResearchItem(AcademicTeacherCVItem researchItem) {
            this.researchItem = researchItem;
        }

        public AcademicTeacherCVItem getCourseItem() {
            return courseItem;
        }

        public void setCourseItem(AcademicTeacherCVItem courseItem) {
            this.courseItem = courseItem;
        }

        public AcademicTeacherCVItem getEducationItem() {
            return educationItem;
        }

        public void setEducationItem(AcademicTeacherCVItem educationItem) {
            this.educationItem = educationItem;
        }

        public AcademicTeacherCVItem getProjectItem() {
            return projectItem;
        }

        public void setProjectItem(AcademicTeacherCVItem projectItem) {
            this.projectItem = projectItem;
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

    }
}
