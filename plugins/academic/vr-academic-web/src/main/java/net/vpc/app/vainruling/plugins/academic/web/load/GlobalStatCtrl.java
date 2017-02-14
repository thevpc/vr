/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.StatCache;
import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.stat.DeviationConfig;
import net.vpc.app.vainruling.plugins.academic.service.stat.GlobalAssignmentStat;
import net.vpc.app.vainruling.plugins.academic.service.stat.GlobalStat;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultTeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.util.TeacherFilterFactory;
import net.vpc.common.jsf.FacesUtils;
import org.primefaces.model.chart.PieChartModel;

import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Stats Charge",
        url = "modules/academic/global-stat",
        menu = "/Education/Load",
        securityKey = "Custom.Education.GlobalStat"
)
public class GlobalStatCtrl {

    private Model model = new Model();
    protected TeacherLoadFilterComponent teacherFilter = new TeacherLoadFilterComponent();
    protected CourseLoadFilterComponent courseFilter = new CourseLoadFilterComponent();

    public Model getModel() {
        return model;
    }

    public TeacherLoadFilterComponent getTeacherFilter() {
        return teacherFilter;
    }

    public CourseLoadFilterComponent getCourseFilter() {
        return courseFilter;
    }

    public void onChangePeriod() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        getTeacherFilter().onChangePeriod();
        getCourseFilter().onChangePeriod();
        getCourseFilter().getModel().getRefreshFilterItems().add(FacesUtils.createSelectItem("x:percent", "Pourcentages", "vr-checkbox"));
        onRefresh();
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        CorePlugin c = VrApp.getBean(CorePlugin.class);

        int periodId = getTeacherFilter().getPeriodId();

        StatCache cache = new StatCache();
        TeacherFilter teacherFilter = getTeacherFilter().getTeacherFilter();
        DeviationConfig deviationConfig = getCourseFilter().getDeviationConfig();
        CourseAssignmentFilter courseAssignmentFilter = getCourseFilter().getCourseAssignmentFilter();
        GlobalStat allTeachers = p.evalGlobalStat(periodId <= 0 ? -100 : periodId,
                teacherFilter, courseAssignmentFilter,
                deviationConfig);
        getModel().setStat(allTeachers);
        List<GlobalStatByDiscipline> globalStatByDisciplines = new ArrayList<>();
        globalStatByDisciplines.add(new GlobalStatByDiscipline(null, allTeachers));
        for (AcademicOfficialDiscipline _discipline : p.findOfficialDisciplines()) {
            globalStatByDisciplines.add(new GlobalStatByDiscipline(_discipline,
                    p.evalGlobalStat(periodId <= 0 ? -100 : periodId,
                            TeacherFilterFactory.and(
                                    TeacherFilterFactory.custom().addAcceptedOfficialDisciplines(_discipline.getId()),
                                    teacherFilter
                            ), courseAssignmentFilter,
                            deviationConfig)
            ));
        }
        getModel().setGlobalStatByDisciplines(globalStatByDisciplines);
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
        getTeacherFilter().onInit();
        getCourseFilter().onInit();
        onChangePeriod();
    }

    public void onFiltersChanged() {
        //onRefresh();
    }

    public void onRefreshFiltersChanged() {
        onRefresh();
    }

    public boolean containsFilter(String s) {
        String[] f = getModel().getFilters();
        if (f == null || f.length == 0) {
            return "value".equals(s);
        }
        return Arrays.asList(f).indexOf(s) >= 0;
    }

    public boolean isPercent() {
        for (String s : getCourseFilter().getModel().getRefreshFilter()) {
            if ("x:percent".equals(s)) {
                return true;
            }
        }
        return false;
    }

    public List<GlobalStatByDiscipline> getGlobalStatByDisciplines() {
        List<GlobalStatByDiscipline> all=new ArrayList<>();
        DefaultTeacherFilter d = getTeacherFilter().getTeacherFilter();
        for (GlobalStatByDiscipline globalStatByDiscipline : getModel().getGlobalStatByDisciplines()) {
            if(globalStatByDiscipline.getDiscipline()==null || d.acceptOfficialDiscipline(globalStatByDiscipline.getDiscipline())){
                all.add(globalStatByDiscipline);
            }
        }
        return all;
    }

    public List<AcademicTeacherSituation> getGlobalStatSituations() {
        List<AcademicTeacherSituation> list = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        DefaultTeacherFilter teacherFilter = getTeacherFilter().getTeacherFilter();
        for (GlobalStatByDiscipline globalStatByDiscipline : getGlobalStatByDisciplines()) {
            for (GlobalAssignmentStat s : globalStatByDiscipline.getStat().getSituationDetails(null, null)) {
                AcademicTeacherSituation sit = s.getSituation();
                if (sit != null && !set.contains(sit.getId())) {
                    if(sit==null || teacherFilter.acceptTeacherSituation(sit)) {
                        list.add(sit);
                    }
                    set.add(sit.getId());
                }
            }
        }
        return list;
    }

    public class Model {

        PieChartModel chart1;
        PieChartModel chart2;
        GlobalStat stat = new GlobalStat();
        List<AcademicSemester> semesters = new ArrayList<>();
        List<GlobalStatByDiscipline> globalStatByDisciplines = new ArrayList<>();
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "C", "TD", "TP", "PM"};
        String[] filters = defaultFilters;

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

        public List<GlobalStatByDiscipline> getGlobalStatByDisciplines() {
            return globalStatByDisciplines;
        }

        public List<String> getGlobalStatByDisciplineStrings() {
            List<String> all = new ArrayList<>();
            for (GlobalStatByDiscipline globalStatByDiscipline : globalStatByDisciplines) {
                all.add(globalStatByDiscipline.discipline == null ? "" : globalStatByDiscipline.discipline.getName());
            }
            return all;
        }



        public void setGlobalStatByDisciplines(List<GlobalStatByDiscipline> globalStatByDisciplines) {
            this.globalStatByDisciplines = globalStatByDisciplines;
        }

    }

    public static class GlobalStatByDiscipline {
        private GlobalStat stat;
        private AcademicOfficialDiscipline discipline;

        public GlobalStatByDiscipline(AcademicOfficialDiscipline discipline, GlobalStat stat) {
            this.stat = stat;
            this.discipline = discipline;
        }

        public GlobalStat getStat() {
            return stat;
        }

        public void setStat(GlobalStat stat) {
            this.stat = stat;
        }

        public AcademicOfficialDiscipline getDiscipline() {
            return discipline;
        }

        public void setDiscipline(AcademicOfficialDiscipline discipline) {
            this.discipline = discipline;
        }
    }
}
