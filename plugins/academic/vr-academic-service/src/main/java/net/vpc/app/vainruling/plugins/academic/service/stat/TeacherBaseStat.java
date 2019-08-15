/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.stat;

import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicTeacherDegree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class TeacherBaseStat {
    private LoadValue value = new LoadValue();
    private LoadValue valueWeek = new LoadValue();
    private LoadValue extraWeek = new LoadValue();
    /**
     * heures supp en absolu, pour un MA la charge en heure supp TD
     * pourrait rester negative (par exemple toute sa charge est en cours)
     */
    private LoadValue extra = new LoadValue();
    /**
     * heures supp pares balance des charges
     * par exemple pour un MA si l'heure supp TD est négative
     * et que l'heure supp C est positive, balancer une partie
     * des heures supp C vers le cours pour combler la charge
     */
    private LoadValue extraEff = new LoadValue();

    private LoadValue extraWeekEff = new LoadValue();
    private LoadValue due = new LoadValue();
    private LoadValue dueWeek = new LoadValue();
    private List<AcademicCourseAssignmentInfo> assignments=new ArrayList<>();
    private double weeks;
    private double maxWeeks;
    private DeviationConfig config=new DeviationConfig();
    private TeacherValuePopulation population;

    public LoadValue getExtraWeekEff() {
        return extraWeekEff;
    }

    public LoadValue getExtraEff() {
        return extraEff;
    }

    public LoadValue getExtra() {
        return extra;
    }

    public void setExtra(LoadValue extra) {
        this.extra = extra;
    }

    public LoadValue getDue() {
        return due;
    }

    public void setDue(LoadValue due) {
        this.due = due;
    }


    public LoadValue getValue() {
        return value;
    }

    public void setValue(LoadValue value) {
        this.value = value;
    }

    public double getWeeks() {
        return weeks;
    }

    public void setWeeks(double weeks) {
        this.weeks = weeks;
    }

    public double getMaxWeeks() {
        return maxWeeks;
    }

    public void setMaxWeeks(double maxWeeks) {
        this.maxWeeks = maxWeeks;
    }

    public LoadValue getExtraWeek() {
        return extraWeek;
    }

    public void setExtraWeek(LoadValue extraWeek) {
        this.extraWeek = extraWeek;
    }

    public LoadValue getDueWeek() {
        return dueWeek;
    }

    public void setDueWeek(LoadValue dueWeek) {
        this.dueWeek = dueWeek;
    }

    public LoadValue getValueWeek() {
        return valueWeek;
    }

    public abstract AcademicTeacher getTeacher();

    public TeacherValuePopulation getPopulation() {
        return population;
    }

    public void setPopulation(TeacherValuePopulation population) {
        this.population = population;
    }

    public String getDeviationLabel(){
        double d = getDeviation();
        if(d>=0){
          if(d<=0.2){
              return "valid3";
          }
          if(d<=0.4){
              return "warn3";
          }
          return "invalid3";
        }else{
            if(-d<=0.2){
                return "valid";
            }
            if(-d<=0.4){
                return "warn";
            }
            return "invalid";
        }
    }
    public double getDeviationBaseValue(){
        if(config.isExtraBased()){
            if(config.isWeekBased()){
                return getExtraWeek().getEquiv();
            }else{
                return getExtra().getEquiv();
            }
        }else{
            if(config.isWeekBased()){
                return getValueWeek().getEquiv();
            }else{
                return getValue().getEquiv();
            }
        }
    }

    public double getDeviation(){
        double equiv = getDeviationBaseValue();

        if(population!=null){
            double average = population.getAverage();
            if(average==0){
                return equiv;
            }
            return (equiv/*- average*/)/Math.abs(average);
        }
        return equiv;
    }

    public DeviationConfig getConfig() {
        return config;
    }

    public void setConfig(DeviationConfig config) {
        this.config = config;
    }

    public List<AcademicCourseAssignmentInfo> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AcademicCourseAssignmentInfo> assignments) {
        this.assignments = assignments;
    }

    public abstract AcademicTeacherSituation getTeacherSituation() ;

    public abstract AcademicTeacherDegree getTeacherDegree() ;
}
