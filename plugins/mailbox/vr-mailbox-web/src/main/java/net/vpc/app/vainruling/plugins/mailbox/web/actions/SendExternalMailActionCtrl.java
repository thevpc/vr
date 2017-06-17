/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.notification.VrNotificationEvent;
import net.vpc.app.vainruling.core.service.notification.VrNotificationSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.obj.ObjCtrl;
import net.vpc.app.vainruling.core.web.obj.PropertyView;
import net.vpc.app.vainruling.core.web.obj.PropertyViewManager;
import net.vpc.app.vainruling.core.web.obj.ViewContext;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.SendExternalMailConfig;
import net.vpc.app.vainruling.core.service.model.content.ArticlesItem;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxMessageFormat;
import net.vpc.common.gomail.RecipientType;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.NamedId;
import net.vpc.upa.UPA;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController
public class SendExternalMailActionCtrl {

    private static final Logger log = Logger.getLogger(SendExternalMailActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    private MailboxPlugin mailbox;
    private Model model = new Model();
    @Autowired
    private PropertyViewManager propertyViewManager;

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        if (config == null) {
            config = new Config();
        }

        ViewContext viewContext = new ViewContext();
        PropertyView emailType = propertyViewManager.createPropertyViews("emailType", RecipientType.class, null, viewContext)[0];
        getModel().setEmailType(emailType);
        //MailboxMessageFormat
        getModel().setMailboxMessageFormat(propertyViewManager.createPropertyViews("mailboxMessageFormat", MailboxMessageFormat.class, null, viewContext)[0]);
        String t = config.getTitle();

        getModel().setTitle(StringUtils.isEmpty(t) ? "Envoi de Mail" : t);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/mailbox/send-external-mail-dialog", options, null);

    }

    public void onUpdate() {
//        System.out.println("on update SendExternalMailActionCtrl");
    }

    public void startExec() {
        VrApp.getBean(VrNotificationSession.class).clear(CorePlugin.SEND_EXTERNAL_MAIL_QUEUE);
        SendExternalMailConfig c = new SendExternalMailConfig();
        c.setEmailType((RecipientType) getModel().getEmailType().getValue());
        Object value = getModel().getMailboxMessageFormat().getValue();
        if (value instanceof NamedId) {
            value = UPA.getPersistenceUnit().findById(MailboxMessageFormat.class, ((NamedId) value).getId());
        }
        MailboxMessageFormat mailboxMessageFormat = (MailboxMessageFormat) value;
        c.setTemplateId(mailboxMessageFormat == null ? null : mailboxMessageFormat.getId());

//        core.runThread(new Runnable() {
//            @Override
//            public void run() {
        try {
            ArticlesItem obj = (ArticlesItem) VrApp.getBean(ObjCtrl.class).getCurrentEntityObject();
            mailbox.sendExternalMail(obj, VrUtils.formatJSONObject(c));
        } catch (Exception e) {
            FacesUtils.addErrorMessage(e.getMessage());
            e.printStackTrace();
        }
//            }
//        });
    }

    public void onChange() {

    }

    public void fireEventExtraDialogClosed() {
        VrApp.getBean(VrNotificationSession.class).clear(CorePlugin.SEND_EXTERNAL_MAIL_QUEUE);
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public Model getModel() {
        return model;
    }

    public static class Config {

        private String type;
        private String title;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


    }

    public static class Model {

        private String title;
        private boolean errorsOnly;
        private PropertyView emailType;
        private PropertyView mailboxMessageFormat;

        public List<VrNotificationEvent> getEvents() {
            List<VrNotificationEvent> evts = VrApp.getBean(VrNotificationSession.class).findAll(CorePlugin.SEND_EXTERNAL_MAIL_QUEUE);
            if (errorsOnly) {
                for (Iterator<VrNotificationEvent> i = evts.iterator(); i.hasNext(); ) {
                    VrNotificationEvent v = i.next();
                    if (v.getLevel().intValue() < Level.WARNING.intValue()) {
                        i.remove();
                    }
                }
            }
            Collections.sort(evts, new Comparator<VrNotificationEvent>() {
                @Override
                public int compare(VrNotificationEvent o1, VrNotificationEvent o2) {
                    return o2.getCreationTime().compareTo(o1.getCreationTime());
                }

            });
            return evts;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isErrorsOnly() {
            return errorsOnly;
        }

        public void setErrorsOnly(boolean errorOnly) {
            this.errorsOnly = errorOnly;
        }

        public PropertyView getEmailType() {
            return emailType;
        }

        public void setEmailType(PropertyView emailType) {
            this.emailType = emailType;
        }

        public PropertyView getMailboxMessageFormat() {
            return mailboxMessageFormat;
        }

        public void setMailboxMessageFormat(PropertyView mailboxMessageFormat) {
            this.mailboxMessageFormat = mailboxMessageFormat;
        }


    }

}
