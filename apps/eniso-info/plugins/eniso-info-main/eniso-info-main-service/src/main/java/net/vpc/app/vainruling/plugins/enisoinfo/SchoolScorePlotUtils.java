/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.enisoinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import net.vpc.common.util.Converter;
import net.vpc.scholar.hadrumaths.InterpolationStrategy;
import net.vpc.scholar.hadrumaths.Maths;
import net.vpc.scholar.hadrumaths.Plot;
import net.vpc.scholar.hadrumaths.PlotLines;
import net.vpc.scholar.hadrumaths.plot.PlotType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchoolScorePlotUtils {
    public static Map<DataKey, Data> data = loadDataMapResource("/scores.txt");


    public static void main(String[] args) {
//        ia13();
//        ia15();
        y2017GTE();
    }

    public static Map<DataKey, Data> loadDataMapResource(String resourcePath) {
        Map<DataKey, Data> m = new HashMap<>();
        try {
            URL d = SchoolScorePlotUtils.class.getResource(resourcePath);
            BufferedReader r = new BufferedReader(new InputStreamReader(d.openStream()));
            String line = null;
            while ((line = r.readLine()) != null) {
                Data dd = loadData(line);
                if (dd != null) {
                    m.put(dd.getKey(), dd);
                }
            }
            r.close();
            return m;
        } catch (IOException ex) {
            Logger.getLogger(SchoolScorePlotUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Data loadData(String line) {
        if (line != null) {
            line = line.trim();
            if (line.length() > 0 && !line.startsWith("#")) {
                String[] arr = line.split(",");
                int year = Integer.parseInt(arr[0].trim());
                String spec = arr[1];
                int max = Integer.parseInt(arr[2].trim());
                String name = arr[3];
                double[] vals = new double[arr.length - 4];
                for (int i = 4; i < arr.length; i++) {
                    vals[i - 4] = Double.parseDouble(arr[i].trim());
                }
                return new Data(name, new DataType(spec, year, max), vals);
            }
        }
        return null;
    }
    
    public static Data get(String program,String spec,int year){
        Data d = data.get(new DataKey(year, spec, program));
        if(d==null){
            throw new IllegalArgumentException("Not found");
        }
        return d;
    }

    public static void y2017() {
        double[] years = new double[]{2017, 2017};
        DataSeries MP = new DataSeries("MP", years, get("IA","MP",2017), get("GTE","MP",2017));
        DataSeries PT = new DataSeries("PT", years, get("IA","PT",2017), get("GTE","PT",2017));
        show(MP, PT);
    }

    public static void y2017GTE() {
        double[] years = new double[]{2017, 2017};
        DataSeries MP = new DataSeries("MP", years, get("GTE","MP",2017));
        DataSeries PT = new DataSeries("PT", years, get("GTE","PT",2017));
        show(MP, PT);
    }

    public static void ia15() {
//        IA_MP_2013,IA_MP_2014,
//        IA_PT_2013,IA_PT_2014,
//        2013,2014,
        double[] years = new double[]{2015, 2016, 2017, 2017};
        DataSeries MP = new DataSeries("MP", years, get("IA","MP",2015), get("IA","MP",2016), get("IA","MP",2017));
        DataSeries PT = new DataSeries("PT", years, get("IA","PT",2015), get("IA","PT",2016), get("IA","PT",2017));
        show(MP, PT);
    }

    public static void ia13() {
        double[] years = new double[]{2013, 2014, 2015, 2016, 2017, 2017};
        DataSeries MP = new DataSeries("MP", years, get("IA","MP",2013), get("IA","MP",2014), get("IA","MP",2015), get("IA","MP",2016), get("IA","MP",2017));//, GTE_MP_2017
        DataSeries PT = new DataSeries("PT", years, get("IA","PT",2013), get("IA","PT",2014), get("IA","PT",2015), get("IA","PT",2016), get("IA","PT",2017));//, GTE_PT_2017
        show(MP, PT);
    }

    public static void show(DataSeries... series) {
        for (DataSeries data : series) {
            PlotLines lines = new PlotLines();
            for (Data datum : data.data) {
                lines.addValues(datum.name, null, datum.values);
            }
            //Plot.asCurve().plot(lines);

            Plot.cd("/" + data.name).asCurve().title("Rangs " + data.name).plot(lines.stretchDomain().interpolate(InterpolationStrategy.SMOOTH));

            PlotLines rlines = new PlotLines();
            for (Data datum : data.data) {
                rlines.addValues(datum.name, null, datum.rvalues);
            }
            //Plot.asCurve().plot(lines);

            Plot.cd("/" + data.name + "%").asCurve().title("Rangs % " + data.name).plot(rlines.stretchDomain().interpolate(InterpolationStrategy.SMOOTH));
            // min max of every year
            PlotLines linesMinMax = new PlotLines();
            Data[] data1 = data.data;
            for (int i = 0; i < data1.length; i++) {
                Data datum = data1[i];
                linesMinMax.addValue("min", data.years[i], Maths.min(datum.values));
                linesMinMax.addValue("max", data.years[i], Maths.max(datum.values));
                linesMinMax.addValue("avg", data.years[i], Maths.avg(datum.values));
            }
            Plot.cd("/" + data.name).asCurve().title("Rangs Min/Max " + data.name).asBar().plot(linesMinMax);

            for (int i = 1; i <= 1; i++) {
                final int i100 = i * 100;
                Converter<PlotLines.PlotPoint, Double> group = r -> Math.floor(r.getY().getReal() / i100) * i100;
                PlotType type = PlotType.PIE;
//            Plot.asCurve().title("avg "+ i100).plotType(type).plot(lines.avgBy         (group).stretchDomain().interpolate(PlotLines.InterpolationStrategy.SMOOTH));
//            Plot.asCurve().cd(String.valueOf(i)).title("# Population / "+ i100).plotType(type).plot(lines.countBy       (group).stretchDomain().interpolate(PlotLines.InterpolationStrategy.PREDECESSOR));
                Plot.asCurve().cd("/" + data.name).title("% Population / " + i100).plotType(type).plot(lines.countBy(group).interpolate(InterpolationStrategy.ZERO));
//            Plot.asCurve().cd("1/"+String.valueOf(i)).title("Rang Moy cumulé /  "+ i100).plotType(type).plot(lines.avgBy         (group).accumulateLeft().stretchDomain().interpolate(PlotLines.InterpolationStrategy.SMOOTH));
//            Plot.asCurve().cd(String.valueOf(i)).title("count (c) "+ i100).plotType(type).plot(lines.countBy       (group).accumulateLeft().stretchDomain().interpolate(PlotLines.InterpolationStrategy.PREDECESSOR));
//            Plot.asCurve().cd(String.valueOf(i)).title("count% (c) "+ i100).plotType(type).plot(lines.countPercentBy(group).accumulateLeft().stretchDomain().interpolate(PlotLines.InterpolationStrategy.PREDECESSOR));
            }
        }

    }

    private double[] sort(double[] a) {
        Arrays.sort(a);
        return a;
    }

    static class DataType {

        int year;
        String spec;
        String name;
        int max;

        public DataType(String spec, int year, int max) {
            this.name = year + "_" + spec;
            this.max = max;
            this.year = year;
            this.spec = spec;
        }

    }

    static class DataKey {

        int year;
        String spec;
        String program;

        public DataKey(int year, String spec, String program) {
            this.year = year;
            this.spec = spec;
            this.program = program;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 13 * hash + this.year;
            hash = 13 * hash + Objects.hashCode(this.spec);
            hash = 13 * hash + Objects.hashCode(this.program);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DataKey other = (DataKey) obj;
            if (this.year != other.year) {
                return false;
            }
            if (!Objects.equals(this.spec, other.spec)) {
                return false;
            }
            if (!Objects.equals(this.program, other.program)) {
                return false;
            }
            return true;
        }

    }

    static class Data {

        DataType type;
        String program;
        String name;
        double[] values;
        double[] rvalues;

        public Data(String program, DataType type, double[] values) {
            this.program = program;
            this.name = program + "_" + type.name;
            Arrays.sort(values);
            this.values = values;
            this.type = type;
            this.rvalues = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                rvalues[i] = values[i] * 100 / type.max;
            }
        }

        public DataKey getKey() {
            return new DataKey(type.year, type.spec, program);
        }
    }

    static class DataSeries {

        String name;
        Data[] data;
        double[] years;

        public DataSeries(String name, double[] years, Data... data) {
            this.name = name;
            this.data = data;
            this.years = years;
        }
    }

}
