/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.internship.service.AcademicInternshipPlugin;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.planning.*;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.UPA;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.bean.ManagedBean;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;

/**
 * internships for teachers
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Planification",
        menu = "/Education/Internship",
        securityKey = "Custom.Education.AllInternships",
        url = "modules/academic/internship/internships-planning"
)
@ManagedBean
public class InternshipsPlanningCtrl {
    private Model model = new Model();

    @OnPageLoad
    public void onPageLoad() {
        getModel().setGenerationDays(5);
        getModel().setGenerationMinutesPerSession(60);
        getModel().setGenerationSessionsPerDay(5);
        getModel().setGenerationStartDate("2016-06-27");
        getModel().setGenerationStartTime("08:30");
        getModel().setGenerationRoomPerDay(8);
        reloadActivityTable();
    }

    public StreamedContent downloadFetXml(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            VrApp.getBean(PlanningService.class).storeFetXml(getModel().getTable(), byteArrayOutputStream);
            InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return new DefaultStreamedContent(stream, "text/xml", "planning.fet");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void resetErrors(){
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

    public void updateTable(){
        List<Column> columns = getModel().getColumns();
        Map<String, PlanningTeacherStats> stringPlanningTeacherStatsMap = VrApp.getBean(PlanningService.class).evalTeacherStats(getModel().getTable());
        resetErrors();
        int tableWidth=0;
        for (Column column : columns) {
            if(column.isVisible()) {
                tableWidth += column.getWidth();
            }
        }
        getModel().setBestWidth(tableWidth+10);

        for (int i = getModel().getFirstJuryColumn(); i < columns.size(); i++) {
            Column column = columns.get(i);
            column.stats= stringPlanningTeacherStatsMap.get(column.name);
        }
        for (int i = getModel().getFirstJuryColumn(); i < columns.size(); i++) {
            for (Row row : getModel().getRows()) {
                StringBuilder sb=new StringBuilder();
                Cell cell = row.cells.get(i);
                PlanningActivity a=row.activity;
                if(Objects.equals(a.getChair(),cell.column.name)){
                    sb.append("P");
                }
                if(Objects.equals(a.getExaminer(), cell.column.name)){
                    sb.append("R");
                }
                for (String supervisor : a.getInternship().getSupervisors()) {
                    if(Objects.equals(supervisor, cell.column.name)){
                        sb.append("E");
                    }
                }
                if(sb.length()>1){
                    cell.setError("Plusieurs Roles pour le meme enseignant");
                    cell.setStyle("background-color:red;");
                }else if(sb.toString().equals("P")){
                    cell.setStyle("background-color:lightsalmon;");
                }else if(sb.toString().equals("R")){
                    cell.setStyle("background-color:lightgreen;");
                }else if(sb.toString().equals("E")) {
                    cell.setStyle("background-color:lightblue;");
                }else{
                    cell.setStyle("");
                }
                cell.setValue(sb.toString());
                row.setCompleted(a.getAllTeachers().size()==(2+a.getInternship().getSupervisors().size()));
            }
            Column column = columns.get(i);
            column.stats= stringPlanningTeacherStatsMap.get(column.name);
            column.setCompleted(Math.abs(column.stats.chairBalance) < 1 && Math.abs(column.stats.examinerBalance) < 1);
            column.setTitleSuffix(formatDouble(column.stats.supervisor) + "," + formatDouble(column.stats.chair) + "," + formatDouble(column.stats.supervisor));
            column.setTitleTooltip(
                    "encadrant=" + formatDouble(column.stats.supervisor)
                            + "," + "president=" + formatDouble(column.stats.chair)
                            + "," + "rapporteur=" + formatDouble(column.stats.supervisor)
                            + "," + "activites=" + formatDouble(column.stats.activities)
                            + "," + "jours=" + formatDouble(column.stats.days)
            );
        }
        for (Row row : getModel().getRows()) {
            row.getCells().get(0).setStyle(row.isCompleted() ? "background-color:lightgreen;":"");
            row.getCells().get(4).setValue(row.getActivity().getChair());
            row.getCells().get(5).setValue(row.getActivity().getExaminer());
        }
    }
    private static String formatDouble(double d){
        if(d==(int)d){
            return String.valueOf((int)d);
        }
        return String.valueOf(d);
    }

    public void onGenerateJury(){
        try {
        PlanningResult r = VrApp.getBean(PlanningService.class).generateActivitiesJury(getModel().getTable());
        loadActivityTable(r.getResut());
            FacesUtils.addInfoMessage("Generation reussie");
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(InternshipsPlanningCtrl.class.getName()).log(Level.SEVERE, "Error saving Jury",ex);
            FacesUtils.addErrorMessage("Impossibe de generer le jury");
        }
    }

    public void onSaveJury(){
        try {
            AcademicPlugin academic = VrApp.getBean(AcademicPlugin.class);
            AcademicInternshipPlugin internships = VrApp.getBean(AcademicInternshipPlugin.class);
            List<AcademicTeacher> teachers = academic.findTeachers();
            Map<String,AcademicTeacher> teachersByName=new HashMap<>();
            for (AcademicTeacher teacher : teachers) {
                teachersByName.put(teacher.getContact().getFullName(),teacher);
            }
            for (PlanningActivity activity : getModel().getTable().getActivities()) {
                int id = activity.getInternship().getId();
                AcademicInternship internship = getModel().getInternshipsListMap().get(id);
                if (!StringUtils.isEmpty(activity.getChair())) {
                    internship.setChairExaminer(teachersByName.get(activity.getChair()));
                } else {
                    internship.setChairExaminer(null);
                }
                if (!StringUtils.isEmpty(activity.getExaminer())) {
                    internship.setFirstExaminer(teachersByName.get(activity.getExaminer()));
                } else {
                    internship.setFirstExaminer(null);
                }
                UPA.getPersistenceUnit().updatePartial(internship, "firstExaminer","chairExaminer");
            }
            FacesUtils.addInfoMessage("Enregistrement reussi");
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(InternshipsPlanningCtrl.class.getName()).log(Level.SEVERE, "Error saving Jury",ex);
            FacesUtils.addErrorMessage("Impossibe d'enregistrer le jury");
        }
    }

    public void onResetJury(){
        try {
            for (PlanningActivity activity : getModel().getTable().getActivities()) {
                int id = activity.getInternship().getId();
                if(!activity.isFixedChair()) {
                    activity.setChair(null);
                }
                if(!activity.isFixedExaminer()) {
                    activity.setExaminer(null);
                }
            }
            loadActivityTable(getModel().getTable());
            FacesUtils.addInfoMessage("Rechargement reussi");
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(InternshipsPlanningCtrl.class.getName()).log(Level.SEVERE, "Error resetting Jury",ex);
            FacesUtils.addErrorMessage("Impossibe de recharger le jury");
        }
    }

    public void onSetAllFixedChair(){
        for (PlanningActivity activity : getModel().getTable().getActivities()) {
            activity.setFixedChair(true);
        }
    }

    public void onSetAllNonFixedChair(){
        for (PlanningActivity activity : getModel().getTable().getActivities()) {
            activity.setFixedChair(false);
        }
    }
    public void onSetAllFixedExaminer(){
        for (PlanningActivity activity : getModel().getTable().getActivities()) {
            activity.setFixedExaminer(true);
        }
    }

    public void onSetAllNonFixedExaminer(){
        for (PlanningActivity activity : getModel().getTable().getActivities()) {
            activity.setFixedExaminer(false);
        }
    }

    public void onReloadJury(){
        reloadActivityTable();
    }

    public void onChangeCellJury(Cell cell){
        PlanningActivity activity = cell.row.activity;
        if(Objects.equals(activity.getChair(),cell.column.name)){
            //was chair ==> examiner
            activity.setChair(null);
            activity.setExaminer(cell.column.name);
        }else if(Objects.equals(activity.getExaminer(),cell.column.name)){
            activity.setExaminer(null);
        }else {
            activity.setChair(cell.column.name);
        }
        updateTable();
    }

    public void reloadActivityTable(){
        List<AcademicInternship> internshipsList = VrApp.getBean(AcademicInternshipPlugin.class).findInternships(-1, -1, -1, -1, true);
        setInternshipsList(internshipsList);
       reinitializeActivityTableFromModel();
    }

    public void setInternshipsList(List<AcademicInternship> internshipsList){
        getModel().setInternshipsList(internshipsList);
        Map<Integer,AcademicInternship> map=new HashMap<>();
        for (AcademicInternship a : internshipsList) {
            map.put(a.getId(),a);
        }
        getModel().setInternshipsListMap(map);

    }
    public void reinitializeActivityTableFromModel(){
        //initialize table
        PlanningActivityTable t=new PlanningActivityTable();
        t.addGeneratedRooms("R", getModel().getGenerationRoomPerDay());
        try {
            t.addGeneratedTimes(getModel().getGenerationStartDate().trim()+" "+getModel().getGenerationStartTime().trim(), getModel().getGenerationDays(), getModel().getGenerationMinutesPerSession(), getModel().getGenerationSessionsPerDay());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (AcademicInternship academicInternship : getModel().getInternshipsList()) {
            if(academicInternship.getSupervisor()==null){
                String internshipName = academicInternship.getCode() + " - " + academicInternship.getName() + " - " + academicInternship.getStudent().getContact().getFullName();
                System.err.println(internshipName+" ignored, no supervisor found");
            }else {
                t.addActivity(academicInternship);
            }
        }
        t.setDefaultChairsAndExaminers();

        loadActivityTable(t);
    }

    public void loadActivityTable(PlanningActivityTable t){
        getModel().setTable(t);
        List<Row> cells= getModel().getRows();
        List<Column> columns = getModel().getColumns();
        columns.clear();
        cells.clear();

        getModel().setTeachers(getModel().getTable().getTeachers());
        columns.add(new Column(columns.size(),ColumnType.STATIC, "code",30));
        columns.add(new Column(columns.size(),ColumnType.STATIC, "name",200));
        columns.add(new Column(columns.size(),ColumnType.STATIC, "etudiant",100));
        columns.add(new Column(columns.size(),ColumnType.STATIC, "encadrant",100));
        columns.add(new Column(columns.size(),ColumnType.STATIC, "président",100));
        columns.add(new Column(columns.size(),ColumnType.STATIC, "rapporteur",100));
        getModel().setFirstJuryColumn(columns.size());
        for (String teacher : getModel().getTeachers()) {
            columns.add(new Column(columns.size(),ColumnType.TEACHER, teacher,50));
        }
//        int row=0;
        List<PlanningActivity> activities = getModel().getTable().getActivities();
        for (int i = 0; i < activities.size(); i++) {
            PlanningActivity activity = activities.get(i);
            Row currRow = new Row();
            currRow.index=i;
            currRow.activity=activity;
            int col = 0;
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getInternship().getCode()));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getInternship().getName()));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getInternship().getStudent()));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getInternship().getSupervisors().get(0)));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getChair()));
            currRow.cells.add(new Cell(columns.get(col++), currRow, activity.getExaminer()));
            for (String teacher : getModel().getTeachers()) {
                currRow.cells.add(new Cell(columns.get(col++),currRow, ""));
            }
            cells.add(currRow);
        }
        updateTable();
    }

    public Model getModel() {
        return model;
    }


    public void onSaveOptions(){
        try {

            FacesUtils.addInfoMessage("Rien à enregister");
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(InternshipsPlanningCtrl.class.getName()).log(Level.SEVERE, "Error saving options",ex);
            FacesUtils.addErrorMessage("Impossibe d'enregistrer les options");
        }
    }

    public static class Model {
        private PlanningActivityTable table;
        private List<String> teachers;
        private List<AcademicInternship> internshipsList;
        private Map<Integer,AcademicInternship> internshipsListMap;
        private List<Column> columns=new ArrayList<>();
        private List<Row> rows=new ArrayList<>();
        private int firstJuryColumn;
        private int bestWidth;
        private String generationStartDate;
        private String generationStartTime;
        private int generationDays;
        private int generationSessionsPerDay;
        private int generationMinutesPerSession;
        private int generationRoomPerDay;

        public String getGenerationStartDate() {
            return generationStartDate;
        }

        public int getGenerationRoomPerDay() {
            return generationRoomPerDay;
        }

        public void setGenerationRoomPerDay(int generationRoomPerDay) {
            this.generationRoomPerDay = generationRoomPerDay;
        }

        public void setGenerationStartDate(String generationStartDate) {
            this.generationStartDate = generationStartDate;
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

        public void setFirstJuryColumn(int firstJuryColumn) {
            this.firstJuryColumn = firstJuryColumn;
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
        public List<Column> getVisibleColumns() {
            List<Column> allVisible=new ArrayList<>();
            for (Column column : columns) {
                if(column.isVisible()){
                    allVisible.add(column);
                }
            }
            return allVisible;
        }

        public int getFirstJuryColumn() {
            return firstJuryColumn;
        }

        public void setColumns(List<Column> columns) {
            this.columns = columns;
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
    }

    public static class Cell {
        Column column;
        Row row;
        Object value;
        boolean error;
        String errorMessage;
        String style;

        public Cell(Column column, Row row,Object value) {
            this.column = column;
            this.row = row;
            this.value=value;
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
        public void resetError(){
            error=false;
            errorMessage=null;
        }

        public void setError(String message){
            error=true;
            if(errorMessage!=null){
                errorMessage+="\n"+message;
            }else{
                errorMessage=message;
            }
//            column.setError(message);
//            row.setError(message);
        }

    }

    public enum ColumnType {
        STATIC,
        TEACHER
    }

    public static class Row {
        int index;
        boolean completed;
        PlanningActivity activity;
        boolean error;
        String errorMessage;
        List<Cell> cells=new ArrayList<>();
        public void resetError(){
            error=false;
            errorMessage=null;
        }
        public void setError(String message){
            error=true;
            if(errorMessage!=null){
                errorMessage+="\n"+message;
            }else{
                errorMessage=message;
            }
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
    }

    public static class Column {
        boolean completed;
        int index;
        int width;
        boolean visible=true;
        ColumnType type;
        String name;
        PlanningTeacherStats stats;
        boolean error;
        String errorMessage;
        String titleSuffix;
        String titleTooltip;

        public Column(int index,ColumnType type,String name,int width) {
            this.index = index;
            this.type = type;
            this.name = name;
            this.width = width;
        }

        public void setError(String message){
            error=true;
            if(errorMessage!=null){
                errorMessage+="\n"+message;
            }else{
                errorMessage=message;
            }
        }

        public void resetError(){
            error=false;
            errorMessage=null;
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
