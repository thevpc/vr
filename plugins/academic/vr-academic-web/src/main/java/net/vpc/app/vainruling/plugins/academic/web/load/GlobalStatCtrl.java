/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgramType;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.GlobalStat;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import org.primefaces.model.chart.PieChartModel;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Stats Charge",
        url = "modules/academic/global-stat",
        menu = "/Education/Load",
        securityKey = "Custom.Education.GlobalStat"
)
public class GlobalStatCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public int getPeriodId() {
        String p = getModel().getSelectedPeriod();
        if (StringUtils.isEmpty(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig appConfig = core.findAppConfig();
            if(appConfig!=null && appConfig.getMainPeriod()!=null) {
                return appConfig.getMainPeriod().getId();
            }
            return -1;
        }
        return Integer.parseInt(p);
    }

    public boolean containsFilter(String s) {
        String[] f = getModel().getFilters();
        if (f == null || f.length == 0) {
            return "value".equals(s);
        }
        return Arrays.asList(f).indexOf(s) >= 0;
    }

    public boolean containsRefreshFilter(String s) {
        String[] f = getModel().getRefreshFilter();
        return Arrays.asList(f).indexOf(s) >= 0;
    }

    public void onChangePeriod() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        List<SelectItem> refreshableFilers = new ArrayList<>();
        refreshableFilers.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
        for (AcademicProgramType pt : a.findProgramTypes()) {
            refreshableFilers.add(FacesUtils.createSelectItem("AcademicProgramType:" + pt.getId(), pt.getName(), "vr-checkbox"));
        }
        int periodId = getPeriodId();
        if(periodId>=-1) {
            for (String label : a.findCoursePlanLabels(periodId)) {
                refreshableFilers.add(FacesUtils.createSelectItem("label:" + label, label, "vr-checkbox"));
                refreshableFilers.add(FacesUtils.createSelectItem("label:!" + label, "!" + label, "vr-checkbox"));
            }
        }
        getModel().setRefreshFilterItems(refreshableFilers);
        onRefresh();
    }

    public CourseFilter getCourseFilter() {
        HashSet<String> labels = new HashSet<>(Arrays.asList(getModel().getRefreshFilter()));
        CourseFilter c = CourseFilter.build(labels);
        getModel().setRefreshFilter(labels.toArray(new String[labels.size()]));
        return c;
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        CorePlugin c = VrApp.getBean(CorePlugin.class);

        int periodId = getPeriodId();

        GlobalStat stat = periodId<=0?null:p.evalGlobalStat(periodId, getCourseFilter(), null);
        if(stat==null){
            stat=new GlobalStat();
        }
        getModel().setStat(stat);
        getModel().setSemesters(p.findSemesters());

        PieChartModel pieModel1 = new PieChartModel();

        pieModel1.set("Permanents", getModel().getStat().getTeachersPermanentCount());
        pieModel1.set("Contactuels", getModel().getStat().getTeachersContractualCount());
        pieModel1.set("Vacataires", getModel().getStat().getTeachersTemporaryCount());
        pieModel1.set("Autres", getModel().getStat().getTeachersOtherCount());
        pieModel1.setTitle("Repartition Enseignants");
        pieModel1.setLegendPosition("s");
        pieModel1.setShowDataLabels(true);

        PieChartModel pieModel2 = new PieChartModel();

        pieModel2.set("Permanents", getModel().getStat().getTeachersPermanentCount());
        pieModel2.set("Assistants pour combler Charges Supplémentaires hors du", getModel().getStat().getNeededAbsolute().getTeachersCount());
        pieModel2.set("Assistants pour combler Charges Supplémentaires", getModel().getStat().getNeededRelative().getTeachersCount());
        pieModel2.set("Enseignants restant à recruter", getModel().getStat().getMissing().getTeachersCount());
        pieModel2.setTitle("Manque Enseignants");
        pieModel2.setLegendPosition("s");
        pieModel2.setShowDataLabels(true);

        getModel().chart1 = pieModel1;
        getModel().chart2 = pieModel2;
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        List<AppPeriod> navigatablePeriods = core.findNavigatablePeriods();
        AppPeriod mainPeriod = core.findAppConfig().getMainPeriod();
        getModel().setSelectedPeriod(null);
        getModel().getPeriods().clear();
        for (AppPeriod p : navigatablePeriods) {
            getModel().getPeriods().add(new SelectItem(String.valueOf(p.getId()), p.getName()));
            if (mainPeriod != null && p.getId() == mainPeriod.getId()) {
                getModel().setSelectedPeriod(String.valueOf(p.getId()));
            }
        }
        onChangePeriod();
    }

    public void onFiltersChanged() {
        //onRefresh();
    }

    public void onRefreshFiltersChanged() {
        onRefresh();
    }

    public class Model {

        List<SelectItem> refreshFilterItems;
        PieChartModel chart1;
        PieChartModel chart2;
        GlobalStat stat = new GlobalStat();
        List<AcademicSemester> semesters = new ArrayList<>();
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "C", "TD", "TP", "PM"};
        String[] filters = defaultFilters;
        String[] refreshFilter = {};
        List<SelectItem> periods = new ArrayList<>();
        String selectedPeriod = null;

        public GlobalStat getStat() {
            return stat;
        }

        public void setStat(GlobalStat stat) {
            this.stat = stat;
        }

        public List<AcademicSemester> getSemesters() {
            return semesters;
        }

        public void setSemesters(List<AcademicSemester> semesters) {
            this.semesters = semesters;
        }

        public String[] getFilters() {
            return filters;
        }

        public void setFilters(String[] filters) {
            this.filters = filters;
            this.filters = (filters == null || filters.length == 0) ? defaultFilters : filters;
        }

        public String[] getRefreshFilter() {
            return refreshFilter;
        }

        public void setRefreshFilter(String[] refreshFilter) {
            this.refreshFilter = refreshFilter;
        }

        public PieChartModel getChart1() {
            return chart1;
        }

        public void setChart1(PieChartModel chart1) {
            this.chart1 = chart1;
        }

        public PieChartModel getChart2() {
            return chart2;
        }

        public void setChart2(PieChartModel chart2) {
            this.chart2 = chart2;
        }

        public List<SelectItem> getRefreshFilterItems() {
            return refreshFilterItems;
        }

        public void setRefreshFilterItems(List<SelectItem> refreshFilterItems) {
            this.refreshFilterItems = refreshFilterItems;
        }

        public List<SelectItem> getPeriods() {
            return periods;
        }

        public void setPeriods(List<SelectItem> periods) {
            this.periods = periods;
        }

        public String getSelectedPeriod() {
            return selectedPeriod;
        }

        public void setSelectedPeriod(String selectedPeriod) {
            this.selectedPeriod = selectedPeriod;
        }
    }
}
