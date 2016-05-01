/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.util;

/**
 *
 * @author vpc
 */
public interface TreeVisitor<T> {

    public void visit(T t, TreeDefinition<T> tree);
    
}
