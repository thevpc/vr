/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrPageInfo implements VrActionInfo {

    private String controllerName;
    private String title;
    private String subTitle;

    private String url;
    private String menuPath;
    private String source;
    private String securityKey;

    private String css;
    private VrActionEnabler enabler;

    private VrBreadcrumbItem[] breadcrumb;
    private int priority;
    private String cmd;
    private boolean acceptAnonymous;

    public VrPageInfo() {
    }

    public VrPageInfo(String controllerName,String cmd,String title, String subTitle, String menuPath, String url, String css, String securityKey, VrBreadcrumbItem... breadcrumb) {
        this.title = title;
        this.subTitle = subTitle;
        this.menuPath = menuPath;
        this.url = url;
        this.css = css;
        this.securityKey = securityKey;
        this.breadcrumb = breadcrumb;
        this.controllerName = controllerName;
        this.cmd = cmd;
    }

    public boolean isAcceptAnonymous() {
        return acceptAnonymous;
    }

    public void setAcceptAnonymous(boolean acceptAnonymous) {
        this.acceptAnonymous = acceptAnonymous;
    }

     
    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }
    

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMenuPath() {
        return menuPath;
    }

    public void setMenuPath(String menuPath) {
        this.menuPath = menuPath;
    }

    public VrBreadcrumbItem[] getBreadcrumb() {
        return breadcrumb;
    }

    public VrPageInfo setBreadcrumb(VrBreadcrumbItem[] breadcrumb) {
        this.breadcrumb = breadcrumb;
        return this;
    }

    public String getCss() {
        return css;
    }

    public VrPageInfo setCss(String css) {
        this.css = css;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public VrPageInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public VrPageInfo setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public VrPageInfo setUrl(String url) {
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

    public VrActionEnabler getEnabler() {
        return enabler;
    }

    public VrPageInfo setEnabler(VrActionEnabler enabler) {
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
