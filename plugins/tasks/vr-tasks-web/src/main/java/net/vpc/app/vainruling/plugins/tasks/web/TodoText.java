package net.vpc.app.vainruling.plugins.tasks.web;

import net.vpc.app.vainruling.core.service.content.ContentPath;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.strict.AppUserStrict;
import net.vpc.app.vainruling.plugins.tasks.service.model.Todo;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by vpc on 9/11/16.
 */
public class TodoText implements ContentText{
    private Todo todo;

    public TodoText(Todo todo) {
        this.todo = todo;
    }

    @Override
    public int getId() {
        return todo.getId();
    }

    public int getProgress() {
        return todo.getProgress();
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getDecoration() {
        return null;
    }

    @Override
    public String getSubject() {
        return todo.getName();
    }

    @Override
    public String getSubTitle() {
        return null;
    }

    @Override
    public String getContent() {
        return todo.getMessage();
    }

    @Override
    public String getImageURL() {
        return null;
    }

    @Override
    public AppUserStrict getUser() {
        return new AppUserStrict(todo.getResponsible());
    }

    @Override
    public List<ContentPath> getAttachments() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ContentPath> getImageAttachments() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ContentPath> getNonImageAttachments() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getLinkClassStyle() {
        return null;
    }

    @Override
    public boolean isImportant() {
        return false;
    }

    @Override
    public boolean isNoSubject() {
        return false;
    }

    @Override
    public String getLinkText() {
        return null;
    }

    @Override
    public String getLinkURL() {
        return null;
    }

    @Override
    public Date getPublishTime() {
        return null;
    }

    @Override
    public int getVisitCount() {
        return 0;
    }
}
