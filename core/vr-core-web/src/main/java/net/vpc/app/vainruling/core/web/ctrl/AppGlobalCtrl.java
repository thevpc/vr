/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import java.util.Calendar;
import java.util.Date;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.web.ctrl.BasePageCtrl;
import net.vpc.common.utils.Chronometer;

/**
 *
 * @author vpc
 */
@ManagedBean
@ApplicationScoped
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
    }

    public void doCancelShutdown() {
        getModel().setShutdownTime(null);
        getModel().setHeadMessageStyle(null);
        getModel().setHeadMessageText(null);
    }

    public boolean isShutdown() {
        return getModel().getShutdownTime() != null;
    }

    public String getHeadMessageText() {
        if (isShutdown()) {
            long p = getModel().getShutdownTime().getTime()-System.currentTimeMillis();
            if (p <= 60000) {
                return "Arrêt imminent...";
            }
            return "Arrêt dans " + Chronometer.formatPeriod(p, Chronometer.DatePart.m);
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
