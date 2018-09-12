/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import net.vpc.common.strings.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
//        title = "News",
        url = "#{vr.publicThemeRelativePath}/news",
        acceptAnonymous = true
)
@Scope(value = "singleton")
@Controller
public class NewsCtrl {

    @OnPageLoad
    public void onLoad(String cmd) {
        VrMenuManager bean = VrApp.getBean(VrMenuManager.class);
        bean.getModel().setCurrentPageId("news");
//        bean.setPageCtrl("news");
        NewsCtrlCmd newsCtrlCmd = VrUtils.parseJSONObject(cmd, NewsCtrlCmd.class);
        if(newsCtrlCmd!=null) {
            //TODO should check for article access!!
            if(newsCtrlCmd.getId()>0) {
                Vr.get().getCmsTextService().setSelectedContentTextById(newsCtrlCmd.getId());
            }else{
                Vr.get().getCmsTextService().setContentDisposition(StringUtils.isEmpty(newsCtrlCmd.getDisposition())?"News":newsCtrlCmd.getDisposition());
            }
        }else{
            Vr.get().getCmsTextService().setContentDisposition("News");
            Vr.get().getCmsTextService().setSelectedContentTextById(-1);
        }
    }

    public static class NewsCtrlCmd{
        private int id=-1;
        private String disposition;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDisposition() {
            return disposition;
        }

        public void setDisposition(String disposition) {
            this.disposition = disposition;
        }
    }
}
