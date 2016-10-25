/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.internship;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipGroup;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipSessionType;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.planning.*;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.upa.UPA;
import net.vpc.upa.bulk.*;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;

/**
 * internships for teachers
 *
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Planification de Stages",
        menu = "/Education/Internship",
        securityKey = "Custom.Education.InternshipsPlanning",
        url = "modules/academic/internship/internships-planning"
)
public class InternshipsPlanningCtrl {
    private static final String CHAIR_LETTER = "P";
    private static final String EXAMINER_LETTER = "R";
    private static final String SUPERVISOR_LETTER = "E";

    private Model model = new Model();
    @Autowired
    private CorePlugin core;
    @Autowired
    private AcademicPlugin academicPlugin;

    private static String formatDouble(double d) {
        if (d == (int) d) {
            return String.valueOf((int) d);
        }
        return String.valueOf(d);
    }

    @OnPageLoad
    public void onPageLoad() {
        UserSession userSession = core.getUserSession();
        getModel().setManager(userSession.isDepartmentManager());
        getModel().setGenerationDays(6);
        getModel().setGenerationMinutesPerSession(60);
        getModel().setGenerationSessionsPerDay(6);
        getModel().setGenerationStartDate("2016-06-27");
        getModel().setGenerationStartTime("08:30");
        getModel().setGenerationRoomPerDay(8);
        getModel().setSelectedGroup(null);
        reloadInternshipGroups();
        reloadInternshipSessionTypes();
        reloadActivityTable();
    }

    public void onGroupChanged() {
        reloadActivityTable();
    }

    public void onSessionTypeChanged() {
        updateTable();
    }

    private StreamedContent createStreamedContent(ByteArrayOutputStream byteArrayOutputStream,String contentType,String extension){
        InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        AcademicInternshipGroup g = UPA.getPersistenceUnit().findById(AcademicInternshipGroup.class, Integer.parseInt(getModel().getSelectedGroup()));
        String groupName = g.getName();
        groupName = groupName.replace(" ", "-");
        String selectedSessionType = getModel().getSelectedSessionType();
        if (!StringUtils.isEmpty(selectedSessionType)) {
            selectedSessionType = "-" + selectedSessionType.replace(" ", "-");
        }
        return new DefaultStreamedContent(stream, contentType, "planning-" + groupName + selectedSessionType + "."+extension);
    }

    public StreamedContent downloadCsv() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            TextCSVFormatter formatter = UPA.getPersistenceUnit().getImportExportManager().createTextCSVFormatter(byteArrayOutputStream);
            formatter.getColumns().add((TextCSVColumn) new TextCSVColumn().updateName("Code"));
            formatter.getColumns().add((TextCSVColumn) new TextCSVColumn().updateName("Intitule"));
            formatter.getColumns().add((TextCSVColumn) new TextCSVColumn().updateName("Etudiant"));
            formatter.getColumns().add((TextCSVColumn) new TextCSVColumn().updateName("Encadrant"));
            formatter.getColumns().add((TextCSVColumn) new TextCSVColumn().updateName("President"));
            formatter.getColumns().add((TextCSVColumn) new TextCSVColumn().updateName("Rapporteur"));
            formatter.setWriteHeader(true);
            DataWriter writer = formatter.createWriter();

            PlanningActivityTable table = getModel().getTable();
            List<Row> rows = getModel().getVisibleRows();
            for (Row row : rows) {
                if (row.isVisible()) {
                    writer.writeRow(new Object[]{
                            row.activity.getInternship().getCode(),
                            row.activity.getInternship().getName(),
                            row.activity.getInternship().getStudent(),
                            StringUtils.listToString(row.activity.getInternship().getSupervisors(), "/"),
                            row.activity.getChair(),
                            row.activity.getExaminer()
                    });
                }
            }
            writer.close();
            return createStreamedContent(byteArrayOutputStream,"text/csv","csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public StreamedContent downloadXls() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            SheetFormatter formatter = UPA.getPersistenceUnit().getImportExportManager().createSheetFormatter(byteArrayOutputStream);
            formatter.getColumns().add((SheetColumn) new SheetColumn().updateName("Code"));
            formatter.getColumns().add((SheetColumn) new SheetColumn().updateName("Intitule"));
            formatter.getColumns().add((SheetColumn) new SheetColumn().updateName("Etudiant"));
            formatter.getColumns().add((SheetColumn) new SheetColumn().updateName("Encadrant"));
            formatter.getColumns().add((SheetColumn) new SheetColumn().updateName("President"));
            formatter.getColumns().add((SheetColumn) new SheetColumn().updateName("Rapporteur"));
            formatter.setWriteHeader(true);
            DataWriter writer = formatter.createWriter();

            PlanningActivityTable table = getModel().getTable();
            List<Row> rows = getModel().getVisibleRows();
            for (Row row : rows) {
                if (row.isVisible()) {
                    writer.writeRow(new Object[]{
                            row.activity.getInternship().getCode(),
                            row.activity.getInternship().getName(),
                            row.activity.getInternship().getStudent(),
                            StringUtils.listToString(row.activity.getInternship().getSupervisors(), "/"),
                            row.activity.getChair(),
                            row.activity.getExaminer()
                    });
                }
            }
            writer.close();
            return createStreamedContent(byteArrayOutputStream,"text/csv","csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public StreamedContent downloadFetXml() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            PlanningActivityTable table = getModel().getTable();
            PlanningActivityTable table2 = table.copy();
            ArrayList<PlanningActivity> activities = new ArrayList<>();
            List<Row> rows = getModel().getVisibleRows();
            for (Row row : rows) {
                if (row.isVisible()) {
                    activities.add(row.activity.copy());
                }
            }
            table2.setActivities(activities);
            VrApp.getBean(PlanningService.class).storeFetXml(table2, byteArrayOutputStream);
            return createStreamedContent(byteArrayOutputStream, "text/xml","fet");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void resetErrors() {
        for (Column column : getModel().getColumns()) {
            column.resetError();
        }
        for (Row row : getModel().getRows()) {
            row.resetError();
        }
        for (Row row : getModel().getRows()) {
            for (Cell cell : row.cells) {
                cell.resetError();
            }
        }
    }

    public void updateTable() {
        List<Column> columns = getModel().getColumns();
        Map<String, PlanningTeacherStats> stringPlanningTeacherStatsMap = VrApp.getBean(PlanningService.class).evalTeacherStats(getModel().getTable(), false);
        resetErrors();
        int tableWidth = 0;
        for (Column column : columns) {
            if (column.isVisible() || !getModel().isShowVisibleColumnsOnly()) {
                tableWidth += column.getWidth();
            }
        }
        if (tableWidth <= 800) {
            tableWidth = 800;
        }
        getModel().setBestWidth(tableWidth + 10);
        boolean manager = getModel().isManager();
        for (Row row : getModel().getRows()) {
            row.enableChairFixing = manager && !StringUtils.isEmpty(row.activity.getChair());
            row.enableExaminerFixing = manager && !StringUtils.isEmpty(row.activity.getExaminer());
        }
        for (int i = getModel().getFirstJuryColumn(); i < columns.size(); i++) {
            Column column = columns.get(i);
            column.stats = stringPlanningTeacherStatsMap.get(column.name);
        }
        for (int i = getModel().getFirstJuryColumn(); i < columns.size(); i++) {
            for (Row row : getModel().getRows()) {
                StringBuilder sb = new StringBuilder();
                Cell cell = row.cells.get(i);
                PlanningActivity a = row.activity;
                if (Objects.equals(a.getChair(), cell.column.name)) {
                    sb.append(CHAIR_LETTER);
                }
                if (Objects.equals(a.getExaminer(), cell.column.name)) {
                    sb.append(EXAMINER_LETTER);
                }
                for (String supervisor : a.getInternship().getSupervisors()) {
                    if (Objects.equals(supervisor, cell.column.name)) {
                        sb.append(SUPERVISOR_LETTER);
                    }
                }
                cell.setChair(sb.indexOf(CHAIR_LETTER) >= 0);
                cell.setExaminer(sb.indexOf(EXAMINER_LETTER) >= 0);
                cell.setSupervisor(sb.indexOf(SUPERVISOR_LETTER) >= 0);
                cell.setValue(sb.toString());
                if (sb.length() > 1) {
                    cell.setError("Plusieurs Roles pour le meme enseignant");
                    cell.setStyle("background-color:red;");
                } else if (sb.toString().equals(CHAIR_LETTER)) {
                    cell.setStyle("background-color:lightsalmon;");
                } else if (sb.toString().equals(EXAMINER_LETTER)) {
                    cell.setStyle("background-color:lightgreen;");
                } else if (sb.toString().equals(SUPERVISOR_LETTER)) {
                    cell.setStyle("background-color:lightblue;");
                } else {
                    if (cell.column.highlighted || cell.row.highlighted) {
                        cell.setStyle("background-color:gold;");
                    } else if (!cell.column.isVisible() || !cell.row.isVisible()) {
                        cell.setStyle("background-color:lightgray;");
                    } else {
                        cell.setStyle("");
                    }
                }
                cell.setEnabledChairOrExaminerSwitcher(!cell.supervisor);
            }
            Column column = columns.get(i);
            column.stats = stringPlanningTeacherStatsMap.get(column.name);
            column.setCompleted(Math.abs(column.stats.chairBalance) < 1 && Math.abs(column.stats.examinerBalance) < 1);
            column.setTitleSuffix(formatDouble(column.stats.supervisor) + "," + formatDouble(column.stats.chair) + "," + formatDouble(column.stats.examiner));
            column.setTitleTooltip(
                    "encadrant=" + formatDouble(column.stats.supervisor)
                            + "," + "president=" + formatDouble(column.stats.chair)
                            + "," + "rapporteur=" + formatDouble(column.stats.examiner)
                            + "," + "activites=" + formatDouble(column.stats.activities)
                            + "," + "jours=" + formatDouble(column.stats.days)
            );
        }

        for (int i = 0; i < getModel().getFirstJuryColumn(); i++) {
            for (Row row : getModel().getRows()) {
                Cell cell = row.cells.get(i);
                if (cell.row.highlighted) {
                    cell.setStyle("background-color:gold;");
                } else {
                    cell.setStyle("");
                }
            }
        }

        for (Row row : getModel().getRows()) {
            row.getCells().get(0).setStyle(row.isCompleted() ? "background-color:lightgreen;" : "");
            row.getCells().get(4).setValue(row.getActivity().getChair());
            row.getCells().get(5).setValue(row.getActivity().getExaminer());
        }

        List<Row> visibleRows = new ArrayList<>();
        String s = getModel().getSelectedSessionType();
        if (!getModel().isShowVisibleRowsOnly()) {
            for (Row row : getModel().getRows()) {
                if (
                        (StringUtils.isEmpty(s) || s.equals(row.activity.getInternship().getSession()))
                                && (row.isVisible() || !getModel().isShowVisibleRowsOnly())
                        ) {
                    visibleRows.add(row);
                }
            }
        } else {
            for (Row row : getModel().getRows()) {
                if (
                        (StringUtils.isEmpty(s) || s.equals(row.activity.getInternship().getSession()))
                                &&
                                row.isVisible()
                        ) {
                    visibleRows.add(row);
                }
            }
        }
        getModel().setVisibleRows(visibleRows);
        List<Column> visibleColumns = new ArrayList<>();
        if (!getModel().isShowVisibleColumnsOnly()) {
            for (Column column : columns) {
                if (column.isVisible() || !getModel().isShowVisibleColumnsOnly()) {
                    visibleColumns.add(column);
                }
            }
        } else {
            for (Column column : columns) {
                if (column.isVisible()) {
                    visibleColumns.add(column);
                }
            }
        }
        getModel().setVisibleColumns(visibleColumns);
    }

    public void onGenerateJury() {
        try {
            PlanningResult r = VrApp.getBean(PlanningService.class).generateActivitiesJury(getModel().getTable());
            loadActivityTable(r.getResut());
            FacesUtils.addInfoMessage("Generation reussie");
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(InternshipsPlanningCtrl.class.getName()).log(Level.SEVERE, "Error saving Jury", ex);
            FacesUtils.addErrorMessage("Impossibe de generer le jury");
        }
    }

    public void onSaveJury() {
        try {
            AcademicPlugin academic = VrApp.getBean(AcademicPlugin.class);
            AcademicPlugin internships = VrApp.getBean(AcademicPlugin.class);
            List<AcademicTeacher> teachers = academic.findTeachers();
            Map<String, AcademicTeacher> teachersByName = new HashMap<>();
            for (AcademicTeacher teacher : teachers) {
                teachersByName.put(teacher.getContact().getFullName(), teacher);
            }
            for (PlanningActivity activity : getModel().getTable().getActivities()) {
                int id = activity.getInternship().getId();
                AcademicInternship internship = getModel().getInternshipsListMap().get(id);
                if (!StringUtils.isEmpty(activity.getChair()) && activity.isFixedChair()) {
                    internship.setChairExaminer(teachersByName.get(activity.getChair()));
                } else {
                    internship.setChairExaminer(null);
                }
                if (!StringUtils.isEmpty(activity.getExaminer()) && activity.isFixedExaminer()) {
                    internship.setFirstExaminer(teachersByName.get(activity.getExaminer()));
                } else {
                    internship.setFirstExaminer(null);
                }
                UPA.getPersistenceUnit()
                        .createUpdateQuery(internship)
                        .setIgnoreUnspecified(false)
                        .update("firstExaminer", "chairExaminer").execute();
            }
            FacesUtils.addInfoMessage("Enregistrement reussi");
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(InternshipsPlanningCtrl.class.getName()).log(Level.SEVERE, "Error saving Jury", ex);
            FacesUtils.addErrorMessage("Impossibe d'enregistrer le jury");
        }
    }

    public void onResetJury() {
        try {
            for (PlanningActivity activity : getModel().getTable().getActivities()) {
                int id = activity.getInternship().getId();
                if (!activity.isFixedChair()) {
                    activity.setChair(null);
                }
                if (!activity.isFixedExaminer()) {
                    activity.setExaminer(null);
                }
            }
            loadActivityTable(getModel().getTable());
            FacesUtils.addInfoMessage("Rechargement reussi");
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(InternshipsPlanningCtrl.class.getName()).log(Level.SEVERE, "Error resetting Jury", ex);
            FacesUtils.addErrorMessage("Impossibe de recharger le jury");
        }
    }

    public void onSetAllFixedChair() {
        for (PlanningActivity activity : getModel().getTable().getActivities()) {
            activity.setFixedChair(true);
        }
    }

    public void onSetAllNonFixedChair() {
        for (PlanningActivity activity : getModel().getTable().getActivities()) {
            activity.setFixedChair(false);
        }
    }

    public void onSetAllFixedExaminer() {
        for (PlanningActivity activity : getModel().getTable().getActivities()) {
            activity.setFixedExaminer(true);
        }
    }

    public void onSetAllNonFixedExaminer() {
        for (PlanningActivity activity : getModel().getTable().getActivities()) {
            activity.setFixedExaminer(false);
        }
    }

    public void onReloadJury() {
        reloadActivityTable();
    }

    public void onChangeCellJury(Cell cell) {
        PlanningActivity activity = cell.row.activity;
        if (Objects.equals(activity.getChair(), cell.column.name)) {
            //was chair ==> examiner
            activity.setChair(null);
            activity.setExaminer(cell.column.name);
        } else if (Objects.equals(activity.getExaminer(), cell.column.name)) {
            activity.setExaminer(null);
        } else {
            activity.setChair(cell.column.name);
        }
        updateTable();
    }

    public void onChangeCellChair(Cell cell) {
        Chronometer c = new Chronometer("onChangeCellChair");
        PlanningActivity activity = cell.row.activity;
        if (Objects.equals(activity.getChair(), cell.column.name)) {
            //was chair ==> examiner
            activity.setChair(null);
        } else {
            activity.setChair(cell.column.name);
            if (activity.getExaminer() != null && activity.getExaminer().equals(activity.getChair())) {
                activity.setExaminer(null);
            }
        }
        updateTable();
        System.out.println(c.stop());
    }

    public void onChangeCellExaminer(Cell cell) {
        Chronometer c = new Chronometer("onChangeCellExaminer");
        PlanningActivity activity = cell.row.activity;
        if (Objects.equals(activity.getExaminer(), cell.column.name)) {
            //was chair ==> examiner
            activity.setExaminer(null);
        } else {
            activity.setExaminer(cell.column.name);
            if (activity.getChair() != null && activity.getChair().equals(activity.getExaminer())) {
                activity.setChair(null);
            }
        }
        updateTable();
        System.out.println(c.stop());
    }

    public void onChangeColumnVisibility(Column column) {
        column.setVisible(!column.isVisible());
        updateTable();
    }

    public void onChangeRowVisibility(Row row) {
        row.setVisible(!row.isVisible());
        updateTable();
    }

    public void onChangeColumnHighlight(Column column) {
        column.setHighlighted(!column.isHighlighted());
        updateTable();
    }

    public void onChangeRowHighlight(Row row) {
        row.setHighlighted(!row.isHighlighted());
        updateTable();
    }

    public void onSwitchColumnsVisibility() {
        getModel().setShowVisibleColumnsOnly(!getModel().isShowVisibleColumnsOnly());
        updateTable();
    }

    public void onSwitchRowsVisibility() {
        getModel().setShowVisibleRowsOnly(!getModel().isShowVisibleRowsOnly());
        updateTable();
    }


    public void reloadActivityTable() {
        List<AcademicInternship> internshipsList = new ArrayList<>();
        int groupId = Convert.toInteger(getModel().getSelectedGroup(), IntegerParserConfig.LENIENT_F);
        if (groupId > 0) {
            internshipsList = academicPlugin.findInternships(-1, groupId, -1, -1, -1, true);
        }
        setInternshipsList(internshipsList);
        reinitializeActivityTableFromModel();
    }

    public void setInternshipsList(List<AcademicInternship> internshipsList) {
        getModel().setInternshipsList(internshipsList);
        Map<Integer, AcademicInternship> map = new HashMap<>();
        for (AcademicInternship a : internshipsList) {
            map.put(a.getId(), a);
        }
        getModel().setInternshipsListMap(map);

    }

    public void reinitializeActivityTableFromModel() {
        //initialize table
        PlanningActivityTable t = new PlanningActivityTable();
        t.addGeneratedRooms("R", getModel().getGenerationRoomPerDay());
        try {
            t.addGeneratedTimes(getModel().getGenerationStartDate().trim() + " " + getModel().getGenerationStartTime().trim(), getModel().getGenerationDays(), getModel().getGenerationMinutesPerSession(), getModel().getGenerationSessionsPerDay());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (AcademicInternship academicInternship : getModel().getInternshipsList()) {
            if (academicInternship.getSupervisor() == null) {
                String internshipName = academicInternship.getCode() + " - " + academicInternship.getName() + " - " + academicInternship.getStudent().getContact().getFullName();
                System.err.println(internshipName + " ignored, no supervisor found");
            } else {
                t.addActivity(academicInternship);
            }
        }

        HashSet<String> all = new HashSet<>();
        for (PlanningActivity a : t.getActivities()) {
            all.addAll(a.getInternship().getSupervisors());
        }
        String extraTeachers = getModel().getExtraTeachers();
        if (!StringUtils.isEmpty(extraTeachers)) {
            for (String tn : extraTeachers.trim().split(" +")) {
                if (!StringUtils.isEmpty(tn)) {
                    all.add(tn);
                }
            }
        }
        t.setChairs(new ArrayList<String>(all));
        t.setExaminers(new ArrayList<String>(all));


        loadActivityTable(t);
    }

    public void loadActivityTable(PlanningActivityTable t) {
        getModel().setTable(t);
        List<Row> cells = getModel().getRows();
        List<Column> columns = getModel().getColumns();
        columns.clear();
        cells.clear();

        getModel().setTeachers(getModel().getTable().getTeachers());
        columns.add(new Column(columns.size(), ColumnType.STATIC, "code", 30));
        columns.add(new Column(columns.size(), ColumnType.STATIC, "intitulé", 200));
        columns.add(new Column(columns.size(), ColumnType.STATIC, "étudiant", 100));
        columns.add(new Column(columns.size(), ColumnType.STATIC, "encadrant", 100));
        columns.add(new Column(columns.size(), ColumnType.STATIC, "président", 100));
        columns.add(new Column(columns.size(), ColumnType.STATIC, "rapporteur", 100));
        getModel().setFirstJuryColumn(columns.size());
        for (String teacher : getModel().getTeachers()) {
            columns.add(new Column(columns.size(), ColumnType.TEACHER, teacher, 50));
        }
//        int row=0;
        List<PlanningActivity> activities = getModel().getTable().getActivities();
        for (int i = 0; i < activities.size(); i++) {
            PlanningActivity activity = activities.get(i);
            Row currRow = new Row();
            currRow.index = i;
            currRow.activity = activity;
            int col = 0;
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getInternship().getCode()));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getInternship().getName()));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getInternship().getStudent()));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getInternship().getSupervisors().get(0)));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getChair()));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getExaminer()));
            for (String teacher : getModel().getTeachers()) {
                currRow.cells.add(new Cell(columns.get(col++), currRow, ""));
            }
            cells.add(currRow);
        }
        updateTable();
    }

    public void onSetAllColumnsVisible() {
        List<Column> columns = getModel().getColumns();
        for (int i = getModel().getFirstJuryColumn(); i < columns.size(); i++) {
            Column column = columns.get(i);
            column.setVisible(true);
        }
        updateTable();
    }

    public void onSetAllColumnsNonVisible() {
        List<Column> columns = getModel().getColumns();
        for (int i = getModel().getFirstJuryColumn(); i < columns.size(); i++) {
            Column column = columns.get(i);
            column.setVisible(false);
        }
        updateTable();
    }

    public void onSetAllColumnsHighlighted() {
        List<Column> columns = getModel().getColumns();
        for (int i = getModel().getFirstJuryColumn(); i < columns.size(); i++) {
            Column column = columns.get(i);
            column.setHighlighted(true);
        }
        updateTable();
    }

    public void onSetAllColumnsNonHighlighted() {
        List<Column> columns = getModel().getColumns();
        for (int i = getModel().getFirstJuryColumn(); i < columns.size(); i++) {
            Column column = columns.get(i);
            column.setHighlighted(true);
        }
        updateTable();
    }

    public void onSetAllRowsVisible() {
        for (Row row : getModel().getRows()) {
            row.setVisible(true);
        }
        updateTable();
    }

    public void onSetAllRowsNonVisible() {
        for (Row row : getModel().getRows()) {
            row.setVisible(false);
        }
        updateTable();
    }

    public void onSetAllRowsHighlighted() {
        for (Row row : getModel().getRows()) {
            row.setHighlighted(true);
        }
        updateTable();
    }

    public void onSetAllRowsNonHighlighted() {
        for (Row row : getModel().getRows()) {
            row.setHighlighted(true);
        }
        updateTable();
    }

    public Model getModel() {
        return model;
    }


    public void onSaveOptions() {
        try {
            reloadActivityTable();
            FacesUtils.addInfoMessage("Rien à enregister");
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(InternshipsPlanningCtrl.class.getName()).log(Level.SEVERE, "Error saving options", ex);
            FacesUtils.addErrorMessage("Impossibe d'enregistrer les options");
        }
    }

    public void reloadInternshipGroups() {
        List<SelectItem> internshipGroupsItems = new ArrayList<>();
        AcademicTeacher tt = academicPlugin.getCurrentTeacher();
        if (tt != null) {
            List<AcademicInternshipGroup> internshipGroups = academicPlugin.findEnabledInternshipGroupsByDepartment(tt.getDepartment().getId());
            for (AcademicInternshipGroup t : internshipGroups) {
                String n = t.getName();
                internshipGroupsItems.add(new SelectItem(String.valueOf(t.getId()), n));
            }
        }
        getModel().setGroups(internshipGroupsItems);
    }

    public void reloadInternshipSessionTypes() {
        List<SelectItem> sessionTypeItems = new ArrayList<>();
        AcademicTeacher tt = academicPlugin.getCurrentTeacher();
        List<AcademicInternshipSessionType> sessionType = academicPlugin.findAcademicInternshipSessionType();
        for (AcademicInternshipSessionType t : sessionType) {
            String n = t.getName();
            //no id used because in Planning only session name is mentioned
            sessionTypeItems.add(new SelectItem(n, n));
        }
        getModel().setSessionTypes(sessionTypeItems);
    }


    public enum ColumnType {
        STATIC,
        TEACHER
    }

    public static class Model {
        private PlanningActivityTable table;
        private List<String> teachers;
        private List<SelectItem> groups = new ArrayList<SelectItem>();
        private String selectedGroup;
        private List<SelectItem> sessionTypes = new ArrayList<SelectItem>();
        private String selectedSessionType;
        private List<AcademicInternship> internshipsList;
        private Map<Integer, AcademicInternship> internshipsListMap;
        private List<Column> columns = new ArrayList<>();
        private List<Row> rows = new ArrayList<>();
        private List<Row> visibleRows = new ArrayList<>();
        private List<Column> visibleColumns = new ArrayList<>();
        private int firstJuryColumn;
        private int bestWidth;
        private String generationStartDate;
        private String generationStartTime;
        private int generationDays;
        private boolean manager;
        private int generationSessionsPerDay;
        private int generationMinutesPerSession;
        private int generationRoomPerDay;
        private String extraTeachers;
        private boolean showVisibleColumnsOnly = true;
        private boolean showVisibleRowsOnly = true;

        public List<SelectItem> getGroups() {
            return groups;
        }

        public void setGroups(List<SelectItem> groups) {
            this.groups = groups;
        }

        public boolean isManager() {
            return manager;
        }

        public void setManager(boolean manager) {
            this.manager = manager;
        }

        public String getExtraTeachers() {
            return extraTeachers;
        }

        public void setExtraTeachers(String extraTeachers) {
            this.extraTeachers = extraTeachers;
        }

        public List<Row> getVisibleRows() {
            return visibleRows;
        }

        public void setVisibleRows(List<Row> visibleRows) {
            this.visibleRows = visibleRows;
        }

        public List<Column> getVisibleColumns() {
            return visibleColumns;
        }

        public void setVisibleColumns(List<Column> visibleColumns) {
            this.visibleColumns = visibleColumns;
        }

        public String getGenerationStartDate() {
            return generationStartDate;
        }

        public void setGenerationStartDate(String generationStartDate) {
            this.generationStartDate = generationStartDate;
        }

        public int getGenerationRoomPerDay() {
            return generationRoomPerDay;
        }

        public void setGenerationRoomPerDay(int generationRoomPerDay) {
            this.generationRoomPerDay = generationRoomPerDay;
        }

        public String getGenerationStartTime() {
            return generationStartTime;
        }

        public void setGenerationStartTime(String generationStartTime) {
            this.generationStartTime = generationStartTime;
        }

        public int getGenerationDays() {
            return generationDays;
        }

        public void setGenerationDays(int generationDays) {
            this.generationDays = generationDays;
        }

        public int getGenerationSessionsPerDay() {
            return generationSessionsPerDay;
        }

        public void setGenerationSessionsPerDay(int generationSessionsPerDay) {
            this.generationSessionsPerDay = generationSessionsPerDay;
        }

        public int getGenerationMinutesPerSession() {
            return generationMinutesPerSession;
        }

        public void setGenerationMinutesPerSession(int generationMinutesPerSession) {
            this.generationMinutesPerSession = generationMinutesPerSession;
        }

        public List<String> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<String> teachers) {
            this.teachers = teachers;
        }

        public PlanningActivityTable getTable() {
            return table;
        }

        public void setTable(PlanningActivityTable table) {
            this.table = table;
        }

        public List<Row> getRows() {
            return rows;
        }

        public void setRows(List<Row> rows) {
            this.rows = rows;
        }

        public List<Column> getColumns() {
            return columns;
        }

        public void setColumns(List<Column> columns) {
            this.columns = columns;
        }

        public int getFirstJuryColumn() {
            return firstJuryColumn;
        }

        public void setFirstJuryColumn(int firstJuryColumn) {
            this.firstJuryColumn = firstJuryColumn;
        }

        public int getBestWidth() {
            return bestWidth;
        }

        public void setBestWidth(int bestWidth) {
            this.bestWidth = bestWidth;
        }

        public List<AcademicInternship> getInternshipsList() {
            return internshipsList;
        }

        public void setInternshipsList(List<AcademicInternship> internshipsList) {
            this.internshipsList = internshipsList;
        }

        public Map<Integer, AcademicInternship> getInternshipsListMap() {
            return internshipsListMap;
        }

        public void setInternshipsListMap(Map<Integer, AcademicInternship> internshipsListMap) {
            this.internshipsListMap = internshipsListMap;
        }

        public boolean isShowVisibleColumnsOnly() {
            return showVisibleColumnsOnly;
        }

        public void setShowVisibleColumnsOnly(boolean showVisibleColumnsOnly) {
            this.showVisibleColumnsOnly = showVisibleColumnsOnly;
        }

        public boolean isShowVisibleRowsOnly() {
            return showVisibleRowsOnly;
        }

        public void setShowVisibleRowsOnly(boolean showVisibleRowsOnly) {
            this.showVisibleRowsOnly = showVisibleRowsOnly;
        }

        public String getSelectedGroup() {
            return selectedGroup;
        }

        public void setSelectedGroup(String selectedGroup) {
            this.selectedGroup = selectedGroup;
        }

        public List<SelectItem> getSessionTypes() {
            return sessionTypes;
        }

        public void setSessionTypes(List<SelectItem> sessionTypes) {
            this.sessionTypes = sessionTypes;
        }

        public String getSelectedSessionType() {
            return selectedSessionType;
        }

        public void setSelectedSessionType(String selectedSessionType) {
            this.selectedSessionType = selectedSessionType;
        }
    }

    public static class Cell {
        Column column;
        Row row;
        Object value;
        boolean error;
        boolean chair;
        boolean supervisor;
        boolean examiner;
        boolean enabledChairOrExaminerSwitcher;
        String errorMessage;
        String style;

        public Cell(Column column, Row row, Object value) {
            this.column = column;
            this.row = row;
            this.value = value;
        }


        public boolean isEnabledChairOrExaminerSwitcher() {
            return enabledChairOrExaminerSwitcher;
        }

        public void setEnabledChairOrExaminerSwitcher(boolean enabledChairOrExaminerSwitcher) {
            this.enabledChairOrExaminerSwitcher = enabledChairOrExaminerSwitcher;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public void resetError() {
            error = false;
            errorMessage = null;
        }

        public void setError(String message) {
            error = true;
            if (errorMessage != null) {
                errorMessage += "\n" + message;
            } else {
                errorMessage = message;
            }
//            column.setError(message);
//            row.setError(message);
        }

        public boolean isChair() {
            return chair;
        }

        public void setChair(boolean chair) {
            this.chair = chair;
        }

        public boolean isSupervisor() {
            return supervisor;
        }

        public void setSupervisor(boolean supervisor) {
            this.supervisor = supervisor;
        }

        public boolean isExaminer() {
            return examiner;
        }

        public void setExaminer(boolean examiner) {
            this.examiner = examiner;
        }

    }

    public static class Row {
        int index;
        boolean visible = true;
        boolean completed;
        boolean highlighted;
        PlanningActivity activity;
        boolean error;
        String errorMessage;
        List<Cell> cells = new ArrayList<>();
        boolean enableChairFixing;
        boolean enableExaminerFixing;

        public void resetError() {
            error = false;
            errorMessage = null;
        }

        public int getIndex() {
            return index;
        }

        public PlanningActivity getActivity() {
            return activity;
        }

        public boolean isError() {
            return error;
        }

        public void setError(String message) {
            error = true;
            if (errorMessage != null) {
                errorMessage += "\n" + message;
            } else {
                errorMessage = message;
            }
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public List<Cell> getCells() {
            return cells;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public boolean isEnableChairFixing() {
            return enableChairFixing;
        }

        public void setEnableChairFixing(boolean enableChairFixing) {
            this.enableChairFixing = enableChairFixing;
        }

        public boolean isEnableExaminerFixing() {
            return enableExaminerFixing;
        }

        public void setEnableExaminerFixing(boolean enableExaminerFixing) {
            this.enableExaminerFixing = enableExaminerFixing;
        }

        public boolean isHighlighted() {
            return highlighted;
        }

        public void setHighlighted(boolean highlighted) {
            this.highlighted = highlighted;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    public static class Column {
        boolean completed;
        int index;
        int width;
        boolean visible = true;
        boolean highlighted = false;
        ColumnType type;
        String name;
        PlanningTeacherStats stats;
        boolean error;
        String errorMessage;
        String titleSuffix;
        String titleTooltip;

        public Column(int index, ColumnType type, String name, int width) {
            this.index = index;
            this.type = type;
            this.name = name;
            this.width = width;
        }

        public void resetError() {
            error = false;
            errorMessage = null;
        }

        public boolean isHighlighted() {
            return highlighted;
        }

        public void setHighlighted(boolean highlighted) {
            this.highlighted = highlighted;
        }

        public int getIndex() {
            return index;
        }

        public ColumnType getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public PlanningTeacherStats getStats() {
            return stats;
        }

        public boolean isError() {
            return error;
        }

        public void setError(String message) {
            error = true;
            if (errorMessage != null) {
                errorMessage += "\n" + message;
            } else {
                errorMessage = message;
            }
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getTitleSuffix() {
            return titleSuffix;
        }

        public void setTitleSuffix(String titleSuffix) {
            this.titleSuffix = titleSuffix;
        }

        public String getTitleTooltip() {
            return titleTooltip;
        }

        public void setTitleTooltip(String titleTooltip) {
            this.titleTooltip = titleTooltip;
        }
    }

}
