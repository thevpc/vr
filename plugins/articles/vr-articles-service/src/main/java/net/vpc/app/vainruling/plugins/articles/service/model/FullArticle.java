/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.service.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class FullArticle {

    private ArticlesItem content;
    private List<ArticlesFile> attachments;
    private List<ArticlesFile> imageAttachments;
    private List<ArticlesFile> nonImageAttachments;

    public FullArticle(ArticlesItem content, List<ArticlesFile> attachments) {
        this.content = content;
        this.attachments = attachments;
        this.imageAttachments = new ArrayList<>();
        this.nonImageAttachments = new ArrayList<>();
        for (ArticlesFile attachment : attachments) {
            String n = (attachment != null && attachment.getPath() != null) ? attachment.getPath().toLowerCase() : "";
            if (n.endsWith(".gif") || n.endsWith(".jpeg") || n.endsWith(".jpg") || n.endsWith(".png")) {
                imageAttachments.add(attachment);
            } else {
                nonImageAttachments.add(attachment);
            }
        }
    }

    public ArticlesItem getContent() {
        return content;
    }

    public void setContent(ArticlesItem content) {
        this.content = content;
    }

    public List<ArticlesFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ArticlesFile> attachments) {
        this.attachments = attachments;
    }

    public List<ArticlesFile> getImageAttachments() {
        return imageAttachments;
    }

    public void setImageAttachments(List<ArticlesFile> imageAttachments) {
        this.imageAttachments = imageAttachments;
    }

    public List<ArticlesFile> getNonImageAttachments() {
        return nonImageAttachments;
    }

    public void setNonImageAttachments(List<ArticlesFile> nonImageAttachments) {
        this.nonImageAttachments = nonImageAttachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullArticle that = (FullArticle) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (attachments != null ? !attachments.equals(that.attachments) : that.attachments != null) return false;
        if (imageAttachments != null ? !imageAttachments.equals(that.imageAttachments) : that.imageAttachments != null)
            return false;
        return nonImageAttachments != null ? nonImageAttachments.equals(that.nonImageAttachments) : that.nonImageAttachments == null;

    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        result = 31 * result + (imageAttachments != null ? imageAttachments.hashCode() : 0);
        result = 31 * result + (nonImageAttachments != null ? nonImageAttachments.hashCode() : 0);
        return result;
    }
}
