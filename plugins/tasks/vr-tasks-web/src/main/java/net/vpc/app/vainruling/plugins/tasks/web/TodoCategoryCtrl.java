/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import java.util.List;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoCategory;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoList;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.ctrl.AbstractNameCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Parametrage", css = "fa-dashboard", ctrl = ""),
        }
        ,css = "fa-table"
        ,title = "Categoreies Todo"
        ,url = "modules/todo/config-todo-category"
)
@ManagedBean
@Scope(value = "session")
public class TodoCategoryCtrl extends AbstractNameCtrl<TodoCategory> {

    @Autowired
    private TaskPlugin todoService;

    public TodoCategoryCtrl() {
        super(new PModel());
    }

    @Override
    public PModel getModel() {
        return (PModel) super.getModel();
    }

    @Override
    public TodoCategory delegated_newInstance() {
        return new TodoCategory();
    }

    @Override
    public void delegated_newCurrent() {
        TodoCategory c = getModel().getCurrent();
        c.setName("Nouveau");
    }

    @Override
    public void delegated_deleteCurrent() {
        TodoCategory c = getModel().getCurrent();
        todoService.removeTodo(c.getId());
    }

    @Override
    public void delegated_saveCurrent() {
        TodoCategory c = getModel().getCurrent();
        if (c.getList() == null) {
            TodoList list = todoService.findTodoList(getModel().getListId());
            c.setList(list);
        }
        todoService.saveTodoCategory(c);
    }

    @Override
    public List<TodoCategory> delegated_findAll() {
        return todoService.findTodoCategories(getModel().getListId());
    }

    public static class PModel extends Model<TodoCategory> {

        private int listId;

        public int getListId() {
            return listId;
        }

        public void setListId(int listId) {
            this.listId = listId;
        }

    }
}
