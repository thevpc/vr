/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import java.util.List;

/**
 *
 * @author vpc
 */
public interface TreeDefinition<T> {

    public T getRoot();

    public List<T> getChildren(T t);
    
}
