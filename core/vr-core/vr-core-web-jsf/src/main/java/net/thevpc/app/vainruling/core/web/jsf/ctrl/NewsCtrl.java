/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.common.strings.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
//        title = "News",
        url = "#{vr.publicThemeRelativePath}/news",
        acceptAnonymous = true
)
@Scope(value = "singleton")
@Controller
public class NewsCtrl {

    @VrOnPageLoad
    public void onLoad(String cmd) {
        Vr bean = Vr.get();
        bean.setCurrentPageId("news");
//        bean.setPageCtrl("news");
        NewsCtrlCmd newsCtrlCmd = VrUtils.parseJSONObject(cmd, NewsCtrlCmd.class);
        if(newsCtrlCmd!=null) {
            //TODO should check for article access!!
            if(newsCtrlCmd.getId()>0) {
                Vr.get().getCmsTextService().setSelectedContentTextById(newsCtrlCmd.getDisposition(),newsCtrlCmd.getId());
            }else{
                Vr.get().getCmsTextService().setContentDisposition(StringUtils.isBlank(newsCtrlCmd.getDisposition())?"News":newsCtrlCmd.getDisposition());
            }
        }else{
            Vr.get().getCmsTextService().setContentDisposition("News");
            Vr.get().getCmsTextService().setSelectedContentTextById("News",-1);
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
