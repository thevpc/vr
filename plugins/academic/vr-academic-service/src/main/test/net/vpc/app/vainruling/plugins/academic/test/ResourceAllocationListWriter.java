package net.vpc.app.vainruling.plugins.academic.test;

import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningActivity;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningTime;
import net.vpc.common.strings.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class ResourceAllocationListWriter {
    public static final String STYLE="th, td {border-bottom: 1px solid #ddd;padding: 15px;text-align: left;}";
    private ResourceAllocationList resourceAllocationsTable;

    public ResourceAllocationListWriter(ResourceAllocationList resourceAllocationsTable) {
        this.resourceAllocationsTable = resourceAllocationsTable;
    }
    public void writeTeacherHtml(File out) throws IOException {
        try (PrintStream ps = new PrintStream(out)) {
            writeTeacherHtml(ps);
        }
    }
    public void writeStudentsHtml(File out) throws IOException {
        try (PrintStream ps = new PrintStream(out)) {
            writeStudentsHtml(ps);
        }
    }

    public void writeTeacherHtml(PrintStream out) {
        out.println("<html>");
        out.println("<header>");
        out.println("<style>");
//        out.println("th, td {border-bottom: 1px solid #ddd;padding: 15px;text-align: left;}");
        out.println(STYLE);
        out.println("</style>");
        out.println("</header>");
        out.println("<body>");
        out.println("<h1>PFE Info Appliquee 2017-2018, Session 1, version 1.1</h1>");
        for (TeacherInfo teacherInfo : resourceAllocationsTable.getTeacherInfos()) {
            List<PlanningActivity> enabledActivities = teacherInfo.getEnabledActivities();
            if (!enabledActivities.isEmpty()) {
                out.println("<h2>" + teacherInfo.getTeacher() + "</h2>");
                out.println("<table class='mytable'>");
                out.println("<tr>");
                out.println("<th>Code</th>");
                out.println("<th>Eleve</th>");
                out.println("<th>Date</th>");
                out.println("<th>Heure</th>");
                out.println("<th>Salle</th>");
                out.println("<th>Encadrant</td>");
                out.println("<th>President</td>");
                out.println("<th>Rapporteur</td>");
                out.println("</tr>");
                for (PlanningActivity planningActivity : enabledActivities) {
                    out.println("<tr>");
                    out.println("<td>" + planningActivity.getInternship().getCode() + "</td>");
                    out.println("<td>" + planningActivity.getInternship().getStudent() + "</td>");
                    out.println("<td>" + PlanningTime.TOSTRING_DATEONLY_FORMAT.format(planningActivity.getSpaceTime().getTime().getDateOnly()) + "</td>");
                    out.println("<td>" + PlanningTime.TIME_FORMAT.format(planningActivity.getSpaceTime().getTime().getTimeOnly()) + "</td>");
                    out.println("<td>" + planningActivity.getSpaceTime().getRoom() + "</td>");
                    List<String> supervisors = planningActivity.getInternship().getSupervisors();
                    if (supervisors.size() == 0) {
                        out.println("<td>" + "" + "</td>");
                    } else if (supervisors.size() == 1) {
                        out.println("<td>" + supervisors.get(0) + "</td>");
                    } else {
                        out.println("<td>" + supervisors + "</td>");
                    }
                    out.println("<td>" + planningActivity.getChair() + "</td>");
                    out.println("<td>" + planningActivity.getExaminer() + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }
        }
        out.println("</body>");
        out.println("</html>");
    }

    public void writeStudentsHtml(PrintStream out) {
        out.println("<html>");
        out.println("<header>");
        out.println("<style>");
        out.println(STYLE);
//        out.println("th, td {border-bottom: 1px solid #ddd;padding: 15px;text-align: left;}");
        out.println("</style>");
        out.println("</header>");
        out.println("<body>");
        out.println("<h1>PFE Info Appliquee 2017-2018, Session 1, version 1.1</h1>");
        out.println("<table class='mytable'>");
        out.println("<tr>");
        out.println("<th>Code</th>");
        out.println("<th>Eleve</th>");
        out.println("<th>Date</th>");
        out.println("<th>Heure</th>");
        out.println("<th>Salle</th>");
        out.println("<th>Encadrant</td>");
        out.println("<th>President</td>");
        out.println("<th>Rapporteur</td>");
        out.println("</tr>");
        List<PlanningActivity> activities = resourceAllocationsTable.getActivitiesTable().getActivities();

        for (PlanningActivity planningActivity : activities) {
            if (planningActivity.isEnabled()) {
                out.println("<tr>");
                out.println("<td>" + planningActivity.getInternship().getCode() + "</td>");
                out.println("<td>" + planningActivity.getInternship().getStudent() + "</td>");
                out.println("<td>" + PlanningTime.TOSTRING_DATEONLY_FORMAT.format(planningActivity.getSpaceTime().getTime().getDateOnly()) + "</td>");
                out.println("<td>" + PlanningTime.TIME_FORMAT.format(planningActivity.getSpaceTime().getTime().getTimeOnly()) + "</td>");
                out.println("<td>" + planningActivity.getSpaceTime().getRoom() + "</td>");
                List<String> supervisors = planningActivity.getInternship().getSupervisors();
                if (supervisors.size() == 0) {
                    out.println("<td>" + "" + "</td>");
                } else if (supervisors.size() == 1) {
                    out.println("<td>" + supervisors.get(0) + "</td>");
                } else {
                    out.println("<td>" + supervisors + "</td>");
                }
                out.println("<td>" + planningActivity.getChair() + "</td>");
                out.println("<td>" + planningActivity.getExaminer() + "</td>");
                out.println("</tr>");
            }
        }
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    public void writeText(PrintStream out) {
        for (TeacherInfo teacherInfo : resourceAllocationsTable.getTeacherInfos()) {
            out.println("");
            out.println("==================================================");
            out.println(teacherInfo.getTeacher() + ":");
            out.println("\t" + "E=" + teacherInfo.getCounter("Supervisor"));
            out.println("\t" + "P=" + teacherInfo.getCounter("Chair"));
            out.println("\t" + "R=" + teacherInfo.getCounter("Examiner"));
            out.println("\t" + "Rooms=" + teacherInfo.getRoomsCount());
            out.println("\t" + "Time Slots=" + teacherInfo.getTimesCount());
            out.println("\t" + "Days=" + teacherInfo.getDaysCount() + "::Hours=" + teacherInfo.getHoursCount());
            out.println("\t" + "Distance(R)=" + teacherInfo.getRoomsDistance());
            out.println("\t" + "Distance(T)=" + teacherInfo.getTimeDistance());
        }
        for (TeacherInfo teacherInfo : resourceAllocationsTable.getTeacherInfos()) {
            out.println("");
            out.println("==================================================");
            out.println(teacherInfo.getTeacher() + ":");
            for (PlanningActivity planningActivity : teacherInfo.getActivities()) {
                out.println("\t" + planningActivity.getInternship().getCode()
                        + " " + StringUtils.expand(planningActivity.getInternship().getStudent(), " ", 20)
                        + " : E:" + planningActivity.getSpaceTime()
                        + " : E:" + planningActivity.getInternship().getSupervisors()
                        + " : P:" + planningActivity.getChair()
                        + " : R:" + planningActivity.getExaminer()
                );
            }
        }
    }
}
