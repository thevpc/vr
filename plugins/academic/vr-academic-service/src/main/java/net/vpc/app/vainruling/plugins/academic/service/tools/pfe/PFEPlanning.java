package net.vpc.app.vainruling.plugins.academic.service.tools.pfe;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.*;
import net.vpc.common.io.FileUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import net.vpc.upa.UPA;
import net.vpc.upa.bulk.DataReader;
import net.vpc.upa.bulk.DataRow;
import net.vpc.upa.bulk.ImportExportManager;
import net.vpc.upa.bulk.SheetParser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * this is not a unit test
 */
public class PFEPlanning {

//    public static void main(String[] args) {
//        File file = new File(System.getProperty("user.home") + "/myplanning.xlsx");
//
//        try {
//            generate(100, 5, file);
//            run(new PlanningService(), file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        VrApp.stopStandalone();
//    }
    public static void main(String[] args) {
//        VrApp.runStandaloneNoLog();
        PFEPlanning p = new PFEPlanning();
        try {
            File file = new File("/data/vpc/Data/eniso/pfe-stages-projets/2017-2018/PFE-2017-2018-Session02-v1.4-input.xlsx");
            PlanningActivityTable t = p.loadPlanningXlsFile(file);
            System.out.println("Loaded " + t.getActivities().size() + " Activities");
            ResourceAllocationList resourceAllocationList = p.generateResourceAllocationList(t);
            ResourceAllocationListWriter w = new ResourceAllocationListWriter(resourceAllocationList,
                    "PFE Info Appliquee 2017-2018, Session (2) - Sept 2018",
                    "1.4"
            );
            String name = file.getPath();
            for (String suffix : new String[]{".xlsx", ".xls", ".input", "-input"}) {
                if (name.toLowerCase().endsWith(suffix.toLowerCase())) {
                    name = name.substring(0, name.length() - suffix.length());
                }
            }
            w.writeTeachersHtml(new File(name + ".teachers.html"));
            w.writeStudentsHtml(new File(name + ".students.html"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResourceAllocationList generateResourceAllocationList(PlanningActivityTable table) {
        ResourceAllocationList resourceAllocationsTable = new ResourceAllocationList(table);
        for (PlanningActivity planningActivity : table.getActivities()) {
            fillAllocations(planningActivity, resourceAllocationsTable);
        }

        for (PlanningActivity planningActivity : table.getActivities()) {

            if (!StringUtils.isEmpty(planningActivity.getExaminer())) {
                TeacherInfo teacherInfo = resourceAllocationsTable.getTeacherInfo(planningActivity.getExaminer());
                teacherInfo.getCounter("Examiner").inc();
                teacherInfo.addSpaceTime(planningActivity.getSpaceTime());
                teacherInfo.getActivities().add(planningActivity);
            }
            if (!StringUtils.isEmpty(planningActivity.getChair())) {
                TeacherInfo teacherInfo = resourceAllocationsTable.getTeacherInfo(planningActivity.getChair());
                teacherInfo.getCounter("Chair").inc();
                teacherInfo.addSpaceTime(planningActivity.getSpaceTime());
                teacherInfo.getActivities().add(planningActivity);
            }
            for (String s : planningActivity.getInternship().getSupervisors()) {
                if (!StringUtils.isEmpty(s)) {
                    TeacherInfo teacherInfo = resourceAllocationsTable.getTeacherInfo(s);
                    teacherInfo.getCounter("Supervisor").add(1.0 / planningActivity.getInternship().getSupervisors().size());
                    teacherInfo.addSpaceTime(planningActivity.getSpaceTime());
                    teacherInfo.getActivities().add(planningActivity);
                }
            }
        }
        for (TeacherInfo teacherInfo : resourceAllocationsTable.getTeacherInfos()) {
            Collections.sort(teacherInfo.getActivities(), new Comparator<PlanningActivity>() {
                @Override
                public int compare(PlanningActivity o1, PlanningActivity o2) {
                    int o = o1.getSpaceTime().compareTo(o2.getSpaceTime());
                    if (o == 0) {
                        return o1.getInternship().getCode().compareTo(o2.getInternship().getCode());
                    }
                    return o;
                }
            });
        }

        return resourceAllocationsTable;
    }

    private int parseInt(Object o, int rowNumber, int col) {
        try {
            return Integer.parseInt(StringUtils.trimObject(o));
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage() + " . Row " + rowNumber + " Colum " + col, ex);
        }
    }

    private Date parseDate(Object o, int rowNumber, int col) {
        if (o == null) {
            return null;
        }
        if (o instanceof Date) {
            return (Date) o;
        }
        if (o instanceof String) {
            String s = o.toString().trim();
            if (s.length() > 0) {
                if (s.contains("/")) {
                    try {
                        return new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(s);
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e.getMessage() + " . Row " + rowNumber + " Colum " + col, e);
                    }
                } else if (s.contains("-")) {
                    try {
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(s);
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e.getMessage() + " . Row " + rowNumber + " Colum " + col, e);
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported format for date " + o + " . Row " + rowNumber + " Colum " + col);
                }
            } else {
                return null;
            }
        }
        throw new IllegalArgumentException("Unsupported format for date " + o + " . Row " + rowNumber + " Colum " + col);
    }

    public PlanningActivityTable loadPlanningXlsFile(File file) throws IOException {
        PlanningActivityTable t = new PlanningActivityTable();
        ImportExportManager ie = UPA.getBootstrapFactory().createObject(ImportExportManager.class);
        ie.setFactory(UPA.getBootstrapFactory());
//        ImportExportManager ie = UPA.getPersistenceUnit().getImportExportManager();
        SheetParser p = ie.createSheetParser(file);
        p.setContainsHeader(true);
        DataReader reader = p.parse();
        int window = 5;
        int rowNumber = 0;
        while (reader.hasNext()) {
            rowNumber++;
            DataRow dataRow = reader.readRow();
            int roomsCount = (dataRow.getColumns().length - 3) / window;
            Object[] values = dataRow.getValues();
            Date timeObj = parseDate(values[0], rowNumber, 1);
            int d = parseInt(values[1], rowNumber, 2);
            int h = parseInt(values[2], rowNumber, 3);
            //String time = String.valueOf(timeObj);
            if (timeObj != null) {
                for (int r = 0; r < roomsCount; r++) {
                    String code = StringUtils.trimObject(values[r * window + 3]);
                    boolean enabled = true;
                    if (code.toLowerCase().endsWith("(disabled)")) {
                        code = code.substring(0, code.length() - "(disabled)".length());
                        enabled = false;
                    }
                    String room = "Salle" + (r + 1);
                    if (!StringUtils.isEmpty(code)) {
                        String student = StringUtils.trimObject(values[r * window + 4]);
                        String supervisor = StringUtils.trimObject(values[r * window + 5]);
                        String chair = StringUtils.trimObject(values[r * window + 6]);
                        String examiner = StringUtils.trimObject(values[r * window + 7]);
                        if (StringUtils.isEmpty(supervisor)) {
//                            System.out.println("Why");
                        }
                        if (StringUtils.isEmpty(chair)) {
//                            System.out.println("Why");
                        }
                        if (StringUtils.isEmpty(examiner)) {
//                            System.out.println("Why");
                        }
                        PlanningActivity a = new PlanningActivity();
                        PlanningInternship is = new PlanningInternship();
                        is.setCode(code);
                        is.setStudent(student);
                        is.setSupervisors(Arrays.asList(supervisor));
                        a.setInternship(is);
                        a.setEnabled(enabled);
                        a.setExaminer(examiner.trim());
                        a.setChair(chair.trim());
                        a.setSpaceTime(new PlanningSpaceTime(new PlanningRoom(room), new PlanningTime(timeObj, h, d)));
                        t.addActivity(a);
                    }
                }
            }
        }
        return t;
    }

    public void fillAllocations(PlanningActivity a, ResourceAllocationList table) {
        if (a.getChair() != null) {
            String id = "P:" + a.getChair() + ";" + a.getTime();
            table.add(new ResourceAllocation(id, "PersonSpaceTime", a, a.getChair() + " is Chair      of " + a.getInternship().getCode() + " at " + a.getSpaceTime()));
        }
        if (a.getExaminer() != null) {
            String id = "P:" + a.getExaminer() + ";" + a.getTime();
            table.add(new ResourceAllocation(id, "PersonSpaceTime", a, a.getExaminer() + " is Examiner   of " + a.getInternship().getCode() + " at " + a.getSpaceTime()));
        }
        if (a.getInternship().getSupervisors() != null) {
            for (String s : a.getInternship().getSupervisors()) {
                String id = "P:" + s + ";" + a.getTime();
                table.add(new ResourceAllocation(id, "PersonSpaceTime", a, s + " is Supervisor of " + a.getInternship().getCode() + " at " + a.getSpaceTime()));
            }
        }
        table.add(new ResourceAllocation("R:" + a.getSpaceTime(), "Room", a, a.getRoom().getName() + " is allocated to " + a.getInternship().getCode() + " at " + a.getTime()));
    }

    public static void generate(int maxActivities, int days, File file) throws IOException {
        VrApp.runStandaloneNoLog();
        AcademicPlugin internships = VrApp.getBean(AcademicPlugin.class);
        List<AcademicInternship> internshipsList = internships.findInternships(-1, -1, -1, -1, -1, true);
        if (maxActivities > internshipsList.size() || maxActivities <= 0) {
            maxActivities = internshipsList.size();
        } else if (maxActivities < 3) {
            throw new IllegalArgumentException("Too few activities <3");
        }
        System.out.println("");
        System.out.println("######");
        System.out.println("#    ACTIVITTIES " + maxActivities);
        System.out.println("######");
        Chronometer ch = new Chronometer();

        PlanningActivityTable t = new PlanningActivityTable();
        t.addGeneratedRooms("R", 8);
        t.addGeneratedTimes("2016-06-27", days, "08:30", 60, 6);
        for (AcademicInternship academicInternship : internshipsList) {
            if (academicInternship.getSupervisor() == null) {
                String internshipName = academicInternship.getCode() + " - " + academicInternship.getName() + " - " + academicInternship.getStudent().resolveFullName();
                System.err.println(internshipName + " ignored, no supervisor found");
            } else {
                t.addActivity(academicInternship);
                maxActivities--;
                if (maxActivities == 0) {
                    break;
                }
            }
        }

        t.setDefaultChairsAndExaminers();

        System.out.println();

        PlanningService s = VrApp.getBean(PlanningService.class);
        //s.display(t);
        s.store(t, file);
    }

    public static void run(PlanningService planningService, File file) throws IOException {
        Chronometer ch = new Chronometer();
        //s.display(t);
        PlanningActivityTable t = planningService.load(file);
        PlanningResult r = planningService.generateActivitiesJury(t);
        t = r.getResut();
//        r=planningService.generateActivitiesSpaceTime(t,60*5);
        planningService.display(r.getResut());
        try {
            File file1 = FileUtils.changeFileExtension(file, "fet");
            System.out.println("write to " + file1.getPath());
            planningService.storeFetXml(r.getResut(), file1);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        System.out.println(ch.stop());
//        List<AcademicTeacherCV> list = UPA.getPersistenceUnit().findAll(AcademicTeacherCV.class);
//        
//        
//        for (AcademicTeacherCV r : list) {
//            System.out.println(r);
//        }
    }
}
