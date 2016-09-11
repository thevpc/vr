/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.common.strings.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl
public class GenerateFeedbackActionCtrl {

    private static final Logger log = Logger.getLogger(GenerateFeedbackActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        if (config == null) {
            config = new Config();
        }
        getModel().setFilter("");

        String t = config.getTitle();

        getModel().setTitle(StringUtils.isEmpty(t) ? "Générer Feedbacks" : t);
        getModel().setModelId(config.getModelId());

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/academic/perfeval/generate-feedback-dialog", options, null);

    }

    public void onUpdate() {
//        System.out.println("on update SendExternalMailActionCtrl");
    }

    public void onChange() {

    }

    public void fireEventExtraDialogApply() {
        VrApp.getBean(AcademicPerfEvalPlugin.class).generateStudentsFeedbackForm(
                getModel().getModelId(),
                getModel().getFilter()
        );
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void fireEventExtraDialogCancel() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public Model getModel() {
        return model;
    }

    public static class Config {

        private String type;
        private int modelId;
        private String title;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getModelId() {
            return modelId;
        }

        public void setModelId(int modelId) {
            this.modelId = modelId;
        }

    }

    public static class Model {

        private String title;
        private String filter;
        private int modelId;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public int getModelId() {
            return modelId;
        }

        public void setModelId(int modelId) {
            this.modelId = modelId;
        }

    }

}
