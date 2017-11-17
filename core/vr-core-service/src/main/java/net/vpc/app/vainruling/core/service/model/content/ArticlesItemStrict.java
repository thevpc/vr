package net.vpc.app.vainruling.core.service.model.content;

import net.vpc.app.vainruling.core.service.model.strict.AppUserStrict;

import java.util.Date;

public class ArticlesItemStrict {
    private int id;
    private ArticlesDispositionStrict disposition;
    private ArticlesDispositionGroup dispositionGroup;
    private AppUserStrict sender;
    private String content;
    private String decoration;
    private String imageURL;
    private String linkClassStyle;
    private String recipientProfiles;
    private String subject;
    private String subTitle;
    private String linkText;
    private String linkURL;
    private Date sendTime;
    private int position;
    private int visitCount;
    private boolean noSubject;
    private boolean important;
    public ArticlesItemStrict(ArticlesItem item) {
        id=item.getId();
        disposition=new ArticlesDispositionStrict(item.getDisposition());
        imageURL=item.getImageURL();
        linkClassStyle=item.getLinkClassStyle();
        recipientProfiles=item.getRecipientProfiles();
        decoration=item.getDecoration();
        content=item.getContent();
        subject=item.getSubject();
        subTitle=item.getSubTitle();
        dispositionGroup=item.getDispositionGroup();
        position=item.getPosition();
        visitCount=item.getVisitCount();
        sender=new AppUserStrict(item.getSender());
        noSubject=item.isNoSubject();
        important=item.isImportant();
        linkText=item.getLinkText();
        linkURL=item.getLinkURL();
        sendTime=item.getSendTime();
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public boolean isNoSubject() {
        return noSubject;
    }

    public void setNoSubject(boolean noSubject) {
        this.noSubject = noSubject;
    }

    public AppUserStrict getSender() {
        return sender;
    }

    public void setSender(AppUserStrict sender) {
        this.sender = sender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArticlesDispositionStrict getDisposition() {
        return disposition;
    }

    public void setDisposition(ArticlesDispositionStrict disposition) {
        this.disposition = disposition;
    }

    public ArticlesDispositionGroup getDispositionGroup() {
        return dispositionGroup;
    }

    public void setDispositionGroup(ArticlesDispositionGroup dispositionGroup) {
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLinkClassStyle() {
        return linkClassStyle;
    }

    public void setLinkClassStyle(String linkClassStyle) {
        this.linkClassStyle = linkClassStyle;
    }

    public String getRecipientProfiles() {
        return recipientProfiles;
    }

    public void setRecipientProfiles(String recipientProfiles) {
        this.recipientProfiles = recipientProfiles;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArticlesItemStrict that = (ArticlesItemStrict) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
