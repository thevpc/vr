//package net.thevpc.app.vainruling.plugins.academic.internship.test;
//
//import net.thevpc.app.vainruling.core.service.TraceService;
//import net.thevpc.app.vainruling.core.service.VrApp;
//import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
//import net.thevpc.common.time.Chronometer;
//
///**
// * this is not a unit test
// */
//public class Ex2 {
//    public static void main(String[] args) {
//        Chronometer ch = Chronometer.start();
//        VrApp.runStandalone("taha.bensalah", "myâssword");
//        TraceService trace = TraceService.get();
//        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
//        AcademicPlugin aci = VrApp.getBean(AcademicPlugin.class);
//        aci.findActualInternshipsByStudent(0);
//        aci.findActualInternshipsBySupervisor(0);
//        aci.findActualInternshipsByTeacher(1, 1);
//        aci.findInternships(1, -1, 1, 1, 1, true);
//        aci.findInternshipsByDepartment(1, true);
//        aci.findInternshipsByDepartmentExt(1, true);
//        aci.findInternshipsByTeacherExt(1, 1, 1, 1, true);
//        aci.findInternshipTeacherInternshipsCount(1, 1, 1);
//        aci.findInternshipTeachersInternshipsCounts(1, 1);
//        aci.generateInternships(3, "IA3.1");
////        trace.archiveLogs(0);
//        System.out.println(ch.stop());
////        List<AcademicTeacherCV> list = UPA.getPersistenceUnit().findAll(AcademicTeacherCV.class);
////
////
////        for (AcademicTeacherCV r : list) {
////            System.out.println(r);
////        }
//    }
//}
