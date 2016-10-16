/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.service.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.common.gomail.RecipientType;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;
import net.vpc.upa.types.DateTime;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "sendTime desc")
@Path("Social")
public class MailboxReceived {

    @Id
    @Sequence
    private int id;

    @Main
    private String subject;

    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
//    @Field(max = "30000")
    @Field(max = "max")
    private String content;

    @Summary
    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Flags"))
    private boolean read;

    @Summary
    private boolean important;

    @Summary
    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "SourceAndDestination"))
    private AppUser sender;

    @Summary
    private String toProfiles;

    private String ccProfiles;

    @Summary
    private AppUser owner;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Time"))
    @Summary
    private DateTime sendTime;

    private DateTime readTime;

    @Summary
    private String category;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    private boolean archived;

    private boolean deleted;

    private String deletedBy;

    private Timestamp deletedOn;

    private RecipientType recipientType = RecipientType.TO;

    @ManyToOne(type = RelationshipType.SHADOW_ASSOCIATION)
    private MailboxSent outboxMessage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public String getToProfiles() {
        return toProfiles;
    }

    public void setToProfiles(String toProfiles) {
        this.toProfiles = toProfiles;
    }

    public DateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(DateTime sendTime) {
        this.sendTime = sendTime;
    }

    public DateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(DateTime readTime) {
        this.readTime = readTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public MailboxSent getOutboxMessage() {
        return outboxMessage;
    }

    public void setOutboxMessage(MailboxSent outboxMessage) {
        this.outboxMessage = outboxMessage;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public String getCcProfiles() {
        return ccProfiles;
    }

    public void setCcProfiles(String ccProfiles) {
        this.ccProfiles = ccProfiles;
    }

}
