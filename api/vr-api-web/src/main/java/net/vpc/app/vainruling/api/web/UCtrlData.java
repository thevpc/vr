/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web;

/**
 *
 * @author vpc
 */
public class UCtrlData {

    private String title;

    private String url;

    private String css;

    private BreadcrumbItem[] breadcrumb;

    public UCtrlData() {
    }

    public UCtrlData(String title, String url, String css, BreadcrumbItem ... breadcrumb) {
        this.title = title;
        this.url = url;
        this.css = css;
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

    public String getUrl() {
        return url;
    }

    public UCtrlData setUrl(String url) {
        this.url = url;
        return this;
    }

}
