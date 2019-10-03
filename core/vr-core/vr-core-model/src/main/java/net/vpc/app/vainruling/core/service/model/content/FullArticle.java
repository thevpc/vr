/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model.content;

import net.vpc.app.vainruling.core.service.content.ContentPath;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.strict.AppUserStrict;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class FullArticle implements ContentText{

    private AppArticleStrict article;
    private List<ContentPath> attachments;
    private List<ContentPath> imageAttachments;
    private List<ContentPath> nonImageAttachments;

    public FullArticle(AppArticleStrict article, List<AppArticleFile> attachments) {
        this.article = article;
        this.attachments = new ArrayList<ContentPath>();
        for (AppArticleFile attachment : attachments) {
            this.attachments.add(new DefaultContentPath(attachment));
        }
        this.imageAttachments = new ArrayList<>();
        this.nonImageAttachments = new ArrayList<>();
        for (AppArticleFile attachment : attachments) {
            String n = (attachment != null && attachment.getPath() != null) ? attachment.getPath().toLowerCase() : "";
            if (n.endsWith(".gif") || n.endsWith(".jpeg") || n.endsWith(".jpg") || n.endsWith(".png")) {
                imageAttachments.add(new DefaultContentPath(attachment));
            } else {
                nonImageAttachments.add(new DefaultContentPath(attachment));
            }
        }
    }



    public ArticlesDispositionStrict getDisposition() {
        return article.getDisposition();
    }

    public AppArticleStrict getArticle() {
        return article;
    }

    public void setArticle(AppArticleStrict content) {
        this.article = content;
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
    public String getSubTitle() {
        return article.getSubTitle();
    }

    @Override
    public int getId() {
        return article.getId();
    }

    @Override
    public String getSubject() {
        return article.getSubject();
    }

    @Override
    public String getDecoration() {
        return article.getDecoration();
    }

    @Override
    public String getContent() {
        return article.getContent();
    }

    @Override
    public String getCategory() {
        return article.getDisposition()==null?null:article.getDisposition().getName();
    }

    @Override
    public String getImageURL() {
        return article.getImageURL();
    }

    @Override
    public AppUserStrict getUser() {
        return article.getSender();
    }

    @Override
    public String getLinkClassStyle() {
        return article.getLinkClassStyle();
    }

    @Override
    public boolean isNoSubject() {
        return article.isNoSubject();
    }

    @Override
    public boolean isImportant() {
        return article.isImportant();
    }

    @Override
    public String getLinkText() {
        return article.getLinkText();
    }

    @Override
    public String getLinkURL() {
        return article.getLinkURL();
    }

    @Override
    public Date getPublishTime() {
        return article.getSendTime();
    }

    @Override
    public int getVisitCount() {
        return article.getVisitCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullArticle that = (FullArticle) o;

        if (article != null ? !article.equals(that.article) : that.article != null) return false;
        if (attachments != null ? !attachments.equals(that.attachments) : that.attachments != null) return false;
        if (imageAttachments != null ? !imageAttachments.equals(that.imageAttachments) : that.imageAttachments != null)
            return false;
        return nonImageAttachments != null ? nonImageAttachments.equals(that.nonImageAttachments) : that.nonImageAttachments == null;

    }

    @Override
    public int hashCode() {
        int result = article != null ? article.hashCode() : 0;
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        result = 31 * result + (imageAttachments != null ? imageAttachments.hashCode() : 0);
        result = 31 * result + (nonImageAttachments != null ? nonImageAttachments.hashCode() : 0);
        return result;
    }
}
