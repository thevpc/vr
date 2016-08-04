/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.VrColorTable;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseFilter;
import net.vpc.app.vainruling.plugins.academic.service.StatCache;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgramType;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherPeriodStat;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherSemesterStat;
import net.vpc.app.vainruling.plugins.academic.web.dialog.CopyPeriodDialogCtrl;
import net.vpc.app.vainruling.plugins.academic.web.dialog.GenerateLoadDialogCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Charge Enseignants",
        url = "modules/academic/all-teachers-course-load",
        menu = "/Education/Load",
        securityKey = "Custom.Education.AllTeachersCourseLoad"
)
@ManagedBean
public class AllTeachersCourseLoadCtrl {
    private static Comparator<TeacherSemesterStat> teacherSemesterStatComparatorByTeacher = new Comparator<TeacherSemesterStat>() {
        @Override
        public int compare(TeacherSemesterStat o1, TeacherSemesterStat o2) {
            return o1.getTeacher().getContact().getFullName()
                    .compareTo(o2.getTeacher().getContact().getFullName());
        }
    };
    private static Comparator<TeacherPeriodStat> teacherPeriodStatComparatorByTeacher = new Comparator<TeacherPeriodStat>() {
        @Override
        public int compare(TeacherPeriodStat o1, TeacherPeriodStat o2) {
            return o1.getTeacher().getContact().getFullName()
                    .compareTo(o2.getTeacher().getContact().getFullName());
        }
    };
    protected Model model = new Model();

    private void reset() {
        getModel().setSemester1(new ArrayList<TeacherSemesterStat>());
        getModel().setSemester2(new ArrayList<TeacherSemesterStat>());
        getModel().setYear(new ArrayList<TeacherPeriodStat>());
    }

    public int getPeriodId() {
        String p = getModel().getSelectedPeriod();
        if (StringUtils.isEmpty(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            return core.findAppConfig().getMainPeriod().getId();
        }
        return Integer.parseInt(p);
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
        VrColorTable colorTable = VrApp.getBean(VrColorTable.class);
        List<SelectItem> columnFilers = new ArrayList<>();
        columnFilers.add(FacesUtils.createSelectItem("degree", "grade", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("situation", "situation", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("weeks", "semaines", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("eq", "equivalent", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("due", "du", "vr-label-bg" + colorTable.pos("due") + " vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("value", "charge", "vr-label-bg" + colorTable.pos("value") + " vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("extra", "H. Supp.", "vr-label-bg" + colorTable.pos("extra") + " vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("dueWeek", "du / Sem", "vr-label-bg" + colorTable.pos("dueWeek") + " vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("valueWeek", "charge / Sem", "vr-label-bg" + colorTable.pos("valueWeek") + " vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("extraWeek", "H. Supp. / Sem", "vr-label-bg" + colorTable.pos("extraWeek") + " vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("c", "cours", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("tp", "tp", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("td", "td", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("pm", "pm", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("tppm", "tp+pm", "vr-checkbox"));
        getModel().setColumnFilterItems(columnFilers);

        onChangePeriod();
    }

    public void onChangePeriod() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        List<SelectItem> refreshableFilers = new ArrayList<>();
        refreshableFilers.add(FacesUtils.createSelectItem("intents", "Inclure Voeux", "vr-checkbox"));
        for (AcademicProgramType pt : a.findProgramTypes()) {
            refreshableFilers.add(FacesUtils.createSelectItem("AcademicProgramType:" + pt.getId(), pt.getName(), "vr-checkbox"));
        }
        int periodId = getPeriodId();
        for (String label : a.findCoursePlanLabels(periodId)) {
            refreshableFilers.add(FacesUtils.createSelectItem("label:" + label, label, "vr-checkbox"));
            refreshableFilers.add(FacesUtils.createSelectItem("label:!" + label, "!" + label, "vr-checkbox"));
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
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        StatCache cache = new StatCache();
        int periodId = getPeriodId();
        CourseFilter filter = getCourseFilter();

        getModel().setSemester1(a.evalTeacherSemesterStatList(periodId, "S1", null, filter, cache));
        getModel().setSemester2(a.evalTeacherSemesterStatList(periodId, "S2", null, filter, cache));
        getModel().setYear(a.evalTeacherStatList(periodId, null, null, filter, cache));
        getModel().setTables(Arrays.asList(
                new TeacherBaseStatTable("Charge Globale", (List) getModel().getYear()),
                new TeacherBaseStatTable("Semestre 1", (List) getModel().getSemester1()),
                new TeacherBaseStatTable("Semestre 2", (List) getModel().getSemester2())
        ));
    }

    public Model getModel() {
        return model;
    }

    public void onOthersFiltersChanged() {
        onRefresh();
    }

    public boolean containsFilter(String s) {
        String[] f = getModel().getColumnFilters();
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

    public void openGenerateDialog() {
        GenerateLoadDialogCtrl bean = VrApp.getBean(GenerateLoadDialogCtrl.class);
        GenerateLoadDialogCtrl.Config c = new GenerateLoadDialogCtrl.Config();
        bean.openDialog(c);
    }

    public void openCopyDialog() {
        CopyPeriodDialogCtrl bean = VrApp.getBean(CopyPeriodDialogCtrl.class);
        CopyPeriodDialogCtrl.Config c = new CopyPeriodDialogCtrl.Config();
        bean.openDialog(c);
    }

    public static class Model {

        List<TeacherSemesterStat> semester1 = new ArrayList<>();
        List<TeacherSemesterStat> semester2 = new ArrayList<>();
        List<TeacherPeriodStat> year = new ArrayList<>();
        List<TeacherBaseStatTable> tables = new ArrayList<>();
        String[] defaultFilters = {"situation", "degree", "value", "extraWeek", "c", "td", "tppm"};
        String[] columnFilters = defaultFilters;
        List<SelectItem> columnFilterItems;
        String[] refreshFilter = {};
        List<SelectItem> refreshFilterItems;
        List<SelectItem> periods = new ArrayList<>();
        String selectedPeriod = null;

        public String[] getColumnFilters() {
            return columnFilters;
        }

        public void setColumnFilters(String[] columnFilters) {
            this.columnFilters = (columnFilters == null || columnFilters.length == 0) ? defaultFilters : columnFilters;
        }

        public String[] getDefaultFilters() {
            return defaultFilters;
        }

        public void setDefaultFilters(String[] defaultFilters) {
            this.defaultFilters = defaultFilters;
        }

        public String[] getRefreshFilter() {
            return refreshFilter;
        }

        public void setRefreshFilter(String[] refreshFilter) {
            this.refreshFilter = refreshFilter;
        }

        public List<SelectItem> getColumnFilterItems() {
            return columnFilterItems;
        }

        public void setColumnFilterItems(List<SelectItem> columnFilterItems) {
            this.columnFilterItems = columnFilterItems;
        }

        public List<SelectItem> getRefreshFilterItems() {
            return refreshFilterItems;
        }

        public void setRefreshFilterItems(List<SelectItem> refreshFilterItems) {
            this.refreshFilterItems = refreshFilterItems;
        }

        public List<TeacherSemesterStat> getSemester1() {
            return semester1;
        }

        public void setSemester1(List<TeacherSemesterStat> semester1) {
            this.semester1 = semester1;
            Collections.sort(this.semester1, teacherSemesterStatComparatorByTeacher);
        }

        public List<TeacherSemesterStat> getSemester2() {
            return semester2;
        }

        public void setSemester2(List<TeacherSemesterStat> semester2) {
            this.semester2 = semester2;
            Collections.sort(this.semester2, teacherSemesterStatComparatorByTeacher);
        }

        public List<TeacherPeriodStat> getYear() {
            return year;
        }

        public void setYear(List<TeacherPeriodStat> year) {
            this.year = year;
            Collections.sort(this.year, teacherPeriodStatComparatorByTeacher);
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

        public List<TeacherBaseStatTable> getTables() {
            return tables;
        }

        public void setTables(List<TeacherBaseStatTable> tables) {
            this.tables = tables;
        }
    }

}
