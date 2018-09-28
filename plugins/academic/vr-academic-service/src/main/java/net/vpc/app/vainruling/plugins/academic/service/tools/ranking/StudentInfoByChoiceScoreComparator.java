/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.tools.ranking;

import java.util.Comparator;

//    private class MultiList{

//        List<List<StudentInfo>> list=new ArrayList<>();
//        private void add(List<StudentInfo> list){
//            this.list.add(list);
//        }
//        public StudentInfo next(){
//            double bestScore=Double.NaN;
//            int bestScoreIndex=-1;
//            StudentInfo student=null;
//            for (int i = 0; i < list.size(); i++) {
//                List<StudentInfo> li = list.get(i);
//                if(li.size()>0) {
//                    if (bestScoreIndex < 0 || li.get(0).options[i].score>bestScore){
//                        student=li.get(0);
//                        bestScoreIndex=i;
//                        bestScore=li.get(0).options[i].score;
//                    }
//                }
//            }
//            if(student!=null){
//                list.get(bestScoreIndex).remove(0);
//            }
//            return student;
//        }
//    }
//
//    private void doAssignmentsByOptionScore2(List<StudentInfo> liste, ChoiceInfo[] optionsInfos) {
//        List<StudentInfo>[] byOpt = new List[nbOptions];
//        int[] indexes = new int[nbOptions];
//        int[] indexeMaxs = new int[nbOptions];
//        for (int i = 0; i < nbOptions; i++) {
//            byOpt[i] = new ArrayList<>(Arrays.asList(sortCopy(StudentInfo.class, liste, new StudentInfoByChoiceScoreComparator(i + 1))));
//            indexeMaxs[i] = byOpt[i].size();
//        }
//        while()
//
//        for (int choiceIndex = 0; choiceIndex < nbOptions; choiceIndex++) {
//            for (int i = 0; i < nbOptions; i++) {
//                indexes[i] = 0;
//            }
//            while (true) {
//                int err = 0;
//                int bestIndex = Integer.MAX_VALUE;
//                int bestIndexOption = Integer.MAX_VALUE;
//                for (int i = 0; i < nbOptions; i++) {
//                    if (indexes[i] >= indexeMaxs[i]) {
//                        err++;
//                    } else {
//                        if (indexes[i] < bestIndex) {
//                            bestIndex = indexes[i];
//                            bestIndexOption = i;
//                        }
//                    }
//                }
//                if (err == 3) {
//                    break;
//                }
//                int index = indexes[bestIndexOption];
//                indexes[bestIndexOption]++;
//                StudentInfo studentInfo = byOpt[bestIndexOption][index];
//                if (studentInfo.assignment == 0) {
//                    if (optionsInfos[bestIndexOption].available > 0) {
//                        if (studentInfo.choice[choiceIndex] == optionsInfos[bestIndexOption].number) {
//                            doAssignStudent(studentInfo, optionsInfos[bestIndexOption]);
//                        }
//                    }
//                }
//            }
//        }
//    }
class StudentInfoByChoiceScoreComparator implements Comparator<StudentInfo> {

    private final int finalOp;

    public StudentInfoByChoiceScoreComparator(int finalOp) {
        this.finalOp = finalOp;
    }

    @Override
    public int compare(StudentInfo o1, StudentInfo o2) {
        return -Double.compare(o1.options[finalOp - 1].score, o2.options[finalOp - 1].score);
    }
    
}
