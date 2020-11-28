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
public class CourseAssignmentsMapping {
    
    @ColumnMapping(value = {"department", "departement", "dpt"})
    public int DEPARTMENT_COLUMN = 0;
    @ColumnMapping(value = {"owner department", "departement responsable", "owner dpt", "dpt resp"})
    public int OWNER_DEPARTMENT_COLUMN = 1;
    @ColumnMapping(value = {"program", "filiere"})
    public int PROGRAM_COLUMN = 2;
    @ColumnMapping(value = {"class", "classe"})
    public int STUDENT_CLASS_COLUMN = 3;
    @ColumnMapping(value = {"subclass", "sous classe"})
    public int STUDENT_SUBLASS_COLUMN = 4;
    @ColumnMapping(value = {"semester", "semestre"})
    public int SEMESTER_COLUMN = 5;
    @ColumnMapping(value = {"course", "cours","module"})
    public int COURSE_NAME_COLUMN = 7;
    @ColumnMapping(value = {"course type", "type cours"})
    public int COURSE_TYPE_COLUMN = 8;
    @ColumnMapping(value = {"C"})
    public int LOAD_C_COLUMN = 9;
    @ColumnMapping(value = {"td"})
    public int LOAD_TD_COLUMN = 10;
    @ColumnMapping(value = {"tp"})
    public int LOAD_TP_COLUMN = 11;
    @ColumnMapping(value = {"pm","pr"})
    public int LOAD_PM_COLUMN = 12;
    @ColumnMapping(value = {"groups", "groupes"})
    public int NBR_GROUPS_COLUMN = 13;
    @ColumnMapping(value = {"teacher", "enseignant","affectation"})
    public int TEACHER_NAME_COLUMN = 14;
    @ColumnMapping(value = {"intents", "intent", "voeu", "voeux"})
    public int TEACHER_INTENTS_COLUMN = 15;
    @ColumnMapping(value = {"proposition"})
    public int TEACHER_PROPOSAL_COLUMN = 15;
    @ColumnMapping(value = {"annee","periode"})
    public int PERIOD_COLUMN = 15;
    @ColumnMapping(value = {"ignore", "ignorer"})
    public int IGNORE_COLUMN = 17;
    
}
