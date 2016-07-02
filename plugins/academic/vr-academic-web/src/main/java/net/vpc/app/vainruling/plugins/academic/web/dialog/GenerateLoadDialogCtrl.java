/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseFilter;
import net.vpc.app.vainruling.plugins.academic.web.admin.AcademicAdminToolsCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vpc
 */
@Component
@ManagedBean
@Scope(value = "session")
public class GenerateLoadDialogCtrl {

    @Autowired
    private AcademicPlugin acad;

    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrHelper.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        getModel().setConfig(config);
        initContent();

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/academic/dialog/generateload", options, null);

    }

    String getVersion(int periodId){
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        final AppPeriod period = core.findPeriod(periodId);
        return UPA.getContext().invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                return (String) VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPlugin.generate."+period.getName()+".version", null, "v01");
            }
        });
    }

    void setVersion(int periodId,final String value){
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        final AppPeriod period = core.findPeriod(periodId);
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                VrApp.getBean(CorePlugin.class).setAppProperty("AcademicPlugin.generate."+period.getName()+".version", null, value);
            }
        });
    }

    private void initContent() {
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
            getModel().setConfig(c);
        }
        String title = c.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = "Generer Charge";
        }
        getModel().setTitle(title);

        getModel().getPeriods().clear();
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AppPeriod curr= core.findAppConfig().getMainPeriod();
        for (AppPeriod period : core.findNavigatablePeriods()) {
            SelectItem item = new SelectItem(String.valueOf(period.getId()), period.getName());
            if(!period.isReadOnly()){
                getModel().getPeriods().add(item);
            }
        }
        getModel().setPeriod(String.valueOf(curr.getId()));

    }

    public void onChangePeriod(){
        int periodId = getPeriodId();
        getModel().setVersion(getVersion(periodId));
    }

    public int getPeriodId(){
        String p = getModel().getPeriod();
        if(StringUtils.isEmpty(p)){
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            return core.findAppConfig().getMainPeriod().getId();
        }
        return Integer.parseInt(p);
    }


    public void onGenerate() {
        try {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            int periodId = getPeriodId();
            setVersion(periodId, getModel().getVersion());
            p.generateTeachingLoad(periodId, new CourseFilter(), getModel().getVersion());
            FacesUtils.addInfoMessage("Successful Operation");
        } catch (Exception ex) {
            Logger.getLogger(AcademicAdminToolsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex.getMessage());
        }
        fireEventExtraDialogClosed();
    }

    public Model getModel() {
        return model;
    }

    public void fireEventExtraDialogClosed() {
        //Object obj
        RequestContext.getCurrentInstance().closeDialog(new DialogResult(getModel().getVersion(), getModel().getVersion()));
    }


    public static class Config {

        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class Model {

        private String title = "";
        private Config config = new Config();
        private String version = "";
        private List<SelectItem> periods = new ArrayList<>();
        private String period;

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }
    }
}
