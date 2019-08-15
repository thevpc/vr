/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.vpc.app.vainruling.core.service.editor.DialogResult;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicDiscipline;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
@Scope(value = "session")
public class DisciplineDialogCtrl {

    @Autowired
    private AcademicPlugin acad;

    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        getModel().setConfig(config);
        initContent();

        new DialogBuilder("/modules/academic/dialog/discipline-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
    }

    private void initContent() {
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
            getModel().setConfig(c);
        }
        String title = c.getTitle();
        if (StringUtils.isBlank(title)) {
            title = "Disciplines";
        }
        getModel().setTitle(title);
        getModel().setExpression(c.getExpression() == null ? "" : c.getExpression());

        getModel().getMap().clear();
        getModel().getItems().clear();

        for (AcademicDiscipline p : acad.findDisciplines()) {
            ExprItem i = new ExprItem(p.getName(), p.getName(), "profile", p.getName());
            if (!getModel().getMap().containsKey(i.getExpr())) {
                getModel().getMap().put(i.getExpr(), i);
                getModel().getItems().add(FacesUtils.createSelectItem(i.getExpr(), i.getName()));
            }
        }
    }

    public void onAdd() {
        if (getModel().getSelected() != null) {
            Object v = getModel().getSelected();
            if (v != null) {
                ExprItem i = getModel().getMap().get(v);
                if (i != null) {
                    String e = getModel().getExpression();
                    if (e == null) {
                        e = "";
                    }
                    e = e.trim();
                    if (e.length() > 0) {
                        e += ", ";
                    }
                    e += i.getExpr();
                    getModel().setExpression(e);
                }
            }
        }
    }

    public Model getModel() {
        return model;
    }

    public void fireEventExtraDialogClosed() {
        //Object obj
        RequestContext.getCurrentInstance().closeDialog(new DialogResult(getModel().getExpression(), getModel().getConfig().getUserInfo()));
    }

    public static class ExprItem {

        private String name;
        private String label;
        private String type;
        private String expr;

        public ExprItem(String name, String label, String type, String expr) {
            this.name = name;
            this.label = label;
            this.type = type;
            this.expr = expr;
        }

        public ExprItem() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getExpr() {
            return expr;
        }

        public void setExpr(String expr) {
            this.expr = expr;
        }

    }

    public static class Config {

        private String title;
        private String expression;
        private String sourceId;
        private String userInfo;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public String getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(String userInfo) {
            this.userInfo = userInfo;
        }

    }

    public static class Model {

        private String title = "";
        private Config config = new Config();
        private String expression = "";
        private List<SelectItem> items = new ArrayList<>();
        private Map<String, ExprItem> map = new HashMap<String, ExprItem>();
        private String selected;

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public List<SelectItem> getItems() {
            return items;
        }

        public void setItems(List<SelectItem> items) {
            this.items = items;
        }

        public Map<String, ExprItem> getMap() {
            return map;
        }

        public void setMap(Map<String, ExprItem> map) {
            this.map = map;
        }

        public String getSelected() {
            return selected;
        }

        public void setSelected(String selected) {
            this.selected = selected;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }
}
