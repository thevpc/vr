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
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.StatCache;
import net.vpc.app.vainruling.plugins.academic.service.CourseFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgramType;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherBaseStat;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherPeriodStat;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherSemesterStat;
import net.vpc.app.vainruling.plugins.academic.web.dialog.CopyPeriodDialogCtrl;
import net.vpc.app.vainruling.plugins.academic.web.dialog.GenerateLoadDialogCtrl;
import net.vpc.common.strings.StringUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import java.util.*;

/**
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
        getModel().setYear(new ArrayList<TeacherPeriodStat>());
    }

    public int getPeriodId(){
        String p = getModel().getSelectedPeriod();
        if(StringUtils.isEmpty(p)){
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
            if(mainPeriod!=null && p.getId()==mainPeriod.getId()){
                getModel().setSelectedPeriod(String.valueOf(p.getId()));
            }
        }
        List<SelectItem> columnFilers=new ArrayList<>();
        columnFilers.add(new SelectItem("degree", "grade"));
        columnFilers.add(new SelectItem("situation", "situation"));
        columnFilers.add(new SelectItem("weeks", "semaines"));
        columnFilers.add(new SelectItem("eq", "equivalent"));
        columnFilers.add(new SelectItem("due", "<span style='color:peru;'>du</span>",null,false,false));
        columnFilers.add(new SelectItem("value", "<span style='color:cornflowerblue;'>charge</span>",null,false,false));
        columnFilers.add(new SelectItem("extra", "<span style='color:deeppink;'>H. Supp.</span>",null,false,false));
        columnFilers.add(new SelectItem("dueWeek", "du / Sem"));
        columnFilers.add(new SelectItem("valueWeek", "<span style='color:goldenrod;'>charge / Sem</span>",null,false,false));
        columnFilers.add(new SelectItem("extraWeek", "H. Supp. / Sem"));
        columnFilers.add(new SelectItem("c", "cours"));
        columnFilers.add(new SelectItem("tp", "tp"));
        columnFilers.add(new SelectItem("td", "td"));
        columnFilers.add(new SelectItem("pm", "pm"));
        columnFilers.add(new SelectItem("tppm", "tp+pm"));
        getModel().setColumnFilterItems(columnFilers);

        onChangePeriod();
    }

    public void onChangePeriod() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        List<SelectItem> refreshableFilers=new ArrayList<>();
        refreshableFilers.add(new SelectItem("intents","Inclure Voeux"));
        for (AcademicProgramType pt : a.findProgramTypes()) {
            refreshableFilers.add(new SelectItem("AcademicProgramType:"+pt.getId(),pt.getName()));
        }
        int periodId = getPeriodId();
        for (String label : a.findCoursePlanLabels(periodId)) {
            refreshableFilers.add(new SelectItem("label:"+label,label));
            refreshableFilers.add(new SelectItem("label:!"+label,"!"+label));
        }
        refreshableFilers.add(new SelectItem("nolabel", "Sans Label"));
        getModel().setRefreshFilterItems(refreshableFilers);
        onRefresh();
    }

    public CourseFilter getCourseFilter(){
        CourseFilter filter = new CourseFilter();
        boolean nolabel=false;
        for (String rf : getModel().getRefreshFilter()) {
            if(rf.equals("intents")){
                filter.setIncludeIntents(true);
            }else if(rf.startsWith("label:")){
                Set<String> labels = filter.getLabels();
                if(labels==null){
                    labels=new HashSet<>();
                    filter.setLabels(labels);
                }
                labels.add(rf.substring(rf.indexOf(":")+1));
            }else if(rf.startsWith("AcademicProgramType:")){
                Set<Integer> types = filter.getProgramTypes();
                if(types==null){
                    types=new HashSet<>();
                    filter.setProgramTypes(types);
                }
                types.add(Integer.parseInt(rf.substring(rf.indexOf(":") + 1)));
            }else if(rf.equals("nolabel")){
                nolabel=true;
            }
        }
        if(nolabel){
            filter.setLabels(new HashSet<String>());
        }
        return filter;
    }

    public void onRefresh() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        StatCache cache = new StatCache();
        int periodId = getPeriodId();
        CourseFilter filter=getCourseFilter();

        getModel().setSemester1(a.evalTeacherSemesterStatList(periodId, "S1", null, filter, cache));
        getModel().setSemester2(a.evalTeacherSemesterStatList(periodId, "S2", null, filter, cache));
        getModel().setYear(a.evalTeacherStatList(periodId, null, null, filter, cache));
        getModel().setTables(Arrays.asList(
                new TeacherBaseStatTable("Charge Globale",(List) getModel().getYear()),
                new TeacherBaseStatTable("Semestre 1",(List) getModel().getSemester1()),
                new TeacherBaseStatTable("Semestre 2",(List) getModel().getSemester2())
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
        String[] defaultFilters = {"situation", "degree", "valueWeek", "extraWeek", "c", "td", "tp", "pm"};
        String[] columnFilters = defaultFilters;
        String[] refreshFilter = {};
        List<SelectItem> columnFilterItems;
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

        public void setRefreshFilter(String[] refreshFilter) {
            this.refreshFilter = refreshFilter;
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

        public List<TeacherPeriodStat> getYear() {
            return year;
        }

        public void setYear(List<TeacherPeriodStat> year) {
            this.year = year;
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
