/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.integration;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.*;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.service.integration.mapping.CourseAssignmentsMapping;
import net.thevpc.app.vainruling.plugins.academic.service.integration.mapping.CoursePlansMapping;
import net.thevpc.app.vainruling.plugins.academic.service.integration.mapping.StudentMapping;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicPreClass;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicBac;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudentStage;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionTable;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseType;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherSituation;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherSemestrialLoad;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseLevel;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicPreClassType;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicProgram;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseGroup;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionRule;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherDegree;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicOfficialDiscipline;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionRow;
import net.thevpc.app.vainruling.core.service.util.ColumnMappingMatcher;
import net.thevpc.app.vainruling.core.service.util.LenientArray;
import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.*;
import net.thevpc.app.vainruling.core.service.util.VrPasswordStrategyNin;
import net.thevpc.app.vainruling.core.service.util.VrPasswordStrategyRandom;
import net.thevpc.app.vainruling.plugins.academic.model.imp.AcademicStudentImport;
import net.thevpc.app.vainruling.plugins.academic.model.imp.AcademicTeacherImport;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.util.Chronometer;
import net.thevpc.common.util.Convert;
import net.thevpc.common.util.DoubleParserConfig;
import net.thevpc.common.util.IntegerParserConfig;
import net.thevpc.common.vfs.VFile;
import net.thevpc.upa.UPA;
import net.thevpc.upa.bulk.DataReader;
import net.thevpc.upa.bulk.DataRow;
import net.thevpc.upa.bulk.ParseFormatManager;
import net.thevpc.upa.bulk.SheetParser;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.thevpc.common.collections.MapUtils;

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
        Chronometer ch = Chronometer.start();
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
        trace.trace("Academic.import-departments", "success", MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count),
                getClass().getSimpleName(), Level.INFO);
    }

    public void importTeacherDegrees(VFile file) throws IOException {
        AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        Chronometer ch = Chronometer.start();
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
        Chronometer ch = Chronometer.start();
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
                if (StringUtils.isBlank(tableName)) {
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

    private boolean isXlsxImportFile(VFile file, String name, ImportOptions importOptions) {
        if (importOptions != null) {
            if (importOptions.getFileTypeName() != null && importOptions.getFileTypeName().length() > 0) {
                if (importOptions.getFileTypeName().equals(name)) {
                    return true;
                }
                return false;
            }
        }
        return (file.getName().equals(name + ".xlsx") || file.getName().endsWith("." + name + ".xlsx"));
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

            if (isXlsxImportFile(file, "departments", importOptions)) {
                return Arrays.asList(new ImportFile(file, 1));
            }
            if (isXlsxImportFile(file, "teacher-degrees", importOptions)) {
                return Arrays.asList(new ImportFile(file, 2));
            }
            if (isXlsxImportFile(file, "teachers", importOptions)) {
                return Arrays.asList(new ImportFile(file, 3));
            }
            if (isXlsxImportFile(file, "students", importOptions)) {
                return Arrays.asList(new ImportFile(file, 4));
            }
            if (isXlsxImportFile(file, "course-assignments", importOptions)) {
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
        Chronometer ch = Chronometer.start();
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

        if (isXlsxImportFile(file, "departments", importOptions)) {
            importDepartments(file);
        }
        if (isXlsxImportFile(file, "load-conversion-table", importOptions)) {
            if (file.length() > 0) {
                count++;
                importLoadConversionTable(file);
            } else {
                count++;
                trace.trace("Academic.import-load-conversion-table-default", "success", null, getClass().getSimpleName(), Level.INFO);
                XlsxLoadImporter.this.importLoadConversionTable();
            }
        }
        if (isXlsxImportFile(file, "load-degrees", importOptions)) {
            if (file.length() > 0) {
                count++;
                importTeacherDegrees(file);
            } else {
                count++;
                trace.trace("Academic.import-teacher-degrees-default", "success", null, "/Education/Config", Level.INFO);
                XlsxLoadImporter.this.importTeacherDegrees();
            }
        }
        if (isXlsxImportFile(file, "teachers", importOptions)) {
            count++;
            importTeachers(periodId, file);
        }
        if (isXlsxImportFile(file, "students", importOptions)) {
            count++;
            importStudents(periodId, file);
        }
        if (isXlsxImportFile(file, "course-assignments", importOptions)) {
            count++;
            importCourseAssignments(periodId, file, importOptions);
        }
        if (isXlsxImportFile(file, "course-plans", importOptions)) {
            count++;
            importCoursePlans(periodId, file, importOptions);
        }
        ch.stop();
        String ch2 = ch.toString();
        if (count > 0) {
            trace.trace("Academic.import-teaching-load", "success", MapUtils.map("path", file.getPath(), "time", ch2.toString(), "rows", count), "/Education/Config", Level.INFO);
        } else {
            trace.trace("Academic.import-teaching-load", "error", MapUtils.map("path", file.getPath(), "time", ch2.toString(), "rows", count), "/Education/Config", Level.INFO);
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
        if (!StringUtils.isBlank(academicTeacherImport.getNin()) || !StringUtils.isBlank(academicTeacherImport.getFirstName()) || !StringUtils.isBlank(academicTeacherImport.getLastName())) {
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
        AcademicTeacherPeriod academicTeacherPeriod = service.findTeacherPeriod(mainPeriodId, academicTeacher.getId());
        if (academicTeacherPeriod.getId() < 0) {
            //this is temp period
            service.updateTeacherPeriod(mainPeriodId, academicTeacher.getId(), -1);
        }
    }

    public void importTeachers(int periodId, VFile file) throws IOException {
        Chronometer ch = Chronometer.start();
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
        if (!StringUtils.isBlank(nin) || !StringUtils.isBlank(fn) || !StringUtils.isBlank(ln)) {
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
            a.setPreClassChoice(values.getInt(sm.COL_PREP_RANK_ASSIGNMENT));
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
        if (StringUtils.isBlank(a.getNin())) {
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
        } else if (!StringUtils.isBlank(a.getDepartmentName())) {
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

        AppGender gender = ctx.getGenders().resolveGender(a.getGenderId(), a.getGenderName(), false);
        AppCivility civility = ctx.getCivilities().resolveCivility(a.getCivilityId(), a.getCivilityName(), gender, false);
        if (gender == null && civility == null) {
            gender = ctx.getGenders().resolveGender(a.getGenderId(), a.getGenderName(), true);
        } else if (gender == null) {
            //deduce gender by civility
            switch (String.valueOf(civility.getName()).toLowerCase()) {
                case "mlle":
                case "mlle.":
                case "ml":
                case "mle":
                case "mle.":
                case "mme.":
                case "mme": {
                    gender = ctx.getGenders().resolveGender(null, "F", true);
                    break;
                }
                default: {
                    gender = ctx.getGenders().resolveGender(null, "M", true);
                    break;
                }
            }
        } else if (civility == null) {
            //deduce civility by gender
            civility = ctx.getCivilities().resolveCivility(null, "Mlle", gender, true);
        }
        AcademicBac bac = null;
        if (a.getPreClassBacId() != null) {
            bac = service.findAcademicBac(a.getPreClassBacId());
            if (bac == null) {
                throw new NoSuchElementException("Bac Not Found " + a.getPreClassBacId());
            }
        } else if (!StringUtils.isBlank(a.getPreClassBacName())) {
            Map<String, AcademicBac> all = (Map<String, AcademicBac>) ctx.getCache().get(AcademicBac.class.getName());
            if (all == null) {
                all = new HashMap<>();
                ctx.getCache().put(AcademicBac.class.getName(), all);
                for (AcademicBac b : service.findAcademicBacs()) {
                    all.put(VrUtils.normalizeName(b.getName()), b);
                    for (String v : VrUtils.parseNormalizedOtherNames(b.getOtherNames())) {
                        all.put(v, b);
                    }
                }
            }
            bac = all.get(VrUtils.normalizeName(a.getPreClassBacName().trim()));
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
        } else if (!StringUtils.isBlank(a.getPreClassPrepName())) {
            Map<String, AcademicPreClass> all = (Map<String, AcademicPreClass>) ctx.getCache().get(AcademicPreClass.class.getName());
            if (all == null) {
                all = new HashMap<>();
                ctx.getCache().put(AcademicPreClass.class.getName(), all);
                for (AcademicPreClass b : service.findAcademicPreClasses()) {
                    all.put(VrUtils.normalizeName(b.getName()), b);
                    for (String v : VrUtils.parseNormalizedOtherNames(b.getOtherNames())) {
                        all.put(v, b);
                    }
                }
            }

            prep = all.get(VrUtils.normalizeName(a.getPreClassPrepName().trim()));
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
        } else if (!StringUtils.isBlank(a.getPreClassTypeName())) {
            Map<String, AcademicPreClassType> all = (Map<String, AcademicPreClassType>) ctx.getCache().get(AcademicPreClassType.class.getName());
            if (all == null) {
                all = new HashMap<>();
                ctx.getCache().put(AcademicPreClassType.class.getName(), all);
                for (AcademicPreClassType b : service.findAcademicPreClassTypes()) {
                    all.put(VrUtils.normalizeName(b.getName()), b);
                    for (String v : VrUtils.parseNormalizedOtherNames(b.getOtherNames())) {
                        all.put(v, b);
                    }
                }
            }

            prepType = all.get(VrUtils.normalizeName(a.getPreClassTypeName().trim()));
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
            Map<String, AcademicClass> all = (Map<String, AcademicClass>) ctx.getCache().get(AcademicClass.class.getName());
            if (all == null) {
                all = new HashMap<>();
                ctx.getCache().put(AcademicClass.class.getName(), all);
                for (AcademicClass b : service.findAcademicClasses()) {
                    all.put(VrUtils.normalizeName(b.getName()), b);
                    for (String v : VrUtils.parseNormalizedOtherNames(b.getOtherNames())) {
                        all.put(v, b);
                    }
                }
            }

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
        AppUserType studentType = core.findUserType("Student");
        user.setType(studentType);
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
            return StringUtils.isBlank((String) oldVal);
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
        Chronometer ch = Chronometer.start();
        log.log(Level.INFO, (simulate ? " Simulate " : "") + "importStudents from {0}", file);
        StudentMapping sm = new StudentMapping();
        DataReader rows = openXlsxFile(file, sm);

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

    public void importCourseAssignments(Integer defaultPeriodId, VFile file, ImportOptions importOptions) throws IOException {
        importCourseAssignments(defaultPeriodId, file, importOptions, true);
        importCourseAssignments(defaultPeriodId, file, importOptions, false);
    }

    public void importCourseAssignments(Integer defaultPeriodId, VFile file, ImportOptions importOptions, boolean simulate) throws IOException {
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        final CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (importOptions == null) {
            importOptions = new ImportOptions();
        }
        ParseHelper ph = new ParseHelper();
        Chronometer ch = Chronometer.start();
        if (!simulate) {
            log.log(Level.INFO, "importCourseAssignments from {0}", file);
        }
        int all = 0;
        long count = 0;
        long maxCount = -1;
        CourseAssignmentsMapping sm = new CourseAssignmentsMapping();
        DataReader rows = openXlsxFile(file, sm);
        if (sm.COURSE_NAME_COLUMN < 0) {
            throw new IllegalArgumentException("Missing Course Name");
        }

        Set<Integer> visitedPeriodIds = new HashSet<>();
        while (rows.hasNext() && (maxCount < 0 || count < maxCount)) {
            DataRow row = rows.readRow();
            LenientArray values = new LenientArray(row.getValues());
            all++;
            String nbrGroupsString = values.getString(sm.NBR_GROUPS_COLUMN);
            String ignoreString = values.getString(sm.IGNORE_COLUMN);
            String courseName = values.getString(sm.COURSE_NAME_COLUMN);
            String periodName = values.getString(sm.PERIOD_COLUMN);

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
            if (StringUtils.isBlank(courseName)) {
                ignoreRow = true;
            }

            if (!ignoreRow) {
                AppPeriod period = sm.PERIOD_COLUMN < 0 ? defaultPeriodId == null ? null : core.findPeriod(defaultPeriodId) : core.findPeriod(periodName);
                if (period == null) {
                    throw new IllegalArgumentException("Missing Period");
                }
                visitedPeriodIds.add(period.getId());
                AcademicCourseType courseType = null;
                AppDepartment department = null;
                AppDepartment ownerDepartment = null;
                AcademicProgram program = null;
                AcademicCourseLevel courseLevel = null;
                AcademicClass studentClass = null;
                AcademicClass studentSubClass = null;
                AcademicSemester semester = null;
                AcademicTeacher teacher = null;
                List<AcademicTeacher> teacherIntents = new ArrayList<>();
                List<AcademicTeacher> teacherProposals = new ArrayList<>();
                department = ph.parseDepartment(values.getString(sm.DEPARTMENT_COLUMN));
                ownerDepartment = ph.parseDepartment(values.getString(sm.OWNER_DEPARTMENT_COLUMN));
                program = ph.parseProgram(values.getString(sm.PROGRAM_COLUMN), department == null ? null : department.getId());
                studentClass = ph.parseClassRequired(values.getString(sm.STUDENT_CLASS_COLUMN), program == null ? null : program.getId(), courseName);
                studentSubClass = ph.parseClass(values.getString(sm.STUDENT_SUBLASS_COLUMN), program == null ? null : program.getId());
                ph.chechMatch(studentClass, program, department);
                if (program == null) {
                    program = studentClass.getProgram();
                }
                if (department == null) {
                    department = program.getDepartment();
                }
                if (ownerDepartment == null) {
                    ownerDepartment = department;
                }
                semester = ph.parseSemesterRequired(values.getString(sm.SEMESTER_COLUMN), courseName);
                courseLevel = ph.getOrCreateCourseLevel(studentClass, semester);
                courseType = ph.parseCourseTypeRequired(values.getString(sm.COURSE_TYPE_COLUMN), courseName);
                double valueC = values.getDouble(sm.LOAD_C_COLUMN);
                double valueTD = values.getDouble(sm.LOAD_TD_COLUMN);
                double valueTP = values.getDouble(sm.LOAD_TP_COLUMN);
                double valuePM = values.getDouble(sm.LOAD_PM_COLUMN);
                double nbrGroups = values.getDouble(sm.NBR_GROUPS_COLUMN);
                teacher = ph.parseTeacher(values.getString(sm.TEACHER_NAME_COLUMN));
                teacherIntents = ph.parseTeacherList(values.getString(sm.TEACHER_INTENTS_COLUMN));
                teacherProposals = ph.parseTeacherList(values.getString(sm.TEACHER_PROPOSAL_COLUMN));
                if (teacher == null && teacherIntents.isEmpty() && teacherProposals.isEmpty()) {
                    throw new IllegalArgumentException("Missing Assignment in Row " + Arrays.asList(values));
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
                AcademicCoursePlan coursePlan = service.findCoursePlan(period.getId(), courseLevel.getId(), coursePlanName);
                if (coursePlan == null) {
                    throw new IllegalArgumentException("Module not found " + period.getName() + " : " + courseLevel.getName() + " : " + coursePlanName);
                }
                boolean processed = false;
                AcademicCourseAssignment d = simulate ? null : service.findCourseAssignment(coursePlan.getId(), studentClass == null ? null : studentClass.getId(), null);
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
                    d.setValueEffWeek(effWeek);
                    d.setTeacher(teacher);
                    processed = true;
                    if (!simulate) {
                        core.save(null, d);
                    }
                } else {
                    if (teacher != null) {
                        d.setTeacher(teacher);
                        processed = true;
                        if (!simulate) {
                            core.save(null, d);
                        }
                    }
                }
                if (!simulate) {
                    for (AcademicTeacher ti : teacherIntents) {
                        processed = true;
                        service.addIntent(ti.getId(), d.getId());
                    }
                    for (AcademicTeacher ti : teacherProposals) {
                        processed = true;
                        service.addProposal(ti.getId(), d.getId());
                    }
                }
                if (processed) {
                    count++;
                }
            } else {
                System.out.println("Ignored Row " + Arrays.asList(values));
            }
        }
        TraceService trace = TraceService.get();
        ch.stop();
        if (!simulate) {
            trace.trace("Academic.import-course-assignments", "success",
                    MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count),
                    getClass().getSimpleName(), Level.INFO);
            log.log(Level.INFO, "importCourseAssignments from {0} in {1}", new Object[]{file, ch.stop()});
            for (Integer periodId : visitedPeriodIds) {
                service.updateAllCoursePlanValuesByLoadValues(periodId);
            }
        }
    }

    public void importCoursePlans(Integer defaultPeriodId, VFile file, ImportOptions importOptions) throws IOException {
        importCoursePlans(defaultPeriodId, file, importOptions, true);
        importCoursePlans(defaultPeriodId, file, importOptions, false);
    }

    private DataReader openXlsxFile(VFile file, Object columnMapping) {
        try {
            File tmp = file.copyToNativeTempFile();
            SheetParser sp = pfm.createSheetParser(tmp);
            sp.setContainsHeader(false);
            sp.setSheetIndex(0);
            DataReader rows = sp.parse();
            boolean columnsDetect = false;
            if (rows.hasNext()) {
                if (ColumnMappingMatcher.match(columnMapping, rows.readRow().getValues()) >= 3) {
                    columnsDetect = true;
                } else {
                    if (rows.hasNext()) {
                        if (ColumnMappingMatcher.match(columnMapping, rows.readRow().getValues()) < 3) {
                            columnsDetect = true;
                        }
                    }
                }
            }
            if (!columnsDetect) {
                throw new IllegalArgumentException("Unable to resolve columns for file " + file.getPath());
            }
            return rows;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void importCoursePlans(Integer defaultPeriodId, VFile file, ImportOptions importOptions, boolean simulate) {
        final AcademicPlugin service = VrApp.getBean(AcademicPlugin.class);
        final CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (importOptions == null) {
            importOptions = new ImportOptions();
        }
        ParseHelper ph = new ParseHelper();
        Chronometer ch = Chronometer.start();
        if (!simulate) {
            log.log(Level.INFO, "importCoursePlans from {0}", file);
        }
        CoursePlansMapping sm = new CoursePlansMapping();
        DataReader rows = openXlsxFile(file, sm);
        if (sm.COURSE_NAME_COLUMN < 0) {
            throw new IllegalArgumentException("Missing Course Name");
        }
        int all = 0;
        long count = 0;
        long maxCount = -1;
        Set<Integer> visitedPeriodIds = new HashSet<>();
        while (rows.hasNext() && (maxCount < 0 || count < maxCount)) {
            DataRow row = rows.readRow();
            LenientArray values = new LenientArray(row.getValues());
            all++;
            String ignoreString = values.getString(sm.IGNORE_COLUMN);
            String courseName = values.getString(sm.COURSE_NAME_COLUMN);
            String periodName = values.getString(sm.PERIOD_COLUMN);

            boolean ignoreRow = false;
            if (courseName == null || courseName.trim().length() == 0) {
                ignoreRow = true;
            } else if (ignoreString != null
                    && ("X".equalsIgnoreCase(ignoreString)
                    || "true".equalsIgnoreCase(ignoreString)
                    || "yes".equalsIgnoreCase(ignoreString)
                    || "oui".equalsIgnoreCase(ignoreString))) {
                ignoreRow = true;
            }
            if (StringUtils.isBlank(courseName)) {
                ignoreRow = true;
            }

            if (!ignoreRow) {
                AppPeriod period = sm.PERIOD_COLUMN < 0 ? defaultPeriodId == null ? null : core.findPeriod(defaultPeriodId) : core.findPeriod(periodName);
                if (period == null) {
                    throw new IllegalArgumentException("Missing Period");
                }
                visitedPeriodIds.add(period.getId());
//                AcademicCourseType courseType = null;
                AppDepartment department = null;
//                AppDepartment ownerDepartment = null;
                AcademicProgram program = null;
                AcademicCourseLevel courseLevel = null;
                AcademicCourseGroup courseGroup = null;
                AcademicTeacher responsible = null;
                String discipline = null;
                AcademicClass studentClass = null;
//                AcademicClass studentSubClass = null;
                AcademicSemester semester = null;
                department = ph.parseDepartment(values.getString(sm.DEPARTMENT_COLUMN));
                program = ph.parseProgram(values.getString(sm.PROGRAM_COLUMN), department == null ? null : department.getId());
                studentClass = ph.parseClassRequired(values.getString(sm.STUDENT_CLASS_COLUMN), program == null ? null : program.getId(), courseName);
                ph.chechMatch(studentClass, program, department);
                if (program == null) {
                    program = studentClass.getProgram();
                }
                if (department == null) {
                    department = program.getDepartment();
                }
                semester = ph.parseSemesterRequired(values.getString(sm.SEMESTER_COLUMN), courseName);
                responsible = ph.parseTeacher(values.getString(sm.RESPONSIBLE_NAME_COLUMN));
                courseLevel = ph.getOrCreateCourseLevel(studentClass, semester);
                courseGroup = ph.parseOrCreateCourseGroup(values.getString(sm.COURSE_GROUP_COLUMN), studentClass, period);
                discipline = service.formatDisciplinesNames(values.getString(sm.DISCIPLINE_COLUMN), true);
                double valueC = values.getDouble(sm.LOAD_C_COLUMN);
                double valueTD = values.getDouble(sm.LOAD_TD_COLUMN);
                double valueTP = values.getDouble(sm.LOAD_TP_COLUMN);
                double valuePM = values.getDouble(sm.LOAD_PM_COLUMN);
                double credits = values.getDouble(sm.CREDITS_COLUMN);
                double ects = values.getDouble(sm.ECTS_COLUMN);
                int nbrGroupsC = values.getInt(sm.NBR_GROUPS_C_COLUMN);
                int nbrGroupsTD = values.getInt(sm.NBR_GROUPS_TD_COLUMN);
                int nbrGroupsTP = values.getInt(sm.NBR_GROUPS_TP_COLUMN);
                int nbrGroupsPM = values.getInt(sm.NBR_GROUPS_PM_COLUMN);

                if (nbrGroupsC == 0 && nbrGroupsTD == 0 && nbrGroupsTP == 0 && nbrGroupsPM == 0) {
                    if (valueC > 0) {
                        nbrGroupsC = 1;
                    }
                    if (valueTD > 0) {
                        nbrGroupsTD = 1;
                    }
                    if (valueTP > 0) {
                        nbrGroupsTP = 2;
                    }
                    if (valuePM > 0) {
                        nbrGroupsPM = 2;
                    }
                } else if (nbrGroupsC > 0 && nbrGroupsTD == 0 && nbrGroupsTP == 0 && nbrGroupsPM == 0) {
                    int x = nbrGroupsC;
                    if (valueC > 0) {
                        nbrGroupsC = x;
                    } else {
                        nbrGroupsC = 0;
                    }
                    if (valueTD > 0) {
                        nbrGroupsTD = x;
                    }
                    if (valueTP > 0) {
                        nbrGroupsTP = 2 * x;
                    }
                    if (valuePM > 0) {
                        nbrGroupsPM = 2 * x;
                    }
                }
                if (valueC < 0 || nbrGroupsC < 0 || (valueC * nbrGroupsC == 0 && nbrGroupsC + valueC != 0)) {
                    throw new IllegalArgumentException("Invalid value/group C for " + courseName);
                }
                if (valueTD < 0 || nbrGroupsTD < 0 || (valueTD * nbrGroupsTD == 0 && valueTD + nbrGroupsTD != 0)) {
                    throw new IllegalArgumentException("Invalid value/group TD for " + courseName);
                }
                if (valueTP < 0 || nbrGroupsTP < 0 || (valueTP * nbrGroupsTP == 0 && valueTP + nbrGroupsTP != 0)) {
                    throw new IllegalArgumentException("Invalid value/group TP for " + courseName);
                }
                if (valuePM < 0 || nbrGroupsPM < 0 || (valuePM * nbrGroupsPM == 0 && valuePM + nbrGroupsPM != 0)) {
                    throw new IllegalArgumentException("Invalid value/group PM for " + courseName);
                }

                String delegatedModuleName = values.getString(sm.DELEGATED_COURSE_NAME_COLUMN);
                AcademicClass delegatedStudentClass = null;
                AcademicCoursePlan delegatedCoursePlan = null;
                if (!StringUtils.isBlank(delegatedModuleName)) {
                    delegatedStudentClass = ph.parseClassRequired(values.getString(sm.DELEGATED_STUDENT_CLASS_COLUMN), null, courseName);
                    ph.chechMatch(delegatedStudentClass, null, department);
                    AcademicSemester delegatedSemester = ph.parseSemesterRequired(values.getString(sm.SEMESTER_COLUMN), courseName);
                    AcademicCourseLevel delegatedCourseLevel = ph.getOrCreateCourseLevel(delegatedStudentClass, delegatedSemester);

                    delegatedCoursePlan = service.findCoursePlan(period.getId(), delegatedCourseLevel.getId(), delegatedModuleName);
                    if (delegatedCoursePlan == null) {
                        throw new IllegalArgumentException("Delegated module " + delegatedModuleName + " not found");
                    }
                }

                AcademicCoursePlan coursePlan = service.findCoursePlan(period.getId(), courseLevel.getId(), courseName);
                if (coursePlan == null) {
                    coursePlan = new AcademicCoursePlan();
                    coursePlan.setName(courseName);
                    coursePlan.setCourseLevel(courseLevel);
                    coursePlan.setPeriod(period);

                    coursePlan.setCourseGroup(courseGroup);
                    coursePlan.setDiscipline(discipline);
                    coursePlan.setValueC(valueC);
                    coursePlan.setValueTD(valueTD);
                    coursePlan.setValueTP(valueTP);
                    coursePlan.setValuePM(valuePM);
                    coursePlan.setGroupCountC(nbrGroupsC);
                    coursePlan.setGroupCountTD(nbrGroupsTD);
                    coursePlan.setGroupCountTP(nbrGroupsTP);
                    coursePlan.setGroupCountPM(nbrGroupsPM);
                    coursePlan.setResponsible(responsible);
                    coursePlan.setDelegate(delegatedCoursePlan);
                    coursePlan.setCredits(credits);
                    coursePlan.setEcts(ects);
                    if (!simulate) {
                        core.save(null, coursePlan);
                    }
                } else {
                    boolean updated = false;
                    if (courseGroup != null && coursePlan.getCourseGroup() == null) {
                        coursePlan.setCourseGroup(courseGroup);
                        updated = true;
                    }
                    if (responsible != null && coursePlan.getResponsible() == null) {
                        coursePlan.setResponsible(responsible);
                        updated = true;
                    }
                    if (delegatedCoursePlan != null && coursePlan.getDelegate() == null) {
                        coursePlan.setDelegate(delegatedCoursePlan);
                        updated = true;
                    }
                    if (!StringUtils.isBlank(discipline) && StringUtils.isBlank(coursePlan.getDiscipline())) {
                        coursePlan.setDiscipline(discipline);
                        updated = true;
                    }
                    if (credits > 0) {
                        coursePlan.setCredits(credits);
                        updated = true;
                    }
                    if (ects > 0) {
                        coursePlan.setEcts(ects);
                        updated = true;
                    }
                }
                count++;
            } else {
                System.out.println("Ignored Row " + Arrays.asList(values));
            }
        }
        TraceService trace = TraceService.get();
        ch.stop();
        if (!simulate) {
            trace.trace("Academic.import-course-plans", "success",
                    MapUtils.map("path", file.getPath(), "time", ch.toString(), "rows", count),
                    getClass().getSimpleName(), Level.INFO);
            log.log(Level.INFO, "importCoursePlans from {0} in {1}", new Object[]{file, ch.stop()});
            for (Integer periodId : visitedPeriodIds) {
                service.updateAllCoursePlanValuesByLoadValues(periodId);
            }
        }
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
