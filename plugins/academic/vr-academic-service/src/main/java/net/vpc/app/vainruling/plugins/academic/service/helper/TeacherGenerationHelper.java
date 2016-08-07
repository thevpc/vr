package net.vpc.app.vainruling.plugins.academic.service.helper;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.ExcelTemplate;
import net.vpc.app.vainruling.core.service.util.NamedDoubles;
import net.vpc.app.vainruling.plugins.academic.service.*;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherPeriodStat;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherSemesterStat;
import net.vpc.common.streams.PathInfo;
import net.vpc.common.strings.MapStringConverter;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.common.vfs.impl.NativeVFS;
import net.vpc.common.vfs.impl.VZipOptions;
import net.vpc.common.vfs.impl.VZipUtils;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vpc on 6/25/16.
 */
public class TeacherGenerationHelper {
    private static final Logger log = Logger.getLogger(TeacherGenerationHelper.class.getName());
    private static PropertyPlaceholderHelper h = new PropertyPlaceholderHelper("${", "}");
    private static DecimalFormat FF = new DecimalFormat("0.0#");

    private static String dformat(double d) {
        return FF.format(d);
    }

    private static String iformat(double d) {
        if (Math.floor(d) == d) {
            return String.valueOf((int) d);
        }
        return FF.format(d);
    }

    public void generate(TeacherGenerationOptions filter) throws IOException {
        generate(filter, null);
    }

    public void generate(TeacherGenerationOptions options, StatCache cache) throws IOException {
        if (cache == null) {
            cache = new StatCache();
        }
        Chronometer ch = new Chronometer();
        if (options == null) {
            options = new TeacherGenerationOptions();
        }
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);

        Integer[] teacherIds = options.getTeacherIds();
        String semester = options.getSemester();
        String templateFolder = options.getTemplateFolder();
        String outputFolder = options.getOutputFolder();
        String outputNamePattern = options.getOutputNamePattern();
        AppPeriod period = options.getPeriod();
        if (period == null) {
            AppConfig appConfig = core.findAppConfig();
            period = appConfig==null?null:appConfig.getMainPeriod();
        }
        int periodId = period.getId();
        if (!outputNamePattern.contains("*")) {
            outputNamePattern = "*-" + outputNamePattern;
        }
        //remove extensions!, remove leading path, retain just name part
        {
            PathInfo uu = PathInfo.create(outputNamePattern);
            outputNamePattern = uu.getNamePart();
        }
        List<TeacherPeriodStat> stats = null;
        Set<GeneratedContent> requestedContents = options.getContents() == null ? new HashSet() : new HashSet<>(Arrays.asList(options.getContents()));

        if (requestedContents.isEmpty()) {
            requestedContents.addAll(Arrays.asList(GeneratedContent.values()));
        }
        boolean writeVars = requestedContents.contains(GeneratedContent.Vars);
        final VirtualFileSystem fs = core.getFileSystem();
        if (requestedContents.contains(GeneratedContent.TeacherListAssignmentsSummary)) {
            if (stats == null) {
                stats = academicPlugin.evalTeacherStatList(periodId, teacherIds, semester, options.getCourseFilter(), cache);
            }
            String teacherListAssignmentsSummaryFile_template = templateFolder + "/teacher-list-assignments-summary-template.xls";

            generateTeacherListAssignmentsSummaryFile(periodId, stats.toArray(new TeacherPeriodStat[stats.size()]),
                    cache, fs.get(teacherListAssignmentsSummaryFile_template), fs.get(outputFolder + File.separator + outputNamePattern.replace("*", "teacher-list-assignments-summary") + ".xls"), writeVars);
        }
        String teacherAssignments_template = templateFolder + "/teacher-assignments-template.xls";
        String teacherAssignments_filename = outputFolder + File.separator + outputNamePattern.replace("*", "teacher-assignments") + ".xls";
        PathInfo uu = PathInfo.create(teacherAssignments_filename);
        if (requestedContents.contains(GeneratedContent.TeacherAssignments)) {
            if (stats == null) {
                stats = academicPlugin.evalTeacherStatList(periodId, teacherIds, semester, options.getCourseFilter(), cache);
            }
            generateTeacherAssignmentsFolder(periodId, stats.toArray(new TeacherPeriodStat[stats.size()]), cache, fs.get(teacherAssignments_template), fs.get(uu.getDirName() + File.separator + uu.getNamePart() + "-details/" + outputNamePattern + ".xls"), writeVars);
        }
        if (requestedContents.contains(GeneratedContent.GroupedTeacherAssignments)) {
            if (stats == null) {
                stats = academicPlugin.evalTeacherStatList(periodId, teacherIds, semester, options.getCourseFilter(), cache);
            }
            generateTeacherAssignmentsFile(periodId, stats.toArray(new TeacherPeriodStat[stats.size()]), cache, fs.get(teacherAssignments_template), fs.get(teacherAssignments_filename)
            );
        }
        if (requestedContents.contains(GeneratedContent.CourseListLoads)) {
            String courseListLoads_template = templateFolder + "/course-list-loads-template.xls";
            VFile t = fs.get(courseListLoads_template);
            generateCourseListLoadsFile(periodId, t, fs.get(outputFolder + File.separator + outputNamePattern.replace("*", "course-list-loads") + ".xls"));

            courseListLoads_template = templateFolder + "/course-assignments-template.xls";
            t = fs.get(courseListLoads_template);
            generateCourseListAssignmentsFile(periodId, t, fs.get(outputFolder + File.separator + outputNamePattern.replace("*", "course-assignments") + ".xls"));
        }
        if (requestedContents.contains(GeneratedContent.Bundle)) {
            Chronometer c2 = new Chronometer();
            String zipFile = outputFolder + File.separator + outputNamePattern.replace("*", "bundle") + ".zip";
            log.log(Level.FINE, "creating bundle {0} from {1}", new Object[]{(zipFile), outputFolder});
            VZipUtils.zip(fs.get(zipFile), new VZipOptions()
                            .setSkipRoots(true)
                            .setTempFileSystem(new NativeVFS())
                            .setTempFile(true),
                    fs.get(outputFolder)
            );
            log.log(Level.FINE, "created bundle in {0}", c2.stop());
        }
        System.out.println("data generated in " + ch.stop());
    }

    private void generateTeacherAssignmentsFile(int periodId, TeacherPeriodStat[] stats, StatCache cache, VFile template, VFile output) throws IOException {
        if (stats.length == 0) {
            throw new IllegalArgumentException("No valid Teacher found");
        }
        try {
            Workbook workbook;
            InputStream in = null;
            try {
                try {
                    in = template.getInputStream();
                    workbook = Workbook.getWorkbook(in);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            } catch (BiffException ex) {
                throw new IOException(ex);
            }
            final VFile p = output.getParentFile();
            if (p != null) {
                p.mkdirs();
            }
            File ff = null;
            try {
                ff = File.createTempFile("tmp", "tmp." + output.getFileName().getShortExtension());
                WritableWorkbook copy = Workbook.createWorkbook(ff, workbook);

                int count0 = copy.getSheets().length;
                int count = count0;
                for (TeacherPeriodStat st : stats) {
                    copy.copySheet(0, AppContact.getName(st.getTeacher().getContact()), count);
                    WritableSheet sheet2 = copy.getSheet(count);
//                    sheet2.setName(st.getTeacher().getName());
                    ExcelTemplate.generateExcelSheet(sheet2, preparePrintableTeacherLoadProperties(periodId, st, cache));
                    count++;
                }
                while (count0 > 0) {
                    count0--;
                    copy.removeSheet(0);
                }
                copy.write();
                workbook.close();
                copy.close();

                VFS.createNativeFS().copyTo(ff.getPath(), output);
            } finally {
                if (ff != null) {
                    ff.delete();
                }
//                if (out != null) {
//                    out.close();
//                }
            }
        } catch (WriteException ex) {
            throw new IOException(ex);
        }
    }

    protected void generatePrintableTeacherLoadFile(int periodId, int teacherId, CourseFilter filter, VFile template, VFile output, boolean writeVars, StatCache cache) throws IOException {
        Map<String, Object> p = preparePrintableTeacherLoadProperties(periodId, teacherId, filter, cache);
        if (writeVars) {
            writeVars(p, output);
        }
        ExcelTemplate.generateExcel(template, output, p);
    }

    private void writeVars(Map<String, Object> p, VFile baseOutput) throws IOException {
        PathInfo uu = PathInfo.create(baseOutput.getPath());
        baseOutput.getFileSystem().get(uu.getDirName() + "/vars/").mkdirs();
        String varsFile = uu.getDirName() + "/vars/" + uu.getNamePart() + ".config";
        TreeSet<String> keys = new TreeSet<>(p.keySet());
        PrintStream out = null;
        try {
            out = new PrintStream(baseOutput.getFileSystem().getOutputStream(varsFile));
            for (String key : keys) {
                out.println(key + "=" + p.get(key));
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void generateTeacherListAssignmentsSummaryFile(int periodId, TeacherPeriodStat[] stats, StatCache cache, VFile template, VFile output, boolean writeVarFiles) throws IOException {
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        for (int i = 0; i < stats.length; i++) {
            TeacherPeriodStat stat = stats[i];
            Map<String, Object> p0 = preparePrintableTeacherLoadProperties(periodId, stat, cache);
            copyPrefixed(p0, p, "row(" + (i + 1) + ")");
        }
        if (writeVarFiles) {
            writeVars(p, output);
        }
        ExcelTemplate.generateExcel(template, output, p);
        log.log(Level.FINE, "generateTeacherListAssignmentsSummaryFile {0} teachers in {1}", new Object[]{stats.length, ch.stop()});
    }

    public void generateCourseListLoadsFile(int periodId, VFile template, VFile output) throws IOException {
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        NamedDoubles inc = new NamedDoubles();

        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        for (AcademicCoursePlan c : academicPlugin.findCoursePlans(periodId)) {

            String prefix;
//            prefix = "all.row";
//            prefix = prefix+"(" + (int)inc.inc(prefix) + ").";
//            fillCoursePlanProps(c, p, prefix);

            prefix = c.getCourseLevel().getAcademicClass().getProgram().getName() + "." + c.getCourseLevel().getName();
            prefix = prefix + "(" + (int) inc.inc(prefix) + ").";
            fillCoursePlanProps(c, p, prefix);
        }
        writeVars(p, output);
        ExcelTemplate.generateExcel(template, output, p);
        log.log(Level.FINE, "generateCourseListLoadsFile in {0}", new Object[]{ch.stop()});
    }

    public Map<String, Object> preparePrintableTeacherLoadProperties(int periodId, int teacher, CourseFilter filter, StatCache cache) throws IOException {
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        return preparePrintableTeacherLoadProperties(periodId, academicPlugin.evalTeacherStat(periodId, teacher, null, null, null, filter, cache), cache);
    }

    private Map<String, Object> preparePrintableTeacherLoadProperties(int periodId, TeacherPeriodStat stat, StatCache cache) throws IOException {
        AcademicTeacher t = stat.getTeacher();
        if (t == null) {
            throw new IllegalArgumentException("Teacher not found ");
        }
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher tal = stat.getTeacher();
        Map<String, Object> p = new HashMap<>();
        p.put("teacher.name", AppContact.getName(t.getContact()));
        p.put("teacher.firstName", t.getContact().getFirstName());
        p.put("teacher.lastName", t.getContact().getLastName());
        AcademicTeacherPeriod academicTeacherPeriod = academicPlugin.findAcademicTeacherPeriod(periodId, tal);
        AcademicTeacherDegree degree = academicTeacherPeriod.getDegree();
        p.put("teacher.degree", degree == null ? null : degree.getName());
        p.put("teacher.situation", academicTeacherPeriod.getSituation() == null ? null : academicTeacherPeriod.getSituation().getName());
        for (TeacherSemesterStat semester : stat.getSemesters()) {
            AcademicSemester sem = semester.getSemester();
            int moduleIndex = 1;
            String semesterPrefix = "sem(" + sem.getName() + ")";
            for (AcademicCourseAssignment m : academicPlugin.findCourseAssignments(periodId, t.getId(), sem.getName(), stat.getCourseFilter(), cache)) {
                if ((Math.abs(m.getValueC()) + Math.abs(m.getValuePM()) + Math.abs(m.getValueTD()) + Math.abs(m.getValueTP())) * Math.abs(m.getGroupCount() * m.getShareCount()) != 0) {
                    String modulePrefix = semesterPrefix + ".mod(" + moduleIndex + ")";
                    p.put(modulePrefix + ".name", m.getName());
                    p.put(modulePrefix + ".c", m.getValueC() != 0 ? dformat(m.getValueC()) : "");
                    p.put(modulePrefix + ".td", m.getValueTD() != 0 ? dformat(m.getValueTD()) : "");
                    p.put(modulePrefix + ".tp", m.getValueTP() != 0 ? dformat(m.getValueTP()) : "");
                    p.put(modulePrefix + ".pm", m.getValuePM() != 0 ? dformat(m.getValuePM()) : "");
                    p.put(modulePrefix + ".grp", iformat(m.getGroupCount()));
                    p.put(modulePrefix + ".sh", iformat(m.getShareCount()));
                    p.put(modulePrefix + ".grpsh", iformat(m.getGroupCount() * m.getShareCount()));
                    AcademicCoursePlan coursePlan = m.getCoursePlan();
                    p.put(modulePrefix + ".class", StringUtils.nonNull(coursePlan.getCourseLevel().getAcademicClass()));
                    p.put(modulePrefix + ".level", StringUtils.nonNull(coursePlan.getCourseLevel()));
                    p.put(modulePrefix + ".type", StringUtils.nonNull(m.getCourseType()));
                    p.put(modulePrefix + ".program", StringUtils.nonNull(coursePlan.getCourseLevel().getAcademicClass().getProgram()));
                    moduleIndex++;
                }
            }
            p.put(semesterPrefix + ".c", dformat(semester.getValue().getC()));
            p.put(semesterPrefix + ".td", dformat(semester.getValue().getTd()));
            p.put(semesterPrefix + ".tp", dformat(semester.getValue().getTp()));
            p.put(semesterPrefix + ".pm", dformat(semester.getValue().getPm()));
            p.put(semesterPrefix + ".tppm", dformat(semester.getValue().getTppm()));
            p.put(semesterPrefix + ".c.w", dformat(semester.getValueWeek().getC()));
            p.put(semesterPrefix + ".td.w", dformat(semester.getValueWeek().getTd()));
            p.put(semesterPrefix + ".tp.w", dformat(semester.getValueWeek().getTp()));
            p.put(semesterPrefix + ".pm.w", dformat(semester.getValueWeek().getPm()));
            p.put(semesterPrefix + ".tppm.w", dformat(semester.getValueWeek().getTppm()));
            p.put(semesterPrefix + ".load", dformat(semester.getValue().getEquiv()));
            p.put(semesterPrefix + ".load.w", dformat(semester.getValueWeek().getEquiv()));
            p.put(semesterPrefix + ".extra", dformat(semester.getExtraWeek().getEquiv()));
            p.put(semesterPrefix + ".extra.c", dformat(semester.getExtraWeek().getC()));
            p.put(semesterPrefix + ".extra.td", dformat(semester.getExtraWeek().getTd()));
            p.put(semesterPrefix + ".extra.tp", dformat(semester.getExtraWeek().getTp()));
            p.put(semesterPrefix + ".extra.tppm", dformat(semester.getExtraWeek().getTppm()));
            p.put(semesterPrefix + ".w", iformat(semester.getWeeks()));
            p.put(semesterPrefix + ".mw", iformat(semester.getMaxWeeks()));
        }
        p.put("tot.load", dformat(stat.getValue().getEquiv()));
        p.put("tot.load.w", dformat(stat.getValueWeek().getEquiv()));
        p.put("tot.w", dformat(stat.getWeeks()));
        p.put("tot.mw", dformat(stat.getMaxWeeks()));
        p.put("tot.extra", dformat(stat.getExtraWeek().getEquiv()));
        p.put("tot.extra.c", dformat(stat.getExtraWeek().getC()));
        p.put("tot.extra.td", dformat(stat.getExtraWeek().getTd()));
        p.put("tot.extra.tp", dformat(stat.getExtraWeek().getTp()));
        p.put("tot.extra.tppm", dformat(stat.getExtraWeek().getTppm()));
        p.put("du.c", dformat(stat.getDueWeek().getC()));
        p.put("du.td", dformat(stat.getDueWeek().getTd()));
        p.put("du.tp", dformat(stat.getDueWeek().getTp()));
        p.put("du.tppm", dformat(stat.getDueWeek().getTppm()));
        return p;
    }

    //    protected void generatePrintableTeacherLoadFolder(int yearId, Integer[] teacherIds, String semester, String template, String output) throws IOException {
//        TeacherStat[] stats = evalTeachersStat(yearId, teacherIds, semester);
//        generateTeacherAssignmentsFolder(stats, template, output);
//    }
    private void generateTeacherAssignmentsFolder(int periodId, TeacherPeriodStat[] stats, StatCache cache, VFile template, VFile output, boolean writeVars) throws IOException {
//        if (!output.endsWith(File.separator) && !output.endsWith("/")) {
//            output = output + File.separator;
//        }
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        String soutput = output.getPath();
        if (!soutput.contains("*")) {
            soutput = soutput + "*";
        }
        if (!soutput.toLowerCase().endsWith(".xls")) {
            soutput = soutput + ".xls";
        }
        for (TeacherPeriodStat st : stats) {
            String pp = soutput.replace("*", AppContact.getName(st.getTeacher().getContact()));
            VFile f2 = core.getFileSystem().get(pp);
            f2.getParentFile().mkdirs();
            Map<String, Object> p = preparePrintableTeacherLoadProperties(periodId, st, cache);
            if (writeVars) {
                writeVars(p, f2);
            }
            AppUser u = st.getTeacher().getUser();
            if (u != null) {
                VFile mydocs = core.getUserDocumentsFolder(u.getLogin());
                mydocs.get("Charges").mkdirs();
                ExcelTemplate.generateExcel(template, f2, p);
                f2.copyTo(mydocs.get("Charges").get(f2.getName()));
            }
        }
    }

    private void fillCoursePlanProps(AcademicCoursePlan c, Map<String, Object> p, String prefix) {
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        double wc = c.getWeeksC();
        if (wc == 0) {
            wc = 1;
        }
        double wtd = c.getWeeksTD();
        if (wtd == 0) {
            wtd = 1;
        }
        double wtp = c.getWeeksTP();
        if (wtp == 0) {
            wtp = 1;
        }
        double wpm = c.getWeeksPM();
        if (wpm == 0) {
            wpm = 1;
        }
        double wtppm = c.getWeeksTPPM();
        if (wtppm == 0) {
            wtppm = 1;
        }
        p.put(prefix + "name", c.getName());
        p.put(prefix + "name2", c.getName2());
        p.put(prefix + "sem.code", c.getCourseLevel().getSemester().getCode());
        p.put(prefix + "sem.name", c.getCourseLevel().getSemester().getName());
        p.put(prefix + "sem.name2", c.getCourseLevel().getSemester().getName2());
        p.put(prefix + "ue", c.getCourseGroup() == null ? "" : c.getCourseGroup().getName());
        p.put(prefix + "lvl", c.getCourseLevel().getName());
        p.put(prefix + "c.grp", c.getGroupCountC());
        p.put(prefix + "td.grp", c.getGroupCountTD());
        p.put(prefix + "tp.grp", c.getGroupCountTP());
        p.put(prefix + "pm.grp", c.getGroupCountPM());
        p.put(prefix + "tppm.grp", c.getGroupCountTPPM());
        p.put(prefix + "c", c.getValueC());
        p.put(prefix + "td", c.getValueTD());
        p.put(prefix + "tp", c.getValueTP());
        p.put(prefix + "pm", c.getValuePM());

        p.put(prefix + "w.c", wc);
        p.put(prefix + "w.td", wtd);
        p.put(prefix + "w.tp", wtp);
        p.put(prefix + "w.pm", wpm);
        p.put(prefix + "w.tppm", wtppm);

        p.put(prefix + "w1.c", c.getValueC() / wc);
        p.put(prefix + "w1.td", c.getValueTD() / wtd);
        p.put(prefix + "w1.tp", c.getValueTP() / wtp);
        p.put(prefix + "w1.pm", c.getValuePM() / wpm);
        p.put(prefix + "w1.tppm", c.getValueTPPM() / wtppm);

        p.put(prefix + "class.name", c.getCourseLevel().getAcademicClass().getName());
        p.put(prefix + "class.name2", c.getCourseLevel().getAcademicClass().getName2());
        p.put(prefix + "program.name", c.getCourseLevel().getAcademicClass().getProgram().getName());
        p.put(prefix + "class.name2", c.getCourseLevel().getAcademicClass().getProgram().getName2());
        p.put(prefix + "department.name", c.getCourseLevel().getAcademicClass().getProgram().getDepartment().getName());
        p.put(prefix + "department.name2", c.getCourseLevel().getAcademicClass().getProgram().getDepartment().getName2());
    }

    private Map<String, Object> copyPrefixed(Map<String, Object> p, Map<String, Object> p2, String prefix) {
        if (p2 == null) {
            p2 = new HashMap<String, Object>();
        }
        if (prefix == null || prefix.isEmpty()) {
            prefix = "";
        } else if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        for (Map.Entry<String, Object> entrySet : p.entrySet()) {
            p2.put(prefix + entrySet.getKey(), entrySet.getValue());
        }
        return p2;
    }

    public void generateCourseListAssignmentsFile(int periodId, VFile template, VFile output) throws IOException {
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        NamedDoubles inc = new NamedDoubles();
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        CorePlugin core = VrApp.getBean(CorePlugin.class);

        for (AcademicCourseAssignment c : academicPlugin.findCourseAssignments(periodId)) {

            String prefix;
//            prefix = "all.row";
//            prefix = prefix+"(" + (int)inc.inc(prefix) + ").";
//            fillCoursePlanProps(c, p, prefix);

            prefix = "row(" + (int) inc.inc("x") + ").";
            fillCourseAssignementPlanProps(c, p, prefix);
        }
        String year = core.findPeriodOrMain(periodId).getName();
        String version = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.version", null, "v01");
        p.put("version", version);
        p.put("year", year);
        writeVars(p, output);
        ExcelTemplate.generateExcel(template, output, p);
        log.log(Level.FINE, "generateCourseListLoadsFile in {0}", new Object[]{ch.stop()});
    }

    private void fillCourseAssignementPlanProps(AcademicCourseAssignment c, Map<String, Object> p, String prefix) {
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        AcademicCoursePlan coursePlan = c.getCoursePlan();
        AcademicClass cls = c.getSubClass();
        if (cls == null && coursePlan != null) {
            cls = coursePlan.getCourseLevel().getAcademicClass();
        }
        p.put(prefix + "name", c.getName());
        p.put(prefix + "name2", c.getName2());
        p.put(prefix + "c", c.getValueC());
        p.put(prefix + "td", c.getValueTD());
        p.put(prefix + "tp", c.getValueTP());
        p.put(prefix + "pm", c.getValuePM());
        if (coursePlan != null) {
            double wc = coursePlan.getWeeksC();
            if (wc == 0) {
                wc = 1;
            }
            double wtd = coursePlan.getWeeksTD();
            if (wtd == 0) {
                wtd = 1;
            }
            double wtp = coursePlan.getWeeksTP();
            if (wtp == 0) {
                wtp = 1;
            }
            double wpm = coursePlan.getWeeksPM();
            if (wpm == 0) {
                wpm = 1;
            }
            double wtppm = coursePlan.getWeeksTPPM();
            if (wtppm == 0) {
                wtppm = 1;
            }
            p.put(prefix + "type", StringUtils.nonNull(c.getCourseType() == null ? null : c.getCourseType().getName()));
            p.put(prefix + "discipline", coursePlan.getDiscipline());
            p.put(prefix + "roomConstraintsC", coursePlan.getRoomConstraintsC());
            p.put(prefix + "roomConstraintsTP", coursePlan.getRoomConstraintsTP());
            if (c.getValueC() != 0 && c.getValueTP() == 0) {
                p.put(prefix + "roomConstraints", coursePlan.getRoomConstraintsC());
            } else if (c.getValueC() == 0 && c.getValueTP() != 0) {
                p.put(prefix + "roomConstraints", coursePlan.getRoomConstraintsTP());
            } else {
                StringBuilder s = new StringBuilder();
                if (c.getValueC() != 0 && !StringUtils.isEmpty(coursePlan.getRoomConstraintsC())) {
                    s.append("C:").append(coursePlan.getRoomConstraintsC().trim());
                }
                if (c.getValueTP() != 0 && !StringUtils.isEmpty(coursePlan.getRoomConstraintsTP())) {
                    if (s.length() > 0) {
                        s.append(", ");
                    }
                    s.append("TP:").append(coursePlan.getRoomConstraintsTP().trim());
                }
                p.put(prefix + "roomConstraints", s.toString());
            }
            p.put(prefix + "sem.code", coursePlan.getCourseLevel().getSemester().getCode());
            p.put(prefix + "sem.name", coursePlan.getCourseLevel().getSemester().getName());
            p.put(prefix + "sem.name2", coursePlan.getCourseLevel().getSemester().getName2());
            p.put(prefix + "ue", coursePlan.getCourseGroup() == null ? "" : coursePlan.getCourseGroup().getName());
            p.put(prefix + "lvl", coursePlan.getCourseLevel().getName());
            p.put(prefix + "grp", c.getGroupCount());
            p.put(prefix + "share", c.getShareCount());
            p.put(prefix + "c.grp", coursePlan.getGroupCountC());
            p.put(prefix + "td.grp", coursePlan.getGroupCountTD());
            p.put(prefix + "tp.grp", coursePlan.getGroupCountTP());
            p.put(prefix + "pm.grp", coursePlan.getGroupCountPM());
            p.put(prefix + "tppm.grp", coursePlan.getGroupCountTPPM());

            p.put(prefix + "w.c", wc);
            p.put(prefix + "w.td", wtd);
            p.put(prefix + "w.tp", wtp);
            p.put(prefix + "w.pm", wpm);
            p.put(prefix + "w.tppm", wtppm);

            p.put(prefix + "w1.c", c.getValueC() / wc);
            p.put(prefix + "w1.td", c.getValueTD() / wtd);
            p.put(prefix + "w1.tp", c.getValueTP() / wtp);
            p.put(prefix + "w1.pm", c.getValuePM() / wpm);
            p.put(prefix + "w1.tppm", coursePlan.getValueTPPM() / wtppm);
            if (cls != null) {
                p.put(prefix + "class.name", cls.getName());
                p.put(prefix + "class.name2", cls.getName2());
                p.put(prefix + "program.name", cls.getProgram().getName());
                p.put(prefix + "program.name2", cls.getProgram().getName2());
                p.put(prefix + "department.code", cls.getProgram().getDepartment().getCode());
                p.put(prefix + "department.name", cls.getProgram().getDepartment().getName());
                p.put(prefix + "department.name2", cls.getProgram().getDepartment().getName2());
            }
        }
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        p.put(prefix + "teacher.name", c.getTeacher() == null ? null : academicPlugin.getValidName(c.getTeacher()));
        p.put(prefix + "teacher.name2", (c.getTeacher() == null || c.getTeacher().getContact() == null) ? null : c.getTeacher().getContact().getFullName2());
        p.put(prefix + "teacher.discipline", c.getTeacher() == null ? null : c.getTeacher().getDiscipline());
    }

    public void generateTeachingLoad(int periodId, CourseFilter courseFilter, String version0) throws IOException {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        try {
        AppPeriod mainPeriod = core.findPeriodOrMain(periodId);
        String year = mainPeriod.getName();
        String version = (!StringUtils.isEmpty(version0)) ? version0 : ((String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.version", null, "v01"));
        String dir = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.configFolder", null, "/Config/Import/import/${year}");
        String namePattern = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.namePattern", null, "*-eniso-ii-${year}-${version}");
        Map<String, String> vars = new HashMap<>();
        vars.put("home", System.getProperty("user.home"));
        vars.put("year", year);
        vars.put("version", version);

        dir = StringUtils.replaceDollarPlaceHolders(dir, new MapStringConverter(vars));
        ///Output/${year}/${version}
        String outdir = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.outputFolder", null, "/Documents/ByProfile/DirectorOfStudies/Charges/${year}/${version}");

        outdir = StringUtils.replaceDollarPlaceHolders(outdir, new MapStringConverter(vars));

//            String dataFolder = dir + "/data";
        String templatesFolder = dir + "/templates";

        namePattern = StringUtils.replaceDollarPlaceHolders(namePattern, new MapStringConverter(vars));

        net.vpc.common.vfs.VirtualFileSystem fs = core.getFileSystem();
        fs.get(outdir).mkdirs();
        //TODO should export from DB all this information
//            VFS.copy(fs.get(dataFolder), fs.get(outdir), new VFileFilter() {
//
//                @Override
//                public boolean accept(VFile pathname) {
//                    return pathname.isDirectory() || (pathname.isFile()
//                            && (pathname.getName().toLowerCase().endsWith(".xls")
//                            || pathname.getName().toLowerCase().endsWith(".xlsx")));
//                }
//            });
        generate(
                new TeacherGenerationOptions()
                        //                    .setTeacherIds(new Integer[]{s.findTeacher(StringComparators.ilike("*zarrouk*")).getId()})
                        //                    .setSemester(null)
                        .setContents(
                                //                            GeneratedContent.CourseListLoads,
                                GeneratedContent.GroupedTeacherAssignments,
                                GeneratedContent.TeacherAssignments,
                                GeneratedContent.CourseListLoads,
                                GeneratedContent.TeacherListAssignmentsSummary,
                                GeneratedContent.Bundle
                        )
                        .setTemplateFolder(templatesFolder)
                        .setOutputFolder(outdir)
                        .setOutputNamePattern(namePattern)
                        .setCourseFilter(courseFilter)
                        .setPeriod(mainPeriod)
        );
//            XMailService mails = new XMailService();
//            XMail m=mails.read(XMailFormat.TEXT, new File(dir+"/notification-charge.xmail"));
//            m.setSimulate(true);
//            mails.send(m);
//            for () {
//                break;
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(XlsxLoadImporter.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }


}
