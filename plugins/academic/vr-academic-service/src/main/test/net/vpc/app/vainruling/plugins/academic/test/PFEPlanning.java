package net.vpc.app.vainruling.plugins.academic.test;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningActivityTable;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningResult;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.PlanningService;
import net.vpc.common.io.FileUtils;
import net.vpc.common.util.Chronometer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * this is not a unit test
 */
public class PFEPlanning {
    public static void main(String[] args) {
        File file = new File(System.getProperty("user.home") + "/myplanning.xlsx");

        try {
            generate(100, 5, file);
            run(new PlanningService(), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VrApp.stopStandalone();
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
