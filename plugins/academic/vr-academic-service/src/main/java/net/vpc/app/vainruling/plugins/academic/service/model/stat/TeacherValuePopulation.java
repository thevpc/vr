package net.vpc.app.vainruling.plugins.academic.service.model.stat;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vpc on 8/19/16.
 */
public class TeacherValuePopulation {
    private List<Double> values=new ArrayList<>();
    private AcademicTeacherSituation situation;
    private AcademicTeacherDegree degree;
    private AcademicOfficialDiscipline discipline;
    private double sum;
    private double average;
    private double median;
    private double variance;
    private double standardDeviation;

    public TeacherValuePopulation(AcademicTeacherSituation situation, AcademicTeacherDegree degree, AcademicOfficialDiscipline discipline) {
        this.situation = situation;
        this.degree = degree;
        this.discipline = discipline;
    }
    public void addValue(double d){
        values.add(d);
    }

    public void build(){
        Collections.sort(values);
        sum=0;
        average=0;
        median=0;
        variance=0;
        standardDeviation=0;
        if(values.size()>0){
            median=values.get(values.size()/2);
            for (Double value : values) {
                sum+=value;
            }
            average=sum/values.size();
            for (Double value : values) {
                variance+=Math.pow((value-average),2);
            }
            variance=variance/values.size();
            standardDeviation=Math.sqrt(variance);
        }
    }

    public int getSize() {
        return values.size();
    }

    public List<Double> getValues() {
        return values;
    }

    public double getSum() {
        return sum;
    }

    public double getAverage() {
        return average;
    }

    public double getMedian() {
        return median;
    }

    public double getVariance() {
        return variance;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }
}
