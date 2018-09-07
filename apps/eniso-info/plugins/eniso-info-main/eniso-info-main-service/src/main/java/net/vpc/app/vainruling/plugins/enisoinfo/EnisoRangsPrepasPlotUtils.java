/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.enisoinfo;

import java.util.Map;
import net.vpc.app.vainruling.plugins.enisoinfo.SchoolScorePlotUtils.Data;
import net.vpc.app.vainruling.plugins.enisoinfo.SchoolScorePlotUtils.DataKey;
import net.vpc.app.vainruling.plugins.enisoinfo.SchoolScorePlotUtils.DataSeries;

public class EnisoRangsPrepasPlotUtils {
    public static Map<DataKey, Data> data = SchoolScorePlotUtils.loadDataMapResource("/scores.txt");


    public static void main(String[] args) {
        y2018();
//        gte_all();
//        ia_all();
//        y2017GTE();
    }


    public static Data get(String program,String spec,int year){
        Data d = data.get(new DataKey(year, spec, program));
        if(d==null){
            throw new IllegalArgumentException("Not found "+program+" "+spec+" "+year);
        }
        return d;
    }

    public static void y2018() {
        double[] years = new double[]{2017, 2017};
        DataSeries MP = new DataSeries("MP", years, get("IA","MP",2018), get("GTE","MP",2018), get("MEC","MP",2018), get("EI","MP",2018), get("GMP","MP",2018));
        DataSeries PT = new DataSeries("PT", years, get("IA","PT",2018), get("GTE","PT",2018), get("MEC","PT",2018), get("EI","PT",2018), get("GMP","PT",2018));
//        DataSeries PT = new DataSeries("PT", years, get("IA","PT",2017), get("GTE","PT",2017));
        SchoolScorePlotUtils.show(MP, PT);
    }
    public static void y2017() {
        double[] years = new double[]{2017, 2017};
        DataSeries MP = new DataSeries("MP", years, get("IA","MP",2017), get("GTE","MP",2017));
        DataSeries PT = new DataSeries("PT", years, get("IA","PT",2017), get("GTE","PT",2017));
        SchoolScorePlotUtils.show(MP, PT);
    }

    public static void y2017GTE() {
        double[] years = new double[]{2017, 2017};
        DataSeries MP = new DataSeries("MP", years, get("GTE","MP",2017));
        DataSeries PT = new DataSeries("PT", years, get("GTE","PT",2017));
        SchoolScorePlotUtils.show(MP, PT);
    }

    public static void ia15() {
//        IA_MP_2013,IA_MP_2014,
//        IA_PT_2013,IA_PT_2014,
//        2013,2014,
        double[] years = new double[]{2015, 2016, 2017, 2017,2018};
        DataSeries MP = new DataSeries("MP", years, get("IA","MP",2015), get("IA","MP",2016), get("IA","MP",2017), get("IA","MP",2018));
        DataSeries PT = new DataSeries("PT", years, get("IA","PT",2015), get("IA","PT",2016), get("IA","PT",2017), get("IA","PT",2018));
        SchoolScorePlotUtils.show(MP, PT);
    }

    public static void ia_all() {
        double[] years = new double[]{2014, 2015, 2016, 2017, 2018};
        DataSeries MP = new DataSeries("MP", years, get("IA","MP",2014), get("IA","MP",2015), get("IA","MP",2016), get("IA","MP",2017), get("IA","MP",2018));//, GTE_MP_2017
        DataSeries PT = new DataSeries("PT", years, get("IA","PT",2014), get("IA","PT",2015), get("IA","PT",2016), get("IA","PT",2017), get("IA","PT",2017));//, GTE_PT_2017
        SchoolScorePlotUtils.show(MP, PT);
    }
    
    public static void gte_all() {
        double[] years = new double[]{2017, 2018};
        DataSeries MP = new DataSeries("MP", years, get("GTE","MP",2017), get("GTE","MP",2018));
        DataSeries PT = new DataSeries("PT", years, get("GTE","PT",2017), get("GTE","PT",2017));
        SchoolScorePlotUtils.show(MP, PT);
    }


}
