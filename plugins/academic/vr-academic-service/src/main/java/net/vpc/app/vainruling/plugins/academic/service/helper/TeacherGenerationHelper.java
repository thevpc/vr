package net.vpc.app.vainruling.plugins.academic.service.helper;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.util.ExcelTemplate;
import net.vpc.app.vainruling.core.service.util.NamedDoubles;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.academic.service.*;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.stat.DeviationConfig;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherPeriodStat;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherSemesterStat;
import net.vpc.app.vainruling.plugins.academic.service.util.CourseAssignmentFilterAnd;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.util.TeacherFilterFactory;
import net.vpc.common.io.PathInfo;
import net.vpc.common.strings.MapStringConverter;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import net.vpc.common.util.MapList;
import net.vpc.common.util.mon.EnhancedProgressMonitor;
import net.vpc.common.util.mon.ProgressMonitor;
import net.vpc.common.util.mon.ProgressMonitorFactory;
import net.vpc.common.util.mon.VoidMonitoredAction;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.impl.NativeVFS;
import net.vpc.common.vfs.impl.VZipOptions;
import net.vpc.common.vfs.impl.VZipUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.bulk.DataColumn;
import net.vpc.upa.bulk.DataRowConverter;
import net.vpc.upa.bulk.DataWriter;
import net.vpc.upa.bulk.TextCSVFormatter;
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

    public void generate(TeacherGenerationOptions _options) throws IOException {
        Chronometer ch = new Chronometer();
        TeacherGenerationOptions options=(_options == null) ? new TeacherGenerationOptions():_options;

        EnhancedProgressMonitor[] mon = ProgressMonitorFactory.split(options.getProgressMonitor(), new double[]{
            2,
            2,
            5,
            2,
            1,
            1,
            1,
        });
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);

        Integer[] teacherIds = options.getTeacherIds();
        String semester = options.getSemester();
        Integer semesterId = StringUtils.isEmpty(semester) ? null : academicPlugin.findSemester(semester).getId();
        String templateFolder = options.getTemplateFolder();
        String outputFolder = options.getOutputFolder();
        net.vpc.common.vfs.VirtualFileSystem fs = core.getFileSystem();
        fs.get(outputFolder).mkdirs();

        String _outputNamePattern = options.getOutputNamePattern();
        AppPeriod period = options.getPeriod();
        if (period == null) {
            AppConfig appConfig = core.getCurrentConfig();
            period = appConfig == null ? null : appConfig.getMainPeriod();
        }
        if (period == null) {
            throw new IllegalArgumentException("Invalid period");
        }
        int periodId = period.getId();
        if (!_outputNamePattern.contains("*")) {
            _outputNamePattern = "*-" + _outputNamePattern;
        }
        //remove extensions!, remove leading path, retain just name part
        {
            PathInfo uu = PathInfo.create(_outputNamePattern);
            _outputNamePattern = uu.getNamePart();
        }
        String outputNamePattern = _outputNamePattern;
        class Data {
            List<TeacherPeriodStat> stats = null;
        }
        Data data = new Data();
        Set<GeneratedContent> requestedContents = options.getContents() == null ? new HashSet() : new HashSet<>(Arrays.asList(options.getContents()));

        if (requestedContents.isEmpty()) {
            requestedContents.addAll(Arrays.asList(GeneratedContent.values()));
        }
        boolean writeVars = requestedContents.contains(GeneratedContent.Vars);
        TeacherFilter teacherIdsFilter = TeacherFilterFactory.teacherIds(teacherIds);
        String teacherAssignments_template = templateFolder + "/teacher-assignments-template.xls";
        String teacherAssignments_filename = outputFolder + File.separator + outputNamePattern.replace("*", "teacher-assignments") + ".xls";
        PathInfo uu = PathInfo.create(teacherAssignments_filename);

        ProgressMonitorFactory.invokeMonitoredAction(mon[0],
                "TeacherListAssignmentsSummary ",
                new VoidMonitoredAction() {
                    @Override
                    public void invoke(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
                        if (requestedContents.contains(GeneratedContent.TeacherListAssignmentsSummary) || requestedContents.contains(GeneratedContent.TeacherAssignments) || requestedContents.contains(GeneratedContent.GroupedTeacherAssignments)) {
                            data.stats = academicPlugin.evalTeacherStatList(periodId, teacherIdsFilter, new CourseAssignmentFilterAnd().and(options.getCourseAssignmentFilter()).and(new DefaultCourseAssignmentFilter().addAcceptedSemester(semesterId)), options.getDeviationConfig(),monitor);
                        }
                    }
                }
        );
        ProgressMonitorFactory.invokeMonitoredAction(mon[1],
                "TeacherListAssignmentsSummary ",
                new VoidMonitoredAction() {
                    @Override
                    public void invoke(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
                        if (requestedContents.contains(GeneratedContent.TeacherListAssignmentsSummary)) {
                            TeacherPeriodStat[] statsArray = data.stats.toArray(new TeacherPeriodStat[data.stats.size()]);
                            String teacherListAssignmentsSummaryFile_template = templateFolder + "/teacher-list-assignments-summary-template.xls";
                            EnhancedProgressMonitor[] mons2 = monitor.split(new double[]{2, 1});
                            generateTeacherListAssignmentsSummaryFile(periodId, statsArray,
                                    fs.get(teacherListAssignmentsSummaryFile_template), fs.get(outputFolder + File.separator
                                            + outputNamePattern.replace("*", "teacher-list-assignments-summary") + ".xls"), writeVars,mons2[0]);

                            VFile currGenFile = fs.get(outputFolder + File.separator + "vars" + File.separator + "teacher-list-assignments.csv");
                            VFile diffGenFile = fs.get(outputFolder + File.separator + "vars" + File.separator + "teacher-list-assignments-diff.html");
                            VFile oldGenFile = StringUtils.isEmpty(options.getOldOutputFolder()) ? null : fs.get(options.getOldOutputFolder() + File.separator + "vars" + File.separator + "teacher-list-assignments.csv");
                            generateTeacherAssignmentsCsvFile(periodId, statsArray, currGenFile,mons2[1]);
                            if (oldGenFile != null && oldGenFile.exists()) {
                                VrUtils.diffToHtml(currGenFile, oldGenFile, diffGenFile, null);
                            }
                        }
                    }
                });


        ProgressMonitorFactory.invokeMonitoredAction(mon[2],
                "TeacherListAssignmentsSummary ",
                new VoidMonitoredAction() {
                    @Override
                    public void invoke(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
                        if (requestedContents.contains(GeneratedContent.TeacherAssignments)) {
                            TeacherPeriodStat[] statsArray = data.stats.toArray(new TeacherPeriodStat[data.stats.size()]);
                            generateTeacherAssignmentsFolder(periodId, statsArray, fs.get(teacherAssignments_template), fs.get(uu.getDirName()
                                    + File.separator + uu.getNamePart() + "-details/" + outputNamePattern + ".xls"), writeVars,monitor);
                        }
                    }
                });

        ProgressMonitorFactory.invokeMonitoredAction(mon[3],
                "TeacherListAssignmentsSummary ",
                new VoidMonitoredAction() {
                    @Override
                    public void invoke(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
                        if (requestedContents.contains(GeneratedContent.GroupedTeacherAssignments)) {
                            TeacherPeriodStat[] statsArray = data.stats.toArray(new TeacherPeriodStat[data.stats.size()]);
                            generateTeacherAssignmentsFile(periodId, statsArray, fs.get(teacherAssignments_template), fs.get(teacherAssignments_filename),monitor);
                        }
                    }
                });

        ProgressMonitorFactory.invokeMonitoredAction(mon[4],
                "TeacherListAssignmentsSummary ",
                new VoidMonitoredAction() {
                    @Override
                    public void invoke(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
                        if (requestedContents.contains(GeneratedContent.CourseListLoads)) {
                            String courseListLoads_template = templateFolder + "/course-list-loads-template.xls";
                            VFile t = fs.get(courseListLoads_template);
                            EnhancedProgressMonitor[] mons2 = monitor.split(new double[]{1, 1});
                            generateCourseListLoadsFile(periodId, t, fs.get(outputFolder + File.separator + outputNamePattern.replace("*", "course-list-loads") + ".xls"),mons2[0]);

                            courseListLoads_template = templateFolder + "/course-assignments-template.xls";
                            t = fs.get(courseListLoads_template);
                            generateCourseListAssignmentsFile(periodId, t, fs.get(outputFolder + File.separator + outputNamePattern.replace("*", "course-assignments") + ".xls"), options.getVersion(),mons2[1]);
                        }
                    }
                });

        ProgressMonitorFactory.invokeMonitoredAction(mon[5],
                "TeacherListAssignmentsSummary ",
                new VoidMonitoredAction() {
                    @Override
                    public void invoke(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
                        generateGlobalVars(periodId, fs.get(outputFolder + File.separator + "vars" + File.separator + "vars.config"), options.getVersion());
                    }
                });

        ProgressMonitorFactory.invokeMonitoredAction(mon[6],
                "TeacherListAssignmentsSummary ",
                new VoidMonitoredAction() {
                    @Override
                    public void invoke(EnhancedProgressMonitor monitor, String messagePrefix) throws Exception {
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
                    }
                });

        System.out.println("data generated in " + ch.stop());
    }

    private void generateTeacherAssignmentsFile(int periodId, TeacherPeriodStat[] stats, VFile template, VFile output,ProgressMonitor mon) throws IOException {
        EnhancedProgressMonitor monitor=ProgressMonitorFactory.enhance(mon);
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
                    monitor.setProgress(count,stats.length);
                    copy.copySheet(0, AppContact.getName(st.getTeacher().getContact()), count);
                    WritableSheet sheet2 = copy.getSheet(count);
//                    sheet2.setName(st.getTeacher().getName());
                    ExcelTemplate.generateExcelSheet(sheet2, preparePrintableTeacherLoadProperties(periodId, st));
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

    private void generateTeacherAssignmentsCsvFile(int periodId, TeacherPeriodStat[] stats, VFile output,ProgressMonitor mon) throws IOException {
        EnhancedProgressMonitor monitor=ProgressMonitorFactory.enhance(mon);
        if (stats.length == 0) {
            throw new IllegalArgumentException("No valid Teacher found");
        }
        try {
            List<AcademicCourseAssignmentInfo> assignments = new ArrayList<>();
            for (TeacherPeriodStat st : stats) {
                assignments.addAll(st.getAssignments());
            }
            Collections.sort(assignments, new Comparator<AcademicCourseAssignmentInfo>() {
                @Override
                public int compare(AcademicCourseAssignmentInfo o1, AcademicCourseAssignmentInfo o2) {
                    return Integer.compare(o1.getAssignment().getId(), o2.getAssignment().getId());
                }
            });
            final VFile p = output.getParentFile();
            if (p != null) {
                p.mkdirs();
            }
            File ff = null;
            try {
                ff = File.createTempFile("tmp", "tmp." + output.getFileName().getShortExtension());
                TextCSVFormatter csv = UPA.getPersistenceUnit().getImportExportManager().createTextCSVFormatter(ff);
                csv.setWriteHeader(true);
                csv.setWriteHeader(true);
                csv.setDataRowConverter(new DataRowConverter() {
                    @Override
                    public DataColumn[] getColumns() {
                        return new DataColumn[]{
                                new DataColumn(0, "Id"),
                                new DataColumn(0, "Assignment"),
                                new DataColumn(0, "Teacher"),
                                new DataColumn(0, "Groups"),
                                new DataColumn(0, "Shares"),
                        };
                    }

                    @Override
                    public Object[] objectToRow(Object o) {
                        AcademicCourseAssignmentInfo a = (AcademicCourseAssignmentInfo) o;
                        return new Object[]{
                                a.getAssignment().getId(),
                                a.getAssignment().getFullName(),
                                a.getAssignment().getTeacher() == null ? null : a.resolveContact().getFullName(),
                                a.getAssignment().getGroupCount(),
                                a.getAssignment().getShareCount()
                        };
                    }
                });
                DataWriter writer = csv.createWriter();
                int i = 0;
                int size = assignments.size();
                for (AcademicCourseAssignmentInfo assignment : assignments) {
                    monitor.setProgress(i++, size);
                    writer.writeObject(assignment);
                }
                writer.close();
                csv.close();
                VFS.createNativeFS().copyTo(ff.getPath(), output);
            } finally {
                if (ff != null) {
                    ff.delete();
                }
//                if (out != null) {
//                    out.close();
//                }
            }
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    protected void generatePrintableTeacherLoadFile(int periodId, int teacherId, CourseAssignmentFilter filter, DeviationConfig deviationConfig, VFile template, VFile output, boolean writeVars) throws IOException {
        Map<String, Object> p = preparePrintableTeacherLoadProperties(periodId, teacherId, filter, deviationConfig);
        if (writeVars) {
            writeVars(p, output);
        }
        ExcelTemplate.generateExcel(template, output, p);
    }

    private void writeVars(Map<String, Object> p, VFile baseOutput) throws IOException {
        PathInfo uu = PathInfo.create(baseOutput.getPath());
        String varsFile = uu.getDirName() + "/vars/" + uu.getNamePart() + ".config";
        writeVars0(p, baseOutput.getFileSystem().get(varsFile));
    }

    private void writeVars0(Map<String, Object> p, VFile varsFile) throws IOException {
        TreeSet<String> keys = new TreeSet<>(p.keySet());
        PrintStream out = null;
        try {
            VFile parentFile = varsFile.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            out = new PrintStream(varsFile.getOutputStream());
            for (String key : keys) {
                out.println(key + "=" + p.get(key));
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void generateTeacherListAssignmentsSummaryFile(int periodId, TeacherPeriodStat[] stats, VFile template, VFile output, boolean writeVarFiles,ProgressMonitor mon) throws IOException {
        EnhancedProgressMonitor monitor=ProgressMonitorFactory.enhance(mon);
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        for (int i = 0; i < stats.length; i++) {
            monitor.setProgress(i,stats.length);
            TeacherPeriodStat stat = stats[i];
            Map<String, Object> p0 = preparePrintableTeacherLoadProperties(periodId, stat);
            copyPrefixed(p0, p, "row(" + (i + 1) + ")");
        }
        if (writeVarFiles) {
            writeVars(p, output);
        }
        ExcelTemplate.generateExcel(template, output, p);
        log.log(Level.FINE, "generateTeacherListAssignmentsSummaryFile {0} teachers in {1}", new Object[]{stats.length, ch.stop()});
    }

    public void generateCourseListLoadsFile(int periodId, VFile template, VFile output,ProgressMonitor mon) throws IOException {
        EnhancedProgressMonitor monitor=ProgressMonitorFactory.enhance(mon);
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        NamedDoubles inc = new NamedDoubles();

        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        List<AcademicCoursePlan> coursePlans = academicPlugin.findCoursePlans(periodId);
        monitor=monitor.createIncrementalMonitor(coursePlans.size());
        for (AcademicCoursePlan c : coursePlans) {
            monitor.inc();
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

    public Map<String, Object> preparePrintableTeacherLoadProperties(int periodId, int teacher, CourseAssignmentFilter filter, DeviationConfig deviationConfig) throws IOException {
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        return preparePrintableTeacherLoadProperties(periodId, academicPlugin.evalTeacherStat0(periodId, teacher, null, null, null, filter, deviationConfig));
    }

    private Map<String, Object> preparePrintableTeacherLoadProperties(int periodId, TeacherPeriodStat stat) throws IOException {
        AcademicTeacher t = stat.getTeacher();
        if (t == null) {
            throw new IllegalArgumentException("Teacher not found ");
        }
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher tal = stat.getTeacher();
        Map<String, Object> p = new HashMap<>();
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AppPeriod period = core.findPeriod(periodId);
        if (period != null) {
            p.put("period.name", period.getName());
        }
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
            for (AcademicCourseAssignment m : academicPlugin.findCourseAssignments(periodId, t.getId(), new CourseAssignmentFilterAnd().and(stat.getCourseAssignmentFilter()).and(
                    new DefaultCourseAssignmentFilter().addAcceptedSemester(sem.getId())
            ))) {
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
            p.put(semesterPrefix + ".extra.eff.c", dformat(semester.getExtraWeekEff().getC()));
            p.put(semesterPrefix + ".extra.eff.td", dformat(semester.getExtraWeekEff().getTd()));
            p.put(semesterPrefix + ".extra.eff.tp", dformat(semester.getExtraWeekEff().getTp()));
            p.put(semesterPrefix + ".extra.eff.pm", dformat(semester.getExtraWeekEff().getPm()));
            p.put(semesterPrefix + ".extra.eff.tppm", dformat(semester.getExtraWeekEff().getTppm()));
            p.put(semesterPrefix + ".extra.eff.tdtppm", dformat(semester.getExtraWeekEff().getTdtppm()));
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
        p.put("tot.extra.eff.c", dformat(stat.getExtraWeekEff().getC()));
        p.put("tot.extra.eff.td", dformat(stat.getExtraWeekEff().getTd()));
        p.put("tot.extra.eff.tp", dformat(stat.getExtraWeekEff().getTp()));
        p.put("tot.extra.eff.pm", dformat(stat.getExtraWeekEff().getPm()));
        p.put("tot.extra.eff.tppm", dformat(stat.getExtraWeekEff().getTppm()));
        p.put("tot.extra.eff.tdtppm", dformat(stat.getExtraWeekEff().getTdtppm()));
        p.put("du.c", dformat(stat.getDueWeek().getC()));
        p.put("du.td", dformat(stat.getDueWeek().getTd()));
        p.put("du.tp", dformat(stat.getDueWeek().getTp()));
        p.put("du.pm", dformat(stat.getDueWeek().getPm()));
        p.put("du.tppm", dformat(stat.getDueWeek().getTppm()));
        p.put("du.tdtppm", dformat(stat.getDueWeek().getTdtppm()));
        AcademicTeacher headOfDepartment = null;
        AppDepartment department = null;

        if (stat.getTeacher().getDepartment() != null) {
            department = stat.getTeacher().getDepartment();
            headOfDepartment = academicPlugin.findHeadOfDepartment(stat.getTeacher().getDepartment().getId());
        }
        if (department != null) {
            p.put("department.code", department.getCode());
            p.put("department.name", department.getName());
            p.put("department.name2", department.getName2());
            p.put("department.name3", department.getName3());
        }
        if (headOfDepartment != null) {
            p.put("head-of-department.name", AppContact.getName(headOfDepartment.getContact()));
            p.put("head-of-department.firstName", headOfDepartment.getContact().getFirstName());
            p.put("head-of-department.lastName", headOfDepartment.getContact().getLastName());
        } else {
            p.put("head-of-department.name", "Unknown");
            p.put("head-of-department.firstName", "Unknown");
            p.put("head-of-department.lastName", "Unknown");
        }

        return p;
    }

    //    protected void generatePrintableTeacherLoadFolder(int yearId, Integer[] teacherIds, String semester, String template, String output) throws IOException {
//        TeacherStat[] stats = evalTeachersStat(yearId, teacherIds, semester);
//        generateTeacherAssignmentsFolder(stats, template, output);
//    }
    private void generateTeacherAssignmentsFolder(int periodId, TeacherPeriodStat[] stats, VFile template, VFile output, boolean writeVars,ProgressMonitor mon) throws IOException {
//        if (!output.endsWith(File.separator) && !output.endsWith("/")) {
//            output = output + File.separator;
//        }
        EnhancedProgressMonitor monitor=ProgressMonitorFactory.enhance(mon);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        String soutput = output.getPath();
        if (!soutput.contains("*")) {
            soutput = soutput + "*";
        }
        if (!soutput.toLowerCase().endsWith(".xls")) {
            soutput = soutput + ".xls";
        }
        int i=0;
        int max=stats.length;
        for (TeacherPeriodStat st : stats) {
            monitor.setProgress(i++,max);
            String pp = soutput.replace("*", AppContact.getName(st.getTeacher().getContact()));
            VFile f2 = core.getFileSystem().get(pp);
            f2.getParentFile().mkdirs();
            Map<String, Object> p = preparePrintableTeacherLoadProperties(periodId, st);
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
        p.put(prefix + "sem.code", c.resolveSemester().getCode());
        p.put(prefix + "sem.name", c.resolveSemester().getName());
        p.put(prefix + "sem.name2", c.resolveSemester().getName2());
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

        p.put(prefix + "class.name", c.resolveAcademicClass().getName());
        p.put(prefix + "class.name2", c.resolveAcademicClass().getName2());
        p.put(prefix + "program.name", c.resolveProgram().getName());
        p.put(prefix + "class.name2", c.resolveProgram().getName2());
        p.put(prefix + "department.name", c.resolveDepartment().getName());
        p.put(prefix + "department.name2", c.resolveDepartment().getName2());
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

    public void generateCourseListAssignmentsFile(int periodId, VFile template, VFile output, String version,ProgressMonitor mon) throws IOException {
        EnhancedProgressMonitor monitor=ProgressMonitorFactory.enhance(mon);
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        NamedDoubles inc = new NamedDoubles();
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        MapList<Integer, AcademicCourseAssignment> courseAssignments = academicPlugin.findCourseAssignments(periodId);
        EnhancedProgressMonitor incmon = monitor.createIncrementalMonitor(courseAssignments.size());
        for (AcademicCourseAssignment c : courseAssignments) {
            incmon.inc();
            String prefix;
//            prefix = "all.row";
//            prefix = prefix+"(" + (int)inc.inc(prefix) + ").";
//            fillCoursePlanProps(c, p, prefix);

            prefix = "row(" + (int) inc.inc("x") + ").";
            fillCourseAssignementPlanProps(c, p, prefix);
        }
        String year = core.findPeriodOrMain(periodId).getName();
//        String version = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.version", null, "v01");
        p.put("version", version);
        p.put("period", year);
        writeVars(p, output);
        ExcelTemplate.generateExcel(template, output, p);
        log.log(Level.FINE, "generateCourseListLoadsFile in {0}", new Object[]{ch.stop()});
    }

    public void generateGlobalVars(int periodId, VFile output, String version) throws IOException {
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        CorePlugin core = CorePlugin.get();
        String year = core.findPeriodOrMain(periodId).getName();
        p.put("version", version);
        p.put("period", year);
        writeVars0(p, output);
        log.log(Level.FINE, "generateGlobalVars in {0}", new Object[]{ch.stop()});
    }

    private void fillCourseAssignementPlanProps(AcademicCourseAssignment c, Map<String, Object> p, String prefix) {
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        AcademicCoursePlan coursePlan = c.getCoursePlan();
        AcademicClass cls = c.resolveAcademicClass();
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
                p.put(prefix + "department.code", cls.resolveDepartment().getCode());
                p.put(prefix + "department.name", cls.resolveDepartment().getName());
                p.put(prefix + "department.name2", cls.resolveDepartment().getName2());
            }
        }
        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        p.put(prefix + "teacher.name", c.getTeacher() == null ? null : academicPlugin.getValidName(c.getTeacher()));
        p.put(prefix + "teacher.name2", (c.getTeacher() == null || c.getTeacher().getContact() == null) ? null : c.getTeacher().getContact().getFullName2());
        p.put(prefix + "teacher.discipline", c.getTeacher() == null ? null : c.getTeacher().getDiscipline());
    }

    public void generateTeachingLoad(int periodId, CourseAssignmentFilter courseAssignmentFilter, String version, String oldRefVersion,ProgressMonitor monitor) throws IOException {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        try {
        AppPeriod mainPeriod = core.findPeriodOrMain(periodId);
//        String year = mainPeriod.getName();
//        String version = (!StringUtils.isEmpty(version0)) ? version0 : ((String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.version", null, "v01"));
//        String dir = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.configFolder", null, "/Config/Import/import/${year}");
//        String namePattern = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.namePattern", null, "*-eniso-ii-${year}-${version}");
//        Map<String, String> vars = new HashMap<>();
//        vars.put("home", System.getProperty("user.home"));
//        vars.put("year", year);
//        vars.put("version", version);
//
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
                        .setVersion(getVersion(periodId, version))
                        .setTemplateFolder(getConfigFolder(periodId, version) + "/templates")
                        .setOutputFolder(getOutputFolder(periodId, version))
                        .setOldOutputFolder(getOutputFolder(periodId, oldRefVersion))
                        .setOutputNamePattern(getNamePattern(periodId, version))
                        .setCourseAssignmentFilter(courseAssignmentFilter)
                        .setPeriod(mainPeriod)
                        .setProgressMonitor(monitor)
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

    public String getVersion(int periodId, String version0) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AppPeriod mainPeriod = core.findPeriodOrMain(periodId);
        String year = mainPeriod.getName();
        return (!StringUtils.isEmpty(version0)) ? version0 : ((String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.version", null, "v01"));
    }

    public String getNamePattern(int periodId, String version0) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AppPeriod mainPeriod = core.findPeriodOrMain(periodId);
        String year = mainPeriod.getName();
        String version = getVersion(periodId, version0);
        String namePattern = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.namePattern", null, "*-eniso-ii-${year}-${version}");
        Map<String, String> vars = new HashMap<>();
        vars.put("home", System.getProperty("user.home"));
        vars.put("year", year);
        vars.put("version", version);
        return StringUtils.replaceDollarPlaceHolders(namePattern, new MapStringConverter(vars));
    }

    public String getConfigFolder(int periodId, String version) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        try {
        AppPeriod mainPeriod = core.findPeriodOrMain(periodId);
        String year = mainPeriod.getName();
        String goodVersion = getVersion(periodId, version);
        String dir = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.configFolder", null, "/Config/Import/import/${year}");
        Map<String, String> vars = new HashMap<>();
        vars.put("home", System.getProperty("user.home"));
        vars.put("year", year);
        vars.put("version", goodVersion);

        return StringUtils.replaceDollarPlaceHolders(dir, new MapStringConverter(vars));
    }

    public String getOutputFolder(int periodId, String version0) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        try {
        AppPeriod mainPeriod = core.findPeriodOrMain(periodId);
        String year = mainPeriod.getName();
        String version = (!StringUtils.isEmpty(version0)) ? version0 : ((String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.version", null, "v01"));
        String dir = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.configFolder", null, "/Config/Import/import/${year}");
        Map<String, String> vars = new HashMap<>();
        vars.put("home", System.getProperty("user.home"));
        vars.put("year", year);
        vars.put("version", version);
        String path = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.outputFolder", null, "/Documents/ByProfile/DirectorOfStudies/Charges/${year}/${version}");
        return StringUtils.replaceDollarPlaceHolders(path, new MapStringConverter(vars));
    }

}
