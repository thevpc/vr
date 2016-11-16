/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.VrColorTable;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.stat.DeviationConfig;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherBaseStat;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherPeriodStat;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherSemesterStat;
import net.vpc.app.vainruling.plugins.academic.web.dialog.CopyPeriodDialogCtrl;
import net.vpc.app.vainruling.plugins.academic.web.dialog.GenerateLoadDialogCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.util.MapList;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Charge Enseignants",
        url = "modules/academic/all-teachers-course-load",
        menu = "/Education/Load",
        securityKey = "Custom.Education.AllTeachersCourseLoad"
)
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
    protected TeacherLoadFilterComponent teacherFilter = new TeacherLoadFilterComponent();
    protected CourseLoadFilterComponent courseFilter = new CourseLoadFilterComponent();

    private void reset() {
        getModel().setTables(new ArrayList<TeacherBaseStatTable>());
    }

    public TeacherLoadFilterComponent getTeacherFilter() {
        return teacherFilter;
    }

    public CourseLoadFilterComponent getCourseFilter() {
        return courseFilter;
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        VrColorTable colorTable = VrApp.getBean(VrColorTable.class);
        List<SelectItem> columnFilers = new ArrayList<>();
        columnFilers.add(FacesUtils.createSelectItem("degree", "grade", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("situation", "situation", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("discipline", "discipline", "vr-checkbox"));
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
        columnFilers.add(FacesUtils.createSelectItem("deviation", "Balance", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("deviation-graph", "Balance Grph", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("average", "Charge Moyenne", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("standard-deviation", "Ecart Type", "vr-checkbox"));
        columnFilers.add(FacesUtils.createSelectItem("population", "Population", "vr-checkbox"));

        getModel().setNoRefreshFilterItems(columnFilers);

        getTeacherFilter().onInit();
        getCourseFilter().onInit();
        onChangePeriod();
    }

    public void onChangePeriod() {
        getTeacherFilter().onChangePeriod();
        getCourseFilter().onChangePeriod();
        onRefresh();
    }

    public void onRefresh() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        int periodId = getTeacherFilter().getPeriodId();
        CourseAssignmentFilter filter = getCourseFilter().getCourseAssignmentFilter();
        boolean includeIntents = getCourseFilter().isIncludeIntents();
        reset();
        TeacherFilter customTeacherFilter = getTeacherFilter().getTeacherFilter();
        DeviationConfig deviationConfig = getCourseFilter().getDeviationConfig();
        if (periodId >= -1) {
            //ordered semesters
            List<Integer> semesterIds=new ArrayList<>();
            List<String> semesterNames=new ArrayList<>();
            List<TeacherBaseStatTable> all=new ArrayList<>();
            Map<Integer,List<TeacherBaseStat>> semesters=new HashMap<>();
            MapList<Integer, TeacherPeriodStat> year = a.evalTeacherStatList(periodId, customTeacherFilter, filter, includeIntents, deviationConfig);
            for (TeacherPeriodStat teacherPeriodStat : year) {
                if(semesterIds.isEmpty()){
                    for (TeacherSemesterStat teacherSemesterStat : teacherPeriodStat.getSemesters()) {
                        semesterIds.add(teacherSemesterStat.getSemester().getId());
                        semesterNames.add(teacherSemesterStat.getSemester().getName());
                    }
                }
                for (TeacherSemesterStat teacherSemesterStat : teacherPeriodStat.getSemesters()) {
                    List<TeacherBaseStat> list = semesters.get(teacherSemesterStat.getSemester().getId());
                    if(list==null){
                        list=new ArrayList<TeacherBaseStat>();
                        semesters.put(teacherSemesterStat.getSemester().getId(),list);
                    }
                    list.add(teacherSemesterStat);
                }
            }
            all.add(new TeacherBaseStatTable("Charge Globale",(List)year));
            for (int i = 0; i < semesterIds.size(); i++) {
                Integer semesterId = semesterIds.get(i);
                String semesterName = semesterNames.get(i);
                all.add(new TeacherBaseStatTable(semesterName, semesters.get(semesterId)));
            }
            getModel().setTables(all);

//            List<AcademicSemester> semesters = a.findSemesters();
//            getModel().setSemester1(a.evalTeacherSemesterStatList(periodId, semesters.get(0).getId(), customTeacherFilter, filter, includeIntents, deviationConfig, cache));
//            getModel().setSemester2(a.evalTeacherSemesterStatList(periodId, semesters.get(1).getId(), customTeacherFilter, filter, includeIntents, deviationConfig, cache));

//            getModel().setYear(year);
        }else{
            getModel().setTables(new ArrayList<>());
        }
//        getModel().setTables(Arrays.asList(
//                new TeacherBaseStatTable("Charge Globale", (List) getModel().getYear()),
//                new TeacherBaseStatTable("Semestre 1", (List) getModel().getSemester1()),
//                new TeacherBaseStatTable("Semestre 2", (List) getModel().getSemester2())
//        ));
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

//        List<TeacherSemesterStat> semester1 = new ArrayList<>();
//        List<TeacherSemesterStat> semester2 = new ArrayList<>();
//        List<TeacherPeriodStat> year = new ArrayList<>();
        List<TeacherBaseStatTable> tables = new ArrayList<>();
        String[] defaultFilters = {"situation", "degree", "value", "extraWeek", "c", "td", "tppm","valueWeek","eq"};
        String[] columnFilters = defaultFilters;
        List<SelectItem> noRefreshFilterItems;


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

        public List<SelectItem> getNoRefreshFilterItems() {
            return noRefreshFilterItems;
        }

        public void setNoRefreshFilterItems(List<SelectItem> noRefreshFilterItems) {
            this.noRefreshFilterItems = noRefreshFilterItems;
        }

//        public List<TeacherSemesterStat> getSemester1() {
//            return semester1;
//        }
//
//        public void setSemester1(List<TeacherSemesterStat> semester1) {
//            this.semester1 = semester1;
//            Collections.sort(this.semester1, teacherSemesterStatComparatorByTeacher);
//        }
//
//        public List<TeacherSemesterStat> getSemester2() {
//            return semester2;
//        }
//
//        public void setSemester2(List<TeacherSemesterStat> semester2) {
//            this.semester2 = semester2;
//            Collections.sort(this.semester2, teacherSemesterStatComparatorByTeacher);
//        }

//        public List<TeacherPeriodStat> getYear() {
//            return year;
//        }
//
//        public void setYear(List<TeacherPeriodStat> year) {
//            this.year = year;
//            Collections.sort(this.year, teacherPeriodStatComparatorByTeacher);
//        }

        public List<TeacherBaseStatTable> getTables() {
            return tables;
        }

        public void setTables(List<TeacherBaseStatTable> tables) {
            this.tables = tables;
        }

    }

}
