/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public interface TreeDefinition<T> {

    T getRoot();

    List<T> getChildren(T t);

}
