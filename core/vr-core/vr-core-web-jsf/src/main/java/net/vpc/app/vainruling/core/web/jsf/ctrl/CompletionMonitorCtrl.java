package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.util.CompletionInfo;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;

@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = ""),
                @VrPathItem(title = "APP", css = "fa-dashboard", ctrl = ""),
        },
//        css = "fa-table",
//        title = "Projets APP",
        url = "modules/completion-monitor",
        menu = "/Desktop",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_COMPLETION_MONITOR
)public class CompletionMonitorCtrl {
    private Model model=new Model();
    @Autowired
    private CorePlugin core;

    @OnPageLoad
    public void init(){
        refresh();
    }

    public void refresh(){
        model.setList(core.findAllCompletions(core.getCurrentUserId(), null, null, null, Level.WARNING));
    }

    public Model getModel() {
        return model;
    }

    public static class Model{
        private List<CompletionInfo> list=new ArrayList<>();

        public List<CompletionInfo> getList() {
            return list;
        }

        public void setList(List<CompletionInfo> list) {
            this.list = list;
        }
    }
}
