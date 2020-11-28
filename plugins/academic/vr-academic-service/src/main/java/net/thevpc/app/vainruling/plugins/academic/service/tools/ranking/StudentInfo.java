/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.tools.ranking;

/**
 *
 * @author vpc
 */
class StudentInfo {
    
    String num;
    String id;
    String nom;
    String prenom;
    double moyg;
    StudentOption[] options;
    double moyr;
    StudentResult result;
    int rank;
    int rankEff;
    double master;
    int masterPos;
    boolean redoublement;
    int[] choice;
    int assignment;
    int satisfaction;
    boolean ignored;
    boolean fixedChoice;

    public String getStudentName() {
        return prenom + " " + nom;
    }

    public int getBestRank(int nbOptions) {
        int r = options[0].rankEff;
        for (int i = 1; i < nbOptions; i++) {
            if (options[i].rankEff < r) {
                r = options[i].rankEff;
            }
        }
        return r;
    }

    @Override
    public String toString() {
        return "StudentInfo{" + "num=" + num + ", id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", moyg=" + moyg + ", result=" + result + ", rank=" + rank + "/" + getEffectiveRank() + ", assignment=" + assignment + '}';
    }
    
    
    public int getEffectiveRank() {
        int r = rank;
        int bonus = 0;
        if(choice==null){
            return  -1;
        }
        for (int i = 0; i < choice.length; i++) {
            if (choice[i] == masterPos) {
                bonus = choice.length - 1 - i;
            }
        }
        r = r - bonus;
        if (r < 1) {
            r = 1;
        }
        return r;
    }

}
