/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoList;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.api.web.ctrl.AbstractObjectCtrl;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(ctrl = "dashboard")}, css = "fa-table", title = "Listes", url = "modules/todo/config-todo-list"
)
@ManagedBean
@Scope(value = "session")
public class TodoListCtrl extends AbstractObjectCtrl<TodoList> {

    @Autowired
    private TaskPlugin todoService;
    @Autowired
    private CorePlugin coreService;
    @Autowired
    private TodoCategoryCtrl todoCategoryCtrl;
    @Autowired
    private TodoStatusCtrl todoStatusCtrl;

    public TodoListCtrl() {
        super(new PModel());
    }

    public PModel getModel() {
        return (PModel) super.getModel();
    }

    @Override
    protected TodoList delegated_newInstance() {
        final TodoList todo = new TodoList();
        return todo;
    }

    @Override
    public boolean isEnabledButton(String buttonId) {
        if ("Archive".equals(buttonId)) {
            return false;
        }
        return super.isEnabledButton(buttonId);
    }

    public void onSaveCurrent() {
        TodoList c = getModel().getCurrent();
        todoService.saveTodoList(c);
        onCancelCurrent();
        getModel().setMode(EditCtrlMode.LIST);
        reloadPage(true);
    }

    public void onDeleteCurrent() {
        TodoList c = getModel().getCurrent();
        todoService.removeTodoList(c.getId());
        getModel().setMode(EditCtrlMode.LIST);
        reloadPage(true);
    }

//    public void onArchiveCurrent() {
//        Todo c = getModel().getCurrent();
//        todoService.archiveTodo(c.getId());
//        getModel().setCurrent(new Todo());
//        getModel().setMode(EditCtrlMode.LIST);
//    }
    public void onShowCategoriesList() {
        int currentListId = getModel().getCurrent().getId();
        getTodoCategoryCtrl().getModel().setListId(currentListId);
        getTodoCategoryCtrl().reloadPage();
    }

    public void onShowStatusesList() {
        int currentListId = getModel().getCurrent().getId();
        getTodoStatusCtrl().getModel().setListId(currentListId);
        getTodoStatusCtrl().reloadPage();
    }

    @OnPageLoad
    @Override
    public void reloadPage(String cmd, boolean enableCustomization) {
        getModel().setCmd(cmd);
        getModel().setList(todoService.findTodoListsByResp(null));
        getModel().setCurrent(delegated_newInstance());
        onShowCategoriesList();
        onShowStatusesList();
    }

    public TodoCategoryCtrl getTodoCategoryCtrl() {
        return todoCategoryCtrl;
    }

    public void setTodoCategoryCtrl(TodoCategoryCtrl todoCategoryCtrl) {
        this.todoCategoryCtrl = todoCategoryCtrl;
    }

    public TodoStatusCtrl getTodoStatusCtrl() {
        return todoStatusCtrl;
    }

    public void setTodoStatusCtrl(TodoStatusCtrl todoStatusCtrl) {
        this.todoStatusCtrl = todoStatusCtrl;
    }

    public static class PModel extends Model<TodoList> {

        public PModel() {
            setCurrent(new TodoList());
        }

        public TodoList getCurrent() {
            return super.getCurrent();
        }

        @Override
        public List<TodoList> getList() {
            return super.getList();
        }
    }
}
