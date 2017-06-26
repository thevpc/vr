/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.internship;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.OpinionType;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.AcademicInternshipDuration;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.AcademicInternshipVariant;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.web.dialog.DisciplineDialogCtrl;
import net.vpc.common.io.PathInfo;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.model.SelectItem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * internships for students
 *
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Mes Stages",
        menu = "/Education/Projects/Internships",
        securityKey = "Custom.Education.MyInternships",
        url = "modules/academic/internship/my-internships"
)
public class MyInternshipsCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public boolean isStudent() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentStudent() != null;
    }

    public boolean isTeacher() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentTeacher() != null;
    }

    public AcademicStudent getCurrentStudent() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentStudent();
    }

    public AcademicTeacher getCurrentTeacher() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentTeacher();
    }

    public AcademicTeacher getCurrentHeadOfDepartment() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicTeacher t = a.getCurrentTeacher();
        if (t != null) {
            AppDepartment d = t.getDepartment();
            if (d != null) {
                AcademicTeacher h = a.findHeadOfDepartment(d.getId());
                if (h != null && h.getId() == t.getId()) {
                    return t;
                }
            }
        }
        return null;
    }

    public AcademicInternship getSelectedInternship() {
        String ii = getModel().getInternshipId();
        if (ii != null && ii.length() > 0) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            AcademicInternship tt = p.findInternship(Integer.parseInt(ii));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicTeacher getSelectedTeacher(String id) {
        if (id != null && id.length() > 0) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            AcademicTeacher tt = p.findTeacher(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AppCompany getSelectedCompany(String id) {
        if (id != null && id.length() > 0) {
            CorePlugin p = VrApp.getBean(CorePlugin.class);
            AppCompany tt = p.findCompany(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicInternshipVariant getSelectedInternshipVariant(String id) {
        if (id != null && id.length() > 0) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            AcademicInternshipVariant tt = p.findInternshipVariant(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicInternshipDuration getSelectedInternshipDuration(String id) {
        if (id != null && id.length() > 0) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            AcademicInternshipDuration tt = p.findInternshipDuration(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public void onUpdateCompany() {
        if (getModel().getInternship() != null) {
            AppCompany s1 = getSelectedCompany(getModel().getCompanyId());
            getModel().getInternship().setCompany(s1);
        }
    }

    public void onUpdateSupervisor() {
        if (getModel().getInternship() != null) {
            if (isUpdatesSupervisors()) {
                AcademicTeacher s1 = getSelectedTeacher(getModel().getSupervisor1Id());
                getModel().getInternship().setSupervisor(s1);
                AcademicTeacher s2 = getSelectedTeacher(getModel().getSupervisor2Id());
                getModel().getInternship().setSecondSupervisor(s2);
            }
        }
    }

    public boolean isUpdatesDescr() {
        if (isStudent()) {
            return getModel().getInternship().getInternshipStatus().isStudentUpdatesDescr();
        }
        if (isTeacher()) {
            return getModel().getInternship().getInternshipStatus().isBoardUpdatesDescr();
        }
        return false;
    }

    public boolean isUpdatesSupervisors() {
        if (isStudent()) {
            return getModel().getInternship().getInternshipStatus().isStudentUpdatesSupervisors();
        }
        if (isTeacher()) {
            return getModel().getInternship().getInternshipStatus().isBoardUpdatesSupervisors();
        }
        return false;
    }

    public boolean isUpdatesReport1() {
        if (isStudent()) {
            return getModel().getInternship().getInternshipStatus().isStudentUpdatesReport1();
        }
        return false;
    }

    public boolean isUpdatesReport2() {
        if (isStudent()) {
            return getModel().getInternship().getInternshipStatus().isStudentUpdatesReport2();
        }
        return false;
    }

    public boolean isUpdatesReport3() {
        if (isStudent()) {
            return getModel().getInternship().getInternshipStatus().isStudentUpdatesReport3();
        }
        return false;
    }

    public void onUpdateVariant() {
        if (getModel().getInternship() != null) {
            if (isUpdatesDescr()) {
                AcademicInternshipVariant s1 = getSelectedInternshipVariant(getModel().getTypeVariantId());
                getModel().getInternship().setInternshipVariant(s1);
            }
        }
    }

    public void onUpdateDuration() {
        if (getModel().getInternship() != null) {
            if (isUpdatesDescr()) {
                AcademicInternshipDuration s1 = getSelectedInternshipDuration(getModel().getDurationId());
                getModel().getInternship().setDuration(s1);
            }
        }
    }

    public void onSave() {
        try {
            if (getModel().getInternship() != null) {
                UPA.getContext().invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        PersistenceUnit pu = UPA.getPersistenceUnit();
                        AcademicInternship old = getModel().getInternship();
                        if (getModel().isStudent()) {
                            old.setLastStudentUpdateTime(new Timestamp(System.currentTimeMillis()));
                            old.setLastUpdateTime(old.getLastStudentUpdateTime());
                        } else {
                            old.setLastTeacherUpdateTime(new Timestamp(System.currentTimeMillis()));
                            old.setLastUpdateTime(old.getLastTeacherUpdateTime());
                        }
                        pu.merge(old);
                        getModel().setInternship((AcademicInternship) pu.findById(AcademicInternship.class, old.getId()));
                    }
                });
                FacesUtils.addWarnMessage(null, "Enregistrement réussi");
            } else {
                FacesUtils.addWarnMessage(null, "Rien à enregistrer");
            }
        } catch (Exception e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    @OnPageLoad
    public void onPageLoad() {
        getModel().setInternshipId(null);
        getModel().setInternship(null);
        onRefresh();
    }

    @OnPageLoad
    public void onRefresh() {
        getModel().setUploading(false);
        getModel().setInternship(getSelectedInternship());

        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        AcademicPlugin pi = VrApp.getBean(AcademicPlugin.class);
        getModel().setInternships(new ArrayList<SelectItem>());
        getModel().setTeachers(new ArrayList<SelectItem>());
        getModel().setTypeVariants(new ArrayList<SelectItem>());
        getModel().setDurations(new ArrayList<SelectItem>());
        getModel().setCompanies(new ArrayList<SelectItem>());

        AcademicStudent currentStudent = getCurrentStudent();
        AcademicTeacher currentTeacher = getCurrentTeacher();
        AcademicTeacher headOfDepartment = getCurrentHeadOfDepartment();
        List<AcademicInternship> internships = new ArrayList<>();
        if (currentStudent != null) {
            getModel().setStudent(true);
            getModel().setTeacher(false);
            internships = pi.findActualInternshipsByStudent(currentStudent.getId());
        } else if (currentTeacher != null) {
            getModel().setStudent(false);
            getModel().setTeacher(true);
            internships = pi.findActualInternshipsBySupervisor(currentTeacher.getId());
        }
        getModel().setManager(headOfDepartment != null);
        for (AcademicInternship t : internships) {
            String n = null;
            if (currentStudent == null) {
                AcademicStudent s = t.getStudent();
                AcademicPlugin pp = VrApp.getBean(AcademicPlugin.class);
                String sname = pp.getValidName(s);
                n = (t.getBoard() == null ? "?" : t.getBoard().getName()) + "-" + t.getCode() + "-" + sname + "-" + t.getName();
            } else {
                n = (t.getBoard() == null ? "?" : t.getBoard().getName()) + "-" + t.getCode() + "-" + t.getName();
            }
            getModel().getInternships().add(new SelectItem(String.valueOf(t.getId()), n));
        }

        if (getModel().getInternship() != null && getModel().getInternship().getCompany() != null) {
            getModel().setCompanyId(String.valueOf(getModel().getInternship().getCompany().getId()));
        } else {
            getModel().setCompanyId(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getDuration() != null) {
            getModel().setDurationId(String.valueOf(getModel().getInternship().getDuration().getId()));
        } else {
            getModel().setDurationId(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getSupervisor() != null) {
            getModel().setSupervisor1Id(String.valueOf(getModel().getInternship().getSupervisor().getId()));
        } else {
            getModel().setSupervisor1Id(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getSecondSupervisor() != null) {
            getModel().setSupervisor2Id(String.valueOf(getModel().getInternship().getSecondSupervisor().getId()));
        } else {
            getModel().setSupervisor2Id(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getInternshipVariant() != null) {
            getModel().setTypeVariantId(String.valueOf(getModel().getInternship().getInternshipVariant().getId()));
        } else {
            getModel().setTypeVariantId(null);
        }

        if (getModel().getInternship() != null && getModel().getInternship().getBoard() != null) {
            for (AcademicInternshipVariant t : pi.findInternshipVariantsByType(getModel().getInternship().getBoard().getInternshipType().getId())) {
                String n = t.getName();
                getModel().getTypeVariants().add(new SelectItem(String.valueOf(t.getId()), n));
            }
            for (AcademicInternshipDuration t : pi.findInternshipDurationsByType(getModel().getInternship().getBoard().getInternshipType().getId())) {
                String n = t.getName();
                getModel().getDurations().add(new SelectItem(String.valueOf(t.getId()), n));
            }
        }

        for (AcademicTeacher t : p.findTeachers()) {
            String n = p.getValidName(t);
            getModel().getTeachers().add(new SelectItem(String.valueOf(t.getId()), n));
        }
        for (AppCompany t : c.findCompanies()) {
            getModel().getCompanies().add(new SelectItem(String.valueOf(t.getId()), t.getName()));
        }
        getModel().setSupervisor1Id((getModel().getInternship() == null || getModel().getInternship().getSupervisor() == null) ? null : String.valueOf(getModel().getInternship().getSupervisor().getId()));
        getModel().setSupervisor2Id((getModel().getInternship() == null || getModel().getInternship().getSecondSupervisor() == null) ? null : String.valueOf(getModel().getInternship().getSecondSupervisor().getId()));
        getModel().setTypeVariantId((getModel().getInternship() == null || getModel().getInternship().getInternshipVariant() == null) ? null : String.valueOf(getModel().getInternship().getInternshipVariant().getId()));
        getModel().setDurationId((getModel().getInternship() == null || getModel().getInternship().getDuration() == null) ? null : String.valueOf(getModel().getInternship().getDuration().getId()));
    }

    public void onRequestUpload(String report) {
        getModel().setRequestUploadType(report);
        getModel().setUploading(true);
    }

    public StreamedContent download(final String report) {
        final CorePlugin fs = VrApp.getBean(CorePlugin.class);
        return UPA.getPersistenceUnit().invokePrivileged(new Action<StreamedContent>() {
            @Override
            public StreamedContent run() {
                VFile f = null;
                if ("report1".equals(report)) {
                    f = fs.getFileSystem().get(getModel().getInternship().getSpecFilePath());
                } else if ("report2".equals(report)) {
                    f = fs.getFileSystem().get(getModel().getInternship().getMidTermReportFilePath());
                } else if ("report3".equals(report)) {
                    f = fs.getFileSystem().get(getModel().getInternship().getReportFilePath());
                }
                if (f != null) {
                    InputStream stream = null;
                    try {
                        fs.markDownloaded(f);
                        stream = f.getInputStream();
                        return new DefaultStreamedContent(stream, f.probeContentType(), f.getName());
                    } catch (IOException ex) {
                        Logger.getLogger(MyInternshipsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }
        });
    }

    public void handleFileUpload(final FileUploadEvent event) {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    String report = getModel().getRequestUploadType();
                    CorePlugin fs = VrApp.getBean(CorePlugin.class);
                    String login = UserSession.getCurrentLogin();
                    String tempPath = CorePlugin.PATH_TEMP + "/Import/" + VrUtils.date(new Date(), "yyyy-MM-dd-HH-mm")
                            + "-" + login;
                    String p = fs.getNativeFileSystemPath() + tempPath;
                    new File(p).mkdirs();
                    File f = new File(p, event.getFile().getFileName());
                    event.getFile().write(f.getPath());
                    AcademicInternship internship = getModel().getInternship();

                    VFile userHome = fs.getUserFolder(login).get("MesRapports");
                    userHome.mkdirs();
                    PathInfo uu = PathInfo.create(f);
                    String extensionPart = uu.getExtensionPart();
                    if (extensionPart == null) {
                        extensionPart = "pdf";
                    }
                    if ("report1".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-spec." + extensionPart;
                        VFile ff = userHome.get(validName);
                        fs.getFileSystem().get(tempPath + "/" + event.getFile().getFileName()).copyTo(ff);
                        internship.setSpecFilePath(ff.getBaseFile("vrfs").getPath());
                    } else if ("report2".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-mid." + extensionPart;
                        VFile ff = userHome.get(validName);
                        fs.getFileSystem().get(tempPath + "/" + event.getFile().getFileName()).copyTo(ff);
                        internship.setMidTermReportFilePath(ff.getBaseFile("vrfs").getPath());

                    } else if ("report3".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-final." + extensionPart;
                        VFile ff = userHome.get(validName);
                        fs.getFileSystem().get(tempPath + "/" + event.getFile().getFileName()).copyTo(ff);
                        internship.setReportFilePath(ff.getBaseFile("vrfs").getPath());
                    } else {
                        return;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MyInternshipsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        getModel().setRequestUploadType(null);
        getModel().setUploading(false);
    }

    public void openDisciplineDialog() {
        DisciplineDialogCtrl.Config c = new DisciplineDialogCtrl.Config();
        c.setSourceId("");
        c.setUserInfo("");
        c.setTitle("Disciplines");
        c.setExpression(getModel().getInternship().getMainDiscipline());
        VrApp.getBean(DisciplineDialogCtrl.class).openDialog(c);
    }

    public void onDisciplineDialogClosed(SelectEvent event) {
        DialogResult o = (DialogResult) event.getObject();
        if (o != null) {
            getModel().getInternship().setMainDiscipline((String) o.getValue());
        }
    }

    public static class InternshipInfo {

        private AcademicInternship internship;
        private String intents;
        private String intentsId;

        public AcademicInternship getInternship() {
            return internship;
        }

        public void setInternship(AcademicInternship internship) {
            this.internship = internship;
        }

        public String getIntents() {
            return intents;
        }

        public void setIntents(String intents) {
            this.intents = intents;
        }

        public String getIntentsId() {
            return intentsId;
        }

        public void setIntentsId(String intentsId) {
            this.intentsId = intentsId;
        }

    }

    public class Model {

        private String requestUploadType;
        private boolean uploading;
        private boolean student;
        private boolean teacher;
        private boolean manager;
        private String internshipId;
        private String companyId;
        private String supervisor1Id;
        private String supervisor2Id;
        private String typeVariantId;
        private String durationId;
        private AcademicInternship internship;
        private List<SelectItem> internships = new ArrayList<SelectItem>();
        private List<SelectItem> teachers = new ArrayList<SelectItem>();
        private List<SelectItem> companies = new ArrayList<SelectItem>();
        private List<SelectItem> typeVariants = new ArrayList<SelectItem>();
        private List<SelectItem> durations = new ArrayList<SelectItem>();

        public OpinionType[] getOpinions() {
            return OpinionType.values();
        }

        public AcademicInternship getInternship() {
            return internship;
        }

        public void setInternship(AcademicInternship internship) {
            this.internship = internship;
        }

        public String getInternshipId() {
            return internshipId;
        }

        public void setInternshipId(String internshipId) {
            this.internshipId = internshipId;
        }

        public List<SelectItem> getInternships() {
            return internships;
        }

        public void setInternships(List<SelectItem> internships) {
            this.internships = internships;
        }

        public String getSupervisor1Id() {
            return supervisor1Id;
        }

        public void setSupervisor1Id(String supervisor1Id) {
            this.supervisor1Id = supervisor1Id;
        }

        public String getSupervisor2Id() {
            return supervisor2Id;
        }

        public void setSupervisor2Id(String supervisor2Id) {
            this.supervisor2Id = supervisor2Id;
        }

        public List<SelectItem> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<SelectItem> teachers) {
            this.teachers = teachers;
        }

        public String getTypeVariantId() {
            return typeVariantId;
        }

        public void setTypeVariantId(String typeVariantId) {
            this.typeVariantId = typeVariantId;
        }

        public List<SelectItem> getTypeVariants() {
            return typeVariants;
        }

        public void setTypeVariants(List<SelectItem> typeVariants) {
            this.typeVariants = typeVariants;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public List<SelectItem> getCompanies() {
            return companies;
        }

        public void setCompanies(List<SelectItem> companies) {
            this.companies = companies;
        }

        public String getRequestUploadType() {
            return requestUploadType;
        }

        public void setRequestUploadType(String requestUploadType) {
            this.requestUploadType = requestUploadType;
        }

        public boolean isUploading() {
            return uploading;
        }

        public void setUploading(boolean uploading) {
            this.uploading = uploading;
        }

        public String getDurationId() {
            return durationId;
        }

        public void setDurationId(String durationId) {
            this.durationId = durationId;
        }

        public List<SelectItem> getDurations() {
            return durations;
        }

        public void setDurations(List<SelectItem> durations) {
            this.durations = durations;
        }

        public boolean isStudent() {
            return student;
        }

        public void setStudent(boolean student) {
            this.student = student;
        }

        public boolean isTeacher() {
            return teacher;
        }

        public void setTeacher(boolean teacher) {
            this.teacher = teacher;
        }

        public boolean isManager() {
            return manager;
        }

        public void setManager(boolean manager) {
            this.manager = manager;
        }

    }
}
