/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.menu;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogAdapter;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogManager;
import net.thevpc.app.vainruling.VrMenuInfo;
import net.thevpc.app.vainruling.VrMenuLabel;
import net.thevpc.app.vainruling.VrMenuProvider;
import net.thevpc.app.vainruling.VrPageInfoResolver;
import net.thevpc.app.vainruling.VrPageInfo;
import net.thevpc.app.vainruling.VrActionEnabler;
import net.thevpc.app.vainruling.VrActionInfo;
import net.thevpc.app.vainruling.VrPageHistoryItem;
import net.thevpc.app.vainruling.VrBreadcrumbItem;
import net.thevpc.app.vainruling.VrPage;
import net.thevpc.app.vainruling.VrPathItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.thevpc.app.vainruling.core.service.ProfileRightBuilder;
import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.util.I18n;
import net.thevpc.app.vainruling.core.service.util.JsonUtils;
import net.thevpc.app.vainruling.core.service.util.PlatformReflector;
import net.thevpc.app.vainruling.core.service.util.TreeDefinition;
import net.thevpc.app.vainruling.core.service.util.TreeTraversal;
import net.thevpc.app.vainruling.core.service.util.TreeVisitor;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.collections.Collections2;
import net.thevpc.common.collections.ListValueMap;
import net.thevpc.common.collections.MapUtils;
import net.thevpc.upa.Entity;
import net.thevpc.upa.ManyToOneRelationship;
import net.thevpc.upa.MissingStrategy;
import net.thevpc.upa.UPA;
import net.thevpc.upa.VoidAction;
import net.thevpc.upa.exceptions.UPAException;
import net.thevpc.upa.filters.DefaultEntityFilter;
import net.thevpc.upa.filters.EntityFilter;
import net.thevpc.upa.filters.ObjectFilter;
import net.thevpc.app.vainruling.VrOnPageLoad;

/**
 *
 * @author vpc
 */
public class VrServiceMenu {

    Map<String, List<VrPageMenuItem>> pages = new HashMap<>();
    private LinkedList<VrPageHistoryItem> pageHistory = new LinkedList<>();
    private Model model = new Model();
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(VrServiceMenu.class.getName());

    public List<VrMenuInfo> resolveAutowiredCustomMenusByCtrl() {
        List<VrMenuInfo> menuCtrl = new ArrayList<>();

        for (String k : pages.keySet()) {
            List<VrPageMenuItem> values = pages.get(k);
            VrPageInfo vrControllerInfo = null;
            if (values.isEmpty()) {
                //never
            } else if (values.size() == 1) {
                //no conflict
                VrPageMenuItem d = values.get(0);
                menuCtrl.addAll(resolveMenuInfos(k, d.value));
            } else {
                List<List<VrMenuInfo>> bestList = null;
                int bestPrio = -1;
                for (VrPageMenuItem value : values) {
                    if (bestList == null || value.priority > bestPrio) {
                        bestList = new ArrayList<>();
                        bestList.add(resolveMenuInfos(k, value));
                        bestPrio = value.priority;
                    } else if (value.priority == bestPrio) {
                        bestList.add(resolveMenuInfos(k, value));
                    } else {
                        //ignore
                    }
                }
                for (List<VrMenuInfo> vrMenuInfos : bestList) {
                    menuCtrl.addAll(vrMenuInfos);
                }
            }
        }
        return menuCtrl;
    }

    public boolean isCurrentPageId(String name) {
        if (StringUtils.isBlank(name)) {
            return StringUtils.isBlank(getModel().getCurrentPageId());
        } else {
            return name.equals(getModel().getCurrentPageId());
        }
    }

    private List<VrMenuInfo> resolveMenuInfos(String beanName, Object b) {
        List<VrMenuInfo> menuCtrl = new ArrayList<>();
        VrPage c = (VrPage) PlatformReflector.getTargetClass(b).getAnnotation(VrPage.class);
        if (c != null && c.menu().length() > 0) {
            String menu = c.menu();
            if (!menu.startsWith("/")) {
                menu = "/" + menu;
            }
            String title = I18n.get().getOrNull("Controller." + beanName);
            if (StringUtils.isBlank(title)) {
                title = c.title();
            }
            VrActionEnabler actionEnabler = null;
            if (b instanceof VrActionEnabler) {
                actionEnabler = (VrActionEnabler) b;
            }
            VrMenuInfo md = new VrMenuInfo(title, menu, beanName, "", c.securityKey(), actionEnabler, "", c.order(), new VrMenuLabel[0]);
            VrPageInfoAndObject uc = resolvePageInfoAndObjectByInstance(md.getType(), md.getCommand());
            if (uc != null) {
                md.setName(uc.getInfo().getTitle());
            }
            if (md.getSecurityKey() == null
                    || md.getSecurityKey().length() == 0
                    || UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(md.getSecurityKey())) {
                if (isEnabled(md.getEnabler(), md, beanName)) {
                    menuCtrl.add(md);
                }
            }
        }
        if (b instanceof VrMenuProvider) {
            for (VrMenuInfo md : ((VrMenuProvider) b).createCustomMenus()) {
                if (md != null) {
                    applyVRMenuInfoSource(md, beanName);
                    if (md.getSecurityKey() == null
                            || md.getSecurityKey().length() == 0
                            || UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(md.getSecurityKey())) {
                        VrPageInfoAndObject uc = resolvePageInfoAndObjectByInstance(md.getType(), md.getCommand());
                        if (uc != null) {
                            md.setName(uc.getInfo().getTitle());
                            if (uc.getInfo().getEnabler() != null) {
                                md.setEnabler(uc.getInfo().getEnabler());
                            }
                        }
                        menuCtrl.add(md);
                    }
                }
            }
        }
        return menuCtrl;
    }

    private void applyVRMenuInfoSource(VrMenuInfo m, String source) {
        if (m == null) {
            return;
        }
        if (m.getSource() == null) {
            m.setSource(source);
        }
        List<VrMenuInfo> children = m.getChildren();
        if (children != null) {
            for (VrMenuInfo c : children) {
                applyVRMenuInfoSource(c, source);
            }
        }
    }

    public VrPageInfoAndObject resolvePageInfoAndObjectByInstance(String name, String cmd) {
        if (name == null) {
            return null;
        }
        List<VrPageMenuItem> controllerInfos = pages.get(name);
        if (controllerInfos == null) {
            if (!name.endsWith("Ctrl")) {
                name = name + "Ctrl";
                controllerInfos = pages.get(name);
            }
        }
        if (controllerInfos == null) {
            return null;
        }
        for (VrPageMenuItem controllerInfo : controllerInfos) {
            Object obj = controllerInfo.value;
            Class targetClass = PlatformReflector.getTargetClass(obj);
            if (obj instanceof VrPageInfoResolver) {
                VrPageInfoResolver up = (VrPageInfoResolver) obj;
                VrPageInfo vrControllerInfo = up.resolvePageInfo(cmd);
                if (vrControllerInfo != null) {
                    return new VrPageInfoAndObject(vrControllerInfo, obj);
                }
            }
            if (obj instanceof VrActionEnabler) {
                VrActionEnabler r = (VrActionEnabler) obj;
                String name2 = name;
                VrActionInfo vv = new VrActionInfo() {
                    @Override
                    public String getMenuPath() {
                        VrPage c2Ann = (VrPage) targetClass.getAnnotation(VrPage.class);
                        if (c2Ann != null) {
                            return c2Ann.menu();
                        }
                        return null;
                    }

                    @Override
                    public String getSource() {
                        return name2;
                    }
                };
                if (!isEnabled(r, vv, cmd)) {
                    continue;
                }
            }
            VrPage c2Ann = (VrPage) targetClass.getAnnotation(VrPage.class);
            if (c2Ann != null) {
                VrPageInfo vrControllerInfo = resolvePageInfoByAnnotation(obj, c2Ann, name, cmd);
                if (vrControllerInfo != null) {
                    return new VrPageInfoAndObject(vrControllerInfo, obj);
                }
            }
        }
        return null;
    }

    public String getPageUniformName(String typeName) {
        String uniformCtrlName = typeName;
        if (uniformCtrlName.endsWith("Ctrl") && uniformCtrlName.length() > "Ctrl".length()) {
            uniformCtrlName = uniformCtrlName.substring(0, uniformCtrlName.length() - "Ctrl".length());
        }
        return uniformCtrlName;
    }

    public VrPageInfo resolvePageInfoByAnnotation(Object obj, VrPage c2Ann, String typeName, String cmd) {
        if (c2Ann != null) {
            String uniformCtrlNameBase = typeName;
            String uniformCtrlName = getPageUniformName(typeName);
            VrPageInfo d = new VrPageInfo();
            d.setAcceptAnonymous(c2Ann.acceptAnonymous());
            d.setCmd(cmd);
            d.setControllerName(uniformCtrlName);
            String title = I18n.get().getOrNull("Controller." + uniformCtrlNameBase);
            if (StringUtils.isBlank(title)) {
                title = I18n.get().getOrNull("Controller." + uniformCtrlName);
            }
            if (StringUtils.isBlank(title)) {
                title = c2Ann.title();
            }
            if (StringUtils.isBlank(title)) {
                title = "Controller." + uniformCtrlName;
            }
            d.setTitle(title);

            String subTitle = I18n.get().getOrNull("Controller." + uniformCtrlNameBase + ".subTitle");
            if (StringUtils.isBlank(subTitle)) {
                subTitle = I18n.get().getOrNull("Controller." + uniformCtrlName + ".subTitle");
            }
            if (StringUtils.isBlank(subTitle)) {
                subTitle = c2Ann.subTitle();
            }
            d.setSubTitle(subTitle);

            String css = I18n.get().getOrNull("Controller." + uniformCtrlNameBase + ".css");
            if (StringUtils.isBlank(css)) {
                css = I18n.get().getOrNull("Controller." + uniformCtrlName + ".css");
            }
            if (StringUtils.isBlank(css)) {
                css = c2Ann.css();
            }
            d.setCss(css);

            String url = VrUtils.evalSpringExprMessage(c2Ann.url());
            d.setUrl(url);
            d.setMenuPath(c2Ann.menu());
            d.setSource(typeName);
            d.setSecurityKey(c2Ann.securityKey());
            List<VrBreadcrumbItem> aa = new ArrayList<>();
            for (VrPathItem breadcrumb : c2Ann.breadcrumb()) {
                title = breadcrumb.title();
                subTitle = breadcrumb.subTitle();
                css = breadcrumb.css();
                String ctrl = breadcrumb.ctrl();
                String cmd2 = breadcrumb.cmd();
                if (!StringUtils.isBlank(ctrl)) {
                    VrPageInfoAndObject vrControllerInfoAndObject = resolvePageInfoAndObjectByInstance(ctrl, cmd2);
                    if (vrControllerInfoAndObject != null) {
                        VrPageInfo d2 = vrControllerInfoAndObject.getInfo();
                        if (d2 != null) {
                            if (StringUtils.isBlank(css)) {
                                css = d2.getCss();
                            }
                            if (StringUtils.isBlank(title)) {
                                title = d2.getTitle();
                            }
                            if (StringUtils.isBlank(subTitle)) {
                                subTitle = d2.getSubTitle();
                            }
                        }
                    }
                }
                aa.add(new VrBreadcrumbItem(title, subTitle, css, ctrl, cmd2));
            }
            d.setBreadcrumb(aa.toArray(new VrBreadcrumbItem[aa.size()]));
            if (obj instanceof VrActionEnabler) {
                d.setEnabler((VrActionEnabler) obj);
            }
            return d;
        }
        return null;
    }

    private boolean isSelectedVRMenuInfo(VrMenuInfo t) {
        if (t.isLeaf()) {
            VrPageInfo ci = getModel().getControllerInfo();
            if (ci != null) {
                String ctrlName = getPageUniformName(t.getType());
                if (ci.getControllerName() != null && ci.getControllerName().endsWith(ctrlName)) {
                    String cmd = StringUtils.trim(ci.getCmd());
                    String cmd2 = StringUtils.trim(t.getCommand());
                    if (cmd.equals(cmd2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<VrPageHistoryItem> getPageHistory() {
        return pageHistory;
    }

    public Model getModel() {
        return model;
    }

    public void build() {
        final CorePlugin core = VrApp.getBean(CorePlugin.class);

        ListValueMap<String, VrPageMenuItem> controllers0 = Collections2.arrayListValueHashMap();
        UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                Map<String, Object> beansMapForAnnotations = VrApp.getBeansMapForAnnotations(VrPage.class);
                ProfileRightBuilder bb = new ProfileRightBuilder();
                for (Map.Entry<String, Object> beanEntry : beansMapForAnnotations.entrySet()) {
                    Object b = beanEntry.getValue();
                    VrPage c = (VrPage) PlatformReflector.getTargetClass(b).getAnnotation(VrPage.class);
                    if (c != null) {
                        if (!StringUtils.isBlank(c.replacementFor())) {
                            controllers0.add(c.replacementFor(), new VrPageMenuItem(beanEntry.getKey(), beanEntry.getValue(), c.priority()));
                        } else {
                            controllers0.add(beanEntry.getKey(), new VrPageMenuItem(beanEntry.getKey(), beanEntry.getValue(), c.priority()));
                        }
                        final String securityKey = c.securityKey();
                        final String[] securityKeys = c.declareSecurityKeys();
                        if (!StringUtils.isBlank(securityKey)) {
                            bb.addName(securityKey);
                        }
                        for (String key : securityKeys) {
                            if (!StringUtils.isBlank(key)) {
                                bb.addName(key);
                            }
                        }

                    }

                    if (b instanceof VrMenuProvider) {
                        for (VrMenuInfo md : ((VrMenuProvider) b).createCustomMenus()) {

                            if (md != null) {
                                String securityKey = md.getSecurityKey();
                                if (!StringUtils.isBlank(securityKey)) {
                                    bb.addName(securityKey);
                                }
                                applyVRMenuInfoSource(md, beanEntry.getKey());
                            }
                        }
                    }
                }

                ActionDialogManager actionDialogManager = VrApp.getBean(ActionDialogManager.class);
                for (Entity entity : UPA.getPersistenceUnit().getEntities()) {
                    for (ActionDialogAdapter a : actionDialogManager.findActionsByEntity(entity.getName())) {
                        if (!StringUtils.isBlank(a.getId())) {
                            bb.addName(a.getId());
                        }
                    }
                }
                bb.execute();
            }

        });

        for (String s : controllers0.keySet()) {
            ArrayList<VrPageMenuItem> list = new ArrayList<>(controllers0.getValues(s));
            Collections.sort(list, new Comparator<VrPageMenuItem>() {
                @Override
                public int compare(VrPageMenuItem o1, VrPageMenuItem o2) {
                    return o2.priority - o1.priority;
                }
            });
            pages.put(s, list);
        }
    }

    public static class Model {

        private List<VrBreadcrumbItem> breadcrumb = new ArrayList<VrBreadcrumbItem>();
        private VrBreadcrumbItem titleCrumb = new VrBreadcrumbItem("", "", "", "", "");
        private List<VrMenuInfo> menuCtrl = null;
        private VrPageInfo controllerInfo = null;
        private String currentPageId;
        private VrMenuInfo root;
        private String searchText;

        public VrPageInfo getControllerInfo() {
            return controllerInfo;
        }

        public void setControllerInfo(VrPageInfo controllerInfo) {
            this.controllerInfo = controllerInfo;
        }

        public String getSearchText() {
            return searchText;
        }

        public void setSearchText(String searchText) {
            this.searchText = searchText;
        }

        public VrMenuInfo getRoot() {
            return root;
        }

        public Model setRoot(VrMenuInfo root) {
            this.root = root;
            return this;
        }

        public List<VrBreadcrumbItem> getBreadcrumb() {
            return breadcrumb;
        }

        public void setBreadcrumb(List<VrBreadcrumbItem> breadcrumb) {
            this.breadcrumb = breadcrumb;
            for (int i = 0; i < breadcrumb.size(); i++) {
                breadcrumb.get(i).setActive(i == breadcrumb.size() - 1);
            }
//            if (this.breadcrumb.isEmpty()) {
//                titleCrumb = new BreadcrumbItem("", "", "", "", "");
//                titleCrumb.setActive(false);
//            } else {
//                titleCrumb = breadcrumb.get(breadcrumb.size() - 1);
//            }
        }

        public void setTitleCrumb(VrBreadcrumbItem titleCrumb) {
            this.titleCrumb = titleCrumb;
        }

        public void setBreadcrumb(VrBreadcrumbItem... breadcrumb) {
            setBreadcrumb(Arrays.asList(breadcrumb));
        }

        public String getCurrentPageId() {
            return currentPageId;
        }

        public void setCurrentPageId(String currentPageId) {
            this.currentPageId = currentPageId;
        }

        public VrBreadcrumbItem getTitleCrumb() {
            return titleCrumb;
        }

        public List<VrMenuInfo> getMenuCtrl() {
            return menuCtrl;
        }
    }

    public VrPageHistoryItem peekHistory() {
        if (pageHistory.size() > 0) {
            return pageHistory.getLast();
        }
        return null;
    }

    public VrPageHistoryItem popHistory() {
        if (pageHistory.size() > 0) {
            return pageHistory.removeLast();
        }
        return null;
    }

    public void pushHistory(String command, Object arguments) {
        if (arguments == null) {
            arguments = "";
        } else if (arguments instanceof String) {
            //okkay
        } else {
            arguments = VrUtils.formatJSONObject(arguments);
        }
        VrPageHistoryItem pi = new VrPageHistoryItem(command, String.valueOf(arguments));
        if (pageHistory.size() > 0) {
            if (pageHistory.getLast().equals(pi)) {
                return;
            }
        }
        pageHistory.add(pi);
        if (pageHistory.size() > 20) {
            pageHistory.removeFirst();
        }
    }

    public void prepareGoto(String command, String arguments, VrPageInfoAndObject d, List<VrBreadcrumbItem> bc, boolean addToHistory) {
        PlatformReflector.InstanceInvoker[] mm = PlatformReflector.findInstanceMethodsByAnnotation(d.getInstance(), VrOnPageLoad.class);
        for (PlatformReflector.InstanceInvoker m : mm) {
            if (m.getParameterTypes().length == 0) {
                m.invoke();
            } else if (m.getParameterTypes().length == 1) {
                m.invoke(JsonUtils.parse(arguments, m.getParameterTypes()[0]));
            }
        }
        getModel().setControllerInfo(d == null ? null : d.getInfo());
        getModel().setBreadcrumb(bc);
        getModel().setTitleCrumb((d != null) ? new VrBreadcrumbItem(d.getInfo().getTitle(), d.getInfo().getSubTitle(), d.getInfo().getCss(), "", "") : new VrBreadcrumbItem("", "", "", "", ""));
//        UserSession s = CorePlugin.get().getCurrentSession();
        StringBuilder lvp = new StringBuilder();
        for (VrBreadcrumbItem breadcrumbItem : bc) {
            if (!StringUtils.isBlank(breadcrumbItem.getTitle())) {
                if (lvp.length() > 0) {
                    lvp.append(", ");
                }
                lvp.append(breadcrumbItem.getTitle());
            }
        }
        if (!StringUtils.isBlank(getModel().getTitleCrumb().getTitle())) {
            if (lvp.length() > 0) {
                lvp.append(", ");
            }
            lvp.append(getModel().getTitleCrumb().getTitle());
        }
        if (!StringUtils.isBlank(getModel().getTitleCrumb().getSubTitle())) {
            if (lvp.length() > 0) {
                lvp.append(", ");
            }
            lvp.append(getModel().getTitleCrumb().getSubTitle());
        }
        String data = command;
        if (!StringUtils.isBlank(arguments)) {
            data += " ; " + arguments;
        }
        TraceService.get().trace("System.visit-page", null, MapUtils.map("path", lvp.toString(), "command", data), "/System/Access", Level.FINE);
        if (addToHistory) {
            pushHistory(command, arguments);
        }
    }

    private List<String> findCustomPaths(String parent, List<VrMenuInfo> autowiredCustomMenusByCtrl) {
        LinkedHashSet<String> ok = new LinkedHashSet<>();
        if (parent == null) {
            parent = "/";
        }
        String parent2 = parent;
        if (!parent2.endsWith("/")) {
            parent2 = parent2 + "/";
        }
        for (VrMenuInfo m : autowiredCustomMenusByCtrl) {
            String p = m.getPath();
            if (p == null) {
                p = "";
            }
            p = p.replace('.', '/');
            if (!p.startsWith("/")) {
                p = "/" + p;
            }
            if (p.toLowerCase().startsWith(parent2.toLowerCase()) && !p.toLowerCase().equals(parent2.toLowerCase())) {
                String p2 = p.substring(parent2.length() - 1);
                if (p2.length() > 0) {
                    int c = p2.indexOf("/", 1);
                    String path = null;
                    if (c < 0) {
                        path = parent2 + p2.substring(1);
                    } else {
                        path = parent2 + p2.substring(1, c);
                    }
                    if (!ok.contains(path)) {
                        ok.add(path);
                    }
                }
            }
        }
        return new ArrayList<>(ok);
    }

    public VrMenuInfo createMenu(String searchText) {
        String finalSearchText = (searchText == null ? "" : searchText.toLowerCase());
        ObjectFilter<String> menuFilter = StringUtils.isBlank(searchText) ? new ObjectFilter<String>() {
            @Override
            public boolean accept(String value) {
                return true;
            }
        } : new ObjectFilter<String>() {
            @Override
            public boolean accept(String value) {
                if (value == null) {
                    value = "";
                }
                return value.toLowerCase().contains(finalSearchText);
            }
        };
        return (new VrMenuTree(menuFilter, this, I18n.get(), CorePlugin.get()).root);
    }

    private List<VrMenuInfo> findCustomMenus(String parent, List<VrMenuInfo> autowiredCustomMenusByCtrl) {
        List<VrMenuInfo> ok = new ArrayList<>();
        if (parent == null) {
            parent = "/";
        }
        for (VrMenuInfo m : autowiredCustomMenusByCtrl) {
            String p = m.getPath();
            if (p == null) {
                p = "";
            }
            p = p.replace('.', '/');
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

    private List<Entity> filterEntities(List<Entity> in, EntityFilter f) {
        ArrayList<Entity> out = new ArrayList<>();
        for (Entity i : in) {
            if (f.accept(i)) {
                out.add(i);
            }
        }
        return out;
    }

    private List<Entity> filterEntities(List<Entity> in) {
        return filterEntities(in, new DefaultEntityFilter().setAcceptSystem(false));
    }

    public VrPageHistoryItem goBack() {
        if (pageHistory.size() > 0) {
            pageHistory.removeLast();
            if (pageHistory.size() > 0) {
                VrPageHistoryItem last = pageHistory.getLast();
                return last;
            }
        }
        return null;
    }

    public String gotoPage(String command, String arguments, boolean addToHistory) {
        VrPageInfoAndObject d = resolvePageInfoAndObjectByInstance(command, arguments);
        boolean loggedIn = CorePlugin.get().isLoggedIn();
        if (!loggedIn && !d.getInfo().isAcceptAnonymous()) {
            return null;
        }
//        String goodBeanName = resolveGoodBeanName(o);
        List<VrBreadcrumbItem> bc = new ArrayList<>();
        String url = null;
        if (d != null) {
            bc.addAll(Arrays.asList(d.getInfo().getBreadcrumb()));
            url = d.getInfo().getUrl();
            CorePlugin core = CorePlugin.get();
            if (!StringUtils.isBlank(d.getInfo().getSecurityKey())) {
                if (!"welcome".equals(command) && !UPA.getPersistenceUnit().getSecurityManager().isAllowedKey(d.getInfo().getSecurityKey())) {
                    if ("publicIndex".equals(command)) {
                        throw new SecurityException("Page is Inaccessible");
                    } else {
                        LOG.warning("Illegal Access to " + command + " by " + core.getCurrentUser() + ". Required Authorization : " + d.getInfo().getSecurityKey());
                        //FacesUtils.addErrorMessage("Illegal Access to " + command + " by " + core.getCurrentUser());
                        if (core.getCurrentUser() != null) {
                            return gotoPage("welcome", "", true);
                        } else {
                            return gotoPage("publicIndex", "", true);
                        }
                    }
                }
            }
            if (!isEnabled(d.getInfo().getEnabler(), d.getInfo(), command)) {
                return gotoPage("publicIndex", "", true);
            }
        }
        if (d == null) {
            return null;
        }
        prepareGoto(command, arguments, d, bc, addToHistory && !StringUtils.isBlank(url));
        if (StringUtils.isBlank(url)) {
            return null;
        }
        return url;
    }

    private boolean isEnabled(VrActionEnabler e, VrActionInfo data, String command) {
        if (e == null) {
            return true;
        }
        CorePlugin core = CorePlugin.get();
        boolean d = false;
        try {
            e.checkEnabled(data);
            d = true;
        } catch (SecurityException ex) {
            LOG.log(Level.WARNING, "Illegal Access to {0} by {1}. Disabled because of : {2}", new Object[]{command, core.getCurrentUser(), ex.getMessage()});
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Illegal Access to {0} by {1}. Disabled by Error in Enabler : {2}", new Object[]{command, core.getCurrentUser(), e});
        }
        return d;
    }

    private static class MenuTreeVisitor implements TreeVisitor<VrMenuInfo> {

        private ObjectFilter<String> filter;
        private VrServiceMenu m;

        public MenuTreeVisitor(ObjectFilter<String> filter, VrServiceMenu m) {
            this.filter = filter;
            this.m = m;
        }

        @Override
        public void visit(VrMenuInfo t, TreeDefinition<VrMenuInfo> tree) {
            if (m.isSelectedVRMenuInfo(t)) {
                t.setSelected(true);
            }
            List<VrMenuInfo> children = tree.getChildren(t);
            for (int i = children.size() - 1; i >= 0; i--) {
                VrMenuInfo el = children.get(i);
                boolean removed = false;
                if (el.getType().equals("package") && el.getChildren().isEmpty()) {
                    children.remove(i);
                    removed = true;
                } else {
                    if (el.getChildren().isEmpty()) {
                        if (!filter.accept(el.getName())) {
                            children.remove(i);
                            removed = true;
                        }
                    }
                }
                if (!removed) {
                    if (el.isSelected()) {
                        t.setSelected(true);
                    }
                }
            }
        }
    }

    public static class VrMenuTree implements TreeDefinition<VrMenuInfo> {

        final VrMenuInfo root = new VrMenuInfo("/", "/", "package", "", "", null, "", 0, new VrMenuLabel[0]);
        List<VrMenuInfo> autowiredCustomMenusByCtrl;
        final HashSet<VrMenuInfo> nonVisitedCustomMenus;
        final HashSet<VrMenuInfo> visitedCustomMenus = new HashSet<>();
        CorePlugin core;
        I18n i18n;
        VrServiceMenu builder;

        public VrMenuTree(ObjectFilter<String> filter, VrServiceMenu builder, I18n i18n, CorePlugin core) {
            this.i18n = i18n;
            this.core = core;
            this.builder = builder;
            autowiredCustomMenusByCtrl = builder.resolveAutowiredCustomMenusByCtrl();
            nonVisitedCustomMenus = new HashSet<>(autowiredCustomMenusByCtrl);
            TreeTraversal.postOrderTreeTraversal(this, new MenuTreeVisitor(filter, builder));
        }

        @Override
        public VrMenuInfo getRoot() {
            return root;
        }

        @Override
        public List<VrMenuInfo> getChildren(VrMenuInfo t) {
            if (t.getChildren() == null) {
                ArrayList<VrMenuInfo> children = new ArrayList<VrMenuInfo>();
                if (t.getType().equals("package")) {
                    net.thevpc.upa.Package pp = UPA.getPersistenceUnit().getPackage(t.getPath(), MissingStrategy.NULL);
                    List<net.thevpc.upa.Package> pk = null;
                    if (pp == null) {
                        pk = new ArrayList<>();
                    } else {
                        pk = pp.getPackages();
                    }
                    HashSet<String> subFolders = new HashSet<String>();
                    for (net.thevpc.upa.Package p : pk) {
                        subFolders.add(p.getPath());
                        String p0 = p.getPath();
                        if (!p0.startsWith("/")) {
                            p0 = "/" + p0;
                        }
                        String p1 = p0;
                        p1 = p1.replace('/', '.');
                        if (p1.startsWith(".")) {
                            p1 = p1.substring(1);
                        }
                        String orderString = i18n.getOrNull("Package." + p1 + ".order");
                        int order = 100;
                        try {
                            if (!StringUtils.isBlank(orderString)) {
                                order = Integer.parseInt(orderString);
                            }
                        } catch (Exception e) {
                            //ignore
                        }
                        children.add(new VrMenuInfo(p.getTitle(), p0, "package", "", "", null, i18n.getOrNull("Package." + p1 + ".css-icon-class"), order, new VrMenuLabel[0]));
                    }
                    for (String p : builder.findCustomPaths(t.getPath(), autowiredCustomMenusByCtrl)) {
                        if (!subFolders.contains(p)) {
                            subFolders.add(p);
                            String p0 = p;
                            if (!p0.startsWith("/")) {
                                p0 = "/" + p0;
                            }
                            String p1 = p0;
                            p1 = p1.replace('/', '.');
                            if (p1.startsWith(".")) {
                                p1 = p1.substring(1);
                            }
                            String orderString = i18n.getOrNull("Package." + p1 + ".order");
                            int order = 100;
                            try {
                                if (!StringUtils.isBlank(orderString)) {
                                    order = Integer.parseInt(orderString);
                                }
                            } catch (Exception e) {
                                //ignore
                            }

                            children.add(new VrMenuInfo(i18n.get("Package." + p1), p0, "package", "", "", null, i18n.getOrNull("Package." + p1 + ".css-icon-class"), order, new VrMenuLabel[0]));
                        }
                    }
                    for (VrMenuInfo p : builder.findCustomMenus(t.getPath(), autowiredCustomMenusByCtrl)) {
                        nonVisitedCustomMenus.remove(p);
                        if (visitedCustomMenus.contains(p)) {
                            System.err.println("Menu added twice " + p);
                        } else {
                            visitedCustomMenus.add(p);
                            nonVisitedCustomMenus.add(p);
                        }
                        if (p.getType() == null || !core.isInaccessibleComponent(p.getType())) {
                            children.add(p);
                        }
                    }
                    if (t.getPath().equals("/")) {
                        List<Entity> entities = builder.filterEntities(UPA.getPersistenceUnit().getDefaultPackage().getEntities(), new DefaultEntityFilter().setAcceptSystem(false));
                        for (Entity ee : entities) {
                            if (acceptEntityMenu(ee)) {
                                try {
                                    int order = 100;
                                    //UCtrlData d = ;
                                    VrMenuInfo md = new VrMenuInfo(ee.getTitle(), t.getPath(), "editor", "{entity:'" + ee.getName() + "'}", "", null, "", 100, new VrMenuLabel[0]);
                                    VrPageInfoAndObject uc = builder.resolvePageInfoAndObjectByInstance(md.getType(), md.getCommand());
                                    if (uc != null) {
                                        md.setName(uc.getInfo().getTitle());
                                    }
                                    children.add(md);
                                } catch (UPAException e) {
                                    //ignore
                                }
                            }
                        }
                    } else {
                        net.thevpc.upa.Package pp2 = UPA.getPersistenceUnit().getPackage(t.getPath(), MissingStrategy.NULL);
                        //should check for deep in level 3!
                        if (pp2 != null) {
                            List<Entity> entities = builder.filterEntities(pp2.getEntities(), new DefaultEntityFilter().setAcceptSystem(false));
                            for (Entity ee : entities) {
                                if (acceptEntityMenu(ee)) {
                                    try {
                                        int order = 100;
                                        VrMenuInfo md = new VrMenuInfo(ee.getTitle(), t.getPath(), "editor", "{entity:'" + ee.getName() + "'}", "", null, "", 100, new VrMenuLabel[0]);
                                        VrPageInfoAndObject uc = builder.resolvePageInfoAndObjectByInstance(md.getType(), md.getCommand());
                                        if (uc != null) {
                                            md.setName(uc.getInfo().getTitle());
                                        }
                                        children.add(md);
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

        public boolean acceptEntityMenu(Entity ee) {
            if (ee.getCompositionRelation() == null
                    || (ee.getCompositionRelation() instanceof ManyToOneRelationship && ((ManyToOneRelationship) ee.getCompositionRelation()).getHierarchyExtension() != null)) {
                try {
                    ee.getShield().checkNavigate();
                    if (UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(
                            CorePluginSecurity.getEntityRightEditor(ee))) {
                        if (!core.isInaccessibleEntity(ee.getName())) {
                            return true;
                        }
                    }
                } catch (UPAException e) {
                    //ignore
                }
            }
            return false;
        }

    }

    public static class GotoPageInfo {

    }

}
