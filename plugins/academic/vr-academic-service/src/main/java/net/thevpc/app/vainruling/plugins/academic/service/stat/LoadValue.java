/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.stat;

import java.text.DecimalFormat;

/**
 * @author taha.bensalah@gmail.com
 */
public class LoadValue {

    private static final DecimalFormat FORMAT1 = new DecimalFormat("#0.000");
    private static final DecimalFormat FORMAT2 = new DecimalFormat("0.000");
    private double c;
    private double td;
    private double tp;
    private double pm;

    private double equiv;
    /**
     * TP and PM in TP equivalent
     */
    private double equivC;
    private double equivTD;

    private double tdtppm;
    private double tppm;

    public LoadValue() {
    }

    public LoadValue(double c, double td, double tp, double pm) {
        this.c = c;
        this.td = td;
        this.tp = tp;
        this.pm = pm;
    }

    public double getF2f() {
        return c + td + tp;
    }

    public double getNonF2f() {
        return pm;
    }

    private String formatF(double d) {
        double r = ((int) d);
        if (r == d) {
            return String.valueOf((int) d);
        }
        return FORMAT2.format(d);
    }

    public String formatString() {
        StringBuilder b = new StringBuilder();
        b.append(FORMAT1.format(equiv));
        boolean addSep = false;
        if (c != 0 || td != 0 || tp != 0 || pm != 0) {
            b.append("=");
            if (c != 0) {
                b.append(formatF(c)).append("C");
                addSep = true;
            }
            if (td != 0) {
                if (addSep) {
                    b.append(",");
                }
                b.append(formatF(td)).append("TD");
                addSep = true;
            }
            if (tp != 0) {
                if (addSep) {
                    b.append(",");
                }
                b.append(formatF(tp)).append("TP");
                addSep = true;
            }
            if (pm != 0) {
                if (addSep) {
                    b.append(",");
                }
                b.append(pm).append("PM");
                addSep = true;
            }

        }
        return b.toString();
    }

    public LoadValue set(LoadValue o) {
        c = o.getC();
        td = o.getTd();
        tp = o.getTp();
        pm = o.getPm();
        tppm = o.getTppm();
        equiv = o.getEquiv();
        equivC = o.getEquivC();
        equivTD = o.getEquivTD();
        return this;
    }

    public LoadValue add(LoadValue o) {
        c += o.getC();
        td += o.getTd();
        tp += o.getTp();
        pm += o.getPm();
        tppm += o.getTppm();
        equiv += o.getEquiv();
        equivC += o.getEquivC();
        equivTD += o.getEquivTD();
        return this;
    }

    public LoadValue mul(LoadValue o) {
        c *= o.getC();
        td *= o.getTd();
        tp *= o.getTp();
        pm *= o.getPm();
        tppm *= o.getTppm();
        equiv *= o.getEquiv();
        equivC *= o.getEquivC();
        equivTD *= o.getEquivTD();
        return this;
    }

    public LoadValue mul(double o) {
        c *= o;
        td *= o;
        tp *= o;
        pm *= o;
        tppm *= o;
        equiv *= o;
        equivC *= o;
        equivTD *= o;
        return this;
    }

    public LoadValue div(double o) {
        c /= o;
        td /= o;
        tp /= o;
        pm /= o;
        tppm /= o;
        equiv /= o;
        equivC /= o;
        equivTD /= o;
        return this;
    }

    public LoadValue substruct(LoadValue o) {
        c -= o.getC();
        td -= o.getTd();
        tp -= o.getTp();
        pm -= o.getPm();
        tppm -= o.getTppm();
        equiv -= o.getEquiv();
        equivC -= o.getEquivC();
        equivTD -= o.getEquivTD();
        return this;
    }

    public LoadValue copy() {

        LoadValue loadValue = new LoadValue(c, td, tp, pm);
        loadValue.equiv = equiv;
        loadValue.equivC = equivC;
        loadValue.equivTD = equivTD;
        loadValue.tdtppm = tdtppm;
        loadValue.tppm = tppm;
        //double tdtppm
        return loadValue;
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

    public double getEquivC() {
        return equivC;
    }

    public LoadValue setEquivC(double equivC) {
        this.equivC = equivC;
        return this;
    }

    public double getEquivTD() {
        return equivTD;
    }

    public LoadValue setEquivTD(double equivTD) {
        this.equivTD = equivTD;
        return this;
    }

    public double getTdtppm() {
        return tdtppm;
    }

    public LoadValue setTdtppm(double tdtppm) {
        this.tdtppm = tdtppm;
        return this;
    }

    @Override
    public String toString() {
        if (c == 0 && td == 0 && pm == 0 && tp == 0 && equiv == 0) {
            return "Value{0}";
        }
        StringBuilder sb = new StringBuilder();
        if (c != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("c=").append(FORMAT1.format(c));
        }
        if (td != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("td=").append(FORMAT1.format(td));
        }
        if (tp != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("tp=").append(FORMAT1.format(tp));
        }
        if (pm != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("pm=").append(FORMAT1.format(pm));
        }
        if (tppm != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("tppm=").append(FORMAT1.format(tppm));
        }
        if (tdtppm != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("tdtppm=").append(FORMAT1.format(tdtppm));
        }
        if (equiv != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("equiv=").append(FORMAT1.format(equiv));
        }
        sb.insert(0, "Value{");
        sb.append("}");
        return sb.toString();
    }

}
