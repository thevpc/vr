package net.vpc.app.vainruling.core.service.stats;

import java.util.*;

/**
 * Created by vpc on 8/29/16.
 */
public class KPIProcessProcessor {
    public static final KPIProcessProcessor INSTANCE=new KPIProcessProcessor();

    public <V> KPIResult run(List<V> assignments, KPIGroupBy<V>[] eduKPIGroupByList, KPI<V>[] kpis) {
        KPIGroupBy<V> eduKPIGroupBy=KPIUtils.groupBy(eduKPIGroupByList);
        DefaultKPIResult result = new DefaultKPIResult();
        result.kpis = kpis;
        for (KPI kpi : kpis) {
            result.kpisDefs.addAll(Arrays.asList(kpi.getValueDefinitions()));
        }
        for (V assignment : assignments) {
            for (KPIGroup group : eduKPIGroupBy.createGroups(assignment)) {
                DefaultKPIResultRow kpinfo = result.kpiInfos.get(group);
                if (kpinfo == null) {
                    kpinfo = new DefaultKPIResultRow(group);
                    kpinfo.kpiEvals = new KPIEvaluator[result.kpis.length];
                    for (int i = 0; i < result.kpis.length; i++) {
                        KPIEvaluator evaluator = result.kpis[i].createEvaluator();
                        evaluator.start();
                        kpinfo.kpiEvals[i] = evaluator;
                    }
                    result.kpiInfos.put(group, kpinfo);
                }
                for (KPIEvaluator<V> evals : kpinfo.kpiEvals) {
                    evals.visit(assignment);
                }
            }
        }
        for (DefaultKPIResultRow kpinfo : result.kpiInfos.values()) {
            List<KPIValue> all = new ArrayList<>();
            for (KPIEvaluator evals : kpinfo.kpiEvals) {
                all.addAll(Arrays.asList(evals.evaluate()));
            }
            kpinfo.setValues(all.toArray(new KPIValue[all.size()]));
        }
        DefaultKPIResultRow[] rows = result.kpiInfos.values().toArray(new DefaultKPIResultRow[result.kpiInfos.size()]);
        Arrays.sort(rows, new Comparator<DefaultKPIResultRow>() {
            @Override
            public int compare(DefaultKPIResultRow o1, DefaultKPIResultRow o2) {
                return o1.getGroup().compareTo(o2.getGroup());
            }
        });
        result.orderedRows.addAll(Arrays.asList(rows));
        return result;
    }

    private static class DefaultKPIResult implements KPIResult {
        private KPI[] kpis;
        private Map<KPIGroup, DefaultKPIResultRow> kpiInfos = new HashMap<>();
        private List<KPIValueDef> kpisDefs = new ArrayList<>();
        private List<DefaultKPIResultRow> orderedRows = new ArrayList<>();

        @Override
        public List<KPIValueDef> getDefinitions() {
            return kpisDefs;
        }

        @Override
        public List<KPIResultRow> getRows() {
            return (List) orderedRows;
        }

        @Override
        public KPIResultRow getRow(KPIGroup group) {
            return kpiInfos.get(group);
        }
    }

    private static class DefaultKPIResultRow implements KPIResultRow {
        KPIGroup group;
        KPIEvaluator[] kpiEvals;
        KPIValue[] values;
        Map<String, KPIValue> valuesMap = new HashMap<>();

        public DefaultKPIResultRow(KPIGroup group) {
            this.group = group;
        }

        @Override
        public KPIGroup getGroup() {
            return group;
        }

        public void setValues(KPIValue[] values) {
            this.values = values;
            valuesMap.clear();
            for (KPIValue value : values) {
                valuesMap.put(value.getName(), value);
            }
        }

        @Override
        public KPIValue[] getValues() {
            return values;
        }

        @Override
        public KPIValue getValue(int index) {
            return values[index];
        }

        @Override
        public KPIValue getValue(String name) {
            return null;
        }
    }
}
