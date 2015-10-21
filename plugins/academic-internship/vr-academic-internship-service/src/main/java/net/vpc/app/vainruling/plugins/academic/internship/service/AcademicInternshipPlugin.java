/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.AppPlugin;

/**
 *
 * @author vpc
 */
@AppPlugin(dependsOn = "academicPlugin")
public class AcademicInternshipPlugin {

    public static void main(String[] args) {
        String str = "P62:FBA+AD+KK	P14:OBK+IM+TBS	P4:ABA+MLA+WHA	P42:AB+NJ+SBA\n"
                + "P29:KK+NJ+MAH	P18:IM+TA+AD	P13:NS+SBJ+TBS	P26:IB+NK+WC\n"
                + "P27:AD+KK+AB	P1:WC+LH+ABA	P10:IM+SBA+BB	P61:IB+HM+MLA\n"
                + "	P45:HM+AD+MLA	P28:TBS+JBT+NK	\n"
                + "			\n"
                + "P68:NJ+FBA+MAH	P39:LH+AM+HM	P34:AB+IS+BB	\n"
                + "P21:NJ+WAL+FBA	P57:SBJ+IS+OBK	P9:BB+HM+NK	\n"
                + "P56:NJ+AB+AM	P48:HM+KK+IS+NK		\n"
                + "P31:KK+FBA+AB			";

        try {
            BufferedReader r = new BufferedReader(new StringReader(str));
            String line = null;
            int row = 0;
            while ((line = r.readLine()) != null) {
                HashSet<String> rowSet = new HashSet<>();
                row++;
                for (String s : line.split("[ :+\t]+")) {
                    if (s.length() > 0) {
                        if (rowSet.contains(s)) {
                            System.err.println("**** r=" + row + " Duplicate " + s + "    *********************************** : " + line);
                        } else {
                            rowSet.add(s);
                        }
                    }
                }
//                System.out.println(rowSet);
            }
        } catch (IOException ex) {
            Logger.getLogger(AcademicInternshipPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
