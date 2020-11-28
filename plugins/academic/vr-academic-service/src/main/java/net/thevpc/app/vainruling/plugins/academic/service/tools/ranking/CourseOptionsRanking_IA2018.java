/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.tools.ranking;

import net.thevpc.upa.UPA;
import net.thevpc.upa.bulk.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
public class CourseOptionsRanking_IA2018 {
    private final String USER_HOME_DATA_ENISO_STUDENTS = "/data/vpc/data/eniso/eleves-ingenieurs/2018-2019/choix-options/IA";///old/v01
    int[] maxOptions = new int[]{28, 14, 23};
    private String[][] modulesByOptionsNames = new String[][]{
        {"M298,N,5,2", "M288,N,5,2", "M2I1,N,5,2"},
        {"M258,N,5,2", "M259,N,5,2", "M260,N,5,2"},
        {"M262,N,5,2", "M279,N,5,2", "M2I1,N,5,2"}};
    private String file_notes = USER_HOME_DATA_ENISO_STUDENTS + "/2IA_P.xlsx";
    private String file_choix = USER_HOME_DATA_ENISO_STUDENTS + "/2IA_CHOIX.xlsx";
    private int version = 2;

    
    
    private static ParseFormatManager pfm = UPA.getBootstrap().getFactory().createObject(ParseFormatManager.class);
    TreeSet<ChoiceInfo> sortedByNameChoiceInfos = new TreeSet<ChoiceInfo>(new Comparator<ChoiceInfo>() {
        @Override
        public int compare(ChoiceInfo o1, ChoiceInfo o2) {
            return Integer.compare(o1.number, o2.number);
        }
    });

    private String file_intermediaire = USER_HOME_DATA_ENISO_STUDENTS + "/3IA-FOR-OPTION"+version+".xlsx";
    private String file_result = USER_HOME_DATA_ENISO_STUDENTS + "/3IA-OPTIONS-FINAL"+version+".xlsx";
    private double[][] avgMoygByOption;
    private final Comparator<StudentInfo> STUDENT_MERITE_COMPARATOR = new Comparator<StudentInfo>() {

        @Override
        public int compare(StudentInfo o1, StudentInfo o2) {
            if (o1.result != o2.result) {
                return o1.result.compareTo(o2.result);
            }
//            if (o1.redoublement != o2.redoublement) {
//                if (o1.redoublement) {
//                    return 1;
//                } else if (o2.redoublement) {
//                    return -1;
//                }
//            }
            double moyg1 = evalGlobalScore(o1);
            double moyg2 = evalGlobalScore(o2);
            int v = -Double.compare(moyg1, moyg2);
            if (v != 0) {
                return v;
            }
            v = -Double.compare(o1.moyr, o2.moyr);
            if (v != 0) {
                return v;
            }
            v = o1.nom.compareTo(o2.nom);
            if (v != 0) {
                return v;
            }
            v = o1.prenom.compareTo(o2.prenom);
            if (v != 0) {
                return v;
            }
            return v;
        }
    };

    public static void main(String[] args) {
        CourseOptionsRanking_IA2018 c = new CourseOptionsRanking_IA2018();
        c.generateOptionsFile();
        c.evaluateAssignments();
    }

    public int getNbOptions() {
        return modulesByOptionsNames.length;
    }
    
    

    private static int getAssignments(List<StudentInfo> o, int choiceNumber) {
        int x = 0;
        for (StudentInfo o1 : o) {
            if (o1.assignment == choiceNumber) {
                x++;
            }
        }
        return x;
    }

    private static int getNbChoix(List<StudentInfo> o, int choiceNumber, int value) {
        int x = 0;
        for (StudentInfo o1 : o) {
            if (o1.choice[choiceNumber - 1] == value) {
                x++;
            }
        }
        return x;
    }

    public static ChoiceInfo findChoiceInfo(ChoiceInfo[] optionsInfos, int number) {
        for (int i = 0; i < optionsInfos.length; i++) {
            if (optionsInfos[i].number == number) {
                return optionsInfos[i];
            }
        }
        throw new IllegalArgumentException("Never");
    }

    private double evalGlobalScore(StudentInfo o1) {
//        double penal_redoulement = 0.05;
//        double bonus_mastere = 0.05;
//        return (o1.moyg + bonus_mastere * Math.max(o1.master - 10, 0))*(o1.redoublement?(1- penal_redoulement):1);

        double v = o1.moyg;

        v += 0.01 * Math.max(o1.master - 10, 0);

        if (o1.redoublement) {
            v -= 0.1;
        }

        if (null != o1.result) {
            switch (o1.result) {
                case C:
                    v -= 0.3;
                    break;
                case CA:
                    v -= 5;
                    break;
                case CAC:
                    v -= 7;
                    break;
                default:
                    break;
            }
        }

        return v;
    }

    public int getNbModulesByOption(int optionIndex) {
        return modulesByOptionsNames[optionIndex].length;
    }

    private double evalOptionScore(StudentInfo studentInfo, int optionIndex) {
        int count = getNbModulesByOption(optionIndex);
        double s = 0;
        for (int j = 0; j < count; j++) {
            s += (studentInfo.options[optionIndex].moyg[j] - avgMoygByOption[optionIndex][j]);
        }
        s /= count;

        return evalGlobalScore(studentInfo) + s * 0.5;
    }

    private StudentInfo findBestDiff(StudentInfo a, List<StudentInfo> b) {
        for (StudentInfo b0 : b) {
            if (isSameStudent(a, b0)) {
                return b0;
            }
        }
        throw new IllegalArgumentException("Not found Student " + a.id + " " + a.prenom + " " + a.nom);
    }

    private boolean isSameStudent(StudentInfo a, StudentInfo b) {
        String n1 = a.id;
        String n2 = b.id;
        while (n1.length() < n2.length()) {
            n1 = "0" + n1;
        }
        while (n2.length() < n1.length()) {
            n2 = "0" + n2;
        }
        return n1.equals(n2);
    }

    public List<StudentInfo> loadStudents1() {
        List<StudentInfo> list1 = loadStudents1_notes(RankingTools.resolveFile(file_notes));
        loadStudents1_choix(RankingTools.resolveFile(file_choix), list1);
        return list1;
    }

    public void loadStudents1_choix(File f, List<StudentInfo> list1) {
        try {
            SheetParser sp = pfm.createSheetParser(f);
            sp.setContainsHeader(true);
            DataReader rows = sp.parse();
            List<String> headers = null;
            while (rows.hasNext()) {
                DataRow row = rows.readRow();
                if (headers == null) {
                    DataColumn[] columns = rows.getColumns();
                    headers = new ArrayList<>(columns.length);
                    for (DataColumn column : columns) {
                        headers.add(column.getName());
                    }
                }
                Object[] values = row.getValues();
                StudentInfo studentInfo = new StudentInfo();
                studentInfo.num = values[2] == null ? null : String.valueOf(values[2]);
                studentInfo.id = studentInfo.num;
                if (studentInfo.num != null && studentInfo.num.length() > 0) {
                    studentInfo.prenom = String.valueOf(values[5]);
                    studentInfo.nom = String.valueOf(values[6]);
                    StudentInfo pp = findBestDiff(studentInfo, list1);
                    studentInfo = pp;
                    String master = String.valueOf(values[1]);
                    studentInfo.choice=new int[getNbOptions()];
                    for (int i = 0; i < getNbOptions(); i++) {
                        studentInfo.choice[i]=Integer.parseInt(String.valueOf(values[8+i]));
                    }
                    studentInfo.masterPos = ("IIC".equals(master) ? 1 : "II".equals(master) ? 2 : "GT".equals(master) ? 3 : -1);
                    studentInfo.redoublement = RankingTools.strValOrNull(values, 12-3+getNbOptions()) != null;
                    studentInfo.fixedChoice = RankingTools.strValOrNull(values, 13-3+getNbOptions()) != null;
                    if (studentInfo.fixedChoice) {
                        studentInfo.choice = new int[getNbOptions()];
                        studentInfo.choice[0]=RankingTools.intValOrFF(values, 13-3+getNbOptions());
                        int oo = 1;
                        for (int i = 1; i < studentInfo.choice.length + 1; i++) {
                            if (i != studentInfo.choice[0]) {
                                studentInfo.choice[oo] = i;
                                oo++;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (StudentInfo studentInfo : list1) {
            if (studentInfo.choice == null) {
                studentInfo.choice = new int[getNbOptions()];
                System.out.println("Choix manquant pour " + studentInfo.prenom + " " + studentInfo.nom);
                for (int i = 0; i < maxOptions.length; i++) {
                    studentInfo.choice[i] = i + 1;
                }
                studentInfo.fixedChoice = true;
            }
        }
    }

    public List<StudentInfo> loadStudents1_notes(File f) {
        List<StudentInfo> principal = new ArrayList<StudentInfo>();
        try {
            SheetParser sp = pfm.createSheetParser(f);
            sp.setContainsHeader(true);
            DataReader rows = sp.parse();
            List<String> headers = null;
            int[][] modulesByOptionsIndexes;
            modulesByOptionsIndexes = new int[getNbOptions()][];
            for (int i = 0; i < getNbOptions(); i++) {
                modulesByOptionsIndexes[i] = new int[getNbModulesByOption(i)];
            }
            while (rows.hasNext()) {
                DataRow row = rows.readRow();
                if (headers == null) {
                    DataColumn[] columns = rows.getColumns();
                    headers = new ArrayList<>(columns.length);
                    for (DataColumn column : columns) {
                        headers.add(column.getName());
                    }
                    for (int i = 0; i < getNbOptions(); i++) {
                        for (int j = 0; j < getNbModulesByOption(i); j++) {
                            modulesByOptionsIndexes[i][j] = headers.indexOf(modulesByOptionsNames[i][j]);
                            if (modulesByOptionsIndexes[i][j] < 0) {
                                throw new IllegalArgumentException("Not Found");
                            }
                        }
                    }
                }
                Object[] values = row.getValues();
                StudentInfo studentInfo = new StudentInfo();
                studentInfo.num = String.valueOf(values[0]);
                if (studentInfo.num.length() > 0) {
                    studentInfo.id = String.valueOf(values[1]);
                    studentInfo.nom = String.valueOf(values[2]);
                    studentInfo.prenom = String.valueOf(values[3]);
                    studentInfo.moyg = RankingTools.asDouble((values[103]));
//                    studentInfo.moyr = asDouble((values[105]));
                    studentInfo.result = StudentResult.valueOf(String.valueOf(values[104]));
                    studentInfo.redoublement = values.length > 114 ? ("R".equals(String.valueOf(values[114]))) : false;
                    studentInfo.choice = values.length > 113 ? (new int[]{RankingTools.asInt(values[111]), RankingTools.asInt(values[112]), RankingTools.asInt(values[113])}) : null;
                    studentInfo.fixedChoice = values.length > 115 ? ("1".equals(String.valueOf(values[115]))) : false;
                    studentInfo.options = new StudentOption[getNbOptions()]; //
                    for (int i = 0; i < studentInfo.options.length; i++) {
                        StudentOption option = new StudentOption();
                        studentInfo.options[i] = option;
                        option.optionNumber = i + 1;
                        double[] doubles = new double[getNbModulesByOption(i)];
                        studentInfo.options[i].moyg = doubles;
                        for (int j = 0; j < getNbModulesByOption(i); j++) {
                            doubles[j] = RankingTools.asDouble(values[modulesByOptionsIndexes[i][j]]);
                        }

                    }
                    principal.add(studentInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return principal;
    }

    public void writeStudents2(List<StudentInfo> initialList) throws IOException {
        List<StudentInfo> all = new ArrayList<>();
        List<StudentInfo> nonFixed = new ArrayList<>();
        List<StudentInfo> fixed = new ArrayList<>();
        for (StudentInfo p : initialList) {
            if (p.result != StudentResult.R) {
                //admis ok
                if (p.fixedChoice) {
                    fixed.add(p);
                } else {
                    nonFixed.add(p);
                }
                all.add(p);
            }
        }
        Collections.sort(nonFixed, STUDENT_MERITE_COMPARATOR);
        for (int i = 0; i < nonFixed.size(); i++) {
            if (i > 0) {
                if (nonFixed.get(i).moyg == nonFixed.get(i - 1).moyg) {
                    nonFixed.get(i).rank = nonFixed.get(i - 1).rank;
                } else {
                    nonFixed.get(i).rank = i + 1;
                }
            } else {
                nonFixed.get(i).rank = i + 1;
            }
        }
        SheetFormatter frm = pfm.createSheetFormatter(new File(file_intermediaire.replace("${user.home}", System.getProperty("user.home"))));
        List<String> outputHeader = new ArrayList<>();
        outputHeader.addAll(Arrays.asList("SEL", "NUM", "ID", "NOM", "PRENOM", "MOYG", "RANG"));
        for (int i = 0; i < getNbOptions(); i++) {
            for (int j = 0; j < getNbModulesByOption(i); j++) {
                outputHeader.add("OP" + (i + 1) + "-M" + (j + 1));
            }
        }
        outputHeader.addAll(Arrays.asList("RES", "REDOUBLEMENT", "MOY MASTER"
        //                , "MASTER"
        ));
        for (int i = 0; i < getNbOptions(); i++) {
            outputHeader.add("CHOIX" + (i + 1));
        }
        outputHeader.add("FIXED");

        for (String c : outputHeader) {
            SheetColumn cc = new SheetColumn();
            cc.setName(c);
            frm.getColumns().add(cc);
        }
        frm.setContentType(SheetContentType.XLSX);
        frm.setWriteHeader(true);
        DataWriter w = frm.createWriter();
        for (StudentInfo a : all) {
            List<Object> row = new ArrayList<>();
            row.addAll(Arrays.asList(
                    "",
                    a.num,
                    a.id,
                    a.nom,
                    a.prenom,
                    a.moyg,
                    a.rank
            ));
            for (int i = 0; i < a.options.length; i++) {
                for (int j = 0; j < a.options[i].moyg.length; j++) {
                    row.add(a.options[i].moyg[j]);
                }
            }
            row.addAll(Arrays.asList(
                    String.valueOf(a.result),
                    a.redoublement ? "R" : "",
                    a.master
            //                    ,a.masterPos
            ));
            for (int i = 0; i < a.choice.length; i++) {
                row.add(a.choice[i]);
            }
            row.add(a.fixedChoice ? "1" : "");
            w.writeRow(row.toArray());
        }
        w.close();
    }

    public void generateOptionsFile() {
        try {
            List<StudentInfo> list = loadStudents1();
            writeStudents2(list);
        } catch (Exception ex) {
            Logger.getLogger(CourseOptionsRanking_IA2018.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<StudentInfo> loadStudents2() throws IOException {
        final List<StudentInfo> liste = new ArrayList<StudentInfo>();
        SheetParser sp = pfm.createSheetParser(new File(file_intermediaire.replace("${user.home}", System.getProperty("user.home"))));
        sp.setContainsHeader(true);
        DataReader rows = sp.parse();
        while (rows.hasNext()) {
            DataRow row = rows.readRow();
            Object[] values = row.getValues();
            StudentInfo studentInfo = new StudentInfo();

            studentInfo.ignored = String.valueOf(values[0]).equalsIgnoreCase("X");
            studentInfo.num = String.valueOf(values[1]);
            if (studentInfo.num.length() > 0) {
                studentInfo.id = String.valueOf(values[2]);
                studentInfo.nom = String.valueOf(values[3]);
                studentInfo.prenom = String.valueOf(values[4]);
                studentInfo.moyg = RankingTools.asDouble((values[5]));
                studentInfo.rank = RankingTools.asInt((values[6]));
                studentInfo.options = new StudentOption[getNbOptions()];
                int col = 7;
                for (int i = 0; i < getNbOptions(); i++) {
                    studentInfo.options[i] = new StudentOption();
                    studentInfo.options[i].optionNumber = i + 1;
                    double[] doubles = new double[getNbModulesByOption(i)];
                    studentInfo.options[i].moyg = doubles;
                    for (int j = 0; j < getNbModulesByOption(i); j++) {
                        doubles[j] = RankingTools.asDouble(values[col++]);
                    }
                }

                studentInfo.result = StudentResult.valueOf(String.valueOf(values[col++]));
                studentInfo.redoublement = String.valueOf(values[col++]).equals("R");
                studentInfo.master = RankingTools.asDouble(values[col++]);
                int[] cc = new int[getNbOptions()];
                for (int i = 0; i < getNbOptions(); i++) {
                    cc[i] = RankingTools.asInt(values[col++]);
                }
                studentInfo.fixedChoice = String.valueOf(values[col++]).equals("1");

                studentInfo.choice = new int[getNbOptions()];
//                        System.out.println(cc[0] + ";" + cc[1]+ ";" + cc[2]);
//                        System.out.println("");
                for (int i = 0; i < getNbOptions(); i++) {
                    if (cc[i] > 0) {
                        studentInfo.choice[i] = cc[i];
//                                studentInfo.choice[cc[i] - 1] = i + 1;
                    }
                }
                int[] cc2 = studentInfo.choice;
                if (!Arrays.equals(cc, cc2)) {
                    System.out.println(studentInfo.prenom + " " + studentInfo.nom + " : " + cc[0] + ";" + cc[1] + ";" + cc[2] + " ==> " + cc2[0] + ";" + cc2[1] + ";" + cc2[2]);
                }
                if (!studentInfo.ignored) {
                    liste.add(studentInfo);
                } else {
                    System.err.println("Ignored " + studentInfo);
                }
            }
        }
        return liste;
    }

    public void evaluateAssignments() {
        try {
            final List<StudentInfo> allStudents = loadStudents2();
            List<StudentInfo> liste = new ArrayList<>();
            List<StudentInfo> fixed = new ArrayList<>();
            for (StudentInfo s : allStudents) {
                if (s.fixedChoice) {
                    s.assignment = s.choice[0];
                    s.satisfaction = 1;
                    fixed.add(s);
                } else {
                    liste.add(s);
                }
            }

            avgMoygByOption = new double[getNbOptions()][];
            for (int i = 0; i < getNbOptions(); i++) {
                avgMoygByOption[i] = new double[getNbModulesByOption(i)];
            }
            for (StudentInfo studentInfo : liste) {
                for (int i = 0; i < getNbOptions(); i++) {
                    for (int j = 0; j < getNbModulesByOption(i); j++) {
                        avgMoygByOption[i][j] += studentInfo.options[i].moyg[j];
                    }
                }
            }
            for (int i = 0; i < getNbOptions(); i++) {
                for (int j = 0; j < getNbModulesByOption(i); j++) {
                    avgMoygByOption[i][j] /= liste.size();
                }
            }
            for (StudentInfo studentInfo : liste) {
                for (int i = 0; i < getNbOptions(); i++) {
                    studentInfo.options[i].score = evalOptionScore(studentInfo, i);
                }
            }

            for (int op = 0; op < getNbOptions(); op++) {
                StudentInfo[] studentInfos = RankingTools.sortCopy(StudentInfo.class, liste, new StudentInfoByChoiceScoreComparator(op + 1));
                for (int i = 0; i < studentInfos.length; i++) {
                    studentInfos[i].options[op].rankEff = i + 1;
                }
            }

//            int maxOption = (int) Math.floor(liste.size() / getNbOptions());
            int rest = liste.size();
            for (int c : maxOptions) {
                rest -= c;
            }
            final ChoiceInfo[] optionsInfos = new ChoiceInfo[getNbOptions()];
            for (int i = 0; i < getNbOptions(); i++) {
                optionsInfos[i] = new ChoiceInfo();
                optionsInfos[i].number = i + 1;
                optionsInfos[i].count = maxOptions[i];
                optionsInfos[i].demands = new int[getNbOptions()];
                for (int j = 0; j < getNbOptions(); j++) {
                    optionsInfos[i].demands[j] = getNbChoix(liste, optionsInfos[i].number, j + 1);
                }
            }

            Arrays.sort(optionsInfos, new Comparator<ChoiceInfo>() {
                @Override
                public int compare(ChoiceInfo o1, ChoiceInfo o2) {
                    for (int i = 0; i < optionsInfos.length; i++) {
                        int x = o2.demands[i] - o1.demands[i];
                        if (x != 0) {
                            return x;
                        }
                    }
                    return 0;
                }
            });

            for (ChoiceInfo optionsInfo : optionsInfos) {
                System.out.println(">> " + optionsInfo);
            }
            while (rest > 0) {
                for (ChoiceInfo optionsInfo : optionsInfos) {
                    if (rest > 0) {
                        optionsInfo.count++;
                        rest--;
                    }
                }
            }

            for (int i = 0; i < getNbOptions(); i++) {
                optionsInfos[i].available = optionsInfos[i].count;
            }

            System.out.println("Available Options");
            for (ChoiceInfo optionsInfo : optionsInfos) {
                System.out.println(">> " + optionsInfo + " == > " + getAssignments(liste, optionsInfo.number));
            }
//            Collections.sort(liste, new Comparator<StudentInfo>() {
//
//                @Override
//                public int compare(StudentInfo o1, StudentInfo o2) {
//                    int x = getEffectiveRank(o1) - getEffectiveRank(o2);
//                    if (x != 0) {
//                        return x;
//                    }
////                    x = o1.rank - o2.rank;
////                    if (x != 0) {
////                        return x;
////                    }
//
//                    x = o1.nom.compareTo(o2.nom);
//                    if (x != 0) {
//                        return x;
//                    }
//                    x = o1.prenom.compareTo(o2.prenom);
//                    if (x != 0) {
//                        return x;
//                    }
//
//                    return x;
//                }
//            });

            Collections.sort(liste, STUDENT_MERITE_COMPARATOR);
            for (int i = 0; i < liste.size(); i++) {
                liste.get(i).rankEff = i + 1;
            }

            doAssignmentsByOptionScore(liste, optionsInfos);

            System.out.println("Available Options");
            for (ChoiceInfo optionsInfo : sortedByNameChoiceInfos) {
                System.out.println(">> " + optionsInfo + " == > " + getAssignments(liste, optionsInfo.number));
            }

            Collections.sort(liste, new Comparator<StudentInfo>() {

                @Override
                public int compare(StudentInfo o1, StudentInfo o2) {
                    int x = o1.assignment - o2.assignment;
                    if (x != 0) {
                        return x;
                    }
                    x = o1.nom.compareTo(o2.nom);
                    if (x != 0) {
                        return x;
                    }
                    x = o1.prenom.compareTo(o2.prenom);
                    if (x != 0) {
                        return x;
                    }
                    return 0;
                }
            });

            SheetFormatter frm = pfm.createSheetFormatter(new File(file_result.replace("${user.home}", System.getProperty("user.home"))));
            for (String c : new String[]{"NUM", "ID", "NOM", "PRENOM", "MOYG", "Score1", "Score2", "Score3", "Rang1", "Rang2", "Rang3", "SCORE" //                    , "MASTER"
                ,
                 "RANG", "RATTR", "REDOUBL", "Opt 1er Choix", "Opt 2eme Choix", "Option 3eme Choix", "AFFECTATION Option", "SATISFACTION", "FIXED"}) {
                SheetColumn cc = new SheetColumn();
                cc.setName(c);
                frm.getColumns().add(cc);
            }
            frm.setContentType(SheetContentType.XLSX);
            frm.setWriteHeader(true);
            DataWriter w = frm.createWriter();
            for (StudentInfo a : liste) {
                w.writeRow(new Object[]{
                    a.num,
                    a.id,
                    a.nom,
                    a.prenom,
                    a.moyg,
                    a.options[0].score,
                    a.options[1].score,
                    a.options[2].score,
                    a.options[0].rankEff,
                    a.options[1].rankEff,
                    a.options[2].rankEff,
                    evalGlobalScore(a),
                    //                        a.master,
                    a.rankEff,
                    a.result == StudentResult.A ? "" : a.result,
                    a.redoublement ? "R" : "",
                    a.choice[0],
                    a.choice[1],
                    a.choice[2],
                    a.assignment,
                    a.satisfaction,
                    a.fixedChoice ? "F" : ""
                });
            }
            for (StudentInfo a : fixed) {
                w.writeRow(new Object[]{
                    a.num,
                    a.id,
                    a.nom,
                    a.prenom,
                    a.moyg,
                    a.options[0].score,
                    a.options[1].score,
                    a.options[2].score,
                    a.options[0].rankEff,
                    a.options[1].rankEff,
                    a.options[2].rankEff,
                    evalGlobalScore(a),
                    //                        a.master,
                    a.rankEff,
                    a.result == StudentResult.A ? "" : a.result,
                    a.redoublement ? "R" : "",
                    a.choice[0],
                    a.choice[1],
                    a.choice[2],
                    a.assignment,
                    a.satisfaction,
                    a.fixedChoice ? "F" : ""
                });
            }
            w.close();
        } catch (IOException ex) {
            Logger.getLogger(CourseOptionsRanking_IA2018.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doAssignmentsByGlobalScore(List<StudentInfo> liste, ChoiceInfo[] optionsInfos) {
//now starting
        List<StudentInfo> notChoosen = new ArrayList<>();
        //sort
        for (StudentInfo s : liste) {
            boolean choiceFound = false;
            for (int i = 0; i < getNbOptions(); i++) {
                if (s.choice[i] > 0) {
                    ChoiceInfo cc = findChoiceInfo(optionsInfos, s.choice[i]);
                    if (cc.available > 0) {
                        doAssignStudent(s, cc);
                        choiceFound = true;
                        break;
                    }
                }
            }
            if (!choiceFound) {
                notChoosen.add(s);
            }
        }

//        System.out.println("Available Options (2)");
//        sortedByNameChoiceInfos.addAll(Arrays.asList(optionsInfos));
//        for (ChoiceInfo optionsInfo : sortedByNameChoiceInfos) {
//            System.out.println(">> " + optionsInfo + " == > " + getAssignments(liste, optionsInfo.number));
//        }
        Arrays.sort(optionsInfos, new Comparator<ChoiceInfo>() {
            @Override
            public int compare(ChoiceInfo o1, ChoiceInfo o2) {
                return o2.available - o1.available;
            }
        });

        for (StudentInfo s : notChoosen) {
            System.err.println("Evaluating with no choice " + s);
            for (ChoiceInfo sa : optionsInfos) {
                if (sa.available > 0) {
                    doAssignStudent(s, sa);
                    break;
                }
            }
        }
    }

    private void doAssignStudent(StudentInfo s, ChoiceInfo choice) {
        if (choice.available <= 0) {
            throw new IllegalArgumentException("Impossible");
        }
        s.assignment = choice.number;
        choice.available--;

        for (int i = 0; i < s.choice.length; i++) {
            if (s.assignment == s.choice[i]) {
                s.satisfaction = i + 1;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Assignment ").append(s.prenom).append(" ").append(s.nom);
        sb.append(" , rang ").append(s.options[choice.number - 1].rankEff).append("/").append(s.rankEff);
        sb.append("  opt ").append(choice.number);
        sb.append(" ; choix ").append(Arrays.toString(s.choice));
        sb.append(" ; ").append(s.satisfaction == 1 ? "1er" : s.satisfaction == 2 ? "2eme" : (s.satisfaction + "eme")).append(" choix : ").append(s.assignment);
        sb.append(" ; ").append(choice.available).append("/").append(choice.count);

        System.out.println(sb);
    }

    private void doAssignmentsByOptionScore(List<StudentInfo> liste, ChoiceInfo[] optionsInfos) {
        StudentInfo[][] byOpt = new StudentInfo[getNbOptions()][];
        int[] indexes = new int[getNbOptions()];
        int[] indexeMaxs = new int[getNbOptions()];
        for (int i = 0; i < getNbOptions(); i++) {
            byOpt[i] = RankingTools.sortCopy(StudentInfo.class, liste, new StudentInfoByChoiceScoreComparator(i + 1));
            indexeMaxs[i] = byOpt[i].length;
        }

        for (int choiceIndex = 0; choiceIndex < getNbOptions(); choiceIndex++) {
            for (int i = 0; i < getNbOptions(); i++) {
                indexes[i] = 0;
            }
            while (true) {
                int err = 0;
                int bestIndex = Integer.MAX_VALUE;
                int bestIndexOption = Integer.MAX_VALUE;
                for (int i = 0; i < getNbOptions(); i++) {
                    if (indexes[i] >= indexeMaxs[i]) {
                        err++;
                    } else {
                        if (indexes[i] < bestIndex) {
                            bestIndex = indexes[i];
                            bestIndexOption = i;
                        }
                    }
                }
                if (err == 3) {
                    break;
                }
                int index = indexes[bestIndexOption];
                indexes[bestIndexOption]++;
                StudentInfo studentInfo = byOpt[bestIndexOption][index];
                if (studentInfo.assignment == 0) {
                    if (optionsInfos[bestIndexOption].available > 0) {
                        if (studentInfo.choice[choiceIndex] == optionsInfos[bestIndexOption].number) {
                            doAssignStudent(studentInfo, optionsInfos[bestIndexOption]);
                        }
                    }
                }
            }
        }
        for (StudentInfo s : liste) {
            for (int i = 1; i < s.choice.length; i++) {
                if (s.assignment == s.choice[i]) {
                    for (int j = 0; j < i; j++) {
                        for (StudentInfo s2 : liste) {
                            if (s2.assignment == s.choice[j] && s2.options[j].score < s.options[j].score) {
                                System.out.println("Madhloum " + s.getStudentName() + " <= " + s2.getStudentName() + " for option " + s.choice[j] + " with score " + s.options[j].score);
                            }
                        }
                    }
                }
            }
        }
    }

}
