/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.service.test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.XlsxLoadImporter;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.common.gomail.GoMail;
import net.vpc.common.gomail.GoMailFormat;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class TeachersLoadTest {

    public static void main(String[] args) {
        VrAppTest.runStandalone();
        VrApp.getBean(TeachersLoadTest.class).run();
    }

    public void run() {
        UPA.getContext().invokePrivileged(new Action<Object>() {

            @Override
            public Object run() {
                runGenerate();
                return null;
            }
        }, null);

//        runSendMail();
    }

//    public void runTest() {
//        try {
//            String version = "v14-temp";
//            String year = "2015-2016";
//            String dir = "/home/vpc/Data/eniso/teaching-load/2015-2016";
//            String templatesFolder = dir + "/templates";
//            String generatedFolder = dir + "/generated/" + version;
////            String semester = "2";
//            String semester = null;
//            CorePlugin core = VRApp.getBean(CorePlugin.class);
//            AcademicPlugin s = VRApp.getBean(AcademicPlugin.class);
//            s.resetModuleTeaching();
//            s.importFolder(core.getFileSystem().get(dir + "/data"),
//                    new ImportOptions()
//            );
//            Integer[] teachers = new Integer[]{s.findTeacher(StringComparators.ilike("*khiroun*")).getId()};
//
////            s.generatePrintableTeacherLoad(year, teachers, semester, templatesFolder, generatedFolder, "*-charge-eniso-ii-s2-" + year + "-" + version + ".xls");
//        }
//        catch (Exception ex) {
//            Logger.getLogger(XlsxLoadImporter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void runGenerate() {
        AcademicPlugin s = VrApp.getBean(AcademicPlugin.class);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        core.setAppProperty("academicPlugin.year", null, "2014-2015");
        core.setAppProperty("academicPlugin.import.version", null, "21bis");
//        s.resetTeachers();
        s.resetModuleTeaching();
        s.importTeachingLoad();
        s.generateTeachingLoad();
    }

    public void runGenerate0() {
        try {
            String version = "v01-temp";
            String year = "2015-2016";
            String inputFolder = "/home/vpc/Data/eniso/teaching-load/${year}";
            String outputFolder = "/home/vpc/Data/eniso/teaching-load/${year}/generated/${version}";

            CorePlugin core = VrApp.getBean(CorePlugin.class);
//            core.setAppProperty("academicPlugin.year", null, year);
            core.setAppProperty("academicPlugin.import.version", null, version);
            core.setAppProperty("academicPlugin.import.configFolder", null, inputFolder);
            core.setAppProperty("academicPlugin.import.outputFolder", null, outputFolder);

            AcademicPlugin s = VrApp.getBean(AcademicPlugin.class);
            s.generateTeachingLoad();
//            net.vpc.vfs.VirtualFileSystem fs = core.getFileSystem();
//            fs = new NativeVFS();
//            VFS.copy(fs.get(dataFolder), fs.get(outputFolder), new VFileFilter() {
//
//                @Override
//                public boolean accept(VFile pathname) {
//                    return pathname.isDirectory() || (pathname.isFile()
//                            && (pathname.getName().toLowerCase().endsWith(".xls")
//                            || pathname.getName().toLowerCase().endsWith(".xlsx")));
//                }
//            });
////            ZipUtils.zip(outputFolder + "/" + namePattern.replace("*", "templates"), new ZipOptions().setSkipRoots(true).setTempFile(false), templatesFolder);
//            s.resetModuleTeaching();
//            s.importFolder(
//                    fs.get(dataFolder),
//                    new ImportOptions()
//            );
//            s.generate(
//                    new TeacherGenerationOptions()
//                    //                    .setTeacherIds(new Integer[]{s.findTeacher(StringComparators.ilike("*zarrouk*")).getId()})
//                    //                    .setSemester(null)
//                    .setContents(
//                            //                            GeneratedContent.CourseListLoads,
//                            GeneratedContent.GroupedTeacherAssignments,
//                            GeneratedContent.TeacherAssignments,
//                            GeneratedContent.TeacherListAssignmentsSummary,
//                            GeneratedContent.Bundle
//                    )
//                    .setTemplateFolder(templatesFolder)
//                    .setOutputFolder(outputFolder)
//                    .setOutputNamePattern(namePattern)
//                    .setIncludeIntents(true)
//            );
//            XMailService mails = new XMailService();
//            XMail m=mails.read(XMailFormat.TEXT, new File(dir+"/notification-charge.xmail"));
//            m.setSimulate(true);
//            mails.send(m);
//            for () {
//                break;
//            }
        }
        catch (Exception ex) {
            Logger.getLogger(XlsxLoadImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runSendMail() {
        try {
            String dir = "/home/vpc/Data/eniso/teaching-load/2014-2015";
            AcademicPlugin s = VrApp.getBean(AcademicPlugin.class);
            MailboxPlugin mails = new MailboxPlugin();
            GoMail m = mails.read(GoMailFormat.TEXT, new File(dir + "/notification-charge-finale.xmail"));
            m.setSimulate(false);
            mails.sendExternalMail(m,null,null);
//            for () {
//                break;
//            }
        }
        catch (IOException ex) {
            Logger.getLogger(XlsxLoadImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    private static void showTeacher(TeachersService s, Teacher t) throws IOException {
//        DecimalFormat F = new DecimalFormat("0.0#");
//        TeacherStat ts = s.evalTeacherStat(null, t.getId());
//        System.out.println("----------------------------------------------");
//        System.out.println(Teacher.getName(ts.getTeacher()));
//        for (Semester sem : s.findSemesters()) {
//            System.out.println("Semestre " + sem);
//            for (Course m : s.findModules(t.getId(), null)) {
//                if (m.getSemester().getName().equals(sem.getName())) {
//                    ModuleStat ms = s.evalModuleStat(null, m.getId());
//                    System.out.println(m.getName() + " " + m.getValueC() + "/" + m.getValueTD() + "/" + m.getValueTP() + "/" + m.getValuePM() + " * " + m.getNbGroups() + " :: " + ms.getValueC() + "/" + ms.getValueTD() + "/" + ms.getValueTP() + "/" + ms.getValuePM() + "/" + ms.getValueEquiv() + "/" + ms.getValueEffWeek());
//                }
//            }
//        }
//        for (TeacherSemesterStat ss : ts.getSemesters()) {
//            System.out.println("  Semestre " + ss.getSemester().getName() + " / C  : " + F.format(ss.getValueC()));
//            System.out.println("  Semestre " + ss.getSemester().getName() + " / TD : " + F.format(ss.getValueTD()));
//            System.out.println("  Semestre " + ss.getSemester().getName() + " / TP : " + F.format(ss.getValueTP()));
//            System.out.println("  Semestre " + ss.getSemester().getName() + " / PM : " + F.format(ss.getValuePM()));
//            System.out.println("  Charge  Semestre " + ss.getSemester().getName() + " : " + F.format(ss.getValueEquiv()));
//
//            System.out.println("  Semestre " + ss.getSemester().getName() + " / C  / Semaine : " + F.format(ss.getValueWeekC()));
//            System.out.println("  Semestre " + ss.getSemester().getName() + " / TD / Semaine : " + F.format(ss.getValueWeekTD()));
//            System.out.println("  Semestre " + ss.getSemester().getName() + " / TP / Semaine : " + F.format(ss.getValueWeekTP()));
//            System.out.println("  Semestre " + ss.getSemester().getName() + " / PM / Semaine : " + F.format(ss.getValueWeekPM()));
//            System.out.println("  Charge Semestre " + ss.getSemester().getName() + " / Semaine : " + F.format(ss.getValueWeekEquiv()));
//
//            System.out.println("  Bilan Heures sup : Semestre " + ss.getSemester().getName() + " : " + F.format(ss.getValueExtra()));
//            System.out.println("  ----------");
//        }
//        System.out.println("Total C  : " + F.format(ts.getValueC()));
//        System.out.println("Total TD : " + F.format(ts.getValueTD()));
//        System.out.println("Total TP : " + F.format(ts.getValueTP()));
//        System.out.println("Total PM : " + F.format(ts.getValuePM()));
//        System.out.println("Charge annuelle totale           : " + F.format(ts.getValueEquiv()));
//        System.out.println("Charge annuelle totale / Semaine : " + F.format(ts.getValueWeekEquiv()));
//        System.out.println("Bilan annuel heures sup          : " + F.format(ts.getValueExtra()));
//    }
}
