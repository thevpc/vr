/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.GlobalStat;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Stats Charge",
        url = "modules/academic/globalstat",
        menu = "/Education/Load",
        securityKey = "Custom.Education.GlobalStat"
)
@ManagedBean
public class GlobalStatCtrl {

    private Model model = new Model();

    public class Model {

        PieChartModel chart1;
        PieChartModel chart2;
        GlobalStat stat = new GlobalStat();
        List<AcademicSemester> semesters = new ArrayList<>();
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "C", "TD", "TP", "PM"};
        String[] filters = defaultFilters;
        String[] refreshFilter = {};

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
        

    }

    public Model getModel() {
        return model;
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

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setStat(p.evalGlobalStat(containsRefreshFilter("intents"), null));
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
        
        getModel().chart1=pieModel1;
        getModel().chart2=pieModel2;
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();

    }

    public void onFiltersChanged() {
        //onRefresh();
    }

    public void onRefreshFiltersChanged() {
        onRefresh();
    }
}
