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
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        getModel().setFilter("");

        String t = config.getTitle();

        getModel().setPeriodName(core.getCurrentPeriod().getName());
        getModel().setTitle((StringUtils.isEmpty(t) ? "Générer Feedbacks" : t)+" - "+getModel().getPeriodName());
        getModel().setModelId(config.getModelId());

        getModel().setSemesters(new ArrayList<>());
//        getModel().getSemesters().clear();
        for (AcademicSemester item : academic.findSemesters()) {
            getModel().getSemesters().add(FacesUtils.createSelectItem(String.valueOf(item.getId()), item.getName(),null));
        }
        getModel().setSelectedSemester(null);

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
        int semesterId= Convert.toInt(getModel().getSelectedSemester(), IntegerParserConfig.LENIENT_F);
        VrApp.getBean(AcademicPerfEvalPlugin.class).generateStudentsFeedbackForm(
                getModel().getModelId(),
                VrApp.getBean(CorePlugin.class).getCurrentPeriod().getId(),
                semesterId,
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
        private String periodName;
        private String selectedSemester;
        private List<SelectItem> semesters=new ArrayList<>();

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

        public String getPeriodName() {
            return periodName;
        }

        public void setPeriodName(String periodName) {
            this.periodName = periodName;
        }

        public String getSelectedSemester() {
            return selectedSemester;
        }

        public void setSelectedSemester(String selectedSemester) {
            this.selectedSemester = selectedSemester;
        }

        public List<SelectItem> getSemesters() {
            return semesters;
        }

        public void setSemesters(List<SelectItem> semesters) {
            this.semesters = semesters;
        }
    }

}
