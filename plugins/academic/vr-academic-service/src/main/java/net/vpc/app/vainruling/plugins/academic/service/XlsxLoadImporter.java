/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.plugins.academic.service.model.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicStudentImport;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicTeacherImport;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.*;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.bulk.DataReader;
import net.vpc.upa.bulk.DataRow;
import net.vpc.upa.bulk.ParseFormatManager;
import net.vpc.upa.bulk.SheetParser;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
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
//        service.add(new AcademicTeacherDegree("A",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 11.0, pos++));
//        service.add(new AcademicTeacherDegree("MA",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ (2.0 / 3.0),/*PM*/ (2.0 / 3.0) * (2.0 / 3.0),/*DU*/ 9.5, pos++));
//        service.add(new AcademicTeacherDegree("MC",/*C*/ 1.0,/*TD*/ 1.0 / 1.33,/*TP*/ 0.5,/*PM*/ 0.5 * (2.0 / 3.0),/*DU*/ 5.5, pos++));
//        service.add(new AcademicTeacherDegree("P",/*C*/ 1.0,/*TD*/ 1.0 / 1.33,/*TP*/ 0.5,/*PM*/ 0.5 * (2.0 / 3.0),/*DU*/ 5.5, pos++));
//        service.add(new AcademicTeacherDegree("C",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 11, pos++));
//        service.add(new AcademicTeacherDegree("CD",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 8, pos++));
//        service.add(new AcademicTeacherDegree("V",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 0, pos++));
//        service.add(new AcademicTeacherDegree("A'",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 0, pos++));
//        service.add(new AcademicTeacherDegree("MA'",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ (2.0 / 3.0),/*PM*/ (2.0 / 3.0) * (2.0 / 3.0),/*DU*/ 0, pos++));
//        service.add(new AcademicTeacherDegree("MC'",/*C*/ 1.0,/*TD*/ 1.0 / 1.33,/*TP*/ 0.5,/*PM*/ 0.5 * (2.0 / 3.0),/*DU*/ 0, pos++));
    }
    //TODO fix me
    public void importLoadConversionTable() throws IOException {
        AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        int pos = 1;
//        service.add(new AcademicTeacherDegree("A",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 11.0, pos++));
//        service.add(new AcademicTeacherDegree("MA",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ (2.0 / 3.0),/*PM*/ (2.0 / 3.0) * (2.0 / 3.0),/*DU*/ 9.5, pos++));
//        service.add(new AcademicTeacherDegree("MC",/*C*/ 1.0,/*TD*/ 1.0 / 1.33,/*TP*/ 0.5,/*PM*/ 0.5 * (2.0 / 3.0),/*DU*/ 5.5, pos++));
//        service.add(new AcademicTeacherDegree("P",/*C*/ 1.0,/*TD*/ 1.0 / 1.33,/*TP*/ 0.5,/*PM*/ 0.5 * (2.0 / 3.0),/*DU*/ 5.5, pos++));
//        service.add(new AcademicTeacherDegree("C",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 11, pos++));
//        service.add(new AcademicTeacherDegree("CD",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 8, pos++));
//        service.add(new AcademicTeacherDegree("V",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 0, pos++));
//        service.add(new AcademicTeacherDegree("A'",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ 0.69,/*PM*/ 0.69 * (2.0 / 3.0),/*DU*/ 0, pos++));
//        service.add(new AcademicTeacherDegree("MA'",/*C*/ 1.83,/*TD*/ 1.0,/*TP*/ (2.0 / 3.0),/*PM*/ (2.0 / 3.0) * (2.0 / 3.0),/*DU*/ 0, pos++));
//        service.add(new AcademicTeacherDegree("MC'",/*C*/ 1.0,/*TD*/ 1.0 / 1.33,/*TP*/ 0.5,/*PM*/ 0.5 * (2.0 / 3.0),/*DU*/ 0, pos++));
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
//            d.setValueC(Convert.toDouble(values[1]));
//            d.setValueTD(Convert.toDouble(values[2]));
//            d.setValueTP(Convert.toDouble(values[3]));
//            d.setValuePM(Convert.toDouble(values[4]));
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

    public void importLoadConversionTable(VFile file) throws IOException {
        AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = new Chronometer();
        File tmp = VFS.copyNativeTempFile(file);
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(true);
        DataReader rows = sp.parse();
        long count = 0;
        AcademicLoadConversionTable table = null;
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
            String tableName = Convert.toString(values[0]);
            if (table == null) {
                if(StringUtils.isEmpty(tableName)){
                    tableName="Table";
                }
                table=service.findLoadConversionTable(tableName);
                if(table==null) {
                    table = new AcademicLoadConversionTable();
                    table.setName("Table");
                    service.add(table);
                }
            }

            AcademicLoadConversionRule rule = service.findLoadConversionRule(tableName);
            if (rule == null) {
                rule=new AcademicLoadConversionRule();
                rule.setName(Convert.toString(values[1]));
                service.add(rule);
            }
            AcademicLoadConversionRow trow = service.findLoadConversionRow(table.getId(), rule.getId());
            if (trow == null) {
                trow = new AcademicLoadConversionRow();
                trow.setConversionTable(table);
                trow.setRule(rule);
                trow.setValueC(Convert.toDouble(values[2]));
                trow.setValueTD(Convert.toDouble(values[3]));
                trow.setValueTP(Convert.toDouble(values[4]));
                trow.setValuePM(Convert.toDouble(values[5]));
                service.add(trow);
            }else{
                trow.setValueC(Convert.toDouble(values[2]));
                trow.setValueTD(Convert.toDouble(values[3]));
                trow.setValueTP(Convert.toDouble(values[4]));
                trow.setValuePM(Convert.toDouble(values[5]));
                service.update(trow);
            }
            count++;
        }
        TraceService trace = VrApp.getBean(TraceService.class);
        trace.trace("importTeacherDegrees", "importDepartments from " + file + " in " + ch.stop() + " (" + count + " rows)", null, getClass().getSimpleName(), Level.INFO);
        log.log(Level.INFO, "importTeacherDegrees from {0} in {1}", new Object[]{file, ch.stop()});
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

    public int importFile(int periodId, VFile file, ImportOptions importOptions) throws IOException {
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
                    count += importFile(periodId, f.file, i);
                }
            }
            return count;
        }

        if (file.getName().equals("departments.xlsx")) {
            importDepartments(file);
        }
        if (file.getName().equals("load-conversion-table.xlsx")) {
            if (file.length() > 0) {
                count++;
                importLoadConversionTable(file);
            } else {
                count++;
                trace.trace("importLoadConversionTable", "Import Default Conversion Table", null, getClass().getSimpleName(), Level.INFO);
                XlsxLoadImporter.this.importLoadConversionTable();
            }
        }
        if (file.getName().equals("load-degrees.xlsx")) {
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
            importTeachers(periodId, file);
        }
        if (file.getName().equals("students.xlsx") || file.getName().endsWith(".students.xlsx")) {
            count++;
            importStudents(periodId, file);
        }
        if (file.getName().equals("course-assignments.xlsx")) {
            count++;
            importCourseAssignments(periodId, file, importOptions);
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

    public AcademicTeacherImport parseAcademicTeacherImport(Object[] values) {
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
        AcademicTeacherImport academicTeacherImport = new AcademicTeacherImport();
        academicTeacherImport.setFirstName(core.validateName(Convert.toString(values[COL_FIRST_NAME])));
        academicTeacherImport.setLastName(core.validateName(Convert.toString(values[COL_LAST_NAME])));
        if (!StringUtils.isEmpty(academicTeacherImport.getNin()) || !StringUtils.isEmpty(academicTeacherImport.getFirstName()) || !StringUtils.isEmpty(academicTeacherImport.getLastName())) {
            academicTeacherImport.setFirstName2(Convert.toString(values[COL_FIRST_NAME2]));
            academicTeacherImport.setLastName2(Convert.toString(values[COL_LAST_NAME2]));
            academicTeacherImport.setDegreeName(Convert.toString(values[COL_DEGREE]));
            academicTeacherImport.setSituationName(Convert.toString(values[COL_SITUATION]));
            academicTeacherImport.setSituationName2(Convert.toString(values[COL_SITUATION2]));
            academicTeacherImport.setGenderName(Convert.toString(values[COL_GENDER]));
            academicTeacherImport.setCivilityName(Convert.toString(values[COL_CIVILITY]));
            academicTeacherImport.setEmail(Convert.toString(values[COL_EMAIL]));
            academicTeacherImport.setDiscipline(Convert.toString(values[COL_DISCIPLINE]));
            academicTeacherImport.setWeekLoads(new int[]{
                    Convert.toInteger(values[COL_WEEK_LOAD_1], IntegerParserConfig.LENIENT),
                    Convert.toInteger(values[COL_WEEK_LOAD_2], IntegerParserConfig.LENIENT)
            });
            return academicTeacherImport;
        }
        return null;
    }

    public void importTeacher(AcademicTeacherImport a, ImportTeacherContext ctx) throws IOException {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);

        if (ctx == null) {
            ctx = new ImportTeacherContext();
        }
        if (ctx.mainCompany == null) {
            AppConfig appConfig = core.findAppConfig();
            ctx.mainCompany = appConfig == null ? null : appConfig.getMainCompany();
        }
        if (ctx.mainPeriod == null) {
            AppConfig appConfig = core.findAppConfig();
            ctx.mainPeriod = appConfig == null ? null : appConfig.getMainPeriod();
        }
        if (ctx.gendersById == null) {
            ctx.gendersByName = new HashMap<>();
            ctx.gendersById = new HashMap<>();
            for (AppGender g : core.findGenders()) {
                ctx.gendersByName.put(g.getName().toUpperCase(), g);
                ctx.gendersById.put(g.getId(), g);
            }

        }

        if (ctx.civilityById == null) {
            ctx.civilityByName = new HashMap<>();
            ctx.civilityById = new HashMap<>();
            for (AppCivility g : core.findCivilities()) {
                ctx.civilityByName.put(g.getName().toUpperCase(), g);
                ctx.civilityById.put(g.getId(), g);
            }
        }

        AcademicTeacher academicTeacher = new AcademicTeacher();
        AppContact contact = new AppContact();
        contact.setNin(a.getNin());
        contact.setFirstName(a.getFirstName());
        contact.setLastName(a.getLastName());
        contact.setFullName(core.validateName(AppContact.getName(contact)));
        contact.setPhone1(a.getPhone());

        AppDepartment dept = null;
        if (a.getDepartmentId() != null) {
            dept = core.findDepartment(a.getDepartmentId());
            if (dept == null) {
                throw new NoSuchElementException("Department Not Found " + a.getDepartmentId());
            }
        } else {
            dept = service.findDepartment(a.getDepartmentName());
            if (dept == null) {
                throw new NoSuchElementException("Department Not Found " + a.getDepartmentName());
            }
        }
        int mainPeriodId = ctx.mainPeriod.getId();
        AppPeriod period = null;
        if (a.getStartPeriodId() != null) {
            period = core.findPeriod(a.getStartPeriodId());
            if (period == null) {
                throw new NoSuchElementException("Period Not Found " + a.getStartPeriodId());
            }
        } else {
            period = core.findPeriod(a.getStartPeriodName());
            if (period == null) {
                throw new NoSuchElementException("Period Not Found " + a.getStartPeriodName());
            }
        }

        AcademicTeacherDegree degree = null;
        if (a.getDegreeId() != null) {
            degree = service.findTeacherDegree(a.getDegreeId());
            if (degree == null) {
                throw new NoSuchElementException("Degree Not Found " + a.getDegreeId());
            }
        } else {
            degree = service.findTeacherDegree(a.getDegreeName());
            if (degree == null) {
                throw new NoSuchElementException("Degree Not Found " + a.getDegreeName());
            }
        }

        AppGender gender = null;
        if (a.getGenderId() != null) {
            gender = ctx.gendersById.get(a.getGenderId());
            if (gender == null) {
                throw new NoSuchElementException("Gender Not Found " + a.getGenderId());
            }
        } else {
            gender = ctx.gendersByName.get(a.getGenderName().toUpperCase());
            if (gender == null) {
                throw new NoSuchElementException("Gender Not Found " + a.getGenderName());
            }
        }

        AppCivility civility = null;
        if (a.getCivilityId() != null) {
            civility = ctx.civilityById.get(a.getCivilityId());
            if (civility == null) {
                throw new NoSuchElementException("Civility Not Found " + a.getCivilityId());
            }
        } else {
            civility = ctx.civilityByName.get(a.getCivilityName().toUpperCase());
            if (civility == null) {
                if (gender.getName().equals("F")) {
                    civility = ctx.civilityByName.get("Mlle");
                } else {
                    civility = ctx.civilityByName.get("M.");
                }
                if (civility == null) {
                    throw new NoSuchElementException("Civility Not Found " + a.getCivilityName());
                }
            }
        }

        AcademicTeacherSituation situation = null;
        if (a.getSituationId() != null) {
            situation = service.findTeacherSituation(a.getSituationId());
            if (situation == null) {
                throw new NoSuchElementException("Situation Not Found " + a.getCivilityId());
            }
        } else {
            situation = service.findTeacherSituation(a.getSituationName());
            if (situation == null) {
                throw new NoSuchElementException("Situation Not Found " + a.getCivilityId());
            }
        }

        contact = core.findOrCreateContact(contact);
        contact.setCompany(ctx.mainCompany);
        AcademicTeacher oldAcademicTeacher = service.findTeacherByContact(contact.getId());
        if (oldAcademicTeacher != null) {
            academicTeacher = oldAcademicTeacher;
        } else {
            academicTeacher.setContact(contact);
        }
        academicTeacher.setDepartment(dept);
        contact.setFirstName2(a.getFirstName2());
        contact.setLastName2(a.getLastName2());
        contact.setFullName2(AppContact.getName2(contact));
        contact.setEmail(a.getEmail());

        List<AcademicTeacherSemestrialLoad> semestrialLoads = new ArrayList<>();
        academicTeacher.setDegree(degree);
        academicTeacher.setSituation(situation);
        if (contact.getGender() == null) {
            contact.setGender(gender);
        }
        if (contact.getCivility() == null) {
            contact.setCivility(civility);
        }
        if (contact.getCivility() == null) {
            contact.setCivility(civility);
        }

        academicTeacher.setDiscipline(VrApp.getBean(AcademicPlugin.class).formatDisciplinesNames(a.getDiscipline(), true));
        service.update(contact);
        if (oldAcademicTeacher == null) {
            service.add(academicTeacher);
        } else {
            service.update(academicTeacher);
        }
        if (a.getWeekLoads() != null) {
            for (int i = 0; i < a.getWeekLoads().length; i++) {
                int weekLoad = a.getWeekLoads()[i];
                AcademicTeacherSemestrialLoad sload = new AcademicTeacherSemestrialLoad();
                sload.setTeacher(academicTeacher);
                sload.setWeeksLoad(weekLoad);
                sload.setSemester(i + 1);
                semestrialLoads.add(sload);
            }
        }
        final AcademicTeacher finalAcademicTeacher = academicTeacher;
        UPA.getContext().invokePrivileged(new Action<Object>() {

            @Override
            public Object run() {
                service.addUserForTeacher(finalAcademicTeacher);
                return null;
            }

        }, null);
//                service.add(tal);
        final List<AcademicTeacherSemestrialLoad> academicTeacherSemestrialLoads = service.findTeacherSemestrialLoadsByTeacher(mainPeriodId, academicTeacher.getId());
        for (AcademicTeacherSemestrialLoad sload : semestrialLoads) {
            boolean found = false;
            for (AcademicTeacherSemestrialLoad al : academicTeacherSemestrialLoads) {
                if (al.getSemester() == sload.getSemester()) {
                    al.setWeeksLoad(sload.getWeeksLoad());
                    service.update(al);
                    found = true;
                    break;
                }
            }
            if (!found) {
                service.add(sload);
            }
        }
    }

    public void importTeachers(int periodId, VFile file) throws IOException {
        Chronometer ch = new Chronometer();
        log.log(Level.INFO, "importTeachers from {0}", file);
        File tmp = VFS.copyNativeTempFile(file);
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(true);
        sp.setSheetIndex(0);
        sp.setSkipRows(0);
        DataReader rows = sp.parse();
        long count = 0;
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
            AcademicTeacherImport aa = parseAcademicTeacherImport(values);
            ImportTeacherContext ctx = new ImportTeacherContext();
            ctx.mainPeriod = VrApp.getBean(CorePlugin.class).findPeriodOrMain(periodId);
            if (aa != null) {
                importTeacher(aa, ctx);
                count++;
            }
        }
        TraceService trace = VrApp.getBean(TraceService.class);
        trace.trace("importTeachers", "importTeachers from " + file + " in " + ch.stop() + " (" + count + " rows)", null, getClass().getSimpleName(), Level.INFO);
        log.log(Level.INFO, "importTeachers from {0} in {1} " + " (" + count + " rows)", new Object[]{file, ch.stop()});
    }

    public AcademicStudentImport parseAcademicStudentImport(Object[] values) throws IOException {
        int col = -1;
        final int COL_DEPT = ++col;
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
        String fn = core.validateName(Convert.toString(values[COL_FIRST_NAME]));
        String ln = core.validateName(Convert.toString(values[COL_LAST_NAME]));
        String nin = Convert.toString(values[COL_NIN]);
        if (!StringUtils.isEmpty(nin) || !StringUtils.isEmpty(fn) || !StringUtils.isEmpty(ln)) {
            AcademicStudentImport a = new AcademicStudentImport();
            a.setFirstName(fn);
            a.setLastName(ln);
            a.setNin(nin);
            a.setStartPeriodName(Convert.toString(values[COL_YEAR1]));
            a.setClassName(Convert.toString(values[COL_CLASS]));
            a.setFirstName2(Convert.toString(values[COL_FIRST_NAME2]));
            a.setLastName2(Convert.toString(values[COL_LAST_NAME2]));
            a.setDepartmentName(Convert.toString(values[COL_DEPT]));
            a.setPhone(Convert.toString(values[COL_GSM]));
            a.setGenderName(Convert.toString(values[COL_GENDER]));
            a.setSubscriptionNumber(Convert.toString(values[COL_SUBSCRIPTION_NBR]));
            a.setCivilityName(Convert.toString(values[COL_CIVILITY]));
            a.setEmail(Convert.toString(values[COL_EMAIL]));
            return a;
        }
        return null;
    }

    public void importStudent(AcademicStudentImport a, ImportStudentContext ctx) throws IOException {
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (ctx == null) {
            ctx = new ImportStudentContext();
        }
        if (ctx.mainCompany == null) {
            AppConfig appConfig = core.findAppConfig();
            ctx.mainCompany = appConfig == null ? null : appConfig.getMainCompany();
        }
        if (ctx.profiles == null) {
            ctx.profiles = new HashMap<>();
            for (AcademicClass cls : service.findAcademicClasses()) {
                AppProfile p = core.findOrCreateCustomProfile(cls.getName(), "AcademicClass");
                ctx.profiles.put(p.getCode(), p);
                ctx.profiles.put(cls.getName(), p);
            }

        }
        if (ctx.gendersById == null) {
            ctx.gendersByName = new HashMap<>();
            ctx.gendersById = new HashMap<>();
            for (AppGender g : core.findGenders()) {
                ctx.gendersByName.put(g.getName().toUpperCase(), g);
                ctx.gendersById.put(g.getId(), g);
            }

        }

        if (ctx.civilityById == null) {
            ctx.civilityByName = new HashMap<>();
            ctx.civilityById = new HashMap<>();
            for (AppCivility g : core.findCivilities()) {
                ctx.civilityByName.put(g.getName().toUpperCase(), g);
                ctx.civilityById.put(g.getId(), g);
            }
        }

        AppDepartment dept = null;
        if (a.getDepartmentId() != null) {
            dept = core.findDepartment(a.getDepartmentId());
//            if (dept == null) {
//                throw new NoSuchElementException("Department Not Found " + a.getDepartmentId());
//            }
        } else {
            dept = service.findDepartment(a.getDepartmentName());
//            if (dept == null) {
//                throw new NoSuchElementException("Department Not Found " + a.getDepartmentName());
//            }
        }

        AppPeriod period = null;
        if (a.getStartPeriodId() != null) {
            period = core.findPeriod(a.getStartPeriodId());
            if (period == null) {
                throw new NoSuchElementException("Period Not Found " + a.getStartPeriodId());
            }
        } else {
            period = core.findPeriod(a.getStartPeriodName());
            if (period == null) {
                throw new NoSuchElementException("Period Not Found " + a.getStartPeriodName());
            }
        }

        AppGender gender = null;
        if (a.getGenderId() != null) {
            gender = ctx.gendersById.get(a.getGenderId());
            if (gender == null) {
                throw new NoSuchElementException("Gender Not Found " + a.getGenderId());
            }
        } else {
            gender = ctx.gendersByName.get(a.getGenderName().toUpperCase());
            if (gender == null) {
                throw new NoSuchElementException("Gender Not Found " + a.getGenderName());
            }
        }

        AppCivility civility = null;
        if (a.getCivilityId() != null) {
            civility = ctx.civilityById.get(a.getCivilityId());
            if (civility == null) {
                throw new NoSuchElementException("Civility Not Found " + a.getCivilityId());
            }
        } else {
            civility = ctx.civilityByName.get(a.getCivilityName().toUpperCase());
            if (civility == null) {
                if (gender.getName().equals("F")) {
                    civility = ctx.civilityByName.get("MLLE");
                } else {
                    civility = ctx.civilityByName.get("M.");
                }
                if (civility == null) {
                    throw new NoSuchElementException("Civility Not Found " + a.getCivilityName());
                }
            }
        }

        AcademicBac bac = null;
        if (a.getPreClassBacId() != null) {
            bac = service.findAcademicBac(a.getPreClassBacId());
            if (bac == null) {
                throw new NoSuchElementException("Bac Not Found " + a.getPreClassBacId());
            }
        } else if (!StringUtils.isEmpty(a.getPreClassBacName())) {
            bac = service.findAcademicBac(a.getPreClassBacName());
            if (bac == null) {
                throw new NoSuchElementException("Bac Not Found " + a.getStartPeriodName());
            }
        }

        AcademicPreClass prep = null;
        if (a.getPreClassBacId() != null) {
            prep = service.findAcademicPreClass(a.getPreClassPrepId());
            if (prep == null) {
                throw new NoSuchElementException("Prep Not Found " + a.getPreClassBacId());
            }
        } else if (!StringUtils.isEmpty(a.getPreClassBacName())) {
            prep = service.findAcademicPreClass(a.getPreClassPrepName());
            if (prep == null) {
                throw new NoSuchElementException("Prep Not Found " + a.getStartPeriodName());
            }
        }

        AcademicClass studentclass = null;
        if (a.getClassId() != null) {
            studentclass = service.findAcademicClass(a.getClassId());
            if (studentclass == null) {
                throw new NoSuchElementException("Class Not Found " + a.getPreClassBacId());
            }
        } else {
            studentclass = service.findAcademicClass(a.getClassName());
            if (studentclass == null) {
                throw new NoSuchElementException("Class Not Found " + a.getStartPeriodName());
            }
        }

        AppContact contact = new AppContact();
        contact.setNin(a.getNin());
        contact.setFirstName(a.getFirstName());
        contact.setLastName(a.getLastName());
        contact.setFullName(core.validateName(AppContact.getName(contact)));
        String fs2 = a.getFirstName2();
        contact = core.findOrCreateContact(contact);
        AcademicStudent academicStudent = null;
        AcademicStudent oldAcademicStudent = service.findStudentByContact(contact.getId());
        if (oldAcademicStudent != null) {
            academicStudent = oldAcademicStudent;
        } else {
            academicStudent = new AcademicStudent();
            academicStudent.setContact(contact);
        }
        if (academicStudent.getDepartment() == null) {
            academicStudent.setDepartment(dept);
        }
        String ln2 = a.getLastName2();
        contact.setFirstName2(fs2);
        contact.setLastName2(ln2);
        contact.setFullName2(AppContact.getName2(contact));
        contact.setPhone1(a.getPhone());
        academicStudent.setFirstSubscription(period);

        academicStudent.setLastClass1(studentclass);
        if (studentclass.getProgram() != null) {
            academicStudent.setDepartment(studentclass.getProgram().getDepartment());
        }
        if (contact.getGender() == null) {
            contact.setGender(gender);
        }
        if (contact.getCivility() == null) {
            contact.setCivility(civility);
        }
        academicStudent.setSubscriptionNumber(a.getSubscriptionNumber());
        if (academicStudent.getBaccalaureateClass() == null) {
            academicStudent.setBaccalaureateClass(bac);
        }
        if (academicStudent.getPreClass() == null) {
            academicStudent.setPreClass(prep);
        }
        if (academicStudent.getPreClassRank() <= 0) {
            academicStudent.setPreClassRank(a.getPreClassPrepRank());
        }
        if (academicStudent.getPreClassRankMax() <= 0) {
            academicStudent.setPreClassRankMax(a.getPreClassPrepRankMax());
        }
        if (academicStudent.getBaccalaureateScore() <= 0) {
            academicStudent.setBaccalaureateScore(a.getPreClassBacScore());
        }

        contact.setEmail(a.getEmail());
        contact.setPositionSuffix(studentclass.getName());
        contact.setPositionTitle1("Student " + studentclass.getName());
        contact.setEnabled(true);
        contact.setCompany(ctx.mainCompany);

        service.update(contact);
        academicStudent.setStage(AcademicStudentStage.ATTENDING);
        if (oldAcademicStudent == null) {
            service.add(academicStudent);
        } else {
            service.update(academicStudent);
        }
        final AcademicStudent finalAcademicStudent = academicStudent;
        UPA.getContext().invokePrivileged(new Action<Object>() {

            @Override
            public Object run() {
                service.addUserForStudent(finalAcademicStudent);
                return null;
            }

        }, null);
    }

    public int importStudents(int periodId, VFile file) throws IOException {
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = new Chronometer();
        log.log(Level.INFO, "importStudents from {0}", file);
        File tmp = VFS.copyNativeTempFile(file);
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(true);
        sp.setSheetIndex(0);
        sp.setSkipRows(0);
        DataReader rows = sp.parse();

        CorePlugin core = VrApp.getBean(CorePlugin.class);
        ImportStudentContext importStudentContext = new ImportStudentContext();
        importStudentContext.mainPeriod = core.findPeriodOrMain(periodId);
        int count = 0;
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
            AcademicStudentImport a = parseAcademicStudentImport(values);
            if (a != null) {
                importStudent(a, importStudentContext);
                count++;
            }
        }
        TraceService trace = VrApp.getBean(TraceService.class);
        trace.trace("importStudents", "importStudents from " + file + " in " + ch.stop() + " (" + count + " rows)", null, getClass().getSimpleName(), Level.INFO);
        log.log(Level.INFO, "importStudents from {0} in {1} " + " (" + count + " rows)", new Object[]{file, ch.stop()});
        return count;
    }

    public void importCourseAssignments(int periodId, VFile file, ImportOptions importOptions) throws IOException {
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        final CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (importOptions == null) {
            importOptions = new ImportOptions();
        }
        AppPeriod period = core.findPeriod(periodId);
        if (period == null) {
            throw new IllegalArgumentException("Missing Period");
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
                        studentClass = service.findAcademicClass(program.getId(), goodName);
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
                        courseLevel = service.findCourseLevel(studentClass.getId(), semester.getId());
                        if (courseLevel == null) {
                            courseLevel = new AcademicCourseLevel();
                            courseLevel.setName(stringVal);
//                            courseLevel.setProgram(program);
                            courseLevel.setCreationDate(new Timestamp(System.currentTimeMillis()));
                            courseLevel.setSemester(semester);
                            courseLevel.setAcademicClass(studentClass);
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
                                AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
                                courseType.setWeeks(ap.getSemesterMaxWeeks());
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
                        AcademicCoursePlan coursePlan = service.findCoursePlan(periodId, studentClass.getId(), semester.getId(), coursePlanName);
                        if (coursePlan == null) {
                            coursePlan = new AcademicCoursePlan();
                            coursePlan.setName(coursePlanName);
                            coursePlan.setCourseLevel(courseLevel);
                            coursePlan.setCourseGroup(courseGroup);
                            coursePlan.setDiscipline(discipline);
                            coursePlan.setPeriod(period);
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
        service.updateAllCoursePlanValuesByLoadValues(periodId);
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

    public static class ImportStudentContext {

        Map<String, AppGender> gendersByName;
        Map<Integer, AppGender> gendersById;
        Map<String, AppCivility> civilityByName;
        Map<Integer, AppCivility> civilityById;
        Map<String, AppProfile> profiles;
        AppCompany mainCompany;
        AppPeriod mainPeriod;
    }

    public static class ImportTeacherContext {

        AppPeriod mainPeriod;
        AppCompany mainCompany;
        Map<String, AppGender> gendersByName;
        Map<Integer, AppGender> gendersById;
        Map<String, AppCivility> civilityByName;
        Map<Integer, AppCivility> civilityById;
    }
}
