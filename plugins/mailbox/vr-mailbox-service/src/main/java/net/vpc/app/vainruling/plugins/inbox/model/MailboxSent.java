/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;
import net.vpc.upa.types.DateTime;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.sendTime desc")
@Properties(
        {
                        @Property(name = UIConstants.Grid.ROW_STYLE, value = "(i.object.deleted) ?'vr-row-deleted':''")
        })
@Path("Social")
public class MailboxSent {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    @Main
    private String subject;

    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "max")
    private String content;

    @Summary
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "SourceAndDestination"))
    @Path("SourceAndDestination")
    private AppUser sender;

    @Summary
    private boolean templateMessage;

    @Summary
    private boolean externalMessage;

    @Summary
    private String toProfiles;

    @Summary
    private String ccProfiles;

    private String bccProfiles;


    @Summary
    private String category;
    private int threadId;

    @Path("Trace")
    @Summary
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Flags"))
    private boolean read;

    @Summary
    private boolean important;

    private boolean deleteOnRead;
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Time"))
    @Summary
    private DateTime sendTime;

    private DateTime readTime;

    //    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    private boolean archived;
    private boolean richText;
    private boolean deleted;
    private String deletedBy;
    private Timestamp deletedOn;

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

    public boolean isDeleteOnRead() {
        return deleteOnRead;
    }

    public void setDeleteOnRead(boolean deleteOnRead) {
        this.deleteOnRead = deleteOnRead;
    }

    public AppUser getSender() {
        return sender;
    }

    public void setSender(AppUser sender) {
        this.sender = sender;
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

    public String getToProfiles() {
        return toProfiles;
    }

    public void setToProfiles(String toProfiles) {
        this.toProfiles = toProfiles;
    }

    public String getCcProfiles() {
        return ccProfiles;
    }

    public void setCcProfiles(String ccProfiles) {
        this.ccProfiles = ccProfiles;
    }

    public String getBccProfiles() {
        return bccProfiles;
    }

    public void setBccProfiles(String bccProfiles) {
        this.bccProfiles = bccProfiles;
    }

    public boolean isTemplateMessage() {
        return templateMessage;
    }

    public void setTemplateMessage(boolean templateMessage) {
        this.templateMessage = templateMessage;
    }

    public boolean isExternalMessage() {
        return externalMessage;
    }

    public void setExternalMessage(boolean externalMessage) {
        this.externalMessage = externalMessage;
    }

    public boolean isRichText() {
        return richText;
    }

    public void setRichText(boolean richText) {
        this.richText = richText;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }
}
