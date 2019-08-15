package net.vpc.app.vainruling.plugins.academic.service.integration;

import net.vpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionRow;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 7/1/16.
 */
public class AcademicConversionTableHelper {
    private AcademicLoadConversionTable table;
    private List<AcademicLoadConversionRow> rows = new ArrayList<>();
    private Map<Integer, AcademicLoadConversionRow> rowsMap = new HashMap<>();
    private Map<String, AcademicLoadConversionRow> rowsMapByName = new HashMap<>();

    public AcademicConversionTableHelper(AcademicLoadConversionTable table) {
        this.table = table;
    }

    public AcademicLoadConversionRow get(int ruleId) {
        return rowsMap.get(ruleId);
    }

    public AcademicLoadConversionRow get(String ruleName) {
        return rowsMapByName.get(ruleName);
    }

    public void add(AcademicLoadConversionRow r) {
        if (rowsMapByName.containsKey(r.getRule().getName())) {
            throw new IllegalArgumentException();
        }
        if (rowsMap.containsKey(r.getRule().getId())) {
            throw new IllegalArgumentException();
        }
        rowsMapByName.put(r.getRule().getName(), r);
        rowsMap.put(r.getRule().getId(), r);
        rows.add(r);
    }
}
