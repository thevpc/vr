/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

/**
 * @author taha.bensalah@gmail.com
 */
public interface TreeVisitor<T> {

    void visit(T t, TreeDefinition<T> tree);

}
