package net.vpc.app.vr.plugins.academicprofile.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.web.*;
import net.vpc.app.vainruling.core.web.fs.files.DocumentUploadListener;
import net.vpc.app.vainruling.core.web.fs.files.DocumentsCtrl;
import net.vpc.app.vainruling.core.web.fs.files.DocumentsUploadDialogCtrl;
import net.vpc.app.vainruling.core.web.themes.VrTheme;
import net.vpc.app.vainruling.core.web.themes.VrThemeFactory;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import net.vpc.app.vr.plugins.academicprofile.service.model.AcademicStudentCV;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.springframework.beans.factory.annotation.Autowired;
import net.vpc.app.vainruling.core.web.util.FileUploadEventHandler;
import net.vpc.common.io.PathInfo;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileFilter;
import org.primefaces.event.FileUploadEvent;

@VrController(
        breadcrumb = {
                @UPathItem(title = "Paramètres", css = "fa-dashboard", ctrl = "")},
        title = "Mon profil étudiant",
        menu = "/Config",
        url = "modules/academic/profile/student-profile-settings"
        //securityKey = "Custom.StudentProfileSettings"
)
public class StudentProfileSettingsCtrl implements DocumentUploadListener,ActionEnabler{

    @Autowired
    private AcademicProfilePlugin app;
    @Autowired
    private CorePlugin cp;

    private static final Logger log = Logger.getLogger(StudentProfileSettingsCtrl.class.getName());

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    private void onPageReload() {
        Vr vr = Vr.get();
        getModel().setContact(vr.getCurrentSession().getUser().getContact());
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        getModel().setStudent(ap.findStudentByUser(vr.getCurrentSession().getUser().getId()));
        getModel().setStudentCV(app.findOrCreateAcademicStudentCV(getModel().getStudent().getId()));
        
        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
        getModel().getThemes().clear();
        getModel().getThemes().add(FacesUtils.createSelectItem("", "<Default>", null));
        for (VrTheme vrTheme : tfactory.getThemes()) {
            getModel().getThemes().add(FacesUtils.createSelectItem(vrTheme.getId(), vrTheme.getName(), null));
        }
        getModel().setTheme(CorePlugin.get().getCurrentUserTheme());
    }

    @Override
    public boolean isEnabled(Object data) {
        return AcademicPlugin.get().getCurrentStudent()!=null;
    }

    public void updateIdentityInformation() {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.updateContactInformations(getModel().contact);
                    app.updateStudentInformations(getModel().student);
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
                    app.updateContactInformations(getModel().contact);
                    app.updateStudentCVInformations(getModel().studentCV);
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
            t.setCurrentUserTheme(getModel().getTheme());
            FacesUtils.addInfoMessage("Theme mis a jour");
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex.getMessage());
        }
    }

    public void onRequestUploadPhoto(){
        getModel().setUploadingPhoto(true);
        getModel().setUploadingCV(false);
        Vr.get().openUploadDialog(new DocumentsUploadDialogCtrl.Config()
                .setExtensions("jpg,png,jpeg")
                .setSizeLimit(1024*1024)
                , this);
    }


    public void onRequestUploadCV(){
        getModel().setUploadingPhoto(false);
        getModel().setUploadingCV(true);
        Vr.get().openUploadDialog(new DocumentsUploadDialogCtrl.Config()
                        .setExtensions("pdf")
                        .setSizeLimit(10*1024*1024)
                , this);
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
    
    public void onUpload(FileUploadEvent event) {
        try {
            if(getModel().isUploadingCV()){
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
                        CorePlugin.get().uploadFile(filePath, new FileUploadEventHandler(event));
                        FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
                    }
                } else {
                    FacesUtils.addErrorMessage(event.getFile().getFileName() + " has invalid extension");
                }
            }else if(getModel().isUploadingPhoto()) {
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
                                if (
                                        pathname.getName().equalsIgnoreCase("photo.png")
                                                || pathname.getName().equalsIgnoreCase("photo.jpg")
                                                || pathname.getName().equalsIgnoreCase("photo.jpeg")
                                        ) {
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

    public void updateStudySection() {


        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    app.updateStudentInformations(getModel().student);
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
        private AppContact contact;
        private AcademicStudentCV studentCV;
        
        private String theme;
        private List<SelectItem> themes = new ArrayList<>();

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

        public AppContact getContact() {
            return contact;
        }

        public void setContact(AppContact contact) {
            this.contact = contact;
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
        
    }

}
