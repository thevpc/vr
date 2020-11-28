/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.tasks.web;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.AbstractObjectCtrl;

import net.thevpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.thevpc.app.vainruling.plugins.tasks.service.model.TodoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Listes Todo", css = "fa-dashboard", ctrl = "")
        },url = "modules/todo/config-todo-list"
)
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
        getModel().setMode(VrAccessMode.READ);
        reloadPage(true);
    }

    public void onDeleteCurrent() {
        TodoList c = getModel().getCurrent();
        todoService.removeTodoList(c.getId());
        getModel().setMode(VrAccessMode.READ);
        reloadPage(true);
    }

    //    public void onArchiveCurrent() {
//        Todo c = getModel().getCurrent();
//        todoService.archiveTodo(c.getId());
//        getModel().setCurrent(new Todo());
//        getModel().setMode(AccessMode.READ);
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

    @VrOnPageLoad
    @Override
    public void reloadPage(String cmd, boolean enableCustomization) {
        getModel().setCmd(cmd);
        getModel().setList(todoService.findTodoListsByResp(null,false));
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
