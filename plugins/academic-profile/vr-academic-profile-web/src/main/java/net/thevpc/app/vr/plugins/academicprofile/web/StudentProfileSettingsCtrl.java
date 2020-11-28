package net.thevpc.app.vr.plugins.academicprofile.web;

import net.thevpc.app.vainruling.VrActionEnabler;
import net.thevpc.app.vainruling.VrActionInfo;
import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.DocumentUploadListener;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.DocumentsCtrl;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.FileUploadEventHandler;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.dialog.DocumentsUploadDialogCtrl;
import net.thevpc.app.vainruling.core.web.themes.VrTheme;
import net.thevpc.app.vainruling.core.web.themes.VrThemeFace;
import net.thevpc.app.vainruling.core.web.themes.VrThemeFactory;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vr.plugins.academicprofile.model.AcademicStudentCV;
import net.thevpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.web.*;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.common.io.PathInfo;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.vfs.VFile;
import net.thevpc.common.vfs.VFileFilter;
import net.thevpc.upa.UPA;
import net.thevpc.upa.VoidAction;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.VrPage;

@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Paramètres", css = "fa-dashboard", ctrl = "")},
        title = "Mon profil étudiant",
        menu = "/Config",
        url = "modules/academic/profile/student-profile-settings",
        replacementFor = "myProfileCtrl",
        priority = 2
//securityKey = "Custom.StudentProfileSettings"
)
@ManagedBean
public class StudentProfileSettingsCtrl implements DocumentUploadListener, VrActionEnabler {

    private static final Logger log = Logger.getLogger(StudentProfileSettingsCtrl.class.getName());
    @Autowired
    private AcademicProfilePlugin app;
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @VrOnPageLoad
    private void onPageReload() {
        Vr vr = Vr.get();
        getModel().setUser(core.getCurrentUser());
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        getModel().setStudent(ap.findStudentByUser(core.getCurrentUserId()));
        getModel().setStudentCV(app.findOrCreateAcademicStudentCV(getModel().getStudent().getId()));

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
        getModel().setPublicTheme(CorePlugin.get().getCurrentUserPublicTheme());
    }

    @Override
    public void checkEnabled(VrActionInfo data) {
        if (AcademicPlugin.get().getCurrentStudent() == null) {
            throw new SecurityException("Expected student");
        }
    }

    public void updateIdentityInformation() {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.saveUserContact(getModel().user);
                    app.saveStudent(getModel().student);
                    FacesUtils.addInfoMessage("Modifications enregistrées");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }

    public void updateContactInformationSection() {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.saveUserContact(getModel().user);
                    app.saveStudentCV(getModel().studentCV);
                    FacesUtils.addInfoMessage("Modifications enregistrées");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
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

    public void updateAboutSection() {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.saveUserContact(getModel().user);
                    FacesUtils.addInfoMessage("Modifications enregistrées");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }

    public void onUpload(FileUploadEvent event) {
        try {
            if (getModel().isUploadingCV()) {
                AcademicStudent c = AcademicPlugin.get().getCurrentStudent();
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
                        UPA.getContext().invokePrivileged(new VoidAction() {
                            @Override
                            public void run() {
                                try {
                                    CorePlugin.get().uploadFile(filePath, new FileUploadEventHandler(event));
                                    FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
                                } catch (IOException e1) {
                                    Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, e1);
                                    FacesUtils.addErrorMessage(e1, event.getFile().getFileName() + " uploading failed.");
                                }
                            }
                        });
                    }
                } else {
                    FacesUtils.addErrorMessage(event.getFile().getFileName() + " has invalid extension");
                }
            } else if (getModel().isUploadingPhoto()) {
                AcademicStudent c = AcademicPlugin.get().getCurrentStudent();
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
                        UPA.getContext().invokePrivileged(new VoidAction() {
                            @Override
                            public void run() {
                                try {
                                    CorePlugin.get().uploadFile(filePath, new FileUploadEventHandler(event));
                                    FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
                                } catch (IOException e1) {
                                    Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, e1);
                                    FacesUtils.addErrorMessage(e1, event.getFile().getFileName() + " uploading failed.");
                                }
                            }
                        });
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

    public void updateStudySection() {

        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.saveStudent(getModel().student);
                    FacesUtils.addInfoMessage("Modifications enregistrées");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error", ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
            }
        });
    }

    public static class Model {

        private boolean uploadingPhoto;
        private boolean uploadingCV;
        private AcademicStudent student;
        private AppUser user;
        private AcademicStudentCV studentCV;

        private String privateTheme;
        private List<SelectItem> privateThemes = new ArrayList<>();
        private String publicTheme;
        private List<SelectItem> publicThemes = new ArrayList<>();

        public AcademicStudent getStudent() {
            return student;
        }

        public void setStudent(AcademicStudent student) {
            this.student = student;
        }

        public AcademicStudentCV getStudentCV() {
            return studentCV;
        }

        public void setStudentCV(AcademicStudentCV studentCV) {
            this.studentCV = studentCV;
        }

        public AppUser getUser() {
            return user;
        }

        public void setUser(AppUser user) {
            this.user = user;
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
    }

}
