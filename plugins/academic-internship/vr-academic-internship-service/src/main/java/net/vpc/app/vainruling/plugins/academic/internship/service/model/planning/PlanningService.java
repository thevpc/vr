package net.vpc.app.vainruling.plugins.academic.internship.service.model.planning;


import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
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
    public static final int EVOLUTION_SIZE = 5000;
    public static final int MAX_ALLOWED_EVOLUTIONS = 500;

    public void display(PlanningActivityTable t) {
        display(t, System.out);
    }

    public void display(PlanningActivityTable t, File file) throws IOException {
        PrintStream out = null;
        try {
            out = new PrintStream(file);
            display(t, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public PlanningActivityTable load(File file) throws IOException {

        ParseFormatManager ie = UPA.getBootstrapFactory().createObject(ParseFormatManager.class);
        SheetParser sheetParser = ie.createSheetParser(file);
        sheetParser.setContainsHeader(false);
        sheetParser.setSkipEmptyRows(false);
        sheetParser.setTrimValues(true);
        DataReader parse = sheetParser.parse();
//        while(parse.)

        BufferedReader r = new BufferedReader(new FileReader(file));
        List<PlanningSpaceTime> spaceTimes = null;
        final int START = 0;
        final int ACTIVITIES = 1;
        int status = 0;
        PlanningActivityTable t = new PlanningActivityTable();
        Map<String, PlanningSpaceTime> spaceTimesMap = new HashMap<>();
        while (parse.hasNext()) {
            Object[] line = parse.readRow().getValues();
            switch (status) {
                case START: {
                    if (line[0].equals("chairs:")) {
                        parse.hasNext();
                        for (Object s : parse.readRow().getValues()) {
                            t.getChairs().add(s.toString().trim());
                        }
                    } else if (line[0].equals("examiners:")) {
                        parse.hasNext();
                        for (Object s : parse.readRow().getValues()) {
                            t.getExaminers().add(s.toString().trim());
                        }
                    } else if (line[0].equals("rooms:")) {
                        parse.hasNext();
                        for (Object s : parse.readRow().getValues()) {
                            t.getRooms().add(new PlanningRoom(s.toString().trim(), t.getRooms().size() + 1));
                        }
                    } else if (line[0].equals("times:")) {
                        parse.hasNext();
                        for (Object s : parse.readRow().getValues()) {
                            t.getTimes().add(new PlanningTime(s.toString().trim()));
                        }
                    } else if (line[0].equals("activities:")) {
                        spaceTimes = t.getSpaceTimes();
                        for (PlanningSpaceTime spaceTime : spaceTimes) {
                            spaceTimesMap.put(spaceTime.toString(), spaceTime);
                        }
                        status = ACTIVITIES;
                    } else {
                        throw new IllegalArgumentException("Parse exception at " + line);
                    }
                    break;
                }
                case ACTIVITIES: {
                    String[] cols = new String[line.length];
                    for (int i = 0; i < cols.length; i++) {
                        cols[i] = line[i].toString().trim();
                    }
                    PlanningActivity a = new PlanningActivity();
                    a.setInternship(new PlanningInternship());
                    if (!cols[0].equals("?")) {
                        a.setSpaceTime(spaceTimesMap.get(cols[0]));
                    }
                    if (!cols[1].equals("?")) {
                        a.setChair(cols[1]);
                    }
                    if (!cols[2].equals("?")) {
                        a.setExaminer(cols[1]);
                    }
                    if (!cols[3].equals("?")) {
                        List<String> sups = new ArrayList<>();
                        for (String s : cols[3].split("\\+")) {
                            sups.add(s.trim());
                        }
                        a.getInternship().setSupervisors(sups);
                    }
                    a.getInternship().setCode(cols[4]);
                    a.getInternship().setName(cols[5]);
                    a.getInternship().setStudent(cols[6]);
                    a.getInternship().setId((int) Double.parseDouble(cols[7]));
                    a.getInternship().setDisciplines(cols[8]);
                    a.getInternship().setDisciplines(cols[9]);
                    t.getActivities().add(a);
                    break;
                }
            }
        }
        r.close();
        return t;
    }

    public void store(PlanningActivityTable t, File file) throws IOException {
        if (t == null) {
            throw new IllegalArgumentException("Null Activity Table");
        } else {
            ParseFormatManager ie = UPA.getBootstrapFactory().createObject(ParseFormatManager.class);
            SheetFormatter sheetFormatter = ie.createSheetFormatter(file);
            sheetFormatter.setWriteHeader(false);
            DataWriter writer = sheetFormatter.createWriter();
//            PrintStream out=new PrintStream(file);
            if (t.getChairs() != null) {
                writer.writeRow(new Object[]{"chairs:"});
                writer.writeRow(t.getChairs().toArray());
            }
            if (t.getExaminers() != null) {
                writer.writeRow(new Object[]{"examiners:"});
                writer.writeRow(t.getExaminers().toArray());
            }
            if (t.getRooms() != null) {
                writer.writeRow(new Object[]{"rooms:"});
                List<String> roomNames = new ArrayList<>();
                for (int i = 0; i < t.getRooms().size(); i++) {
                    PlanningRoom r = t.getRooms().get(i);
                    roomNames.add(r.getName());
                }
                writer.writeRow(roomNames.toArray());
            }
            if (t.getTimes() != null) {
                writer.writeRow(new Object[]{"times:"});
                List<String> timeNames = new ArrayList<>();
                for (int i = 0; i < t.getTimes().size(); i++) {
                    PlanningTime r = t.getTimes().get(i);
                    timeNames.add(PlanningTime.DEFAULT_FORMAT.format(r.getTime()));
                }
                writer.writeRow(timeNames.toArray());
            }
            writer.writeRow(new Object[]{"activities:"});
            List<PlanningActivity> activities = t.getActivities();
            for (PlanningActivity a : activities) {
                //evalTableFitness()
                StringBuilder sup = new StringBuilder();
                if (a.getInternship().getSupervisors() == null) {
                    sup.append("?");
                } else {
                    for (String s : a.getInternship().getSupervisors()) {
                        if (sup.length() > 0) {
                            sup.append(" + ");
                        }
                        sup.append(s);
                    }
                }
                writer.writeRow(new Object[]{
                        (a.getSpaceTime() == null ? "?" : a.getSpaceTime()),
                        (a.getChair() == null ? "?" : a.getChair()),
                        (a.getExaminer() == null ? "?" : a.getExaminer()),
                        sup,
                        a.getInternship().getCode(),
                        a.getInternship().getName(),
                        a.getInternship().getStudent(),
                        a.getInternship().getId(),
                        a.getInternship().getDisciplines(),
                        a.getInternship().getSession()
                });
            }
            writer.close();
//            out.close();
        }
    }

    public Map<String, PlanningTeacherStats> evalTeacherStats(PlanningActivityTable t, boolean fixedOnly) {
        PlanningActivityTableExt t2 = new PlanningActivityTableExt(t);
        PlanningFitnessFunction fitness = new PlanningFitnessFunction(t2);
        return fitness.evalTeacherStats(t, fixedOnly);
    }

    public void display(PlanningActivityTable t, PrintStream out) {
        if (t == null) {
            System.out.println("NULL TABLE");
        } else {
            boolean time = t.getActivities().get(0).getTime() != null;
            PlanningActivityTableExt t2 = new PlanningActivityTableExt(t, true, time, null);


            PlanningFitnessFunction fitness = new PlanningFitnessFunction(t2);
            FitnessValue fitnessValue = fitness.evalTableFitness();
            out.println("------ " + (fitnessValue.valid ? "  VALID" : "INVALID") + " " + fitnessValue);
            out.println("\t times(#" + t.getTimes().size() + "):" + new TreeSet(t.getTimes()));
            out.println("\t rooms(#" + t.getRooms().size() + "):" + new TreeSet(t.getRooms()));
            for (Map.Entry<String, PlanningTeacherStats> e : fitness.evalTeacherStats(t, false).entrySet()) {
                out.println("\t teacher " + e.getKey() + ":");
                out.println("\t\t activities:" + e.getValue().activities);
                out.println("\t\t balance   :" + e.getValue().chairBalance + "/" + e.getValue().examinerBalance);
                out.println("\t\t supervisor:" + e.getValue().supervisor);
                out.println("\t\t chair     :" + e.getValue().chair);
                out.println("\t\t examiner  :" + e.getValue().examiner);
                out.println("\t\t days      :" + e.getValue().days);

            }
            List<PlanningActivity> activities = t.getActivities();
            Collections.sort(activities);
            out.println("\t activities(#" + activities.size() + "):");
            for (PlanningActivity a : activities) {
                //evalTableFitness()
                out.println(a.getSpaceTime() + " : " + a.getChair() + " ; " + a.getExaminer() + " ; " + a.getInternship().getSupervisors() + " ; " + a.getInternship().getCode() + " ; " + a.getInternship().getName());
            }
        }
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

    public PlanningResult generateActivitiesSpaceTime(PlanningActivityTable table, int maxSeconds) {
        return matchActivitiesGeneticAlgo(table, maxSeconds, false, true);
    }

    public PlanningResult generateActivitiesJury(PlanningActivityTable table) {
        PlanningActivityTableExt tableExt = new PlanningActivityTableExt(table, true, false, null);
        PlanningFitnessFunction myFunc = new PlanningFitnessFunction(tableExt);
        List<String> conf_examiners = new ArrayList<>();
        List<String> conf_chairs = new ArrayList<>();
        for (Map.Entry<String, PlanningTeacherStats> e : myFunc.evalTeacherStats(tableExt.getTable(), true).entrySet()) {
            PlanningTeacherStats s = e.getValue();
            double max = Math.ceil(s.supervisor) - s.chair;
            for (int i = 0; i < max; i++) {
                conf_chairs.add(s.teacherName);
            }
            max = Math.ceil(s.supervisor) - s.examiner;
            for (int i = 0; i < max; i++) {
                conf_examiners.add(s.teacherName);
            }
        }
        int setChairs = 0;
        int setExaminers = 0;

        List<Integer> unsetExaminersIndexes = new ArrayList<>();
        List<Integer> unsetChairsIndexes = new ArrayList<>();

        List<PlanningActivity> activities = tableExt.getTable().getActivities();
        for (int i = 0; i < activities.size(); i++) {
            PlanningActivity activity = activities.get(i);
            if (!StringUtils.isEmpty(activity.getChair()) && activity.isFixedChair()) {
                setChairs++;
            } else {
                unsetChairsIndexes.add(i);
            }
            if (!StringUtils.isEmpty(activity.getExaminer()) && activity.isFixedExaminer()) {
                setExaminers++;
            } else {
                unsetExaminersIndexes.add(i);
            }
        }
        Random r = new Random();

        if (unsetExaminersIndexes.size() > 0) {
            int maxLoops = 100;
            while (unsetExaminersIndexes.size() > 0 && conf_examiners.size() > 0 && maxLoops > 0) {
                int a = r.nextInt(unsetExaminersIndexes.size());
                int b = r.nextInt(conf_examiners.size());
                PlanningActivity activity = tableExt.getTable().getActivities().get(a);
                String value = conf_examiners.get(b);
                if (!activity.isSupervisor(value) && !(activity.isFixedChair() && activity.isChair(value))) {
                    activity.setExaminer(value);
                    unsetExaminersIndexes.remove(a);
                    conf_examiners.remove(b);
                    maxLoops = 100;
                } else {
                    maxLoops--;
                }
            }
        }

        if (unsetChairsIndexes.size() > 0) {
            int maxLoops = 100;
            while (unsetChairsIndexes.size() > 0 && conf_chairs.size() > 0 && maxLoops > 0) {
                int a = r.nextInt(unsetChairsIndexes.size());
                int b = r.nextInt(conf_chairs.size());
                PlanningActivity activity = tableExt.getTable().getActivities().get(a);
                String value = conf_chairs.get(b);
                if (!activity.isSupervisor(value) && !(activity.isExaminer(value))) {
                    activity.setChair(value);
                    unsetChairsIndexes.remove(a);
                    conf_chairs.remove(b);
                    maxLoops = 100;
                } else {
                    maxLoops--;
                }
            }
        }

//        int activitiesCount = tableExt.getTable().getActivities().size();
////        if(conf.size()!=activitiesCount){
////            throw new IllegalArgumentException("Why");
////        }
//
//        List<String> chairs=new ArrayList<>(conf_chairs);
//        Collections.shuffle(chairs);
//        for (int i = 0; i < activitiesCount && !chairs.isEmpty(); i++) {
//            boolean valueSet=false;
//            PlanningActivity activity = tableExt.getTable().getActivities().get(i);
//            if(!activity.isFixedChair() || StringUtils.isEmpty(activity.getChair())) {
//                int maxLoops=2*conf_chairs.size();
//                while (maxLoops>0 && !valueSet) {
//                    maxLoops--;
//                    int c = r.nextInt(chairs.size());
//                    String value = chairs.get(c);
//                    if (!activity.isSupervisor(value)) {
//                        if(activity.isFixedExaminer()
//                                && !StringUtils.isEmpty(activity.getExaminer())
//                                && activity.isExaminer(value)){
//                            //could not force this!
//                        }else {
//                            activity.setChair(value);
//                            chairs.remove(c);
//                            valueSet = true;
//                        }
//                    }
//                }
//            }else{
//                chairs.remove(activity.getChair());
//            }
//        }
//
//        List<String> examiners=new ArrayList<>(conf_examiners);
//        Collections.shuffle(examiners);
//        for (int i = 0; i < activitiesCount && !examiners.isEmpty(); i++) {
//            PlanningActivity activity = tableExt.getTable().getActivities().get(i);
//            if(!activity.isFixedExaminer() || StringUtils.isEmpty(activity.getExaminer())) {
//                boolean valueSet = false;
//                int maxLoops = 2 * conf_examiners.size();
//                while (maxLoops > 0 && !valueSet) {
//                    maxLoops--;
//                    int c = r.nextInt(examiners.size());
//                    String value = examiners.get(c);
//                    if (!activity.isChair(value) && !activity.isSupervisor(value)) {
//                        activity.setExaminer(value);
//                        examiners.remove(c);
//                        valueSet = true;
//                    }
//                }
//            }else{
//                examiners.remove(activity.getExaminer());
//            }
//        }
        return new PlanningResult(tableExt.getTable(), myFunc.evalTableFitness(tableExt));
    }

    public PlanningResult matchActivitiesGeneticAlgo(PlanningActivityTable activityTable, int maxSeconds, boolean teachers, boolean spaceTime) {
        PlanningActivityTableExt activityTable2 = null;
        PlanningActivityTable bestActivityTable = null;
        FitnessValue bestFitnessValue = FitnessValue.invalid("invalid", 0);
        Chronometer chronometer = new Chronometer();
        try {
            Configuration.reset();
            Configuration conf = new DefaultConfiguration();
            activityTable2 = new PlanningActivityTableExt(activityTable, teachers, spaceTime, conf);
//            conf.addNaturalSelector(new BestChromosomesSelector(conf, 0.3), true);
            conf.setPreservFittestIndividual(true);
//            conf.setSelectFromPrevGen(0.5);

            if (teachers) {
                conf.getGeneticOperators().clear();
//            conf.addGeneticOperator(new CrossoverOperator(conf, 0.5d));
                conf.addGeneticOperator(new MutationOperator(conf));
                conf.addGeneticOperator(new PlanningMutationOperator(conf, 4, activityTable2));
            }
            PlanningFitnessFunction myFunc = new PlanningFitnessFunction(activityTable2);
            conf.setFitnessFunction(myFunc);
            IChromosome sampleChromosome = null;
            try {
                sampleChromosome = activityTable2.marshall(conf);
            } catch (IllegalArgumentException e) {
                //nothing to plan
                return new PlanningResult(activityTable2.getTable(), myFunc.evalTableFitness(activityTable2));
            }
            conf.setSampleChromosome(sampleChromosome);

            int activitiesSize = activityTable.getActivities().size();
            int spaceTimeSize = activityTable2.getSpaceTimes().size();
            if (spaceTimeSize < activitiesSize) {
                throw new RuntimeException("Too few space time possibilities " + spaceTimeSize + "<" + activitiesSize);
            }
            if (spaceTime) {
                System.out.println("spaceTime ratio : " + ((double) spaceTimeSize) / activitiesSize);
            }
            conf.setPopulationSize(EVOLUTION_SIZE);
            Genotype population = Genotype.randomInitialGenotype(conf);
            IChromosome bestSolutionSoFar = null;
            boolean ok = false;
            int iterations = 0;
            double fitnessValue = Double.POSITIVE_INFINITY;
            int good = 0;
            Double goodVal = Double.NaN;
            Double error = Double.NaN;
            int steadyness = 0;

            for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) {
                iterations = i + 1;
                population.evolve();
                bestSolutionSoFar = population.getFittestChromosome();
                activityTable2.unmarshall(bestSolutionSoFar);
                FitnessValue fv = myFunc.evalTableFitness(activityTable2);
                fitnessValue = myFunc.getFitnessValue(bestSolutionSoFar);
                System.out.println("iteration " + iterations + " : " + fitnessValue + " ; " + (fv.valid ? "VALID" : fv.toString()));
                display(activityTable2.getTable(), new File(System.getProperty("user.home") + "/planning.txt"));
                bestFitnessValue = fv;
                bestActivityTable = activityTable2.getTable().copy();
                if (fv.valid) {
                    if (Double.isNaN(goodVal)) {
                        goodVal = fv.value;
                    } else {
                        error = (fv.value - goodVal) / goodVal;
                    }
                    good++;
                    ok = true;
//                    if(good>=maxIters){
//                        display(activityTable2.getTable());
//                        break;
//                    }
                    //break;
                } else {
                    good = 0;
                }
                long elapsed = chronometer.getTime() / 1000;
                if (elapsed > maxSeconds) {
                    System.out.println(chronometer.toString() + " elapsed > " + maxSeconds + "s. returning " + (ok ? "valid" : "invalid") + " result");
                    break;
                }
            }
            if (!ok) {
                return new PlanningResult(bestActivityTable, bestFitnessValue);
            }
            System.out.println("found solution " + fitnessValue + " in " + iterations + " iterations");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(bestActivityTable.getActivities());
        return new PlanningResult(bestActivityTable, bestFitnessValue);
    }


    public void storeFetXml(PlanningActivityTable activityTable, File file) throws ParserConfigurationException, TransformerException, IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            storeFetXml(activityTable, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private String buildTeacherAcronym(String teacherName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < teacherName.length(); i++) {
            if (i == 0 || teacherName.charAt(i - 1) == ' ') {
                sb.append(Character.toUpperCase(teacherName.charAt(i)));
            }
        }
        return sb.toString();
    }

    private String buildSubjectLabel(PlanningActivity activity) {
        StringBuilder activityLabel = new StringBuilder()
                .append(activity.getInternship().getCode())
                .append("-").append(activity.getInternship()
                        .getName()).append("-")
                .append(activity.getInternship().getStudent());
        activityLabel.append(" (");
        for (String s : activity.getInternship().getSupervisors()) {
            if (activityLabel.charAt(activityLabel.length() - 1) != '(') {
                activityLabel.append(",");
            }
            activityLabel.append("E:");
            activityLabel.append(buildTeacherAcronym(s));
        }
        activityLabel.append(",P:");
        activityLabel.append(buildTeacherAcronym(activity.getChair()));
        activityLabel.append(",R:");
        activityLabel.append(buildTeacherAcronym(activity.getExaminer()));
        activityLabel.append(')');
        return activityLabel.toString();
    }

    public void storeFetXml(PlanningActivityTable activityTable, OutputStream out) throws ParserConfigurationException, TransformerException {
        TreeSet<String> hours = new TreeSet<>();
        TreeSet<String> dtes = new TreeSet<>();
        TreeSet<String> teachers = new TreeSet<>();
        for (PlanningTime t : activityTable.getTimes()) {
            hours.add(t.getTimeName());
            dtes.add(t.getDayName());
        }
        for (String t : activityTable.getTeachers()) {
            teachers.add(t);
        }
        Map<String, PlanningTeacherStats> t = evalTeacherStats(activityTable, false);


        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        //<fet version="5.14.3">
        Element fet = doc.createElement("fet");
        doc.appendChild(fet);
        fet.setAttribute("version", "5.14.3");

        // <Institution_Name>Default institution</Institution_Name>
        Element Institution_Name = doc.createElement("Institution_Name");
        Institution_Name.setTextContent("ENISo");
        fet.appendChild(Institution_Name);

        // <Comments>Default comments</Comments>
        Element Comments = doc.createElement("Comments");
        Comments.setTextContent("Generated by VR");
        fet.appendChild(Comments);

//        <Hours_List>
//        <Number>12</Number>
//        <Name>08:00</Name>
//        <Name>09:00</Name>
//        <Name>10:00</Name>
//        <Name>11:00</Name>
//        <Name>12:00</Name>
//        <Name>13:00</Name>
//        <Name>14:00</Name>
//        <Name>15:00</Name>
//        <Name>16:00</Name>
//        <Name>17:00</Name>
//        <Name>18:00</Name>
//        <Name>19:00</Name>
//        </Hours_List>

        {
            Element Hours_List = doc.createElement("Hours_List");
            fet.appendChild(Hours_List);

            Element Number = doc.createElement("Number");
            Hours_List.appendChild(Number);
            Number.setTextContent(String.valueOf(hours.size()));
            for (String hour : hours) {
                Element Name = doc.createElement("Name");
                Hours_List.appendChild(Name);
                Name.setTextContent(hour);
            }
        }
        {
            Element Days_List = doc.createElement("Days_List");
            fet.appendChild(Days_List);

            Element Number = doc.createElement("Number");
            Days_List.appendChild(Number);
            Number.setTextContent(String.valueOf(dtes.size()));
            for (String hour : dtes) {
                Element Name = doc.createElement("Name");
                Days_List.appendChild(Name);
                Name.setTextContent(hour);
            }
        }

        //<Students_List>
        Element Students_List = doc.createElement("Students_List");
        fet.appendChild(Students_List);


        //<Students_List>
        Element Teachers_List = doc.createElement("Teachers_List");
        fet.appendChild(Teachers_List);
        for (String teacherName : teachers) {
            Element Teacher = doc.createElement("Teacher");
            Teachers_List.appendChild(Teacher);

            Element Name = doc.createElement("Name");
            Teacher.appendChild(Name);

            Name.setTextContent(teacherName);
        }

//        <Subjects_List>
//        <Subject>
//        <Name>pfe</Name>
//        </Subject>
//        </Subjects_List>

        Element Subjects_List = doc.createElement("Subjects_List");
        fet.appendChild(Subjects_List);
        for (PlanningActivity activity : activityTable.getActivities()) {
            Element Subject = doc.createElement("Subject");
            Subjects_List.appendChild(Subject);

            Element Name = doc.createElement("Name");
            Subject.appendChild(Name);
            Name.setTextContent(buildSubjectLabel(activity));
        }


        //<Activity_Tags_List>
        Element Activity_Tags_List = doc.createElement("Activity_Tags_List");
        fet.appendChild(Activity_Tags_List);


        Element Activities_List = doc.createElement("Activities_List");
        fet.appendChild(Activities_List);
        for (PlanningActivity activity : activityTable.getActivities()) {
            Element Activity = doc.createElement("Activity");
            Activities_List.appendChild(Activity);

            for (String tt : activity.getAllTeachers()) {
                Element Teacher = doc.createElement("Teacher");
                Activity.appendChild(Teacher);
                Teacher.setTextContent(tt);
            }
            Element Subject = doc.createElement("Subject");
            Activity.appendChild(Subject);
            Subject.setTextContent(buildSubjectLabel(activity));
            //<Duration>1</Duration>
            Element Duration = doc.createElement("Duration");
            Activity.appendChild(Duration);
            Duration.setTextContent("1");

            Element Total_Duration = doc.createElement("Total_Duration");
            Activity.appendChild(Total_Duration);
            Total_Duration.setTextContent("1");

            Element Id = doc.createElement("Id");
            Activity.appendChild(Id);
            Id.setTextContent(String.valueOf(activity.getInternship().getId()));

            //<Activity_Group_Id>0</Activity_Group_Id>
            Element Activity_Group_Id = doc.createElement("Activity_Group_Id");
            Activity.appendChild(Activity_Group_Id);
            Activity_Group_Id.setTextContent("0");

            Element Active = doc.createElement("Active");
            Activity.appendChild(Active);
            Active.setTextContent("true");
        }

        Element Rooms_List = doc.createElement("Rooms_List");
        fet.appendChild(Rooms_List);
        for (PlanningRoom room : activityTable.getRooms()) {
            Element Room = doc.createElement("Room");
            Rooms_List.appendChild(Room);

            Element Name = doc.createElement("Name");
            Room.appendChild(Name);
            Name.setTextContent(room.getName());

            Element Building = doc.createElement("Building");
            Room.appendChild(Building);

            Element Capacity = doc.createElement("Capacity");
            Room.appendChild(Capacity);
            Capacity.setTextContent("30000");

        }

        {
            Element Time_Constraints_List = doc.createElement("Time_Constraints_List");
            fet.appendChild(Time_Constraints_List);

            Element ConstraintBasicCompulsoryTime = doc.createElement("ConstraintBasicCompulsoryTime");
            Time_Constraints_List.appendChild(ConstraintBasicCompulsoryTime);

            Element Weight_Percentage = doc.createElement("Weight_Percentage");
            ConstraintBasicCompulsoryTime.appendChild(Weight_Percentage);
            Weight_Percentage.setTextContent("100");

//            <ConstraintTeacherMaxDaysPerWeek>
//            <Weight_Percentage>100</Weight_Percentage>
//            <Teacher_Name>Imen Marrakchi</Teacher_Name>
//            <Minimum_Days_Per_Week>1</Minimum_Days_Per_Week>
//            </ConstraintTeacherMinDaysPerWeek>

            for (PlanningTeacherStats stats : t.values()) {
                if (stats.activities > 0) {
                    int maxDays = (int) Math.ceil(1.0 * stats.activities / hours.size());
                    if (maxDays >= 3) {
                        maxDays++;
                    }
                    if (maxDays > dtes.size()) {
                        maxDays = dtes.size();
                    }
                    if (maxDays < dtes.size()) {
                        maxDays++;
                    }
                    Element ConstraintTeacherMaxDaysPerWeek = doc.createElement("ConstraintTeacherMaxDaysPerWeek");
                    Time_Constraints_List.appendChild(ConstraintTeacherMaxDaysPerWeek);

                    Weight_Percentage = doc.createElement("Weight_Percentage");
                    ConstraintTeacherMaxDaysPerWeek.appendChild(Weight_Percentage);
                    Weight_Percentage.setTextContent("100");

                    Element Teacher_Name = doc.createElement("Teacher_Name");
                    ConstraintTeacherMaxDaysPerWeek.appendChild(Teacher_Name);
                    Teacher_Name.setTextContent(stats.teacherName);

                    Element Max_Days_Per_Week = doc.createElement("Max_Days_Per_Week");
                    ConstraintTeacherMaxDaysPerWeek.appendChild(Max_Days_Per_Week);
                    Max_Days_Per_Week.setTextContent(String.valueOf(maxDays));
                }
            }

        }

        {
            Element Space_Constraints_List = doc.createElement("Space_Constraints_List");
            fet.appendChild(Space_Constraints_List);

            Element ConstraintBasicCompulsorySpace = doc.createElement("ConstraintBasicCompulsorySpace");
            Space_Constraints_List.appendChild(ConstraintBasicCompulsorySpace);

            Element Weight_Percentage = doc.createElement("Weight_Percentage");
            ConstraintBasicCompulsorySpace.appendChild(Weight_Percentage);
            Weight_Percentage.setTextContent("100");

            Element ConstraintActivityPreferredRooms = doc.createElement("ConstraintActivityPreferredRooms");
            Space_Constraints_List.appendChild(ConstraintActivityPreferredRooms);

            Weight_Percentage = doc.createElement("Weight_Percentage");
            ConstraintActivityPreferredRooms.appendChild(Weight_Percentage);
            Weight_Percentage.setTextContent("100");

            for (PlanningActivity activity : activityTable.getActivities()) {
                Element Activity_Id = doc.createElement("Activity_Id");
                ConstraintActivityPreferredRooms.appendChild(Activity_Id);
                Weight_Percentage.setTextContent(String.valueOf(activity.getInternship().getId()));

                Element Number_of_Preferred_Rooms = doc.createElement("Number_of_Preferred_Rooms");
                ConstraintActivityPreferredRooms.appendChild(Number_of_Preferred_Rooms);
                Number_of_Preferred_Rooms.setTextContent(String.valueOf(activityTable.getRooms().size()));

                for (PlanningRoom room : activityTable.getRooms()) {
                    Element Preferred_Room = doc.createElement("Preferred_Room");
                    ConstraintActivityPreferredRooms.appendChild(Preferred_Room);
                    Preferred_Room.setTextContent(room.getName());
                }
            }
        }
        {
//            <ConstraintActivityPreferredRooms>
//            <Weight_Percentage>100</Weight_Percentage>
//            <Activity_Id>13</Activity_Id>
//            <Number_of_Preferred_Rooms>5</Number_of_Preferred_Rooms>
//            <Preferred_Room>R1</Preferred_Room>
//            <Preferred_Room>R2</Preferred_Room>
//            <Preferred_Room>R3</Preferred_Room>
//            <Preferred_Room>R4</Preferred_Room>
//            <Preferred_Room>R5</Preferred_Room>
//            </ConstraintActivityPreferredRooms>

            Element Space_Constraints_List = doc.createElement("Space_Constraints_List");
            fet.appendChild(Space_Constraints_List);

            Element ConstraintBasicCompulsorySpace = doc.createElement("ConstraintBasicCompulsorySpace");
            Space_Constraints_List.appendChild(ConstraintBasicCompulsorySpace);

            Element Weight_Percentage = doc.createElement("Weight_Percentage");
            ConstraintBasicCompulsorySpace.appendChild(Weight_Percentage);
            Weight_Percentage.setTextContent("100");
        }


        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(out);

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);
    }


//    public static double nonzero(double x) {
//        if (x == 0) {
//            return 1;
//        }
//        return x;
//    }


}
