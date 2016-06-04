package net.vpc.app.vainruling.service.test;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.common.util.Chronometer;
public class Ex2 {
    public static void main(String[] args) {
        Chronometer ch=new Chronometer();
        VrAppTest.runStandalone("taha.bensalah", "canard77");
        TraceService trace = VrApp.getBean(TraceService.class);
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        for (AcademicStudent a : aca.findStudents("IA1", null)) {
            System.out.println(a);
        }
        trace.archiveLogs(0);
        System.out.println(ch.stop());
//        List<AcademicTeacherCV> list = UPA.getPersistenceUnit().findAll(AcademicTeacherCV.class);
//        
//        
//        for (AcademicTeacherCV r : list) {
//            System.out.println(r);
//        }
    }
}
