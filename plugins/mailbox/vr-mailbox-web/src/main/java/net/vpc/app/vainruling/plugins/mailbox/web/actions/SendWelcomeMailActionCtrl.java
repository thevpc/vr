/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.notification.VrNotificationEvent;
import net.vpc.app.vainruling.core.service.notification.VrNotificationSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.jsf.ctrl.ObjCtrl;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.PrimeFaces;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController
public class SendWelcomeMailActionCtrl {

    private static final Logger log = Logger.getLogger(SendWelcomeMailActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        if (config == null) {
            config = new Config();
        }

        String t = config.getTitle();

        getModel().setTitle(StringUtils.isEmpty(t) ? "Envoi de Mail" : t);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);

        PrimeFaces.current().dialog().openDynamic("/modules/mailbox/send-welcome-mail-dialog", options, null);

    }

    public void onUpdate() {
//        System.out.println("on update SendExternalMailActionCtrl");
    }

    public void startExec() {
        VrApp.getBean(VrNotificationSession.class).clear(MailboxPlugin.SEND_WELCOME_MAIL_QUEUE);
//        ArticlesPlugin.SendExternalMailConfig c=new ArticlesPlugin.SendExternalMailConfig();
//        c.setEmailType((EmailType)getModel().getEmailType().getValue());
//        MailboxMessageFormat mailboxMessageFormat = (MailboxMessageFormat)getModel().getMailboxMessageFormat().getValue();
//        c.setTemplateId(mailboxMessageFormat==null?null:mailboxMessageFormat.getId());

//        core.runThread(new Runnable() {
//            @Override
//            public void run() {
        try {
            ObjCtrl obj = VrApp.getBean(ObjCtrl.class);
            if ("selected".equals(getModel().getTarget())) {
                if("AppUser".equals(obj.getEntity().getName())) {
                    List<AppUser> users = obj.getSelectedEntityObjects();
                    VrApp.getBean(MailboxPlugin.class).sendWelcomeEmail(users, true);
                }else if("AppContact".equals(obj.getEntity().getName())) {
                    List<AppContact> contacts = obj.getSelectedEntityObjects();
                    List<AppUser> users = new ArrayList<>();
                    for (AppContact contact : contacts) {
                        AppUser u = core.findUserByContact(contact.getId());
                        if(u!=null) {
                            users.add(u);
                        }
                    }
                    VrApp.getBean(MailboxPlugin.class).sendWelcomeEmail(users, true);
                }
            } else if ("new".equals(getModel().getTarget())) {
                VrApp.getBean(MailboxPlugin.class).sendWelcomeEmail(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtils.addErrorMessage(e);
        }
//            }
//        });
    }

    public void onChange() {

    }

    public void fireEventExtraDialogClosed() {
        VrApp.getBean(VrNotificationSession.class).clear(MailboxPlugin.SEND_WELCOME_MAIL_QUEUE);
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
        private String target = "selected";

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public List<VrNotificationEvent> getEvents() {
            List<VrNotificationEvent> evts = VrApp.getBean(VrNotificationSession.class).findAll(MailboxPlugin.SEND_WELCOME_MAIL_QUEUE);
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

    }

}
