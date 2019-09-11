package net.vpc.app.vainruling.plugins.academic.model.internship.planning;


import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
//import net.vpc.scholar.hadrumaths.Complex;
import net.vpc.upa.UPA;
import net.vpc.upa.bulk.*;
import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.MutationOperator;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

/**
 * Created by vpc on 5/19/16.
 */
@Service
public class PlanningService {
    //    public static final int MAX_SUPERVIZERS=2;
    public static final int EVOLUTION_SIZE = 200;
    public static final int MAX_ALLOWED_EVOLUTIONS = 50000;

    public static void main(String[] args) {
        PlanningService s = new PlanningService();
        PlanningActivityTable activityTable = new PlanningActivityTable();
        activityTable.addGeneratedRooms("R", 20);
        activityTable.addGeneratedTimes("2017-09-09 08:00", 4, 30, 5);
//        String pfes="A:B:C\n" +
//                "A:C:B\n" +
//                "B:A:C\n" +
//                "B:C:A\n" +
//                "C:A:B\n"+
//                "C:B:A\n";
//        String pfes="A:?:?\n" +
//                "A:?:?\n" +
//                "B:?:?\n" +
//                "B:?:?\n" +
//                "C:?:?\n"+
//                "C:?:?\n";
        String pfes = "Ali Lahouar - Ens. II:?:?\n" +
                "Anis Ben Arbia - Ens. Perm MA II:?:?\n" +
                "Anis Ben Arbia - Ens. Perm MA II:?:?\n" +
                "Anis Ben Arbia - Ens. Perm MA II:?:?\n" +
                "Anis Ben Arbia - Ens. Perm MA II:?:?\n" +
                "Anis Ben Arbia - Ens. Perm MA II:?:?\n" +
                "Aref Meddeb - Ens. Perm P II:?:?\n" +
                "Aref Meddeb - Ens. Perm P II:?:?\n" +
                "Aref Meddeb - Ens. Perm P II:?:?\n" +
                "Aref Meddeb - Ens. Perm P II:?:?\n" +
                "Imed Bennour - Ens. Perm MA II:?:?\n" +
                "Imed Bennour - Ens. Perm MA II:?:?\n" +
                "Imen Khadhraoui - Ens. V II:?:?\n" +
                "Jamel Bel Hadj Taher - Ens. Perm P II:?:?\n" +
                "Jamel Bel Hadj Taher - Ens. Perm P II:?:?\n" +
                "Jamel Bel Hadj Taher - Ens. Perm P II:?:?\n" +
                "Jamel Bel Hadj Taher - Ens. Perm P II:?:?\n" +
                "Jamel Bel Hadj Taher - Ens. Perm P II:?:?\n" +
                "Lotfi Hamrouni - Ens. Perm A II:?:?\n" +
                "Manel Abdel Hedi - Ens. Perm A II:?:?\n" +
                "Manel Abdel Hedi - Ens. Perm A II:?:?\n" +
                "Manel Abdel Hedi - Ens. Perm A II:?:?\n" +
                "Mohamed Nazih Omri - Ens. Perm P II:?:?\n" +
                "Mohamed Nazih Omri - Ens. Perm P II:?:?\n" +
                "Mohamed Nazih Omri - Ens. Perm P II:?:?\n" +
                "Naoufel Khayati - Ens. Perm MA II:?:?\n" +
                "Naoufel Khayati - Ens. Perm MA II:?:?\n" +
                "Naoufel Khayati - Ens. Perm MA II:?:?\n" +
                "Saoussen Ben Jabra - Ens. Perm MA II:?:?\n" +
                "Saoussen Ben Jabra - Ens. Perm MA II:?:?\n" +
                "Saoussen Ben Jabra - Ens. Perm MA II:?:?\n" +
                "Saoussen Ben Jabra - Ens. Perm MA II:?:?\n" +
                "Taha Ben Salah - Ens. Perm MA II:?:?\n" +
                "Taha Ben Salah - Ens. Perm MA II:?:?\n" +
                "Taha Ben Salah - Ens. Perm MA II:?:?\n" +
                "Walid Chainbi - Ens. Perm MA II:?:?\n" +
                "Walid Chainbi - Ens. Perm MA II:?:?\n" +
                "Walid Chainbi - Ens. Perm MA II:?:?\n" +
                "Walid Chainbi - Ens. Perm MA II:?:?\n" +
                "Walid Chainbi - Ens. Perm MA II:?:?\n" +
                "Walid Chainbi - Ens. Perm MA II:?:?\n" +
                "Walid Chainbi - Ens. Perm MA II:?:?\n" +
                "Walid Chainbi - Ens. Perm MA II:?:?\n";
        int index = 1;
        for (String s1 : pfes.split("\n")) {
            if (s1 != null) {
                String[] ss = s1.split(":");
                PlanningActivity internship = new PlanningActivity(new PlanningInternship(index++, null, null, null, null, null, ss[0]));
                if (!"?".equals(ss[1])) {
                    internship.setChair(ss[1]);
                    internship.setFixedChair(true);
                }
                if (!"?".equals(ss[2])) {
                    internship.setChair(ss[2]);
                    internship.setFixedExaminer(true);
                }
                activityTable.addActivity(internship);
            }
        }
        activityTable.setDefaultChairsAndExaminers();
//        PlanningResult planningResult = s.matchActivitiesGeneticAlgo(activityTable, -1, true, false);
//        System.out.println(planningResult);
//        System.out.println(planningResult.getFitness().isValid() ? "------VALID-------" : "------INVALID-------");
//        for (PlanningActivity planningActivity : planningResult.getResut().getActivities()) {
//            System.out.println(planningActivity.getInternship().getId() + " : " + planningActivity.getInternship().getSupervisors() + ";" + planningActivity.getChair() + ";" + planningActivity.getExaminer() + " : " + planningActivity.getSpaceTime());
//        }
    }


//    public PlanningResult matchActivitiesBruteForce(PlanningActivityTable activityTable) {
//        PlanningActivityTableExt t=new PlanningActivityTableExt(activityTable,true,true,null);
//        PlanningFitnessFunction myFunc = new PlanningFitnessFunction(t);
//        List<String[]> jury=new ArrayList<>();
//        for (String chair : t.getTable().getChairs()) {
//            for (String examiner : t.getTable().getExaminers()) {
//                if(!examiner.equals(chair)){
//                    jury.add(new String[]{chair,examiner});
//                }
//            }
//        }
//        Loop loop = new Loop();
//        for (PlanningActivity activity : t.getTable().getActivities()) {
//            loop.over(0,jury.size());
//        }
//        int[] indexes=null;
//        double fitnessVal=-1;
//        int[] bestVal=null;
//        List<PlanningActivity> activities = t.getTable().getActivities();
//        while((indexes=loop.next())!=null){
//            for (int i = 0; i < activities.size(); i++) {
//                PlanningActivity activity = activities.get(i);
//                String[] currJury = jury.get(indexes[i]);
//                activity.setChair(currJury[0]);
//                activity.setExaminer(currJury[1]);
//            }
//            FitnessValue fitness = myFunc.evalTableFitness();
//            if(fitness.value>fitnessVal){
//                fitnessVal=fitness.value;
//                if(fitness.valid){
//                    bestVal=new int[indexes.length];
//                    System.arraycopy(indexes,0,bestVal,0,indexes.length);
//                    display(t.getTable());
//                }else{
//                    System.out.println(">> "+fitness.value);
//                }
//            }
//        }
//        for (int i = 0; i < activities.size(); i++) {
//            PlanningActivity activity = activities.get(i);
//            String[] currJury = jury.get(bestVal[i]);
//            activity.setChair(currJury[0]);
//            activity.setExaminer(currJury[1]);
//        }
//        return new PlanningResult(t.getTable(),myFunc.evalTableFitness(new PlanningActivityTableExt(t.getTable(),true,true,null)));
//    }




//    public static double nonzero(double x) {
//        if (x == 0) {
//            return 1;
//        }
//        return x;
//    }


}
