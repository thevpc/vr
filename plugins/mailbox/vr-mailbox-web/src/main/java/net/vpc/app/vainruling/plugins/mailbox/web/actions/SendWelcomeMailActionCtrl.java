/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web.actions;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.VrNotificationEvent;
import net.vpc.app.vainruling.api.VrNotificationSession;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
@ManagedBean
@Scope("session")
public class SendWelcomeMailActionCtrl {

    private static final Logger log = Logger.getLogger(SendWelcomeMailActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    private Model model = new Model();
    @Autowired
    private PropertyViewManager propertyViewManager;

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

    public void openDialog(String config) {
        openDialog(VrHelper.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        if (config == null) {
            config = new Config();
        }

        String t = config.getTitle();

        getModel().setTitle(StringUtils.isEmpty(t) ? "Envoi de Mail" : t);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/mailbox/sendWelcomeMailDialog", options, null);

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
            List<AppUser> users = VrApp.getBean(ObjCtrl.class).getModel().getSelectedObjects();
            if ("selected".equals(getModel().getTarget())) {
                VrApp.getBean(MailboxPlugin.class).sendWelcomeEmail(users, true);
            } else if ("new".equals(getModel().getTarget())) {
                VrApp.getBean(MailboxPlugin.class).sendWelcomeEmail(true);
            }
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
        VrApp.getBean(VrNotificationSession.class).clear(MailboxPlugin.SEND_WELCOME_MAIL_QUEUE);
        RequestContext.getCurrentInstance().closeDialog(null);
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
                for (Iterator<VrNotificationEvent> i = evts.iterator(); i.hasNext();) {
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

    public Model getModel() {
        return model;
    }

}
