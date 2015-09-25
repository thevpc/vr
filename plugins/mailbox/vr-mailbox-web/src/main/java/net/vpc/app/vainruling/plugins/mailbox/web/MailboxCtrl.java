/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.BreadcrumbItem;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UCtrlData;
import net.vpc.app.vainruling.api.web.UCtrlProvider;
import net.vpc.app.vainruling.api.web.VRMenuDef;
import net.vpc.app.vainruling.api.web.VRMenuDefFactory;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxFolder;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxReceived;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxSent;
import net.vpc.upa.UPA;

/**
 *
 * @author vpc
 */
@UCtrl
@ManagedBean
public class MailboxCtrl implements UCtrlProvider, VRMenuDefFactory {

    private static final Logger log = Logger.getLogger(MailboxCtrl.class.getName());
    private Model model = new Model();

    public void onNew() {
        getModel().setNewItem(new MailboxSent());
        getModel().setMode(EditCtrlMode.NEW);
        getModel().setCurrent(null);
    }

    @Override
    public UCtrlData getUCtrl(String cmd) {
        Config config = VrHelper.parseObject(cmd, Config.class);
        if (config.folder == null) {
            config.folder = MailboxFolder.CURRENT;
        }
        switch (config.folder) {
            case CURRENT: {
                return new UCtrlData(getPreferredTitle(config.folder, config.sent), "modules/mailbox/mailbox", "fa-table",
                        new BreadcrumbItem("Social", "fa-dashboard", "", "")
                );
            }
            case DELETED: {
                return new UCtrlData(getPreferredTitle(config.folder, config.sent), "modules/mailbox/mailbox", "fa-table",
                        new BreadcrumbItem("Social", "fa-dashboard", "", "")
                );
            }
            case ARCHIVED: {
                return new UCtrlData(getPreferredTitle(config.folder, config.sent), "modules/mailbox/mailbox", "fa-table",
                        new BreadcrumbItem("Social", "fa-dashboard", "", "")
                );
            }
        }
        return null;
    }

    public String getPreferredTitle() {
        return getPreferredTitle(getModel().getFolder(), getModel().isSent());
    }

    public String getPreferredTitle(MailboxFolder f, boolean sent) {
        if (sent) {
            switch (f) {
                case CURRENT: {
                    return "Boîte Envoi";
                }
                case DELETED: {
                    return "Corbeille";
                }
                case ARCHIVED: {
                    return "Archive";
                }
            }
        } else {
            switch (f) {
                case CURRENT: {
                    return "Boîte réception";
                }
                case DELETED: {
                    return "Corbeille";
                }
                case ARCHIVED: {
                    return "Archive";
                }
            }
        }
        return "Mes Messages";
    }

    @Override
    public List<VRMenuDef> createVRMenuDefList() {
        return Arrays.asList(
                new VRMenuDef("Mes Messages", "/Social", "mailbox", "{folder:'CURRENT',sent:false}", "Custom.Site.Mailbox","")//, 
//                new VRMenuDef("Messages Envoyés", "/Social", "mailbox", "{folder:'CURRENT',sent:true}", "Custom.Site.Outbox","")
        //                , new VRMenuDef("Mes Messages Archivés", "/Social", "mailbox", "{folder:'ARCHIVED'}", "Custom.Site.Mailbox.Archived")
        );
    }

    @OnPageLoad
    public void onPageLoad(Config config) {
        if (config.folder == null) {
            config.folder = MailboxFolder.CURRENT;
        }
        getModel().setFolder(config.folder);
        getModel().setSent(config.sent);
        onRefresh();
    }

    public void onRefresh() {
        MailboxPlugin p = VrApp.getBean(MailboxPlugin.class);
        List<Row> list = new ArrayList<>();
        int userId = VrApp.getBean(UserSession.class).getUser().getId();
        if (getModel().isSent()) {
            for (MailboxSent inbox : p.loadLocalOutbox(userId, -1, false, getModel().getFolder())) {
                Row r = new Row();
                r.setValue(new Message(inbox));
                r.setHasAttachment(false);
                list.add(r);
            }
        } else {
            for (MailboxReceived inbox : p.loadLocalMailbox(userId, -1, false, getModel().getFolder())) {
                Row r = new Row();
                r.setValue(new Message(inbox));
                r.setHasAttachment(false);
                list.add(r);
            }
        }
        model.setInbox(list);
    }

    public Model getModel() {
        return model;
    }

    public void onSelectInboxFolder() {
        getModel().setSent(false);
        getModel().setFolder(MailboxFolder.CURRENT);
        onRefresh();
    }

    public void onSelectDeletedInboxFolder() {
        getModel().setSent(false);
        getModel().setFolder(MailboxFolder.DELETED);
        onRefresh();
    }

    public void onSelectArchivedInboxFolder() {
        getModel().setSent(false);
        getModel().setFolder(MailboxFolder.ARCHIVED);
        onRefresh();
    }

    public void onSelectOutboxFolder() {
        getModel().setSent(true);
        getModel().setFolder(MailboxFolder.CURRENT);
        onRefresh();
    }

    public void onSelectDeletedOutboxFolder() {
        getModel().setSent(true);
        getModel().setFolder(MailboxFolder.DELETED);
        onRefresh();
    }

    public void onSelectArchivedOutboxFolder() {
        getModel().setSent(true);
        getModel().setFolder(MailboxFolder.ARCHIVED);
        onRefresh();
    }

    public void onSelect(Message r) {
        getModel().setCurrent(r);
        if (r != null && r.msg instanceof MailboxReceived) {
            MailboxPlugin p = VrApp.getBean(MailboxPlugin.class);
            MailboxReceived m = (MailboxReceived) r.msg;
            p.markRead(m.getId(), true);
            m.setRead(true);//no need for refresh
        }
    }

    public void onCancelCurrent() {
        getModel().setNewItem(new MailboxSent());
        getModel().setMode(EditCtrlMode.LIST);
    }

    public void onRemoveSelected() {
        if (getModel().getFolder() == MailboxFolder.DELETED) {
            onEraseSelected();
            return;
        }
        for (Row inbox : getModel().getInbox()) {
            if (inbox.isSelected()) {
                inbox.getValue().setDeleted(true);
                inbox.getValue().setDeletedBy(VrApp.getBean(UserSession.class).getUser().getLogin());
                UPA.getPersistenceUnit().merge(inbox.getValue().msg);
            }
        }
        onRefresh();
    }

    public void onArchiveSelected() {
        for (Row inbox : getModel().getInbox()) {
            if (inbox.isSelected()) {
                inbox.getValue().setArchived(true);
                UPA.getPersistenceUnit().merge(inbox.getValue().msg);
            }
        }
        onRefresh();
    }

    public void onEraseSelected() {
        for (Row inbox : getModel().getInbox()) {
            if (inbox.isSelected()) {
                UPA.getPersistenceUnit().remove(inbox.value.msg);
            }
        }
        onRefresh();
    }

    public void onSend() {
        MailboxPlugin p = VrApp.getBean(MailboxPlugin.class);
        getModel().getNewItem().setSender(VrApp.getBean(UserSession.class).getUser());
        p.sendLocalMail(getModel().getNewItem(), true);
        getModel().setNewItem(new MailboxSent());
        getModel().setMode(EditCtrlMode.LIST);
        onRefresh();
    }

    public void onReply() {
        MailboxSent s = new MailboxSent();
        Message current = getModel().getCurrent();
        if (current != null && current.recieved) {
            MailboxReceived r=(MailboxReceived) current.msg;
            s.setToProfiles(""+r.getSender().getLogin()+",(("+r.getToProfiles()+")-"+r.getOwner().getLogin()+")");
            s.setCcProfiles(r.getCcProfiles());
            s.setSubject("RE:"+r.getSubject());
            getModel().setNewItem(s);
            getModel().setMode(EditCtrlMode.NEW);
            getModel().setCurrent(null);
        }
    }

    public void onAdvancedSettings() {
        getModel().setAdvancedSettings(!getModel().isAdvancedSettings());
    }

    public boolean isEnabledButton(String buttonId) {
        switch (getModel().getMode()) {
            case NEW: {
                if ("Send".equals(buttonId)) {
                    return true;
                }
                if ("Cancel".equals(buttonId)) {
                    return true;
                }
                if ("Advanced".equals(buttonId)) {
                    //check rights?
                    return true;
                }
                return false;
            }
            case LIST: {
                if ("Refresh".equals(buttonId)) {
                    return true;
                }
                if ("New".equals(buttonId)) {
                    return true;
                }
                if ("Remove".equals(buttonId)) {
                    return true;
                }
                if ("Archive".equals(buttonId)) {
                    return true;
                }
                if ("Reply".equals(buttonId)) {
                    return !getModel().isSent() && getModel().getCurrent() != null;
                }
                return false;
            }
        }
        return false;
    }

    public static class Row {

        Message value;
        boolean selected;
        boolean hasAttachment;

        public Message getValue() {
            return value;
        }

        public void setValue(Message value) {
            this.value = value;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isHasAttachment() {
            return hasAttachment;
        }

        public void setHasAttachment(boolean hasAttachment) {
            this.hasAttachment = hasAttachment;
        }
    }

    public boolean isListMode() {
        try {

            return getModel().getMode() == EditCtrlMode.LIST;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isNewOrUpdateMode() {
        try {
            return getModel().getMode() == EditCtrlMode.NEW || getModel().getMode() == EditCtrlMode.UPDATE;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isNewMode() {
        try {
            return getModel().getMode() == EditCtrlMode.NEW;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public static class Config {

        public MailboxFolder folder;
        public boolean sent = false;
    }

    public static class Message {

        Object msg;
        boolean recieved;

        public Message(MailboxReceived msg) {
            this.msg = msg;
            this.recieved = true;
        }

        public Message(MailboxSent msg) {
            this.msg = msg;
            this.recieved = false;
        }

        public String getCategory() {
            if (recieved) {
                return ((MailboxReceived) msg).getCategory();
            }
            return ((MailboxSent) msg).getCategory();
        }

        public void setCategory(String category) {
            if (recieved) {
                ((MailboxReceived) msg).setCategory(category);
            } else {
                ((MailboxSent) msg).setCategory(category);
            }
        }

        public String getToProfiles() {
            if (recieved) {
                return ((MailboxReceived) msg).getToProfiles();
            }
            return ((MailboxSent) msg).getToProfiles();
        }

        public void setToProfiles(String value) {
            if (recieved) {
                ((MailboxReceived) msg).setToProfiles(value);
            } else {
                ((MailboxSent) msg).setToProfiles(value);
            }
        }

        public String getCcProfiles() {
            if (recieved) {
                return ((MailboxReceived) msg).getCcProfiles();
            }
            return ((MailboxSent) msg).getCcProfiles();
        }

        public void setCcProfiles(String value) {
            if (recieved) {
                ((MailboxReceived) msg).setCcProfiles(value);
            } else {
                ((MailboxSent) msg).setCcProfiles(value);
            }
        }

        public String getSubject() {
            if (recieved) {
                return ((MailboxReceived) msg).getSubject();
            }
            return ((MailboxSent) msg).getSubject();
        }

        public void setSubject(String value) {
            if (recieved) {
                ((MailboxReceived) msg).setSubject(value);
            } else {
                ((MailboxSent) msg).setSubject(value);
            }
        }

        public String getContent() {
            if (recieved) {
                return ((MailboxReceived) msg).getContent();
            }
            return ((MailboxSent) msg).getContent();
        }

        public Date getSendTime() {
            if (recieved) {
                return ((MailboxReceived) msg).getSendTime();
            }
            return ((MailboxSent) msg).getSendTime();
        }

        public void setContent(String value) {
            if (recieved) {
                ((MailboxReceived) msg).setContent(value);
            } else {
                ((MailboxSent) msg).setContent(value);
            }
        }

        public boolean isRead() {
            if (recieved) {
                return ((MailboxReceived) msg).isRead();
            }
            return ((MailboxSent) msg).isRead();
        }

        public boolean isImportant() {
            if (recieved) {
                return ((MailboxReceived) msg).isImportant();
            }
            return ((MailboxSent) msg).isImportant();
        }

        public void setImportant(boolean value) {
            if (recieved) {
                ((MailboxReceived) msg).setImportant(value);
            } else {
                ((MailboxSent) msg).setImportant(value);
            }
        }

        public boolean isDeleted() {
            if (recieved) {
                return ((MailboxReceived) msg).isDeleted();
            }
            return ((MailboxSent) msg).isDeleted();
        }

        public void setDeleted(boolean value) {
            if (recieved) {
                ((MailboxReceived) msg).setDeleted(value);
            } else {
                ((MailboxSent) msg).setDeleted(value);
            }
        }

        public boolean isArchived() {
            if (recieved) {
                return ((MailboxReceived) msg).isArchived();
            }
            return ((MailboxSent) msg).isArchived();
        }

        public void setArchived(boolean value) {
            if (recieved) {
                ((MailboxReceived) msg).setArchived(value);
            } else {
                ((MailboxSent) msg).setArchived(value);
            }
        }

        public String getDeletedBy() {
            if (recieved) {
                return ((MailboxReceived) msg).getDeletedBy();
            }
            return ((MailboxSent) msg).getDeletedBy();
        }

        public void setDeletedBy(String value) {
            if (recieved) {
                ((MailboxReceived) msg).setDeletedBy(value);
            } else {
                ((MailboxSent) msg).setDeletedBy(value);
            }
        }

        public String getUserFullName() {
            if (recieved) {
                return ((MailboxReceived) msg).getSender() == null ? null : ((MailboxReceived) msg).getSender().getFullName();
            }
            return ((MailboxSent) msg).getToProfiles();
        }
    }

    public static class Model {

        private MailboxFolder folder = MailboxFolder.CURRENT;
        private boolean sent = false;
        private Message current;
        private MailboxSent newItem = new MailboxSent();
        private List<Row> inbox = new ArrayList<>();
        private EditCtrlMode mode = EditCtrlMode.LIST;
        private boolean advancedSettings;

        public List<Row> getInbox() {
            return inbox;
        }

        public void setInbox(List<Row> inbox) {
            this.inbox = inbox;
        }

        public Message getCurrent() {
            return current;
        }

        public void setCurrent(Message current) {
            this.current = current;
        }

        public EditCtrlMode getMode() {
            return mode;
        }

        public void setMode(EditCtrlMode mode) {
            this.mode = mode;
        }

        public MailboxSent getNewItem() {
            return newItem;
        }

        public void setNewItem(MailboxSent newItem) {
            this.newItem = newItem;
        }

        public MailboxFolder getFolder() {
            return folder;
        }

        public void setFolder(MailboxFolder folder) {
            this.folder = folder;
        }

        public boolean isAdvancedSettings() {
            return advancedSettings;
        }

        public void setAdvancedSettings(boolean advancedSettings) {
            this.advancedSettings = advancedSettings;
        }

        public boolean isSent() {
            return sent;
        }

        public void setSent(boolean sent) {
            this.sent = sent;
        }

    }
}
