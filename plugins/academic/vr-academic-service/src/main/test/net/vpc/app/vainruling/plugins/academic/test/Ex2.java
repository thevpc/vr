package net.thevpc.app.vainruling.plugins.academic.test;

import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.common.time.Chronometer;

/**
 * this is not a unit test
 */
public class Ex2 {
    public static void main(String[] args) {
        Chronometer ch = Chronometer.start();
        VrApp.runStandalone("taha.bensalah", "myâssword");
        TraceService trace = TraceService.get();
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        aca.findActualInternshipsByStudent(0);
        aca.findActualInternshipsBySupervisor(0);
        aca.findActualInternshipsByTeacher(1, 1);
        aca.findInternships(1, -1, 1, 1, 1, true);
        aca.findInternshipsByDepartment(1, true);
        aca.findInternshipsByDepartmentExt(1, true);
        aca.findInternshipsByTeacherExt(1, 1, 1, 1, 1, true);
        aca.findInternshipTeacherInternshipsCount(1, 1, 1);
        aca.findInternshipTeachersInternshipsCounts(1, 1);
        aca.generateInternships(3, "IA3.1");
//        trace.archiveLogs(0);
        System.out.println(ch.stop());
//        List<AcademicTeacherCV> list = UPA.getPersistenceUnit().findAll(AcademicTeacherCV.class);
//        
//        
//        for (AcademicTeacherCV r : list) {
//            System.out.println(r);
//        }
    }
}
