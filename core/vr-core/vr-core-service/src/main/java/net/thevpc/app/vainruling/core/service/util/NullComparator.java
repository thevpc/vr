/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

import java.util.Comparator;

/**
 *
 * @author vpc
 */
public abstract class NullComparator<T> implements Comparator<T>{

    @Override
    public final int compare(T o1, T o2) {
        if(o1==null && o2==null){
            return 0;
        }else if(o1==null){
            return -1;
        }else if(o2==null){
            return 1;
        }else{
            return compareNonNull(o1, o2);
        }
    }
    
    public abstract int compareNonNull(T o1, T o2) ;
    
}
