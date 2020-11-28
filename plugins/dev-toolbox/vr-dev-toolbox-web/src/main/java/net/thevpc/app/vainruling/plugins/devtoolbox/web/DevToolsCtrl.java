/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.devtoolbox.web;

import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.Document;
import net.thevpc.upa.MultiDocument;
import net.thevpc.upa.UPA;
import net.thevpc.upa.persistence.QueryResult;
import org.springframework.context.annotation.Scope;

import java.util.*;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        //title = "Developer Tools", css = "fa-dashboard",
        url = "modules/devtoolbox/dev-ql", 
        menu = "/Admin", 
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_DEV_TOOLS
)
@Scope(value = "session")
public class DevToolsCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void clear() {
        getModel().getRows().clear();
        getModel().getRowNames().clear();
    }

    public void exec() {
        clear();
        String q = getModel().getQuery();
        try {
            if (q != null && q.trim().length() > 0) {
                if (getModel().isSql()) {
                    q = q.trim();
                    if (q.toLowerCase().startsWith("select")) {
                        QueryResult r = UPA.getPersistenceUnit().getConnection().executeQuery(q, null, null, false);
                        final int fieldsCount = r.getColumnsCount();
                        for (int i = 0; i < fieldsCount; i++) {
                            String columnName = r.getColumnName(i);
                            if (StringUtils.isBlank(columnName)) {
                                columnName = "C" + (i + 1);
                            }
                            getModel().getRowNames().add(new ColDef(columnName, i));
                        }
                        while (r.hasNext()) {
                            List<Object> row = new ArrayList<Object>();
                            for (int i = 0; i < fieldsCount; i++) {
                                row.add(r.read(i));
                            }
                            getModel().getRows().add(row);
                        }
                    } else {
                        int r = UPA.getPersistenceUnit().getConnection().executeNonQuery(q, null, null);
                        List<Object> row = Arrays.asList((Object) r);
                        getModel().getRows().add(row);
                        getModel().getRowNames().add(new ColDef("<Result>", 0));
                    }
                } else if (getModel().isupql()) {
                    if (q.toLowerCase().startsWith("select")) {
                        List<MultiDocument> r = UPA.getPersistenceUnit().createQuery(q).getMultiDocumentList();
                        HashMap<String, Integer> indices = new HashMap<String, Integer>();
                        for (MultiDocument r1 : r) {
                            Document rec = r1.merge();
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
                    } else {
                        int r = UPA.getPersistenceUnit().createQuery(q).executeNonQuery();
                        List<Object> row = Arrays.asList((Object) r);
                        getModel().getRows().add(row);
                        getModel().getRowNames().add(new ColDef("<Result>", 0));
                    }
                }
            }
        } catch (Exception ex) {
            Throwable th = ex;
            getModel().getRowNames().add(new ColDef("<Error>", 0));
            while (th != null) {
                List<Object> row1 = Arrays.asList((Object) th.getMessage());
                List<Object> row2 = Arrays.asList((Object) StringUtils.verboseStacktraceToString(th));
                getModel().getRows().add(row1);
                getModel().getRows().add(row2);
                th = th.getCause();
            }
        }

    }

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

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
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

        public ColDef() {
        }

        public ColDef(String name, int index) {
            this.name = name;
            this.index = index;
        }

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

    }
}
