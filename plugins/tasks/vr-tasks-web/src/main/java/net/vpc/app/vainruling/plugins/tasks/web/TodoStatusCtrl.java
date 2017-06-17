/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.ctrl.AbstractNameCtrl;
import net.vpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoList;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Parametrage", css = "fa-dashboard", ctrl = ""),
        }
        , url = "modules/todo/config-todo-status"
)
public class TodoStatusCtrl extends AbstractNameCtrl<TodoStatus> {

    @Autowired
    private TaskPlugin todoService;

    public TodoStatusCtrl() {
        super(new PModel());
    }

    @Override
    public PModel getModel() {
        return (PModel) super.getModel();
    }

    @Override
    public TodoStatus delegated_newInstance() {
        return new TodoStatus();
    }

    @Override
    public void delegated_newCurrent() {
        TodoStatus c = getModel().getCurrent();
        c.setName("Nouveau");
    }

    @Override
    public void delegated_deleteCurrent() {
        TodoStatus c = getModel().getCurrent();
        todoService.removeTodo(c.getId());
    }

    @Override
    public void delegated_saveCurrent() {
        TodoStatus c = getModel().getCurrent();
        if (c.getList() == null) {
            TodoList list = todoService.findTodoList(getModel().getListId());
            c.setList(list);
        }
        todoService.saveTodoStatus(c);
    }

    @Override
    public List<TodoStatus> delegated_findAll() {
        return todoService.findTodoStatuses(getModel().getListId());
    }

    public static class PModel extends Model<TodoStatus> {

        private int listId;

        public int getListId() {
            return listId;
        }

        public void setListId(int listId) {
            this.listId = listId;
        }

    }
}
