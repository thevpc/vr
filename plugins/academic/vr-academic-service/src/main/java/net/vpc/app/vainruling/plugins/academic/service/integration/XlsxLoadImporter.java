/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration;

import net.vpc.app.vainruling.core.service.util.ColumnMappingMatcher;
import net.vpc.app.vainruling.core.service.util.LenientArray;
import net.vpc.app.vainruling.plugins.academic.service.helper.mapping.StudentMapping;
import net.vpc.app.vainruling.plugins.academic.service.helper.mapping.CourseAssignmentsMapping;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.util.VrPasswordStrategyNin;
import net.vpc.app.vainruling.core.service.util.VrPasswordStrategyRandom;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.util.ImportOptions;
import net.vpc.app.vainruling.plugins.academic.service.model.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicStudentImport;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicTeacherImport;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import net.vpc.common.util.Convert;
import net.vpc.common.util.DoubleParserConfig;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.common.vfs.VFile;
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
import net.vpc.app.vainruling.core.service.util.JsonUtils;
import net.vpc.common.util.MapUtils;

/**
 * @author taha.bensalah@gmail.com
 */
public class XlsxLoadImporter {

    private static Logger log = Logger.getLogger(XlsxLoadImporter.class.getName());
    private static DoubleParserConfig LENIENT_1 = DoubleParserConfig.LENIENT.setNullValue(1.0).setInvalidValue(1.0);

    private ParseFormatManager pfm = UPA.getBootstrap().getFactory().createObject(ParseFormatManager.class);

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
        final CorePlugin core = VrApp.getBean(CorePlugin.class);
        AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = new Chronometer();
        File tmp = file.copyToNativeTempFile();
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(true);
        DataReader rows = sp.parse();
        long count = 0;
        CorePlugin corePlugin = CorePlugin.get();
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
            String code = Convert.toString(values[0]);
            AppDepartment curr = core.findDepartment(code);
            if (curr == null) {
                AppDepartment d = new AppDepartment();
                d.setCode(code);
                d.setName(Convert.toString(values[1]));
                AppDepartment parent = core.findDepartment(Convert.toString(values[2]));
                d.setParent(parent);
                d.setName2(Convert.toString(values[3]));
                corePlugin.save("AppDepartment", d);
                count++;
            }
        }
        TraceService trace = TraceService.get();
        ch.stop();
        trace.trace("Academic.import-departments", "success", MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count)
                , getClass().getSimpleName(), Level.INFO);
    }

    public void importTeacherDegrees(VFile file) throws IOException {
        AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = new Chronometer();
        File tmp = file.copyToNativeTempFile();
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
            d.setPosition(Convert.toInt(values[6]));
            CorePlugin.get().save("AcademicTeacherDegree", d);
            count++;
        }
        TraceService trace = TraceService.get();
        ch.stop();
        trace.trace("Academic.import-teacher-degrees", "success", 
                MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count), 
                
                getClass().getSimpleName(), Level.INFO);
    }

    public void importLoadConversionTable(VFile file) throws IOException {
        AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = new Chronometer();
        File tmp = file.copyToNativeTempFile();
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
                if (StringUtils.isEmpty(tableName)) {
                    tableName = "Table";
                }
                table = service.findLoadConversionTable(tableName);
                if (table == null) {
                    table = new AcademicLoadConversionTable();
                    table.setName("Table");
                    CorePlugin.get().save("AcademicLoadConversionTable", table);
                }
            }

            AcademicLoadConversionRule rule = service.findLoadConversionRule(tableName);
            if (rule == null) {
                rule = new AcademicLoadConversionRule();
                rule.setName(Convert.toString(values[1]));
                CorePlugin.get().save("AcademicLoadConversionRule", rule);
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
                CorePlugin.get().save("AcademicLoadConversionRow", trow);
            } else {
                trow.setValueC(Convert.toDouble(values[2]));
                trow.setValueTD(Convert.toDouble(values[3]));
                trow.setValueTP(Convert.toDouble(values[4]));
                trow.setValuePM(Convert.toDouble(values[5]));
                CorePlugin.get().save("AcademicLoadConversionRow", trow);
            }
            count++;
        }
        TraceService trace = TraceService.get();
        ch.stop();
        trace.trace("Academic.import-load-conversion-table", "success", 
                MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count), 
                getClass().getSimpleName(), Level.INFO);
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
        TraceService trace = TraceService.get();
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

        if (file.getName().equals("departments.xlsx") || file.getName().endsWith(".departments.xlsx")) {
            importDepartments(file);
        }
        if (file.getName().equals("load-conversion-table.xlsx") || file.getName().endsWith(".load-conversion-table.xlsx")) {
            if (file.length() > 0) {
                count++;
                importLoadConversionTable(file);
            } else {
                count++;
                trace.trace("Academic.import-load-conversion-table-default", "success", null, getClass().getSimpleName(), Level.INFO);
                XlsxLoadImporter.this.importLoadConversionTable();
            }
        }
        if (file.getName().equals("load-degrees.xlsx") || file.getName().endsWith(".load-degrees.xlsx")) {
            if (file.length() > 0) {
                count++;
                importTeacherDegrees(file);
            } else {
                count++;
                trace.trace("Academic.import-teacher-degrees-default", "success", null, "/Education/Config", Level.INFO);
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
        if (file.getName().equals("course-assignments.xlsx") || file.getName().endsWith(".course-assignments.xlsx")) {
            count++;
            importCourseAssignments(periodId, file, importOptions);
        }
        long end = System.currentTimeMillis();
        String ch = Chronometer.formatPeriod(end - start);
        if (count > 0) {
            trace.trace("Academic.import-teaching-load", "success", MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count), "/Education/Config", Level.INFO);
        } else {
            trace.trace("Academic.import-teaching-load", "error", MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count), "/Education/Config", Level.INFO);
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
        academicTeacherImport.setFirstName(VrUtils.validateContactName(Convert.toString(values[COL_FIRST_NAME])));
        academicTeacherImport.setLastName(VrUtils.validateContactName(Convert.toString(values[COL_LAST_NAME])));
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
                Convert.toInt(values[COL_WEEK_LOAD_1], IntegerParserConfig.LENIENT),
                Convert.toInt(values[COL_WEEK_LOAD_2], IntegerParserConfig.LENIENT)
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
        if (ctx.getMainCompany() == null) {
            AppConfig appConfig = core.getCurrentConfig();
            ctx.setMainCompany(appConfig == null ? null : appConfig.getMainCompany());
        }
        if (ctx.getMainPeriod() == null) {
            AppConfig appConfig = core.getCurrentConfig();
            ctx.setMainPeriod(appConfig == null ? null : appConfig.getMainPeriod());
        }

        AcademicTeacher academicTeacher = new AcademicTeacher();
        AppUser user = new AppUser();
        user.setNin(a.getNin());
        user.setFirstName(VrUtils.validateContactName(a.getFirstName()));
        user.setLastName(VrUtils.validateContactName(a.getLastName()));
        user.setFullName(VrUtils.validateContactName(AppContact.getName(user)));
        user.setPhone1(a.getPhone());

        AppDepartment dept = null;
        if (a.getDepartmentId() != null) {
            dept = core.findDepartment(a.getDepartmentId());
            if (dept == null) {
                throw new NoSuchElementException("Department Not Found " + a.getDepartmentId());
            }
        } else {
            dept = core.findDepartment(a.getDepartmentName());
            if (dept == null) {
                throw new NoSuchElementException("Department Not Found " + a.getDepartmentName());
            }
        }
        int mainPeriodId = ctx.getMainPeriod().getId();
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

        AcademicOfficialDiscipline officialDiscipline = null;
        if (a.getOfficialDisciplineId() != null) {
            officialDiscipline = service.findOfficialDiscipline(a.getOfficialDisciplineId());
            if (officialDiscipline == null) {
                throw new NoSuchElementException("OfficialDiscipline Not Found " + a.getOfficialDisciplineId());
            }
        } else {
            officialDiscipline = service.findOfficialDiscipline(a.getOfficialDisciplineName());
            if (officialDiscipline == null) {
                throw new NoSuchElementException("OfficialDiscipline Not Found " + a.getDegreeName());
            }
        }

        AppGender gender = ctx.getGenders().resolveGender(a.getGenderId(), a.getGenderName(), true);
        AppCivility civility = ctx.getCivilities().resolveCivility(a.getCivilityId(), a.getCivilityName(), gender, true);

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

        user.setCompany(ctx.getMainCompany());
        user.setFirstName2(a.getFirstName2());
        user.setLastName2(a.getLastName2());
        user.setFullName2(AppContact.getName2(user));
        user.setEmail(a.getEmail());
        if (user.getGender() == null) {
            user.setGender(gender);
        }
        if (user.getCivility() == null) {
            user.setCivility(civility);
        }
        if (user.getCivility() == null) {
            user.setCivility(civility);
        }
        user.setDepartment(dept);
        AppUserType teacherType = core.findUserType("Teacher");
        user.setType(teacherType);

        user = core.findOrCreateUser(user, new String[]{"Teacher"}, VrPasswordStrategyRandom.INSTANCE);
        AcademicTeacher oldAcademicTeacher = service.findTeacherByUser(user.getId());
        if (oldAcademicTeacher != null) {
            academicTeacher = oldAcademicTeacher;
        } else {
            AppUser u = core.findUser(user.getId());
            if (u != null) {
                academicTeacher.setUser(u);
            }
        }
//        academicTeacher.setDepartment(dept);

        List<AcademicTeacherSemestrialLoad> semestrialLoads = new ArrayList<>();
        academicTeacher.setDegree(degree);
        academicTeacher.setOfficialDiscipline(officialDiscipline);
        academicTeacher.setSituation(situation);
        academicTeacher.setStartPeriod(period);
        academicTeacher.setLastPeriod(period);
        academicTeacher.setDiscipline(VrApp.getBean(AcademicPlugin.class).formatDisciplinesNames(a.getDiscipline(), true));
        academicTeacher.setUser(user);

        CorePlugin corePlugin = CorePlugin.get();
        corePlugin.save(null, user);

        corePlugin.save(null, academicTeacher);

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
//                service.add(tal);
        final List<AcademicTeacherSemestrialLoad> academicTeacherSemestrialLoads = service.findTeacherSemestrialLoadsByTeacher(mainPeriodId, academicTeacher.getId());
        for (AcademicTeacherSemestrialLoad sload : semestrialLoads) {
            boolean found = false;
            for (AcademicTeacherSemestrialLoad al : academicTeacherSemestrialLoads) {
                if (al.getSemester() == sload.getSemester()) {
                    al.setWeeksLoad(sload.getWeeksLoad());
                    corePlugin.save(null, al);
                    found = true;
                    break;
                }
            }
            if (!found) {
                corePlugin.save(null, sload);
            }
        }
        AcademicTeacherPeriod academicTeacherPeriod = service.findAcademicTeacherPeriod(mainPeriodId, academicTeacher);
        if (academicTeacherPeriod.getId() < 0) {
            //this is temp period
            service.updateTeacherPeriod(mainPeriodId, academicTeacher.getId(), -1);
        }
    }

    public void importTeachers(int periodId, VFile file) throws IOException {
        Chronometer ch = new Chronometer();
        log.log(Level.INFO, "importTeachers from {0}", file);
        File tmp = file.copyToNativeTempFile();
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
            ctx.setMainPeriod(VrApp.getBean(CorePlugin.class).findPeriodOrMain(periodId));
            if (aa != null) {
                importTeacher(aa, ctx);
                count++;
            }
        }
        TraceService trace = TraceService.get();
        ch.stop();
        trace.trace("Academic.import-teachers", "success", 
                MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count), 
                getClass().getSimpleName(), Level.INFO);
    }

    public AcademicStudentImport parseAcademicStudentImport(Object[] values2, StudentMapping sm) throws IOException {
        LenientArray values = new LenientArray(values2);
        String fn = VrUtils.validateContactName(values.getString(sm.COL_FIRST_NAME));
        String ln = VrUtils.validateContactName(values.getString(sm.COL_LAST_NAME));
        String nin = values.getString(sm.COL_NIN);
        if (!StringUtils.isEmpty(nin) || !StringUtils.isEmpty(fn) || !StringUtils.isEmpty(ln)) {
            AcademicStudentImport a = new AcademicStudentImport();
            a.setFirstName(fn);
            a.setLastName(ln);
            a.setNin(nin);
            a.setStartPeriodName(values.getString(sm.COL_YEAR1));
            a.setClassName(values.getString(sm.COL_CLASS));
            a.setFirstName2(values.getString(sm.COL_FIRST_NAME2));
            a.setPreClassPrepName(values.getString(sm.COL_PREP));
            a.setPreClassTypeName(values.getString(sm.COL_PREP_SECTION));
            a.setPreClassPrepRank(values.getInt(sm.COL_PREP_RANK));
            a.setPreClassPrepRankMax(values.getInt(sm.COL_PREP_RANK_MAX));
            a.setPreClassBacName(values.getString(sm.COL_BAC));
            a.setPreClassPrepScore(values.getDouble(sm.COL_PREP_SCORE));
            a.setPreClassBacScore(values.getDouble(sm.COL_BAC_SCORE));
            a.setLastName2(values.getString(sm.COL_LAST_NAME2));
            a.setPhone1(values.getString(sm.COL_GSM));
            a.setGenderName(values.getString(sm.COL_GENDER));
            a.setSubscriptionNumber(values.getString(sm.COL_SUBSCRIPTION_NBR));
            a.setCivilityName(values.getString(sm.COL_CIVILITY));
            a.setEmail(values.getString(sm.COL_EMAIL));
            a.setBirthDate(values.getDate(sm.COL_BIRTH_DATE));
            return a;
        }
        return null;
    }

    public void importStudent(AcademicStudentImport a, ImportStudentContext ctx) throws IOException {
        if (StringUtils.isEmpty(a.getNin())) {
            throw new NoSuchElementException("Nin Not Found " + a);
        }
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (ctx == null) {
            ctx = new ImportStudentContext();
        }
        if (ctx.getMainCompany() == null) {
            AppConfig appConfig = core.getCurrentConfig();
            ctx.setMainCompany(appConfig == null ? null : appConfig.getMainCompany());
        }
        if (ctx.getProfiles() == null) {
            ctx.setProfiles(new HashMap<>());
            for (AcademicClass cls : service.findAcademicClasses()) {
                AppProfile p = core.findOrCreateCustomProfile(cls.getName(), "AcademicClass");
                ctx.getProfiles().put(p.getCode(), p);
                ctx.getProfiles().put(cls.getName(), p);
            }

        }
        AppDepartment dept = null;
        if (a.getDepartmentId() != null) {
            dept = core.findDepartment(a.getDepartmentId());
            if (dept == null) {
                throw new NoSuchElementException("Department Not Found " + a.getDepartmentId());
            }
        } else if (!StringUtils.isEmpty(a.getDepartmentName())) {
            dept = core.findDepartment(a.getDepartmentName().trim());
            if (dept == null) {
                throw new NoSuchElementException("Department Not Found " + a.getDepartmentName());
            }
        }
        AppPeriod period = null;
        if (a.getStartPeriodId() != null) {
            period = core.findPeriod(a.getStartPeriodId());
            if (period == null) {
                throw new NoSuchElementException("Period Not Found " + a.getStartPeriodId());
            }
        } else {
            period = core.findPeriod(a.getStartPeriodName() == null ? null : a.getStartPeriodName().trim());
            if (period == null) {
                throw new NoSuchElementException("Period Not Found " + a.getStartPeriodName());
            }
        }

        AppGender gender = ctx.getGenders().resolveGender(a.getGenderId(), a.getGenderName(), true);
        AppCivility civility = ctx.getCivilities().resolveCivility(a.getCivilityId(), a.getCivilityName(), gender, true);

        AcademicBac bac = null;
        if (a.getPreClassBacId() != null) {
            bac = service.findAcademicBac(a.getPreClassBacId());
            if (bac == null) {
                throw new NoSuchElementException("Bac Not Found " + a.getPreClassBacId());
            }
        } else if (!StringUtils.isEmpty(a.getPreClassBacName())) {
            bac = service.findAcademicBac(a.getPreClassBacName().trim());
            if (bac == null) {
                throw new NoSuchElementException("Bac Not Found " + a.getPreClassBacName());
            }
        }

        AcademicPreClass prep = null;
        if (a.getPreClassPrepId() != null) {
            prep = service.findAcademicPreClass(a.getPreClassPrepId());
            if (prep == null) {
                throw new NoSuchElementException("Prep Not Found " + a.getPreClassBacId());
            }
        } else if (!StringUtils.isEmpty(a.getPreClassPrepName())) {
            prep = service.findAcademicPreClass(a.getPreClassPrepName().trim());
            if (prep == null) {
                throw new NoSuchElementException("Prep Not Found " + a.getPreClassPrepName());
            }
        }

        AcademicPreClassType prepType = null;
        if (a.getPreClassTypeId() != null) {
            prepType = service.findAcademicPreClassType(a.getPreClassTypeId());
            if (prepType == null) {
                throw new NoSuchElementException("Prep Type Not Found " + a.getPreClassTypeId());
            }
        } else if (!StringUtils.isEmpty(a.getPreClassTypeName())) {
            prepType = service.findAcademicPreClassType(a.getPreClassTypeName().trim());
            if (prepType == null) {
                throw new NoSuchElementException("Prep Not Found " + a.getPreClassTypeName());
            }
        }

        AcademicClass studentclass = null;
        if (a.getClassId() != null) {
            studentclass = service.findAcademicClass(a.getClassId());
            if (studentclass == null) {
                throw new NoSuchElementException("Class Not Found " + a.getPreClassBacId());
            }
        } else {
            studentclass = service.findAcademicClass(a.getClassName().trim());
            if (studentclass == null) {
                throw new NoSuchElementException("Class Not Found " + a.getClassName());
            }
        }
        if (dept == null) {
            dept = studentclass.resolveDepartment();
        }
        if (dept == null) {
            throw new NoSuchElementException("Department Not Found ");
        }
        boolean force = false;
        AppUser user = new AppUser();
        user.setNin(a.getNin());
        user.setFirstName(VrUtils.validateContactName(a.getFirstName()));
        user.setLastName(VrUtils.validateContactName(a.getLastName()));
        user.setFullName(VrUtils.validateContactName(AppContact.getName(user)));
        String fs2 = a.getFirstName2();
        if (ctx.isSimulate()) {
            AppUser old = core.findUser(user);
            if (old != null) {
                user = old;
            }
        } else {
            user = core.findOrCreateUser(user, new String[]{"Student"}, VrPasswordStrategyNin.INSTANCE);
        }
        AcademicStudent academicStudent = null;
        AcademicStudent oldAcademicStudent = service.findStudentByUser(user.getId());
        if (oldAcademicStudent != null) {
            academicStudent = oldAcademicStudent;
        } else {
            academicStudent = new AcademicStudent();
            academicStudent.setUser(user);
        }
        if (checkUpdatable(academicStudent.getUser().getDepartment(), studentclass.resolveDepartment(), force)) {
            academicStudent.getUser().setDepartment(studentclass.resolveDepartment());
        }
        if (academicStudent.getUser().getDepartment() == null) {
            academicStudent.getUser().setDepartment(dept);
        }
        String ln2 = a.getLastName2();
        user.setFirstName2(fs2);
        user.setLastName2(ln2);
        user.setFullName2(AppContact.getName2(user));

        if (checkUpdatable(user.getPhone1(), a.getPhone1(), force)) {
            user.setPhone1(a.getPhone1());
        }
        if (checkUpdatable(academicStudent.getFirstSubscription(), period, force)) {
            academicStudent.setFirstSubscription(period);
        }
        if (checkUpdatable(academicStudent.getLastClass1(), studentclass, force)) {
            academicStudent.setLastClass1(studentclass);
        }
        if (studentclass.getProgram() != null && checkUpdatable(academicStudent.getUser().getDepartment(), studentclass.resolveDepartment(), force)) {
            academicStudent.getUser().setDepartment(studentclass.resolveDepartment());
        }
        if (checkUpdatable(user.getGender(), gender, force)) {
            user.setGender(gender);
        }
        if (checkUpdatable(user.getCivility(), civility, force)) {
            user.setCivility(civility);
        }
        if (checkUpdatable(academicStudent.getSubscriptionNumber(), a.getSubscriptionNumber(), force)) {
            academicStudent.setSubscriptionNumber(a.getSubscriptionNumber());
        }
        if (checkUpdatable(academicStudent.getBaccalaureateClass(), bac, force)) {
            academicStudent.setBaccalaureateClass(bac);
        }
        if (checkUpdatable(academicStudent.getPreClass(), prep, force)) {
            academicStudent.setPreClass(prep);
        }
        if (checkUpdatable(academicStudent.getPreClassType(), prepType, force)) {
            academicStudent.setPreClassType(prepType);
        }
        if (checkUpdatable(academicStudent.getPreClassRank(), a.getPreClassPrepRank(), force)) {
            academicStudent.setPreClassRank(a.getPreClassPrepRank());
        }
        if (checkUpdatable(academicStudent.getPreClassRankMax(), a.getPreClassPrepRankMax(), force)) {
            academicStudent.setPreClassRankMax(a.getPreClassPrepRankMax());
        }
        if (checkUpdatable(academicStudent.getBaccalaureateScore(), a.getPreClassBacScore(), force)) {
            academicStudent.setBaccalaureateScore(a.getPreClassBacScore());
        }

        if (checkUpdatable(academicStudent.getPreClassScore(), a.getPreClassPrepScore(), force)) {
            academicStudent.setPreClassScore(a.getPreClassPrepScore());
        }

        if (checkUpdatable(user.getEmail(), a.getEmail(), force)) {
            user.setEmail(a.getEmail());
        }

        if (checkUpdatable(user.getBirthDate(), a.getBirthDate(), force)) {
            user.setBirthDate(a.getBirthDate());
        }
        if (checkUpdatable(user.getBirthDate(), a.getBirthDateString(), force)) {
            user.setBirthDate(LenientArray.convertDate(a.getBirthDateString()));
        }
        if (checkUpdatable(user.getPositionSuffix(), studentclass.getName(), force)) {
            user.setPositionSuffix(studentclass.getName());
        }
        user.setPositionTitle1("Student " + studentclass.getName());
        user.setDepartment(dept);
        user.setEnabled(true);
        if (checkUpdatable(user.getCompany(), ctx.getMainCompany(), force)) {
            user.setCompany(ctx.getMainCompany());
        }
        if (!ctx.isSimulate()) {
            CorePlugin.get().save(null, user);
            academicStudent.setStage(AcademicStudentStage.ATTENDING);
            if (academicStudent.getUser() == null) {
                academicStudent.setUser(user);
            }
            CorePlugin.get().save(null, academicStudent);
        }
    }

    private boolean isEmpty(Object oldVal) {
        if (oldVal == null) {
            return true;
        }
        if (oldVal instanceof String) {
            return StringUtils.isEmpty((String) oldVal);
        }
        if (oldVal instanceof Integer) {
            return ((Integer) oldVal).intValue() <= 0;
        }
        return false;
    }

    private boolean checkUpdatable(Object oldVal, Object newVal, boolean force) {
        return !isEmpty(newVal) && (force || isEmpty(oldVal));
    }

    public int importStudents(int periodId, VFile file) throws IOException {
        importStudents(periodId, file, true);
        return importStudents(periodId, file, false);
    }

    public int importStudents(int periodId, VFile file, boolean simulate) throws IOException {
        Chronometer ch = new Chronometer();
        log.log(Level.INFO, (simulate ? " Simulate " : "") + "importStudents from {0}", file);
        File tmp = file.copyToNativeTempFile();
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(false);
        sp.setSheetIndex(0);
        sp.setSkipRows(0);
        DataReader rows = sp.parse();

        StudentMapping sm = new StudentMapping();
        if (rows.hasNext()) {
            ColumnMappingMatcher.match(sm, rows.readRow().getValues());
        }

        CorePlugin core = VrApp.getBean(CorePlugin.class);
        ImportStudentContext importStudentContext = new ImportStudentContext();
        importStudentContext.setSimulate(simulate);
        importStudentContext.setMainPeriod(core.findPeriodOrMain(periodId));
        int count = 0;
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
            AcademicStudentImport a = parseAcademicStudentImport(values, sm);
            if (a != null) {
                importStudent(a, importStudentContext);
                count++;
            }
        }
        TraceService trace = TraceService.get();
        ch.stop();
        trace.trace("Academic.import-students" + (simulate ? "-simulation" : ""), "success", 
                MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count), 
                "/Education/Import", Level.INFO);
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
        File tmp = file.copyToNativeTempFile();
        SheetParser sp = pfm.createSheetParser(tmp);
        sp.setContainsHeader(false);
        CourseAssignmentsMapping sm = new CourseAssignmentsMapping();
        sp.setSheetIndex(0);
        sp.setSkipRows(1);
        DataReader rows = sp.parse();
        int all = 0;
        long count = 0;
        long maxCount = -1;
        if (rows.hasNext()) {
            ColumnMappingMatcher.match(sm, rows.readRow().getValues());
        }
        while (rows.hasNext() && (maxCount < 0 || count < maxCount)) {
            DataRow row = rows.readRow();
            LenientArray values = new LenientArray(row.getValues());
            all++;
            String nbrGroupsString = values.getString(sm.NBR_GROUPS_COLUMN);
            String ignoreString = values.getString(sm.IGNORE_COLUMN);
            String courseName = values.getString(sm.COURSE_NAME_COLUMN);

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
            if (courseName == null || courseName.trim().length() == 0) {
                ignoreRow = true;
            }

            if (!ignoreRow) {
                AcademicCourseType courseType = null;
                AppDepartment department = null;
                AppDepartment ownerDepartment = null;
                AcademicProgram program = null;
                AcademicCourseLevel courseLevel = null;
                AcademicCourseGroup courseGroup = null;
                String discipline = null;
                AcademicClass studentClass = null;
                AcademicClass studentSubClass = null;
                AcademicSemester semester = null;
                AcademicTeacher teacher = null;
                List<AcademicTeacher> teacherIntents = new ArrayList<>();
                {
                    String stringVal = values.getString(sm.DEPARTMENT_COLUMN);
                    if (!StringUtils.isEmpty(stringVal)) {
                        department = core.findDepartment(stringVal);
                        if (department == null) {
                            throw new IllegalArgumentException("Invalid Department " + stringVal);
                        }
                    }
                }
                {
                    String stringVal = values.getString(sm.OWNER_DEPARTMENT_COLUMN);
                    if (!StringUtils.isEmpty(stringVal)) {
                        ownerDepartment = core.findDepartment(stringVal);
                    }
                }
                {
                    String stringVal = values.getString(sm.PROGRAM_COLUMN);
                    if (!StringUtils.isEmpty(stringVal)) {
                        if (department != null) {
                            program = service.findProgram(department.getId(), stringVal);
                        } else {
                            List<AcademicProgram> allByName = service.findPrograms(stringVal);
                            if (allByName.size() > 1) {
                                throw new IllegalArgumentException("Too many Programs with name " + stringVal + " . Please mention department");
                            } else if (allByName.size() > 0) {
                                program = allByName.get(0);
                            }
                        }
                        if (program == null) {
                            throw new IllegalArgumentException("Invalid Program " + stringVal);
                        }
                    }
                }
                {
                    String stringVal = values.getString(sm.STUDENT_CLASS_COLUMN);
                    if (StringUtils.isEmpty(stringVal)) {
                        throw new IllegalArgumentException("Missing Class Name");
                    }
                    if (program == null) {
                        List<AcademicClass> allByName = service.findAcademicClasses(stringVal);
                        if (allByName.size() > 1) {
                            throw new IllegalArgumentException("Too many Classes with name " + stringVal + " . Please mention program");
                        } else if (allByName.size() > 0) {
                            studentClass = allByName.get(0);
                        }
                    } else {
                        studentClass = service.findAcademicClass(program.getId(), stringVal);
                    }
                    if (studentClass == null) {
                        throw new IllegalArgumentException("Invalid Class " + stringVal);
                    }
                    if (program == null) {
                        program = studentClass.getProgram();
                    } else if (program.getId() != studentClass.getProgram().getId()) {
                        throw new IllegalArgumentException("Invalid Class " + stringVal + " for Program " + program.getName());
                    }
                    if (department == null) {
                        department = studentClass.resolveDepartment();
                    } else if (department.getId() != studentClass.resolveDepartment().getId()) {
                        throw new IllegalArgumentException("Invalid Class " + stringVal + " for Department " + program.getName());
                    }
                }
                {
                    String stringVal = values.getString(sm.STUDENT_SUBLASS_COLUMN);
                    if (!StringUtils.isEmpty(stringVal)) {
                        studentSubClass = service.findAcademicClass(program.getId(), stringVal);
                        if (studentSubClass == null) {
                            throw new IllegalArgumentException("Invalid Class " + stringVal);
                        }
                    }
                }
                {
                    String stringVal = values.getString(sm.SEMESTER_COLUMN);
                    if (stringVal == null) {
                        throw new IllegalArgumentException("Missing semester for " + courseName);
                    }
                    semester = service.findSemester(stringVal);
                    if (semester == null) {
                        throw new IllegalArgumentException("Invalid semester " + stringVal);
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
                        CorePlugin.get().save(null, courseLevel);
                    }
                }
                {
                    String stringVal = values.getString(sm.COURSE_GROUP_COLUMN);
                    if (stringVal != null) {
                        courseGroup = service.findCourseGroup(periodId, courseLevel.getAcademicClass().getId(), stringVal);
                        if (courseGroup == null) {
                            courseGroup = new AcademicCourseGroup();
                            courseGroup.setName(stringVal);
                            courseGroup.setAcademicClass(courseLevel.getAcademicClass());
                            courseGroup.setPeriod(period);
                            CorePlugin.get().save(null, courseGroup);
                        }
                    }
                }
                {
                    discipline = service.formatDisciplinesNames(values.getString(sm.DISCIPLINE_COLUMN), true);
                }
                {
                    String stringVal = values.getString(sm.COURSE_TYPE_COLUMN);
                    if (stringVal == null) {
                        throw new IllegalArgumentException("Missing module type for " + courseName);
                    }
                    courseType = service.findCourseType(stringVal);
                    if (courseType == null) {
                        throw new IllegalArgumentException("Invalid course type " + stringVal);
                    }
                }
                double valueC = values.getDouble(sm.LOAD_C_COLUMN);
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
                double valueTD = values.getDouble(sm.LOAD_TD_COLUMN);
                double valueTP = values.getDouble(sm.LOAD_TP_COLUMN);
                double valuePM = values.getDouble(sm.LOAD_PM_COLUMN);
                double nbrGroups = values.getDouble(sm.NBR_GROUPS_COLUMN);
                double nbrShares = values.getDoubleOr1(sm.NBR_SHARES_COLUMN);
                String teacherName = values.getString(sm.TEACHER_NAME_COUMN);
                String teacherIntentsString = values.getString(sm.TEACHER_INTENTS_COUMN);
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
                    AcademicCoursePlan coursePlan = service.findCoursePlan(periodId, courseLevel.getId(), coursePlanName);
                    if (coursePlan == null) {
                        coursePlan = new AcademicCoursePlan();
                        coursePlan.setName(coursePlanName);
                        coursePlan.setCourseLevel(courseLevel);
                        coursePlan.setCourseGroup(courseGroup);
                        coursePlan.setDiscipline(discipline);
                        coursePlan.setPeriod(period);
                        coursePlan.setValueC(valueC);
                        coursePlan.setValueTD(valueTD);
                        coursePlan.setValueTP(valueTP);
                        coursePlan.setValuePM(valuePM);
                        CorePlugin.get().save(null, coursePlan);
                    }

                    AcademicCourseAssignment d = service.findCourseAssignment(coursePlan.getId(), studentClass == null ? null : studentClass.getId(), null);
                    if (d == null) {
                        d = new AcademicCourseAssignment();
                        d.setName(courseName);
                        d.setCoursePlan(coursePlan);
                        d.setOwnerDepartment(ownerDepartment != null ? ownerDepartment : department);
                        d.setSubClass(studentSubClass);
                        d.setValueC(valueC);
                        d.setValueTD(valueTD);
                        d.setValueTP(valueTP);
                        d.setValuePM(valuePM);
                        d.setCourseType(courseType);
                        d.setGroupCount(nbrGroups);
                        d.setShareCount(nbrShares);
                        d.setValueEffWeek(effWeek);
                        d.setTeacher(teacher);
                        CorePlugin.get().save(null, d);
                        count++;
                    } else {
                        for (AcademicTeacher ti : teacherIntents) {
                            service.addIntent(ti.getId(), d.getId());
                        }
                    }
                }

            } else {
                System.out.println("Ignored Row " + Arrays.asList(values));
            }
        }
        TraceService trace = TraceService.get();
        ch.stop();
        trace.trace("Academic.import-course-assignments", "success", 
                MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count), 
                getClass().getSimpleName(), Level.INFO);
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

        private VFile file;
        private int order;

        public ImportFile(VFile file, int order) {
            this.file = file;
            this.order = order;
        }

        @Override
        public int compareTo(ImportFile o) {
            return order - o.order;
        }

        public VFile getFile() {
            return file;
        }

        public ImportFile setFile(VFile file) {
            this.file = file;
            return this;
        }

        public int getOrder() {
            return order;
        }

        public ImportFile setOrder(int order) {
            this.order = order;
            return this;
        }
    }

}
