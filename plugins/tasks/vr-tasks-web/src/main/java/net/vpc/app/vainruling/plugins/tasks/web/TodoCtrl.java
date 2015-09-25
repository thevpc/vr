/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.vpc.app.vainruling.plugins.tasks.service.model.Todo;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoCategory;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoList;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoPriority;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoStatus;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoStatusType;
import net.vpc.app.vainruling.api.web.BreadcrumbItem;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UCtrlData;
import net.vpc.app.vainruling.api.web.UCtrlProvider;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.api.web.VRMenuDef;
import net.vpc.app.vainruling.api.web.VRMenuDefFactory;
import net.vpc.app.vainruling.api.web.ctrl.AbstractObjectCtrl;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;
import net.vpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Todo", css = "fa-dashboard", ctrl = "")
        }, css = "fa-table", title = "Listes", url = "modules/todo/todos"
)
@ManagedBean
@Scope(value = "session")
public class TodoCtrl extends AbstractObjectCtrl<Todo> implements VRMenuDefFactory, UCtrlProvider {

    @Autowired
    private TaskPlugin todoService;
    @Autowired
    private CorePlugin coreService;

    public TodoCtrl() {
        super(new PModel());
    }

    public PModel getModel() {
        return (PModel) super.getModel();
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
        todo.setInitiator(coreService.getUserSession().getUser());
        todo.setResponsible(coreService.getUserSession().getUser());
        todo.setPriority(TodoPriority.DEFAULT);
        TodoList list = todoService.findTodoList(getModel().getListName());
        todo.setList(list);
        return todo;
    }

    public boolean isCurrentOwned() {
        AppUser r = getModel().getCurrent().getResponsible();
        AppUser me = coreService.getUserSession().getUser();
        return r != null && me != null && r.getId() == me.getId();
    }

    public boolean isEnabledButton(String buttonId) {
        if ("Assign".equals(buttonId)) {
            return getModel().getMode() == EditCtrlMode.UPDATE
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.ASSIGNED) != null;
        }
        if ("Unassign".equals(buttonId)) {
            return getModel().getMode() == EditCtrlMode.UPDATE
                    && isCurrentOwned()
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.UNASSIGNED) != null;
        }

        if ("Done".equals(buttonId)) {
            return getModel().getMode() == EditCtrlMode.UPDATE
                    && isCurrentOwned()
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.DONE) != null;
        }
        if ("Verify".equals(buttonId)) {
            return getModel().getMode() == EditCtrlMode.UPDATE
                    && isCurrentOwned()
                    && todoService.findNextStatus(getModel().getCurrent().getStatus(), TodoStatusType.TO_VERIFY) != null;
        }
        if ("Reopen".equals(buttonId)) {
            return getModel().getMode() == EditCtrlMode.UPDATE
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
            c.setResponsible(coreService.getUserSession().getUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onUnassignCurrent() {
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.UNASSIGNED);
        if (s != null) {
            c.setResponsible(coreService.getUserSession().getUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onDoneCurrent() {
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.DONE);
        if (s != null) {
            c.setResponsible(coreService.getUserSession().getUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onVerifyCurrent() {
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.TO_VERIFY);
        if (s != null) {
            c.setResponsible(coreService.getUserSession().getUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onReopenCurrent() {
        Todo c = getModel().getCurrent();
        TodoStatus s = todoService.findNextStatus(c.getStatus(), TodoStatusType.ASSIGNED);
        if (s != null) {
            c.setResponsible(coreService.getUserSession().getUser());
            c.setStatus(s);
            todoService.saveTodo(c);
        }
    }

    public void onSaveCurrent() {
        switch (getModel().getMode()) {
            case NEW: {
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
        reloadPage();
        getModel().setMode(EditCtrlMode.LIST);
    }

    public void onDeleteCurrent() {
        Todo c = getModel().getCurrent();
        todoService.removeTodo(c.getId());
        getModel().setMode(EditCtrlMode.LIST);
    }

    public void onArchiveCurrent() {
        Todo c = getModel().getCurrent();
        todoService.archiveTodo(c.getId());
        getModel().setCurrent(new Todo());
        getModel().setMode(EditCtrlMode.LIST);
    }

    @OnPageLoad
    @Override
    public void reloadPage(String cmd) {
        if (!StringUtils.isEmpty(cmd)) {
            getModel().setListName(cmd);
            getModel().setCmd(cmd);
        }
        TodoList list = todoService.findTodoList(getModel().getListName());
        int currentListId = list.getId();
        getModel().setTodo(todoService.findTodosByResponsible(currentListId, null, TodoStatusType.UNASSIGNED));
        getModel().setInProgress(todoService.findTodosByResponsible(currentListId, null, TodoStatusType.ASSIGNED));
        getModel().setToVerify(todoService.findTodosByResponsible(currentListId, null, TodoStatusType.TO_VERIFY));
        getModel().setDone(todoService.findTodosByResponsible(currentListId, null, TodoStatusType.DONE));

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

    public static class PModel extends Model<Todo> {

        private String listName = TodoList.LABO_ACTION;
        private String title = "Titre";
        private List<Todo> todo = new ArrayList<>();
        private List<Todo> inProgress = new ArrayList<>();
        private List<Todo> toVerify = new ArrayList<>();
        private List<Todo> done = new ArrayList<>();
//        private List<Stat> statuses = new ArrayList<>();
        private List<TodoStatus> statuses = new ArrayList<>();
        private List<TodoCategory> categories = new ArrayList<>();
        private List<SelectItem> statusItems = new ArrayList<>();
        private List<SelectItem> categoryItems = new ArrayList<>();
        private Integer currentCategoryId;
        private Integer currentStatusId;

        public PModel() {
            setCurrent(new Todo());
        }

        public List<TodoStatus> getStatuses() {
            return statuses;
        }

        public void setStatuses(List<TodoStatus> statuses) {
            this.statuses = statuses;
        }

        public List<TodoCategory> getCategories() {
            return categories;
        }

        public void setCategories(List<TodoCategory> categories) {
            this.categories = categories;
        }

        public Integer getCurrentCategoryId() {
            return currentCategoryId;
        }

        public void setCurrentCategoryId(Integer currentCategoryId) {
            this.currentCategoryId = currentCategoryId;
            if (getCurrent() != null) {
                if (currentCategoryId == null) {
                    getCurrent().setCategory(null);
                } else {
                    for (TodoCategory category : getCategories()) {
                        if (category.getId() == currentCategoryId) {
                            getCurrent().setCategory(category);
                            break;
                        }
                    }
                }
            }
        }

        public Integer getCurrentStatusId() {
            return currentStatusId;
        }

        public void setCurrentStatusId(Integer currentStatusId) {
            this.currentStatusId = currentStatusId;
            if (getCurrent() != null) {
                if (currentStatusId == null) {
                    getCurrent().setStatus(null);
                } else {
                    for (TodoStatus status : getStatuses()) {
                        if (status.getId() == currentStatusId) {
                            getCurrent().setStatus(status);
                            break;
                        }
                    }
                }
            }
        }

        public List<SelectItem> getStatusItems() {
            return statusItems;
        }

        public void setStatusItems(List<SelectItem> statusItems) {
            this.statusItems = statusItems;
        }

        public List<SelectItem> getCategoryItems() {
            return categoryItems;
        }

        public void setCategoryItems(List<SelectItem> categories) {
            this.categoryItems = categories;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getListName() {
            return listName;
        }

        public void setListName(String listName) {
            this.listName = listName;
        }

        @Override
        public void setCurrent(Todo current) {
            super.setCurrent(current);
            TodoCategory c = current.getCategory();
            this.currentCategoryId = c == null ? null : c.getId();
            TodoStatus s = current.getStatus();
            this.currentStatusId = s == null ? null : s.getId();
        }

        @Override
        public Todo getCurrent() {
            return super.getCurrent();
        }

        public List<Todo> getTodo() {
            return todo;
        }

        public void setTodo(List<Todo> todo) {
            this.todo = todo;
        }

        public List<Todo> getInProgress() {
            return inProgress;
        }

        public void setInProgress(List<Todo> inProgress) {
            this.inProgress = inProgress;
        }

        public List<Todo> getToVerify() {
            return toVerify;
        }

        public void setToVerify(List<Todo> toVerify) {
            this.toVerify = toVerify;
        }

        public List<Todo> getDone() {
            return done;
        }

        public void setDone(List<Todo> done) {
            this.done = done;
        }

    }

    @Override
    public List<VRMenuDef> createVRMenuDefList() {
        List<VRMenuDef> ok = new ArrayList<>();
        for (TodoList findTodoListsByResp : todoService.findTodoListsByResp(null)) {
            VrApp.getBean(CorePlugin.class).createRight("Custom.Todo." + findTodoListsByResp.getName(), "TODO " + findTodoListsByResp.getName());
            final VRMenuDef vrMenuDef = new VRMenuDef(findTodoListsByResp.getName(), "/Todo", "todo", findTodoListsByResp.getName(),
                    "Custom.Todo." + findTodoListsByResp.getName(),""
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
        TodoList list = todoService.findTodoList(listName);
        if (list != null) {

            if (TodoList.LABO_ACTION.equals(list.getName())) {
                title = ("Mes Actions Labo");
            } else if (TodoList.LABO_TICKET.equals(list.getName())) {
                title = ("Mes Ticktes Labo");
            } else {
                title = (list.getName());
            }
        }

        UCtrlData d = new UCtrlData();
        d.setUrl("modules/todo/todos");
        d.setCss("fa-table");
        d.setTitle(title);
        List<BreadcrumbItem> items = new ArrayList<>();
        items.add(new BreadcrumbItem("Todo", "fa-dashboard", "", ""));
        d.setBreadcrumb(items.toArray(new BreadcrumbItem[items.size()]));
        return d;
    }

}
