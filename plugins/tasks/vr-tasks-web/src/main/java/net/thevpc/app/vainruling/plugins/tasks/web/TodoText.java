package net.thevpc.app.vainruling.plugins.tasks.web;

import net.thevpc.app.vainruling.core.service.model.strict.AppUserStrict;
import net.thevpc.app.vainruling.plugins.tasks.service.model.Todo;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.model.content.DefaultVrContentText;

/**
 * Created by vpc on 9/11/16.
 */
public class TodoText extends DefaultVrContentText {

    private Todo todo;

    public TodoText(Todo todo) {
        this.todo = todo;
        setTitle(todo.getName());
        setContent(todo.getMessage());
        AppUser user = todo.getResponsible();
        AppUserStrict u = new AppUserStrict(user);
        u.setIconPath(CorePlugin.get().getUserIcon(user == null ? -1 : user.getId()));
        setUser(u);
        setRecipients(todo.getResponsible() == null ? null : todo.getResponsible().getFullName());
    }

    @Override
    public int getId() {
        return todo.getId();
    }

    public int getProgress() {
        return todo.getProgress();
    }
}
