/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import net.vpc.app.vainruling.core.web.jsf.ctrl.AbstractObjectCtrl;
import net.vpc.app.vainruling.plugins.tasks.service.model.Todo;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoCategory;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoList;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoPriority;
import net.vpc.upa.UPA;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
@Scope(value = "session")
public class TodoModel extends AbstractObjectCtrl.Model<Todo> {

    private String listName;
    private TodoList todoList;
    private String title = "Titre";
    private List<Todo> todo = new ArrayList<>();
    private List<TodoText> todoText = new ArrayList<>();
    private List<Todo> inProgress = new ArrayList<>();
    private List<Todo> toVerify = new ArrayList<>();
    private List<Todo> done = new ArrayList<>();
    //        private List<Stat> statuses = new ArrayList<>();
    private List<TodoStatus> statuses = new ArrayList<>();
    private List<TodoCategory> categories = new ArrayList<>();
    private List<SelectItem> statusItems = new ArrayList<>();
    private List<SelectItem> categoryItems = new ArrayList<>();
    private List<SelectItem> responsibleItems = new ArrayList<>();
    private Integer currentCategoryId;
    private String currentCategoryName;
    private Integer currentResponsibleId;
    private Integer currentStatusId;

    public TodoModel() {
        setCurrent(new Todo());
    }

    public String getCurrentCategoryName() {
        return currentCategoryName;
    }

    public void setCurrentCategoryName(String currentCategoryName) {
        this.currentCategoryName = currentCategoryName;
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
    public Todo getCurrent() {
        Todo c = super.getCurrent();
        if (c == null) {
            return null;
        }
        if (currentResponsibleId != null) {
            AppUser u = UPA.getPersistenceUnit().invokePrivileged(() -> CorePlugin.get().findUser(currentResponsibleId));
            c.setResponsible(u);
        }
        return c;
    }

    public int getCurrentPriority() {
        Todo c = getCurrent();
        if (c == null || c.getPriority() == null) {
            return 0;
        }
        return c.getPriority().ordinal();
    }

    public void setCurrentPriority(int priority) {
        if (priority < 0) {
            priority = 0;
        }
        if (priority >= TodoPriority.values().length) {
            priority = TodoPriority.values().length - 1;
        }
        getCurrent().setPriority(TodoPriority.values()[priority]);
    }

    @Override
    public void setCurrent(Todo current) {
        super.setCurrent(current);
        TodoCategory c = current.getCategory();
        this.currentCategoryId = c == null ? null : c.getId();
        this.currentCategoryName = c == null ? null : c.getName();
        TodoStatus s = current.getStatus();
        this.currentStatusId = s == null ? null : s.getId();
        this.currentResponsibleId = current.getResponsible() == null ? null : current.getResponsible().getId();
    }

    public List<Todo> getTodo() {
        return todo;
    }

    public void setTodo(List<Todo> todo) {
        this.todo = todo;
        todoText = new ArrayList<>();
        if (todo != null) {
            for (Todo todo1 : todo) {
                todoText.add(new TodoText(todo1));
            }
        }
    }

    public List<TodoText> getTodoText() {
        return todoText;
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

    public List<SelectItem> getResponsibleItems() {
        return responsibleItems;
    }

    public void setResponsibleItems(List<SelectItem> responsibleItems) {
        this.responsibleItems = responsibleItems;
    }

    public Integer getCurrentResponsibleId() {
        return currentResponsibleId;
    }

    public void setCurrentResponsibleId(Integer currentResponsibleId) {
        this.currentResponsibleId = currentResponsibleId;
    }

    public TodoList getTodoList() {
        return todoList;
    }

    public void setTodoList(TodoList list) {
        this.todoList = list;
    }

}
