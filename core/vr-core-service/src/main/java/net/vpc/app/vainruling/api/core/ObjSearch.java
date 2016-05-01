/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.core;

import java.util.List;

/**
 *
 * @author vpc
 */
public abstract class ObjSearch {
    private String name;
    public ObjSearch(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public String createPreProcessingExpression(String entityName){
        return null;
    }
    
    public List filterList(List list,String entityName){
        return list;
    }
        
}
