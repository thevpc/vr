/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.pbl.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.pbl.service.ApblPlugin;
import net.vpc.app.vainruling.plugins.academic.pbl.service.model.*;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = ""),
                @UPathItem(title = "APP", css = "fa-dashboard", ctrl = ""),
        },
//        css = "fa-table",
//        title = "Mes Equipes APP",
        url = "modules/academic/pbl/my-app-teams",
        menu = "/Education/Projects/Apbl",
        securityKey = "Custom.Education.Apbl.MyTeams"
)
public class AcademicAppMyTeamsCtrl {
    public static final Logger log = Logger.getLogger(AcademicAppProjectsCtrl.class.getName());
    @Autowired
    private ApblPlugin apbl;
    @Autowired
    private CorePlugin core;
    @Autowired
    private AcademicPlugin academic;
    private AcademicTeacher currentTeacher;
    private AppUser currentUser;
    private AcademicStudent currentStudent;
    private boolean currentAdmin;
    private boolean updateTeamAllowed;
    private boolean addProgressionLogAllowed;
    private boolean addCoachingLogAllowed;

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    private void onPageLoad() {
        currentTeacher = academic.getCurrentTeacher();
        currentStudent = academic.getCurrentStudent();
        currentUser = UserSession.getCurrentUser();
        currentAdmin = core.isSessionAdmin();
        reloadTeams();
    }

    public void onOpenProgressionLogDialog() {
        getModel().setSelectedProgressionLog(new ApblProgressionLog());
        getModel().setEditMode(false);
        getModel().setViewOnly(false);
        getModel().getSelectedProgressionLog().setUser(currentUser);
        getModel().getSelectedProgressionLog().setTeam(apbl.findTeam(getModel().getSelectedTeamId()));
        getModel().getSelectedProgressionLog().setProgressionDate(new Date());
        for (ApblProgressionLog old : getModel().getProgressionLogs()) {
            getModel().getSelectedProgressionLog().setProgressionPercent(old.getProgressionPercent());
            break;
        }
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/pbl/dialog/progression-dialog", options, null);
    }

    public void onOpenCoachingLogDialog() {
        getModel().setSelectedCoachingLog(new ApblCoachingLog());
        getModel().setEditMode(false);
        getModel().setViewOnly(false);
        ApblCoaching teamCoach = apbl.findTeamCoach(getModel().getSelectedTeamId(), currentTeacher == null ? -1 : currentTeacher.getId());
        if (teamCoach != null) {
            getModel().getSelectedCoachingLog().setCoaching(teamCoach);
            getModel().getSelectedCoachingLog().setDurationMinutes(15);
            getModel().getSelectedCoachingLog().setAppointmentDate(new Date());
            for (ApblCoachingLog old : getModel().getCoachingLogs()) {
                getModel().getSelectedCoachingLog().setDurationMinutes(old.getDurationMinutes());
            }
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("resizable", false);
            options.put("draggable", true);
            options.put("modal", true);
            RequestContext.getCurrentInstance().openDialog("/modules/academic/pbl/dialog/coaching-dialog", options, null);
        }
    }

    public void onSaveProgressionLog() {
        try {
            apbl.addProgressionLog(getModel().getSelectedProgressionLog());
            onChangeSelectedTeam();
            fireEventExtraDialogClosed();
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage("M.A.J échouée.", ex.getMessage());
        }
    }

    public void onSaveCoachingLog() {
        try {
            apbl.addCoachingLog(getModel().getSelectedCoachingLog());
            onChangeSelectedTeam();
            fireEventExtraDialogClosed();
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage("M.A.J échouée.", ex.getMessage());
        }
    }

    public void onSaveTeam() {
        if (
                Objects.equals(getModel().getSelectedTeam().getReport(), getModel().getSelectedPathBeforeUpload())
                        &&
                        !StringUtils.isEmpty(getModel().getSelectedPathUploaded())
                ) {
            getModel().getSelectedTeam().setReport(getModel().getSelectedPathUploaded());
        }
        if (getModel().isEditMode()) {
            try {
                apbl.updateTeam(getModel().getSelectedTeam());
                getModel().setSelectedTeam(null);
                getModel().setSelectedTeamMembers(new ArrayList<>());
                reloadTeams();
                fireEventExtraDialogClosed();
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
                FacesUtils.addErrorMessage("M.A.J échouée.", ex.getMessage());
            }
        } else {
            try {
                apbl.addTeam(getModel().getSelectedTeam());
                getModel().setSelectedTeam(null);
                getModel().setSelectedTeamMembers(new ArrayList<>());
                reloadTeams();
                fireEventExtraDialogClosed();
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
                FacesUtils.addErrorMessage("Création échouée.", ex.getMessage());
            }
        }
    }

    public void reloadTeams() {
        ArrayList<SelectItem> teamItems = new ArrayList<>();
        if (currentUser != null) {
            for (ApblTeam apblTeam : apbl.findOpenTeamsByUser(currentUser.getId(),true,false)) {
                teamItems.add(new SelectItem(
                        apblTeam.getId(),
                        apblTeam.getName()
                ));
            }
        }
        getModel().setTeamItems(teamItems);
        getModel().setSelectedTeamId(null);
        onChangeSelectedTeam();

    }

    public void onChangeSelectedTeam() {
        Integer i = getModel().getSelectedTeamId();
        ApblTeam team = null;
        Set<Integer> coachs = new HashSet<>();
        Set<Integer> members = new HashSet<>();
        if (i == null) {
            getModel().setSelectedTeam(null);
            getModel().setSelectedTeamMembers(new ArrayList<>());
            getModel().setCoachingLogs(new ArrayList<>());
            getModel().setProgressionLogs(new ArrayList<>());
        } else {
            team = apbl.findTeam(i);
            getModel().setSelectedTeam(team);
            getModel().setSelectedTeamMembers(team==null?new ArrayList<ApblTeamMember>() : apbl.findTeamMembers(team.getId()));
            getModel().setCoachingLogs(apbl.findTeamCoachingLog(i));
            getModel().setProgressionLogs(apbl.findTeamProgressionLog(i));
            for (ApblCoaching apblCoaching : apbl.findTeamCoaches(i)) {
                if (apblCoaching.getTeacher() != null && apblCoaching.getTeacher().getUser() != null) {
                    coachs.add(apblCoaching.getTeacher().getUser().getId());
                }
            }
            for (ApblTeamMember apblCoaching : apbl.findTeamMembers(i)) {
                if (apblCoaching.getStudent() != null && apblCoaching.getStudent().getUser() != null) {
                    members.add(apblCoaching.getStudent().getUser().getId());
                }
            }
            if (team != null && team.getOwner() != null) {
                members.add(team.getOwner().getId());
            }
        }
        addCoachingLogAllowed = team != null && ((currentUser != null && coachs.contains(currentUser.getId())));
        addProgressionLogAllowed = team != null && ((team.getOwner() != null && currentUser != null && members.contains(currentUser.getId())));
        updateTeamAllowed = team != null && ((team.getOwner() != null && currentUser != null && currentUser.getId() == team.getOwner().getId()));
    }

    public boolean isUpdateTeamAllowed() {
        return updateTeamAllowed;
    }

    public boolean isAddProgressionLogAllowed() {
        return addProgressionLogAllowed;
    }

    public boolean isAddCoachingLogAllowed() {
        return addCoachingLogAllowed;
    }

    public void fireEventExtraDialogClosed() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public class Model {
        private List<ApblTeam> teams = new ArrayList<>();
        private List<SelectItem> teamItems = new ArrayList<>();
        private List<ApblProgressionLog> progressionLogs = new ArrayList<>();
        private List<ApblCoachingLog> coachingLogs = new ArrayList<>();
        private List<ApblTeamMember> selectedTeamMembers = new ArrayList<>();
        private Integer selectedTeamId;
        private ApblTeam selectedTeam;
        private ApblCoachingLog selectedCoachingLog;
        private ApblProgressionLog selectedProgressionLog;
        private boolean editMode;
        private boolean viewOnly;
        private String selectedPathUploaded;
        private String selectedPathBeforeUpload;

        public ApblCoachingLog getSelectedCoachingLog() {
            return selectedCoachingLog;
        }

        public void setSelectedCoachingLog(ApblCoachingLog selectedCoachingLog) {
            this.selectedCoachingLog = selectedCoachingLog;
        }

        public ApblProgressionLog getSelectedProgressionLog() {
            return selectedProgressionLog;
        }

        public void setSelectedProgressionLog(ApblProgressionLog selectedProgressionLog) {
            this.selectedProgressionLog = selectedProgressionLog;
        }

        public List<ApblProgressionLog> getProgressionLogs() {
            return progressionLogs;
        }

        public void setProgressionLogs(List<ApblProgressionLog> progressionLogs) {
            this.progressionLogs = progressionLogs;
        }

        public List<ApblCoachingLog> getCoachingLogs() {
            return coachingLogs;
        }

        public void setCoachingLogs(List<ApblCoachingLog> coachingLogs) {
            this.coachingLogs = coachingLogs;
        }

        public List<ApblTeam> getTeams() {
            return teams;
        }

        public Integer getSelectedTeamId() {
            return selectedTeamId;
        }

        public void setSelectedTeamId(Integer selectedTeamId) {
            this.selectedTeamId = selectedTeamId;
        }

        public void setTeams(List<ApblTeam> teams) {
            this.teams = teams;
        }

        public List<SelectItem> getTeamItems() {
            return teamItems;
        }

        public void setTeamItems(List<SelectItem> teamItems) {
            this.teamItems = teamItems;
        }

        public ApblTeam getSelectedTeam() {
            return selectedTeam;
        }

        public void setSelectedTeam(ApblTeam selectedTeam) {
            this.selectedTeam = selectedTeam;
        }

        public boolean isEditMode() {
            return editMode;
        }

        public void setEditMode(boolean editMode) {
            this.editMode = editMode;
        }

        public boolean isViewOnly() {
            return viewOnly;
        }

        public void setViewOnly(boolean viewOnly) {
            this.viewOnly = viewOnly;
        }

        public String getSelectedPathUploaded() {
            return selectedPathUploaded;
        }

        public void setSelectedPathUploaded(String selectedPathUploaded) {
            this.selectedPathUploaded = selectedPathUploaded;
        }

        public String getSelectedPathBeforeUpload() {
            return selectedPathBeforeUpload;
        }

        public void setSelectedPathBeforeUpload(String selectedPathBeforeUpload) {
            this.selectedPathBeforeUpload = selectedPathBeforeUpload;
        }

        public List<ApblTeamMember> getSelectedTeamMembers() {
            return selectedTeamMembers;
        }

        public void setSelectedTeamMembers(List<ApblTeamMember> selectedTeamMembers) {
            this.selectedTeamMembers = selectedTeamMembers;
        }
    }
}
