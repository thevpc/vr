/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.forum.service.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;
import net.thevpc.upa.types.DateTime;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.sendTime desc")
@Path("Social")
public class ForumPost {

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
    private AppUser user;

    @Summary
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private ForumThread thread;

    private ForumPost baseMessage;

    @Path("Trace")
    @Summary
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Flags"))
    private boolean read;

    //    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Time"))
    @Summary
    private DateTime sendTime;
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    private boolean archived;
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

    public DateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(DateTime sendTime) {
        this.sendTime = sendTime;
    }

    public ForumThread getThread() {
        return thread;
    }

    public void setThread(ForumThread thread) {
        this.thread = thread;
    }

    public ForumPost getBaseMessage() {
        return baseMessage;
    }

    public void setBaseMessage(ForumPost baseMessage) {
        this.baseMessage = baseMessage;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

}
