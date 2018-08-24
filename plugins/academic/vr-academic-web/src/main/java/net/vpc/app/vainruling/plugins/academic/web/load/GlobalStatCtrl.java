/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.service.util.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.stat.StatCache;
import net.vpc.app.vainruling.plugins.academic.service.util.TeacherPeriodFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.stat.DeviationConfig;
import net.vpc.app.vainruling.plugins.academic.service.stat.GlobalAssignmentStat;
import net.vpc.app.vainruling.plugins.academic.service.stat.GlobalStat;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultTeacherPeriodFilter;
import net.vpc.app.vainruling.plugins.academic.service.util.TeacherFilterFactory;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import org.primefaces.model.chart.PieChartModel;

import javax.faces.model.SelectItem;
import java.util.*;
import org.springframework.stereotype.Controller;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Stats Charge",
        url = "modules/academic/global-stat",
        menu = "/Education/Load",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_GLOBAL_STAT
)
@Controller
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

        List<SelectItem> refreshableFilters = new ArrayList<>();
        refreshableFilters.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
        refreshableFilters.add(FacesUtils.createSelectItem("deviation-week", "Balance/Sem", "vr-checkbox"));
        refreshableFilters.add(FacesUtils.createSelectItem("deviation-extra", "Balance/Supp", "vr-checkbox"));
        refreshableFilters.add(FacesUtils.createSelectItem("extra-abs", "Supp ABS", "vr-checkbox"));
        getCourseFilter().getModel().setRefreshFilterItems(refreshableFilters);
        getCourseFilter().getModel().getRefreshFilterItems().add(FacesUtils.createSelectItem("x:percent", "Pourcentages", "vr-checkbox"));

//        refreshableFilters = new ArrayList<>();
//        refreshableFilters.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
//
//        getOthersCourseFilter().getModel().setRefreshFilterItems(refreshableFilters);
//        getOthersCourseFilter().onChangePeriod();
        onRefresh();
    }

    private void setValue(PieChartModel m, String name, double value) {
        if (value != 0) {
            m.set(name + " (" + VrUtils.dformat(value, "0.000") + ")", value);
        }
    }

    private void setValuePercent(PieChartModel m, String name, double value) {
        if (value != 0) {
            m.set(name + " (" + VrUtils.dformat(value, "0.00%") + ")", value);
        }
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        CorePlugin c = VrApp.getBean(CorePlugin.class);

        int periodId = getTeacherFilter().getPeriodId();

        StatCache cache = new StatCache();
        TeacherPeriodFilter teacherFilter = getTeacherFilter().getTeacherFilter();
        DeviationConfig deviationConfig = getCourseFilter().getDeviationConfig();
        CourseAssignmentFilter courseAssignmentFilter = getCourseFilter().getCourseAssignmentFilter();
        GlobalStat allTeachers = p.evalGlobalStat(periodId <= 0 ? -100 : periodId,
                teacherFilter, courseAssignmentFilter,
                deviationConfig, null);
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
                            deviationConfig, null)
            ));
        }
        getModel().setGlobalStatByDisciplines(globalStatByDisciplines);
        getModel().setSemesters(p.findSemesters());

        PieChartModel pieModel1 = new PieChartModel();

        GlobalStat stat = getModel().getStat();
        setValue(pieModel1, "Permanents", stat.getTeachersPermanentStat().getTeachersCount());
        setValue(pieModel1, "Contactuels", stat.getTeachersContractualStat().getTeachersCount());
        setValue(pieModel1, "Vacataires", stat.getTeachersTemporaryStat().getTeachersCount());
        setValue(pieModel1, "Autres", stat.getTeachersOtherStat().getTeachersCount());
        setValue(pieModel1, "Manquant", stat.getUnassignedStat().getTeachersCount());
        pieModel1.setTitle("Repartition Enseignants");
        pieModel1.setLegendPosition("e");
        pieModel1.setShowDataLabels(true);
        getModel().chart1 = pieModel1;

        PieChartModel pieModel2 = new PieChartModel();

        setValue(pieModel2, "Permanents", stat.getTeachersPermanentStat().getTeachersCount());
        setValue(pieModel2, "Maitre Assistants pour combler Heures supp", stat.getPermanentOverloadTeacherCount());
        setValue(pieModel2, "Maitre Assistants pour combler Non Permanents", stat.getNonPermanentLoadTeacherCount());
        setValue(pieModel2, "Maitre Assistants restant à recruter", stat.getUnassignedLoadTeacherCount());
        pieModel2.setTitle("Manque Enseignants");
        pieModel2.setLegendPosition("e");
        pieModel2.setShowDataLabels(true);
        getModel().chart2 = pieModel2;

        PieChartModel pieModel3 = new PieChartModel();
        setValue(pieModel3, "Permanents", stat.getTeachersPermanentStat().getValue().getC());
        setValue(pieModel3, "Contractuels", stat.getTeachersContractualStat().getValue().getC());
        setValue(pieModel3, "Vacataire", stat.getTeachersTemporaryStat().getValue().getC());
        setValue(pieModel3, "Manquant", stat.getUnassignedStat().getValue().getC());
        setValue(pieModel3, "Autre", stat.getTeachersOtherStat().getValue().getC());
        pieModel3.setTitle("Repartition du Cours (H)");
        pieModel3.setLegendPosition("e");
        pieModel3.setShowDataLabels(true);
        getModel().chart3 = pieModel3;

        PieChartModel pieModel4 = new PieChartModel();
        setValue(pieModel4, "Permanents", stat.getTeachersPermanentStat().getValue().getTd());
        setValue(pieModel4, "Contractuels", stat.getTeachersContractualStat().getValue().getTd());
        setValue(pieModel4, "Vacataires", stat.getTeachersTemporaryStat().getValue().getTd());
        setValue(pieModel4, "Manquants", stat.getUnassignedStat().getValue().getTd());
        setValue(pieModel4, "Autres", stat.getTeachersOtherStat().getValue().getTd());
        pieModel4.setTitle("Repartition du TD (H)");
        pieModel4.setLegendPosition("e");
        pieModel4.setShowDataLabels(true);
        getModel().chart4 = pieModel4;

        PieChartModel pieModel5 = new PieChartModel();
        setValue(pieModel5, "Permanents", stat.getTeachersPermanentStat().getValue().getTp());
        setValue(pieModel5, "Contractuels", stat.getTeachersContractualStat().getValue().getTp());
        setValue(pieModel5, "Vacataire", stat.getTeachersTemporaryStat().getValue().getTp());
        setValue(pieModel5, "Manquant", stat.getUnassignedStat().getValue().getTp());
        setValue(pieModel5, "Autre", stat.getTeachersOtherStat().getValue().getTp());
        pieModel5.setTitle("Repartition du TP (H)");
        pieModel5.setLegendPosition("e");
        pieModel5.setShowDataLabels(true);
        getModel().chart5 = pieModel5;

        {
            PieChartModel pie = new PieChartModel();
            pie.setTitle("% Charge Non Permanent par discipline officielle (%)");

            Set<String> officialDisciplines = new TreeSet<>(stat.getNonPermanentLoadValueByOfficialDiscipline().keySet());
            officialDisciplines.addAll(stat.getPermanentLoadValueByOfficialDiscipline().keySet());
            for (String officialDiscipline : officialDisciplines) {
                double npv = stat.getNonPermanentLoadValueByOfficialDiscipline().getOrCreate(officialDiscipline).getValue().getEquiv();
                double pv = stat.getPermanentLoadValueByOfficialDiscipline().getOrCreate(officialDiscipline).getValue().getEquiv();
                double tot
                        = stat.getTeachersPermanentStat().getValue().getEquiv()
                        + stat.getTeachersContractualStat().getValue().getEquiv()
                        + stat.getTeachersTemporaryStat().getValue().getEquiv()
                        + stat.getTeachersOtherStat().getValue().getEquiv();
                double s = npv + pv;
                if (npv != 0 && tot != 0) {
                    String category = StringUtils.isEmpty(officialDiscipline) ? "Sans Discipline" : officialDiscipline;
                    pie.set(category + " (" + VrUtils.dformat(npv / tot, "0.00%") + ")", npv / tot);
//                    setValuePercent(pie, category, npv / s);
                }
            }

            pie.setLegendPosition("e");
            pie.setShowDataLabels(true);
            getModel().chart6 = pie;
        }

        {
            PieChartModel pie = new PieChartModel();

            Set<String> officialDisciplines = new TreeSet<>(stat.getNonPermanentLoadValueByNonOfficialDiscipline().keySet());
            officialDisciplines.addAll(stat.getPermanentLoadValueByNonOfficialDiscipline().keySet());
            for (String officialDiscipline : officialDisciplines) {
                double npv = stat.getNonPermanentLoadValueByNonOfficialDiscipline().getOrCreate(officialDiscipline).getValue().getEquiv();
                double pv = stat.getPermanentLoadValueByNonOfficialDiscipline().getOrCreate(officialDiscipline).getValue().getEquiv();
                double s = npv + pv;
                if (s != 0) {
                    String category = StringUtils.isEmpty(officialDiscipline) ? "Sans Discipline" : officialDiscipline;
                    setValuePercent(pie, category, npv / s);
                }
            }

            pie.setTitle("% Charge Non Permanent par discipline non officielle (%)");
            pie.setLegendPosition("e");
            pie.setShowDataLabels(true);
            getModel().chart7 = pie;
        }

        {
            PieChartModel pie = new PieChartModel();
            pie.setTitle("Nbr Non Permanents par discipline officielle");
            Set<String> officialDisciplines = new TreeSet<>(stat.getNonPermanentLoadValueByOfficialDiscipline().keySet());
            officialDisciplines.addAll(stat.getPermanentLoadValueByOfficialDiscipline().keySet());
            for (String officialDiscipline : officialDisciplines) {
                double s = stat.getNonPermanentLoadValueByOfficialDiscipline().getOrCreate(officialDiscipline).getTeachersCount();
                if (s != 0) {
                    String category = StringUtils.isEmpty(officialDiscipline) ? "Sans Discipline" : officialDiscipline;
                    setValue(pie, category, s);
                }
            }

            pie.setLegendPosition("e");
            pie.setShowDataLabels(true);
            getModel().chart8 = pie;
        }

        {
            PieChartModel pie = new PieChartModel();
            pie.setTitle("Nbr Non Permanents par discipline non officielle");
            Set<String> officialDisciplines = new TreeSet<>(stat.getNonPermanentLoadValueByNonOfficialDiscipline().keySet());
            officialDisciplines.addAll(stat.getPermanentLoadValueByNonOfficialDiscipline().keySet());
            for (String officialDiscipline : officialDisciplines) {
                double s = stat.getNonPermanentLoadValueByNonOfficialDiscipline().getOrCreate(officialDiscipline).getTeachersCount();
                if (s != 0) {
                    String category = StringUtils.isEmpty(officialDiscipline) ? "Sans Discipline" : officialDiscipline;
                    setValue(pie, category, s);
                }
            }

            pie.setLegendPosition("e");
            pie.setShowDataLabels(true);
            getModel().chart9 = pie;
        }

        {
            PieChartModel pie = new PieChartModel();
            pie.setTitle("Besoin en Maitre assistants par discipline officielle");
            Set<String> officialDisciplines = new TreeSet<>(stat.getNonPermanentLoadValueByOfficialDiscipline().keySet());
            officialDisciplines.addAll(stat.getPermanentLoadValueByOfficialDiscipline().keySet());
            for (String officialDiscipline : officialDisciplines) {
                double s = stat.getNonPermanentLoadValueByOfficialDiscipline().getOrCreate(officialDiscipline).getValue().getEquiv();
                if (s != 0) {
                    s = stat.getReferenceTeacherDueLoad() == 0 ? 0 : s / stat.getReferenceTeacherDueLoad();
                    String category = StringUtils.isEmpty(officialDiscipline) ? "Sans Discipline" : officialDiscipline;
                    setValue(pie, category, s);
                }
            }

            pie.setLegendPosition("e");
            pie.setShowDataLabels(true);
            getModel().chart10 = pie;
        }

        {
            PieChartModel pie = new PieChartModel();
            pie.setTitle("Besoin en Maitre assistants par discipline non officielle");
            Set<String> officialDisciplines = new TreeSet<>(stat.getNonPermanentLoadValueByNonOfficialDiscipline().keySet());
            officialDisciplines.addAll(stat.getPermanentLoadValueByNonOfficialDiscipline().keySet());
            for (String officialDiscipline : officialDisciplines) {
                double s = stat.getNonPermanentLoadValueByNonOfficialDiscipline().getOrCreate(officialDiscipline).getValue().getEquiv();
                if (s != 0) {
                    s = stat.getReferenceTeacherDueLoad() == 0 ? 0 : s / stat.getReferenceTeacherDueLoad();
                    String category = StringUtils.isEmpty(officialDiscipline) ? "Sans Discipline" : officialDiscipline;
                    setValue(pie, category, s);
                }
            }

            pie.setLegendPosition("e");
            pie.setShowDataLabels(true);
            getModel().chart11 = pie;
        }

        Set<String> nonOfficialDisciplines = new TreeSet<>(stat.getNonPermanentLoadValueByNonOfficialDiscipline().keySet());
        nonOfficialDisciplines.addAll(stat.getPermanentLoadValueByNonOfficialDiscipline().keySet());
        onCalcFiltersChanged();
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        getTeacherFilter().onInit();
        getCourseFilter().onInit();
        onChangePeriod();
    }

    public void onViewFiltersChanged() {
        //onRefresh();
    }

    public void onCalcFiltersChanged() {
        if ("situation".equalsIgnoreCase(getModel().getDetailsType())) {
            List<GlobalAssignmentStat> all = new ArrayList<>();
            for (GlobalAssignmentStat assignment : getModel().getStat().getAssignments()) {
                if (assignment.getSituation() != null && assignment.getDegree() == null) {
                    all.add(assignment);
                }
            }
            getModel().setDetails(all);
        } else if ("degree".equalsIgnoreCase(getModel().getDetailsType())) {
            List<GlobalAssignmentStat> all = new ArrayList<>();
            for (GlobalAssignmentStat assignment : getModel().getStat().getAssignments()) {
                if (assignment.getSituation() == null && assignment.getDegree() != null) {
                    all.add(assignment);
                }
            }
            getModel().setDetails(all);
        } else {
            getModel().setDetails(getModel().getStat().getAssignments());
        }
        Collections.sort(getModel().getStat().getAssignments(), GlobalAssignmentStat.NAME_COMPARATOR);
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
        for (String s : getCourseFilter().getModel().getRefreshFilterSelected()) {
            if ("x:percent".equals(s)) {
                return true;
            }
        }
        return false;
    }

    public List<GlobalStatByDiscipline> getGlobalStatByDisciplines() {
        List<GlobalStatByDiscipline> all = new ArrayList<>();
        DefaultTeacherPeriodFilter d = getTeacherFilter().getTeacherFilter();
        for (GlobalStatByDiscipline globalStatByDiscipline : getModel().getGlobalStatByDisciplines()) {
            if (globalStatByDiscipline.getDiscipline() == null || d.acceptOfficialDiscipline(globalStatByDiscipline.getDiscipline())) {
                all.add(globalStatByDiscipline);
            }
        }
        return all;
    }

    public List<AcademicTeacherSituation> getGlobalStatSituations() {
        List<AcademicTeacherSituation> list = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        DefaultTeacherPeriodFilter teacherFilter = getTeacherFilter().getTeacherFilter();
        for (GlobalStatByDiscipline globalStatByDiscipline : getGlobalStatByDisciplines()) {
            for (GlobalAssignmentStat s : globalStatByDiscipline.getStat().getSituationDetails(null, null)) {
                AcademicTeacherSituation sit = s.getSituation();
                if (sit != null && !set.contains(sit.getId())) {
                    if (s.getTeachersCount() > 0) {
                        if (teacherFilter.acceptTeacherSituation(sit)) {
                            list.add(sit);
                        }
                        set.add(sit.getId());
                    }
                }
            }
        }
        return list;
    }

    public class Model {

        PieChartModel chart1;
        PieChartModel chart2;
        PieChartModel chart3;
        PieChartModel chart4;
        PieChartModel chart5;
        PieChartModel chart6;
        PieChartModel chart7;
        PieChartModel chart8;
        PieChartModel chart9;
        PieChartModel chart10;
        PieChartModel chart11;
        GlobalStat stat = new GlobalStat();
        String detailsType;
        List<GlobalAssignmentStat> details = new ArrayList<>();
        List<AcademicSemester> semesters = new ArrayList<>();
        List<GlobalStatByDiscipline> globalStatByDisciplines = new ArrayList<>();
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "C", "TD", "TP", "PM"};
        String[] filters = defaultFilters;

        public String getDetailsType() {
            return detailsType;
        }

        public void setDetailsType(String detailsType) {
            this.detailsType = detailsType;
        }

        public List<GlobalAssignmentStat> getDetails() {
            return details;
        }

        public void setDetails(List<GlobalAssignmentStat> details) {
            this.details = details;
        }

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

        public PieChartModel getChart3() {
            return chart3;
        }

        public void setChart3(PieChartModel chart3) {
            this.chart3 = chart3;
        }

        public PieChartModel getChart4() {
            return chart4;
        }

        public void setChart4(PieChartModel chart4) {
            this.chart4 = chart4;
        }

        public PieChartModel getChart5() {
            return chart5;
        }

        public void setChart5(PieChartModel chart5) {
            this.chart5 = chart5;
        }

        public PieChartModel getChart6() {
            return chart6;
        }

        public void setChart6(PieChartModel chart6) {
            this.chart6 = chart6;
        }

        public PieChartModel getChart7() {
            return chart7;
        }

        public void setChart7(PieChartModel chart7) {
            this.chart7 = chart7;
        }

        public PieChartModel getChart8() {
            return chart8;
        }

        public void setChart8(PieChartModel chart8) {
            this.chart8 = chart8;
        }

        public PieChartModel getChart9() {
            return chart9;
        }

        public void setChart9(PieChartModel chart9) {
            this.chart9 = chart9;
        }

        public PieChartModel getChart10() {
            return chart10;
        }

        public void setChart10(PieChartModel chart10) {
            this.chart10 = chart10;
        }

        public PieChartModel getChart11() {
            return chart11;
        }

        public void setChart11(PieChartModel chart11) {
            this.chart11 = chart11;
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
