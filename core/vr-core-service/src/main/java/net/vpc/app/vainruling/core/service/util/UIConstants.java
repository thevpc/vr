/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

/**
 * @author taha.bensalah@gmail.com
 */
public final class UIConstants {


    public static final String ENTITY_ID_HIERARCHY = "ui.id-hierarchy";

    public static final class Form {
        public static final String VISIBLE_ON_CREATE = "ui.form.visibleOnInsert";
        public static final String VISIBLE_ON_UPDATE = "ui.form.visibleOnUpdate";
        public static final String CONTROL = "ui.form.control";
        public static final String CONTROL_FILE_TYPE = "ui.form.control.file.type";
        public static final String CONTROL_FILE_PATH = "ui.form.control.file.path";
        /**
         * colspan and rowspan separated with , example : 3,4
         */
        public static final String SPAN = "ui.form.span";
        /**
         * List (separated with ,) of [before|after|label] before : prepend newline
         * after : append newline label : add newline after label
         */
        public static final String NEWLINE = "ui.form.newline";
        public static final String NO_LABEL = "ui.form.nolabel";

        /**
         * number or cells prefixing control that should be emptied
         */
        public static final String EMPTY_PREFIX = "ui.form.empty.prefix";
        public static final String SEPARATOR = "ui.form.separator";

        /**
         * number or cells suffixing control that should be emptied
         */
        public static final String EMPTY_SUFFIX = "ui.form.empty.suffix";
    }

    public static final class Control {

        public static final String WIKITEXTAREA = "wikitextarea";
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

    public static final class Grid {
        public static final String COLUMN_STYLE = "ui.grid.list-column-style";
        public static final String COLUMN_STYLE_CLASS = "ui.grid.list-column-style-class";
        public static final String ROW_STYLE = "ui.grid.list-row-style";

    }
}
