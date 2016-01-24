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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.Install;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppDepartment;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipDuration;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipVariant;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

/**
 *
 * @author vpc
 */
@AppPlugin(dependsOn = "academicPlugin", version = "1.2")
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

    public List<AcademicInternshipVariant> findInternshipVariantsByType(int typeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipVariant u where u.internshipTypeId=:typeId")
                .setParameter("typeId", typeId)
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

    public List<AcademicInternship> findActualInternshipsByTeacher(int teacherId, int boardId) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(teacherId);
        AppDepartment d = t.getDepartment();
        AcademicTeacher h = d == null ? null : ap.findHeadOfDepartment(d.getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (h != null && d != null && h.getId() == teacherId) {
            if (boardId <= 0) {
                return pu.createQuery("Select u from AcademicInternship u where (u.boardId==null or u.board.departmentId=:departmentId) and u.internshipStatus.closed=false")
                        .setParameter("departmentId", d.getId())
                        .getEntityList();
            } else {
                return pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                        .setParameter("boardId", boardId)
                        .getEntityList();
            }
        }
        return pu.createQuery("Select u from AcademicInternship u where u.supervisorId=:teacherId or u.secondSupervisorId=:teacherId and u.internshipStatus.closed=false")
                .setParameter("teacherId", teacherId)
                .getEntityList();
    }

    @Install
    public void install() {
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.MyInternships", "Custom.Education.MyInternships");
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.MyInternshipBoards", "Custom.Education.MyInternshipBoards");
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

        for (AppUser appUser : core.resolveUsersByProfileFilter(studentProfiles)) {
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
}
