package net.vpc.app.vainruling.plugins.mailbox.web;

import net.vpc.app.vainruling.core.service.obj.AppFile;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxReceived;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxSent;

import java.util.Date;
import java.util.List;

/**
 * Created by vpc on 10/16/16.
 */
public class Message {

    private Object msg;
    private boolean received;
    private List<AppFile> files;

    public Message(MailboxReceived msg,List<AppFile> files) {
        this.msg = msg;
        this.files = files;
        this.received = true;
    }

    public Object getMsg() {
        return msg;
    }

    public Message(MailboxSent msg, List<AppFile> files) {
        this.msg = msg;
        this.received = false;
        this.files = files;
    }

    public boolean isInbox(){
        return received;
    }

    public boolean isRichText(){
//        if(true){
//            return false;
//        }
        if (received) {
            return ((MailboxReceived) msg).getOutboxMessage().isRichText();
        }
        return ((MailboxSent) msg).isRichText();
    }

    public boolean isOutbox(){
        return !received;
    }

    public String getCategory() {
        if (received) {
            return ((MailboxReceived) msg).getCategory();
        }
        return ((MailboxSent) msg).getCategory();
    }

    public void setCategory(String category) {
        if (received) {
            ((MailboxReceived) msg).setCategory(category);
        } else {
            ((MailboxSent) msg).setCategory(category);
        }
    }

    public String getToProfiles() {
        if (received) {
            return ((MailboxReceived) msg).getToProfiles();
        }
        return ((MailboxSent) msg).getToProfiles();
    }

    public void setToProfiles(String value) {
        if (received) {
            ((MailboxReceived) msg).setToProfiles(value);
        } else {
            ((MailboxSent) msg).setToProfiles(value);
        }
    }

    public String getCcProfiles() {
        if (received) {
            return ((MailboxReceived) msg).getCcProfiles();
        }
        return ((MailboxSent) msg).getCcProfiles();
    }

    public void setCcProfiles(String value) {
        if (received) {
            ((MailboxReceived) msg).setCcProfiles(value);
        } else {
            ((MailboxSent) msg).setCcProfiles(value);
        }
    }

    public String getSubject() {
        if (received) {
            return ((MailboxReceived) msg).getSubject();
        }
        return ((MailboxSent) msg).getSubject();
    }

    public void setSubject(String value) {
        if (received) {
            ((MailboxReceived) msg).setSubject(value);
        } else {
            ((MailboxSent) msg).setSubject(value);
        }
    }

    public String getContent() {
        if (received) {
            return ((MailboxReceived) msg).getContent();
        }
        return ((MailboxSent) msg).getContent();
    }

    public void setContent(String value) {
        if (received) {
            ((MailboxReceived) msg).setContent(value);
        } else {
            ((MailboxSent) msg).setContent(value);
        }
    }

    public Date getSendTime() {
        if (received) {
            return ((MailboxReceived) msg).getSendTime();
        }
        return ((MailboxSent) msg).getSendTime();
    }

    public boolean hasAttachment() {
        return files.size()>0;
    }

    public void setRead(boolean read) {
        if (received) {
            ((MailboxReceived) msg).setRead(read);
        }
    }
    public boolean isRead() {
        if (received) {
            return ((MailboxReceived) msg).isRead();
        }
        return ((MailboxSent) msg).isRead();
    }

    public boolean isImportant() {
        if (received) {
            return ((MailboxReceived) msg).isImportant();
        }
        return ((MailboxSent) msg).isImportant();
    }

    public void setImportant(boolean value) {
        if (received) {
            ((MailboxReceived) msg).setImportant(value);
        } else {
            ((MailboxSent) msg).setImportant(value);
        }
    }

    public boolean isDeleted() {
        if (received) {
            return ((MailboxReceived) msg).isDeleted();
        }
        return ((MailboxSent) msg).isDeleted();
    }

    public int getId() {
        if (received) {
            return ((MailboxReceived) msg).getId();
        }
        return ((MailboxSent) msg).getId();
    }

    public void setDeleted(boolean value) {
        if (received) {
            ((MailboxReceived) msg).setDeleted(value);
        } else {
            ((MailboxSent) msg).setDeleted(value);
        }
    }

    public boolean isArchived() {
        if (received) {
            return ((MailboxReceived) msg).isArchived();
        }
        return ((MailboxSent) msg).isArchived();
    }

    public void setArchived(boolean value) {
        if (received) {
            ((MailboxReceived) msg).setArchived(value);
        } else {
            ((MailboxSent) msg).setArchived(value);
        }
    }

    public int getThreadId() {
        if (received) {
            MailboxSent outboxMessage = ((MailboxReceived) msg).getOutboxMessage();
            return outboxMessage==null?-1:outboxMessage.getThreadId();
        }
        return ((MailboxSent) msg).getThreadId();
    }

    public String getDeletedBy() {
        if (received) {
            return ((MailboxReceived) msg).getDeletedBy();
        }
        return ((MailboxSent) msg).getDeletedBy();
    }

    public void setDeletedBy(String value) {
        if (received) {
            ((MailboxReceived) msg).setDeletedBy(value);
        } else {
            ((MailboxSent) msg).setDeletedBy(value);
        }
    }

    public String getUserPhoto() {
        if (received) {
            return Vr.get().getUserPhoto(((MailboxReceived) msg).getSender() == null ? -1 : ((MailboxReceived) msg).getSender().getId());
        }
        return Vr.get().getUserPhoto(((MailboxSent) msg).getSender() == null ? -1 : ((MailboxSent) msg).getSender().getId());
        //return Vr.get().getUnknownUserPhoto(((MailboxSent) msg).getToProfiles());
    }

    public String getUserFullName() {
        if (received) {
            return ((MailboxReceived) msg).getSender() == null ? null : ((MailboxReceived) msg).getSender().resolveFullTitle();
        }
        return ((MailboxSent) msg).getToProfiles();
    }
    public List<AppFile> getAttachments(){
        return files;
    }
}
