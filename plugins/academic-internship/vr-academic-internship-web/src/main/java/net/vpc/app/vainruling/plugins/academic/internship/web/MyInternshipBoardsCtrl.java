/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppCompany;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.api.web.obj.DialogResult;
import net.vpc.app.vainruling.plugins.academic.internship.service.AcademicInternshipPlugin;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipDuration;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipType;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipVariant;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.web.dialog.DisciplineDialogCtrl;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import net.vpc.upa.types.DateTime;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * internships for teachers
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Mes Commissions de Stage",
        menu = "/Education/Internship",
        securityKey = "Custom.Education.MyInternshipBoards",
        url = "modules/academic/internship/myinternshipboards"
)
@ManagedBean
public class MyInternshipBoardsCtrl {

    private Model model = new Model();

    public class Model {

        private String requestUploadType;
        private boolean uploading;
        private String internshipId;
        private String boardId;
        private String companyId;
        private String internshipStatusId;
        private String chairExaminerId;
        private String firstExaminerId;
        private String secondExaminerId;
        private String superviser1Id;
        private String superviser2Id;
        private String typeVariantId;
        private String durationId;
        private AcademicInternship internship;
        private AcademicInternshipBoard internshipBoard;
        private List<SelectItem> boards = new ArrayList<SelectItem>();
        private List<SelectItem> internships = new ArrayList<SelectItem>();
        private List<SelectItem> teachers = new ArrayList<SelectItem>();
        private List<SelectItem> companies = new ArrayList<SelectItem>();
        private List<SelectItem> typeVariants = new ArrayList<SelectItem>();
        private List<SelectItem> durations = new ArrayList<SelectItem>();
        private List<SelectItem> internshipStatuses = new ArrayList<SelectItem>();

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

        public String getSuperviser1Id() {
            return superviser1Id;
        }

        public void setSuperviser1Id(String superviser1Id) {
            this.superviser1Id = superviser1Id;
        }

        public String getSuperviser2Id() {
            return superviser2Id;
        }

        public void setSuperviser2Id(String superviser2Id) {
            this.superviser2Id = superviser2Id;
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

        public String getBoardId() {
            return boardId;
        }

        public void setBoardId(String boardId) {
            this.boardId = boardId;
        }

        public List<SelectItem> getBoards() {
            return boards;
        }

        public void setBoards(List<SelectItem> boards) {
            this.boards = boards;
        }

        public AcademicInternshipBoard getInternshipBoard() {
            return internshipBoard;
        }

        public void setInternshipBoard(AcademicInternshipBoard internshipBoard) {
            this.internshipBoard = internshipBoard;
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

        public String getInternshipStatusId() {
            return internshipStatusId;
        }

        public void setInternshipStatusId(String internshipStatusId) {
            this.internshipStatusId = internshipStatusId;
        }

        public String getChairExaminerId() {
            return chairExaminerId;
        }

        public void setChairExaminerId(String chairExaminerId) {
            this.chairExaminerId = chairExaminerId;
        }

        public String getFirstExaminerId() {
            return firstExaminerId;
        }

        public void setFirstExaminerId(String firstExaminerId) {
            this.firstExaminerId = firstExaminerId;
        }

        public String getSecondExaminerId() {
            return secondExaminerId;
        }

        public void setSecondExaminerId(String secondExaminerId) {
            this.secondExaminerId = secondExaminerId;
        }

        public List<SelectItem> getInternshipStatuses() {
            return internshipStatuses;
        }

        public void setInternshipStatuses(List<SelectItem> internshipStatuses) {
            this.internshipStatuses = internshipStatuses;
        }

    }

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

    public AcademicTeacher getCurrentTeacher() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentTeacher();
    }

    public AcademicInternship getSelectedInternship() {
        String ii = getModel().getInternshipId();
        if (ii != null && ii.length() > 0) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternship tt = p.findInternship(Integer.parseInt(ii));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicInternshipBoard getSelectedInternshipBoard() {
        String ii = getModel().getBoardId();
        if (ii != null && ii.length() > 0) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternshipBoard tt = p.findInternshipBoard(Integer.parseInt(ii));
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

    public AcademicInternshipStatus getSelectedInternshipStatus(String id) {
        if (id != null && id.length() > 0) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternshipStatus tt = p.findInternshipStatus(Integer.parseInt(id));
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
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
            AcademicInternshipVariant tt = p.findInternshipVariant(Integer.parseInt(id));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public AcademicInternshipDuration getSelectedInternshipDuration(String id) {
        if (id != null && id.length() > 0) {
            AcademicInternshipPlugin p = VrApp.getBean(AcademicInternshipPlugin.class);
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

    public void onUpdateChairExaminer() {
        if (getModel().getInternship() != null) {
            AcademicTeacher s1 = getSelectedTeacher(getModel().getChairExaminerId());
            getModel().getInternship().setChairExaminer(s1);
        }
    }

    public void onUpdateFirstExaminer() {
        if (getModel().getInternship() != null) {
            AcademicTeacher s1 = getSelectedTeacher(getModel().getFirstExaminerId());
            getModel().getInternship().setFirstExaminer(s1);
        }
    }

    public void onUpdateSecondExaminer() {
        if (getModel().getInternship() != null) {
            AcademicTeacher s1 = getSelectedTeacher(getModel().getSecondExaminerId());
            getModel().getInternship().setSecondExaminer(s1);
        }
    }

    public void onUpdateInternshipStatus() {
        if (getModel().getInternship() != null) {
            AcademicInternshipStatus s1 = getSelectedInternshipStatus(getModel().getInternshipStatusId());
            getModel().getInternship().setInternshipStatus(s1);
        }
    }

    public void onUpdateSupervisor() {
        if (getModel().getInternship() != null) {
            if (getModel().getInternship().getInternshipStatus().isStudentUpdatesSupervisors()) {
                AcademicTeacher s1 = getSelectedTeacher(getModel().getSuperviser1Id());
                getModel().getInternship().setSupervisor(s1);
                AcademicTeacher s2 = getSelectedTeacher(getModel().getSuperviser2Id());
                getModel().getInternship().setSecondSupervisor(s2);
            }
        }
    }

    public void onUpdateVariant() {
        if (getModel().getInternship() != null) {
            if (getModel().getInternship().getInternshipStatus().isStudentUpdatesDescr()) {
                AcademicInternshipVariant s1 = getSelectedInternshipVariant(getModel().getTypeVariantId());
                getModel().getInternship().setInternshipVariant(s1);
            }
        }
    }

    public void onUpdateDuration() {
        if (getModel().getInternship() != null) {
            if (getModel().getInternship().getInternshipStatus().isStudentUpdatesDescr()) {
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
                        old.setLastTeacherUpdateTime(new Timestamp(System.currentTimeMillis()));
                        old.setLastUpdateTime(old.getLastTeacherUpdateTime());
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
        onUpdateBoard();
    }

    public void onUpdateBoard() {
        getModel().setInternshipBoard(getSelectedInternshipBoard());
        if (getModel().getInternshipBoard() == null) {
            getModel().setInternship(null);
        } else if (getModel().getInternship() != null && getModel().getInternship().getBoard() != null && getModel().getInternship().getBoard().getId() == getModel().getInternshipBoard().getId()) {
            //ok
        } else {
            getModel().setInternship(null);
        }
        onRefresh();
    }

    public void onUpdateInternship() {
        getModel().setInternship(getSelectedInternship());
        onRefresh();
    }

    public void onRefresh() {

        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        AcademicInternshipPlugin pi = VrApp.getBean(AcademicInternshipPlugin.class);
        getModel().setInternships(new ArrayList<SelectItem>());
        getModel().setBoards(new ArrayList<SelectItem>());
        getModel().setTeachers(new ArrayList<SelectItem>());
        getModel().setTypeVariants(new ArrayList<SelectItem>());
        getModel().setDurations(new ArrayList<SelectItem>());
        getModel().setCompanies(new ArrayList<SelectItem>());
        getModel().setInternshipStatuses(new ArrayList<SelectItem>());

        AcademicTeacher currentTeacher = getCurrentTeacher();
        List<AcademicInternship> internships = new ArrayList<>();
        List<AcademicInternshipBoard> internshipBoards = new ArrayList<>();
        
        if (currentTeacher != null) {
            if (currentTeacher.getDepartment() != null) {
                internshipBoards = pi.findEnabledInternshipBoardsByDepartment(currentTeacher.getDepartment().getId());
            }
            internships = pi.findActualInternshipsByTeacher(currentTeacher.getId(), getModel().getInternshipBoard() == null ? -1 : getModel().getInternshipBoard().getId());
        }
        
        for (AcademicInternshipBoard t : internshipBoards) {
            String n = t.getName();
            getModel().getBoards().add(new SelectItem(String.valueOf(t.getId()), n));
        }
        
        for (AcademicInternship t : internships) {
            String n = null;
            AcademicStudent s = t.getStudent();
            AcademicPlugin pp = VrApp.getBean(AcademicPlugin.class);
            String sname = pp.getValidName(s);
            n = (t.getBoard()==null ? "?":t.getBoard().getName()) + "-" + t.getCode() + "-" + sname + "-" + t.getName();
            getModel().getInternships().add(new SelectItem(String.valueOf(t.getId()), n));
        }

        if (getModel().getInternship() != null && getModel().getInternship().getBoard()!= null) {
            AcademicInternshipType internshipType = getModel().getInternship().getBoard().getInternshipType();
            for (AcademicInternshipVariant t : pi.findInternshipVariantsByType(internshipType.getId())) {
                String n = t.getName();
                getModel().getTypeVariants().add(new SelectItem(String.valueOf(t.getId()), n));
            }
            for (AcademicInternshipDuration t : pi.findInternshipDurationsByType(internshipType.getId())) {
                String n = t.getName();
                getModel().getDurations().add(new SelectItem(String.valueOf(t.getId()), n));
            }
            for (AcademicInternshipStatus t : pi.findInternshipStatusesByType(internshipType.getId())) {
                String n = t.getName();
                getModel().getInternshipStatuses().add(new SelectItem(String.valueOf(t.getId()), n));
            }
        }

        for (AcademicTeacher t : p.findTeachers()) {
            String n = p.getValidName(t);
            getModel().getTeachers().add(new SelectItem(String.valueOf(t.getId()), n));
        }
        for (AppCompany t : c.findCompanies()) {
            getModel().getCompanies().add(new SelectItem(String.valueOf(t.getId()), t.getName()));
        }
        getModel().setSuperviser1Id((getModel().getInternship() == null || getModel().getInternship().getSupervisor() == null) ? null : String.valueOf(getModel().getInternship().getSupervisor().getId()));
        getModel().setSuperviser2Id((getModel().getInternship() == null || getModel().getInternship().getSecondSupervisor() == null) ? null : String.valueOf(getModel().getInternship().getSecondSupervisor().getId()));
        getModel().setTypeVariantId((getModel().getInternship() == null || getModel().getInternship().getInternshipVariant() == null) ? null : String.valueOf(getModel().getInternship().getInternshipVariant().getId()));
        getModel().setDurationId((getModel().getInternship() == null || getModel().getInternship().getDuration() == null) ? null : String.valueOf(getModel().getInternship().getDuration().getId()));
        getModel().setChairExaminerId((getModel().getInternship() == null || getModel().getInternship().getChairExaminer() == null) ? null : String.valueOf(getModel().getInternship().getChairExaminer().getId()));
        getModel().setFirstExaminerId((getModel().getInternship() == null || getModel().getInternship().getFirstExaminer() == null) ? null : String.valueOf(getModel().getInternship().getFirstExaminer().getId()));
        getModel().setSecondExaminerId((getModel().getInternship() == null || getModel().getInternship().getSecondExaminer() == null) ? null : String.valueOf(getModel().getInternship().getSecondExaminer().getId()));
        getModel().setInternshipStatusId((getModel().getInternship() == null || getModel().getInternship().getInternshipStatus() == null) ? null : String.valueOf(getModel().getInternship().getInternshipStatus().getId()));
    }

    public void onRequestUpload(String report) {
        getModel().setRequestUploadType(report);
        getModel().setUploading(true);
    }

    public StreamedContent download(final String report) {
        final FileSystemPlugin fs = VrApp.getBean(FileSystemPlugin.class);
        return UPA.getContext().invokePrivileged(new Action<StreamedContent>() {
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
                        FileSystemPlugin fsp = VrApp.getBean(FileSystemPlugin.class);
                        fsp.markDownloaded(f);
                        stream = f.getInputStream();
                        return new DefaultStreamedContent(stream, f.probeContentType(), f.getName());
                    } catch (IOException ex) {
                        Logger.getLogger(MyInternshipBoardsCtrl.class.getName()).log(Level.SEVERE, null, ex);
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
                    FileSystemPlugin fs = VrApp.getBean(FileSystemPlugin.class);
                    String login = VrApp.getBean(UserSession.class).getUser().getLogin();
                    String tempPath = "/Temp/Import/" + VrHelper.date(new Date(), "yyyy-MM-dd-HH-mm")
                            + "-" + login;
                    String p = fs.getNativeFileSystemPath() + tempPath;
                    new File(p).mkdirs();
                    File f = new File(p, event.getFile().getFileName());
                    event.getFile().write(f.getPath());
                    AcademicInternship internship = getModel().getInternship();

                    VFile userHome = fs.getUserFolder(login).get("MesRapports");
                    userHome.mkdirs();
                    if ("report1".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-spec.pdf";
                        VFile ff = userHome.get(validName);
                        fs.getFileSystem().get(tempPath + "/" + event.getFile().getFileName()).copyTo(ff);
                        internship.setSpecFilePath(ff.getBaseFile("vrfs").getPath());
                    } else if ("report2".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-mid.pdf";
                        VFile ff = userHome.get(validName);
                        fs.getFileSystem().get(tempPath + "/" + event.getFile().getFileName()).copyTo(ff);
                        internship.setMidTermReportFilePath(ff.getBaseFile("vrfs").getPath());

                    } else if ("report3".equals(report)) {
                        String validName = internship.getBoard().getInternshipType().getName() + "-" + internship.getCode() + "-" + login + "-final.pdf";
                        VFile ff = userHome.get(validName);
                        fs.getFileSystem().get(tempPath + "/" + event.getFile().getFileName()).copyTo(ff);
                        internship.setReportFilePath(ff.getBaseFile("vrfs").getPath());
                    } else {
                        return;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MyInternshipBoardsCtrl.class.getName()).log(Level.SEVERE, null, ex);
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
}
