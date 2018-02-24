/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.pbl.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.fs.files.DocumentsDialogCtrl;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import net.vpc.app.vainruling.plugins.academic.pbl.service.ApblPlugin;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.*;
import net.vpc.app.vainruling.plugins.academic.pbl.service.model.*;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringComparators;
import net.vpc.common.strings.StringTransforms;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.UPA;
import net.vpc.upa.filters.ObjectFilter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
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
//        title = "Projets APP",
        url = "modules/academic/pbl/app-projects",
        menu = "/Education/Projects/Apbl",
        securityKey = "Custom.Education.Apbl.Projects"
)
public class AcademicAppProjectsCtrl {
    public static final Logger log = Logger.getLogger(AcademicAppProjectsCtrl.class.getName());
    @Autowired
    private ApblPlugin apbl;
    @Autowired
    private CorePlugin core;
    @Autowired
    private AcademicPlugin academic;

    private Model model = new Model();
    private AcademicTeacher currentTeacher;
    private AppUser currentUser;
    private AcademicStudent currentStudent;
    private boolean currentAdmin;
    private boolean currentJoinAnyAllowed;
    private boolean currentAddTeamAllowed;
    private boolean currentAddProjectAllowed;
    private boolean currentLockAllowed;

    public Model getModel() {
        return model;
    }


    public boolean isAllowed(String action) {
        if ("CreateProject".equals(action)) {
            return currentAddProjectAllowed;
        } else if ("CreateTeam".equals(action)) {
            return currentAddTeamAllowed;
        } else if ("Shuffle".equals(action)) {
            return currentAdmin;
        } else if ("Lock".equals(action)) {
            return currentAdmin;
        }
        return false;
    }

    @OnPageLoad
    private void onPageLoad() {
        currentTeacher = academic.getCurrentTeacher();
        currentStudent = academic.getCurrentStudent();
        currentUser = core.getCurrentUser();
        currentAdmin = core.isCurrentSessionAdmin();
        List<ApblSession> sessions = apbl.findAvailableSessions();
        getModel().getSessionsMap().clear();
        getModel().getSessionItems().clear();
        for (ApblSession session : sessions) {
            getModel().getSessionsMap().put(session.getId(), session);
            getModel().getSessionItems().add(FacesUtils.createSelectItem(String.valueOf(session.getId()), session.getName()));
        }
        getModel().setCurrentSessionId(null);
        getModel().setProjects(new ArrayList<>());
        if (sessions.size() > 0) {
            getModel().setCurrentSessionId(sessions.get(0).getId());
        } else {
            getModel().setCurrentSessionId(null);
        }
        onSearchByText();
        onUpdateSession();
    }

    public void onSearchByText() {
        if (getModel().getSession() == null) {
            getModel().setProjects(new ArrayList<>());
            getModel().setProjectItems(new ArrayList<>());

        } else {
            List<ProjectNode> projectNodes =
                    apbl.findProjectNodes(getModel().getSession().getId(),
                            StringUtils.isEmpty(getModel().getFilterText()) ? null : StringComparators.ilikepart(getModel().getFilterText()).apply(StringTransforms.UNIFORM),
                            new ObjectFilter<ApblNode>() {
                                @Override
                                public boolean accept(ApblNode value) {
                                    if (value instanceof TeamNode) {
                                        if (!((TeamNode) value).getCoaches().isEmpty() && getModel().isFilterNoCoach()) {
                                            return false;
                                        }
                                        if (!((TeamNode) value).getMembers().isEmpty() && getModel().isFilterNoMember()) {
                                            return false;
                                        }
                                        return true;
                                    }
                                    return true;
                                }
                            }
                    );
            getModel().setProjects(projectNodes);
            ArrayList<SelectItem> projectItems = new ArrayList<>();
            HashMap<Integer, ApblProject> projectsMap = new HashMap<>();
            for (ApblProject apblProject : apbl.findProjects(getModel().getSession().getId())) {
                projectItems.add(FacesUtils.createSelectItem(String.valueOf(apblProject.getId()), Vr.get().strcut(apblProject.getName(), 80)));
                projectsMap.put(apblProject.getId(), apblProject);
            }
            getModel().setProjectItems(projectItems);
            getModel().setProjectsMap(projectsMap);
        }
    }

    public boolean isTeamNode(NodeItem node) {
        return node.getValue() instanceof TeamNode;
    }

    public boolean isProjectNode(NodeItem node) {
        return node.getValue() instanceof ProjectNode;
    }

    public boolean isAdmin() {
        return currentAdmin;
    }

    public void onUpdateSession() {
        onSearchByText();
        ApblSession session = getModel().getSession();
        if (currentAdmin) {
            currentAddProjectAllowed = true;
            currentAddTeamAllowed = true;
            currentJoinAnyAllowed = true;
            currentLockAllowed = true;
        } else {
            currentAddProjectAllowed = false;
            currentAddTeamAllowed = false;
            currentJoinAnyAllowed = false;
            currentLockAllowed = false;
            if (session != null) {
                currentAddProjectAllowed = core.userMatchesProfileFilter(currentUser.getId(), session.getProjectOwnerProfiles());
                if (currentAddProjectAllowed) {
                    if (session.getStatus() != null && !session.getStatus().isAllowAddProject()) {
                        currentAddProjectAllowed = false;
                    }
                }
                if (currentAddProjectAllowed) {
                    if (!UPA.getPersistenceUnit().getSecurityManager().isAllowedPersist(UPA.getPersistenceUnit().getEntity(ApblProject.class))) {
                        currentAddProjectAllowed = false;
                    }
                }
                currentAddTeamAllowed = core.userMatchesProfileFilter(currentUser.getId(), session.getTeamOwnerProfiles());
                if (currentAddTeamAllowed) {
                    if (session.getStatus() != null && !session.getStatus().isAllowAddTeam()) {
                        currentAddTeamAllowed = false;
                    }
                }
                if (currentAddTeamAllowed) {
                    if (!UPA.getPersistenceUnit().getSecurityManager().isAllowedPersist(UPA.getPersistenceUnit().getEntity(ApblTeam.class))) {
                        currentAddTeamAllowed = false;
                    }
                }

                if (currentTeacher != null && currentTeacher.getUser() != null) {
                    currentJoinAnyAllowed = core.userMatchesProfileFilter(currentTeacher.getUser().getId(), session.getCoachProfiles());
                    if (currentJoinAnyAllowed) {
                        if (session.getStatus() != null && !session.getStatus().isAllowAddCoach()) {
                            currentJoinAnyAllowed = false;
                        }
                    }
                } else if (currentStudent != null && currentStudent.getUser() != null) {
                    currentJoinAnyAllowed = core.userMatchesProfileFilter(currentStudent.getUser().getId(), session.getMemberProfiles());
                    if (currentJoinAnyAllowed) {
                        if (session.getStatus() != null && !session.getStatus().isAllowAddMember()) {
                            currentJoinAnyAllowed = false;
                        }
                    }
                }
            }
        }
    }

    public void openDocumentsDialog() {
        DocumentsDialogCtrl.Config c = new DocumentsDialogCtrl.Config();
        c.setPath(getModel().getSelectedProject().getSpecFilePath());
        c.setUserInfo("");
        VrApp.getBean(DocumentsDialogCtrl.class).openDialog(c);
    }

    public void onDocumentsDialogClosed(SelectEvent event) {
        DialogResult o = (DialogResult) event.getObject();
        getModel().getSelectedProject().setSpecFilePath((String) o.getValue());
    }

    public void reloadProjects() {
//        getModel().setProjects(new ArrayList<>());
//        if (getModel().getSession() != null) {
        onSearchByText();
//        }
    }

    public void onShuffleSession() {
        if (getModel().getSession() != null) {
            apbl.shuffleSession(getModel().getSession().getId());
            reloadProjects();
        }
    }

    public void onUnShuffleSession() {
        if (getModel().getSession() != null) {
            apbl.removeShuffleSession(getModel().getSession().getId());
            reloadProjects();
        }
    }


    public boolean isJoinAllowed(NodeItem item) {
        if (item != null) {
            ApblSession session = getModel().getSession();
            String type = item.getType();
            if ("team".equals(type)) {
                if (currentTeacher != null) {
                    if (!currentJoinAnyAllowed) {
                        return false;
                    }
                    TeamNode t = (TeamNode) item.getValue();
                    if (t.getTeam().isLockedCoaches()) {
                        return false;
                    }
                    int teamCoachMax = session.getTeamCoachMax();
                    if (teamCoachMax > 0 && t.getCoaches().size() >= teamCoachMax) {
                        return false;
                    }
                    for (CoachNode node : t.getCoaches()) {
                        if (node.getCoaching().getTeacher().getId() == currentTeacher.getId()) {
                            return false;
                        }
                    }
                    return true;
                }
                if (currentStudent != null) {
                    if (!currentJoinAnyAllowed) {
                        return false;
                    }
                    TeamNode t = (TeamNode) item.getValue();
                    if (t.getTeam().isLockedMembers()) {
                        return false;
                    }
                    if (!t.getTeam().isFreeMembers()) {
                        int teamCoachMax = session.getTeamMemberMax();
                        if (teamCoachMax > 0 && t.getMembers().size() >= teamCoachMax) {
                            return false;
                        }
                        for (MemberNode node : t.getMembers()) {
                            if (node.getMember().getStudent().getId() == currentStudent.getId()) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isLockableMembers(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            if ("team".equals(type)) {
                TeamNode t = (TeamNode) item.getValue();
                return !t.getTeam().isLockedMembers();
            }
        }
        return false;
    }

    public boolean isUnlockableMembers(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            if ("team".equals(type)) {
                TeamNode t = (TeamNode) item.getValue();
                return t.getTeam().isLockedMembers();
            }
        }
        return false;
    }

    public boolean isLockableCoaches(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            if ("team".equals(type)) {
                TeamNode t = (TeamNode) item.getValue();
                return !t.getTeam().isLockedCoaches();
            }
        }
        return false;
    }

    public boolean isUnlockableCoaches(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            if ("team".equals(type)) {
                TeamNode t = (TeamNode) item.getValue();
                return t.getTeam().isLockedCoaches();
            }
        }
        return false;
    }

    public boolean isLeaveAllowed(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            ApblSession session = getModel().getSession();
            if ("coach".equals(type)) {
                if (currentAdmin) {
                    return true;
                }
                if (!session.getStatus().isAllowRemoveCoach()) {
                    return false;
                }
                CoachNode t = (CoachNode) item.getValue();
                if (currentTeacher != null) {
                    if (t.getCoaching().getTeacher().getId() == currentTeacher.getId()) {
                        ApblTeam tt = apbl.findTeam(t.getCoaching().getTeam().getId());
                        return tt != null && !tt.isLockedCoaches();
                    }
                }
                return false;
            } else if ("member".equals(type)) {
                if (currentAdmin) {
                    return true;
                }
                if (!session.getStatus().isAllowRemoveMember()) {
                    return false;
                }
                if (currentStudent != null) {
                    MemberNode t = (MemberNode) item.getValue();
                    if (t.getMember().getStudent().getId() == currentStudent.getId()) {
                        ApblTeam tt = apbl.findTeam(t.getMember().getTeam().getId());
                        return tt != null && !tt.isLockedMembers();
                    }
                }
                return false;
            } else if ("project".equals(type)) {
                ProjectNode t = (ProjectNode) item.getValue();
                if (t.getProject() == null) {
                    return false;
                }
                if (!session.getStatus().isAllowRemoveProject()) {
                    return false;
                }
                if (currentUser != null) {
                    if (t.getProject().getOwner() != null) {
                        if (t.getProject().getOwner().getId() == currentUser.getId()) {
                            return true;
                        }
                    }
                }
                if (currentAdmin) {
                    return true;
                }
            } else if ("team".equals(type)) {
                TeamNode t = (TeamNode) item.getValue();
                if (currentAdmin) {
                    return true;
                }
                if (!session.getStatus().isAllowRemoveTeam()) {
                    return false;
                }
                if (currentUser != null) {
                    if (t.getTeam().getOwner() != null) {
                        if (t.getTeam().getOwner().getId() == currentUser.getId()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void onJoin(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            ApblSession session = getModel().getSession();
            if ("team".equals(type)) {
                TeamNode value = (TeamNode) item.getValue();
                if (currentTeacher != null) {
                    if (!currentJoinAnyAllowed) {
                        return;
                    }
                    if (!session.getStatus().isAllowAddCoach()) {
                        return;
                    }
                    if (value.getTeam().isLockedCoaches()) {
                        return;
                    }
                    ApblCoaching apblCoaching = apbl.addTeamCoach(value.getTeam().getId(), currentTeacher.getId());
                    if (apblCoaching != null) {
                        value.getCoaches().add(new CoachNode(apblCoaching));
                    }
                } else if (currentStudent != null) {
                    if (!currentJoinAnyAllowed) {
                        return;
                    }
                    if (!session.getStatus().isAllowAddMember()) {
                        return;
                    }
                    if (value.getTeam().isLockedMembers()) {
                        return;
                    }
                    if (!value.getTeam().isFreeMembers()) {
                        ApblPlugin.TeamConstraintsChecker c = new ApblPlugin.TeamConstraintsChecker(session.getId());
                        if (!c.isAutoAddableMember(value, currentStudent.getId())) {
                            return;
                        }
                    }
                    ApblTeamMember member = apbl.addTeamMember(value.getTeam().getId(), currentStudent.getId());
                    if (member != null) {
                        value.getMembers().add(new MemberNode(member));
                    }
                }
            }
        }

    }

    public void onJoinTeacher(NodeItem item, int teacherId) {
        if (item != null && teacherId >= 0) {
            String type = item.getType();
            ApblSession session = getModel().getSession();
            if ("team".equals(type)) {
                TeamNode value = (TeamNode) item.getValue();
//                if (currentTeacher != null) {
//                    if (!currentJoinAnyAllowed) {
//                        return;
//                    }
                if (!session.getStatus().isAllowAddCoach()) {
                    return;
                }
                if (value.getTeam().isLockedCoaches()) {
                    return;
                }
                ApblCoaching apblCoaching = apbl.addTeamCoach(value.getTeam().getId(), teacherId);
                if (apblCoaching != null) {
                    value.getCoaches().add(new CoachNode(apblCoaching));
                }
//                } else if (currentStudent != null) {
//                    if (!currentJoinAnyAllowed) {
//                        return;
//                    }
//                    if(!session.getStatus().isAllowAddMember()){
//                        return;
//                    }
//                    if(value.getTeam().isLockedMembers()){
//                        return;
//                    }
//                    if(!value.getTeam().isFreeMembers()){
//                        ApblPlugin.TeamConstraintsChecker c=new ApblPlugin.TeamConstraintsChecker(session.getId());
//                        if(!c.isAutoAddableMember(value,currentStudent.getId())){
//                            return;
//                        }
//                    }
//                    ApblTeamMember member = apbl.addTeamMember(value.getTeam().getId(), currentStudent.getId());
//                    if (member != null) {
//                        value.getMembers().add(new MemberNode(member));
//                    }
//                }
            }
        }

    }

    public void onJoinStudent(NodeItem item, int studentId) {
        if (item != null && studentId >= 0) {
            String type = item.getType();
            ApblSession session = getModel().getSession();
            if ("team".equals(type)) {
                TeamNode value = (TeamNode) item.getValue();
//                if (currentTeacher != null) {
////                    if (!currentJoinAnyAllowed) {
////                        return;
////                    }
//                    if (!session.getStatus().isAllowAddCoach()) {
//                        return;
//                    }
//                    if (value.getTeam().isLockedCoaches()) {
//                        return;
//                    }
//                    ApblCoaching apblCoaching = apbl.addTeamCoach(value.getTeam().getId(), studentId);
//                    if (apblCoaching != null) {
//                        value.getCoaches().add(new CoachNode(apblCoaching));
//                    }
//                } else
//                if (currentStudent != null) {
//                if (!currentJoinAnyAllowed) {
//                    return;
//                }
                if (!session.getStatus().isAllowAddMember()) {
                    return;
                }
                if (value.getTeam().isLockedMembers()) {
                    return;
                }
                if (!value.getTeam().isFreeMembers()) {
                    ApblPlugin.TeamConstraintsChecker c = new ApblPlugin.TeamConstraintsChecker(session.getId());
                    if (!c.isAutoAddableMember(value, studentId)) {
                        return;
                    }
                }
                ApblTeamMember member = apbl.addTeamMember(value.getTeam().getId(), studentId);
                if (member != null) {
                    value.getMembers().add(new MemberNode(member));
                }
//                }
            }
        }

    }

    public void onLockMembers(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            try {
                if ("team".equals(type)) {
                    TeamNode team = (TeamNode) item.getValue();
                    team.getTeam().setLockedMembers(true);
                    apbl.updateTeam(team.getTeam());
                }
            } catch (Exception e) {
                e.printStackTrace();
                FacesUtils.addErrorMessage(e);
            }
        }
    }

    public void onLockCoaches(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            try {
                if ("team".equals(type)) {
                    TeamNode team = (TeamNode) item.getValue();
                    team.getTeam().setLockedCoaches(true);
                    apbl.updateTeam(team.getTeam());
                }
            } catch (Exception e) {
                e.printStackTrace();
                FacesUtils.addErrorMessage(e);
            }
        }
    }

    public void onUnlockMembers(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            try {
                if ("team".equals(type)) {
                    TeamNode team = (TeamNode) item.getValue();
                    team.getTeam().setLockedMembers(false);
                    apbl.updateTeam(team.getTeam());
                }
            } catch (Exception e) {
                e.printStackTrace();
                FacesUtils.addErrorMessage(e);
            }
        }
    }

    public void onUnlockCoaches(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            try {
                if ("team".equals(type)) {
                    TeamNode team = (TeamNode) item.getValue();
                    team.getTeam().setLockedCoaches(false);
                    apbl.updateTeam(team.getTeam());
                }
            } catch (Exception e) {
                e.printStackTrace();
                FacesUtils.addErrorMessage(e);
            }
        }
    }

    public void onRemove(NodeItem item) {
        if (item != null) {
            String type = item.getType();
            try {
                if ("project".equals(type)) {
                    ProjectNode team = (ProjectNode) item.getValue();
                    apbl.removeProject(team.getProject().getId());
                } else if ("team".equals(type)) {
                    TeamNode team = (TeamNode) item.getValue();
                    apbl.removeTeam(team.getTeam().getId());
                } else if ("coach".equals(type)) {
                    CoachNode team = (CoachNode) item.getValue();
                    apbl.removeTeamCoach(team.getCoaching().getTeam().getId(), team.getCoaching().getTeacher().getId());
                } else if ("member".equals(type)) {
                    MemberNode team = (MemberNode) item.getValue();
                    apbl.removeTeamMember(team.getMember().getTeam().getId(), team.getMember().getStudent().getId());
                }
                reloadProjects();
            } catch (Exception e) {
                e.printStackTrace();
                FacesUtils.addErrorMessage(e);
            }
        }
    }

    public void onOpenCreateProjectDialog() {
        getModel().setSelectedProject(new ApblProject());
        getModel().setEditMode(false);
        getModel().setViewOnly(false);
        getModel().getSelectedProject().setOwner(currentUser);
        getModel().getSelectedProject().setSession(getModel().getSession());
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/pbl/dialog/project-edit-dialog", options, null);
    }

    public void onOpenCreateTeamDialog() {
        getModel().setSelectedTeam(new ApblTeam());
        getModel().setViewOnly(false);
        getModel().setEditMode(false);
        getModel().getSelectedTeam().setOwner(core.getCurrentUser());
        getModel().getSelectedTeam().setSession(getModel().getSession());
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/academic/pbl/dialog/team-edit-dialog", options, null);
    }

    public void onOpenViewDialog(NodeItem node) {
        if (node.getValue() instanceof ProjectNode) {
            ApblProject project = ((ProjectNode) node.getValue()).getProject();
            getModel().setSelectedProject(project);
            getModel().setEditMode(true);
            AppUser currentUser = core.getCurrentUser();
            getModel().setViewOnly(!(currentAdmin || currentUser != null && project != null && project.getOwner() != null && project.getOwner().getId() == currentUser.getId()));
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("resizable", false);
            options.put("draggable", true);
            options.put("modal", true);
            RequestContext.getCurrentInstance().openDialog("/modules/academic/pbl/dialog/project-edit-dialog", options, null);
        } else if (node.getValue() instanceof TeamNode) {
            ApblTeam team = ((TeamNode) node.getValue()).getTeam();
            getModel().setViewOnly(!(currentAdmin || currentStudent != null && team != null && team.getOwner() != null && currentStudent.getUser() != null && team.getOwner().getId() == currentStudent.getUser().getId()));
            getModel().setSelectedTeam(team);
            getModel().setEditMode(true);
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("resizable", false);
            options.put("draggable", true);
            options.put("modal", true);
            RequestContext.getCurrentInstance().openDialog("/modules/academic/pbl/dialog/team-edit-dialog", options, null);
        }
    }

    public void onOpenAssignCoachDialog(NodeItem node) {
        if (node.getValue() instanceof ProjectNode) {
            //do nothing
        } else if (node.getValue() instanceof TeamNode) {
            TeamNode teamNode = (TeamNode) node.getValue();
            ApblTeam team = teamNode.getTeam();
            getModel().setViewOnly(!currentAdmin);
            getModel().setSelectedTeam(team);
            getModel().setSelectedTeamNode(teamNode);
            getModel().setEditMode(true);
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("resizable", false);
            options.put("draggable", true);
            options.put("modal", true);
            RequestContext.getCurrentInstance().openDialog("/modules/academic/pbl/dialog/team-assign-dialog", options, null);
        }
    }

    public void onAddCoach() {
        TeamNode value = getModel().getSelectedTeamNode();
        if(getModel().getNewCoach()!=null) {
            ApblCoaching apblCoaching = apbl.addTeamCoach(value.getTeam().getId(), getModel().getNewCoach().getId());
            if (apblCoaching != null) {
                value.getCoaches().add(new CoachNode(apblCoaching));
            }
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
                getModel().setSelectedProject(null);
                reloadProjects();
                fireEventExtraDialogClosed();
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
                FacesUtils.addErrorMessage(ex);
            }
        } else {
            try {
                apbl.addTeam(getModel().getSelectedTeam());
                getModel().setSelectedProject(null);
                reloadProjects();
                fireEventExtraDialogClosed();
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
                FacesUtils.addErrorMessage(ex);
            }
        }
    }

    public void onSaveProject() {
        if (
                Objects.equals(getModel().getSelectedProject().getSpecFilePath(), getModel().getSelectedPathBeforeUpload())
                        &&
                        !StringUtils.isEmpty(getModel().getSelectedPathUploaded())
                ) {
            getModel().getSelectedProject().setSpecFilePath(getModel().getSelectedPathUploaded());
        }
        if (getModel().isEditMode()) {
            try {
                apbl.updateProject(getModel().getSelectedProject());
                getModel().setSelectedProject(null);
                reloadProjects();
                fireEventExtraDialogClosed();
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
                FacesUtils.addErrorMessage(ex);
            }
        } else {
            try {
                apbl.addProject(getModel().getSelectedProject());
                getModel().setSelectedProject(null);
                reloadProjects();
                fireEventExtraDialogClosed();
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
                FacesUtils.addErrorMessage(ex);
            }
        }
    }

    public void onHandleProjectFileUpload(FileUploadEvent event) {
        try {
            ApblProject p = getModel().getSelectedProject();
            ApblSession s = p.getSession();
            VFile file = VrWebHelper.handleFileUpload(event, "/Documents/Services/Ext/pbl/" + s.getId() + "-" + s.getName() + "/Projects/" + p.getId() + "-" + p.getName() + "/*", false, true);
            if (file != null) {
                getModel().setSelectedPathBeforeUpload(p.getSpecFilePath());
                getModel().setSelectedPathUploaded(file.getPath());
                p.setSpecFilePath(file.getPath());
            }
            RequestContext.getCurrentInstance().update("myform:pathComp");

        } catch (Exception ex) {
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void onHandleTeamFileUpload(FileUploadEvent event) {
        try {
            ApblTeam p = getModel().getSelectedTeam();
            ApblSession s = p.getSession();
            VFile file = VrWebHelper.handleFileUpload(event, "/Documents/Services/Ext/pbl/" + s.getId() + "-" + s.getName() + "/Teams/" + p.getId() + "-" + p.getName() + "/*", false, true);
            if (file != null) {
                getModel().setSelectedPathBeforeUpload(p.getReport());
                getModel().setSelectedPathUploaded(file.getPath());
                p.setReport(file.getPath());
            }
//            RequestContext.getCurrentInstance().update("myform:pathComp");

        } catch (Exception ex) {
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void fireEventExtraDialogClosed() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public class NodeItem {
        String name;
        String type;
        String owner;
        Object value;
        int childrenCount1;
        int childrenCount2;
        int childrenCount3;
        int childrenCount4;

        public NodeItem(String name, int childrenCount1, String type, String owner, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.owner = owner;
            this.childrenCount1 = childrenCount1;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public int getChildrenCount1() {
            return childrenCount1;
        }

        public void setChildrenCount1(int childrenCount1) {
            this.childrenCount1 = childrenCount1;
        }

        public int getChildrenCount2() {
            return childrenCount2;
        }

        public void setChildrenCount2(int childrenCount2) {
            this.childrenCount2 = childrenCount2;
        }

        public int getChildrenCount3() {
            return childrenCount3;
        }

        public void setChildrenCount3(int childrenCount3) {
            this.childrenCount3 = childrenCount3;
        }

        public int getChildrenCount4() {
            return childrenCount4;
        }

        public NodeItem setChildrenCount4(int childrenCount4) {
            this.childrenCount4 = childrenCount4;
            return this;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

//    public void collapsingORexpanding(TreeNode n, boolean option) {
//        if(n.getChildren().size() == 0) {
//            n.setSelected(false);
//        }
//        else {
//            for(TreeNode s: n.getChildren()) {
//                collapsingORexpanding(s, option);
//            }
//            n.setExpanded(option);
//            n.setSelected(false);
//        }
//    }

    public class Model {
        ApblSession session;
        List<ProjectNode> projects = new ArrayList<>();
        Map<Integer, ApblProject> projectsMap = new HashMap<>();
        Map<Integer, ApblSession> sessionsMap = new HashMap<>();
        List<SelectItem> projectItems = new ArrayList<>();
        List<SelectItem> sessionItems = new ArrayList<>();
        List<SelectItem> teacherItems = new ArrayList<>();
        List<SelectItem> sItems = new ArrayList<>();
        Integer currentTeamProject = null;
        AcademicTeacher newCoach = null;
        Integer currentSessionId = null;
        private TreeNode root;
        private ApblProject selectedProject;
        private ApblTeam selectedTeam;
        private TeamNode selectedTeamNode;
        private boolean editMode;
        private boolean viewOnly;
        private String selectedPathUploaded;
        private String selectedPathBeforeUpload;
        private String filterText;
        private boolean filterNoCoach;
        private boolean filterNoMember;

        public AcademicTeacher getNewCoach() {
            return newCoach;
        }

        public void setNewCoach(AcademicTeacher newCoach) {
            this.newCoach = newCoach;
        }

        public TeamNode getSelectedTeamNode() {
            return selectedTeamNode;
        }

        public void setSelectedTeamNode(TeamNode selectedTeamNode) {
            this.selectedTeamNode = selectedTeamNode;
        }

        public boolean isFilterNoMember() {
            return filterNoMember;
        }

        public void setFilterNoMember(boolean filterNoMember) {
            this.filterNoMember = filterNoMember;
        }

        public boolean isFilterNoCoach() {
            return filterNoCoach;
        }

        public void setFilterNoCoach(boolean filterNoCoach) {
            this.filterNoCoach = filterNoCoach;
        }

        public Map<Integer, ApblSession> getSessionsMap() {
            return sessionsMap;
        }

        public void setSessionsMap(Map<Integer, ApblSession> sessionsMap) {
            this.sessionsMap = sessionsMap;
        }

        public Map<Integer, ApblProject> getProjectsMap() {
            return projectsMap;
        }

        public void setProjectsMap(Map<Integer, ApblProject> projectsMap) {
            this.projectsMap = projectsMap;
        }

        public List<SelectItem> getSessionItems() {
            return sessionItems;
        }

        public void setSessionItems(List<SelectItem> sessionItems) {
            this.sessionItems = sessionItems;
        }

        public Integer getCurrentSessionId() {
            return currentSessionId;
        }

        public void setCurrentSessionId(Integer currentSessionId) {
            this.currentSessionId = currentSessionId;
            session = sessionsMap.get(currentSessionId);
        }

        public List<SelectItem> getProjectItems() {
            return projectItems;
        }

        public void setProjectItems(List<SelectItem> projectItems) {
            this.projectItems = projectItems;
        }

        public Integer getCurrentTeamProject() {
            return currentTeamProject;
        }

        public void setCurrentTeamProject(Integer currentTeamProject) {
            this.currentTeamProject = currentTeamProject;
            if (selectedTeam != null) {
                selectedTeam.setProject(projectsMap.get(currentTeamProject));
            }
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

        public ApblProject getSelectedProject() {
            return selectedProject;
        }

        public void setSelectedProject(ApblProject selectedProject) {
            this.selectedProject = selectedProject;
            setSelectedPathBeforeUpload(null);
            setSelectedPathUploaded(null);
        }

        public boolean isEditMode() {
            return editMode;
        }

        public void setEditMode(boolean editMode) {
            this.editMode = editMode;
        }

        public ApblTeam getSelectedTeam() {
            return selectedTeam;
        }

        public void setSelectedTeam(ApblTeam selectedTeam) {
            this.selectedTeam = selectedTeam;
            setSelectedPathBeforeUpload(null);
            setSelectedPathUploaded(null);
            setCurrentTeamProject(selectedTeam.getProject() != null ? selectedTeam.getProject().getId() : null);
        }

        public TreeNode getRoot() {
            return root;
        }

        public void setRoot(TreeNode root) {
            this.root = root;
        }

        public ApblSession getSession() {
            return session;
        }

        public void setSession(ApblSession session) {
            this.session = session;
        }

        public List<ProjectNode> getProjects() {
            return projects;
        }

        public void setProjects(List<ProjectNode> projects) {
            this.projects = projects;
            root = new DefaultTreeNode(new NodeItem("Root", projects.size(), "root", "", null), null);
            boolean defaultExpand = true;// !net.vpc.common.strings.StringUtils.isEmpty(getFilterText());
            for (ProjectNode i : projects) {
                NodeItem project =
                        i.getProject() == null ?
                                new NodeItem(
                                        "<Equipes Sans Projets>", i.getTeams().size()
                                        , "project", "", i)
                                :
                                new NodeItem(
                                        i.getProject().getName(), i.getTeams().size()
                                        , "project", (i.getProject().getOwner() == null ? null : i.getProject().getOwner().resolveFullTitle()), i);
                DefaultTreeNode n = new DefaultTreeNode(project, this.root);
                n.setExpanded(defaultExpand);
                HashSet<Integer> teachersByProject = new HashSet<>();
                HashSet<Integer> studentsByProject = new HashSet<>();
                for (TeamNode teamNode : i.getTeams()) {
                    NodeItem team = new NodeItem(teamNode.getTeam().getName(), -1, "team", (teamNode.getTeam().getOwner()==null?"":teamNode.getTeam().getOwner().resolveFullTitle()), teamNode);
                    HashSet<Integer> teachersByTeam = new HashSet<>();
                    HashSet<Integer> studentsByTeam = new HashSet<>();
                    DefaultTreeNode t = new DefaultTreeNode(team, n);
                    t.setExpanded(defaultExpand);
                    //if (teamNode.getCoaches().size() > 0) {
                    NodeItem data = new NodeItem("Coachs", 0, "coachingFolder", "", "");
                    data.setChildrenCount2(teamNode.getCoaches().size());
                    DefaultTreeNode d = new DefaultTreeNode(data, t);
                    d.setExpanded(defaultExpand);

                    for (CoachNode coachNode : teamNode.getCoaches()) {
                        teachersByProject.add(coachNode.getCoaching().getTeacher().getId());
                        teachersByTeam.add(coachNode.getCoaching().getTeacher().getId());
                        DefaultTreeNode c = new DefaultTreeNode(new NodeItem(coachNode.getCoaching().getTeacher().resolveFullTitle(), 0, "coach", "", coachNode), d);
                        d.setExpanded(defaultExpand);
                    }
                    //}
                    //if (teamNode.getMembers().size() > 0) {
                    NodeItem membersNodeData = new NodeItem("Membres", 0, "membersFolder", "", "");
                    membersNodeData.setChildrenCount3(teamNode.getMembers().size());
                    DefaultTreeNode membersNode = new DefaultTreeNode(membersNodeData, t);
                    membersNode.setExpanded(defaultExpand);
                    for (MemberNode coachNode : teamNode.getMembers()) {
                        studentsByProject.add(coachNode.getMember().getStudent().getId());
                        studentsByTeam.add(coachNode.getMember().getStudent().getId());
                        DefaultTreeNode c = new DefaultTreeNode(new NodeItem(coachNode.getMember().getStudent().resolveFullTitle(), 0, "member", "", coachNode), membersNode);
                        c.setExpanded(defaultExpand);
                    }
                    //}
                    team.setChildrenCount2(teachersByTeam.size());
                    team.setChildrenCount3(studentsByTeam.size());
                    team.setChildrenCount4(teamNode.getUnsatisfiedTeamConstraints());
                }
                project.setChildrenCount2(teachersByProject.size());
                project.setChildrenCount3(studentsByProject.size());

            }
        }

        public String getFilterText() {
            return filterText;
        }

        public void setFilterText(String filterText) {
            this.filterText = filterText;
        }
    }

}
