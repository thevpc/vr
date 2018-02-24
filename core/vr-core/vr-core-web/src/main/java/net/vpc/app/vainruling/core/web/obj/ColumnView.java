/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import java.io.Serializable;

// add rows, labels and editable controls
// set relationship between label and editable controls to support outputLabel with "for" attribute
// 1. row
//        DynaFormRow row = dynModel.createRegularRow();
//
//        DynaFormLabel label11 = row.addLabel("Author");
//        DynaFormControl control12 = row.addControl(new BookProperty("Author", true), "input");
//        label11.setForControl(control12);
//
//        DynaFormLabel label13 = row.addLabel("ISBN");
//        DynaFormControl control14 = row.addControl(new BookProperty("ISBN", true), "input");
//        label13.setForControl(control14);
//
//        // 2. row
//        row = dynModel.createRegularRow();
//
//        DynaFormLabel label21 = row.addLabel("Title");
//        DynaFormControl control22 = row.addControl(new BookProperty("Title", false), "input", 3, 1);
//        label21.setForControl(control22);
//
//        // 3. row
//        row = model.createRegularRow();
//
//        DynaFormLabel label31 = row.addLabel("Publisher");
//        DynaFormControl control32 = row.addControl(new BookProperty("Publisher", false), "input");
//        label31.setForControl(control32);
//
//        DynaFormLabel label33 = row.addLabel("Published on");
//        DynaFormControl control34 = row.addControl(new BookProperty("Published on", false), "calendar");
//        label33.setForControl(control34);
//
//        // 4. row
//        row = model.createRegularRow();
//
//        DynaFormLabel label41 = row.addLabel("Language");
//        DynaFormControl control42 = row.addControl(new BookProperty("Language", false), "select");
//        label41.setForControl(control42);
//
//        DynaFormLabel label43 = row.addLabel("Description", 1, 2);
//        DynaFormControl control44 = row.addControl(new BookProperty("Description", false), "textarea", 1, 2);
//        label43.setForControl(control44);
//
//        // 5. row
//        row = model.createRegularRow();
//
//        DynaFormLabel label51 = row.addLabel("Rating");
//        DynaFormControl control52 = row.addControl(new BookProperty("Rating", 3, true), "rating");
//        label51.setForControl(control52);
//        }
public class ColumnView implements Serializable {

    private String header;
    private String property;
    private String displayPropertyExpression;
    private String type;

    public ColumnView(String header, String property, String displayPropertyExpression, String type) {
        this.header = header;
        this.property = property;
        this.displayPropertyExpression = displayPropertyExpression;
        this.type = type;
    }

    public String getDisplayPropertyExpression() {
        return displayPropertyExpression;
    }


    public String getType() {
        return type;
    }

    public String getHeader() {
        return header;
    }

    public String getProperty() {
        return property;
    }

}
