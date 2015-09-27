/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.i18n.I18n;
import net.vpc.app.vainruling.api.util.Reflector;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.api.web.util.TreeDefinition;
//import net.vpc.app.vainruling.tasks.TaskPlugin;
//import net.vpc.app.vainruling.tasks.model.TodoList;
import net.vpc.app.vainruling.api.web.util.TreeTraversal;
import net.vpc.app.vainruling.api.web.util.TreeVisitor;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Entity;
import net.vpc.upa.MissingStrategy;
import net.vpc.upa.Package;
import net.vpc.upa.UPA;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.filters.DefaultEntityFilter;
import net.vpc.upa.filters.EntityFilter;
import net.vpc.upa.impl.util.Strings;
import net.vpc.vfs.VFS;
import net.vpc.vfs.VFile;
import net.vpc.vfs.VirtualFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author vpc
 */
@UCtrl
@ManagedBean
@Scope(value = "session")
public class VrMenuManager {

    private Model model = new Model();
    private Object pageCtrl;
//    @Autowired
//    private TaskPlugin todoService;
    @Autowired
    private I18n i18n;
    private LinkedList<PageInfo> pageHistory = new LinkedList<>();

    public Object getPageCtrl() {
        return pageCtrl;
    }

    public void setPageCtrl(Object pageCtrl) {
        this.pageCtrl = pageCtrl;
    }

    public Object getPageCtrl(String type) {
        if (!type.endsWith("Ctrl")) {
            type = type + "Ctrl";
        }
        return VrApp.getContext().getBean(type);
    }

    public boolean isCurrentPageId(String name) {
        if (Strings.isNullOrEmpty(name)) {
            return Strings.isNullOrEmpty(getModel().getCurrentPageId());
        } else {
            return name.equals(getModel().getCurrentPageId());
        }
    }

    public VRMenuDef getMenuTree() {
        MenuTree v = new MenuTree();
        TreeTraversal.preOrderTreeTraversal(v, new TreeVisitor<VRMenuDef>() {

            @Override
            public void visit(VRMenuDef t, TreeDefinition<VRMenuDef> tree) {

            }
        });
        for (VRMenuDef nonVisitedCustomMenu : v.nonVisitedCustomMenus) {
            System.err.println(nonVisitedCustomMenu + " not added ");
        }
        return new MenuTree().root;
    }

    private List<VRMenuDef> findCustomMenus(String parent, List<VRMenuDef> autowiredCustomMenusByCtrl) {
        List<VRMenuDef> ok = new ArrayList<>();
        if (parent == null) {
            parent = "/";
        }
        for (VRMenuDef m : autowiredCustomMenusByCtrl) {
            String p = m.getPath();
            if (p == null) {
                p = "";
            }
            if (!p.startsWith("/")) {
                p = "/" + p;
            }
            if (!p.equals("/") && p.endsWith("/")) {
                p = p.substring(0, p.length() - 1);
            }
            if (p.equalsIgnoreCase(parent)) {
                ok.add(m);
            }
        }
        return ok;
    }

    private List<String> findCustomPaths(String parent, List<VRMenuDef> autowiredCustomMenusByCtrl) {
        List<String> ok = new ArrayList<>();
        if (parent == null) {
            parent = "/";
        }
        VirtualFileSystem fs = VFS.createEmptyFS();
        for (VRMenuDef m : autowiredCustomMenusByCtrl) {
            String p = m.getPath();
            if (p == null) {
                p = "";
            }
            if (!p.startsWith("/")) {
                p = "/" + p;
            }
            if (!p.equals("/")) {
                VFile containingFolder = fs.get(p).getParentFile();
                String ancestor = containingFolder.getParentPath();
                if (ancestor != null && ancestor.equalsIgnoreCase(parent)) {
                    ok.add(containingFolder.getPath());
                }
            }
        }
        return ok;
    }

    private List<Entity> filterEntities(List<Entity> in) {
        return filterEntities(in, new DefaultEntityFilter().setAcceptSystem(false));
    }

    private List<Entity> filterEntities(List<Entity> in, EntityFilter f) {
        ArrayList<Entity> out = new ArrayList<>();
        for (Entity i : in) {
            if (f.accept(i)) {
                out.add(i);
            }
        }
        return out;
    }

    private UCtrlData getUCtrlData(String ctrl0, String cmd) {
        Object ccc = getPageCtrl(ctrl0);
        if (ccc instanceof UCtrlProvider) {
            UCtrlProvider up = (UCtrlProvider) ccc;
            return up.getUCtrl(cmd);
        }
        UCtrl c2Ann = ccc.getClass().getAnnotation(UCtrl.class);
        if (c2Ann != null) {
            UCtrlData d = new UCtrlData();
            d.setCss(c2Ann.css());
            d.setTitle(c2Ann.title());
            d.setUrl(c2Ann.url());
            List<BreadcrumbItem> aa = new ArrayList<>();
            for (UPathItem breadcrumb : c2Ann.breadcrumb()) {
                String title = breadcrumb.title();
                String css = breadcrumb.css();
                String ctrl = breadcrumb.ctrl();
                String cmd2 = breadcrumb.cmd();
                if (!StringUtils.isEmpty(ctrl)) {
                    UCtrlData d2 = getUCtrlData(ctrl, cmd2);
                    if (d2 != null) {
                        if (StringUtils.isEmpty(css)) {
                            css = d2.getCss();
                        }
                        if (StringUtils.isEmpty(title)) {
                            css = d2.getTitle();
                        }
                    }
                }
                aa.add(new BreadcrumbItem(title, css, ctrl, cmd2));
            }
            d.setBreadcrumb(aa.toArray(new BreadcrumbItem[aa.size()]));
            return d;
        }
        return null;
    }

    private String resolveGoodBeanName(Object o) {
        StringBuilder ctrl0 = new StringBuilder(o.getClass().getSimpleName());
        ctrl0.setCharAt(0, Character.toLowerCase(ctrl0.charAt(0)));
        return ctrl0.toString();
    }

//    public String gotoPage(Class type, String cmd) {
//        Object o = VrApp.getBean(type);
//        return gotoPageByBean(o, cmd);
//    }
    public String goBack() {
        if (pageHistory.size() > 0) {
            pageHistory.removeLast();
            if (pageHistory.size() > 0) {
                PageInfo last = pageHistory.getLast();
                return gotoPage(last.getCommand(), last.getArguments(), false);
            }
        }
        return null;
    }

    public String gotoPage(String command, String arguments) {
        return gotoPage(command, arguments, true);
    }

    private String gotoPage(String command, String arguments, boolean addtoHistory) {
        Object o = getPageCtrl(command);
        VRWebHelper.prepareUserSession();
        String goodBeanName = resolveGoodBeanName(o);
        UCtrlData d = getUCtrlData(goodBeanName, arguments);
        setPageCtrl(o);
        List<BreadcrumbItem> bc = new ArrayList<>();
        String url = null;
        if (d != null) {
            bc.addAll(Arrays.asList(d.getBreadcrumb()));
            url = d.getUrl();
        }
        setPageCtrl(o);
        Reflector.InstanceInvoker[] mm = Reflector.findInstanceByAnnotation(o, OnPageLoad.class);
        for (Reflector.InstanceInvoker m : mm) {
            if (m.getParameterTypes().length == 0) {
                m.invoke();
            } else if (m.getParameterTypes().length == 1) {
                m.invoke(VrHelper.parseJSONObject(arguments, m.getParameterTypes()[0]));
            }
        }
        if (d != null) {
            bc.add(new BreadcrumbItem(d.getTitle(), d.getCss(), "", ""));
        } else {
            bc.add(new BreadcrumbItem("", "", "", ""));
        }
        getModel().setBreadcrumb(bc);
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        if (addtoHistory) {
            pushHistory(command, arguments);
        }
        return path(url);
    }

    public void pushHistory(String command, Object arguments) {
        if (arguments == null) {
            arguments = "";
        } else if (arguments instanceof String) {
            //okkay
        } else {
            arguments = VrHelper.formatJSONObject(arguments);
        }
        PageInfo pi = new PageInfo(command, String.valueOf(arguments));
        if (pageHistory.size() > 0 && pageHistory.getLast().equals(pi)) {
            return;
        }
        pageHistory.add(pi);
        if (pageHistory.size() > 20) {
            pageHistory.removeFirst();
        }
    }

//    public String invoke(String name) {
//        try {
//            return (String) getClass().getMethod(name).invoke(this);
//        } catch (Exception ex) {
//            Logger.getLogger(VrMenu.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
//    }
//
//    public String gotoLaboMyLaboTodos() {
//        TodoCtrl todo = VRApp.getBean(TodoCtrl.class);
//        getModel().setBreadcrumb(
//                new BreadcrumbItem("Dashboard", "fa-dashboard", "gotoDashboardMain"), new BreadcrumbItem("Labo", "fa-dashboard", "gotoDashboardLabo"), new BreadcrumbItem("Mes Actions", "fa-table", null)
//        );
//        todo.getModel().setListName(TodoList.LABO_ACTION);
//        todo.reloadPage(null);
//        return path("modules/todo/todos");
//    }
//
//    public String gotoLaboMyLaboTickets() {
//        final TodoCtrl todo = VRApp.getBean(TodoCtrl.class);
//        getModel().setBreadcrumb(
//                new BreadcrumbItem("Dashboard", "fa-dashboard", "gotoDashboardMain"), new BreadcrumbItem("Labo", "fa-dashboard", "gotoDashboardLabo"), new BreadcrumbItem("Mes Tickets", "fa-table", null)
//        );
//        todo.getModel().setListName(TodoList.LABO_TICKET);
//        todo.reloadPage(null);
//        return "modules/todo/todo.xhtml";
//    }
    public String path(String p) {
        return "/faces/" + p + "?faces-redirect=true";
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private List<BreadcrumbItem> breadcrumb = new ArrayList<BreadcrumbItem>();
        private BreadcrumbItem titleCrumb = new BreadcrumbItem("", "", "", "");
        private List<VRMenuDef> menuCtrl = null;
        private String currentPageId;

        public List<BreadcrumbItem> getBreadcrumb() {
            return breadcrumb;
        }

        public String getCurrentPageId() {
            return currentPageId;
        }

        public void setCurrentPageId(String currentPageId) {
            this.currentPageId = currentPageId;
        }

        public void setBreadcrumb(List<BreadcrumbItem> breadcrumb) {
            this.breadcrumb = breadcrumb;
            for (int i = 0; i < breadcrumb.size(); i++) {
                breadcrumb.get(i).setActive(i == breadcrumb.size() - 1);
            }
            if (this.breadcrumb.isEmpty()) {
                titleCrumb = new BreadcrumbItem("", "", "", "");
                titleCrumb.setActive(false);
            } else {
                titleCrumb = breadcrumb.get(breadcrumb.size() - 1);
            }
        }

        public void setBreadcrumb(BreadcrumbItem... breadcrumb) {
            setBreadcrumb(Arrays.asList(breadcrumb));
        }

        public BreadcrumbItem getTitleCrumb() {
            return titleCrumb;
        }

        public List<VRMenuDef> getMenuCtrl() {
            return menuCtrl;
        }

    }

    public List<VRMenuDef> resolveAutowiredCustomMenusByCtrl() {
        List<VRMenuDef> menuCtrl = new ArrayList<>();
        for (String beanName : VrApp.getContext().getBeanNamesForAnnotation(UCtrl.class)) {
            Object b = VrApp.getContext().getBean(beanName);
            UCtrl c = b.getClass().getAnnotation(UCtrl.class);
            if (c != null && c.menu().length() > 0) {
                String menu = c.menu();
                if (!menu.startsWith("/")) {
                    menu = "/" + menu;
                }
                VRMenuDef md = new VRMenuDef(c.title(), menu, beanName, "", c.securityKey(), "");
                UCtrlData uc = getUCtrlData(md.getType(), md.getCommand());
                if (uc != null) {
                    md.setName(uc.getTitle());
                }
                if (md.getSecurityKey() == null
                        || md.getSecurityKey().length() == 0
                        || UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(md.getSecurityKey())) {
                    menuCtrl.add(md);
                }
            }
            if (b instanceof VRMenuDefFactory) {
                for (VRMenuDef md : ((VRMenuDefFactory) b).createVRMenuDefList()) {
                    if (md.getSecurityKey() == null
                            || md.getSecurityKey().length() == 0
                            || UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(md.getSecurityKey())) {
                        UCtrlData uc = getUCtrlData(md.getType(), md.getCommand());
                        if (uc != null) {
                            md.setName(uc.getTitle());
                        }
                        menuCtrl.add(md);
                    }
                }
            }
        }
        return menuCtrl;
    }

//    public List<VRMenuDef> getAutowiredCustomMenusByCtrl() {
//        if (getModel().getMenuCtrl() == null) {
//            getModel().menuCtrl = resolveAutowiredCustomMenusByCtrl();
//        }
//        return getModel().getMenuCtrl();
//    }
    private static class MenuTreeVisitor implements TreeVisitor<VRMenuDef> {

        @Override
        public void visit(VRMenuDef t, TreeDefinition<VRMenuDef> tree) {
            List<VRMenuDef> children = tree.getChildren(t);
            for (int i = children.size() - 1; i >= 0; i--) {
                VRMenuDef el = children.get(i);
                if (el.getType().equals("package") && el.getChildren().isEmpty()) {
                    children.remove(i);
                }
            }
        }
    }

    private class MenuTree implements TreeDefinition<VRMenuDef> {

        final VRMenuDef root = new VRMenuDef("/", "/", "package", "", "", "");
        final List<VRMenuDef> autowiredCustomMenusByCtrl = resolveAutowiredCustomMenusByCtrl();
        final HashSet<VRMenuDef> nonVisitedCustomMenus = new HashSet<>(autowiredCustomMenusByCtrl);
        final HashSet<VRMenuDef> visitedCustomMenus = new HashSet<>();

        public MenuTree() {
            TreeTraversal.postOrderTreeTraversal(this, new MenuTreeVisitor());
        }

        @Override
        public VRMenuDef getRoot() {
            return root;
        }

        @Override
        public List<VRMenuDef> getChildren(VRMenuDef t) {
            if (t.getChildren() == null) {
                ArrayList<VRMenuDef> children = new ArrayList<VRMenuDef>();
                if (t.getType().equals("package")) {
                    Package pp = UPA.getPersistenceUnit().getPackage(t.getPath(), MissingStrategy.NULL);
                    List<Package> pk = null;
                    if (pp == null) {
                        pk = new ArrayList<>();
                    } else {
                        pk = pp.getPackages();
                    }
                    HashSet<String> subFolders = new HashSet<String>();
                    for (Package p : pk) {
                        subFolders.add(p.getPath());
                        String p0 = p.getPath();
                        if (!p0.startsWith("/")) {
                            p0 = "/" + p0;
                        }
                        children.add(new VRMenuDef(i18n.get(p), p.getPath(), "package", "", "", i18n.getOrNull("Package." + p0 + ".css-icon-class")));
                    }
                    for (String p : findCustomPaths(t.getPath(), autowiredCustomMenusByCtrl)) {
                        if (!subFolders.contains(p)) {
                            subFolders.add(p);
                            String p0 = p;
                            if (!p0.startsWith("/")) {
                                p0 = "/" + p0;
                            }
                            children.add(new VRMenuDef(i18n.get("Package./" + p), p, "package", "", "", i18n.getOrNull("Package." + p0 + ".css-icon-class")));
                        }
                    }
                    for (VRMenuDef p : findCustomMenus(t.getPath(), autowiredCustomMenusByCtrl)) {
                        nonVisitedCustomMenus.remove(p);
                        if (visitedCustomMenus.contains(p)) {
                            System.err.println("Menu added twice " + p);
                        } else {
                            visitedCustomMenus.add(p);
                            nonVisitedCustomMenus.add(p);
                        }
                        children.add(p);
                    }
                    if (t.getPath().equals("/")) {
                        List<Entity> entities = filterEntities(UPA.getPersistenceUnit().getDefaulPackage().getEntities(), new DefaultEntityFilter().setAcceptSystem(false));
                        for (Entity ee : entities) {
                            if (ee.getCompositionRelation() == null) {
                                try {
                                    //DefaultEditor
                                    ee.getShield().checkNavigate();
                                    if (UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(
                                            ee.getAbsoluteName() + ".DefaultEditor")) {
                                        //UCtrlData d = ;
                                        VRMenuDef md = new VRMenuDef(i18n.get(ee), t.getPath(), "obj", "{entity:'" + ee.getName() + "'}", "", "");
                                        UCtrlData uc = getUCtrlData(md.getType(), md.getCommand());
                                        if (uc != null) {
                                            md.setName(uc.getTitle());
                                        }
                                        children.add(md);
                                    }
                                } catch (UPAException e) {
                                    //ignore
                                }
                            }
                        }
                    } else {
                        Package pp2 = UPA.getPersistenceUnit().getPackage(t.getPath(), MissingStrategy.NULL);
                        //should chek for deep in level 3!
                        if (pp2 != null) {
                            List<Entity> entities = filterEntities(pp2.getEntities(), new DefaultEntityFilter().setAcceptSystem(false));
                            for (Entity ee : entities) {
                                if (ee.getCompositionRelation() == null) {
                                    try {
                                        ee.getShield().checkNavigate();
                                        if (UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(
                                                ee.getAbsoluteName() + ".DefaultEditor")) {
                                            VRMenuDef md = new VRMenuDef(i18n.get(ee), t.getPath(), "obj", "{entity:'" + ee.getName() + "'}", "", "");
                                            UCtrlData uc = getUCtrlData(md.getType(), md.getCommand());
                                            if (uc != null) {
                                                md.setName(uc.getTitle());
                                            }
                                            children.add(md);
                                        }
                                    } catch (UPAException e) {
                                        //ignore
                                    }
                                }
                            }
                        }
                    }
                }
                t.setChildren(children);
            }
            return t.getChildren();
        }

    }
}
