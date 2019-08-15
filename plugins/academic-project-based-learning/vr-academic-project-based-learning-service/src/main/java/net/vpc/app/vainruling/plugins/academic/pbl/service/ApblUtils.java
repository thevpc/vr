/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.pbl.service;

import java.util.Comparator;
import java.util.Date;
import net.vpc.app.vainruling.plugins.academic.pbl.model.ApblSession;
import net.vpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
public class ApblUtils {

    public static Comparator<ApblSession> SESSION_COMPARATOR = new Comparator<ApblSession>() {
        @Override
        public int compare(ApblSession o1, ApblSession o2) {
            if(o1==null && o2==null){
                return 0;
            }
            if(o1==null){
                return -1;
            }
            if(o2==null){
                return 1;
            }
            Date d1 = o1.getStartDate();
            Date d2 = o2.getStartDate();
            if (d1 != null && d1 != null) {
                int x = d2.compareTo(d1);
                if (x != 0) {
                    return x;
                }
            }
            return StringUtils.nonNull(o2.getName()).compareTo(o1.getName());
        }

    };

}
