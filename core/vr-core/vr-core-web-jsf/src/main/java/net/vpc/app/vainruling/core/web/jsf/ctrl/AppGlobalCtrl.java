/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.NotificationText;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.common.util.Chronometer;
import net.vpc.common.util.DatePart;
import org.springframework.context.annotation.Scope;

import java.util.Calendar;
import java.util.Date;
import net.vpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage
@Scope("singleton")
public class AppGlobalCtrl extends BasePageCtrl {

    private Model model = new Model();

    public AppGlobalCtrl() {
    }

    public Model getModel() {
        return model;
    }

    public void doNotifyShutdown() {
        Calendar ii = Calendar.getInstance();
        ii.add(Calendar.MINUTE, 5);
        getModel().setShutdownTime(ii.getTime());
        getModel().setHeadMessageStyle(null);
        getModel().setHeadMessageText(null);

        NotificationText notificationText = new NotificationText();
        notificationText.setId(10001);
        notificationText.setSubject("Arrêt dans moins de 4 minutes...");
        notificationText.setContent("Arrêt dans moins de 4 minutes...");
        notificationText.setDecoration("severe");
        notificationText.setLinkClassStyle("fire-extinguisher text-red");
        VrApp.getBean(Vr.class).getNotificationTextService().publish(notificationText);
    }

    public void doCancelShutdown() {
        getModel().setShutdownTime(null);
        getModel().setHeadMessageStyle(null);
        getModel().setHeadMessageText(null);
        VrApp.getBean(Vr.class).getNotificationTextService().unpublish(10001);
    }

    public boolean isShutdown() {
        return getModel().getShutdownTime() != null;
    }

    public String getHeadMessageText() {
        if (isShutdown()) {
            long p = getModel().getShutdownTime().getTime() - System.currentTimeMillis();
            if (p <= 60000) {
                return "Arrêt imminent...";
            }
            return "Arrêt dans " + Chronometer.formatPeriodMilli(p, DatePart.MINUTE);
        }
        return getModel().getHeadMessageText();
    }

    public String getHeadMessageStyle() {
        if (isShutdown()) {
            return "error";
        }
        return getModel().getHeadMessageStyle();
    }

    public static class Model {

        private Date shutdownTime;
        private String headMessageText;
        private String headMessageStyle;

        public String getHeadMessageText() {
            return headMessageText;
        }

        public void setHeadMessageText(String headMessageText) {
            this.headMessageText = headMessageText;
        }

        public String getHeadMessageStyle() {
            return headMessageStyle;
        }

        public void setHeadMessageStyle(String headMessageStyle) {
            this.headMessageStyle = headMessageStyle;
        }

        public Date getShutdownTime() {
            return shutdownTime;
        }

        public void setShutdownTime(Date shutdownTime) {
            this.shutdownTime = shutdownTime;
        }

    }
}
