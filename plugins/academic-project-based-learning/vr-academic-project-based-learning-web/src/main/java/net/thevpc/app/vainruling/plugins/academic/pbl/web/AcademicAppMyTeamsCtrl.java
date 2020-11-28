/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.pbl.web;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.thevpc.app.vainruling.plugins.academic.pbl.service.ApblPlugin;
import net.thevpc.app.vainruling.plugins.academic.pbl.service.ApblUtils;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.pbl.model.ApblTeam;
import net.thevpc.app.vainruling.plugins.academic.pbl.model.ApblProgressionLog;
import net.thevpc.app.vainruling.plugins.academic.pbl.model.ApblCoachingLog;
import net.thevpc.app.vainruling.plugins.academic.pbl.model.ApblCoaching;
import net.thevpc.app.vainruling.plugins.academic.pbl.model.ApblTeamMember;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")
            ,
                @VrPathItem(title = "APP", css = "fa-dashboard", ctrl = ""),},
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

    @VrOnPageLoad
    private void onPageLoad() {
        currentTeacher = academic.getCurrentTeacher();
        currentStudent = academic.getCurrentStudent();
        currentUser = core.getCurrentUser();
        currentAdmin = core.isCurrentSessionAdmin();
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

        new DialogBuilder("/modules/academic/pbl/dialog/progression-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
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
            new DialogBuilder("/modules/academic/pbl/dialog/coaching-dialog")
                    .setResizable(true)
                    .setDraggable(true)
                    .setModal(true)
                    .open();
        }
    }

    public void onSaveProgressionLog() {
        try {
            apbl.addProgressionLog(getModel().getSelectedProgressionLog());
            onChangeSelectedTeam();
            fireEventExtraDialogClosed();
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void onSaveCoachingLog() {
        try {
            apbl.addCoachingLog(getModel().getSelectedCoachingLog());
            onChangeSelectedTeam();
            fireEventExtraDialogClosed();
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void onSaveTeam() {
        if (Objects.equals(getModel().getSelectedTeam().getReport(), getModel().getSelectedPathBeforeUpload())
                && !StringUtils.isBlank(getModel().getSelectedPathUploaded())) {
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
                FacesUtils.addErrorMessage(ex);
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
                FacesUtils.addErrorMessage(ex);
            }
        }
    }

    public void reloadTeams() {
        ArrayList<SelectItem> teamItems = new ArrayList<>();
        if (currentUser != null) {
            final List<ApblTeam> teams = apbl.findOpenTeamsByUser(currentUser.getId(), true, false);
            Collections.sort(teams, new Comparator<ApblTeam>() {
                @Override
                public int compare(ApblTeam o1, ApblTeam o2) {
                    int x = ApblUtils.SESSION_COMPARATOR.compare(o1.getSession(), o2.getSession());
                    if (x != 0) {
                        return x;
                    }
                    x = StringUtils.trim(o1.getName()).compareTo(StringUtils.trim(o2.getName()));
                    x = StringUtils.trim(o1.getCode()).compareTo(StringUtils.trim(o2.getCode()));
                    if (x != 0) {
                        return x;
                    }
                    return Integer.compare(o1.getId(), o2.getId());
                }
            });

            for (ApblTeam apblTeam : teams) {
                teamItems.add(FacesUtils.createSelectItem(
                        String.valueOf(apblTeam.getId()),
                        "[" + apblTeam.getName() + "] " + apblTeam.getName() + " (" + apblTeam.getSession().getName() + ")"
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
            getModel().setSelectedTeamMembers(team == null ? new ArrayList<ApblTeamMember>() : apbl.findTeamMembers(team.getId()));
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
        DialogBuilder.closeCurrent();
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
