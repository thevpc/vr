/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.upa.UPA;
import net.vpc.upa.bulk.DataReader;
import net.vpc.upa.bulk.DataRow;
import net.vpc.upa.bulk.DataWriter;
import net.vpc.upa.bulk.ParseFormatManager;
import net.vpc.upa.bulk.SheetColumn;
import net.vpc.upa.bulk.SheetContentType;
import net.vpc.upa.bulk.SheetFormatter;
import net.vpc.upa.bulk.SheetParser;

/**
 *
 * @author vpc
 */
public class CourseOptionsRanking {

    private static ParseFormatManager pfm = UPA.getBootstrapFactory().createObject(ParseFormatManager.class);
    private static int nbOptions = 3;

//    private static String file_principal = "/home/vpc/Data/eniso/students/2015-2016/choix-options/IA/2IAPRINC.xlsx";
//    private static String file_rattrapage = "/home/vpc/Data/eniso/students/2015-2016/choix-options/IA/2IARATT.xlsx";
//    private static String file_intermediaire = "/home/vpc/Data/eniso/students/2015-2016/choix-options/IA/2IA-FOR-OPTION.xlsx";
//    private static String file_result = "/home/vpc/Data/eniso/students/2015-2016/choix-options/IA/2IA-OPTIONS-FINAL.xlsx";

    private static String file_principal = "/home/vpc/Data/eniso/students/2015-2016/choix-options/EI/EI2Princip.xlsx";
    private static String file_rattrapage = "/home/vpc/Data/eniso/students/2015-2016/choix-options/EI/2EIRatt.xlsx";
    private static String file_intermediaire = "/home/vpc/Data/eniso/students/2015-2016/choix-options/EI/2EI-FOR-OPTION.xlsx";
    private static String file_result = "/home/vpc/Data/eniso/students/2015-2016/choix-options/EI/2EI-OPTIONS-FINAL.xlsx";

    public static void main(String[] args) {
//        generateOptionsFile();
        evaluateAssignements();
    }

    private static class ChoiceInfo {

        int number;
        int count;
        int available;
        int[] demands;

        @Override
        public String toString() {
            return "ChoiceInfo{" + "number=" + number + ", count=" + count + ", available=" + available + ", demands=" + (demands == null ? "" : Arrays.toString(demands)) + '}';
        }

    }

    private static class StudentInfo {

        String num;
        String id;
        String nom;
        String prenom;
        double moyg;
        String result;
        int rank;
        boolean rattr;
        int master;
        int[] choice;
        int assignement;
        boolean ignored;

        @Override
        public String toString() {
            return "StudentInfo{" + "num=" + num + ", id=" + id + ", nom=" + nom + ", prenom=" + prenom
                    + ", moyg=" + moyg + ", result=" + result + ", rank=" + rank + "/" + getEffectiveRank(this)
                    + ", assignment=" + assignement
                    + '}';
        }

    }

    public static List<StudentInfo> listPrincipal() {
        return listStudents(new java.io.File(file_principal), false);
    }

    public static List<StudentInfo> listRattr() {
        return listStudents(new java.io.File(file_rattrapage), true);
    }

    public static List<StudentInfo> listStudents(File f, boolean rattr) {
        List<StudentInfo> principal = new ArrayList<StudentInfo>();
        try {
            SheetParser sp = pfm.createSheetParser(f);
            sp.setContainsHeader(true);
            DataReader rows = sp.parse();
            while (rows.hasNext()) {
                DataRow row = rows.readRow();
                Object[] values = row.getValues();
                StudentInfo studentInfo = new StudentInfo();
                studentInfo.num = String.valueOf(values[0]);
                if (studentInfo.num.length() > 0) {
                    studentInfo.id = String.valueOf(values[1]);
                    studentInfo.nom = String.valueOf(values[2]);
                    studentInfo.prenom = String.valueOf(values[3]);
                    studentInfo.moyg = Double.parseDouble(String.valueOf(values[4]));
                    studentInfo.result = String.valueOf(values[5]);
                    studentInfo.rattr = rattr;
                    principal.add(studentInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return principal;
    }

    public static void generateOptionsFile() {
        try {
            List<StudentInfo> principal = listPrincipal();
            List<StudentInfo> rattr = listRattr();
            List<StudentInfo> all = new ArrayList<>();
            for (StudentInfo p : principal) {
                if (p.result.equalsIgnoreCase("A")) {
                    //admis ok
                    all.add(p);
                } else if (p.result.equalsIgnoreCase("C")) {
                    //look into rattr
                    StudentInfo found = null;
                    for (StudentInfo r : rattr) {
                        if (r.num.equals(p.num)) {
                            found = r;
                            break;
                        }
                    }
                    if (found == null) {
                        //ignore it
                        System.err.println("Ignored Student : NO RATTR record " + p);
                    } else {
                        if (found.result.equalsIgnoreCase("A")) {
                            all.add(found);
                        } else {
                            System.err.println("Ignored Student : result='" + found.result + "' :  " + p);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Impossible : result found " + p.result + " :: " + p);
                }
            }
            Collections.sort(all, new Comparator<StudentInfo>() {

                @Override
                public int compare(StudentInfo o1, StudentInfo o2) {
                    if (o1.rattr != o2.rattr) {
                        if (o1.rattr) {
                            return 1;
                        } else if (o2.rattr) {
                            return -1;
                        }
                    }
                    int v = -Double.compare(o1.moyg, o2.moyg);
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
            });
            for (int i = 0; i < all.size(); i++) {
                if (i > 0) {
                    if (all.get(i).moyg == all.get(i - 1).moyg) {
                        all.get(i).rank = all.get(i - 1).rank;
                    } else {
                        all.get(i).rank = i + 1;
                    }
                } else {
                    all.get(i).rank = i + 1;
                }
            }
            SheetFormatter frm = pfm.createSheetFormatter(new java.io.File(file_intermediaire));
            for (String c : new String[]{"NUM", "ID", "NOM", "PRENOM", "MOYG", "RANG", "RATTR", "MASTER", "CHOIX1", "CHOIX2", "CHOIX3", "AFFECTATION"}) {
                SheetColumn cc = new SheetColumn();
                cc.setName(c);
                frm.getColumns().add(cc);
            }
            frm.setContentType(SheetContentType.XLSX);
            frm.setWriteHeader(true);
            DataWriter w = frm.createWriter();
            for (StudentInfo a : all) {
                w.writeRow(new Object[]{
                    a.num,
                    a.id,
                    a.nom,
                    a.prenom,
                    a.moyg,
                    a.rank,
                    a.rattr ? "R" : "P",
                    "",
                    "",
                    "",
                    "",
                    ""
                });
            }
            w.close();
        } catch (Exception ex) {
            Logger.getLogger(CourseOptionsRanking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void evaluateAssignements() {
        try {
            final List<StudentInfo> liste = new ArrayList<StudentInfo>();
            try {
                SheetParser sp = pfm.createSheetParser(new java.io.File(file_intermediaire));
                sp.setContainsHeader(true);
                DataReader rows = sp.parse();
                while (rows.hasNext()) {
                    DataRow row = rows.readRow();
                    Object[] values = row.getValues();
                    StudentInfo studentInfo = new StudentInfo();
                    studentInfo.num = String.valueOf(values[0]);
                    if (studentInfo.num.length() > 0) {
                        studentInfo.id = String.valueOf(values[1]);
                        studentInfo.nom = String.valueOf(values[2]);
                        studentInfo.prenom = String.valueOf(values[3]);
                        studentInfo.moyg = Double.parseDouble(String.valueOf(values[4]));
                        studentInfo.rank = asInt(values[5]);
                        studentInfo.rattr = String.valueOf(values[6]).equals("R");
                        studentInfo.master = asInt(values[7]);
                        studentInfo.ignored = String.valueOf(values[8]).equalsIgnoreCase("X");
                        int[] cc = new int[nbOptions];
                        cc = new int[nbOptions];
                        for (int i = 0; i < nbOptions; i++) {
                            cc[i] = asInt(values[9 + i]);
                        }

                        studentInfo.choice = new int[nbOptions];
//                        System.out.println(cc[0] + ";" + cc[1]+ ";" + cc[2]);
//                        System.out.println("");
                        for (int i = 0; i < nbOptions; i++) {
                            if (cc[i] > 0) {
                                studentInfo.choice[cc[i] - 1] = i + 1;
                            }
                        }
                        int[] cc2 = studentInfo.choice;
//                        System.out.println(cc[0] + ";" + cc[1]+ ";" + cc[2] +" ==> "+cc2[0] + ";" + cc2[1]+ ";" + cc2[2]);
                        if (!studentInfo.ignored) {
                            liste.add(studentInfo);
                        } else {
                            System.err.println("Ignored " + studentInfo);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int maxOption = (int) Math.floor(liste.size() / nbOptions);
            int rest = liste.size() - nbOptions * maxOption;

            final ChoiceInfo[] optionsInfos = new ChoiceInfo[nbOptions];
            for (int i = 0; i < nbOptions; i++) {
                optionsInfos[i] = new ChoiceInfo();
                optionsInfos[i].number = i + 1;
                optionsInfos[i].count = maxOption;
                optionsInfos[i].demands = new int[nbOptions];
                for (int j = 0; j < nbOptions; j++) {
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
            for (ChoiceInfo optionsInfo : optionsInfos) {
                if (rest > 0) {
                    optionsInfo.count++;
                    rest--;
                }
            }
            for (int i = 0; i < nbOptions; i++) {
                optionsInfos[i].available = optionsInfos[i].count;
            }

            System.out.println("Available Options");
            for (ChoiceInfo optionsInfo : optionsInfos) {
                System.out.println(">> " + optionsInfo + " == > " + getAssignements(liste, optionsInfo.number));
            }
            Collections.sort(liste, new Comparator<StudentInfo>() {

                @Override
                public int compare(StudentInfo o1, StudentInfo o2) {
                    int x = getEffectiveRank(o1) - getEffectiveRank(o2);
                    if (x != 0) {
                        return x;
                    }
                    x = o1.rank - o2.rank;
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

                    return x;
                }
            });
            List<StudentInfo> notChoosen = new ArrayList<>();
            //sort
            for (StudentInfo s : liste) {
                boolean choiceFound = false;
                for (int i = 0; i < nbOptions; i++) {
                    if (s.choice[i] > 0) {
                        ChoiceInfo cc = findChoiceInfo(optionsInfos, s.choice[i]);
                        if (cc.available>0) {
                            s.assignement = s.choice[i];
                            cc.available--;
//                            int c = cc.available + getAssignements(liste, cc.number);
                            choiceFound = true;
                            System.out.println("Assignement " + (i + 1) + " for " + s + "  ==> " + cc);
                            break;
                        }
                    }
                }
                if (!choiceFound) {
                    notChoosen.add(s);
                }
            }

            System.out.println("Available Options (2)");
            for (ChoiceInfo optionsInfo : optionsInfos) {
                System.out.println(">> " + optionsInfo + " == > " + getAssignements(liste, optionsInfo.number));
            }

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
                        s.assignement = sa.number;
                        sa.available--;
                        break;
                    }
                }
            }

            System.out.println("Available Options");
            for (ChoiceInfo optionsInfo : optionsInfos) {
                System.out.println(">> " + optionsInfo + " == > " + getAssignements(liste, optionsInfo.number));
            }

            Collections.sort(liste,new Comparator<StudentInfo>() {

                @Override
                public int compare(StudentInfo o1, StudentInfo o2) {
                    int x=o1.assignement-o2.assignement;
                    if(x!=0){
                        return x;
                    }
                    x=o1.nom.compareTo(o2.nom);
                    if(x!=0){
                        return x;
                    }
                    x=o1.prenom.compareTo(o2.prenom);
                    if(x!=0){
                        return x;
                    }
                    return 0;
                }
            });
            SheetFormatter frm = pfm.createSheetFormatter(new java.io.File(file_result));
            for (String c : new String[]{"NUM", "ID", "NOM", "PRENOM", "MOYG", "RANG", "RATTR", "MASTER", "CHOIX1", "CHOIX2", "CHOIX3", "AFFECTATION"}) {
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
                    a.rank,
                    a.rattr ? "R" : "P",
                    a.master,
                    a.choice[0],
                    a.choice[1],
                    a.choice[2],
                    a.assignement
                });
            }
            w.close();
        } catch (IOException ex) {
            Logger.getLogger(CourseOptionsRanking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int getEffectiveRank(StudentInfo s) {
        int r = s.rank;
        int bonus = 0;
        for (int i = 0; i < s.choice.length; i++) {
            if (s.choice[i] == s.master) {
                bonus = s.choice.length - 1 - i;
            }
        }
        r = r - bonus;
        if (r < 1) {
            r = 1;
        }
        return r;
    }

    private static int getAssignements(List<StudentInfo> o, int choiceNumber) {
        int x = 0;
        for (StudentInfo o1 : o) {
            if (o1.assignement == choiceNumber) {
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

    private static int asInt(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        if (o instanceof String) {
            String ss = String.valueOf(o);
            ss = ss.trim();
            if (ss.equals("null")) {
                return 0;
            }
            if (ss.isEmpty()) {
                return 0;
            }
        }
        return Integer.parseInt(String.valueOf(o));
    }

    public static ChoiceInfo findChoiceInfo(ChoiceInfo[] optionsInfos, int number) {
        for (int i = 0; i < optionsInfos.length; i++) {
            if (optionsInfos[i].number == number) {
                return optionsInfos[i];
            }
        }
        throw new IllegalArgumentException("Never");
    }
}
