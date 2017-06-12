package net.vpc.app.vainruling.plugins.academic.pbl.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.NameGenerator;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.*;
import net.vpc.app.vainruling.plugins.academic.pbl.service.model.*;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.common.strings.StringComparator;
import net.vpc.common.strings.StringComparators;
import net.vpc.common.util.*;
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
    public static final Comparator<ApblTeacherInfo> APBL_TEACHER_INFO_COMPARATOR = new Comparator<ApblTeacherInfo>() {
        @Override
        public int compare(ApblTeacherInfo o1, ApblTeacherInfo o2) {
            String s1 = o1.getTeacher().getContact() != null ? o1.getTeacher().getContact().getFullTitle() : "";
            String s2 = o2.getTeacher().getContact() != null ? o2.getTeacher().getContact().getFullTitle() : "";
            return s1.compareTo(s2);
        }
    };
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
            if (!core.isSessionAdmin() && student != null && student.getId() == old.getStudent().getId()) {
                UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        UPA.getPersistenceUnit().remove(old);
                    }
                });
            } else {
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
            if (!core.isSessionAdmin() && teacher != null && teacher.getId() == old.getTeacher().getId()) {
                UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        UPA.getPersistenceUnit().remove(old);
                    }
                });
            } else {
                UPA.getPersistenceUnit().remove(old);
            }
        }
    }

    public ApblTeamMember addTeamMember(int teamId, int studentId) {
        return addTeamMember(teamId, studentId, null);
    }

    public ApblTeamMember addTeamMember(int teamId, int studentId, String description) {
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
            e.setDescription(description);
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
        if (student != null) {
            addTeamMember(team.getId(), student.getId());
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
        if (other != null && (project.getId() == 0 || other.getId() != project.getId())) {
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
        if (other != null && (project.getId() == 0 || other.getId() != project.getId())) {
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
    
    public void applyTeacherLoad(int sessionId, ObjectFilter<AcademicTeacher> teacherFilter){
        ApblSessionListInfo teacherInfos = findTeacherInfos(new int[]{sessionId}, true,teacherFilter);
        ApblSessionInfo sessionInfo = teacherInfos.getSessions().get(0);
        ApblSession session = sessionInfo.getSession();
        List<ApblProgramSession> sessionPrograms = findSessionPrograms(session.getId());
        MapList<Integer,ApblProgramSession> list=new DefaultMapList<Integer, ApblProgramSession>(sessionPrograms,ApblProgramSession::getId);
        AcademicCourseType tp = academic.findCourseType("TP");
        for (ApblTeacherInfo t : teacherInfos.getTeachers()) {
            if(teacherFilter.accept(t.getTeacher())) {
                for (ApblProgramSession sessionProgram : sessionPrograms) {
                    AcademicProgram program = sessionProgram.getProgram();
                    double val = t.getProgramSessionLoadById(sessionProgram.getId());
                    AcademicCoursePlan coursePlan = sessionProgram.getCourse();
                    if(coursePlan!=null) {
                        List<AcademicCourseAssignment> assignments = academic.findCourseAssignments(session.getPeriod().getId(), t.getTeacher().getId(), new CourseAssignmentFilter() {
                            @Override
                            public boolean acceptAssignment(AcademicCourseAssignment academicCourseAssignment) {
                                AcademicCoursePlan p = academicCourseAssignment.getCoursePlan();
                                return (p != null && p.getId() == coursePlan.getId()
                                        && String.valueOf(t.getTeacher().getId()).equals(academicCourseAssignment.getDiscriminator())
                                        && academic.buildCoursePlanLabelsFromString(academicCourseAssignment.getLabels()).contains("Pbl"));
                            }

                            @Override
                            public boolean lookupIntents() {
                                return false;
                            }
                        });
                        for (int i = 1; i < assignments.size(); i++) {
                            academic.removeCourseAssignment(assignments.get(i).getId());
                        }
                        if (assignments.size() > 0) {
                            AcademicCourseAssignment a = assignments.get(0);
                            if(val==0){
                                academic.removeCourseAssignment(a.getId());
                            }else {
                                a.setShareCount(1);
                                a.setGroupCount(1);
                                a.setValueC(0);
                                a.setValueTD(0);
                                a.setValueTP(Math.round(val * 100.0) / 100.0);
                                a.setValuePM(0);
                                academic.updateCourseAssignment(a);
                                a.setSubClass(null);
                            }
                        } else {
                            if(val!=0) {
                                AcademicCourseAssignment a = new AcademicCourseAssignment();
                                a.setShareCount(1);
                                a.setGroupCount(1);
                                a.setValueC(0);
                                a.setValueTD(0);
                                a.setValueTP(Math.round(val * 100.0) / 100.0);
                                a.setValuePM(0);
                                a.setDiscriminator(String.valueOf(t.getTeacher().getId()));
                                a.setLabels("Pbl");
                                a.setTeacher(t.getTeacher());
                                a.setCoursePlan(coursePlan);
                                a.setCourseType(tp);
                                a.setOwnerDepartment(program.getDepartment());
                                a.setSubClass(null);
                                academic.addCourseAssignment(a);
                            }
                        }
                    }
                }
            }
        }
    }

    public ApblSessionListInfo findTeacherInfos(int[] sessionIds, boolean includeMissingTeachers, ObjectFilter<AcademicTeacher> teacherFilter) {
        ApblSessionListInfo apblSessionListInfo = new ApblSessionListInfo();
        for (Integer sessionId : new HashSet<Integer>(Arrays.asList(Utils.toIntArray(sessionIds)))) {
            Map<Integer, ApblTeacherInfo> rows = new HashMap<>();
            ApblSession currentSession = findSession(sessionId);
            if(currentSession==null){
                continue;
            }
            ApblSessionInfo sessionInfo = new ApblSessionInfo();
            apblSessionListInfo.getSessions().add(sessionInfo);
            sessionInfo.setSession(currentSession);
            ApblSessionLoadStrategy teamsBasedLoad = currentSession.getLoadStrategy();
            if (teamsBasedLoad == null) {
                teamsBasedLoad = ApblSessionLoadStrategy.ALL_STUDENTS;
            }

            int maxStudents = academic.findStudents(currentSession.getMemberProfiles(), null).size();
            int teamsCount = 0; // may include duplicates
            int teamedStudents = 0; // may include duplicates
            int coachedStudents = 0; // may include duplicates
            Map<Integer,ApblProgramSession> programs=apblSessionListInfo.getPrograms();
            VMap<Integer,Integer> studentsCountByProgram=new VMap<>(key -> 0);
            double totLoad=0;
            //totLoad=currentSession.getLoad();
            for (ApblProgramSession p : findSessionPrograms(sessionId)) {
                programs.put(p.getProgram().getId(),p);
                totLoad+=p.getLoad();
            }

            for (ProjectNode projectNode : findProjectNodes(sessionId)) {
                for (TeamNode teamNode : projectNode.getTeams()) {
                    if (!teamNode.getTeam().isExcludeFromLoad()) {
                        teamsCount++;
                        for (CoachNode coachNode : teamNode.getCoaches()) {
                            ApblCoaching c = coachNode.getCoaching();
                            if (c != null) {
                                if (teacherFilter == null || teacherFilter.accept(c.getTeacher())) {
                                    ApblTeacherInfo apblTeacherInfo = rows.get(c.getTeacher().getId());
                                    if (apblTeacherInfo == null) {
                                        apblTeacherInfo = new ApblTeacherInfo();
                                        apblTeacherInfo.setTeacher(c.getTeacher());
                                        rows.put(c.getTeacher().getId(), apblTeacherInfo);
                                    }
                                    for (MemberNode memberNode : teamNode.getMembers()) {

                                        AcademicStudent student = memberNode.getMember().getStudent();
                                        for (AcademicClass cls : new AcademicClass[]{student.getLastClass1(), student.getLastClass2(), student.getLastClass3()}) {
                                            if(cls!=null && cls.getProgram()!=null){
                                                studentsCountByProgram.plus(cls.getProgram().getId(),1);
                                            }
                                        }
                                        apblTeacherInfo.getStudents().add(student);
                                    }
                                    apblTeacherInfo.setStudentsCount(apblTeacherInfo.getStudentsCount() + teamNode.getMembers().size() / ((double) teamNode.getCoaches().size()));
                                    apblTeacherInfo.getTeams().add(teamNode);
                                }
                            }
                        }
                        teamedStudents += teamNode.getMembers().size();
                        if (!teamNode.getCoaches().isEmpty()) {
                            coachedStudents += teamNode.getMembers().size();
                        }
                    }
                }
            }
            Set<Integer> visitedTeachers = new HashSet<>();
            for (ProjectNode projectNode : findProjectNodes(sessionId)) {
                for (TeamNode teamNode : projectNode.getTeams()) {
                    if (!teamNode.getTeam().isExcludeFromLoad()) {
                        for (CoachNode coachNode : teamNode.getCoaches()) {
                            ApblCoaching c = coachNode.getCoaching();
                            if (c != null) {
                                if (teacherFilter == null || teacherFilter.accept(c.getTeacher())) {
                                    visitedTeachers.add(c.getTeacher().getId());
                                }
                            }
                        }
                    }
                }
            }
            sessionInfo.setMaxStudentCount(maxStudents);
            sessionInfo.setTeamedStudentCount(teamedStudents);
            sessionInfo.setCoachedStudentCount(coachedStudents);
            sessionInfo.setTeamsCount(teamsCount);
            switch (teamsBasedLoad) {
                case ALL_STUDENTS: {
                    sessionInfo.setBaseStudentCount(maxStudents);
                    if(sessionInfo.getBaseStudentCount()!=0) {
                        sessionInfo.setUnitLoad(totLoad/sessionInfo.getBaseStudentCount());
                    }
                    break;
                }
                case TEAMED_STUDENTS: {
                    sessionInfo.setBaseStudentCount(teamedStudents);
                    if(sessionInfo.getBaseStudentCount()!=0) {
                        sessionInfo.setUnitLoad(totLoad/sessionInfo.getBaseStudentCount());
                    }
                    break;
                }
                case COACHED_STUDENTS: {
                    sessionInfo.setBaseStudentCount(coachedStudents);
                    if(sessionInfo.getBaseStudentCount()!=0) {
                        sessionInfo.setUnitLoad(totLoad/sessionInfo.getBaseStudentCount());
                    }
                    break;
                }
            }
            for (Integer visitedTeacher : visitedTeachers) {
                ApblTeacherInfo r = rows.get(visitedTeacher);
                if (r != null) {
                    r.setLoad(r.getStudentsCount() * sessionInfo.getUnitLoad());
                    for (ApblProgramSession apblProgramSession : programs.values()) {
                        AcademicProgram p = apblProgramSession.getProgram();
                        r.setProgramSessionLoadById(apblProgramSession.getId(), r.getLoad() * apblProgramSession.getLoad() / totLoad);
                        r.addProgramLoadByName(p.getName(), r.getLoad() * apblProgramSession.getLoad() / totLoad);
                    }
                }
            }
            if (includeMissingTeachers) {
                for (AcademicTeacher ss : academic.findTeachers(currentSession.getMemberProfiles())) {
                    ApblTeacherInfo r = rows.get(ss.getId());
                    if (r == null) {
                        if (teacherFilter == null || teacherFilter.accept(ss)) {
                            r = new ApblTeacherInfo();
                            r.setTeacher(ss);
                            rows.put(ss.getId(), r);
                        }
                    }
                }
            }
            ArrayList<ApblTeacherInfo> apblTeacherInfos = new ArrayList<>(rows.values());
            apblTeacherInfos.sort(APBL_TEACHER_INFO_COMPARATOR);
            sessionInfo.setTeachers(apblTeacherInfos);
        }
        Map<Integer, ApblTeacherInfo> allRows = new HashMap<>();
        for (ApblSessionInfo sessionInfo : apblSessionListInfo.getSessions()) {
            for (ApblTeacherInfo a : sessionInfo.getTeachers()) {
                ApblTeacherInfo t = allRows.get(a.getTeacher().getId());
                if (t == null) {
                    t = new ApblTeacherInfo();
                    allRows.put(a.getTeacher().getId(), t);
                    t.setTeacher(a.getTeacher());
                }
                t.getStudents().addAll(a.getStudents());
                t.getTeams().addAll(a.getTeams());
                t.setStudentsCount(t.getStudentsCount() + a.getStudentsCount());
                t.setLoad(t.getLoad() + a.getLoad());
                for (Map.Entry<Integer, Double> z : a.getProgramSessionsLoadById().entrySet()) {
                    t.addProgramSessionLoadByName(z.getKey(),z.getValue());
                }
                for (Map.Entry<String, Double> z : a.getProgramsLoadByName().entrySet()) {
                    t.addProgramLoadByName(z.getKey(),z.getValue());
                }
            }
            apblSessionListInfo.setMaxStudentCount(apblSessionListInfo.getMaxStudentCount() + sessionInfo.getMaxStudentCount());
            apblSessionListInfo.setTeamedStudentCount(apblSessionListInfo.getTeamedStudentCount() + sessionInfo.getTeamedStudentCount());
            apblSessionListInfo.setCoachedStudentCount(apblSessionListInfo.getCoachedStudentCount() + sessionInfo.getCoachedStudentCount());
            apblSessionListInfo.setBaseStudentCount(apblSessionListInfo.getBaseStudentCount() + sessionInfo.getBaseStudentCount());
            apblSessionListInfo.setTeamsCount(apblSessionListInfo.getTeamsCount() + sessionInfo.getTeamsCount());
            apblSessionListInfo.setUnitLoad(apblSessionListInfo.getUnitLoad() + sessionInfo.getUnitLoad());
        }
        if(apblSessionListInfo.getSessions().size()>0) {
            apblSessionListInfo.setUnitLoad(apblSessionListInfo.getUnitLoad() / apblSessionListInfo.getSessions().size());
        }
        ArrayList<ApblTeacherInfo> apblTeacherInfos = new ArrayList<>(allRows.values());
        apblTeacherInfos.sort(APBL_TEACHER_INFO_COMPARATOR);
        apblSessionListInfo.setTeachers(apblTeacherInfos);
        return apblSessionListInfo;
    }

    public List<ApblStudentInfo> findStudentInfos(int[] sessionIds, boolean includeMissingStudents, ObjectFilter<AcademicStudent> studentFilter) {
        Map<Integer, ApblStudentInfo> rows = new HashMap<>();
        for (Integer sessionId : new HashSet<Integer>(Arrays.asList(Utils.toIntArray(sessionIds)))) {
            for (ProjectNode projectNode : findProjectNodes(sessionId)) {
                for (TeamNode teamNode : projectNode.getTeams()) {
                    for (MemberNode coachNode : teamNode.getMembers()) {
                        AcademicStudent student = coachNode.getMember().getStudent();
                        if (studentFilter == null || studentFilter.accept(student)) {
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
            HashSet<String> classes = new HashSet<>();
            HashSet<String> departments = new HashSet<>();
            for (TeamNode node : apblStudentInfo.getTeams()) {
                if (node.getTeam() != null) {
                    for (MemberNode m : node.getMembers()) {
                        AcademicClass lastClass1 = m.getMember().getStudent().getLastClass1();
                        if (lastClass1 != null) {
                            classes.add(lastClass1.getName());
                        }
                        AppDepartment department = m.getMember().getStudent().getDepartment();
                        if (department != null) {
                            departments.add(department.getCode());
                        }
                    }
                }
            }
            apblStudentInfo.setInterClasses(classes.size() > 1);
            apblStudentInfo.setInterDepartments(departments.size() > 1);
        }
        if (includeMissingStudents) {
            for (Integer sessionId : new HashSet<Integer>(Arrays.asList(Utils.toIntArray(sessionIds)))) {
                ApblSession s = findSession(sessionId);
                if (s != null) {
                    for (AcademicStudent ss : academic.findStudents(s.getMemberProfiles(), null)) {
                        ApblStudentInfo r = rows.get(ss.getId());
                        if (r == null) {
                            if (studentFilter == null || studentFilter.accept(ss)) {
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
                String s1 = o1.getStudent().getContact() != null ? o1.getStudent().getContact().getFullTitle() : "";
                String s2 = o2.getStudent().getContact() != null ? o2.getStudent().getContact().getFullTitle() : "";
                return s1.compareTo(s2);
            }
        });
        return apblStudentInfos;
    }

    public List<ProjectNode> findProjectNodes(int sessionId) {
        List<ProjectNode> projects = new ArrayList<>();
        List<ApblProject> foundProjectsAndNull = new ArrayList<>(findProjects(sessionId));
        foundProjectsAndNull.add(null);
        List<TeamNode> teams = new ArrayList<>();
        for (ApblProject project : foundProjectsAndNull) {
            ProjectNode pnode = new ProjectNode();
            pnode.setProject(project);
            List<ApblTeam> teamsByProject = project == null ? findTeamsWithNoProject(sessionId) : findTeamsByProject(project.getId());
            for (ApblTeam team : teamsByProject) {
                TeamNode tnode = new TeamNode();
                teams.add(tnode);
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
            if (project != null || pnode.getTeams().size() > 0) {
                projects.add(pnode);
            }
        }
        updateUnsatisfiedTeamConstraints(teams, sessionId);
        return projects;
    }

    private void updateUnsatisfiedTeamConstraints(List<TeamNode> teams, int sessionId) {
        TeamConstraintsChecker c = new TeamConstraintsChecker(sessionId);
        for (TeamNode team : teams) {
            team.setUnsatisfiedTeamConstraints(c.evalUnsatisfiedConstraintsCount(team));
        }
    }

    public static class TeamConstraintsChecker {
        int sessionId;
        List<ApblTeamConstraint> constraints = null;

        public TeamConstraintsChecker(int sessionId) {
            this.sessionId = sessionId;
        }

        public boolean isAutoAddableMember(TeamNode node, int userId) {
            HashSet<Integer> memberUsers = new HashSet<>();
            if (node.getTeam().isFreeMembers()) {
                return false;
            }
            for (MemberNode memberNode : node.getMembers()) {
                memberUsers.add(memberNode.getMember().getStudent().getUser().getId());
            }
            memberUsers.add(userId);
            return checkMax(new ArrayList<>(memberUsers));
        }


        public int evalUnsatisfiedConstraintsCount(TeamNode team) {
            if (team.getTeam().isFreeMembers()) {
                return 0;
            }

            List<Integer> memberUsers = new ArrayList<>();
            for (MemberNode memberNode : team.getMembers()) {
                memberUsers.add(memberNode.getMember().getStudent().getUser().getId());
            }
            return evalUnsatisfiedConstraintsCount(memberUsers);
        }

        public int evalUnsatisfiedConstraintsCount(List<Integer> memberUsers) {
            int unsatisfied = 0;
            PersistenceUnit pu = UPA.getPersistenceUnit();
            if (constraints == null) {
                constraints = pu.createQuery("Select u from ApblTeamConstraint u where u.sessionId=:sessionId")
                        .setParameter("sessionId", sessionId).getResultList();
            }
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            for (ApblTeamConstraint constraint : constraints) {
                if (!StringUtils.isEmpty(constraint.getProfiles()) && (constraint.getMinCount() > 0 || constraint.getMaxCount() > 0)) {
                    int count = 0;
                    for (Integer user : memberUsers) {
                        if (core.userMatchesProfileFilter(user, constraint.getProfiles())) {
                            count++;
                        }
                    }
                    if ((constraint.getMinCount() > 0 && count < constraint.getMinCount())
                            ||
                            (constraint.getMaxCount() > 0 && count > constraint.getMaxCount())
                            ) {
                        unsatisfied++;
                    }
                }
            }
            return unsatisfied;
        }

        public boolean isValid(TeamNode team) {
            if (team.getTeam().isFreeMembers()) {
                return true;
            }

            List<Integer> memberUsers = new ArrayList<>();
            for (MemberNode memberNode : team.getMembers()) {
                memberUsers.add(memberNode.getMember().getStudent().getUser().getId());
            }
            return check(memberUsers);
        }

        public boolean check(List<Integer> memberUsers) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            if (constraints == null) {
                constraints = pu.createQuery("Select u from ApblTeamConstraint u where u.sessionId=:sessionId")
                        .setParameter("sessionId", sessionId).getResultList();
            }
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            for (ApblTeamConstraint constraint : constraints) {
                if (!StringUtils.isEmpty(constraint.getProfiles()) && (constraint.getMinCount() > 0 || constraint.getMaxCount() > 0)) {
                    int count = 0;
                    for (Integer user : memberUsers) {
                        if (core.userMatchesProfileFilter(user, constraint.getProfiles())) {
                            count++;
                        }
                    }
                    if ((constraint.getMinCount() > 0 && count < constraint.getMinCount())
                            ||
                            (constraint.getMaxCount() > 0 && count > constraint.getMaxCount())
                            ) {
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean checkMax(List<Integer> memberUsers) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            if (constraints == null) {
                constraints = pu.createQuery("Select u from ApblTeamConstraint u where u.sessionId=:sessionId")
                        .setParameter("sessionId", sessionId).getResultList();
            }
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            for (ApblTeamConstraint constraint : constraints) {
                if (!StringUtils.isEmpty(constraint.getProfiles()) && (constraint.getMinCount() > 0 || constraint.getMaxCount() > 0)) {
                    int count = 0;
                    for (Integer user : memberUsers) {
                        if (core.userMatchesProfileFilter(user, constraint.getProfiles())) {
                            count++;
                        }
                    }
                    if (constraint.getMaxCount() > 0 && count > constraint.getMaxCount()) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public void removeShuffleSession(int sessionId) {
        ApblSession session = findSession(sessionId);
        if (session == null) {
            return;
        }
        List<ProjectNode> projectNodes = findProjectNodes(sessionId);
        for (ProjectNode projectNode : projectNodes) {
            for (TeamNode teamNode : projectNode.getTeams()) {
                if ("#generated#".equals(teamNode.getTeam().getDescription())) {
                    removeTeam(teamNode.getTeam().getId());
                    continue;
                }
                for (MemberNode memberNode : teamNode.getMembers()) {
                    if ("#generated#".equals(memberNode.getMember().getDescription())) {
                        removeTeamMember(memberNode.getMember().getTeam().getId(), memberNode.getMember().getStudent().getId());
                    }
                }
            }
        }
    }

    public void shuffleSession(int sessionId) {
        ApblSession session = findSession(sessionId);
        if (session == null) {
            return;
        }
        TeamConstraintsChecker teamConstraintsChecker = new TeamConstraintsChecker(sessionId);
        Map<Integer, AcademicStudent> studentsMap = new HashMap<>();
        Set<Integer> studentsVisited = new HashSet<>();
        for (AcademicStudent s : academic.findStudents(session.getMemberProfiles(), null)) {
            studentsMap.put(s.getId(), s);
        }
        HashSet<String> teamNames = new HashSet<>();
        List<ProjectNode> projectNodes = findProjectNodes(sessionId);
        List<TeamNode> teams = new ArrayList<>();
        for (ProjectNode projectNode : projectNodes) {
            for (TeamNode teamNode : projectNode.getTeams()) {
                teamNames.add(teamNode.getTeam().getName());
                for (Iterator<MemberNode> iterator = teamNode.getMembers().iterator(); iterator.hasNext(); ) {
                    MemberNode memberNode = iterator.next();
                    int sid = memberNode.getMember().getStudent().getId();
                    int id = memberNode.getMember().getStudent().getUser().getId();
                    if (studentsVisited.contains(sid)) {
                        iterator.remove();
                        removeTeamMember(memberNode.getMember().getTeam().getId(), sid);
                    } else {
                        studentsVisited.add(sid);
                        studentsMap.remove(sid);
                    }
                }
                if (session.getTeamMemberMax() > 0 && teamNode.getMembers().size() < session.getTeamMemberMax()) {
                    teams.add(teamNode);
                }
            }
        }
        Collections.sort(teams, new Comparator<TeamNode>() {
            @Override
            public int compare(TeamNode o1, TeamNode o2) {
                return o1.getMembers().size() - o2.getMembers().size();
            }
        });

        for (Iterator<AcademicStudent> iterator = studentsMap.values().iterator(); iterator.hasNext(); ) {
            AcademicStudent student = iterator.next();
            boolean added = false;
            for (TeamNode team : teams) {
                if (teamConstraintsChecker.isAutoAddableMember(team, student.getUser().getId())) {
                    ApblTeamMember member = addTeamMember(team.getTeam().getId(), student.getId(), "#generated#");
                    MemberNode memberNode = new MemberNode();
                    memberNode.setMember(member);
                    team.getMembers().add(memberNode);
                    iterator.remove();
                    added = true;
                }
                if (added) {
                    break;
                }
            }
        }

        if (!studentsMap.isEmpty()) {
            NameGenerator.SetNameExistenceChecker checker = new NameGenerator.SetNameExistenceChecker();
            NameGenerator gen = new NameGenerator(checker, new NameGenerator.IndexNameSequence("Sans-Equipe-", 1, false));
            for (String teamName : teamNames) {
                checker.registerName(teamName);
            }
            int maxTeams = 0;
            while (!studentsMap.isEmpty()) {
                ApblTeam newTeam = new ApblTeam();
                newTeam.setName(gen.nextName());
                newTeam.setSession(session);
                newTeam.setOwner(UserSession.getCurrentUser());
                addTeam(newTeam);

                TeamNode node = new TeamNode();
                node.setTeam(newTeam);

                for (Iterator<AcademicStudent> iterator = studentsMap.values().iterator(); iterator.hasNext(); ) {
                    if (session.getTeamMemberMax() > 0 && node.getMembers().size() >= session.getTeamMemberMax()) {
                        break;
                    }
                    AcademicStudent student = iterator.next();
                    if (teamConstraintsChecker.isAutoAddableMember(node, student.getUser().getId())) {
                        ApblTeamMember member = addTeamMember(newTeam.getId(), student.getId(), "#generated#");
                        MemberNode memberNode = new MemberNode();
                        memberNode.setMember(member);
                        node.getMembers().add(memberNode);
                        iterator.remove();
                    }
                }
                maxTeams++;
                if (maxTeams > 1000) {
                    break;
                }
            }
        }
    }

    public List<ProjectNode> findProjectNodes(int sessionId, StringComparator comparator,ObjectFilter<ApblNode> nodeFilter) {
        if(comparator==null){
            comparator= StringComparators.any();
        }
        if(nodeFilter==null){
            nodeFilter=value -> true;
        }
        List<ProjectNode> allProjectNodes = findProjectNodes(sessionId);
        List<TeamNode> teams = new ArrayList<>();


        List<ProjectNode> projects = new ArrayList<>();
        for (ProjectNode project : allProjectNodes) {
            ProjectNode pnode = new ProjectNode();
            pnode.setProject(project.getProject());
            boolean projectDownAccept=nodeFilter.accept(project);
            pnode.setSelectionMatch(
                    projectDownAccept && (
                    project.getProject() != null &&

                    (
                            comparator.matches(project.getProject().getName())
                                    || (project.getProject().getOwner() != null && comparator.matches(project.getProject().getOwner().getContact().getFullTitle()))
                    )
                    )
            );
            boolean someTeam = false;
            if(projectDownAccept) {
                for (TeamNode team : project.getTeams()) {
                    TeamNode tnode = new TeamNode();
                    boolean teamDownAccept = nodeFilter.accept(team);
                    if(teamDownAccept) {
                        teams.add(tnode);
                        tnode.setTeam(team.getTeam());
                        tnode.setSelectionMatch(
                                (
                                        comparator.matches(team.getTeam().getName())
                                                || (team.getTeam().getOwner() != null && comparator.matches(team.getTeam().getOwner().getContact().getFullTitle()))
                                )
                        );
                        boolean someCoach = false;
                        boolean someMember = false;
                        for (CoachNode teacher : team.getCoaches()) {
                            CoachNode cnode = new CoachNode();
                            cnode.setCoaching(teacher.getCoaching());
                            cnode.setSelectionMatch(comparator.matches(teacher.getCoaching().getTeacher().getContact().getFullTitle()));
                            if (!nodeFilter.accept(teacher)) {
                                cnode.setSelectionMatch(false);
                            } else {
                                someCoach |= cnode.isSelectionMatch();
                                //if(cnode.isSelectionMatch() || tnode.isSelectionMatch() || pnode.isSelectionMatch()) {
                                tnode.getCoaches().add(cnode);
                            }
                            //}
                        }
                        for (MemberNode student : team.getMembers()) {
                            MemberNode mnode = new MemberNode();
                            mnode.setMember(student.getMember());
                            mnode.setSelectionMatch(comparator.matches(student.getMember().getStudent().getContact().getFullTitle()));
                            if (!nodeFilter.accept(student)) {
                                mnode.setSelectionMatch(false);
                            } else {
                                someMember |= mnode.isSelectionMatch();
                                //if(mnode.isSelectionMatch() || tnode.isSelectionMatch() || pnode.isSelectionMatch()) {
                                tnode.getMembers().add(mnode);
                            }
                            //}
                        }
                        if (someMember || someCoach || tnode.isSelectionMatch() || pnode.isSelectionMatch()) {
                            pnode.getTeams().add(tnode);
                            someTeam |= true;
                        }
                    }
                }
            }
            if (project.getProject() != null || pnode.getTeams().size() > 0) {
                if (someTeam || pnode.isSelectionMatch()) {
                    projects.add(pnode);
                }
            }
        }
        updateUnsatisfiedTeamConstraints(teams, sessionId);
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

    public void addProgressionLog(ApblProgressionLog log) {
        if (log.getTeam() == null) {
            throw new RuntimeException("Empty Team");
        }
        if (StringUtils.isEmpty(log.getDescription())) {
            throw new RuntimeException("Empty Description");
        }
        if (log.getProgressionPercent() <= 0) {
            throw new RuntimeException("Empty Progression");
        }
        if (log.getProgressionDate() == null) {
            throw new RuntimeException("Empty Date");
        }
        UPA.getPersistenceUnit().persist(log);
    }

    public void addCoachingLog(ApblCoachingLog log) {
        if (log.getCoaching() == null) {
            throw new RuntimeException("Empty Team");
        }
        if (StringUtils.isEmpty(log.getDescription())) {
            throw new RuntimeException("Empty Description");
        }
        if (log.getDurationMinutes() <= 0) {
            throw new RuntimeException("Empty Duration");
        }
        if (log.getAppointmentDate() == null) {
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
        ApblTeam t = new ApblTeam();
        return pu.createQuery("Select u from ApblTeam u where u.session.status.closed = false and exists (Select m from ApblCoaching m where m.teamId=u.id and m.teacherId=:teacherId)")
                .setParameter("teacherId", teacherId)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<ApblTeam> findOpenTeamsByUser(int userId, boolean asMember, boolean asOwner) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ApblTeam t = new ApblTeam();
        if (!asMember && !asOwner) {
            return Collections.EMPTY_LIST;
        }
        return pu.createQuery("Select u from ApblTeam u where u.session.status.closed = false and  ( " +
                "1=2 " +
                (asMember ? ("or exists ((Select m from ApblTeamMember m where m.teamId=u.id and m.student.userId=:userId)) " +
                        "or exists ((Select m from ApblCoaching m where m.teamId=u.id and m.teacher.userId=:userId)) ") : "") +
                (asOwner ?
                        ("or exists ((Select m from ApblProject m where m.ownerId=:userId))" +
                                "or u.ownerId=:userId ") : "") +
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

    public List<ApblTeam> findTeamsBySession(int sessionId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from ApblTeam u where u.sessionId=:sessionId")
                .setParameter("sessionId", sessionId)
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

    public List<ApblProgramSession> findSessionPrograms(int sessionId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQueryBuilder(ApblProgramSession.class).byField("sessionId",sessionId).getResultList();
    }

    public ApblSession findSession(int sessionId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(ApblSession.class, sessionId);
    }

    public List<ApblSession> findAvailableSessions() {
        AcademicTeacher currentTeacher = academic.getCurrentTeacher();
        AcademicStudent currentStudent = academic.getCurrentStudent();
        AppUser currentUser = UserSession.getCurrentUser();
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
