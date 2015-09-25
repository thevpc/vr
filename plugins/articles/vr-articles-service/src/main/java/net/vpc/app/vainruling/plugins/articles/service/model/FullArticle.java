/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.service.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vpc
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

}
