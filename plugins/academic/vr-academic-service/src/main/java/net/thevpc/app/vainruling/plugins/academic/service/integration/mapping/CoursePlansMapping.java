/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.integration.mapping;

import net.thevpc.app.vainruling.core.service.util.ColumnMapping;

/**
 *
 * @author vpc
 */
public class CoursePlansMapping {

    @ColumnMapping(value = {"department", "departement", "dpt"})
    public int DEPARTMENT_COLUMN;
    @ColumnMapping(value = {"program", "filiere"})
    public int PROGRAM_COLUMN;
    @ColumnMapping(value = {"class", "classe"})
    public int STUDENT_CLASS_COLUMN;
    @ColumnMapping(value = {"semester", "semestre"})
    public int SEMESTER_COLUMN;
    @ColumnMapping(value = {"ue"})
    public int COURSE_GROUP_COLUMN;
    @ColumnMapping(value = {"course", "cours", "module"})
    public int COURSE_NAME_COLUMN;
    @ColumnMapping(value = {"C"})
    public int LOAD_C_COLUMN;
    @ColumnMapping(value = {"td"})
    public int LOAD_TD_COLUMN;
    @ColumnMapping(value = {"tp"})
    public int LOAD_TP_COLUMN;
    @ColumnMapping(value = {"pm", "pr"})
    public int LOAD_PM_COLUMN;
    @ColumnMapping(value = {"groups c", "groupes c"})
    public int NBR_GROUPS_C_COLUMN;
    @ColumnMapping(value = {"groups td", "groupes td"})
    public int NBR_GROUPS_TD_COLUMN;
    @ColumnMapping(value = {"groups tp", "groupes tp"})
    public int NBR_GROUPS_TP_COLUMN;
    @ColumnMapping(value = {"groups pm", "groupes pm", "groups pr", "groupes pr"})
    public int NBR_GROUPS_PM_COLUMN;
    @ColumnMapping(value = {"annee", "periode"})
    public int PERIOD_COLUMN;
    @ColumnMapping(value = {"credits", "credit"})
    public int CREDITS_COLUMN;
    @ColumnMapping(value = {"ects"})
    public int ECTS_COLUMN;
    @ColumnMapping(value = {"discipline", "discipline"})
    public int DISCIPLINE_COLUMN;
    @ColumnMapping(value = {"responsible", "responsable"})
    public int RESPONSIBLE_NAME_COLUMN;

    @ColumnMapping(value = {"delegated class", "classe deleguee"})
    public int DELEGATED_STUDENT_CLASS_COLUMN;
    @ColumnMapping(value = {"delegated semester", "semestre delegue"})
    public int DELEGATED_SEMESTER_COLUMN;
    @ColumnMapping(value = {"delegated course", "delegated module", "cours delegue", "module delegue"})
    public int DELEGATED_COURSE_NAME_COLUMN;

    @ColumnMapping(value = {"ignore", "ignorer", "skip"})
    public int IGNORE_COLUMN;

}
