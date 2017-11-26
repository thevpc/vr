import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.common.util.Chronometer;
import net.vpc.common.vfs.VFS;

import java.io.IOException;

/**
 * this is not a unit test
 */
public class TestImport {
    public static void main(String[] args) {
        Chronometer ch = new Chronometer();
        VrApp.runStandalone("taha.bensalah", "mypassword");
        TraceService trace = TraceService.get();
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        try {
            aca.importFile(11, VFS.createNativeFS().get("/home/vpc/tmp/2016-MEC-example.test.course-assignments.xlsx"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(ch.stop());
    }
}
