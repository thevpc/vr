/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.core.web.jsf.ctrl.AbstractObjectCtrl;

import net.vpc.app.vainruling.core.web.jsf.VrJsf;
import net.vpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.vpc.app.vainruling.plugins.tasks.service.model.*;
import net.vpc.upa.AccessMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Parametrage", css = "fa-dashboard", ctrl = "")}, url = "modules/todo/todo-requests"
)
@Scope(value = "session")
public class TodoRequestsCtrl extends AbstractObjectCtrl<Todo> {

    @Autowired
    private TaskPlugin todoService;
    @Autowired
    private CorePlugin coreService;

    public TodoRequestsCtrl() {
        super(new PModel());
    }

    public PModel getModel() {
        return (PModel) super.getModel();
    }

    @Override
    protected Todo delegated_newInstance() {
        final Todo todo = new Todo();
        todo.setArchived(false);
        todo.setInitiator(coreService.getCurrentUser());
        todo.setResponsible(coreService.getCurrentUser());
        todo.setPriority(TodoPriority.DEFAULT);
        TodoList list = getModel().getTodoList();
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
        reloadPage();
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
        getModel().setTodoLists(todoService.findTodoListsByInitiator(null));
        getModel().setTodoListItems(VrJsf.toSelectItemList(getModel().getTodoLists()));
        if (getModel().getTodoLists().size() > 0) {
            getModel().setTodoList(getModel().getTodoLists().get(0));
        }
        getModel().setAllTodos(todoService.findTodosByInitiator(null, null, null));
        todoListChanged();
    }

    private void todoListChanged() {
//        TodoList list = todoService.findTodoList(getModel().getListName());
//        int currentListId = list.getId();

        getModel().setStatuses(todoService.findTodoStatuses(getModel().getTodoList().getId()));
        getModel().setStatusItems(VrJsf.toSelectItemList(getModel().getStatuses()));
        getModel().setCategories(todoService.findTodoCategories(getModel().getTodoList().getId()));
        getModel().setCategoryItems(VrJsf.toSelectItemList(getModel().getCategories()));
    }

    public void currentTodoListChanged() {
        for (TodoList todoList : getModel().getTodoLists()) {
            if (todoList.getId() == getModel().getCurrentTodoListId()) {
                getModel().setTodoList(todoList);
                todoListChanged();
                break;
            }
        }
    }

    public static class PModel extends Model<Todo> {

        private TodoList todoList;
        private String title = "Titre";
        private List<Todo> allTodos = new ArrayList<>();
        //        private List<Stat> statuses = new ArrayList<>();
        private List<TodoStatus> statuses = new ArrayList<>();
        private List<TodoList> todoLists = new ArrayList<>();
        private List<TodoCategory> categories = new ArrayList<>();
        private List<SelectItem> statusItems = new ArrayList<>();
        private List<SelectItem> categoryItems = new ArrayList<>();
        private List<SelectItem> todoListtIems = new ArrayList<>();
        private Integer currentCategoryId;
        private Integer currentStatusId;
        private Integer currentTodoListId;

        public PModel() {
            setCurrent(new Todo());
        }

        public Integer getCurrentTodoListId() {
            return currentTodoListId;
        }

        public void setCurrentTodoListId(Integer currentTodoListId) {
            this.currentTodoListId = currentTodoListId;
            if (currentTodoListId == null) {
                this.todoList = null;
            } else {
                for (TodoList li : getTodoLists()) {
                    if (li.getId() == currentTodoListId) {
                        todoList = li;
                        break;
                    }
                }
            }
        }

        public TodoList getTodoList() {
            return todoList;
        }

        public void setTodoList(TodoList list) {
            this.todoList = list;
        }

        public List<TodoList> getTodoLists() {
            return todoLists;
        }

        public void setTodoLists(List<TodoList> todoLists) {
            this.todoLists = todoLists;
        }

        public List<SelectItem> getTodoListItems() {
            return todoListtIems;
        }

        public void setTodoListItems(List<SelectItem> listItems) {
            this.todoListtIems = listItems;
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

        @Override
        public Todo getCurrent() {
            return super.getCurrent();
        }

        @Override
        public void setCurrent(Todo current) {
            super.setCurrent(current);
            TodoCategory c = current.getCategory();
            this.currentCategoryId = c == null ? null : c.getId();
            TodoStatus s = current.getStatus();
            this.currentStatusId = s == null ? null : s.getId();
        }

        public List<Todo> getAllTodos() {
            return allTodos;
        }

        public void setAllTodos(List<Todo> allTodos) {
            this.allTodos = allTodos;
        }

    }
}
