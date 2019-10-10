/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service.extensions;

import java.util.List;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.extensions.VrImpersonateListener;
import net.vpc.app.vainruling.core.service.extensions.VrLoginListener;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSessionInfo;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.plugins.tasks.service.TaskPlugin;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoList;
import net.vpc.upa.UPA;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class MyTasksVrLoginListener implements VrLoginListener, VrImpersonateListener {

    @Override
    public void onAuthenticate(UserSessionInfo a) {
        prepare(a.getUserId());
    }

    @Override
    public void onImpersonate(UserToken a) {
        prepare(a.getUserId());
    }

    private void prepare(int uid) {
        CorePlugin core = CorePlugin.get();
        AppUser u = core.findUser(uid);
        if (u != null) {
            if (core.isUserMatchesProfileFilter(uid, "TodoOwner")) {
                String name = u.getLogin() + "/my-todos";
                TaskPlugin t = VrApp.getBean(TaskPlugin.class);
                List<TodoList> all = t.findTodoListsByResp(uid);
                for (TodoList todoList : all) {
                    if (todoList.getName().equals(name)) {
                        return;
                    }
                }
                TodoList td = new TodoList();
                td.setName(name);
                td.setLabel("Mes Todos (" + u.getLogin() + ")");
                td.setRespUser(u);
                td.setStatusGroup(t.findDefaultStatusGroup());
                td.setSystemList(true);
                UPA.getPersistenceUnit().invokePrivileged(()->UPA.getPersistenceUnit().persist(td));
            }
        }
    }

}
