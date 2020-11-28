package net.thevpc.app.vainruling.core.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.thevpc.app.vainruling.VrCompletionInfo;

public class DefaultCompletionInfo implements VrCompletionInfo {

    private String category;
    private Object objectId;

    private String objectName;

    private String objectType;

    private float completion;

    private String message;
    private String content;
    private String objectTypeName;
    private String categoryName;

    private Level messageLevel;

    private List<CompletionInfoAction> actions = new ArrayList<>();
    private String[] filters = new String[0];

    public DefaultCompletionInfo() {
    }

    public DefaultCompletionInfo(String category, String categoryName, Object objectId, String objectName, String objectType, String objectTypeName, float completion, String message, String content, Level messageLevel, String[] filters, List<CompletionInfoAction> actions) {
        this.categoryName = categoryName;
        this.category = category;
        this.filters = filters;
        this.objectId = objectId;
        this.objectName = objectName;
        this.objectType = objectType;
        this.objectTypeName = objectTypeName;
        this.completion = completion;
        this.message = message;
        this.content = content;
        this.messageLevel = messageLevel;
        this.actions = actions;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public Object getObjectId() {
        return objectId;
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public String getObjectType() {
        return objectType;
    }

    @Override
    public float getCompletion() {
        return completion;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Level getMessageLevel() {
        return messageLevel;
    }

    @Override
    public List<CompletionInfoAction> getActions() {
        return actions;
    }

    @Override
    public String[] getFilters() {
        return filters;
    }

    @Override
    public String getFilter(int index) {
        return (filters == null || filters.length <= index) ? null : filters[index];
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setObjectId(Object objectId) {
        this.objectId = objectId;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public void setCompletion(float completion) {
        this.completion = completion;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setMessageLevel(Level messageLevel) {
        this.messageLevel = messageLevel;
    }

    public void setActions(List<CompletionInfoAction> actions) {
        this.actions = actions;
    }

    public void setFilters(String[] filters) {
        this.filters = filters;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
