/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.menu;

import java.util.List;
import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.app.vainruling.core.service.editor.EditorConfig;
import net.vpc.common.strings.StringBuilder2;
import net.vpc.common.strings.StringUtils;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import net.vpc.app.vainruling.VrPageHistoryItem;
import net.vpc.app.vainruling.core.service.menu.VrPageInfoAndObject;
import net.vpc.app.vainruling.core.service.menu.VrServiceMenu;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

//import net.vpc.app.vainruling.tasks.TaskPlugin;
//import net.vpc.app.vainruling.tasks.model.TodoList;
/**
 * @author taha.bensalah@gmail.com
 */
@Controller
@Scope(value = "session")
public class VrMenuManager {

    private static final Logger log = Logger.getLogger(VrMenuManager.class.getName());
    private VrServiceMenu serviceMenu = new VrServiceMenu();

    public boolean isCurrentPageId(String name) {
        return serviceMenu.isCurrentPageId(name);
    }

    public String buildMenu() {
        HttpServletRequest req = VrWebHelper.getHttpServletRequest();
        String searchText = req == null ? null : req.getParameter("searchTextInput");
        getModel().setSearchText("");
        getModel().setRoot(serviceMenu.createMenu(searchText));
        return "";//ignore-me
    }

    public void pushHistory(String command, Object arguments) {
        serviceMenu.pushHistory(command, arguments);
    }

    public VrPageHistoryItem peekHistory() {
        return serviceMenu.peekHistory();
    }

    public VrPageHistoryItem popHistory() {
        return serviceMenu.popHistory();
    }

    public String goBack() {
        VrPageHistoryItem last = serviceMenu.goBack();
        if (last != null) {
            return gotoPage(last.getCommand(), last.getArguments(), false);
        }
        return null;
    }

    public String gotoPage(String command, String arguments) {
        return gotoPage(command, arguments, true);
    }

    public String gotoPageObjItem(String entity, String id) {
        EditorConfig c = new EditorConfig();
        c.entity = entity;
        c.id = id;
        return gotoPage("editor", VrUtils.formatJSONObject(c));
    }

    private String gotoPage(String command, String arguments, boolean addToHistory) {
        VrWebHelper.prepareUserSession();
        String url = serviceMenu.gotoPage(command, arguments, addToHistory);
        if (StringUtils.isBlank(url)) {
            return null;
        }
        return path(url);
    }

    public String path(String p) {
        String c = StringUtils.trim(VrWebHelper.getContext());
        String r = StringUtils.trim(VrWebHelper.getFacesContextPrefix());
        StringBuilder2 sb = new StringBuilder2();
//        sb.appendWithSeparator("/",c);
        sb.appendWithSeparator("/", r);
        sb.appendWithSeparator("/", p);
        sb.append("?faces-redirect=true");
        return sb.toString();
    }

    public VrServiceMenu.Model getModel() {
        return serviceMenu.getModel();
    }

    @PostConstruct
    public void initCustomMenus() {
        //controllers.clear();
        serviceMenu.build();

    }

    public List<VrPageHistoryItem> getPageHistory() {
        return serviceMenu.getPageHistory();
    }

    public VrPageInfoAndObject resolvePageInfoAndObjectByInstance(String name, String cmd) {
        return serviceMenu.resolvePageInfoAndObjectByInstance(name, cmd);
    }
}
