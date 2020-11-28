/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;

/**
 *
 * @author vpc
 */
public class AcademicUtils {
    public static Set<Integer> getStudentClassIds(AcademicStudent st){
        return getStudentClasses(st).stream().map(x->x.getId()).collect(Collectors.toSet());
    }
    
    public static List<AcademicClass> getStudentClasses(AcademicStudent st){
        List<AcademicClass> r=new ArrayList<>();
        if(st!=null){
            AcademicClass c = null;
            c=st.getLastClass1();
            if(c!=null){
                r.add(c);
            }
            c=st.getLastClass2();
            if(c!=null){
                r.add(c);
            }
            c=st.getLastClass3();
            if(c!=null){
                r.add(c);
            }
        }
        return r;
    }
}
