/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.stat;

/**
 *
 * @author vpc
 */
public class LoadValue {

    private double c;
    private double td;
    private double tp;
    private double pm;
    private double equiv;
    /**
     * TP and PM in TP equivalent
     */
    private double tppm;

    public LoadValue() {
    }

    public LoadValue(double c, double td, double tp, double pm, double equiv, double tppm) {
        this.c = c;
        this.td = td;
        this.tp = tp;
        this.pm = pm;
        this.equiv = equiv;
        this.tppm = tppm;
    }

    public LoadValue set(LoadValue o) {
        c = o.getC();
        td = o.getTd();
        tp = o.getTp();
        pm = o.getPm();
        tppm = o.getTppm();
        equiv = o.getEquiv();
        return this;
    }

    public LoadValue add(LoadValue o) {
        c += o.getC();
        td += o.getTd();
        tp += o.getTp();
        pm += o.getPm();
        tppm += o.getTppm();
        equiv += o.getEquiv();
        return this;
    }
    
    public LoadValue mul(LoadValue o) {
        c *= o.getC();
        td *= o.getTd();
        tp *= o.getTp();
        pm *= o.getPm();
        tppm *= o.getTppm();
        equiv *= o.getEquiv();
        return this;
    }
    
    public LoadValue mul(double o) {
        c *= o;
        td *= o;
        tp *= o;
        pm *= o;
        tppm *= o;
        equiv *= o;
        return this;
    }
    
    public LoadValue div(double o) {
        c /= o;
        td /= o;
        tp /= o;
        pm /= o;
        tppm /= o;
        equiv /= o;
        return this;
    }

    public LoadValue substruct(LoadValue o) {
        c -= o.getC();
        td -= o.getTd();
        tp -= o.getTp();
        pm -= o.getPm();
        tppm -= o.getTppm();
        equiv -= o.getEquiv();
        return this;
    }

    public LoadValue copy() {
        return new LoadValue(c, td, tp, pm, equiv, tppm);
    }

    public double getC() {
        return c;
    }

    public LoadValue setC(double c) {
        this.c = c;
        return this;
    }

    public double getTd() {
        return td;
    }

    public LoadValue setTd(double td) {
        this.td = td;
        return this;
    }

    public double getTp() {
        return tp;
    }

    public LoadValue setTp(double tp) {
        this.tp = tp;
        return this;
    }

    public double getPm() {
        return pm;
    }

    public LoadValue setPm(double pm) {
        this.pm = pm;
        return this;
    }

    public double getTppm() {
        return tppm;
    }

    public LoadValue setTppm(double tppm) {
        this.tppm = tppm;
        return this;
    }

    public double getEquiv() {
        return equiv;
    }

    public LoadValue setEquiv(double equiv) {
        this.equiv = equiv;
        return this;
    }

    @Override
    public String toString() {
        return "Value{" + "c=" + c + ", td=" + td + ", tp=" + tp + ", pm=" + pm + ", equiv=" + equiv + ", tppm=" + tppm + '}';
    }
    

}
