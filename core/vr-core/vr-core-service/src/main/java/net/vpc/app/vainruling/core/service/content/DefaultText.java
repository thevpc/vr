package net.vpc.app.vainruling.core.service.content;

import net.vpc.app.vainruling.core.service.model.strict.AppUserStrict;

import java.util.Date;
import java.util.List;

/**
 * Created by vpc on 9/11/16.
 */
public class DefaultText implements ContentText {
    private int id;
    private String category;
    private String decoration;
    private String subject;
    private String subTitle;
    private String content;
    private String imageURL;
    private AppUserStrict user;
    private List<ContentPath> attachments;
    private List<ContentPath> imageAttachments;
    private List<ContentPath> nonImageAttachments;
    private String linkClassStyle;
    private boolean important;
    private boolean noSubject;
    private String linkText;
    private String linkURL;
    private Date publishTime;
    private int visitCount;

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public AppUserStrict getUser() {
        return user;
    }

    public void setUser(AppUserStrict user) {
        this.user = user;
    }

    @Override
    public List<ContentPath> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ContentPath> attachments) {
        this.attachments = attachments;
    }

    @Override
    public List<ContentPath> getImageAttachments() {
        return imageAttachments;
    }

    public void setImageAttachments(List<ContentPath> imageAttachments) {
        this.imageAttachments = imageAttachments;
    }

    @Override
    public List<ContentPath> getNonImageAttachments() {
        return nonImageAttachments;
    }

    public void setNonImageAttachments(List<ContentPath> nonImageAttachments) {
        this.nonImageAttachments = nonImageAttachments;
    }

    @Override
    public String getLinkClassStyle() {
        return linkClassStyle;
    }

    public void setLinkClassStyle(String linkClassStyle) {
        this.linkClassStyle = linkClassStyle;
    }

    @Override
    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    @Override
    public boolean isNoSubject() {
        return noSubject;
    }

    public void setNoSubject(boolean noSubject) {
        this.noSubject = noSubject;
    }

    @Override
    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    @Override
    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    @Override
    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
}
