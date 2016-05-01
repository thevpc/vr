/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.Install;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppDepartment;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipBoardMessage;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipBoardTeacher;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipDuration;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipType;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipVariant;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipSuperviserIntent;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.ext.AcademicInternshipExt;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

/**
 *
 * @author vpc
 */
@AppPlugin(dependsOn = "academicPlugin", version = "1.4")
public class AcademicInternshipPlugin {

    public AcademicInternship findInternship(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternship.class, id);

    }

    public AcademicInternshipStatus findInternshipStatus(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternshipStatus.class, id);

    }

    public AcademicInternshipVariant findInternshipVariant(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternshipVariant.class, id);
    }

    public AcademicInternshipDuration findInternshipDuration(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternshipDuration.class, id);
    }

    public AcademicInternshipBoard findInternshipBoard(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternshipBoard.class, id);
    }

    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByDepartment(int departmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipBoard u where u.enabled=true and u.departmentId=:departmentId")
                .setParameter("departmentId", departmentId)
                .getEntityList();
    }

    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByTeacher(int teacherId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u.board from AcademicInternshipBoardTeacher u where u.board.enabled=true and u.teacherId=:teacherId")
                .setParameter("teacherId", teacherId)
                .getEntityList();
    }

    public List<AcademicInternshipVariant> findInternshipVariantsByType(int typeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipVariant u where u.internshipTypeId=:typeId")
                .setParameter("typeId", typeId)
                .getEntityList();
    }

    public double findInternshipTeacherInternshipsCount(int teacherId, int yearId, int internshipTypeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicInternship> list = pu.createQuery("Select u from AcademicInternship u where u.board.periodId=:periodId and u.board.internshipTypeId=:internshipTypeId and (u.supervisorId=:teacherId or u.secondSupervisorId=:teacherId) and u.internshipStatus.closed=false")
                .setParameter("teacherId", teacherId)
                .setParameter("periodId", yearId)
                .setParameter("internshipTypeId", internshipTypeId)
                .getEntityList();
        double count = 0;
        for (AcademicInternship a : list) {
            double m = 1;
            if (a.getSupervisor() != null && a.getSecondSupervisor() != null) {
                m = 0.5;
            }
            count += m;
        }
        return count;
    }

    public Map<Integer, Number> findInternshipTeachersInternshipsCounts(int yearId, int internshipTypeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicInternship> list = pu.createQuery("Select u from AcademicInternship u where u.board.periodId=:periodId and u.board.internshipTypeId=:internshipTypeId and u.internshipStatus.closed=false")
                .setParameter("periodId", yearId)
                .setParameter("internshipTypeId", internshipTypeId)
                .getEntityList();
        Map<Integer, Number> map = new HashMap<>();
        for (AcademicInternship a : list) {
            if (a.getSupervisor() == null && a.getSecondSupervisor() == null) {

            } else if (a.getSupervisor() != null) {
                Number count = map.get(a.getSupervisor().getId());
                if (count == null) {
                    count = 0;
                }
                count = (count.doubleValue() + 1.0);
                map.put(a.getSupervisor().getId(), count);

            } else if (a.getSecondSupervisor() != null) {
                Number count = map.get(a.getSecondSupervisor().getId());
                if (count == null) {
                    count = 0;
                }
                count = (count.doubleValue() + 1.0);
                map.put(a.getSecondSupervisor().getId(), count);

            } else {
                Number count = map.get(a.getSupervisor().getId());
                if (count == null) {
                    count = 0;
                }
                count = (count.doubleValue() + 0.5);
                map.put(a.getSupervisor().getId(), count);

                count = map.get(a.getSecondSupervisor().getId());
                if (count == null) {
                    count = 0;
                }
                count = (count.doubleValue() + 0.5);
                map.put(a.getSecondSupervisor().getId(), count);
            }
        }
        return map;
    }

    public List<AcademicInternshipBoardTeacher> findInternshipTeachersByBoard(int boardId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipBoardTeacher u where u.boardId=:boardId")
                .setParameter("boardId", boardId)
                .getEntityList();
    }

    public void addBoardMessage(AcademicInternshipBoardMessage m) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        m.setObsUpdateDate(new Date());
        pu.persist(m);
    }
    
    public void removeBoardMessage(int messageId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicInternshipBoardMessage m = pu.findById(AcademicInternshipBoardMessage.class, messageId);
        pu.remove(m);
    }

    public List<AcademicInternshipBoardMessage> findInternshipMessagesByInternship(int internshipId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipBoardMessage u where u.internshipId=:internshipId order by u.obsUpdateDate desc")
                .setParameter("internshipId", internshipId)
                .setHint("navigationDepth", 3)
                .getEntityList();
    }

    public List<AcademicInternshipType> findInternshipTypes() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipType u")
                .getEntityList();
    }

    public List<AcademicInternshipStatus> findInternshipStatusesByType(int typeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipStatus u where u.internshipTypeId=:typeId")
                .setParameter("typeId", typeId)
                .getEntityList();
    }

    public List<AcademicInternshipDuration> findInternshipDurationsByType(int typeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipDuration u where u.internshipTypeId=:typeId")
                .setParameter("typeId", typeId)
                .getEntityList();
    }

    public List<AcademicInternship> findActualInternshipsByStudent(int studentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternship u where u.studentId=:studentId or u.secondStudentId=:studentId and u.internshipStatus.closed=false")
                .setParameter("studentId", studentId)
                .getEntityList();
    }

    public List<AcademicInternship> findActualInternshipsBySupervisor(int teacherId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternship u where u.supervisorId=:teacherId or u.secondSupervisorId=:teacherId and u.internshipStatus.closed=false")
                .setParameter("teacherId", teacherId)
                .getEntityList();
    }

    public List<AcademicInternship> findInternshipsByDepartment(int departmentId, boolean activeOnly) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternship u where u.board.departmentId=:departmentId " + (activeOnly ? "and u.internshipStatus.closed=false" : ""))
                .setParameter("departmentId", departmentId)
                .getEntityList();
    }

    public AcademicInternshipExtList findInternshipsByDepartmentExt(int departmentId, boolean openOnly) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where u.board.departmentId=:departmentId " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
                .setParameter("departmentId", departmentId)
                .getEntityList();
        List<AcademicInternshipSuperviserIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSuperviserIntent u where (u.internship.board.departmentId=:departmentId)" + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                .setParameter("departmentId", departmentId)
                .getEntityList();
        List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipSuperviserIntent u where (u.internship.board.departmentId=:departmentId) " + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                .setParameter("departmentId", departmentId)
                .setHint("navigationDepth", 3)
                .getEntityList();
        return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
    }

    public List<AcademicInternship> findActualInternshipsByTeacher(int teacherId, int boardId) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(teacherId);
        AppDepartment d = t == null ? null : t.getDepartment();
        AcademicTeacher h = d == null ? null : ap.findHeadOfDepartment(d.getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (h != null && d != null && h.getId() == teacherId) {
            if (boardId <= 0) {
                return pu.createQuery("Select u from AcademicInternship u where (u.boardId==null or u.board.departmentId=:departmentId) and u.internshipStatus.closed=false")
                        .setParameter("departmentId", d.getId())
                        .setHint("navigationDepth", 2)
                        .getEntityList();
            } else {
                return pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                        .setParameter("boardId", boardId)
                        .setHint("navigationDepth", 2)
                        .getEntityList();
            }
        }

        if (boardId <= 0) {
            ArrayList<AcademicInternship> all = new ArrayList<AcademicInternship>();
            for (AcademicInternshipBoard b : findEnabledInternshipBoardsByTeacher(teacherId)) {
                List<AcademicInternship> curr = pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                        .setParameter("boardId", b.getId())
                        .setHint("navigationDepth", 2)
                        .getEntityList();
                all.addAll(curr);
            }
            return all;
        } else {
            return pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                    .setParameter("boardId", boardId)
                    .setHint("navigationDepth", 2)
                    .getEntityList();
        }
    }

    private AcademicInternshipExtList mergeAcademicInternshipExt(
            List<AcademicInternship> internships,
            List<AcademicInternshipSuperviserIntent> supervisorIntents,
            List<AcademicInternshipBoardMessage> messages
    ) {
        if (internships == null) {
            internships = new ArrayList<>();
        }
        if (supervisorIntents == null) {
            supervisorIntents = new ArrayList<>();
        }
        if (messages == null) {
            messages = new ArrayList<>();
        }
        List<AcademicInternshipExt> exts = new ArrayList<>();
        Map<Integer, AcademicInternshipExt> map = new HashMap<>();
        for (AcademicInternship i : internships) {
            AcademicInternshipExt e = new AcademicInternshipExt();
            e.setInternship(i);
            e.setMessages(new ArrayList<AcademicInternshipBoardMessage>());
            e.setSuperviserIntents(new ArrayList<AcademicInternshipSuperviserIntent>());
            map.put(i.getId(), e);
            exts.add(e);
        }
        for (AcademicInternshipSuperviserIntent s : supervisorIntents) {
            map.get(s.getInternship().getId()).getSuperviserIntents().add(s);
        }
        for (AcademicInternshipBoardMessage s : messages) {
            map.get(s.getInternship().getId()).getMessages().add(s);
        }

        AcademicInternshipExtList list = new AcademicInternshipExtList();
        list.setInternshipExts(exts);
        list.setInternships(internships);
        list.setSuperviserIntents(supervisorIntents);
        list.setMessages(messages);
        return list;
    }

    public AcademicInternshipExtList findInternshipsByTeacherExt(int teacherId, int boardId, int deptId, int internshipTypeId, boolean openOnly) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        CorePlugin cp = VrApp.getBean(CorePlugin.class);
        AcademicTeacher t = teacherId < 0 ? null : ap.findTeacher(teacherId);
        AppDepartment d = deptId < 0 ? (t == null ? null : t.getDepartment()) : cp.findDepartment(deptId);
        AcademicTeacher h = d == null ? null : ap.findHeadOfDepartment(d.getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (h != null && d != null && h.getId() == teacherId) {
            if (boardId <= 0) {
                if (internshipTypeId < 0) {
                    List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where (u.boardId==null or u.board.departmentId=:departmentId) " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
                            .setParameter("departmentId", d.getId())
                            .setHint("navigationDepth", 2)
                            .getEntityList();
                    List<AcademicInternshipSuperviserIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSuperviserIntent u where (u.internship.boardId==null or u.internship.board.departmentId=:departmentId)" + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                            .setParameter("departmentId", d.getId())
                            .getEntityList();
                    List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u where (u.internship.boardId==null or u.internship.board.departmentId=:departmentId) " + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                            .setParameter("departmentId", d.getId())
                            .setHint("navigationDepth", 3)
                            .getEntityList();
                    return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
                } else {
                    List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where (u.boardId==null or (u.board.departmentId=:departmentId and u.board.internshipTypeId=:internshipTypeId)) " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
                            .setParameter("departmentId", d.getId())
                            .setParameter("internshipTypeId", internshipTypeId)
                            .setHint("navigationDepth", 2)
                            .getEntityList();
                    List<AcademicInternshipSuperviserIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSuperviserIntent u where (u.internship.boardId==null or (u.internship.board.departmentId=:departmentId  and u.internship.board.internshipTypeId=:internshipTypeId)) " + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                            .setParameter("departmentId", d.getId())
                            .setParameter("internshipTypeId", internshipTypeId)
                            .getEntityList();
                    List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u where (u.internship.boardId==null or (u.internship.board.departmentId=:departmentId and u.internship.board.internshipTypeId=:internshipTypeId)) " + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                            .setParameter("departmentId", d.getId())
                            .setParameter("internshipTypeId", internshipTypeId)
                            .setHint("navigationDepth", 3)
                            .getEntityList();
                    return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
                }
            } else {
                List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId " 
                        + (openOnly ? " and u.internshipStatus.closed=false" : ""))
                        .setParameter("boardId", boardId)
                        .setHint("navigationDepth", 2)
                        .getEntityList();
                List<AcademicInternshipSuperviserIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSuperviserIntent u where  u.internship.boardId=:boardId " + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                        .setParameter("boardId", boardId)
                        .getEntityList();
                List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u where u.internship.boardId=:boardId  " + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                        .setParameter("boardId", boardId)
                        .setHint("navigationDepth", 3)
                        .getEntityList();
                return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
            }
        }

        if (boardId <= 0) {
            StringBuilder boardList = new StringBuilder();
            List<AcademicInternshipBoard> goodBoards = pu.createQuery("Select u.board from AcademicInternshipBoardTeacher u where "
                    + " 1=1"
                    + ((openOnly) ? " and u.board.enabled=true" : "")
                    + ((deptId > 0) ? (" and u.board.departmentId=" + deptId) : "")
                    + ((teacherId > 0) ? (" and u.teacherId=" + teacherId) : "")
                    + ((internshipTypeId > 0) ? (" and u.board.internshipTypeId=" + internshipTypeId) : "")
            )
                    .getEntityList();
            for (AcademicInternshipBoard b : goodBoards) {
                if (boardList.length() > 0) {
                    boardList.append(",");
                }
                boardList.append(b.getId());
            }
            if (boardList.length() == 0) {
                List<AcademicInternship> internships = new ArrayList<>();
                List<AcademicInternshipSuperviserIntent> supervisorIntents = new ArrayList<>();
                List<AcademicInternshipBoardMessage> messages = new ArrayList<>();
                return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
            }
            List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where u.boardId in (" + boardList + ") " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
                    .setHint("navigationDepth", 2)
                    .getEntityList();
            List<AcademicInternshipSuperviserIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSuperviserIntent u where  u.internship.boardId in (" + boardList + ")  " + (openOnly ? "and u.internship.internshipStatus.closed=false" : ""))
                    .getEntityList();
            List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u where u.internship.boardId in (" + boardList + ") " + (openOnly ? "and u.internship.internshipStatus.closed=false" : ""))
                    .setHint("navigationDepth", 3)
                    .getEntityList();
            return mergeAcademicInternshipExt(internships, supervisorIntents, messages);

        } else {
            List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                    .setParameter("boardId", boardId)
                    .setHint("navigationDepth", 2)
                    .getEntityList();
            List<AcademicInternshipSuperviserIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSuperviserIntent u where  u.internship.boardId=:boardId  " + (openOnly ? "and u.internship.internshipStatus.closed=false" : ""))
                    .setParameter("boardId", boardId)
                    .getEntityList();
            List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u where u.internship.boardId=:boardId " + (openOnly ? "and u.internship.internshipStatus.closed=false" : ""))
                    .setParameter("boardId", boardId)
                    .setHint("navigationDepth", 3)
                    .getEntityList();
            return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
        }
    }

    public List<AcademicInternship> findInternships(int teacherId, int boardId, int deptId, int internshipTypeId, boolean openOnly) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        CorePlugin cp = VrApp.getBean(CorePlugin.class);
        AcademicTeacher t = teacherId < 0 ? null : ap.findTeacher(teacherId);
        AppDepartment d = deptId < 0 ? (t == null ? null : t.getDepartment()) : cp.findDepartment(deptId);
        AcademicTeacher h = d == null ? null : ap.findHeadOfDepartment(d.getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (h != null && d != null && h.getId() == teacherId) {
            if (boardId <= 0) {
                if (internshipTypeId < 0) {
                    return  pu.createQuery("Select u from AcademicInternship u where (u.boardId==null or u.board.departmentId=:departmentId) " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
                            .setParameter("departmentId", d.getId())
                            .setHint("navigationDepth", 2)
                            .getEntityList();
                } else {
                    return pu.createQuery("Select u from AcademicInternship u where (u.boardId==null or (u.board.departmentId=:departmentId and u.board.internshipTypeId=:internshipTypeId)) " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
                            .setParameter("departmentId", d.getId())
                            .setParameter("internshipTypeId", internshipTypeId)
                            .setHint("navigationDepth", 2)
                            .getEntityList();
                }
            } else {
                return pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId " 
                        + (openOnly ? " and u.internshipStatus.closed=false" : ""))
                        .setParameter("boardId", boardId)
                        .setHint("navigationDepth", 2)
                        .getEntityList();
            }
        }

        if (boardId <= 0) {
            StringBuilder boardList = new StringBuilder();
            List<AcademicInternshipBoard> goodBoards = pu.createQuery("Select u.board from AcademicInternshipBoardTeacher u where "
                    + " 1=1"
                    + ((openOnly) ? " and u.board.enabled=true" : "")
                    + ((deptId > 0) ? (" and u.board.departmentId=" + deptId) : "")
                    + ((teacherId > 0) ? (" and u.teacherId=" + teacherId) : "")
                    + ((internshipTypeId > 0) ? (" and u.board.internshipTypeId=" + internshipTypeId) : "")
            )
                    .getEntityList();
            for (AcademicInternshipBoard b : goodBoards) {
                if (boardList.length() > 0) {
                    boardList.append(",");
                }
                boardList.append(b.getId());
            }
            if (boardList.length() == 0) {
                return new ArrayList<>();
            }
            return pu.createQuery("Select u from AcademicInternship u where u.boardId in (" + boardList + ") " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
                    .setHint("navigationDepth", 2)
                    .getEntityList();

        } else {
            return pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                    .setParameter("boardId", boardId)
                    .setHint("navigationDepth", 2)
                    .getEntityList();
        }
    }

    @Install
    public void install() {
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.MyInternships", "Custom.Education.MyInternships");
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.MyInternshipBoards", "Custom.Education.MyInternshipBoards");
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.AllInternships", "Custom.Education.AllInternships");
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.InternshipBoardsStat", "Custom.Education.InternshipBoardsStat");
    }

    public void generateInternships(AcademicInternship internship, String studentProfiles) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AcademicPlugin acad = VrApp.getBean(AcademicPlugin.class);
        DecimalFormat df = new DecimalFormat("000");
        int pos = 1;
        PersistenceUnit pu = UPA.getPersistenceUnit();

        List<AcademicInternship> allFound = pu.createQuery("Select u from AcademicInternship u where "
                + "u.boardId=:boardId "
        )
                .setParameter("departmentId", internship.getBoard().getId())
                .getEntityList();
        HashSet<String> validCodes = new HashSet<>();
        for (AcademicInternship vc : allFound) {
            validCodes.add(vc.getCode());
        }

        for (AppUser appUser : core.findUsersByProfileFilter(studentProfiles)) {
            AcademicStudent student = acad.findStudentByUser(appUser.getId());
            if (student != null) {
                AcademicInternship i = pu.createQuery("Select u from AcademicInternship u where "
                        + "(u.studentId=:studentId or u.secondStudentId==:studentId) "
                        + "and u.departmentId=:departmentId "
                        + "and u.periodId=:periodId "
                        + "and u.programId=:programId "
                        + "and u.internshipTypeId=:internshipTypeId "
                )
                        .setParameter("studentId", student.getId())
                        .setParameter("boardId", internship.getBoard().getId())
                        .getEntity();

                if (i == null) {
                    i = new AcademicInternship();
                    i.setStudent(student);
                    i.setBoard(internship.getBoard());
                    i.setDescription(internship.getDescription());
                    i.setStartDate(internship.getStartDate());
                    i.setEndDate(internship.getEndDate());
                    i.setExamDate(internship.getExamDate());
                    i.setInternshipStatus(internship.getInternshipStatus());
                    i.setMainDiscipline(internship.getMainDiscipline());
                    i.setName(internship.getName());
                    i.setValidationObservations(internship.getValidationObservations());
                    i.setTechnologies(internship.getTechnologies());
//                    i.setSecondStudent(student);
                    while (validCodes.contains(df.format(pos))) {
                        pos++;
                    }
                    i.setCode(df.format(pos));
                    validCodes.add(i.getCode());
                    pu.persist(i);
                    pos++;
                } else {
//                    i.setStudent(student);
//                    i.setDepartment(internship.getDepartment());
//                    i.setDescription(internship.getDescription());
//                    i.setStartDate(internship.getStartDate());
//                    i.setEndDate(internship.getEndDate());
//                    i.setExamDate(internship.getExamDate());
//                    i.setInternshipStatus(internship.getInternshipStatus());
//                    i.setInternshipType(internship.getInternshipType());
//                    i.setMainDiscipline(internship.getMainDiscipline());
//                    i.setName(internship.getName());
//                    i.setPeriod(internship.getPeriod());
//                    i.setProgram(internship.getProgram());
//                    i.setValidationObservations(internship.getValidationObservations());
//                    i.setTechnologies(internship.getTechnologies());
//                    i.setSecondStudent(student);
//                    while (validCodes.contains(df.format(pos))) {
//                        pos++;
//                    }
//                    i.setCode(df.format(pos));
//                    validCodes.add(i.getCode());
//                    pu.persist(i);
                }
            }
        }
    }

    public static void main(String[] args) {
        String str = "P62:FBA+AD+KK	P14:OBK+IM+TBS	P4:ABA+MLA+WHA	P42:AB+NJ+SBA\n"
                + "P29:KK+NJ+MAH	P18:IM+TA+AD	P13:NS+SBJ+TBS	P26:IB+NK+WC\n"
                + "P27:AD+KK+AB	P1:WC+LH+ABA	P10:IM+SBA+BB	P61:IB+HM+MLA\n"
                + "	P45:HM+AD+MLA	P28:TBS+JBT+NK	\n"
                + "			\n"
                + "P68:NJ+FBA+MAH	P39:LH+AM+HM	P34:AB+IS+BB	\n"
                + "P21:NJ+WAL+FBA	P57:SBJ+IS+OBK	P9:BB+HM+NK	\n"
                + "P56:NJ+AB+AM	P48:HM+KK+IS+NK		\n"
                + "P31:KK+FBA+AB			";

        try {
            BufferedReader r = new BufferedReader(new StringReader(str));
            String line = null;
            int row = 0;
            while ((line = r.readLine()) != null) {
                HashSet<String> rowSet = new HashSet<>();
                row++;
                for (String s : line.split("[ :+\t]+")) {
                    if (s.length() > 0) {
                        if (rowSet.contains(s)) {
                            System.err.println("**** r=" + row + " Duplicate " + s + "    *********************************** : " + line);
                        } else {
                            rowSet.add(s);
                        }
                    }
                }
//                System.out.println(rowSet);
            }
        } catch (IOException ex) {
            Logger.getLogger(AcademicInternshipPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addSupervisorIntent(int internship, int teacherId) {
        AcademicInternshipSuperviserIntent a = findInternshipTeacherIntent(internship, teacherId);
        if (a == null) {
            a = new AcademicInternshipSuperviserIntent();
            AcademicInternship i = findInternship(internship);
            AcademicTeacher t = VrApp.getBean(AcademicPlugin.class).findTeacher(teacherId);
            if (i != null && t != null) {
                a.setInternship(i);
                a.setTeacher(t);
                PersistenceUnit pu = UPA.getPersistenceUnit();
                pu.persist(a);
            }
        }
    }

    public void removeSupervisorIntent(int internship, int teacherId) {
        AcademicInternshipSuperviserIntent a = findInternshipTeacherIntent(internship, teacherId);
        if (a != null) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            pu.remove(a);
        }
    }

    public List<AcademicTeacher> findInternshipSuperviserIntents(int internship) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicInternshipSuperviserIntent> intents = pu.createQuery("Select u from AcademicInternshipSuperviserIntent u where u.internshipId=:id")
                .setParameter("id", internship)
                .getEntityList();
        List<AcademicTeacher> all = new ArrayList<>();
        for (AcademicInternshipSuperviserIntent aa : intents) {
            all.add(aa.getTeacher());
        }
        return all;
    }

    public AcademicInternshipSuperviserIntent findInternshipTeacherIntent(int internship, int teacherId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipSuperviserIntent u where u.internshipId=:id and u.teacherId=:teacherId")
                .setParameter("id", internship)
                .setParameter("teacherId", teacherId)
                .getEntity();
    }
}
