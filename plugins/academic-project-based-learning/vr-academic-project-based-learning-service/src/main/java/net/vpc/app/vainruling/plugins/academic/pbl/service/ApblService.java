package net.vpc.app.vainruling.plugins.academic.pbl.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.CoachNode;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.MemberNode;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.ProjectNode;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.TeamNode;
import net.vpc.app.vainruling.plugins.academic.pbl.service.model.*;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.QueryHints;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 9/25/16.
 */
@AppPlugin
public class ApblService {
    @Autowired
    private CorePlugin core;
    @Autowired
    private AcademicPlugin academic;

    public List<ApblTeam> findTeamsByOwner(int sessionId, int userId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblTeam u where u.ownerId=:ownerId and u.sessionId=:sessionId")
                .setParameter("sessionId", sessionId)
                .setParameter("ownerId", userId)
                .getResultList();
    }

    public void removeTeamMember(int teamId, int studentId) {
        ApblTeamMember old = findTeamMember(teamId, studentId);
        if (old != null) {
            AcademicStudent student = academic.getCurrentStudent();
            if(!core.isSessionAdmin() && student!=null && student.getId()==old.getStudent().getId()) {
                UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        UPA.getPersistenceUnit().remove(old);
                    }
                });
            }else {
                UPA.getPersistenceUnit().remove(old);
            }
        }
    }

    public void removeProject(int projectId) {
        AcademicTeacher teacher = academic.getCurrentTeacher();
        ApblProject project = findProject(projectId);
        if (project == null) {
            return;
        }
        AcademicTeacher owner = project.getOwner();
        boolean forceAllowed = false;
        if (owner != null && teacher != null && owner.getId() == teacher.getId()) {
            forceAllowed = true;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (forceAllowed) {
            pu.invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    for (ApblTeam team : findTeamsByProject(projectId)) {
                        team.setProject(null);
                        pu.merge(team);
                    }
                }
            });
        } else {
            for (ApblTeam team : findTeamsByProject(projectId)) {
                team.setProject(null);
                pu.merge(team);
            }
        }
    }

    public void removeTeam(int teamId) {
        AppUser user = core.getUserSession().getUser();
        ApblTeam team = findTeam(teamId);
        if (team == null) {
            return;
        }
        boolean forceAllowed = false;
        AppUser owner = team.getOwner();
        if (owner != null && user != null && owner.getId() == user.getId()) {
            forceAllowed = true;
        }
        if (!core.isSessionAdmin() && forceAllowed) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            pu.invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    for (ApblCoaching apblCoaching : findTeamCoaches(teamId)) {
                        pu.remove(apblCoaching);
                    }
                    for (ApblTeamMember apblCoaching : findTeamMembers(teamId)) {
                        pu.remove(apblCoaching);
                    }
                }
            });
        } else {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            for (ApblCoaching apblCoaching : findTeamCoaches(teamId)) {
                pu.remove(apblCoaching);
            }
            for (ApblTeamMember apblCoaching : findTeamMembers(teamId)) {
                pu.remove(apblCoaching);
            }
        }
    }

    public void removeTeamCoach(int teamId, int teacherId) {
        ApblCoaching old = findTeamCoach(teamId, teacherId);
        if (old != null) {
            AcademicTeacher teacher = academic.getCurrentTeacher();
            if(!core.isSessionAdmin() && teacher!=null && teacher.getId()==old.getTeacher().getId()) {
                UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        UPA.getPersistenceUnit().remove(old);
                    }
                });
            }else{
                UPA.getPersistenceUnit().remove(old);
            }
        }
    }

    public ApblTeamMember addTeamMember(int teamId, int studentId) {
        AcademicStudent student = academic.findStudent(studentId);
        if (student == null) {
            throw new RuntimeException("Missing Student");
        }
        ApblTeam team = findTeam(teamId);
        if (team == null) {
            throw new RuntimeException("Missing Team");
        }
        ApblTeamMember old = findTeamMember(teamId, studentId);
        if (old == null) {


            ApblSession session = findSession(team.getId());
            int teamMemberhMax = session.getTeamMemberMax();
            List<AcademicStudent> students = findTeamMemberStudents(teamId);
            if (teamMemberhMax > 0 && students.size() >= teamMemberhMax) {
                throw new RuntimeException("Too many students");
            }

            ApblTeamMember e = new ApblTeamMember();
            e.setStudent(student);
            e.setTeam(team);
            UPA.getPersistenceUnit().persist(e);
            return e;
        }
        return null;
    }

    public ApblCoaching addTeamCoach(int teamId, int teacherId) {
        AcademicTeacher teacher = academic.findTeacher(teacherId);
        if (teacher == null) {
            throw new RuntimeException("Missing Teacher");
        }
        ApblTeam team = findTeam(teamId);
        if (team == null) {
            throw new RuntimeException("Missing Team");
        }

        ApblCoaching old = findTeamCoach(teamId, teacherId);
        if (old == null) {
            ApblSession session = findSession(team.getId());
            int teamCoachMax = session.getTeamCoachMax();
            List<ApblCoaching> coaches = findTeamCoaches(teamId);
            if (teamCoachMax > 0 && coaches.size() >= teamCoachMax) {
                throw new RuntimeException("Too many coaches");
            }

            ApblCoaching e = new ApblCoaching();
            e.setTeacher(teacher);
            e.setTeam(team);
            UPA.getPersistenceUnit().persist(e);
            return e;
        }
        return null;
    }

    public void addTeam(ApblTeam team) {
        if (team.getOwner() == null) {
            throw new RuntimeException("Missing Owner");
        }
        if (StringUtils.isEmpty(team.getName())) {
            throw new RuntimeException("Empty Name");
        }
        if (findTeam(team.getSession().getId(), team.getName()) != null) {
            throw new RuntimeException("Name already exists");
        }
        UPA.getPersistenceUnit().persist(team);
        //if owner is student, he should be member of the team
        AcademicStudent student = academic.findStudentByUser(team.getOwner().getId());
        if(student!=null) {
            addTeamMember(team.getId(),student.getId());
        }
    }

    public void updateProject(ApblProject project) {
        if (project.getOwner() == null) {
            throw new RuntimeException("Missing Owner");
        }
        if (StringUtils.isEmpty(project.getName())) {
            throw new RuntimeException("Empty Name");
        }
        ApblProject other = findProject(project.getSession().getId(), project.getName());
        if (other != null && (project.getId()==0 || other.getId()!=project.getId())) {
            throw new RuntimeException("Name already exists");
        }
        UPA.getPersistenceUnit().merge(project);
    }

    public void addProject(ApblProject project) {
        if (project.getOwner() == null) {
            throw new RuntimeException("Missing Owner");
        }
        if (StringUtils.isEmpty(project.getName())) {
            throw new RuntimeException("Empty Name");
        }
        if (findProject(project.getSession().getId(), project.getName()) != null) {
            throw new RuntimeException("Name already exists");
        }
        UPA.getPersistenceUnit().persist(project);
    }

    public void updateTeam(ApblTeam project) {
        if (project.getOwner() == null) {
            throw new RuntimeException("Missing Owner");
        }
        if (StringUtils.isEmpty(project.getName())) {
            throw new RuntimeException("Empty Name");
        }
        ApblTeam other = findTeam(project.getSession().getId(), project.getName());
        if (other != null && (project.getId()==0 || other.getId()!=project.getId())) {
            throw new RuntimeException("Name already exists");
        }
        UPA.getPersistenceUnit().merge(project);
    }

    public ApblTeam findTeam(int teamId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(ApblTeam.class, teamId);
    }

    public ApblTeam findTeam(int sessionId, String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblTeam u where u.sessionId=:sessionId and u.name=:name")
                .setParameter("sessionId", sessionId)
                .setParameter("name", name)
                .getFirstResultOrNull();
    }

    public ApblProject findProject(int sessionId, String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblProject u where u.sessionId=:sessionId and u.name=:name")
                .setParameter("sessionId", sessionId)
                .setParameter("name", name)
                .getFirstResultOrNull();
    }

    public ApblProject findProject(int projectId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(ApblProject.class, projectId);
    }

    public List<ProjectNode> findProjectNodes(int sessionId) {
        List<ProjectNode> projects = new ArrayList<>();
        for (ApblProject project : findProjects(sessionId)) {
            ProjectNode pnode = new ProjectNode();
            pnode.setProject(project);
            for (ApblTeam team : findTeamsByProject(project.getId())) {
                TeamNode tnode = new TeamNode();
                tnode.setTeam(team);
                pnode.getTeams().add(tnode);
                for (ApblCoaching teacher : findTeamCoaches(team.getId())) {
                    CoachNode cnode = new CoachNode();
                    cnode.setCoaching(teacher);
                    tnode.getCoaches().add(cnode);
                }
                for (ApblTeamMember student : findTeamMembers(team.getId())) {
                    MemberNode mnode = new MemberNode();
                    mnode.setMember(student);
                    tnode.getMembers().add(mnode);
                }
            }
            projects.add(pnode);
        }

        ProjectNode pnode = new ProjectNode();
        pnode.setProject(null);
        for (ApblTeam team : findTeamsWithNoProject(sessionId)) {
            TeamNode tnode = new TeamNode();
            tnode.setTeam(team);
            pnode.getTeams().add(tnode);
            for (ApblCoaching teacher : findTeamCoaches(team.getId())) {
                CoachNode cnode = new CoachNode();
                cnode.setCoaching(teacher);
                tnode.getCoaches().add(cnode);
            }
            for (ApblTeamMember student : findTeamMembers(team.getId())) {
                MemberNode mnode = new MemberNode();
                mnode.setMember(student);
                tnode.getMembers().add(mnode);
            }
        }
        if (pnode.getTeams().size() > 0) {
            projects.add(pnode);
        }

        return projects;
    }

    public List<ApblProject> findProjects(int sessionId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblProject u where u.sessionId=:sessionId")
                .setParameter("sessionId", sessionId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<ApblTeam> findTeamsByProject(int projectId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblTeam u where u.projectId=:projectId")
                .setParameter("projectId", projectId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<ApblTeam> findTeamsWithNoProject(int sessionId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblTeam u where u.sessionId=:sessionId and u.projectId=null")
                .setParameter("sessionId", sessionId)
                .getResultList();
    }

    public ApblTeamMember findTeamMember(int teamId, int studentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblTeamMember u where u.teamId=:teamId and u.studentId=:studentId")
                .setParameter("teamId", teamId)
                .setParameter("studentId", studentId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 4)
                .getFirstResultOrNull();
    }

    public List<ApblTeamMember> findTeamMembers(int teamId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblTeamMember u where u.teamId=:teamId")
                .setParameter("teamId", teamId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<AcademicStudent> findTeamMemberStudents(int teamId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u.student from ApblTeamMember u where u.teamId=:teamId")
                .setParameter("teamId", teamId)
                .getResultList();
    }

    public List<ApblTeam> findTeamsByCoach(int sessionId, int userId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblTeam u where u.sessionId=:sessionId and exists (Select 1 from ApblCoaching x where x.teacher.userId=:userId and x.teamId=u.id)")
                .setParameter("sessionId", sessionId)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<ApblCoaching> findTeamCoaches(int teamId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblCoaching u where u.teamId=:teamId")
                .setParameter("teamId", teamId)
                .getResultList();
    }

    public ApblCoaching findTeamCoach(int teamId, int teacherId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblCoaching u where u.teamId=:teamId and u.teacherId=:teacherId")
                .setParameter("teamId", teamId)
                .setParameter("teacherId", teacherId)
                .getFirstResultOrNull();
    }

    public List<AcademicTeacher> findTeamTeachers(int teamId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u.teacher from ApblCoaching u where u.teamId=:teamId")
                .setParameter("teamId", teamId)
                .getResultList();
    }

    public ApblSession findSession(int sessionId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(ApblSession.class, sessionId);
    }

    public List<ApblSession> findSessions(ApblSessionStatus... status) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        StringBuilder sb = new StringBuilder("Select u from ApblSession u where 1=1 ");
        Map<String, Object> params = new HashMap();
        if (status.length > 0) {
            sb.append(" and u.status in (");
            for (int i = 0; i < status.length; i++) {
                String var = "s" + i;
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(":" + var);
                params.put(var, status[i]);
            }
            sb.append(") ");
        }
        return pu.createQuery(sb.toString()).setParameters(params).getResultList();
    }
}
