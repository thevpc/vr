/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.integration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseGroup;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseLevel;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseType;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicProgram;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.UPA;

/**
 *
 * @author vpc
 */
public class ParseHelper {

    final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
    final CorePlugin core = VrApp.getBean(CorePlugin.class);

    public AcademicTeacher parseTeacher(String teacherName) {
        if (!StringUtils.isBlank(teacherName)) {
            AcademicTeacher teacher = service.findTeacher(teacherName);
            if (teacher == null) {
                throw new IllegalArgumentException("Teacher not found " + teacherName);
            }
            return teacher;
        }
        return null;
    }

    public List<AcademicTeacher> parseTeacherList(String teacherNames) {
        List<AcademicTeacher> teachers = new ArrayList<>();
        for (String te : StringUtils.split(teacherNames, ",;")) {
            te = te.trim();
            if (!te.isEmpty()) {
                AcademicTeacher teo = service.findTeacher(te);
                if (teo != null) {
                    teachers.add(teo);
                } else if (teo == null) {
                    throw new IllegalArgumentException("Teacher not found " + te);
                }
            }
        }
        return teachers;
    }

    public AcademicCourseType parseCourseType(String courseTypeName) {
        if (!StringUtils.isBlank(courseTypeName)) {
            AcademicCourseType courseType = service.findCourseType(courseTypeName);
            if (courseType == null) {
                throw new IllegalArgumentException("Invalid course type " + courseTypeName);
            }
            return courseType;
        }
        return null;
    }

    public AcademicCourseType parseCourseTypeRequired(String courseTypeName, String requiredFor) {
        AcademicCourseType t = parseCourseType(courseTypeName);
        if (t == null) {
            throw new IllegalArgumentException("Missing course type for " + requiredFor);
        }
        return t;
    }

    public AcademicSemester parseSemester(String stringVal) {
        if (!StringUtils.isBlank(stringVal)) {
            AcademicSemester semester = service.findSemester(stringVal);
            if (semester == null) {
                throw new IllegalArgumentException("Invalid semester " + stringVal);
            }
            return semester;
        }
        return null;
    }

    public AcademicSemester parseSemesterRequired(String name, String requiredFor) {
        AcademicSemester t = parseSemester(name);
        if (t == null) {
            throw new IllegalArgumentException("Missing semester for " + requiredFor);
        }
        return t;
    }

    public AppDepartment parseDepartment(String stringVal) {
        if (!StringUtils.isBlank(stringVal)) {
            AppDepartment value = core.findDepartment(stringVal);
            if (value == null) {
                throw new IllegalArgumentException("Invalid department " + stringVal);
            }
            return value;
        }
        return null;
    }

    public AcademicCourseLevel getOrCreateCourseLevel(AcademicClass studentClass, AcademicSemester semester) {
        String stringVal = studentClass.getName() + "-" + semester.getName();
        AcademicCourseLevel courseLevel = service.findCourseLevel(studentClass.getId(), semester.getId());
        if (courseLevel == null) {
            courseLevel = new AcademicCourseLevel();
            courseLevel.setName(stringVal);
            courseLevel.setCreationDate(new Timestamp(System.currentTimeMillis()));
            courseLevel.setSemester(semester);
            courseLevel.setAcademicClass(studentClass);
            UPA.getPersistenceUnit().persist(courseLevel);
        }
        return courseLevel;
    }

    public AcademicCourseGroup parseOrCreateCourseGroup(String stringVal, AcademicClass aclass, AppPeriod period) {
        if (!StringUtils.isBlank(stringVal)) {
            AcademicCourseGroup courseGroup = service.findCourseGroup(period.getId(), aclass.getId(), stringVal);
            if (courseGroup == null) {
                courseGroup = new AcademicCourseGroup();
                courseGroup.setName(stringVal);
                courseGroup.setAcademicClass(aclass);
                courseGroup.setPeriod(period);
                UPA.getPersistenceUnit().persist(courseGroup);
            }
            return courseGroup;
        }
        return null;
    }

    public AppDepartment parseDepartmentRequired(String name, String requiredFor) {
        AppDepartment value = parseDepartment(name);
        if (value == null) {
            throw new IllegalArgumentException("Missing department for " + requiredFor);
        }
        return value;
    }

    public AcademicProgram parseProgram(String stringVal, Integer departmentId) {
        if (!StringUtils.isBlank(stringVal)) {
            AcademicProgram program = null;
            if (departmentId != null) {
                program = service.findProgram(departmentId, stringVal);
            } else {
                List<AcademicProgram> allByName = service.findPrograms(stringVal);
                if (allByName.size() > 1) {
                    throw new IllegalArgumentException("Too many Programs with name " + stringVal + " . Please mention department");
                } else if (allByName.size() > 0) {
                    program = allByName.get(0);
                }
            }
            if (program == null) {
                throw new IllegalArgumentException("Invalid program " + stringVal);
            }
            return program;
        }
        return null;
    }

    public AcademicProgram parseProgram(String name, Integer departmentId, String requiredFor) {
        AcademicProgram value = parseProgram(name, departmentId);
        if (value == null) {
            throw new IllegalArgumentException("Missing program for " + requiredFor);
        }
        return value;
    }

    public AcademicClass parseClass(String stringVal, Integer programId) {
        if (!StringUtils.isBlank(stringVal)) {
            AcademicClass clazz = null;
            if (programId != null) {
                clazz = service.findAcademicClass(programId, stringVal);
            } else {
                List<AcademicClass> allByName = service.findAcademicClasses(stringVal);
                if (allByName.size() > 1) {
                    throw new IllegalArgumentException("Too many Classes with name " + stringVal + " . Please mention program");
                } else if (allByName.size() > 0) {
                    clazz = allByName.get(0);
                }
            }
            if (clazz == null) {
                throw new IllegalArgumentException("Invalid class " + stringVal);
            }
            return clazz;
        }
        return null;
    }

    public AcademicClass parseClassRequired(String name, Integer departmentId, String requiredFor) {
        AcademicClass value = parseClass(name, departmentId);
        if (value == null) {
            throw new IllegalArgumentException("Missing class for " + requiredFor);
        }
        return value;
    }

    public void chechMatch(AcademicClass c, AcademicProgram p, AppDepartment d) {
        if (c != null && p != null && c.getProgram() != null) {
            if (c.getProgram().getId() != p.getId()) {
                throw new IllegalArgumentException("Class/Program Mismatch " + c.getName() + "/" + p.getName());
            }
        }
        if (p != null && d != null && p.getDepartment() != null) {
            if (p.getDepartment().getId() != d.getId()) {
                throw new IllegalArgumentException("Program/Department Mismatch " + p.getName() + "/" + d.getName());
            }
        }
        if (c != null && d != null && c.getProgram() != null && c.getProgram().getDepartment() != null) {
            if (c.getProgram().getDepartment().getId() != d.getId()) {
                throw new IllegalArgumentException("Class/Department Mismatch " + c.getName() + "/" + d.getName());
            }
        }
    }
}
