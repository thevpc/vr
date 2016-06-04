/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.common.strings.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
@ManagedBean
@Scope(value = "session")
public class ProfileExprDialogCtrl {

    @Autowired
    private CorePlugin core;

    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrHelper.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        getModel().setConfig(config);
        initContent();

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/obj/profileexprdialog", options, null);

    }

    private void initContent() {
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
            getModel().setConfig(c);
        }
        String title = c.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = "Groupes Utilisateurs";
        }
        getModel().setTitle(title);
        getModel().setExpression(c.getExpression() == null ? "" : c.getExpression());

        getModel().getMap().clear();
        getModel().getItems().clear();

        List<SelectItem> tgroups = new ArrayList<>();
        List<SelectItem> tusers = new ArrayList<>();
        for (AppProfile p : core.findProfiles()) {
            ProfileExprItem i = new ProfileExprItem(p.getName(), p.getName(), "profile", p.getName());
            if (!getModel().getMap().containsKey(i.getExpr())) {
                getModel().getMap().put(i.getExpr(), i);
                tgroups.add(new SelectItem(i.getExpr(), i.getName()));
            }
        }
        for (AppUser p : core.findUsers()) {
            ProfileExprItem i = new ProfileExprItem(p.getLogin(), AppUser.getName(p), "user", p.getLogin());
            if (!getModel().getMap().containsKey(i.getExpr())) {
                getModel().getMap().put(i.getExpr(), i);
                tusers.add(new SelectItem(i.getExpr(), i.getName()));
            }
        }

        SelectItemGroup g1 = new SelectItemGroup("Groupes");
        g1.setSelectItems(tgroups.toArray(new SelectItem[tgroups.size()]));

        SelectItemGroup g2 = new SelectItemGroup("Utilisateurs");
        g2.setSelectItems(tusers.toArray(new SelectItem[tgroups.size()]));
//        getModel().getItems().add(new SelectItem("", "profil"));

        getModel().getItems().add(g1);
        getModel().getItems().add(g2);
        revalidateUsersList();
    }

    public void onAdd() {
        if (getModel().getSelected() != null) {
            Object v = getModel().getSelected();
            if (v != null) {
                ProfileExprItem i = getModel().getMap().get(v);
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
        revalidateUsersList();
    }

    public void revalidateUsersList() {
        String e = getModel().getExpression();
        getModel().setUsers(core.findUsersByProfileFilter(e,null));
    }

    public Model getModel() {
        return model;
    }

    public void fireEventExtraDialogClosed() {
        //Object obj
        RequestContext.getCurrentInstance().closeDialog(new DialogResult(getModel().getExpression(),getModel().getConfig().getUserInfo()));
    }

    public static class ProfileExprItem {

        private String name;
        private String label;
        private String type;
        private String expr;

        public ProfileExprItem(String name, String label, String type, String expr) {
            this.name = name;
            this.label = label;
            this.type = type;
            this.expr = expr;
        }

        public ProfileExprItem() {
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
        private List<AppUser> users = new ArrayList<>();
        private Map<String, ProfileExprItem> map = new HashMap<String, ProfileExprItem>();
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

        public Map<String, ProfileExprItem> getMap() {
            return map;
        }

        public void setMap(Map<String, ProfileExprItem> map) {
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

        public List<AppUser> getUsers() {
            return users;
        }

        public void setUsers(List<AppUser> users) {
            this.users = users;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }
}
