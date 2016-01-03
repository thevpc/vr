/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.devtoolbox.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.MultiRecord;
import net.vpc.upa.Record;
import net.vpc.upa.UPA;
import net.vpc.upa.persistence.Parameter;
import net.vpc.upa.persistence.QueryResult;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author vpc
 */
@UCtrl(
        title = "Developer Tools", css = "fa-dashboard", url = "modules/devtoolbox/dev-ql", menu = "/Admin", securityKey = "Custom.DevTools"
)
@ManagedBean
@Scope(value = "session")
public class DevToolsCtrl {

    private Model model = new Model();

    public static class Model {

        private String query;
        private String lang = "sql";
        private List<List<Object>> rows = new ArrayList<>();
        private List<ColDef> rowNames = new ArrayList<>();

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public boolean isSql() {
            return "sql".equals(lang);
        }
        
        public boolean isupql() {
            return "upql".equals(lang);
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getLang() {
            return lang;
        }

        public List<List<Object>> getRows() {
            return rows;
        }

        public void setRows(List<List<Object>> rows) {
            this.rows = rows;
        }

        public List<ColDef> getRowNames() {
            return rowNames;
        }

        public void setRowNames(List<ColDef> rowNames) {
            this.rowNames = rowNames;
        }

    }

    public class ColDef {

        private String name;
        private int index;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public ColDef() {
        }

        public ColDef(String name, int index) {
            this.name = name;
            this.index = index;
        }

    }

    public Model getModel() {
        return model;
    }

    public void clear() {
        getModel().getRows().clear();
        getModel().getRowNames().clear();
    }

    public void exec() {
        String q = getModel().getQuery();
        if (q != null && q.trim().length() > 0) {
            if (getModel().isSql()) {
                clear();
                q = q.trim();
                if (q.toLowerCase().startsWith("select")) {
                    try {
                        QueryResult r = UPA.getPersistenceUnit().getPersistenceStore().getConnection().executeQuery(q, null, null, false);
                        final int fieldsCount = r.getFieldsCount();
                        for (int i = 0; i < fieldsCount; i++) {
                            getModel().getRowNames().add(new ColDef("C" + (i + 1), i));
                        }
                        while (r.hasNext()) {
                            List<Object> row = new ArrayList<Object>();
                            for (int i = 0; i < fieldsCount; i++) {
                                row.add(r.read(i));
                            }
                            getModel().getRows().add(row);
                        }
                    } catch (Exception e) {
                        List<Object> row1 = Arrays.asList((Object) e.getMessage());
                        List<Object> row2 = Arrays.asList((Object) StringUtils.verboseStacktraceToString(e));
                        getModel().getRows().add((List<Object>) row1);
                        getModel().getRows().add((List<Object>) row2);
                        getModel().getRowNames().add(new ColDef("<Error>", 0));
                    }
                } else {
                    try {
                        int r = UPA.getPersistenceUnit().getPersistenceStore().getConnection().executeNonQuery(q, new ArrayList<Parameter>(), new ArrayList<Parameter>());
                        List<Object> row = Arrays.asList((Object) r);
                        getModel().getRows().add((List<Object>) row);
                        getModel().getRowNames().add(new ColDef("<Result>", 0));
                    } catch (Exception e) {
                        List<Object> row1 = Arrays.asList((Object) e.getMessage());
                        List<Object> row2 = Arrays.asList((Object) StringUtils.verboseStacktraceToString(e));
                        getModel().getRows().add((List<Object>) row1);
                        getModel().getRows().add((List<Object>) row2);
                        getModel().getRowNames().add(new ColDef("<Error>", 0));
                    }
                }
            } else if (getModel().isupql()) {
                if (q.toLowerCase().startsWith("select")) {
                    try {
                        List<MultiRecord> r = UPA.getPersistenceUnit().createQuery(q).getMultiRecordList();
                        HashMap<String, Integer> indices = new HashMap<String, Integer>();
                        for (MultiRecord r1 : r) {
                            Record rec = r1.merge();
                            List<Object> row = new ArrayList<>();
                            for (String k : rec.keySet()) {
                                Integer p = indices.get(k);
                                if (p == null) {
                                    p = indices.size();
                                    indices.put(k, p);
                                }
                                while (row.size() < p + 1) {
                                    row.add(null);
                                }
                                row.set(p, rec.getObject(k));
                            }
                            getModel().getRows().add(row);
                        }
                        while (getModel().getRowNames().size() < indices.size()) {
                            getModel().getRowNames().add(null);
                        }
                        for (Map.Entry<String, Integer> es : indices.entrySet()) {
                            getModel().getRowNames().set(es.getValue(), new ColDef(es.getKey(), es.getValue()));
                        }
                    } catch (Exception e) {
                        List<Object> row1 = Arrays.asList((Object) e.getMessage());
                        List<Object> row2 = Arrays.asList((Object) StringUtils.verboseStacktraceToString(e));
                        getModel().getRows().clear();
                        getModel().getRows().add((List<Object>) row1);
                        getModel().getRows().add((List<Object>) row2);
                        getModel().getRowNames().clear();
                        getModel().getRowNames().add(new ColDef("<Error>", 0));
                    }
                } else {
                    try {
                        int r = UPA.getPersistenceUnit().createQuery(q).executeNonQuery();
                        List<Object> row = Arrays.asList((Object) r);
                        getModel().getRows().add((List<Object>) row);
                        getModel().getRowNames().add(new ColDef("<Result>", 0));
                    } catch (Exception e) {
                        List<Object> row1 = Arrays.asList((Object) e.getMessage());
                        List<Object> row2 = Arrays.asList((Object) StringUtils.verboseStacktraceToString(e));
                        getModel().getRows().clear();
                        getModel().getRows().add((List<Object>) row1);
                        getModel().getRows().add((List<Object>) row2);
                        getModel().getRowNames().clear();
                        getModel().getRowNames().add(new ColDef("<Error>", 0));
                    }
                }
            }
        }
    }
}
