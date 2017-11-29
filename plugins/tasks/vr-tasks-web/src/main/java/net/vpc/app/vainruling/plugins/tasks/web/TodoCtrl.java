/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.content.TaskTextService;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.*;
import net.vpc.app.vainruling.core.web.ctrl.AbstractObjectCtrl;

import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.menu.VRMenuDef;
import net.vpc.app.vainruling.core.web.menu.VRMenuDefFactory;
import net.vpc.app.vainruling.core.web.menu.VRMenuLabel;
import net.vpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.vpc.app.vainruling.plugins.tasks.service.model.*;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.AccessMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Todo", css = "fa-dashboard", ctrl = "")
        }, url = "modules/todo/todos"
)
@Scope("singleton")
public class TodoCtrl extends AbstractObjectCtrl<Todo> implements VRMenuDefFactory, UCtrlProvider, TaskTextService{

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
    public BreadcrumbItem getTitle() {
        BreadcrumbItem b = super.getTitle();
        TodoList list = todoService.findTodoList(getModel().getListName());
        if (TodoList.LABO_ACTION.equals(list.getName())) {
            b.setTitle("Mes Actions Labo");
        } else if (TodoList.LABO_TICKET.equals(list.getName())) {
            b.setTitle("Mes Ticktes Labo");
        } else {
            b.setTitle(list.getName());
        }
        return b;
    }

    @Override
    protected Todo delegated_newInstance() {
        final Todo todo = new Todo();
        todo.setArchived(false);
        todo.setInitiator(coreService.getCurrentUser());
        todo.setResponsible(coreService.getCurrentUser());
        todo.setPriority(TodoPriority.DEFAULT);
        TodoList list = todoService.findTodoList(getModel().getListName());
        todo.setList(list);
        return todo;
    }

    public boolean isCurrentOwned() {
        AppUser r = getModel().getCurrent().getResponsible();
        AppUser me = coreService.getCurrentUser();
        return r != null && me != null && r.getId() == me.getId();
    }

    public boolean isEnabledButton(String buttonId) {
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
        return super.isEnabledButton(buttonId);
    }

    public void onAssignCurrent() {
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.ASSIGNED);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onUnassignCurrent() {
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.UNASSIGNED);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onDoneCurrent() {
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.DONE);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onVerifyCurrent() {
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.TO_VERIFY);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onReopenCurrent() {
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.ASSIGNED);
        if (s != null) {
            c.setResponsible(coreService.getCurrentUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onSaveCurrent() {
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
        Todo c = getModel().getCurrent();
        todoService.removeTodo(c.getId());
        getModel().setMode(AccessMode.READ);
    }

    public void onArchiveCurrent() {
        Todo c = getModel().getCurrent();
        todoService.archiveTodo(c.getId());
        getModel().setCurrent(new Todo());
        getModel().setMode(AccessMode.READ);
    }

    @OnPageLoad
    @Override
    public void reloadPage(String cmd, boolean ustomization) {
        if (!StringUtils.isEmpty(cmd)) {
            getModel().setListName(cmd);
            getModel().setCmd(cmd);
        }
        TodoList list = todoService.findTodoList(getModel().getListName());
        int currentListId = list.getId();
        getModel().setTodo(todoService.findTodosByResponsible(currentListId, null, new TodoStatusType[]{TodoStatusType.UNASSIGNED}));
        getModel().setInProgress(todoService.findTodosByResponsible(currentListId, null, new TodoStatusType[]{TodoStatusType.ASSIGNED}));
        getModel().setToVerify(todoService.findTodosByResponsible(currentListId, null, new TodoStatusType[]{TodoStatusType.TO_VERIFY}));
        getModel().setDone(todoService.findTodosByResponsible(currentListId, null, new TodoStatusType[]{TodoStatusType.DONE}));

        getModel().setStatuses(todoService.findTodoStatuses(currentListId));
        ArrayList<SelectItem> st = new ArrayList<SelectItem>();
//        st.add(new SelectItem(null, "-"));
        for (TodoStatus s : getModel().getStatuses()) {
            st.add(new SelectItem(s.getId(), s.getName()));
        }
        getModel().setStatusItems(st);

        getModel().setCategories(todoService.findTodoCategories(currentListId));
        st = new ArrayList<SelectItem>();
//        st.add(new SelectItem(null, "-"));
        for (TodoCategory s : getModel().getCategories()) {
            st.add(new SelectItem(s.getId(), s.getName()));
        }
        getModel().setCategoryItems(st);
    }

    @Override
    public List<VRMenuDef> createVRMenuDefList() {
        List<VRMenuDef> ok = new ArrayList<>();
        for (TodoList findTodoListsByResp : todoService.findTodoListsByResp(null)) {
            AppUser user = coreService.getCurrentUser();
            int count=user==null?0:todoService.findTodosByResponsible(findTodoListsByResp.getId(),
                    user.getId(),
                    new TodoStatusType[]{
                        TodoStatusType.ASSIGNED,
                        TodoStatusType.TO_VERIFY,
                    }
                    ).size();
            final VRMenuDef vrMenuDef = new VRMenuDef(findTodoListsByResp.getName(), "/Todo", "todo", findTodoListsByResp.getName(),
                    "Custom.Todo." + findTodoListsByResp.getName(), null,"",100,
                    new VRMenuLabel[]{
                        new VRMenuLabel(String.valueOf(count),"severe")
                    }
            );
//            vrMenuDef.
            ok.add(vrMenuDef);
        }
        return ok;
    }

    @Override
    public UCtrlData getUCtrl(String cmd) {
        String listName = cmd;
        String title = "?";
        if (TodoList.LABO_ACTION.equals(listName)) {
            title = ("Mes Actions Labo");
        } else if (TodoList.LABO_TICKET.equals(listName)) {
            title = ("Mes Ticktes Labo");
        } else {
            title = (listName);
        }

        UCtrlData d = new UCtrlData();
        d.setUrl("modules/todo/todos");
        d.setCss("fa-table");
        d.setTitle(title);
        d.setSecurityKey("Custom.Todo." + listName);
        List<BreadcrumbItem> items = new ArrayList<>();
        items.add(new BreadcrumbItem("Todo","Mes taches à faire","fa-dashboard", "", ""));
        d.setBreadcrumb(items.toArray(new BreadcrumbItem[items.size()]));
        return d;
    }

    @Override
    public void loadContentTexts(String name) {
        if(true) return;//TODO FIX ME
        reloadPage(null,true);
    }

    @Override
    public List<ContentText> getContentTextList(String id) {
        return (List)getModel().getTodoText();
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
        return contentTextList==null?0:contentTextList.size();
    }
}
