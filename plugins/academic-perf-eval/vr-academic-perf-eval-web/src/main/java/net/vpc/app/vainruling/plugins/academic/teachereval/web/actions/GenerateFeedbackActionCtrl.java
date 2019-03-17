/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.teachereval.web.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.AcademicPerfEvalPlugin;
import net.vpc.app.vainruling.plugins.academic.perfeval.service.model.AcademicFeedbackModel;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.upa.UPA;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController
public class GenerateFeedbackActionCtrl {

    private static final Logger log = Logger.getLogger(GenerateFeedbackActionCtrl.class.getName());
    @Autowired
    private CorePlugin core;
    @Autowired
    private AcademicPlugin academic;
    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        if (config == null) {
            config = new Config();
        }
        String t = config.getTitle();
        getModel().setPeriodName(core.getCurrentPeriod().getName());
        getModel().setTitle((StringUtils.isEmpty(t) ? "Générer Feedbacks" : t) + " - " + getModel().getPeriodName());
        getModel().setSelectedSession(String.valueOf(config.getSessionId()));

        new DialogBuilder("/modules/academic/perfeval/generate-feedback-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
    }

    public void onUpdate() {
//        System.out.println("on update SendExternalMailActionCtrl");
    }

    public void onChange() {

    }

    public void fireEventExtraDialogApply() {
        int sessionId = Convert.toInt(getModel().getSelectedSession(), IntegerParserConfig.LENIENT_F);
        VrApp.getBean(AcademicPerfEvalPlugin.class).generateStudentsFeedbackForm(sessionId);
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
        private int sessionId;
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

        public int getSessionId() {
            return sessionId;
        }

        public void setSessionId(int sessionId) {
            this.sessionId = sessionId;
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
        private String periodName;
        private String selectedSession;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPeriodName() {
            return periodName;
        }

        public void setPeriodName(String periodName) {
            this.periodName = periodName;
        }

        public String getSelectedSession() {
            return selectedSession;
        }

        public void setSelectedSession(String selectedSemester) {
            this.selectedSession = selectedSemester;
        }
    }
}
