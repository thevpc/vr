/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.core.service.pages.VrPageInfo;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.content.TaskTextService;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.web.jsf.ctrl.AbstractObjectCtrl;

import net.vpc.app.vainruling.core.service.pages.VrBreadcrumbItem;
import net.vpc.app.vainruling.core.service.menu.VRMenuInfo;
import net.vpc.app.vainruling.core.service.menu.VRMenuLabel;
import net.vpc.app.vainruling.core.web.jsf.VrJsf;
import net.vpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.vpc.app.vainruling.plugins.tasks.service.model.*;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.AccessMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.vpc.app.vainruling.core.service.menu.VRMenuProvider;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.pages.VrPageInfoResolver;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.upa.UPA;
import org.primefaces.event.SlideEndEvent;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Todo", css = "fa-dashboard", ctrl = "")
        }, url = "modules/todo/todos"
)
@Scope("singleton")
public class TodoCtrl extends AbstractObjectCtrl<Todo> implements VRMenuProvider, VrPageInfoResolver, TaskTextService {

    @Autowired
    private TaskPlugin todoService;
    @Autowired
    private CorePlugin coreService;

    public TodoCtrl() {
        super(null);
    }

    public TodoModel getModel() {
        return (TodoModel) VrApp.getBean(TodoModel.class);
    }

    public List<TodoList> findMyActions() {
        return todoService.findTodoListsByResp(null);
    }

    @Override
    public VrBreadcrumbItem getTitle() {
        VrBreadcrumbItem b = super.getTitle();
        TodoList list = getCurrentTodoList();
        if (TodoList.LABO_ACTION.equals(list.getName())) {
            b.setTitle("Mes Actions Labo");
        } else if (TodoList.LABO_TICKET.equals(list.getName())) {
            b.setTitle("Mes Ticktes Labo");
        } else {
            b.setTitle(list.getName());
        }
        return b;
    }

    public void onEstimationSlideEnd(SlideEndEvent event) {
        getModel().getCurrent().setEstimation(event.getValue());
    }

    public void onPrioritySlideEnd(SlideEndEvent event) {
        getModel().setCurrentPriority(event.getValue());
    }

    public void onProgressSlideEnd(SlideEndEvent event) {
        getModel().getCurrent().setProgress(event.getValue());
    }

    @Override
    public int getSupport(String name) {
        return "Todo".equals(name) ? 1 : -1;
    }

    @Override
    protected Todo delegated_newInstance() {
        final Todo todo = new Todo();
        todo.setArchived(false);
        todo.setInitiator(coreService.getCurrentUser());
        todo.setResponsible(coreService.getCurrentUser());
        todo.setPriority(TodoPriority.DEFAULT);
        TodoList list = getCurrentTodoList();
        todo.setList(list);
        TodoStatus s = todoService.findStartStatus(list.getStatusGroup().getId());
        todo.setStatus(s);
        return todo;
    }

    public TodoList getCurrentTodoList() {
        return todoService.findTodoList(getModel().getListName());
    }

    public List<String> onCompleteCategory(String n) {
        n = VrUtils.normalizeName(n);
        TodoList list = getCurrentTodoList();
        Set<String> ok = new HashSet<>();
        if (list != null) {
            List<TodoCategory> p = todoService.findTodoCategories(list.getId());
            for (TodoCategory t : p) {
                ok.add(t.getName());
            }
        }
        if (ok.isEmpty()) {
            ok.add("Divers");
            ok.add("Perso");
            ok.add("Travail");
        }
        for (Iterator<String> it = ok.iterator(); it.hasNext();) {
            String s = it.next();
            if (!n.isEmpty() && !VrUtils.normalizeName(s).contains(n)) {
                it.remove();
            }
        }
        return new ArrayList<>(ok);
    }

    public boolean isCurrentOwned() {
        AppUser r = getModel().getCurrent().getResponsible();
        AppUser me = coreService.getCurrentUser();
        return r != null && me != null && r.getId() == me.getId();
    }

    public boolean isEnabledButton(String buttonId) {
        if ("Archive".equals(buttonId)) {
            return getModel().getMode() == AccessMode.UPDATE
                    && getModel().getCurrent().getStatus().getType() == TodoStatusType.DONE;
        }
        if ("Assign".equals(buttonId)) {
            return getModel().getMode() == AccessMode.UPDATE
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.ASSIGNED) != null;
        }
        if ("Unassign".equals(buttonId)) {
            return getModel().getMode() == AccessMode.UPDATE
                    && isCurrentOwned()
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.UNASSIGNED) != null;
        }

        if ("Done".equals(buttonId)) {
            return getModel().getMode() == AccessMode.UPDATE
                    && isCurrentOwned()
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.DONE) != null;
        }
        if ("Verify".equals(buttonId)) {
            return getModel().getMode() == AccessMode.UPDATE
                    && isCurrentOwned()
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.TO_VERIFY) != null;
        }
        if ("Reopen".equals(buttonId)) {
            return getModel().getMode() == AccessMode.UPDATE
                    && isCurrentOwned()
                    && (getModel().getCurrent().getStatus().getType() == TodoStatusType.TO_VERIFY || getModel().getCurrent().getStatus().getType() == TodoStatusType.DONE)
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.ASSIGNED) != null;
        }
        if ("New".equals(buttonId)) {
            return getModel().getMode() == AccessMode.READ;
        }
        return super.isEnabledButton(buttonId);
    }

    public void updateCurrentCategory() {
        Todo todo = getModel().getCurrent();
        String n = getModel().getCurrentCategoryName();
        if (StringUtils.isBlank(n)) {
            todo.setCategory(null);
        } else {
            todo.setCategory(todoService.addTodoCategory(todo.getList().getId(), n));
        }
    }

    public void onAssignCurrent() {
        updateCurrentCategory();
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.ASSIGNED);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
            reloadPage(true);
        }
    }

    public void onUnassignCurrent() {
        updateCurrentCategory();
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.UNASSIGNED);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
            reloadPage(true);
        }
    }

    public void onDoneCurrent() {
        updateCurrentCategory();
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.DONE);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
            reloadPage(true);
        }
    }

    public void onVerifyCurrent() {
        updateCurrentCategory();
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.TO_VERIFY);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
            reloadPage(true);
        }
    }

    public void onReopenCurrent() {
        updateCurrentCategory();
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.ASSIGNED);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
            reloadPage(true);
        }
    }

    public void onSaveCurrent() {
        updateCurrentCategory();
        switch (getModel().getMode()) {
            case PERSIST: {
                Todo c = getModel().getCurrent();
                todoService.saveTodo(c);
                onCancelCurrent();
                break;
            }
            case UPDATE: {
                Todo c = getModel().getCurrent();
                todoService.saveTodo(c);
                onCancelCurrent();
                break;
            }
        }
        reloadPage(true);
        getModel().setMode(AccessMode.READ);
    }

    public void onDeleteCurrent() {
        updateCurrentCategory();
        Todo c = getModel().getCurrent();
        todoService.removeTodo(c.getId());
        reloadPage(true);
        getModel().setMode(AccessMode.READ);
    }

    public void onArchiveCurrent() {
        updateCurrentCategory();
        Todo c = getModel().getCurrent();
        todoService.archiveTodo(c.getId());
        getModel().setCurrent(new Todo());
        reloadPage(true);
        getModel().setMode(AccessMode.READ);
    }

    @OnPageLoad
    public void reloadPage(String cmd) {
        reloadPage(cmd, true);
    }

    public void onAddConsumption(double value) {
        Todo v = getModel().getCurrent();
        double n = v.getConsumption() + value;
        if (n < 0) {
            n = 0;
        }
        v.setConsumption(n);
    }

    @Override
    public void reloadPage(String cmd, boolean ustomization) {
        if (!StringUtils.isBlank(cmd)) {
            getModel().setListName(cmd);
            getModel().setCmd(cmd);
        }
        refreshList();
    }

    public void refreshList() {
        TodoList list = getCurrentTodoList();
        int currentListId = list.getId();
        getModel().setTodo(todoService.findTodosByResponsible(currentListId, null, new TodoStatusType[]{TodoStatusType.UNASSIGNED}));
        getModel().setInProgress(todoService.findTodosByResponsible(currentListId, null, new TodoStatusType[]{TodoStatusType.ASSIGNED}));
        getModel().setToVerify(todoService.findTodosByResponsible(currentListId, null, new TodoStatusType[]{TodoStatusType.TO_VERIFY}));
        getModel().setDone(todoService.findTodosByResponsible(currentListId, null, new TodoStatusType[]{TodoStatusType.DONE}));

        getModel().setStatuses(todoService.findTodoStatuses(list.getStatusGroup().getId()));
        getModel().setStatusItems(VrJsf.toSelectItemList(getModel().getStatuses()));
        getModel().setCategories(todoService.findTodoCategories(currentListId));
        getModel().setCategoryItems(VrJsf.toSelectItemList(getModel().getCategories()));
        getModel().setResponsibleItems(VrJsf.toSelectItemList(todoService.findCollaborators(list)));

    }

    public boolean isSharableList() {
        TodoList l = getCurrentTodoList();
        if (l == null) {
            return false;
        }
        CorePlugin a = CorePlugin.get();
        int u = a.getCurrentUserIdFF();
        if (u < 0) {
            return false;
        }
        if (a.isCurrentSessionAdmin()) {
            return true;
        }
        //default is true...
        return true;
    }

    public boolean isListManager() {
        TodoList l = getCurrentTodoList();
        CorePlugin a = CorePlugin.get();
        int u = a.getCurrentUserIdFF();
        return l != null
                && (a.isCurrentSessionAdmin()
                || (l.getRespUser() != null && l.getRespUser().getId() == u));
    }

    public void onUpdateCollaborators() {
        TodoList li = getCurrentTodoList();
        if (isListManager()) {
            UPA.getPersistenceUnit().invokePrivileged(() -> UPA.getPersistenceUnit().merge(li));
            refreshList();
        }
    }

    @Override
    public void onSelectCurrent() {
        super.onSelectCurrent();
    }

    @Override
    public List<VRMenuInfo> createCustomMenus() {
        List<VRMenuInfo> ok = new ArrayList<>();
        for (TodoList list : todoService.findTodoListsByResp(null)) {
            if (isAllowedList(list, false)) {
                AppUser user = coreService.getCurrentUser();
                int count = user == null ? 0 : todoService.findTodosByResponsible(list.getId(),
                        user.getId(),
                        new TodoStatusType[]{
                            TodoStatusType.UNASSIGNED,
                            TodoStatusType.ASSIGNED,
                            TodoStatusType.TO_VERIFY
                        }
                ).size();
                final VRMenuInfo vrMenuDef = new VRMenuInfo(list.getLabel() == null ? list.getName() : list.getLabel(), "/Todo", "todo", list.getName(),
                        null, null, "", 100,
                        new VRMenuLabel[]{
                            new VRMenuLabel(String.valueOf(count), "severe")
                        }
                );
//            vrMenuDef.
                ok.add(vrMenuDef);
            }
        }
        return ok;
    }

    protected boolean isAllowedList(TodoList list, boolean promoteAdmin) {
        if (list == null) {
            return false;
        }
        CorePlugin core = CorePlugin.get();
        if (core.getCurrentUserId() == null) {
            return false;
        }
        if (!StringUtils.isBlank(list.getCollaborators())) {
            if (!core.isUserMatchesProfileFilter(core.getCurrentUserId(), list.getCollaborators())) {
                return false;
            }
        }
        if (list.getRespUser() != null) {
            if (promoteAdmin) {
                if (!core.isCurrentSessionAdminOrUser(core.getCurrentUserId())) {
                    return false;
                }
            } else {
                if (core.getCurrentUserId() != list.getRespUser().getId()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public VrPageInfo resolvePageInfo(String cmd) {
//        String listName = cmd;
        TodoList list = todoService.findTodoList(cmd);
        if (!isAllowedList(list, true)) {
            return null;
        }
        String title = list.getLabel();
//       String title = "?";
//        if (TodoList.LABO_ACTION.equals(listName)) {
//        } else if (TodoList.LABO_TICKET.equals(listName)) {
//            title = ("Mes Ticktes Labo");
//        } else {
//            title = (listName);
//        }
        if (StringUtils.isBlank(title)) {
            title = list.getName();
        }
        if (StringUtils.isBlank(title)) {
            title = "Todo#" + list.getId();
        }
        VrPageInfo d = new VrPageInfo();
        d.setUrl("modules/todo/todos");
        d.setCss("fa-table");
        d.setTitle(title);
        List<VrBreadcrumbItem> items = new ArrayList<>();
        items.add(new VrBreadcrumbItem("Todo", "Mes taches à faire", "fa-dashboard", "", ""));
        d.setBreadcrumb(items.toArray(new VrBreadcrumbItem[items.size()]));
        return d;
    }

    @Override
    public void loadContentTexts(String name) {
        if (true) {
            return;//TODO FIX ME
        }
        reloadPage(null, true);
    }

    @Override
    public List<ContentText> getContentTextList(String id) {
        return (List) getModel().getTodoText();
    }

    public List<ContentText> getContentTextListHead(String id, int max) {
        List<ContentText> list = getContentTextList(id);
        if (list.size() > max) {
            return list.subList(0, max);
        }
        return list;
    }

    @Override
    public int getActiveCount() {
        List<ContentText> contentTextList = getContentTextList(null);
        return contentTextList == null ? 0 : contentTextList.size();
    }
}
