package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrPathItem;
import net.vpc.app.vainruling.VrCompletionInfo;
import net.vpc.app.vainruling.VrOnPageLoad;

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

    @VrOnPageLoad
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
        private List<VrCompletionInfo> list=new ArrayList<>();

        public List<VrCompletionInfo> getList() {
            return list;
        }

        public void setList(List<VrCompletionInfo> list) {
            this.list = list;
        }
    }
}
