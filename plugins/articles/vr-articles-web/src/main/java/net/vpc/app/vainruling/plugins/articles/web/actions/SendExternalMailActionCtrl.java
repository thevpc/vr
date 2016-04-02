/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.web.actions;

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
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.app.vainruling.plugins.articles.service.ArticlesPlugin;
import net.vpc.app.vainruling.plugins.articles.service.model.ArticlesItem;
import net.vpc.app.vainruling.plugins.inbox.service.model.EmailType;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxMessageFormat;
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
public class SendExternalMailActionCtrl {

    private static final Logger log = Logger.getLogger(SendExternalMailActionCtrl.class.getName());
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
        
        PropertyView emailType = propertyViewManager.createPropertyView("emailType", EmailType.class, null)[0];
        getModel().setEmailType(emailType);
        //MailboxMessageFormat
        getModel().setMailboxMessageFormat(propertyViewManager.createPropertyView("mailboxMessageFormat", MailboxMessageFormat.class, null)[0]);
        String t = config.getTitle();

        getModel().setTitle(StringUtils.isEmpty(t) ? "Envoi de Mail" : t);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/articles/sendExternalMailDialog", options, null);

    }

    public void onUpdate() {
//        System.out.println("on update SendExternalMailActionCtrl");
    }

    public void startExec() {
        VrApp.getBean(VrNotificationSession.class).clear(ArticlesPlugin.SEND_EXTERNAL_MAIL_QUEUE);
        ArticlesPlugin.SendExternalMailConfig c=new ArticlesPlugin.SendExternalMailConfig();
        c.setEmailType((EmailType)getModel().getEmailType().getValue());
        MailboxMessageFormat mailboxMessageFormat = (MailboxMessageFormat)getModel().getMailboxMessageFormat().getValue();
        c.setTemplateId(mailboxMessageFormat==null?null:mailboxMessageFormat.getId());
        
//        core.runThread(new Runnable() {
//            @Override
//            public void run() {
        try {
            ArticlesItem obj = (ArticlesItem)VrApp.getBean(ObjCtrl.class).getCurrentEntityObject();
            VrApp.getBean(ArticlesPlugin.class).sendExternalMail(obj, VrHelper.formatJSONObject(c));
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
        VrApp.getBean(VrNotificationSession.class).clear(ArticlesPlugin.SEND_EXTERNAL_MAIL_QUEUE);
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public static class Model {

        private String title;
        private boolean errorsOnly;
        private PropertyView emailType;
        private PropertyView mailboxMessageFormat;

        public List<VrNotificationEvent> getEvents() {
            List<VrNotificationEvent> evts = VrApp.getBean(VrNotificationSession.class).findAll(ArticlesPlugin.SEND_EXTERNAL_MAIL_QUEUE);
            if(errorsOnly){
                for(Iterator<VrNotificationEvent> i=evts.iterator();i.hasNext();){
                    VrNotificationEvent v = i.next();
                    if(v.getLevel().intValue()<Level.WARNING.intValue()){
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

    public Model getModel() {
        return model;
    }

}
