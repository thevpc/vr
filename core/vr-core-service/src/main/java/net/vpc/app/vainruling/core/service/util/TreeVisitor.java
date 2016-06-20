/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

/**
 * @author vpc
 */
public interface TreeVisitor<T> {

    public void visit(T t, TreeDefinition<T> tree);

}
