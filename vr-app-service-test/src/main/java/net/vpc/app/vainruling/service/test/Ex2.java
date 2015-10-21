package net.vpc.app.vainruling.service.test;
import java.util.List;
import net.vpc.app.vainruling.plugins.academic.service.model.content.AcademicTeacherCV;
import net.vpc.upa.UPA;
public class Ex2 {
    public static void main(String[] args) {
        VrAppTest.runStandalone("aref.meddeb", "aref1243");
        List<AcademicTeacherCV> list = UPA.getPersistenceUnit().findAll(AcademicTeacherCV.class);
        
        
        for (AcademicTeacherCV r : list) {
            System.out.println(r);
        }
    }
}
