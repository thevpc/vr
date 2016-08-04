/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.obj;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class ObjSearch {
    private String name;

    public ObjSearch(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String createPreProcessingExpression(String entityName) {
        return null;
    }

    public List filterList(List list, String entityName) {
        return list;
    }

}
