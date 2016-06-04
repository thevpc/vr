/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

/**
 *
 * @author vpc
 */
public final class UIConstants {

    public static final String FIELD_FORM_VISIBLE_ON_CREATE = "ui.form.visibleOnInsert";
    public static final String FIELD_FORM_VISIBLE_ON_UPDATE = "ui.form.visibleOnUpdate";
    public static final String FIELD_FORM_CONTROL = "ui.form.control";
    /**
     * colspan and rowspan separated with , example : 3,4
     */
    public static final String FIELD_FORM_SPAN = "ui.form.span";
    /**
     * List (separated with ,) of [before|after|label] before : prepend newline
     * after : append newline label : add newline after label
     */
    public static final String FIELD_FORM_NEWLINE = "ui.form.newline";
    public static final String FIELD_FORM_NOLABEL = "ui.form.nolabel";

    /**
     * number or cells prefixing control that should be emptied
     */
    public static final String FIELD_FORM_EMPTY_PREFIX = "ui.form.empty.prefix";
    public static final String FIELD_FORM_SEPARATOR = "ui.form.separator";

    /**
     * number or cells suffixing control that should be emptied
     */
    public static final String FIELD_FORM_EMPTY_SUFFIX = "ui.form.empty.suffix";

    public static final String ENTITY_ID_HIERARCHY = "ui.id-hierarchy";

    public static final class ControlType {

        public static final String RICHTEXTAREA = "richtextarea";
        public static final String TEXTAREA = "textarea";
        public static final String TEXT = "text";
        public static final String INTEGER = "integer";
        public static final String LONG = "long";
        public static final String FLOAT = "float";
        public static final String DOUBLE = "double";
        public static final String SELECT = "select";
        public static final String ENTITY = "entity";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String DATETIME = "datetime";
        public static final String CHECKBOX = "checkbox";
        public static final String RATING = "rating";
        public static final String SEPARATOR = "separator";
        public static final String ENTITY_DETAIL = "entityDetail";
        public static final String PROFILE_EXPRESSION = "profileExpr";
        public static final String DISCIPLINE = "discipline";
        public static final String FILE = "file";
    }
    
    public static final class Grid{
        public static final String COLUMN_STYLE = "ui.list-column-style";
        public static final String ROW_STYLE = "ui.list-row-style";
        
    }
}
