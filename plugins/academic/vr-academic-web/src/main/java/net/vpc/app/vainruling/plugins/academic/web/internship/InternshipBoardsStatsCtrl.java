/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.internship;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.NamedValueCount;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.AcademicInternshipType;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExt;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExtList;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import org.primefaces.model.chart.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * internships for teachers
 *
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Stats Stages",
        menu = "/Education/Projects/Internships",
        securityKey = "Custom.Education.InternshipBoardsStat",
        url = "modules/academic/internship/internship-boards-stats"
)
public class InternshipBoardsStatsCtrl /*extends MyInternshipBoardsCtrl*/ {
    private Model model = new Model();
    @Autowired
    private CorePlugin core;
    AcademicPlugin academic = VrApp.getBean(AcademicPlugin.class);

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    public void onPageLoad() {
        onUpdatePeriod();
    }

    public void onUpdateBoard() {
        getModel().setInternshipBoard(getSelectedInternshipBoard());
        onRefresh();
    }

    public void onUpdatePeriod() {
        onRefresh();
    }

    public AcademicInternshipBoard getSelectedInternshipBoard() {
        String ii = getModel().getBoardId();
        if (ii != null && ii.length() > 0) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            AcademicInternshipBoard tt = p.findInternshipBoard(Integer.parseInt(ii));
            if (tt != null) {
                return tt;
            }
        }
        return null;
    }

    public void onRefresh() {
        onRefreshListMode();
    }

    public void onRefreshListMode() {
        getModel().getStatCharts().clear();
        getModel().getStatTables().clear();
        getModel().setInternshipInfos(new ArrayList<AcademicInternshipInfo>());
        getModel().setInternshipItems(new ArrayList<SelectItem>());
        getModel().setInternships(new ArrayList<AcademicInternship>());
        getModel().setBoards(new ArrayList<SelectItem>());
        getModel().setTeachers(new ArrayList<SelectItem>());
        getModel().setInternshipTypes(new ArrayList<SelectItem>());
        getModel().getPeriods().clear();
        AcademicTeacher currentTeacher = getCurrentTeacher();
        AcademicInternshipExtList internships = new AcademicInternshipExtList();
        List<AcademicInternshipBoard> internshipBoards = new ArrayList<>();
        for (AppPeriod period : core.findNavigatablePeriods()) {
            getModel().getPeriods().add(new SelectItem(String.valueOf(period.getId()), period.getName()));
        }

        int periodId= Convert.toInt(getModel().getPeriodId(), IntegerParserConfig.LENIENT_F);
        int departmentId=currentTeacher==null || currentTeacher.getDepartment()==null?-1:currentTeacher.getDepartment().getId();

        if (currentTeacher != null) {
            int boardId = getModel().getInternshipBoard() == null ? -1 : getModel().getInternshipBoard().getId();
            int internshipTypeId = -1;
            if (boardId == -1) {
                internshipTypeId = StringUtils.isEmpty(getModel().getFilterInternshipTypeId()) ? -1 : Integer.valueOf(getModel().getFilterInternshipTypeId());
            }
            internshipBoards = academic.findEnabledInternshipBoardsByDepartment(periodId,departmentId,null);
            if (boardId == -1 && internshipTypeId == -1) {
                internships = new AcademicInternshipExtList();
            } else {
                internships =academic.findInternshipsByTeacherExt(
                        periodId, departmentId, -1,
                        internshipTypeId, boardId,
                        false
                );
            }
        }

        for (AcademicInternshipBoard t : internshipBoards) {
            String n = t.getName();
            getModel().getBoards().add(new SelectItem(String.valueOf(t.getId()), n));
        }

        AcademicPlugin pp = VrApp.getBean(AcademicPlugin.class);
        List<AcademicInternshipInfo> internshipInfosToAddTo = getModel().getInternshipInfos();
        List<SelectItem> internshipItemsToAddTo = getModel().getInternshipItems();
        for (AcademicInternshipExt t : internships.getInternshipExts()) {
            String n = null;
            AcademicStudent s = t.getInternship().getStudent();
            String sname = pp.getValidName(s);
            n = (t.getInternship().getBoard() == null ? "?" : t.getInternship().getBoard().getName()) + "-" + t.getInternship().getCode() + "-" + sname + "-" + t.getInternship().getName();
            internshipItemsToAddTo.add(new SelectItem(String.valueOf(t.getInternship().getId()), n));
            internshipInfosToAddTo.add(new AcademicInternshipInfo(t, getCurrentTeacher()));
        }

        getModel().setInternships(internships.getInternships());

        getModel().setBoardManager(false);


        for (AcademicInternshipType t : academic.findInternshipTypes()) {
            getModel().getInternshipTypes().add(new SelectItem(String.valueOf(t.getId()), t.getName()));
        }
        getModel().setFilterInternshipTypeVisible(StringUtils.isEmpty(getModel().getBoardId()));

        getModel().setAcademicInternshipCounts(new ArrayList<AcademicInternshipCount>());
        {
            BarChartModel d1 = new BarChartModel();
            d1.setTitle("Encadrements");
            d1.setLegendPosition("e");
            d1.setShadow(true);

            ChartSeries boys = new ChartSeries();
            boys.setLabel("Encadrements");
            d1.addSeries(boys);

            Map<String, Number> data = new LinkedHashMap<String, Number>();
            Map<Integer, Number> localIntershipSupersorsMap = new LinkedHashMap<Integer, Number>();

            for (AcademicInternshipInfo ii : getModel().getInternshipInfos()) {
                AcademicTeacher s1 = ii.getInternship().getSupervisor();
                AcademicTeacher s2 = ii.getInternship().getSecondSupervisor();
                if (s1 == null && s2 == null) {
                    //do nothing
                    String s0 = "<< Stages Sans Encadrement >>";
                    Number y = data.get(s0);
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 1;
                    }
                    data.put(s0, y);
                } else if (s1 != null && s2 == null) {
                    String s0 = s1.resolveFullName();
                    Number y = localIntershipSupersorsMap.get(s1.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 1;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s1.getId(), y);
                } else if (s2 != null && s1 == null) {
                    String s0 = s2.resolveFullName();
                    Number y = localIntershipSupersorsMap.get(s2.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 1;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s2.getId(), y);
                } else {
                    String s0 = s1.resolveFullName();
                    Number y = localIntershipSupersorsMap.get(s1.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 0.5;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s1.getId(), y);

                    s0 = s2.resolveFullName();
                    y = localIntershipSupersorsMap.get(s2.getId());
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.doubleValue() + 0.5;
                    }
                    data.put(s0, y);
                    localIntershipSupersorsMap.put(s2.getId(), y);
                }
            }
            int filterPeriodId = -1;
            int filterTypeId = -1;
            if (getModel().getInternshipBoard() == null) {
                AppConfig appConfig = VrApp.getBean(CorePlugin.class).getCurrentConfig();
                filterPeriodId = (appConfig == null || appConfig.getMainPeriod() == null) ? -1 : appConfig.getMainPeriod().getId();
                String d = getModel().getFilterInternshipTypeId();
                filterTypeId = StringUtils.isEmpty(d) ? -1 : Integer.valueOf(d);
            } else {
                filterPeriodId = getModel().getInternshipBoard().getPeriod().getId();
                filterTypeId = getModel().getInternshipBoard().getInternshipType().getId();
            }
            Map<Integer, Number> internshipTeachersInternshipsCounts = academic.findInternshipTeachersInternshipsCounts(
                    filterPeriodId,
                    filterTypeId
            );
            for (AcademicTeacher t : academic.findTeachers()) {
                String n = academic.getValidName(t);
                double count = 0;
                double localCount = 0;
                if (getModel().getInternshipBoard() != null) {
                    Number cc = internshipTeachersInternshipsCounts.get(t.getId());
                    count = cc == null ? 0 : cc.doubleValue();
                }
                Number cc = localIntershipSupersorsMap.get(t.getId());
                localCount = cc == null ? 0 : cc.doubleValue();

                String countSuffix = "";
                if (count > 0) {
                    if (localCount == count) {
                        //all internships are visible here
                        if (count == ((int) count)) {
                            countSuffix += " (" + ((int) count) + ")";
                        } else {
                            countSuffix += " (" + (count) + ")";
                        }
                    } else {
                        String s2 = (count == ((int) count)) ? String.valueOf(((int) count)) : String.valueOf(count);
                        String s1 = (localCount == ((int) localCount)) ? String.valueOf(((int) localCount)) : String.valueOf(localCount);
                        countSuffix += " (" + s1 + "<" + s2 + ")";
                    }
                }
                getModel().getTeachers().add(new SelectItem(String.valueOf(t.getId()), n + countSuffix));
            }

            data = VrUtils.reverseSortCount(data);
            boys.setData(new LinkedHashMap<Object, Number>(data));

//                getModel().setBar1(d1);
            List<AcademicInternshipCount> list = new ArrayList<>();
            for (Map.Entry<String, Number> entry : data.entrySet()) {
                AcademicInternshipCount a = new AcademicInternshipCount();
                a.setTeacherName(entry.getKey());
                a.setCount(entry.getValue().doubleValue());
                list.add(a);
            }
            getModel().setAcademicInternshipCounts(list);
        }
        onRefreshStats();

    }

     public AcademicTeacher getCurrentTeacher() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        return a.getCurrentTeacher();
    }

    protected void onRefreshStats() {
        getModel().getStatCharts().clear();
        AcademicPlugin aca = AcademicPlugin.get();

        if (!getModel().getInternshipInfos().isEmpty()) {
            List<AcademicInternship> internships = getModel().getInternships();
            {
                //Etats stages
                DonutChartModel d1 = new DonutChartModel();
                d1.setTitle("Préparation Stage");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);

                Map<String, Number> circle1 = aca.statEvalInternshipStatus(internships);
                Map<String, Number> circle2 = aca.statEvalInternshipAssignmentCount(internships);
                VrUtils.mergeMapKeys(circle1, circle2);
                d1.addCircle(circle1);
                d1.addCircle(circle2);
                getModel().getStatCharts().put("InternshipStatus", d1);
            }
            {

                Map<String, Number> circle3 = aca.statEvalInternshipJuryExaminerCount(internships);
                Map<String, Number> circle4 = aca.statEvalInternshipJuryChairCount(internships);
                VrUtils.mergeMapKeys(circle3, circle4);

                //Etat Soutenance
                DonutChartModel d2 = new DonutChartModel();
                d2.setTitle("Préparation Soutenance");

                d2.setLegendPosition("e");
                d2.setSliceMargin(2);
                d2.setShowDataLabels(true);
                d2.setDataFormat("value");
                d2.setShadow(true);
                d2.addCircle(circle3);
                d2.addCircle(circle4);
                getModel().getStatCharts().put("InternshipJury", d2);
            }

            {

                List<NamedValueCount> circle1_table = aca.statEvalInternshipRegion(internships);
                Map<String, Number> circle1 = VrUtils.namedValueCountToMap(circle1_table);

                DonutChartModel d1 = new DonutChartModel();
                d1.setTitle("Régions");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);
                d1.addCircle(circle1);
                getModel().getStatCharts().put("Regions", d1);
                getModel().getStatTables().put("Regions", circle1_table);
            }
            {

                List<NamedValueCount> circle2_table = aca.statEvalInternshipGovernorate(internships);
                Map<String, Number> circle2 = VrUtils.namedValueCountToMap(circle2_table);

                DonutChartModel d1 = new DonutChartModel();
                d1.setTitle("Gouvernorats");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);
                d1.addCircle(circle2);
                getModel().getStatCharts().put("Governorates", d1);
                getModel().getStatTables().put("Governorates", circle2_table);
            }
            // disciplines & technologies
            {


                List<NamedValueCount> circle1_table = aca.statEvalInternshipDiscipline(internships);
                List<NamedValueCount> circle2_table = aca.statEvalInternshipTechnologies(internships);
                Map<String, Number> circle1 = VrUtils.namedValueCountToMap(circle1_table);
                Map<String, Number> circle2 = VrUtils.namedValueCountToMap(circle2_table);

                circle1 = VrUtils.reverseSortCount(circle1, 10, "Autres Disc.");
                circle2 = VrUtils.reverseSortCount(circle2, 10, "Autres Technos");

                PieChartModel d1 = new PieChartModel();
                d1.setTitle("Disciplines");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);
                d1.setFill(true);

                PieChartModel d2 = new PieChartModel();
                d2.setTitle("Technologies");
                d2.setLegendPosition("e");
                d2.setSliceMargin(2);
                d2.setShowDataLabels(true);
                d2.setDataFormat("value");
                d2.setShadow(true);
                d2.setFill(true);
                d1.setData(circle1);
                d2.setData(circle2);
                getModel().getStatCharts().put("Disciplines", d1);
                getModel().getStatCharts().put("Technologies", d2);
                getModel().getStatTables().put("Disciplines", circle1_table);
                getModel().getStatTables().put("Technologies", circle2_table);

            }

            //variant
            {

                List<NamedValueCount> circle1_table = aca.statEvalInternshipVariant(internships);
                Map<String, Number> circle1 = VrUtils.namedValueCountToMap(circle1_table);

                DonutChartModel d1 = new DonutChartModel();
                d1.setTitle("Variantes");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);
                d1.addCircle(circle1);
                getModel().getStatCharts().put("InternshipVariant", d1);
                getModel().getStatTables().put("InternshipVariant", circle1_table);
            }

            //periods
            {

                List<NamedValueCount> circle2_table = aca.statEvalInternshipPeriod(internships);
                Map<String, Number> circle2 = VrUtils.namedValueCountToMap(circle2_table);

                DonutChartModel d1 = new DonutChartModel();
                d1.setTitle("Périodes");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);
                d1.addCircle(circle2);
                getModel().getStatCharts().put("InternshipPeriod", d1);
                getModel().getStatTables().put("InternshipPeriod", circle2_table);
            }

            //periods
            {

//                List<NamedValueCount> circle2_table=aca.statEvalInternshipPeriod(internships);
                Map<String, Number> circle2 = new LinkedHashMap<>();
                for (AcademicInternshipCount c : getModel().getAcademicInternshipCounts()) {
                    circle2.put(c.getTeacherName(), c.getCount());
                }
                Map<String, Number> circle_part = VrUtils.reverseSortCount(circle2, 10, "Autres");

                PieChartModel d1 = new PieChartModel();
                d1.setTitle("Enseignants");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);
                d1.setData(circle_part);
                getModel().getStatCharts().put("SupervisorsPie", d1);

                BarChartModel d2 = new BarChartModel();
                d2.setTitle("Enseignants");
                d2.setLegendPosition("e");
                d2.setShadow(true);
                d2.setZoom(true);
                ChartSeries serie = new ChartSeries();
                serie.setLabel("Enseignants");
                serie.setData((Map) circle2);
                d2.addSeries(serie);
                d2.getAxis(AxisType.X).setTickAngle(-50);
                getModel().getStatCharts().put("SupervisorsBar", d2);

                //                getModel().getStatTables().put("InternshipPeriod", circle2_table);
            }

            //Teachers


        }
    }
//
//    protected void addStatTable(String name, Map<String, Number> circle1, ListValueMap<String, AcademicInternship> circle1_internships) {
//        List<ValueCount> circle1_values = VrUtils.reverseSortCountValueCountList(circle1);
//        for (ValueCount circle1_value : circle1_values) {
//            circle1_value.setUserValue(circle1_internships.get((String) circle1_value.getValue()));
//        }
//        for (int i = circle1_values.size() - 1; i >= 0; i--) {
//            if (circle1_values.get(i).getCount() == 0) {
//                circle1_values.remove(i);
//            }
//        }
//        getModel().getStatTables().put(name, circle1_values);
//    }


    public class Model {

        private boolean boardManager;
        private String boardId;
        private String periodId;
        private String filterInternshipTypeId;
        private AcademicInternshipBoard internshipBoard;
        private List<AcademicInternshipCount> academicInternshipCounts = new ArrayList<AcademicInternshipCount>();
        private List<SelectItem> periods = new ArrayList<SelectItem>();
        private List<SelectItem> boards = new ArrayList<SelectItem>();
        private List<SelectItem> internshipItems = new ArrayList<SelectItem>();
        private List<SelectItem> internshipTypes = new ArrayList<SelectItem>();
        private List<AcademicInternship> internships = new ArrayList<AcademicInternship>();
        private List<AcademicInternshipInfo> internshipInfos = new ArrayList<AcademicInternshipInfo>();
        private List<SelectItem> teachers = new ArrayList<SelectItem>();
        private Map<String, List<NamedValueCount>> statTables = new HashMap<>();
        private Map<String, ChartModel> statCharts = new HashMap<>();
        private boolean filterInternshipTypeVisible = true;

        public String getPeriodId() {
            return periodId;
        }

        public void setPeriodId(String periodId) {
            this.periodId = periodId;
        }

        public Map<String, List<NamedValueCount>> getStatTables() {
            return statTables;
        }

        public void setStatTables(Map<String, List<NamedValueCount>> statTables) {
            this.statTables = statTables;
        }


        public boolean isBoardManager() {
            return boardManager;
        }

        public void setBoardManager(boolean boardManager) {
            this.boardManager = boardManager;
        }


        public List<SelectItem> getInternshipItems() {
            return internshipItems;
        }

        public void setInternshipItems(List<SelectItem> internships) {
            this.internshipItems = internships;
        }

        public List<SelectItem> getInternshipTypes() {
            return internshipTypes;
        }

        public void setInternshipTypes(List<SelectItem> internshipTypes) {
            this.internshipTypes = internshipTypes;
        }

        public String getFilterInternshipTypeId() {
            return filterInternshipTypeId;
        }

        public void setFilterInternshipTypeId(String filterInternshipTypeId) {
            this.filterInternshipTypeId = filterInternshipTypeId;
        }

        public List<AcademicInternship> getInternships() {
            return internships;
        }

        public void setInternships(List<AcademicInternship> internships) {
            this.internships = internships;
        }


        public List<SelectItem> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<SelectItem> teachers) {
            this.teachers = teachers;
        }


        public String getBoardId() {
            return boardId;
        }

        public void setBoardId(String boardId) {
            this.boardId = boardId;
        }

        public List<SelectItem> getBoards() {
            return boards;
        }

        public void setBoards(List<SelectItem> boards) {
            this.boards = boards;
        }

        public AcademicInternshipBoard getInternshipBoard() {
            return internshipBoard;
        }

        public void setInternshipBoard(AcademicInternshipBoard internshipBoard) {
            this.internshipBoard = internshipBoard;
        }

        public List<SelectItem> getPeriods() {
            return periods;
        }


        public List<AcademicInternshipInfo> getInternshipInfos() {
            return internshipInfos;
        }

        public void setInternshipInfos(List<AcademicInternshipInfo> internshipInfos) {
            this.internshipInfos = internshipInfos;
        }

        public List<AcademicInternshipCount> getAcademicInternshipCounts() {
            return academicInternshipCounts;
        }

        public void setAcademicInternshipCounts(List<AcademicInternshipCount> academicInternshipCounts) {
            this.academicInternshipCounts = academicInternshipCounts;
        }
        public boolean isFilterInternshipTypeVisible() {
            return filterInternshipTypeVisible;
        }

        public void setFilterInternshipTypeVisible(boolean filterInternshipTypeVisible) {
            this.filterInternshipTypeVisible = filterInternshipTypeVisible;
        }

        public Map<String, ChartModel> getStatCharts() {
            return statCharts;
        }

        public void setStatCharts(Map<String, ChartModel> statCharts) {
            this.statCharts = statCharts;
        }
    }


}
