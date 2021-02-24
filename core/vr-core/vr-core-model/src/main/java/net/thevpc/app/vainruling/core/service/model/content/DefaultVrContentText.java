package net.thevpc.app.vainruling.core.service.model.content;

import java.io.Serializable;
import java.util.Collections;
import net.thevpc.app.vainruling.core.service.model.strict.AppUserStrict;

import java.util.Date;
import java.util.List;
import net.thevpc.app.vainruling.core.service.content.VrContentPath;
import net.thevpc.app.vainruling.core.service.content.VrContentText;

public class DefaultVrContentText implements VrContentText, Serializable {

    private int id;
    private AppArticleDispositionGroup dispositionGroup;
    private AppUserStrict user;
    private String content;
    private String decoration;
    private String[] categories=new String[0];
    private String recipients;
    private String title;
    private String subTitle;
    private Date publishTime;
    private int position;
    private int visitCount;
    private boolean noTitle;
    private boolean important;
    private boolean deleted;
    private boolean archived;
    private VrContentPath mainPath;
    private List<VrContentPath> attachments = Collections.emptyList();
    private boolean hasImageAttachments;
    private boolean hasNonImageAttachments;

    public DefaultVrContentText() {

    }

    public boolean isHasImageAttachments() {
        return hasImageAttachments;
    }

    public boolean isHasNonImageAttachments() {
        return hasNonImageAttachments;
    }

    public void setHasImageAttachments(boolean hasImageAttachments) {
        this.hasImageAttachments = hasImageAttachments;
    }

    public void setHasNonImageAttachments(boolean hasNonImageAttachments) {
        this.hasNonImageAttachments = hasNonImageAttachments;
    }

    @Override
    public VrContentPath getMainPath() {
        return mainPath;
    }

    public void setMainPath(VrContentPath mainPath) {
        this.mainPath = mainPath;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public List<VrContentPath> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<VrContentPath> attachments) {
        this.attachments = attachments;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public boolean isNoTitle() {
        return noTitle;
    }

    public void setNoTitle(boolean noTitle) {
        this.noTitle = noTitle;
    }

    public AppUserStrict getUser() {
        return user;
    }

    public void setUser(AppUserStrict user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppArticleDispositionGroup getDispositionGroup() {
        return dispositionGroup;
    }

    public void setDispositionGroup(AppArticleDispositionGroup dispositionGroup) {
        this.dispositionGroup = dispositionGroup;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultVrContentText that = (DefaultVrContentText) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
