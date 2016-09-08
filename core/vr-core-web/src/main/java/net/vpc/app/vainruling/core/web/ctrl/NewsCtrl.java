/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import org.springframework.context.annotation.Scope;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        title = "News",
        url = "news"
)
@Scope(value = "singleton")
public class NewsCtrl {

    @OnPageLoad
    public void onLoad(String cmd) {
        VrMenuManager bean = VrApp.getBean(VrMenuManager.class);
        bean.getModel().setCurrentPageId("news");
        bean.setPageCtrl("news");
        NewsCtrlCmd newsCtrlCmd = VrHelper.parseJSONObject(cmd, NewsCtrlCmd.class);
        if(newsCtrlCmd!=null) {
            //TODO should check for article access!!
            Vr.get().setSelectedArticle(newsCtrlCmd.getId());
        }else{
            Vr.get().setSelectedArticle(-1);

        }
    }

    public static class NewsCtrlCmd{
        private int id=-1;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
