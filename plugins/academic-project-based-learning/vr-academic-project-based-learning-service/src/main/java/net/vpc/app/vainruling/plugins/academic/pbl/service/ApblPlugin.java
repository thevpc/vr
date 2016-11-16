package net.vpc.app.vainruling.plugins.academic.pbl.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.*;
import net.vpc.app.vainruling.plugins.academic.pbl.service.model.*;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.common.strings.StringComparator;
import net.vpc.common.util.Filter;
import net.vpc.common.util.Utils;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.QueryHints;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import net.vpc.upa.filters.ObjectFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by vpc on 9/25/16.
 */
@AppPlugin
public class ApblPlugin {
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
        AppUser teacher = core.getCurrentUser();
        ApblProject project = findProject(projectId);
        if (project == null) {
            return;
        }
        AppUser owner = project.getOwner();
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
                    pu.remove(project);
                }
            });
        } else {
            for (ApblTeam team : findTeamsByProject(projectId)) {
                team.setProject(null);
                pu.merge(team);
            }
            pu.remove(project);
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
                    pu.remove(team);
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
            pu.remove(team);
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


            ApblSession session = team.getSession();
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
            ApblSession session = team.getSession();
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

    public List<ApblTeacherInfo> findTeacherInfos(int[] sessionIds, boolean includeMissingTeachers,ObjectFilter<AcademicTeacher> teacherFilter) {
        Map<Integer,ApblTeacherInfo> rows=new HashMap<>();
        for (Integer sessionId : new HashSet<Integer>(Arrays.asList(Utils.toIntArray(sessionIds)))) {
            for (ProjectNode projectNode : findProjectNodes(sessionId)) {
                for (TeamNode teamNode : projectNode.getTeams()) {
                    for (CoachNode coachNode : teamNode.getCoaches()) {
                        ApblCoaching c = coachNode.getCoaching();
                        if(c!=null){
                            if(teacherFilter==null || teacherFilter.accept(c.getTeacher())) {
                                ApblTeacherInfo apblTeacherInfo = rows.get(c.getTeacher().getId());
                                if (apblTeacherInfo == null) {
                                    apblTeacherInfo = new ApblTeacherInfo();
                                    apblTeacherInfo.setTeacher(c.getTeacher());
                                    rows.put(c.getTeacher().getId(), apblTeacherInfo);
                                }
                                for (MemberNode memberNode : teamNode.getMembers()) {
                                    apblTeacherInfo.getStudents().add(memberNode.getMember().getStudent());
                                }
                                apblTeacherInfo.setStudentsCount(apblTeacherInfo.getStudentsCount() + teamNode.getMembers().size() / ((double) teamNode.getCoaches().size()));
                                apblTeacherInfo.getTeams().add(teamNode);
                            }
                        }
                    }
                }
            }
        }
        if(includeMissingTeachers){
            for (Integer sessionId : new HashSet<Integer>(Arrays.asList(Utils.toIntArray(sessionIds)))) {
                ApblSession s = findSession(sessionId);
                if(s!=null){
                    for (AcademicTeacher ss : academic.findTeachers(s.getMemberProfiles())) {
                        ApblTeacherInfo r = rows.get(ss.getId());
                        if(r==null){
                            if(teacherFilter==null|| teacherFilter.accept(ss)) {
                                r = new ApblTeacherInfo();
                                r.setTeacher(ss);
                                rows.put(ss.getId(), r);
                            }
                        }
                    }
                }
            }
        }
        ArrayList<ApblTeacherInfo> apblTeacherInfos = new ArrayList<>(rows.values());
        Collections.sort(apblTeacherInfos, new Comparator<ApblTeacherInfo>() {
            @Override
            public int compare(ApblTeacherInfo o1, ApblTeacherInfo o2) {
                String s1 = o1.getTeacher().getContact()!=null?o1.getTeacher().getContact().getFullTitle():"";
                String s2 = o2.getTeacher().getContact()!=null?o2.getTeacher().getContact().getFullTitle():"";
                return s1.compareTo(s2);
            }
        });
        return apblTeacherInfos;
    }

    public List<ApblStudentInfo> findStudentInfos(int[] sessionIds, boolean includeMissingStudents, ObjectFilter<AcademicStudent> studentFilter) {
        Map<Integer,ApblStudentInfo> rows=new HashMap<>();
        for (Integer sessionId : new HashSet<Integer>(Arrays.asList(Utils.toIntArray(sessionIds)))) {
            for (ProjectNode projectNode : findProjectNodes(sessionId)) {
                for (TeamNode teamNode : projectNode.getTeams()) {
                    for (MemberNode coachNode : teamNode.getMembers()) {
                        AcademicStudent student = coachNode.getMember().getStudent();
                        if(studentFilter==null|| studentFilter.accept(student)) {
                            ApblStudentInfo apblStudentInfo = rows.get(student.getId());
                            if (apblStudentInfo == null) {
                                apblStudentInfo = new ApblStudentInfo();
                                apblStudentInfo.setStudent(student);
                                rows.put(student.getId(), apblStudentInfo);
                            }
                            apblStudentInfo.setTeam(teamNode);
                            apblStudentInfo.setProject(projectNode);
                            apblStudentInfo.getTeams().add(teamNode);
                            apblStudentInfo.getProjects().add(projectNode);

                            for (CoachNode memberNode : teamNode.getCoaches()) {
                                apblStudentInfo.getCoaches().add(memberNode.getCoaching().getTeacher());
                                apblStudentInfo.setCoach(memberNode.getCoaching().getTeacher());
                            }
                        }
                    }
                }
            }
        }
        for (ApblStudentInfo apblStudentInfo : rows.values()) {
            HashSet<String> classes=new HashSet<>();
            HashSet<String> departments=new HashSet<>();
            for (TeamNode node : apblStudentInfo.getTeams()) {
                if(node.getTeam()!=null){
                    for (MemberNode m : node.getMembers()) {
                        AcademicClass lastClass1 = m.getMember().getStudent().getLastClass1();
                        if(lastClass1!=null) {
                            classes.add(lastClass1.getName());
                        }
                        AppDepartment department = m.getMember().getStudent().getDepartment();
                        if(department!=null) {
                            departments.add(department.getCode());
                        }
                    }
                }
            }
            apblStudentInfo.setInterClasses(classes.size()>1);
            apblStudentInfo.setInterDepartments(departments.size()>1);
        }
        if(includeMissingStudents){
            for (Integer sessionId : new HashSet<Integer>(Arrays.asList(Utils.toIntArray(sessionIds)))) {
                ApblSession s = findSession(sessionId);
                if(s!=null){
                    for (AcademicStudent ss : academic.findStudents(s.getMemberProfiles(), null)) {
                        ApblStudentInfo r = rows.get(ss.getId());
                        if(r==null){
                            if(studentFilter==null|| studentFilter.accept(ss)) {
                                r = new ApblStudentInfo();
                                r.setStudent(ss);
                                rows.put(ss.getId(), r);
                            }
                        }
                    }
                }
            }
        }
        ArrayList<ApblStudentInfo> apblStudentInfos = new ArrayList<>(rows.values());
        Collections.sort(apblStudentInfos, new Comparator<ApblStudentInfo>() {
            @Override
            public int compare(ApblStudentInfo o1, ApblStudentInfo o2) {
                String s1 = o1.getStudent().getContact()!=null?o1.getStudent().getContact().getFullTitle():"";
                String s2 = o2.getStudent().getContact()!=null?o2.getStudent().getContact().getFullTitle():"";
                return s1.compareTo(s2);
            }
        });
        return apblStudentInfos;
    }

    public List<ProjectNode> findProjectNodes(int sessionId) {
        List<ProjectNode> projects = new ArrayList<>();
        List<ApblProject> foundProjectsAndNull = new ArrayList<>(findProjects(sessionId));
        foundProjectsAndNull.add(null);
        for (ApblProject project : foundProjectsAndNull) {
            ProjectNode pnode = new ProjectNode();
            pnode.setProject(project);
            List<ApblTeam> teamsByProject = project==null?findTeamsWithNoProject(sessionId):findTeamsByProject(project.getId());
            for (ApblTeam team : teamsByProject) {
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
            if(project!=null || pnode.getTeams().size() > 0) {
                projects.add(pnode);
            }
        }
        return projects;
    }

    public List<ProjectNode> findProjectNodes(int sessionId, StringComparator comparator) {
        List<ProjectNode> allProjectNodes = findProjectNodes(sessionId);


        List<ProjectNode> projects = new ArrayList<>();
        for (ProjectNode project : allProjectNodes) {
            ProjectNode pnode = new ProjectNode();
            pnode.setProject(project.getProject());
            pnode.setSelectionMatch(project.getProject()!=null &&

                    (
                            comparator.matches(project.getProject().getName())
                            || (project.getProject().getOwner()!=null && comparator.matches(project.getProject().getOwner().getContact().getFullTitle()))
                    )
            );
            boolean someTeam=false;
            for (TeamNode team : project.getTeams()) {
                TeamNode tnode = new TeamNode();
                tnode.setTeam(team.getTeam());
                tnode.setSelectionMatch(
                        comparator.matches(team.getTeam().getName())
                        ||(team.getTeam().getOwner()!=null && comparator.matches(team.getTeam().getOwner().getContact().getFullTitle()))
                );
                boolean someCoach=false;
                for (CoachNode teacher : team.getCoaches()) {
                    CoachNode cnode = new CoachNode();
                    cnode.setCoaching(teacher.getCoaching());
                    cnode.setSelectionMatch(comparator.matches(teacher.getCoaching().getTeacher().getContact().getFullTitle()));
                    someCoach|=cnode.isSelectionMatch();
                    //if(cnode.isSelectionMatch() || tnode.isSelectionMatch() || pnode.isSelectionMatch()) {
                        tnode.getCoaches().add(cnode);
                    //}
                }
                boolean someMember=false;
                for (MemberNode student : team.getMembers()) {
                    MemberNode mnode = new MemberNode();
                    mnode.setMember(student.getMember());
                    mnode.setSelectionMatch(comparator.matches(student.getMember().getStudent().getContact().getFullTitle()));
                    someMember|=mnode.isSelectionMatch();
                    //if(mnode.isSelectionMatch() || tnode.isSelectionMatch() || pnode.isSelectionMatch()) {
                        tnode.getMembers().add(mnode);
                    //}
                }
                if(someMember || someCoach || tnode.isSelectionMatch() || pnode.isSelectionMatch()) {
                    pnode.getTeams().add(tnode);
                    someTeam|=true;
                }
            }
            if(project.getProject()!=null || pnode.getTeams().size() > 0) {
                if(someTeam || pnode.isSelectionMatch()) {
                    projects.add(pnode);
                }
            }
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

    public void addProgressionLog(ApblProgressionLog log){
        if(log.getTeam()==null){
            throw new RuntimeException("Empty Team");
        }
        if(StringUtils.isEmpty(log.getDescription())){
            throw new RuntimeException("Empty Description");
        }
        if(log.getProgressionPercent()<=0){
            throw new RuntimeException("Empty Progression");
        }
        if(log.getProgressionDate()==null){
            throw new RuntimeException("Empty Date");
        }
        UPA.getPersistenceUnit().persist(log);
    }

    public void addCoachingLog(ApblCoachingLog log){
        if(log.getCoaching()==null){
            throw new RuntimeException("Empty Team");
        }
        if(StringUtils.isEmpty(log.getDescription())){
            throw new RuntimeException("Empty Description");
        }
        if(log.getDurationMinutes()<=0){
            throw new RuntimeException("Empty Duration");
        }
        if(log.getAppointmentDate()==null){
            throw new RuntimeException("Empty Date");
        }
        UPA.getPersistenceUnit().persist(log);
    }

    public List<ApblProgressionLog> findTeamProgressionLog(int teamId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblProgressionLog u where u.teamId = :teamId order by u.progressionDate desc")
                .setParameter("teamId", teamId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 4)
                .getResultList();
    }

    public List<ApblCoachingLog> findTeamCoachingLog(int teamId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblCoachingLog u where u.coaching.teamId = :teamId order by u.appointmentDate desc")
                .setParameter("teamId", teamId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 4)
                .getResultList();
    }

    public List<ApblTeam> findOpenTeamsByStudent(int studentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblTeam u where u.session.status.closed = false and exists (Select m from ApblTeamMember m where m.teamId=u.id and m.studentId=:studentId)")
                .setParameter("studentId", studentId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<ApblTeam> findOpenTeamsByTeacher(int teacherId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ApblTeam t=new ApblTeam();
        return pu.createQuery("Select u from ApblTeam u where u.session.status.closed = false and exists (Select m from ApblCoaching m where m.teamId=u.id and m.teacherId=:teacherId)")
                .setParameter("teacherId", teacherId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<ApblTeam> findOpenTeamsByUser(int userId,boolean asMember,boolean asOwner) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ApblTeam t=new ApblTeam();
        if(!asMember && !asOwner){
            return Collections.EMPTY_LIST;
        }
        return pu.createQuery("Select u from ApblTeam u where u.session.status.closed = false and  ( " +
                        "1=2 " +
                        (asMember?("or exists ((Select m from ApblTeamMember m where m.teamId=u.id and m.student.userId=:userId)) " +
                        "or exists ((Select m from ApblCoaching m where m.teamId=u.id and m.teacher.userId=:userId)) "):"") +
                        (asOwner ?
                                ("or exists ((Select m from ApblProject m where m.ownerId=:userId))" +
                        "or u.ownerId=:userId ") :"")+
                        ")"
                )
                .setParameter("userId", userId)
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

    public List<ApblSession> findAvailableSessions() {
        AcademicTeacher currentTeacher = academic.getCurrentTeacher();
        AcademicStudent currentStudent = academic.getCurrentStudent();
        AppUser currentUser= UserSession.getCurrentUser();
        boolean currentAdmin = core.isSessionAdmin();
        List<ApblSession> sessions = new ArrayList<>();
        if (core.isSessionAdmin()) {
            sessions = findOpenSessions();
        } else {
            sessions = findOpenVisibleSessions();
        }
        sessions = (List<ApblSession>) Utils.retainAll(new ArrayList<>(sessions), new Filter<ApblSession>() {
            @Override
            public boolean accept(ApblSession value) {
                if (currentTeacher != null && currentTeacher.getUser() != null) {
                    return true;
                }
                if (currentStudent != null && currentStudent.getUser() != null) {
                    return
                            core.userMatchesProfileFilter(currentStudent.getUser().getId(), value.getMemberProfiles())
                                    || core.userMatchesProfileFilter(currentStudent.getUser().getId(), value.getTeamOwnerProfiles())
                            ;
                }
                //if any other user but not teacher or student, check it fulfills any of the other profiles
                if (currentUser != null) {
                    if (!StringUtils.isEmpty(value.getMemberProfiles())
                            &&
                            core.userMatchesProfileFilter(currentUser.getId(), value.getMemberProfiles())
                            ) {
                        return true;
                    }
                    if (!StringUtils.isEmpty(value.getTeamOwnerProfiles())
                            &&
                            core.userMatchesProfileFilter(currentUser.getId(), value.getTeamOwnerProfiles())
                            ) {
                        return true;
                    }
                    if (!StringUtils.isEmpty(value.getCoachProfiles())
                            &&
                            core.userMatchesProfileFilter(currentUser.getId(), value.getCoachProfiles())
                            ) {
                        return true;
                    }
                    if (!StringUtils.isEmpty(value.getProjectOwnerProfiles())
                            &&
                            core.userMatchesProfileFilter(currentUser.getId(), value.getProjectOwnerProfiles())
                            ) {
                        return true;
                    }
                }

                return false;
            }
        });
        return sessions;
    }

    public List<ApblSession> findOpenSessions() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblSession u where u.status.closed=false ").getResultList();
    }

    public List<ApblSession> findOpenVisibleSessions() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblSession u where u.status.closed=false and u.status.active=true").getResultList();
    }

//    public List<ApblSession> findSessions(ApblSessionStatus... status) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        StringBuilder sb = new StringBuilder("Select u from ApblSession u where 1=1 ");
//        Map<String, Object> params = new HashMap();
//        if (status.length > 0) {
//            sb.append(" and u.status in (");
//            for (int i = 0; i < status.length; i++) {
//                String var = "s" + i;
//                if (i > 0) {
//                    sb.append(",");
//                }
//                sb.append(":" + var);
//                params.put(var, status[i]);
//            }
//            sb.append(") ");
//        }
//        return pu.createQuery(sb.toString()).setParameters(params).getResultList();
//    }
}
