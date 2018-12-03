/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import java.util.Collection;

/**
 * @author taha.bensalah@gmail.com
 */
public interface InSetEvaluator {

    boolean evaluateExpression(String expression, Collection<String> set) ;

}
