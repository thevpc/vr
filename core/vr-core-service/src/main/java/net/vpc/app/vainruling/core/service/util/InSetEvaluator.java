/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import com.fathzer.soft.javaluator.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * @author taha.bensalah@gmail.com
 */
public interface InSetEvaluator {

    <T> T evaluateExpression(String expression) ;

}
