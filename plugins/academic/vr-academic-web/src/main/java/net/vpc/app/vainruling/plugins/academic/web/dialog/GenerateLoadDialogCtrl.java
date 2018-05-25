/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.util.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.web.admin.AcademicAdminToolsCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.mon.AbstractProgressMonitor;
import net.vpc.common.util.mon.ProgressMessage;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import org.primefaces.PrimeFaces;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
@Scope(value = "session")
public class GenerateLoadDialogCtrl {

    @Autowired
    private AcademicPlugin acad;

    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        getModel().setConfig(config);
        initContent();

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);

        PrimeFaces.current().dialog().openDynamic("/modules/academic/dialog/generate-load-dialog", options, null);

    }

    String getVersion(int periodId) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        final AppPeriod period = core.findPeriod(periodId);
        return UPA.getPersistenceUnit().invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                return (String) VrApp.getBean(CorePlugin.class).getOrCreateAppPropertyValue("AcademicPlugin.generate." + period.getName() + ".version", null, "v01");
            }
        });
    }

    void setVersion(int periodId, final String value) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        final AppPeriod period = core.findPeriod(periodId);
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                VrApp.getBean(CorePlugin.class).setAppProperty("AcademicPlugin.generate." + period.getName() + ".version", null, value);
            }
        });
    }

    private void initContent() {
        getModel().setGenerationProgress(0);
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

        getModel().getPeriodItems().clear();
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AppPeriod curr = core.getCurrentPeriod();
        for (AppPeriod period : core.findNavigatablePeriods()) {
            SelectItem item = FacesUtils.createSelectItem(String.valueOf(period.getId()), period.getName());
            //if (!period.isReadOnly()) {
                getModel().getPeriodItems().add(item);
            //}
        }
        getModel().setPeriod(String.valueOf(curr.getId()));

    }

    public void onChangePeriod() {
        int periodId = getPeriodId();
        getModel().setVersion(getVersion(periodId));
    }

    public int getPeriodId() {
        String p = getModel().getPeriod();
        if (StringUtils.isEmpty(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig a = core.getCurrentConfig();
            if(a==null || a.getMainPeriod()==null){
                return -1;
            }
            return a.getMainPeriod().getId();
        }
        return Convert.toInt(p,IntegerParserConfig.LENIENT_F);
    }


    public void onGenerate() {
        UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
                    int periodId = getPeriodId();
                    String version = getModel().getVersion();
                    if(StringUtils.isEmpty(version)){
                        version="temp-version";
                    }
                    setVersion(periodId, version);
                    getModel().setGenerationProgress(0);
                    p.generateTeachingLoad(periodId, CourseAssignmentFilter.NO_INTENTS, version, getModel().getOldVersion(), new AbstractProgressMonitor() {
                        @Override
                        protected void onProgress(double progress, ProgressMessage message) {
                            getModel().setGenerationProgress(progress*100);
                        }
                    });
                    FacesUtils.addInfoMessage("Successful Operation");
                } catch (Exception ex) {
                    Logger.getLogger(AcademicAdminToolsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                    FacesUtils.addErrorMessage(ex.getMessage());
                }
                fireEventExtraDialogClosed();
            }
        });
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
        private String oldVersion = "";
        private List<SelectItem> periodItems = new ArrayList<>();
        private String period;
        private double generationProgress;

        public double getGenerationProgress() {
            return generationProgress;
        }

        public void setGenerationProgress(double generationProgress) {
            this.generationProgress = generationProgress;
        }

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

        public String getOldVersion() {
            return oldVersion;
        }

        public void setOldVersion(String oldVersion) {
            this.oldVersion = oldVersion;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<SelectItem> getPeriodItems() {
            return periodItems;
        }

        public void setPeriodItems(List<SelectItem> periodItems) {
            this.periodItems = periodItems;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }
    }
}
