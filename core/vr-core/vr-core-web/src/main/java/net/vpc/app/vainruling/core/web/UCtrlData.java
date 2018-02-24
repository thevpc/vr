/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web;

import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;

/**
 * @author taha.bensalah@gmail.com
 */
public class UCtrlData {

    private String title;
    private String subTitle;

    private String url;
    private String securityKey;

    private String css;
    private ActionEnabler enabler;

    private BreadcrumbItem[] breadcrumb;
    private int priority;

    public UCtrlData() {
    }

    public UCtrlData(String title, String subTitle,String url, String css, String securityKey, BreadcrumbItem... breadcrumb) {
        this.title = title;
        this.subTitle = subTitle;
        this.url = url;
        this.css = css;
        this.securityKey = securityKey;
        this.breadcrumb = breadcrumb;
    }

    public BreadcrumbItem[] getBreadcrumb() {
        return breadcrumb;
    }

    public UCtrlData setBreadcrumb(BreadcrumbItem[] breadcrumb) {
        this.breadcrumb = breadcrumb;
        return this;
    }

    public String getCss() {
        return css;
    }

    public UCtrlData setCss(String css) {
        this.css = css;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public UCtrlData setTitle(String title) {
        this.title = title;
        return this;
    }

    public UCtrlData setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public UCtrlData setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public ActionEnabler getEnabler() {
        return enabler;
    }

    public UCtrlData setEnabler(ActionEnabler enabler) {
        this.enabler = enabler;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
