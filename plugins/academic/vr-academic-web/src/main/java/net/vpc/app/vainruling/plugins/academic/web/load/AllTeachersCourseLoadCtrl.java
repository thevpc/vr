/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherStat;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.StatCache;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherSemesterStat;
import net.vpc.app.vainruling.plugins.academic.web.admin.AcademicAdminToolsCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Charge Enseignants",
        url = "modules/academic/allteacherscourseload",
        menu = "/Education/Load",
        securityKey = "Custom.Education.AllTeachersCourseLoad"
)
@ManagedBean
public class AllTeachersCourseLoadCtrl {

    protected Model model = new Model();

    private void reset() {
        getModel().setSemester1(new ArrayList<TeacherSemesterStat>());
        getModel().setSemester2(new ArrayList<TeacherSemesterStat>());
        getModel().setYear(new ArrayList<TeacherStat>());
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();
    }

    public void onRefresh() {
        final CorePlugin cp = VrApp.getBean(CorePlugin.class);
        String version = UPA.getContext().invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                return (String) cp.getOrCreateAppPropertyValue("AcademicPlugin.import.version", null, "v01");
            }
        });
        getModel().setVersion(version);
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        StatCache cache = new StatCache();
        boolean includeIntents = containsRefreshFilter("intents");
        getModel().setSemester1(a.evalTeacherSemesterStatList(null, "S1", includeIntents, cache));
        getModel().setSemester2(a.evalTeacherSemesterStatList(null, "S2", includeIntents, cache));
        getModel().setYear(a.evalTeacherStatList(null, null, includeIntents, cache));
    }

    public Model getModel() {
        return model;
    }

    public void onOthersFiltersChanged() {
        onRefresh();
    }

    public static class Model {

        List<TeacherSemesterStat> semester1 = new ArrayList<>();
        List<TeacherSemesterStat> semester2 = new ArrayList<>();
        List<TeacherStat> year = new ArrayList<>();
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "c", "td", "tp", "pm"};
        String[] filters = defaultFilters;
        String[] refreshFilter = {};
        String version;

        public String[] getFilters() {
            return filters;
        }

        public String[] getDefaultFilters() {
            return defaultFilters;
        }

        public void setDefaultFilters(String[] defaultFilters) {
            this.defaultFilters = defaultFilters;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String[] getRefreshFilter() {
            return refreshFilter;
        }

        public void setRefreshFilter(String[] refreshFilter) {
            this.refreshFilter = refreshFilter;
        }

        public void setFilters(String[] filters) {
            this.filters = (filters == null || filters.length == 0) ? defaultFilters : filters;
        }

        public List<TeacherSemesterStat> getSemester1() {
            return semester1;
        }

        public void setSemester1(List<TeacherSemesterStat> semester1) {
            this.semester1 = semester1;
        }

        public List<TeacherSemesterStat> getSemester2() {
            return semester2;
        }

        public void setSemester2(List<TeacherSemesterStat> semester2) {
            this.semester2 = semester2;
        }

        public List<TeacherStat> getYear() {
            return year;
        }

        public void setYear(List<TeacherStat> year) {
            this.year = year;
        }

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

    public void onFiltersChanged() {
        //onRefresh();
    }

    public void onRefreshFiltersChanged() {
        onRefresh();
    }

    public void generateTeachingLoad() {
        try {
            final CorePlugin cp = VrApp.getBean(CorePlugin.class);
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            UPA.getContext().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    cp.setAppProperty("AcademicPlugin.import.version", null, getModel().getVersion());
                }
            });
            p.generateTeachingLoad(getModel().getVersion());
            FacesUtils.addInfoMessage("Successful Operation");
        } catch (Exception ex) {
            Logger.getLogger(AcademicAdminToolsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex.getMessage());
        }

    }
}
