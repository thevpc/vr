/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFS;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgram;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseLevel;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.common.utils.Convert;
import net.vpc.common.utils.DoubleParserConfig;
import net.vpc.common.utils.IntegerParserConfig;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseGroup;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;
import net.vpc.app.vainruling.api.TraceService;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppCivility;
import net.vpc.app.vainruling.api.model.AppContact;
import net.vpc.app.vainruling.api.model.AppDepartment;
import net.vpc.app.vainruling.api.model.AppGender;
import net.vpc.app.vainruling.api.model.AppProfile;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.commonmodel.service.CommonModelPlugin;
import net.vpc.app.vainruling.api.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudentStage;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.utils.Chronometer;
import net.vpc.common.utils.PlatformTypes;
import net.vpc.upa.Action;

import net.vpc.upa.bulk.DataReader;
import net.vpc.upa.UPA;
import net.vpc.upa.bulk.DataRow;
import net.vpc.upa.bulk.ParseFormatManager;
import net.vpc.upa.bulk.SheetParser;

/**
 *
 * @author vpc
 */
public class XlsxLoadImporter {

    private static Logger log = Logger.getLogger(XlsxLoadImporter.class.getName());
    private static DoubleParserConfig LENIENT_1 = DoubleParserConfig.LENIENT.setNullValue(1).setInvalidValue(1);

    private ParseFormatManager pfm = UPA.getBootstrapFactory().createObject(ParseFormatManager.class);

    public XlsxLoadImporter() {
    }

    public void importTeacherDegrees() throws IOException {
        AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        int pos = 1;
        service.add(new AcademicTeacherDegree("A",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 11.0, pos++));
        service.add(new AcademicTeacherDegree("MA",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ (2.0 / 3.0),/*PM*/ (2.0 / 3.0) * (2.0 / 3.0),/*DU*/ 9.5, pos++));
        service.add(new AcademicTeacherDegree("MC",/*C*/ 1.0,/*TD*/ 1.0 / 1.33,/*TP*/ 0.5,/*PM*/ 0.5 * (2.0 / 3.0),/*DU*/ 5.5, pos++));
        service.add(new AcademicTeacherDegree("P",/*C*/ 1.0,/*TD*/ 1.0 / 1.33,/*TP*/ 0.5,/*PM*/ 0.5 * (2.0 / 3.0),/*DU*/ 5.5, pos++));
        service.add(new AcademicTeacherDegree("C",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 11, pos++));
        service.add(new AcademicTeacherDegree("CD",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 8, pos++));
        service.add(new AcademicTeacherDegree("V",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 0, pos++));
        service.add(new AcademicTeacherDegree("A'",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 0, pos++));
        service.add(new AcademicTeacherDegree("MA'",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ (2.0 / 3.0),/*PM*/ (2.0 / 3.0) * (2.0 / 3.0),/*DU*/ 0, pos++));
        service.add(new AcademicTeacherDegree("MC'",/*C*/ 1.0,/*TD*/ 1.0 / 1.33,/*TP*/ 0.5,/*PM*/ 0.5 * (2.0 / 3.0),/*DU*/ 0, pos++));
    }

    public void importDepartments(VFile file) throws IOException {
        AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = new Chronometer();
        File tmp = VFS.copyNativeTempFile(file);
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(true);
        DataReader rows = sp.parse();
        long count = 0;
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
            String code = Convert.toString(values[0]);
            AppDepartment curr = service.findDepartment(code);
            if (curr == null) {
                AppDepartment d = new AppDepartment();
                d.setCode(code);
                d.setName(Convert.toString(values[1]));
                AppDepartment parent = service.findDepartment(Convert.toString(values[2]));
                d.setParent(parent);
                d.setName2(Convert.toString(values[3]));
                service.add(d);
                count++;
            }
        }
        TraceService trace = VrApp.getBean(TraceService.class);
        trace.trace("importDepartments", "importDepartments from " + file + " in " + ch.stop() + " (" + count + " rows)", null, getClass().getSimpleName(), Level.INFO);
        log.log(Level.INFO, "importDepartments from {0} in {1}", new Object[]{file, ch.stop()});
    }

    public void importTeacherDegrees(VFile file) throws IOException {
        AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = new Chronometer();
        File tmp = VFS.copyNativeTempFile(file);
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(true);
        DataReader rows = sp.parse();
        long count = 0;
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
            AcademicTeacherDegree d = new AcademicTeacherDegree();
            String code = Convert.toString(values[0]);
            AcademicTeacherDegree old = service.findTeacherDegree(code);
            if (old == null) {
                d.setCode(code);
                d.setName(Convert.toString(values[0]));
            } else {
                d = old;
            }
            d.setValueC(Convert.toDouble(values[1]));
            d.setValueTD(Convert.toDouble(values[2]));
            d.setValueTP(Convert.toDouble(values[3]));
            d.setValuePM(Convert.toDouble(values[4]));
            d.setValueDU(Convert.toDouble(values[5]));
            d.setPosition(Convert.toInteger(values[6]));
            if (old == null) {
                service.add(d);
            } else {
                service.update(d);
            }
            count++;
        }
        TraceService trace = VrApp.getBean(TraceService.class);
        trace.trace("importTeacherDegrees", "importDepartments from " + file + " in " + ch.stop() + " (" + count + " rows)", null, getClass().getSimpleName(), Level.INFO);
        log.log(Level.INFO, "importTeacherDegrees from {0} in {1}", new Object[]{file, ch.stop()});
    }

    private static class ImportFile implements Comparable<ImportFile> {

        VFile file;
        int order;

        public ImportFile(VFile file, int order) {
            this.file = file;
            this.order = order;
        }

        @Override
        public int compareTo(ImportFile o) {
            return order - o.order;
        }
    }

    private List<ImportFile> locateImportFile(VFile file, ImportOptions importOptions) throws IOException {
        if (importOptions == null) {
            importOptions = new ImportOptions();
        }
        if (file.exists()) {

            List<ImportFile> all = new ArrayList<>();
            if (file.isDirectory()) {
                if (importOptions.getMaxDepth() < 0 || importOptions.getMaxDepth() > 0) {
                    ImportOptions i = importOptions.copy();
                    if (i.getMaxDepth() > 0) {
                        i.setMaxDepth(i.getMaxDepth() - 1);
                    }
                    for (VFile f : file.listFiles()) {
                        all.addAll(locateImportFile(f, i));
                    }
                }
                return all;
            }

            if (file.getName().equals("departments.xlsx")) {
                return Arrays.asList(new ImportFile(file, 1));
            }
            if (file.getName().equals("teacher-degrees.xlsx")) {
                return Arrays.asList(new ImportFile(file, 2));
            }
            if (file.getName().equals("teachers.xlsx") || file.getName().endsWith(".teachers.xlsx")) {
                return Arrays.asList(new ImportFile(file, 3));
            }
            if (file.getName().equals("students.xlsx") || file.getName().endsWith(".students.xlsx")) {
                return Arrays.asList(new ImportFile(file, 4));
            }
            if (file.getName().equals("course-assignments.xlsx")) {
                return Arrays.asList(new ImportFile(file, 5));
            }
        }
        return Collections.EMPTY_LIST;
    }

    public int importFile(VFile file, ImportOptions importOptions) throws IOException {
        if (importOptions == null) {
            importOptions = new ImportOptions();
        }
        int count = 0;
        TraceService trace = VrApp.getBean(TraceService.class);
        long start = System.currentTimeMillis();
        if (!file.exists()) {
            return count;
        }

        if (file.isDirectory()) {
            if (importOptions.getMaxDepth() < 0 || importOptions.getMaxDepth() > 0) {
                ImportOptions i = importOptions.copy();
                if (i.getMaxDepth() > 0) {
                    i.setMaxDepth(i.getMaxDepth() - 1);
                }
                List<ImportFile> locateImportFile = locateImportFile(file, importOptions);
                Collections.sort(locateImportFile);
                for (ImportFile f : locateImportFile) {
                    count += importFile(f.file, i);
                }
            }
            return count;
        }

        if (file.getName().equals("departments.xlsx")) {
            importDepartments(file);
        }
        if (file.getName().equals("teacher-degrees.xlsx")) {
            if (file.length() > 0) {
                count++;
                importTeacherDegrees(file);
            } else {
                count++;
                trace.trace("importTeachingLoad", "Import Default Teacher Degrees", null, getClass().getSimpleName(), Level.INFO);
                XlsxLoadImporter.this.importTeacherDegrees();
            }
        }
        if (file.getName().equals("teachers.xlsx") || file.getName().endsWith(".teachers.xlsx")) {
            count++;
            importTeachers(file);
        }
        if (file.getName().equals("students.xlsx") || file.getName().endsWith(".students.xlsx")) {
            count++;
            importStudents(file);
        }
        if (file.getName().equals("course-assignments.xlsx")) {
            count++;
            importCourseAssignments(file, importOptions);
        }
        long end = System.currentTimeMillis();
        if (count > 0) {
            trace.trace("importTeachingLoad", "data file " + file.getPath() + " imported in " + Chronometer.formatPeriod(end - start), null, getClass().getSimpleName(), Level.INFO);
            System.out.println("data file " + file.getPath() + " imported in " + Chronometer.formatPeriod(end - start));
        } else {
            trace.trace("importTeachingLoad", "ignored data file " + file.getPath(), null, getClass().getSimpleName(), Level.INFO);
            System.out.println("ignored data file " + file.getPath());
        }
        return count;
    }

    public void importTeachers(VFile file) throws IOException {
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = new Chronometer();
        log.log(Level.INFO, "importTeachers from {0}", file);
        File tmp = VFS.copyNativeTempFile(file);
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(true);
        sp.setSheetIndex(0);
        sp.setSkipRows(0);
        DataReader rows = sp.parse();
        final int COL_FIRST_NAME = 1;
        final int COL_LAST_NAME = 2;
        final int COL_DEGREE = 3;
        final int COL_SITUATION = 4;
        final int COL_WEEK_LOAD_1 = 5;
        final int COL_WEEK_LOAD_2 = 6;
        final int COL_GENDER = 7;
        final int COL_CIVILITY = 8;
        final int COL_EMAIL = 9;
        final int COL_DISCIPLINE = 10;
        final int COL_SITUATION2 = 11;
        final int COL_FIRST_NAME2 = 12;
        final int COL_LAST_NAME2 = 13;
        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        final AppUserType teacherType = VRApp.getBean(CorePlugin.class).findUserType("Teacher");
        long count = 0;
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
//            if (!"x".equalsIgnoreCase(Convert.toString(values[8]))) {
            AcademicTeacher academicTeacher = new AcademicTeacher();
            String fs = core.validateName(Convert.toString(values[COL_FIRST_NAME]));
            String ln = core.validateName(Convert.toString(values[COL_LAST_NAME]));
            String nin = null;// national identification number aca CIN
            if (!StringUtils.isEmpty(nin) || !StringUtils.isEmpty(fs) || !StringUtils.isEmpty(ln)) {
                AppContact contact = new AppContact();
                contact.setNin(nin);
                contact.setFirstName(fs);
                contact.setLastName(ln);
                contact.setFullName(core.validateName(AppContact.getName(contact)));
                contact = core.findOrCreateContact(contact);
                AcademicTeacher oldAcademicTeacher = service.findTeacherByContact(contact.getId());
                if (oldAcademicTeacher != null) {
                    academicTeacher = oldAcademicTeacher;
                } else {
                    academicTeacher.setContact(contact);
                }
                String fs2 = Convert.toString(values[COL_FIRST_NAME2]);
                String ln2 = Convert.toString(values[COL_LAST_NAME2]);
                contact.setFirstName2(fs2);
                contact.setLastName2(ln2);
                contact.setFullName2(AppContact.getName2(contact));

                List<AcademicTeacherSemestrialLoad> semestrialLoads = new ArrayList<>();
                String degreeString = Convert.toString(values[COL_DEGREE]);
                if (degreeString == null) {
                    throw new NoSuchElementException("Missing Degree for " + academicTeacher.getContact().getFullName());
                }
                AcademicTeacherDegree degree = service.findTeacherDegree(degreeString);
                if (degree == null) {
                    throw new NoSuchElementException("Degree Not Found " + degreeString);
                }
                academicTeacher.setDegree(degree);
                {
                    String stringVal = Convert.toString(values[COL_SITUATION]);
                    AcademicTeacherSituation v = service.findTeacherSituation(stringVal);
                    if (v == null) {
                        v = new AcademicTeacherSituation();
                        v.setName(stringVal);
                        v.setName2(Convert.toString(values[COL_SITUATION2]));
                        service.add(v);
                    }
                    academicTeacher.setSituation(v);
                }
                {
                    AcademicTeacherSemestrialLoad sload = new AcademicTeacherSemestrialLoad();
                    sload.setTeacher(academicTeacher);
                    sload.setWeeksLoad(Convert.toInteger(values[COL_WEEK_LOAD_1], IntegerParserConfig.LENIENT));
                    sload.setSemester(1);
                    semestrialLoads.add(sload);

                    sload = new AcademicTeacherSemestrialLoad();
                    sload.setTeacher(academicTeacher);
                    sload.setWeeksLoad(Convert.toInteger(values[COL_WEEK_LOAD_2], IntegerParserConfig.LENIENT));
                    sload.setSemester(2);
                    semestrialLoads.add(sload);
                }
                {
                    String stringVal = Convert.toString(values[COL_GENDER]);
                    AppGender v = service.findGender(stringVal);
                    if (v == null) {
                        v = new AppGender();
                        v.setName(stringVal);
                        service.add(v);
                    }
                    contact.setGender(v);
                }
                {
                    String stringVal = Convert.toString(values[COL_CIVILITY]);
                    AppCivility v = service.findCivility(stringVal);
                    if (v == null) {
                        v = new AppCivility();
                        v.setName(stringVal);
                        service.add(v);
                    }
                    contact.setCivility(v);
                }

                academicTeacher.getContact().setEmail(Convert.toString(values[COL_EMAIL]));
                academicTeacher.setDiscipline(VrApp.getBean(AcademicPlugin.class).formatDisciplinesNames(Convert.toString(values[COL_DISCIPLINE]), true));
                service.update(contact);
                if (oldAcademicTeacher == null) {
                    service.add(academicTeacher);
                } else {
                    service.update(academicTeacher);
                }
                count++;
                final AcademicTeacher finalAcademicTeacher = academicTeacher;
                UPA.getContext().invokePrivileged(new Action<Object>() {

                    @Override
                    public Object run() {
                        service.addUserForTeacher(finalAcademicTeacher);
                        return null;
                    }

                }, null);
//                service.add(tal);
                final List<AcademicTeacherSemestrialLoad> academicTeacherSemestrialLoads = service.findTeacherSemestrialLoads(academicTeacher.getId());
                for (AcademicTeacherSemestrialLoad sload : semestrialLoads) {
                    boolean found = false;
                    for (AcademicTeacherSemestrialLoad a : academicTeacherSemestrialLoads) {
                        if (a.getSemester() == sload.getSemester()) {
                            a.setWeeksLoad(sload.getWeeksLoad());
                            service.update(a);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        service.add(sload);
                    }
                }
            }
//            }
        }
        TraceService trace = VrApp.getBean(TraceService.class);
        trace.trace("importTeachers", "importTeachers from " + file + " in " + ch.stop() + " (" + count + " rows)", null, getClass().getSimpleName(), Level.INFO);
        log.log(Level.INFO, "importTeachers from {0} in {1} " + " (" + count + " rows)", new Object[]{file, ch.stop()});
    }

    public int importStudents(VFile file) throws IOException {
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = new Chronometer();
        log.log(Level.INFO, "importStudents from {0}", file);
        File tmp = VFS.copyNativeTempFile(file);
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(true);
        sp.setSheetIndex(0);
        sp.setSkipRows(0);
        DataReader rows = sp.parse();
        int col = -1;
        final int COL_NIN = ++col;
        final int COL_SUBSCRIPTION_NBR = ++col;
        final int COL_FIRST_NAME = ++col;
        final int COL_LAST_NAME = ++col;
        final int COL_EMAIL = ++col;
        final int COL_GSM = ++col;
        final int COL_YEAR1 = ++col;
        final int COL_CLASS = ++col;
        final int COL_GENDER = ++col;
        final int COL_CIVILITY = ++col;
        final int COL_LAST_NAME2 = ++col;
        final int COL_FIRST_NAME2 = ++col;
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        CommonModelPlugin common = VrApp.getBean(CommonModelPlugin.class);
        Map<String, AppGender> genders = new HashMap<>();
        Map<String, AppProfile> profiles = new HashMap<>();
        for (AcademicClass cls : service.findAcademicClasses()) {
            String s = cls.getName();
            s = s.replace("(", "_").replace(")", "_").replace("+", "_").replace("+", "_");
            AppProfile p = core.findOrCreateProfile(cls.getName());
            profiles.put(s, p);
            profiles.put(cls.getName(), p);
        }
        for (AppGender g : core.findGenders()) {
            genders.put(g.getName().toUpperCase(), g);
        }
        int count = 0;
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
//            if (!"x".equalsIgnoreCase(Convert.toString(values[8]))) {
            AcademicStudent academicStudent = new AcademicStudent();
            String fs = core.validateName(Convert.toString(values[COL_FIRST_NAME]));
            String ln = core.validateName(Convert.toString(values[COL_LAST_NAME]));
            String nin = Convert.toString(values[COL_NIN]);
            if (!StringUtils.isEmpty(nin) || !StringUtils.isEmpty(fs) || !StringUtils.isEmpty(ln)) {
                AppContact contact = new AppContact();
                contact.setNin(nin);
                contact.setFirstName(fs);
                contact.setLastName(ln);
                contact.setFullName(core.validateName(AppContact.getName(contact)));
                String fs2 = Convert.toString(values[COL_FIRST_NAME2]);
                contact = core.findOrCreateContact(contact);
                AcademicStudent oldAcademicStudent = service.findStudentByContact(contact.getId());
                if (oldAcademicStudent != null) {
                    academicStudent = oldAcademicStudent;
                } else {
                    academicStudent.setContact(contact);
                }
                String ln2 = Convert.toString(values[COL_LAST_NAME2]);
                contact.setFirstName2(fs2);
                contact.setLastName2(ln2);
                contact.setFullName2(AppContact.getName2(contact));
                contact.setPhone1(Convert.toString(values[COL_GSM]));
                String periodName = Convert.toString(values[COL_YEAR1]);

                AppPeriod period = common.findPeriod(periodName);
                if (period == null) {
                    throw new NoSuchElementException("Period Not Found " + periodName);
                }
                academicStudent.setFirstSubscription(period);

                AcademicClass level = service.findStudentClass(Convert.toString(values[COL_CLASS]));
                if (level == null) {
                    throw new NoSuchElementException("CourseLevel Not Found " + Convert.toString(values[COL_CLASS]));
                }
                academicStudent.setLastClass1(level);
                if (level.getProgram() != null) {
                    academicStudent.setDepartment(level.getProgram().getDepartment());
                }
                {
                    String stringVal = Convert.toString(values[COL_GENDER]);
                    AppGender v = genders.get(stringVal.toUpperCase());
                    if (v == null) {
                        v = new AppGender();
                        v.setName(stringVal);
                        service.add(v);
                        genders.put(stringVal.toUpperCase(), v);
                    }
                    contact.setGender(v);
                }
                academicStudent.setSubscriptionNumber(Convert.toString(values[COL_SUBSCRIPTION_NBR]));
                {
                    String stringVal = Convert.toString(values[COL_CIVILITY]);
                    if (StringUtils.isEmpty(stringVal)) {
                        AppGender g = contact.getGender();
                        if (g != null && g.getName().equalsIgnoreCase("F")) {
                            stringVal = "Mlle";
                        } else {
                            stringVal = "M.";
                        }
                    }
                    AppCivility v = service.findCivility(stringVal);
                    if (v == null) {
                        v = new AppCivility();
                        v.setName(stringVal);
                        service.add(v);
                    }
                    contact.setCivility(v);
                }

                contact.setEmail(Convert.toString(values[COL_EMAIL]));
                contact.setPositionTitle1("Student "+Convert.toString(values[COL_CLASS]));

                service.update(contact);
                academicStudent.setStage(AcademicStudentStage.ATTENDING);
                if (oldAcademicStudent == null) {
                    service.add(academicStudent);
                } else {
                    service.update(academicStudent);
                }
                count++;
                final AcademicStudent finalAcademicStudent = academicStudent;
                UPA.getContext().invokePrivileged(new Action<Object>() {

                    @Override
                    public Object run() {
                        service.addUserForStudent(finalAcademicStudent);
                        return null;
                    }

                }, null);
//                service.add(tal);
            }
//            }
        }
        TraceService trace = VrApp.getBean(TraceService.class);
        trace.trace("importStudents", "importStudents from " + file + " in " + ch.stop() + " (" + count + " rows)", null, getClass().getSimpleName(), Level.INFO);
        log.log(Level.INFO, "importStudents from {0} in {1} " + " (" + count + " rows)", new Object[]{file, ch.stop()});
        return count;
    }

    public void importCourseAssignments(VFile file, ImportOptions importOptions) throws IOException {
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        if (importOptions == null) {
            importOptions = new ImportOptions();
        }
        Chronometer ch = new Chronometer();
        log.log(Level.INFO, "importCourseAssignments from {0}", file);
        File tmp = VFS.copyNativeTempFile(file);
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(true);
        int DEPARTMENT_COLUMN = 0;
        int PROGRAM_COLUMN = 1;
        int STUDENT_CLASS_COLUMN = 2;
        int SEMESTER_COLUMN = 3;
        int COURSE_GROUP_COLUMN = 4;
        int COURSE_NAME_COLUMN = 5;
        int COURSE_TYPE_COLUMN = 6;
        int LOAD_C_COLUMN = 7;
        int LOAD_TD_COLUMN = 8;
        int LOAD_TP_COLUMN = 9;
        int LOAD_PM_COLUMN = 10;
        int NBR_GROUPS_COLUMN = 11;
        int NBR_SHARES_COLUMN = 12;
        int TEACHER_NAME_COUMN = 13;
        int TEACHER_INTENTS_COUMN = 14;
        int DISCIPLINE_COLUMN = 15;
        int IGNORE_COLUMN = 16;

        sp.setSheetIndex(0);
        sp.setSkipRows(1);
        DataReader rows = sp.parse();
        int all = 0;
        long count = 0;
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
            all++;
            String nbrGroupsString = (Convert.toString(values[NBR_GROUPS_COLUMN]));
            String ignoreString = (Convert.toString(values[IGNORE_COLUMN]));
            String courseName = Convert.toString(values[COURSE_NAME_COLUMN]);

            boolean ignoreRow = false;
            if ("##".equals(nbrGroupsString)) {
                ignoreRow = true;
            } else if (courseName == null || courseName.trim().length() == 0) {
                ignoreRow = true;
            } else if (ignoreString != null
                    && ("X".equalsIgnoreCase(ignoreString)
                    || "true".equalsIgnoreCase(ignoreString)
                    || "yes".equalsIgnoreCase(ignoreString)
                    || "oui".equalsIgnoreCase(ignoreString))) {
                ignoreRow = true;
            }

            if (!ignoreRow) {
                AcademicCourseType courseType = null;
                AppDepartment department = null;
                AcademicProgram program = null;
                AcademicCourseLevel courseLevel = null;
                AcademicCourseGroup courseGroup = null;
                String discipline = null;
                AcademicClass studentClass = null;
                AcademicSemester semester = null;
                AcademicTeacher teacher = null;
                List<AcademicTeacher> teacherIntents = new ArrayList<>();
                if (courseName != null && courseName.trim().length() > 0) {
                    {
                        String stringVal = Convert.toString(values[DEPARTMENT_COLUMN]);
                        String[] codeAndName = codeAndName(stringVal);
                        department = service.findDepartment(codeAndName[0]);
                        if (department == null) {
                            department = new AppDepartment();
                            department.setCode(codeAndName[0]);
                            department.setName(codeAndName[1]);
                            department.setName2(codeAndName[1]);
                            service.add(department);
                        }
                    }
                    {
                        String stringVal = Convert.toString(values[PROGRAM_COLUMN]);
                        program = service.findProgram(department.getId(), stringVal);
                        if (program == null) {
                            program = new AcademicProgram();
                            program.setName(stringVal);
                            program.setName2(stringVal);
                            program.setDepartment(department);
                            service.add(program);
                        }
                    }
                    {
                        String stringVal = Convert.toString(values[STUDENT_CLASS_COLUMN]);
                        String goodName = stringVal;
                        if (PlatformTypes.isInteger(stringVal)) {
                            goodName = program.getName() + stringVal;
                        }
                        studentClass = service.findStudentClass(program.getId(), goodName);
                        if (studentClass == null) {
                            studentClass = new AcademicClass();
                            studentClass.setName(goodName);
                            studentClass.setProgram(program);
                            service.add(studentClass);
                        }
                    }
                    {
                        String stringVal = Convert.toString(values[SEMESTER_COLUMN]);
                        if (stringVal == null) {
                            throw new IllegalArgumentException("Missing semester for " + courseName);
                        }
                        String[] codeAndName = codeAndName(stringVal);
                        semester = service.findSemester(codeAndName[0]);
                        if (semester == null) {
                            semester = new AcademicSemester();
                            semester.setCode(codeAndName[0]);
                            semester.setName(codeAndName[1]);
                            semester.setName2(codeAndName[1]);
                            service.add(semester);
                        }
                    }
                    {
                        String stringVal = studentClass.getName() + "-" + semester.getName();
                        courseLevel = service.findCourseLevel(program.getId(), stringVal);
                        if (courseLevel == null) {
                            courseLevel = new AcademicCourseLevel();
                            courseLevel.setName(stringVal);
                            courseLevel.setProgram(program);
                            service.add(courseLevel);
                        }
                    }
                    {
                        String stringVal = Convert.toString(values[COURSE_GROUP_COLUMN]);
                        if (stringVal != null) {
                            courseGroup = service.findCourseGroup(courseLevel.getId(), stringVal);
                            if (courseGroup == null) {
                                courseGroup = new AcademicCourseGroup();
                                courseGroup.setName(stringVal);
                                courseGroup.setCourseLevel(courseLevel);
                                service.add(courseGroup);
                            }
                        }
                    }
                    {
                        discipline = service.formatDisciplinesNames(
                                Convert.toString(values[DISCIPLINE_COLUMN]), true);
                    }
                    {
                        String stringVal = Convert.toString(values[COURSE_TYPE_COLUMN]);
                        if (stringVal == null) {
                            throw new IllegalArgumentException("Missing module type for " + courseName);
                        }
                        courseType = service.findCourseType(stringVal);
                        if (courseType == null) {
                            courseType = new AcademicCourseType();
                            courseType.setName(stringVal);
                            //should load it from some where!
                            if ("tp".equalsIgnoreCase(stringVal)) {
                                courseType.setWeeks(10);
                            } else {
                                courseType.setWeeks(14);
                            }
                            service.add(courseType);
                        }
                    }
                    double valueC = Convert.toDouble(values[LOAD_C_COLUMN], DoubleParserConfig.LENIENT);
//                    {
//                        String stringVal = Convert.toString(values[31]);
//                        ModuleType v = service.findModuleType(stringVal);
//                        if (v == null) {
//                            v = new ModuleType();
//                            v.setName(stringVal);
//                            service.addModuleType(v);
//                        }
//                        d.setModuleType(v);
//                    }
                    double valueTD = Convert.toDouble(values[LOAD_TD_COLUMN], DoubleParserConfig.LENIENT);
                    double valueTP = Convert.toDouble(values[LOAD_TP_COLUMN], DoubleParserConfig.LENIENT);
                    double valuePM = Convert.toDouble(values[LOAD_PM_COLUMN], DoubleParserConfig.LENIENT);
                    double nbrGroups = Convert.toDouble(values[NBR_GROUPS_COLUMN], DoubleParserConfig.LENIENT);
                    double nbrShares = Convert.toDouble(values[NBR_SHARES_COLUMN], LENIENT_1);
                    String teacherName = Convert.toString(values[TEACHER_NAME_COUMN]);
                    String teacherIntentsString = Convert.toString(values[TEACHER_INTENTS_COUMN]);
                    if (teacherName == null && teacherIntents == null) {
                        System.out.println("Missing Assignment in Row " + Arrays.asList(values));
                    } else {
                        teacher = teacherName == null ? null : service.findTeacher(teacherName);
                        if (teacherName != null && teacher == null) {
                            log.log(Level.SEVERE, "Teacher not found " + teacherName);
                        }
                        for (String te : (teacherIntentsString == null ? "" : teacherIntentsString).split(",;")) {
                            te = te.trim();
                            if (!te.isEmpty()) {
                                AcademicTeacher teo = service.findTeacher(te);
                                if (teo != null) {
                                    teacherIntents.add(teo);
                                } else if (teo == null) {
                                    log.log(Level.SEVERE, "Teacher not found " + te);
                                }
                            }
                        }
                        double effWeek = (Math.ceil(valueC / 15.0) + Math.floor(valueTP / 15.0)) * 1.5;
                        String coursePlanName = courseName;
                        for (String suffix : new String[]{
                            "- TP", "- C", "- PS", "- PM",
                            "\u2013 TP", "\u2013 C", "\u2013 PS", "\u2013 PM",
                            "-TP", "-C", "-PS", "-PM",
                            "\u2013TP", "\u2013C", "\u2013PS", "\u2013PM",}) {
                            if (courseName.toUpperCase().endsWith(suffix)) {
                                coursePlanName = courseName.substring(0, courseName.length() - suffix.length()).trim();
                                break;
                            }
                        }
                        AcademicCoursePlan coursePlan = service.findCoursePlan(studentClass.getId(), semester.getId(), coursePlanName);
                        if (coursePlan == null) {
                            coursePlan = new AcademicCoursePlan();
                            coursePlan.setName(coursePlanName);
                            coursePlan.setProgram(program);
                            coursePlan.setStudentClass(studentClass);
                            coursePlan.setSemester(semester);
                            coursePlan.setCourseLevel(courseLevel);
                            coursePlan.setCourseGroup(courseGroup);
                            coursePlan.setDiscipline(discipline);
                            coursePlan.setValueC(0);
                            coursePlan.setValueTD(0);
                            coursePlan.setValueTP(0);
                            coursePlan.setValuePM(0);
                            service.add(coursePlan);
                        }
                        AcademicCourseAssignment d = new AcademicCourseAssignment();
                        d.setName(courseName);
                        d.setCoursePlan(coursePlan);
                        d.setValueC(valueC);
                        d.setValueTD(valueTD);
                        d.setValueTP(valueTP);
                        d.setValuePM(valuePM);
                        d.setCourseType(courseType);
                        d.setGroupCount(nbrGroups);
                        d.setShareCount(nbrShares);
                        d.setValueEffWeek(effWeek);
                        d.setTeacher(teacher);
                        service.add(d);
                        count++;
                        for (AcademicTeacher ti : teacherIntents) {
                            service.addIntent(ti.getId(), d.getId());
                        }
                    }
                }
            } else {
                System.out.println("Ignored Row " + Arrays.asList(values));
            }
        }
        TraceService trace = VrApp.getBean(TraceService.class);
        trace.trace("importCourseAssignments", "importTeachers from " + file + " in " + ch.stop() + " (" + count + " rows)", null, getClass().getSimpleName(), Level.INFO);
        log.log(Level.INFO, "importCourseAssignments from {0} in {1}", new Object[]{file, ch.stop()});
        service.updateAllCoursePlanValuesByLoadValues();
    }

    private String[] codeAndName(String s) {
        if (s == null) {
            s = "";
        }
        String code = null;
        String name = null;
        int eq = s.indexOf('=');
        if (eq >= 0) {
            code = s.substring(0, eq);
            name = s.substring(eq + 1);
        } else {
            code = s;
            name = s;
        }
        return new String[]{code, name};
    }
}
