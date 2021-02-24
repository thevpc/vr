///*
// * To change this license header, choose License Headers in Project Properties.
// *
// * and open the template in the editor.
// */
//package net.thevpc.app.vainruling.core.service.model.content;
//
//import net.thevpc.app.vainruling.core.service.model.strict.AppUserStrict;
//
//import java.util.Date;
//import java.util.List;
//import net.thevpc.app.vainruling.core.service.content.VrContentPath;
//import net.thevpc.app.vainruling.core.service.content.VrContentText;
//
///**
// * @author taha.bensalah@gmail.com
// */
//public class VrFullArticle implements VrContentText {
//
//    private DefaultVrContentText article;
//    private List<VrContentPath> attachments;
//    private List<VrContentPath> imageAttachments;
//    private List<VrContentPath> nonImageAttachments;
//
//    public VrFullArticle(DefaultVrContentText article, List<VrContentPath> attachments,List<VrContentPath> imageAttachments,List<VrContentPath> nonImageAttachments) {
//        this.article = article;
//        this.attachments = attachments;
//        this.imageAttachments = imageAttachments;
//        this.nonImageAttachments = nonImageAttachments;
//    }
//
//    public ArticlesDispositionStrict getDisposition() {
//        return article.getDisposition();
//    }
//
//    public DefaultVrContentText getArticle() {
//        return article;
//    }
//
//    public void setArticle(DefaultVrContentText content) {
//        this.article = content;
//    }
//
//    public List<VrContentPath> getAttachments() {
//        return attachments;
//    }
//
//    public void setAttachments(List<VrContentPath> attachments) {
//        this.attachments = attachments;
//    }
//
//    public List<VrContentPath> getImageAttachments() {
//        return imageAttachments;
//    }
//
//    public void setImageAttachments(List<VrContentPath> imageAttachments) {
//        this.imageAttachments = imageAttachments;
//    }
//
//    public List<VrContentPath> getNonImageAttachments() {
//        return nonImageAttachments;
//    }
//
//    public void setNonImageAttachments(List<VrContentPath> nonImageAttachments) {
//        this.nonImageAttachments = nonImageAttachments;
//    }
//
//    @Override
//    public String getSubTitle() {
//        return article.getSubTitle();
//    }
//
//    @Override
//    public int getId() {
//        return article.getId();
//    }
//
//    @Override
//    public String getSubject() {
//        return article.getSubject();
//    }
//
//    @Override
//    public String getDecoration() {
//        return article.getDecoration();
//    }
//
//    @Override
//    public String getRecipients() {
//        return article.getRecipients();
//    }
//
//    @Override
//    public String getContent() {
//        return article.getContent();
//    }
//
//    @Override
//    public String getCategory() {
//        return article.getDisposition() == null ? null : article.getDisposition().getName();
//    }
//
//    @Override
//    public String getImageURL() {
//        return article.getImageURL();
//    }
//
//    @Override
//    public AppUserStrict getUser() {
//        return article.getUser();
//    }
//
//    @Override
//    public String getLinkClassStyle() {
//        return article.getLinkClassStyle();
//    }
//
//    @Override
//    public boolean isNoSubject() {
//        return article.isNoSubject();
//    }
//
//    @Override
//    public boolean isImportant() {
//        return article.isImportant();
//    }
//
//    @Override
//    public String getLinkText() {
//        return article.getLinkText();
//    }
//
//    @Override
//    public String getLinkURL() {
//        return article.getLinkURL();
//    }
//
//    @Override
//    public Date getPublishTime() {
//        return article.getPublishTime();
//    }
//
//    @Override
//    public int getVisitCount() {
//        return article.getVisitCount();
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//
//        VrFullArticle that = (VrFullArticle) o;
//
//        if (article != null ? !article.equals(that.article) : that.article != null) {
//            return false;
//        }
//        if (attachments != null ? !attachments.equals(that.attachments) : that.attachments != null) {
//            return false;
//        }
//        if (imageAttachments != null ? !imageAttachments.equals(that.imageAttachments) : that.imageAttachments != null) {
//            return false;
//        }
//        return nonImageAttachments != null ? nonImageAttachments.equals(that.nonImageAttachments) : that.nonImageAttachments == null;
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = article != null ? article.hashCode() : 0;
//        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
//        result = 31 * result + (imageAttachments != null ? imageAttachments.hashCode() : 0);
//        result = 31 * result + (nonImageAttachments != null ? nonImageAttachments.hashCode() : 0);
//        return result;
//    }
//}
