///*
// * To change this license header, choose License Headers in Project Properties.
// *
// * and open the template in the editor.
// */
//package net.thevpc.app.vainruling.plugins.devtoolbox.web;
//
//import net.thevpc.app.vainruling.core.service.VrApp;
//import net.thevpc.app.vainruling.core.web.UCtrl;
//import net.thevpc.app.vainruling.plugins.devtoolbox.service.DevSrv;
//import net.thevpc.common.strings.StringUtils;
//import org.springframework.context.annotation.Scope;
//
///**
// * @author taha.bensalah@gmail.com
// */
//@UCtrl(
//        title = "Developer Upgrade",
//        css = "fa-dashboard", url = "modules/devtoolbox/dev-upgrade",
//        menu = "/Admin", securityKey = "Custom.DevTools"
//)
//@Scope(value = "session")
//public class DevCtrl {
//
//    private String message;
//
//    public void doUpgrade() {
//        try {
//            VrApp.getBean(DevSrv.class).doUpgrade();
//            setMessage("Successful upgrade");
//        } catch (Exception e) {
//            setMessage(StringUtils.verboseStacktraceToString(e));
//        }
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//}
