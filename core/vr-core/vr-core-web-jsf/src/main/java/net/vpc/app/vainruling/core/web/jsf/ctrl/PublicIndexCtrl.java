/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import net.vpc.app.vainruling.core.service.pages.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        url = "index"
)
@Scope(value = "singleton")
public class PublicIndexCtrl {
    @Autowired
    private CorePlugin core;
    @OnPageLoad
    public void onLoad(String cmd) {
        Config config = VrUtils.parseJSONObject(cmd, Config.class);
        if(config!=null && StringUtils.isBlank(config.filter)) {
            core.getCurrentSession().setSelectedSiteFilter(config.filter);
        }
        Vr.get().setCurrentPageId("welcome");
//        VrApp.getBean(VrMenuManager.class).setPageCtrl("index");
    }

    public static class Config{
        private String filter;

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }
    }
}
