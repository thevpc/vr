/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.service.model;

import net.vpc.app.vainruling.core.service.content.ContentPath;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.AppUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class FullArticle implements ContentText{

    private ArticlesItem articlesItem;
    private List<ContentPath> attachments;
    private List<ContentPath> imageAttachments;
    private List<ContentPath> nonImageAttachments;

    public FullArticle(ArticlesItem articlesItem, List<ArticlesFile> attachments) {
        this.articlesItem = articlesItem;
        this.attachments = new ArrayList<>();
        for (ArticlesFile attachment : attachments) {
            this.attachments.add(new DefaultContentPath(attachment));
        }
        this.imageAttachments = new ArrayList<>();
        this.nonImageAttachments = new ArrayList<>();
        for (ArticlesFile attachment : attachments) {
            String n = (attachment != null && attachment.getPath() != null) ? attachment.getPath().toLowerCase() : "";
            if (n.endsWith(".gif") || n.endsWith(".jpeg") || n.endsWith(".jpg") || n.endsWith(".png")) {
                imageAttachments.add(new DefaultContentPath(attachment));
            } else {
                nonImageAttachments.add(new DefaultContentPath(attachment));
            }
        }
    }

    public ArticlesItem getArticlesItem() {
        return articlesItem;
    }

    public void setArticlesItem(ArticlesItem content) {
        this.articlesItem = content;
    }

    public List<ContentPath> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ContentPath> attachments) {
        this.attachments = attachments;
    }

    public List<ContentPath> getImageAttachments() {
        return imageAttachments;
    }

    public void setImageAttachments(List<ContentPath> imageAttachments) {
        this.imageAttachments = imageAttachments;
    }

    public List<ContentPath> getNonImageAttachments() {
        return nonImageAttachments;
    }

    public void setNonImageAttachments(List<ContentPath> nonImageAttachments) {
        this.nonImageAttachments = nonImageAttachments;
    }

    @Override
    public int getId() {
        return articlesItem.getId();
    }

    @Override
    public String getSubject() {
        return articlesItem.getSubject();
    }

    @Override
    public String getContent() {
        return articlesItem.getContent();
    }

    @Override
    public String getCategory() {
        return articlesItem.getDisposition()==null?null:articlesItem.getDisposition().getName();
    }

    @Override
    public String getImageURL() {
        return articlesItem.getImageURL();
    }

    @Override
    public AppUser getUser() {
        return articlesItem.getSender();
    }

    @Override
    public String getLinkClassStyle() {
        return articlesItem.getLinkClassStyle();
    }

    @Override
    public boolean isNoSubject() {
        return articlesItem.isNoSubject();
    }

    @Override
    public boolean isImportant() {
        return articlesItem.isImportant();
    }

    @Override
    public String getLinkText() {
        return articlesItem.getLinkText();
    }

    @Override
    public String getLinkURL() {
        return articlesItem.getLinkURL();
    }

    @Override
    public Date getPublishTime() {
        return articlesItem.getSendTime();
    }

    @Override
    public int getVisitCount() {
        return articlesItem.getVisitCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullArticle that = (FullArticle) o;

        if (articlesItem != null ? !articlesItem.equals(that.articlesItem) : that.articlesItem != null) return false;
        if (attachments != null ? !attachments.equals(that.attachments) : that.attachments != null) return false;
        if (imageAttachments != null ? !imageAttachments.equals(that.imageAttachments) : that.imageAttachments != null)
            return false;
        return nonImageAttachments != null ? nonImageAttachments.equals(that.nonImageAttachments) : that.nonImageAttachments == null;

    }

    @Override
    public int hashCode() {
        int result = articlesItem != null ? articlesItem.hashCode() : 0;
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        result = 31 * result + (imageAttachments != null ? imageAttachments.hashCode() : 0);
        result = 31 * result + (nonImageAttachments != null ? nonImageAttachments.hashCode() : 0);
        return result;
    }
}
