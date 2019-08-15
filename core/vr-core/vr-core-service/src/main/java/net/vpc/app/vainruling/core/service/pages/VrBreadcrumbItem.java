/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.pages;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrBreadcrumbItem {

    private boolean active;
    private String title;
    private String subTitle;
    private String css;
    private String ctrl;
    private String cmd;

    public VrBreadcrumbItem(String title, String subTitle, String className, String ctrl, String cmd) {
        this.title = title;
        this.subTitle = subTitle;
        this.css = className;
        this.ctrl = ctrl == null ? "" : ctrl;
        this.cmd = cmd == null ? "" : cmd;
        this.active = false;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getCtrl() {
        return ctrl;
    }

    public void setCtrl(String ctrl) {
        this.ctrl = ctrl;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
