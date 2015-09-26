/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.service.model;

import java.sql.Timestamp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.types.DateTime;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "deleted, archived, position desc, sendTime desc")
@Path("Social")
@Properties(
        //i is a ObjRow!
        @Property(name = UIConstants.Grid.ROW_STYLE,
                value = "(i.value.deleted or i.value.archived or i.value.disposition eq null) ?'vr-row-not-relevant':''")
)
public class ArticlesItem {

    @Id
    @Sequence
    private int id;

    @Field(modifiers = UserFieldModifier.MAIN)
    private String subject;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.RICHTEXTAREA))
    @Field(max = "32000")
    private String content;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Attatchment"))
    @Field(max = "1024")
    private String linkURL;

    private String linkText;

    private String linkClassStyle;

    private String imageURL;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Flags"))
    @Field(defaultValue = "0", modifiers = UserFieldModifier.SUMMARY)
    private int position;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private boolean important;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "SourceAndDestination"))
    private AppUser sender;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String recipientProfiles;

    private String filterExpression;

    private EmailType emailType = EmailType.TOEACH;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "TimeAndLayout"))
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private DateTime sendTime;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    private ArticlesDisposition disposition;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Trace"))
    private boolean archived;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    private boolean deleted;

    private String deletedBy;

    private Timestamp deletedOn;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public AppUser getSender() {
        return sender;
    }

    public void setSender(AppUser sender) {
        this.sender = sender;
    }

    public String getRecipientProfiles() {
        return recipientProfiles;
    }

    public void setRecipientProfiles(String recipientProfiles) {
        this.recipientProfiles = recipientProfiles;
    }

    public DateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(DateTime sendTime) {
        this.sendTime = sendTime;
    }

    public ArticlesDisposition getDisposition() {
        return disposition;
    }

    public void setDisposition(ArticlesDisposition disposition) {
        this.disposition = disposition;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Timestamp getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Timestamp deletedOn) {
        this.deletedOn = deletedOn;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
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

    public String getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

}
