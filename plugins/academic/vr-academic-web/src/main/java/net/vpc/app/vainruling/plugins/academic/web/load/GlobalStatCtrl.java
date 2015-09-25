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

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Charge Globale",
        url = "modules/academic/globalstat",
        menu = "/Education/Load",
        securityKey = "Custom.Education.GlobalStat"
)
@ManagedBean
public class GlobalStatCtrl {

    private Model model = new Model();

    public class Model {

        GlobalStat stat = new GlobalStat();
        List<AcademicSemester> semesters = new ArrayList<>();
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "c", "td", "tp", "pm"};
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
