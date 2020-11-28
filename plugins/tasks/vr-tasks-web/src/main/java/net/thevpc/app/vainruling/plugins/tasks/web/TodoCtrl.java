/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.tasks.web;

import javax.servlet.http.*;
import java.io.IOException;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.AbstractObjectCtrl;
import net.thevpc.app.vainruling.VrPageInfo;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.content.ContentText;
import net.thevpc.app.vainruling.core.service.model.AppUser;

import net.thevpc.app.vainruling.VrBreadcrumbItem;
import net.thevpc.app.vainruling.VrMenuInfo;
import net.thevpc.app.vainruling.VrMenuLabel;
import net.thevpc.app.vainruling.core.web.jsf.VrJsf;
import net.thevpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.thevpc.app.vainruling.plugins.tasks.service.model.*;
import net.thevpc.app.vainruling.plugins.tasks.service.model.*;
import net.thevpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.faces.context.FacesContext;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.app.vainruling.VrPageInfoResolver;
import net.thevpc.app.vainruling.VrPage;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.upa.UPA;
import org.primefaces.event.SlideEndEvent;
import net.thevpc.app.vainruling.VrTaskTextService;
import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrMenuProvider;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Todo", css = "fa-dashboard", ctrl = "")
        }, url = "modules/todo/todos"
)
@Scope("session")
public class TodoCtrl extends AbstractObjectCtrl<Todo> implements VrMenuProvider, VrPageInfoResolver, VrTaskTextService {

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
        return todoService.findTodoListsByResp(null, false);
    }

    @Override
    public VrBreadcrumbItem getTitle() {
        VrBreadcrumbItem b = super.getTitle();
        TodoList list = getCurrentTodoList();
        String title = list.getLabel() == null ? list.getName() : list.getLabel();
        b.setTitle(title);
        return b;
    }

    public void onEstimationSlideEnd(SlideEndEvent event) {
        getModel().getCurrent().setEstimation((int) event.getValue());
    }

    public void onPrioritySlideEnd(SlideEndEvent event) {
        getModel().setCurrentPriority((int) event.getValue());
    }

    public void onProgressSlideEnd(SlideEndEvent event) {
        getModel().getCurrent().setProgress((int) event.getValue());
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
        return getModel().getTodoList();
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
            return getModel().getMode() == VrAccessMode.UPDATE
                    && getModel().getCurrent().getStatus().getType() == TodoStatusType.DONE;
        }
        if ("Assign".equals(buttonId)) {
            return getModel().getMode() == VrAccessMode.UPDATE
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.ASSIGNED) != null;
        }
        if ("Unassign".equals(buttonId)) {
            return getModel().getMode() == VrAccessMode.UPDATE
                    && isCurrentOwned()
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.UNASSIGNED) != null;
        }

        if ("Done".equals(buttonId)) {
            return getModel().getMode() == VrAccessMode.UPDATE
                    && isCurrentOwned()
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.DONE) != null;
        }
        if ("Verify".equals(buttonId)) {
            return getModel().getMode() == VrAccessMode.UPDATE
                    && isCurrentOwned()
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.TO_VERIFY) != null;
        }
        if ("Reopen".equals(buttonId)) {
            return getModel().getMode() == VrAccessMode.UPDATE
                    && isCurrentOwned()
                    && (getModel().getCurrent().getStatus().getType() == TodoStatusType.TO_VERIFY || getModel().getCurrent().getStatus().getType() == TodoStatusType.DONE)
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.ASSIGNED) != null;
        }
        if ("New".equals(buttonId)) {
            return getModel().getMode() == VrAccessMode.READ;
        }
        CorePlugin core = CorePlugin.get();
        boolean admin = core.isCurrentSessionAdmin();
        int currentUserId = core.getCurrentUserIdFF();
        if ("Persist".equals(buttonId)) {
            return getModel().getMode() == VrAccessMode.READ;
        }
        if ("Save".equals(buttonId)) {
            if (getModel().getMode() == VrAccessMode.READ) {
                return false;
            }
            Todo td = getModel().getCurrent();
            if (td == null) {
                return false;
            }
            if (admin) {
                return true;
            }
            if ((td.getList().getRespUser() != null && td.getList().getRespUser().getId() == currentUserId)
                    || (td.getInitiator() != null && td.getInitiator().getId() == currentUserId)
                    || (td.getResponsible() != null && td.getResponsible().getId() == currentUserId)) {
                return true;
            }
            return false;
        }
        if ("Remove".equals(buttonId)
                || "archive".equals(buttonId)) {
            if (getModel().getMode() == VrAccessMode.PERSIST) {
                return false;
            }
            if (getModel().getMode() == VrAccessMode.UPDATE) {
                Todo td = getModel().getCurrent();
                if (td == null) {
                    return false;
                }
                if (admin) {
                    return true;
                }
                if ((td.getList().getRespUser() != null && td.getList().getRespUser().getId() == currentUserId)
                        || (td.getInitiator() != null && td.getInitiator().getId() == currentUserId) //                    ||(td.getResponsible()!= null && td.getResponsible().getId() == currentUserId)
                        ) {
                    return true;
                }
            }
            if (getModel().getMode() == VrAccessMode.READ) {
                TodoList td = getModel().getTodoList();
                if (td == null) {
                    return false;
                }
                if (admin) {
                    return true;
                }
                if (!td.isSystemList() && (td.getRespUser() != null && td.getRespUser().getId() == currentUserId)) {
                    return true;
                }
            }
            return false;
        }
        return super.isEnabledButton(buttonId);
    }

    public String getUserTextIconHtml(AppUser user) {
        if (user == null) {
            return "";
        }
        String login = user.getLogin();
        if (StringUtils.isBlank(login)) {
            return "";
        }
        String label = Vr.get().hashToArr(login, "label-danger", "label-success", "label-info", "label-warning", "label-primary", "label-default");
        String[] y = StringUtils.split(login, ". ");
        String t = "?";
        if (y.length >= 2) {
            t = String.valueOf(Character.toUpperCase(y[0].charAt(0))) + String.valueOf(Character.toUpperCase(y[1].charAt(0)));
        } else if (y.length >= 1) {
            t = String.valueOf(Character.toUpperCase(y[0].charAt(0)));
        }
        return "<span class=\"label " + label + "\" title=\"" + user.getFullTitle() + "\">" + t + "</span>";
    }

    public String getCategoryIconHtml(TodoCategory cat) {
        if (cat == null) {
            return "";
        }
        String name = cat.getName();
        if (StringUtils.isBlank(name)) {
            name = cat.getShortName();
        }
        if (StringUtils.isBlank(name)) {
            name = String.valueOf(cat.getId());
        }
        String label = Vr.get().hashToArr(name, "red", "yellow", "aqua", "blue", "green", "black", "black", "light-blue", "gray", "navy", "teal", "olive", "lime", "orange", "fuchsia", "purple", "maroon");
        String[] y = StringUtils.split(name, ". ");
        //<i class="fa fa-circle-o text-red"></i>
//        return "<i class=\"fa fa-circle-o text-" + label + "\">"+StringEscapeUtils.escapeHtml(name) +"</i>";
        return "<i class=\"text-" + label + "\">" + StringEscapeUtils.escapeHtml(name) + "</i>";
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
        getModel().setMode(VrAccessMode.READ);
    }

    public void onDeleteCurrent() {
        if (getModel().getMode() == VrAccessMode.READ) {
            TodoList td = getModel().getTodoList();
            todoService.removeTodoList(td.getId());
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest origRequest = (HttpServletRequest) context.getExternalContext().getRequest();
            String contextPath = origRequest.getContextPath();
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect(contextPath + "/p/welcome");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (getModel().getMode() == VrAccessMode.UPDATE) {
            updateCurrentCategory();
            Todo c = getModel().getCurrent();
            todoService.removeTodo(c.getId());
            reloadPage(true);
            getModel().setMode(VrAccessMode.READ);
        }
    }

    public void onArchiveCurrent() {
        updateCurrentCategory();
        Todo c = getModel().getCurrent();
        todoService.archiveTodo(c.getId());
        getModel().setCurrent(new Todo());
        reloadPage(true);
        getModel().setMode(VrAccessMode.READ);
    }

    @VrOnPageLoad
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
            getModel().setTodoList(todoService.findTodoList(cmd));
            getModel().setCmd(cmd);
            if (!todoService.isTodoCollaborator(getModel().getTodoList(), null)) {
                throw new IllegalArgumentException("Not Allowed List " + cmd);
            }
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
        if (a.isCurrentSessionAdmin()) {
            return true;
        }
        return l.isSharableList();
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
    public List<VrMenuInfo> createCustomMenus() {
        List<VrMenuInfo> ok = new ArrayList<>();
        for (TodoList list : todoService.findTodoListsByResp(null, false)) {
            AppUser user = coreService.getCurrentUser();
            int count = user == null ? 0 : todoService.findTodosByResponsible(list.getId(),
                    user.getId(),
                    new TodoStatusType[]{
                        TodoStatusType.UNASSIGNED,
                        TodoStatusType.ASSIGNED,
                        TodoStatusType.TO_VERIFY
                    }
            ).size();
            final VrMenuInfo vrMenuDef = new VrMenuInfo(list.getLabel() == null ? list.getName() : list.getLabel(), "/Todo", "todo", list.getName(),
                    null, null, "", 100,
                    count == 0 ? null : new VrMenuLabel[]{
                        new VrMenuLabel(String.valueOf(count), count < 3 ? "warning" : "severe")
                    }
            );
            ok.add(vrMenuDef);
        }
        return ok;
    }

    @Override
    public VrPageInfo resolvePageInfo(String cmd) {
//        String listName = cmd;
        TodoList list = todoService.findTodoList(cmd);
        if (!todoService.isAllowedList(list, true)) {
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
