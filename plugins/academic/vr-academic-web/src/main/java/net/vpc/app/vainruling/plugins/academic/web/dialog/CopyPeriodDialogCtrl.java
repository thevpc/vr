/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.web.admin.AcademicAdminToolsCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
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

/**
 * @author taha.bensalah@gmail.com
 */
@Component
@Scope(value = "session")
public class CopyPeriodDialogCtrl {

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

        RequestContext.getCurrentInstance().openDialog("/modules/academic/dialog/copy-period-dialog", options, null);

    }

    private void initContent() {
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
            getModel().setConfig(c);
        }
        String title = c.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = "Copier Periode";
        }
        getModel().setTitle(title);
        getModel().setFromPeriods(new ArrayList<SelectItem>());
        getModel().setToPeriods(new ArrayList<SelectItem>());
        for (AppPeriod period : VrApp.getBean(CorePlugin.class).findValidPeriods()) {
            SelectItem item = new SelectItem(String.valueOf(period.getId()), period.getName());
            if (!period.isReadOnly()) {
                getModel().getToPeriods().add(item);
            }
            getModel().getFromPeriods().add(item);
        }
    }

    public void onApply() {
        try {
            final CorePlugin cp = VrApp.getBean(CorePlugin.class);
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            int p1 = Convert.toInt(getModel().getFromPeriod(), IntegerParserConfig.LENIENT_F);
            int p2 = Convert.toInt(getModel().getToPeriod(), IntegerParserConfig.LENIENT_F);
            if (p1 > 0 && p2 > 0 && p1 != p2) {
                p.copyAcademicData(p1, p2);
            }
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
        RequestContext.getCurrentInstance().closeDialog(new DialogResult("ok", "ok"));
    }


    public static class Config {

        private String title;
        private String version;

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
    }

    public static class Model {

        private String title = "";
        private Config config = new Config();
        private String fromPeriod = "";
        private String toPeriod = "";
        private List<SelectItem> fromPeriods = new ArrayList<>();
        private List<SelectItem> toPeriods = new ArrayList<>();

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

        public String getFromPeriod() {
            return fromPeriod;
        }

        public void setFromPeriod(String fromPeriod) {
            this.fromPeriod = fromPeriod;
        }

        public String getToPeriod() {
            return toPeriod;
        }

        public void setToPeriod(String toPeriod) {
            this.toPeriod = toPeriod;
        }

        public List<SelectItem> getFromPeriods() {
            return fromPeriods;
        }

        public void setFromPeriods(List<SelectItem> fromPeriods) {
            this.fromPeriods = fromPeriods;
        }

        public List<SelectItem> getToPeriods() {
            return toPeriods;
        }

        public void setToPeriods(List<SelectItem> toPeriods) {
            this.toPeriods = toPeriods;
        }
    }
}
