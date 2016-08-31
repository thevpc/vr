import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.stat.DeviationConfig;
import net.vpc.common.util.Chronometer;

/**
 * Created by vpc on 8/23/16.
 */
public class TestLoad {
    public static void main(String[] args) {
        Chronometer ch = new Chronometer();
        VrApp.runStandalone("taha.bensalah", "myâssword");
        TraceService trace = TraceService.get();
        AcademicPlugin aca = VrApp.getBean(AcademicPlugin.class);
        Chronometer c=new Chronometer();
        for (int i = 0; i < 20000; i++) {
            c.start();
            //List<TeacherPeriodStat> a = aca.evalTeacherStatList(11, null, null, true, new DeviationConfig(), new StatCache(), true);
            aca.evalTeacherStat(11, 19, null, true, new DeviationConfig());
            System.out.println("time = " + c.stop());
            System.out.print("");
        }

        System.out.println(ch.stop());
    }
}
