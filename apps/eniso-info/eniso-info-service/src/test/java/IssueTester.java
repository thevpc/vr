import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;

import java.util.List;

/**
 * Created by vpc on 12/31/16.
 */
public class IssueTester {
    public static void main(String[] args) {
        VrApp.runStandalone();

        AcademicPerfEvalPlugin a=VrApp.getBean(AcademicPerfEvalPlugin.class);
        List<AcademicClass> classesWithFeedbacks = a.findClassesWithFeedbacks(11, null, false, true);
        for (AcademicClass classesWithFeedback : classesWithFeedbacks) {
            System.out.println(classesWithFeedback);
        }
    }
}
